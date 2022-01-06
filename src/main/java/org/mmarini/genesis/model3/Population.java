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

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.mmarini.genesis.model3.Matrix.*;

/**
 * The population is a set of individual of the same species with
 * available resources, location for each individual, parameters for
 * each group of genes
 */
public class Population {
    /**
     * Return a copy of genes
     *
     * @param genes     the genes
     * @param surviving the list of surviving individuals
     */
    static List<Matrix> copyGenes(final List<Matrix> genes, final int[] surviving) {
        return genes.stream().map(gene ->
                        gene.extractCols(surviving))
                .collect(Collectors.toList());
    }

    /**
     * Returns a population
     *
     * @param resources         the individual resources (noResources x noIndividuals)
     * @param photoTargetLevels the target level of each process, a matrix (1 x noIndividuals)
     * @param ipSignals         the ip signals
     * @param eipSignals        the eip signals
     * @param pipSignals        the pip signals
     * @param locations         the location of individuals
     * @param species           the species
     */
    public static Population create(final Matrix resources, List<Matrix> photoTargetLevels, final List<Matrix> ipSignals, final List<Matrix> eipSignals,
                                    List<Matrix> pipSignals, final int[] locations, final Species species) {
        return new Population(resources, photoTargetLevels, ipSignals, eipSignals, pipSignals, locations, species);
    }

    private final Matrix resources;
    private final List<Matrix> photoTargetLevels;
    private final List<Matrix> ipSignals;
    private final List<Matrix> eipSignals;
    private final List<Matrix> pipSignals;
    private final int[] locations;
    private final Species species;

    /**
     * Creates a population
     *
     * @param resources         the individual resources (noResources x noIndividuals)
     * @param photoTargetLevels the target level of each process, a matrix (1 x noIndividuals)
     * @param ipSignals         the individual signals, a matrix (n x m) for each gene
     * @param eipSignals        the environmental-individual genes
     * @param pipSignals        the population-individual genes
     * @param locations         the individual locations
     * @param species           the species of population
     */
    protected Population(final Matrix resources, List<Matrix> photoTargetLevels, final List<Matrix> ipSignals, final List<Matrix> eipSignals,
                         List<Matrix> pipSignals, final int[] locations, final Species species) {
        this.photoTargetLevels = photoTargetLevels;
        this.pipSignals = pipSignals;
        assert ipSignals.size() == species.getIpGenes().size()
                : String.format("# ipGenes of individuals != # ipGenes of species: %d != %d",
                ipSignals.size(), species.getIpGenes().size());
        assert eipSignals.size() == species.getEipGenes().size()
                : String.format("# eipGenes of individuals != # eipGenes of species: %d != %d",
                ipSignals.size(), species.getIpGenes().size());

        assert resources.getNumCols() == locations.length
                : String.format("# cols of resources != # of locations: %d != %d",
                resources.getNumCols(), locations.length);
        this.resources = resources;
        this.ipSignals = ipSignals;
        this.eipSignals = eipSignals;
        this.locations = locations;
        this.species = species;
    }

    /**
     * Returns the population with a set of cloned individual
     *
     * @param random       the random generator
     * @param locationProb the location probabilities 4x1
     * @param cloneProb    the clone probability
     * @param mutationProb the mutation probability
     * @param topology     the topology
     * @param energy       the energy to transfer by individuals 1xn
     * @param kMass        the quantities factor to transfer by individual 1xn
     * @param energyRef    index of energy
     * @param ind          the indices of cloning individuals
     */
    public Population cloneIndividuals(final Random random,
                                       final Matrix locationProb,
                                       final double cloneProb,
                                       final double mutationProb,
                                       final Topology topology,
                                       final Matrix energy,
                                       final Matrix kMass,
                                       final int energyRef,
                                       final int... ind) {

        if (ind.length == 0) {
            return this;
        }
        final int[] cloneLocations = createCloneLocations(random, locationProb, topology, ind);

        // Computes the signals of clones
        List<Matrix> cloneIp = SignalClone.clone(ipSignals, cloneProb, mutationProb, random, ind);
        List<Matrix> cloneEip = SignalClone.clone(eipSignals, cloneProb, mutationProb, random, ind);
        List<Matrix> clonePip = SignalClone.clone(pipSignals, cloneProb, mutationProb, random, ind);

        // Creates the clone individual resources
        int n = resources.getNumCols();
        int n1 = n + ind.length;
        int[] newIdx = IntStream.range(n, n1).toArray();
        Matrix dq = resources.extractCols(ind)
                .muli(kMass)
                .assignRows(energy, energyRef);

        // Creates the changes of parent individual resources
        Matrix dq1 = zeros(resources.getNumRows(), n1)
                .assignCols(dq, newIdx)
                .assignCols(dq.negi(), ind);

        // Create the population resources with the clone
        Matrix newQties = zeros(resources.getNumRows(), n1)
                .assign(resources, 0, 0)
                .addi(dq1);

        // Creates the final individual locations
        int[] newLoc = Arrays.copyOf(locations, n1);
        System.arraycopy(cloneLocations, 0, newLoc, n, cloneLocations.length);

        return new Population(newQties, photoTargetLevels, cloneIp, cloneEip, clonePip, newLoc, species);
    }

