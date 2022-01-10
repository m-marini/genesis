/*
 *
 * Copyright (c) 2021 Marco Marini, marco.marini@mmarini.org
 *
 * Permission is hereby granted, free of charge, to any person
 * obtaining a copy of this software and associated documentation
 * files (the "Software"), to deal in the Software without
 * restriction, including without limitation the rights to use,
 * copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following
 * conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
 * OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 * HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 * OTHER DEALINGS IN THE SOFTWARE.
 *
 *    END OF TERMS AND CONDITIONS
 *
 */

package org.mmarini.genesis.model3;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import static java.util.Objects.requireNonNull;
import static org.mmarini.genesis.model3.Matrix.ones;

/**
 * Simulation engine computes the simulation status transitions
 * due to time interval
 * It contains the mass for each resource unit,
 * the environment topology,
 * the diffusion coefficient for each environment resource,
 * the reference resource for energy used to simulate the survival.
 */
public class SimEngine {
    /**
     * Returns a simulation engine
     *
     * @param masses    the masses (noResources x 1)
     * @param topology  the topology
     * @param diffusion the diffusion configuration (noResources x 1)
     * @param energyRef the energy reference index
     */
    public static SimEngine create(final Matrix masses, final Topology topology, final Matrix diffusion, int energyRef) {
        return new SimEngine(masses, topology, diffusion, energyRef);
    }

    /**
     * Returns the differential field
     * -flux * alpha * dt
     *
     * @param fields   the fields
     * @param dt       the time interval
     * @param alpha    alpha parameters
     * @param topology topology
     */
    public static Matrix differential(final Matrix fields, final double dt, final Matrix alpha, final Topology topology) {
        assert fields.getNumRows() == alpha.getNumRows()
                && fields.getNumCols() == alpha.getNumCols();
        Matrix flux = topology.flux(fields, alpha);
        return flux.muli(dt);
    }

    private final Topology topology;
    private final Matrix diffusion;
    private final Matrix masses;
    private final int energyRef;

    /**
     * Creates a simulation engine
     *
     * @param masses    the masses (noResources x 1)
     * @param topology  the topology
     * @param diffusion the diffusion configuration (noResources x 1)
     * @param energyRef the energy reference index
     */
    protected SimEngine(final Matrix masses, final Topology topology, final Matrix diffusion, int energyRef) {
        this.masses = requireNonNull(masses);
        this.topology = requireNonNull(topology);
        this.diffusion = requireNonNull(diffusion);
        this.energyRef = energyRef;
        assert masses.getNumCols() == 1
                : String.format("masses must be (1 x n) (%d x %d)", masses.getNumRows(), masses.getNumCols());
        assert diffusion.getNumCols() == 1
                : String.format("diffusion must be (1 x n) (%d x %d)", diffusion.getNumRows(), diffusion.getNumCols());
        assert diffusion.getNumRows() == masses.getNumRows()
                : String.format("diffusion (%d x %d) and masses (%d x %d) must have the same size",
                diffusion.getNumRows(), diffusion.getNumCols(),
                masses.getNumRows(), masses.getNumCols());
        assert energyRef >= 0 && energyRef < masses.getNumRows()
                : String.format("energyRef must be in 0-%d range", masses.getNumRows());
    }

    /**
     * @param status the start status
     * @param dt     the time interval
     */
    SimStatus diffuse(final SimStatus status, final double dt) {
        final Matrix quantities = status.getResources();
        final Matrix alphas = ones(1, getTopology().getNoCells())
                .prod(diffusion);
        final Matrix ds = differential(quantities, dt, alphas, topology);
        quantities.addi(ds);
        return status;
    }

    /**
     * Returns the diffusion rates in environment
     */
    public Matrix getDiffusion() {
        return diffusion;
    }

    /**
     *
     */
    public int getEnergyRef() {
        return energyRef;
    }

    /**
     * Returns the molecular masses
     */
    public Matrix getMasses() {
        return masses;
    }

    /**
     * Returns the topology
     */
    public Topology getTopology() {
        return topology;
    }

    /**
     * Returns the status after the maintaining process
     *
     * @param status the start status
     * @param dt     the time interval
     */
    SimStatus maintain(final SimStatus status, final double dt) {
        status.getPopulations().forEach(pop ->
                pop.maintain(dt, energyRef, masses));
        return status;
    }

    /**
     * @param status the start status
     * @param t      the time
     * @param random the random generator
     */
    public SimStatus next(final SimStatus status, final double t, final Random random) {
        final double dt = t - status.getT();
        final SimStatus s0 = status.copy();
        final SimStatus s1 = diffuse(s0, dt);
        final SimStatus s2 = maintain(s1, dt);
        final SimStatus s3 = survive(s2);
        final SimStatus s35 = processPhotos(s3, dt);
        final SimStatus s4 = processReactions(s35, dt);
        final SimStatus s5 = processEnvironIndividuals(s4, dt);
        final SimStatus s6 = processPopulationIndividuals(s5, dt, random);
        return s6.time(t);
    }

    /**
     * @param status the start status
     * @param dt     the time interval
     */
    SimStatus processEnvironIndividuals(final SimStatus status, final double dt) {
        // Computes the population area for each cell
        final Matrix areas = status.getTotalIndividualSurface(topology.getNoCells(), masses);
        // For each species process individual environment
        for (Population population : status.getPopulations()) {
            population.processEnvironIndividual(dt, status.getResources(),
                    areas.extractCols(population.getLocations()), masses);
        }
        return status;
    }

    /**
     * Returns the status after applying the photo reactions for each individual
     *
     * @param status the status
     * @param dt     the time interval
     */
    SimStatus processPhotos(SimStatus status, double dt) {
        // competes for lux energy
        // compute the total individual surface by location
        Matrix surfaces = status.getTotalIndividualSurface(topology.getNoCells(), masses);
        for (Population population : status.getPopulations()) {
            population.processPhotos(dt, surfaces, masses);
        }
        return status;
    }

    /**
     * @param status the start status
     * @param dt     the time interval
     * @param random the random generator
     */
    SimStatus processPopulationIndividuals(final SimStatus status, final double dt, final Random random) {
        // For each species process individual population
        final List<Population> pops = status.getPopulations().stream().map(pop ->
                pop.performPopulationIndividuals(dt, masses, topology, random)
        ).collect(Collectors.toList());
        return status.setPopulation(pops);
    }

    /**
     * Returns the status after applying the reactions for each individual
     *
     * @param status the status
     * @param dt     the time interval
     */
    SimStatus processReactions(SimStatus status, double dt) {
        for (Population population : status.getPopulations()) {
            population.processReactions(dt);
        }
        return status;
    }

    /**
     * Returns the status after the survive process.
     * Only the individual with energy and sufficient mass survive.
     *
     * @param status the start status
     */
    SimStatus survive(final SimStatus status) {
        final List<Population> pops = status.getPopulations().stream()
                .map(pop ->
                        pop.survive(energyRef, masses, status.getResources()))
                .collect(Collectors.toList());

        return status.setPopulation(pops);
    }
}