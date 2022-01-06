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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.sameInstance;
import static org.mmarini.genesis.model3.Matrix.of;
import static org.mmarini.genesis.model3.Matrix.zeros;
import static org.mmarini.genesis.model3.MatrixMatchers.matrixCloseTo;

class SimEngineMaintainTest {

    public static final int WIDTH = 2;
    public static final int HEIGHT = 2;
    public static final int NUM_INDIVIDUALS = 2;
    public static final double LENGTH = 2;
    public static final double CONSUMPTION_SPEED = 2;
    public static final double GLY_MASS = 2;
    public static final double GLY_0 = 2;
    public static final double GLY_1 = 3;
    // s * m * dt= 2 * (2 * 2) * 0.1 = 0.8
    public static final double ERG_0 = 0.75;
    // s * m * dt = 2 * (2 * 3) * 0.1 = 1.2
    public static final double ERG_1 = 2;
    public static final double DELTA_1 = -1.2;
    public static final int ENERGY_REF = 1;
    public static final int NUM_RESOURCES = 2;
    public static final double SURVIVE_MASS = 0.0;
    private static final double DT = 0.1;
    private SimEngine engine;
    private SimStatus status;

    /**
     * Given an engine and a simulation status
     * with 2 individuals: the 1st with no energy, the 2nd with energy
     */
    @BeforeEach
    void given() {
        Matrix masses = of(
                GLY_MASS,
                0
        );
        Topology topology = Topology3.create(WIDTH, HEIGHT, LENGTH);
        Matrix diffusion = zeros(NUM_RESOURCES, 1);
        Matrix resourceFlows = zeros(NUM_RESOURCES, 1);
        engine = new SimEngine(masses, topology, diffusion, ENERGY_REF);

        Matrix quantities = zeros(NUM_RESOURCES, topology.getNoCells());

        Matrix individualQties = of(new double[][]{
                {GLY_0, GLY_1},
                {ERG_0, ERG_1},
        });
        int[] locations = new int[NUM_INDIVIDUALS];
        Species species = new Species(CONSUMPTION_SPEED, SURVIVE_MASS, 0, List.of(), List.of(), List.of(), List.of());
        Population population = new Population(individualQties, List.of(), List.of(), List.of(), List.of(), locations, species);
        List<Population> populations = List.of(population);
        status = new SimStatus(0, quantities, populations);
    }

    @Test
    void maintain() {
        /*
         * Given an engine and a simulation status
         * with 2 individuals: the 1st with no energy, the 2nd with energy
         */

        /*
         * When compute diffusion alphas
         */
        SimStatus result = engine.maintain(status, DT);

        /*
         * Then should return the individuals qith energy changed
         */
        assertThat(result, sameInstance(status));
        assertThat(result.getPopulations(), hasSize(1));
        assertThat(result.getPopulations().get(0).getResources(),
                matrixCloseTo(new double[][]{
                        {GLY_0, GLY_1},
                        {0, ERG_1 + DELTA_1},
                }));
    }

}