    /**
     * @param dt           the time interval
     * @param targetLevels the target levels (1 x ni)
     * @param gene         the gene
     */
    public Population controlResources(final double dt,
                                       final Matrix targetLevels,
                                       final ResourceGene gene) {
        final int ref = gene.getRef();
        // Computes the current reference levels (1 x ni)
        final Matrix currentRefLevels = resources.extractRow(ref);
        // Computes the maximum reference resource changes (1 x ni):
        // max(target - currentLevel, 0)
        final Matrix maxDelta = targetLevels.subi(currentRefLevels).maxi(0);
        // Compute the max target by reaction (1 x ni)
        final Reaction reaction = gene.getReaction();
        final Matrix maxReact = reaction.max(ref, resources, dt);
        // Compute the delta on reference substance (1 x ni)
        final Matrix delta = maxDelta.mini(maxReact);
        // Compute the delta quantities for individuals (nr x ni)
        final Matrix d = reaction.apply(ref, delta);
        // Updates the final quantities of individuals
        resources.addi(d);
        return this;
    }

    /**
     *
     */
    public Population copy() {
        return new Population(resources.copy(),
                photoTargetLevels, ipSignals.stream().map(Matrix::copy).collect(Collectors.toList()),
                eipSignals.stream().map(Matrix::copy).collect(Collectors.toList()),
                pipSignals.stream().map(Matrix::copy).collect(Collectors.toList()),
                Arrays.copyOf(locations, locations.length),
                species
        );
    }

    /**
     * @param random       the random generator
     * @param locationProb the location probabilities 4x1
     * @param topology     the topology
     * @param ind          the indices of cloning individuals
     */
    int[] createCloneLocations(Random random, Matrix locationProb, Topology topology, int... ind) {
        // Compute the probabilities of locations
        final Matrix locProb = ones(1, ind.length).prod(locationProb);
        final int[] locIdx = locProb.choose(rand(1, ind.length, random));

        // Computes the locations of clone
        return IntStream.range(0, ind.length).map(i -> {
            int indIdx = ind[i];
            int adj = locIdx[i];
            int loc = locations[indIdx];
            return (adj == 0) ? loc : topology.getAdjacent(loc, adj - 1);
        }).toArray();
    }

    /**
     * Returns the proportions of resoruce limited by surface by individual (1 x numIndividuals)
     *
     * @param totalSurface total surface by location (1 x numCells)
     * @param masses       the masses by resources (numResources x 1)
     */
    Matrix distributeBySurface(Matrix totalSurface, Matrix masses) {
        Matrix surfaces = getIndividualSurface(masses);
        Matrix totByIndividuals = totalSurface.extractCols(locations);
        return surfaces.divi(totByIndividuals);
    }

    /**
     * Returns this population by exchangeing the individual resource with environment resources
     *
     * @param dt                     the time interval
     * @param targets                the resource targets (nr x ni)
     * @param envResources           the environ resources (nr x nc)
     * @param totalAreasByIndividual the areas by individuals (1 x ni)
     * @param molecularMasses        the molecular masses (nr x 1)
     * @param exchangeRates          exchange rates (1/mol/dt) (nr x 1)
     */
    public Population exchangeResources(final double dt,
                                        final Matrix targets,
                                        final Matrix envResources,
                                        final Matrix totalAreasByIndividual,
                                        final Matrix molecularMasses,
                                        final Matrix exchangeRates) {
        // Computes the maximum individual resource changes (nr x ni):
        // individualResources * surface / areas,
        final Matrix maxIndExchanges = envResources.extractCols(locations)
                .divi(totalAreasByIndividual)
                .muli(getIndividualSurface(molecularMasses));
        // Computes the resource changes by individual:
        // min(resourceTargets - quantities, qa) * rates * dt
        final Matrix dIndResources = targets.subi(resources)
                .mini(maxIndExchanges)
                .muli(exchangeRates)
                .muli(dt);
        // updates the individual resources
        resources.addi(dIndResources);
        // updates the environments resources
        envResources.mapiCols((v, i, j, k) -> v - dIndResources.get(i, j), locations);
        return this;
    }

    /**
     *
     */
    public List<Matrix> getEipSignals() {
        return eipSignals;
    }

    /**
     * Returns the number of individuals
     */
    public int getIndividualCount() {
        return locations.length;
    }

    /**
     * Returns masses ^ (2/3) * areaByMass (1 x numIndividuals)
     *
     * @param masses resource molecular masses (numResources x 1)
     */
    public Matrix getIndividualSurface(Matrix masses) {
        return getMasses(masses)
                .powi(species.getFractalDimension() / 3);
    }

    /**
     *
     */
    public List<Matrix> getIpSignals() {
        return ipSignals;
    }

    /**
     *
     */
    public int[] getLocations() {
        return locations;
    }

    /**
     * Returns the masses of individuals (1 x ni)
     *
     * @param masses the molecular masses
     */
    public Matrix getMasses(Matrix masses) {
        return resources.copy().muli(masses).sumCols();
    }

    /**
     *
     */
    public List<Matrix> getPhotoTargetLevels() {
        return photoTargetLevels;
    }

    /**
     *
     */
    public List<Matrix> getPipSignals() {
        return pipSignals;
    }

    /**
     *
     */
    public Matrix getResources() {
        return resources;
    }

    /**
     *
     */
    public Species getSpecies() {
        return species;
    }

    /**
     * Returns the total population surface by each location (1 x noCells)
     *
     * @param masses  the mass of each resource (noResources x 1)
     * @param noCells the number of cells
     */
    public Matrix getTotalSurface(Matrix masses, int noCells) {
        Matrix indMasses = getIndividualSurface(masses);
        Matrix result = Matrix.zeros(1, noCells);
        result.mapiCols((v, i, j, k) -> v + indMasses.get(i, j),
                locations);
        return result;
    }

    /**
     * Returns the population changed by maintenance.
     * Reduces the individual energy due to basal metabolic rate
     *
     * @param dt              the time interval
     * @param energyRow       the index of energy substance
     * @param molecularMasses the molecular mass of substances
     */
    public Population maintain(double dt, int energyRow, Matrix molecularMasses) {
        // Computes the required basal metabolic energy
        Matrix reqEnergy = getMasses(molecularMasses)
                .muli(species.getBasalMetabolicRate())
                .muli(dt);
        // Compute the remainder energy
        Matrix energy = resources.extractRow(energyRow).subi(reqEnergy);
        Matrix erg = energy.maxi(0);
        resources.assignRows(erg, energyRow);
        return this;
    }

    /**
     * @param dt                  time interval
     * @param molecularMasses     molecular masses (nr x 1)
     * @param topology            the topology
     * @param random              random generator
     * @param massThresholds      mass thresholds (1 x ni)
     * @param energyThresholds    energy thresholds (1 x ni)
     * @param massCloneProbRate   mass clone probability rate (1/s/mol) (1 x ni)
     * @param energyCloneProbRate energy clone probability rate (1/s/mol) (1 x ni)
     * @param gene                the gene
     */
    public Population performClone(final double dt,
                                   final Matrix molecularMasses,
                                   final Topology topology,
                                   final Random random,
                                   final Matrix massThresholds,
                                   final Matrix energyThresholds,
                                   final Matrix massCloneProbRate,
                                   final Matrix energyCloneProbRate,
                                   final CloneGene gene
    ) {
        // Computes the remaining mass
        final Matrix dMass = getMasses(molecularMasses)
                .subi(massThresholds)
                .maxi(0);
        // Computes the remaining energy
        final int energyRef = gene.getEnergyRef();
        final Matrix dErg = resources
                .extractRow(energyRef)
                .subi(energyThresholds)
                .maxi(0);
        // Computes the mass probability
        final Matrix massProb = dMass.copy()
                .muli(dt).muli(massCloneProbRate)
                .negi().expm1i().negi();
        // Computes the energy probability
        final Matrix energyProb = dErg.copy()
                .muli(dt).muli(energyCloneProbRate)
                .negi().expm1i().negi();
        // Computes the probability
        final Matrix prob = massProb.mini(energyProb);
        final Matrix rnd = Matrix.rand(1, prob.getNumCols(), random);

        // Extract the index of cloning individuals
        final int[] ind = rnd.subi(prob).cellsOf(p -> p < 0);

        // Extract the energy transfer
        final Matrix dCloneErg = dErg.extractCols(ind);

        // Computes the relative mass transfer
        final Matrix kmass = massThresholds.extractCols(ind)
                .divi(getMasses(molecularMasses).extractCols(ind))
                .subi(1)
                .negi();

        return cloneIndividuals(random,
                gene.getLocationProb(),
                gene.getMutationProb(),
                gene.getMutationSigma(),
                topology,
                dCloneErg,
                kmass,
                energyRef,
                ind);
    }

    /**
     * @param dt
     * @param molecularMasses
     * @param topology
     * @param random
     * @return
     */
    public Population performPopulationIndividuals(final double dt,
                                                   final Matrix molecularMasses,
                                                   final Topology topology,
                                                   final Random random) {
        final List<? extends PIPGene> pipGenes = species.getPipGenes();
        final int n = pipGenes.size();
        Population pop = this;
        for (int i = 0; i < n; i++) {
            pop = pipGenes.get(i).execute(pop, pop.eipSignals.get(i), dt, molecularMasses, topology, random);
        }
        return pop;
    }

    /**
     * Returns the population processing environment-individual genetic codes
     *
     * @param dt                the time interval
     * @param envResources      the environment resources
     * @param areasByIndividual the surface area
     * @param molecularMasses   the molecular mass
     */
    public Population processEnvironIndividual(final double dt,
                                               final Matrix envResources,
                                               final Matrix areasByIndividual,
                                               final Matrix molecularMasses) {
        final List<? extends EIPGene> eipGenes = species.getEipGenes();
        final int n = eipGenes.size();
        Population pop = this;
        for (int i = 0; i < n; i++) {
            pop = eipGenes.get(i).execute(pop, pop.eipSignals.get(i), dt, envResources, areasByIndividual, molecularMasses);
        }
        return pop;
    }

    /**
     * Returns the population processing individual genetic codes
     *
     * @param dt                the time interval
     * @param areasByIndividual the surface area
     * @param molecularMasses   the molecular mass
     */
    public Population processIndividual(double dt, Matrix areasByIndividual, Matrix molecularMasses) {
        final List<? extends IPGene> ipGenes = species.getIpGenes();
        final int n = ipGenes.size();
        Population pop = this;
        for (int i = 0; i < n; i++) {
            pop = ipGenes.get(i).execute(pop, pop.ipSignals.get(i), dt, null, areasByIndividual, molecularMasses);
        }
        return this;
    }

    /**
     * Returns the this population with individual resources changed by photo process
     *
     * @param dt           the time interval
     * @param totalSurface the total surface by location (1 x noCells)
     * @param masses       the masses by resource (noResources x 1)
     */
    public Population processPhoto(double dt, Matrix totalSurface, Matrix masses) {
        final List<? extends PhotoProcess> processes = species.getPhotoProcess();
        Matrix distribution = distributeBySurface(totalSurface, masses);
        final int noGenes = photoTargetLevels.size();
        for (int i = 0; i < noGenes; i++) {
            Matrix changes = processes.get(i).computeChanges(getResources(), photoTargetLevels.get(i), dt, distribution);
            getResources().addi(changes);
        }
        return this;
    }

    /**
     * Returns the population changed by maintenance.
     * Removes the individuals with no energy and no sufficient mass
     * releasing the resource in the environment
     *
     * @param energyRow       the index of energy substance
     * @param molecularMasses the molecular mass of substances
     * @param envResources    the environmental resources
     */
    public Population survive(final int energyRow,
                              final Matrix molecularMasses,
                              final Matrix envResources) {
        final Matrix indMasses = getMasses(molecularMasses);
        // Gets the surviving individuals index
        final int[] surviving = IntStream.range(0, locations.length)
                .filter(j ->
                        !(indMasses.get(0, j) < species.getSurvivingMass()
                                || resources.get(energyRow, j) <= 0)
                ).toArray();
        // Transfers resource of dying individuals to environment
        IntStream.range(0, locations.length)
                .filter(j ->
                        indMasses.get(0, j) < species.getSurvivingMass()
                                || resources.get(energyRow, j) <= 0
                ).forEach(j -> envResources.assignCol(locations[j],
                        (v, i) -> v + resources.get(i, j)
                ));
        final Matrix survivedIndResources = resources.extractCols(surviving);
        final List<Matrix> ipSignals1 = copyGenes(ipSignals, surviving);
        final List<Matrix> eipSignals1 = copyGenes(eipSignals, surviving);
        final List<Matrix> pipSignals1 = copyGenes(pipSignals, surviving);
        final int[] survivedIndLoc = IntStream.of(surviving).map(i -> locations[i]).toArray();
        return new Population(survivedIndResources, photoTargetLevels, ipSignals1, eipSignals1, pipSignals1, survivedIndLoc, species);
    }
}