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

class SimEnginePITest {

    static final int WIDTH = 2;
    static final int HEIGHT = 2;
    static final int NUM_INDIVIDUALS = 1;
    static final int NUM_RESOURCES = 4;
    static final int PRODUCT_REF = 1;
    static final int ENERGY_REF = 3;
    static final double LENGTH = 2;
    static final double BASAL_METABOLIC_RATE = 2;
    static final double SURVIVE_MASS = 0.0;
    static final double DT = 0.1;
    static final double REAGENT_MASS = 2.0;
    static final double PRODUCT_MASS = 3.0;
    static final double RATE_CONTROLLER_MASS = 0;
    static final double ERG_MASS = 0;
    static final double RATE_CONTROLLER = 2;
    static final double ERG = 1;
    static final double MIN_LEVEL = 2;
    static final double MAX_LEVEL = 4;
    static final double TARGET = Math.sqrt(MAX_LEVEL * MIN_LEVEL);
    static final double REAGENT_CONSUMPTION = 1;
    static final double PRODUCT_PRODUCTION = 1;

    static final double RATE = 2;
    static final double MAX_DELTA_PRODUCT = DT * RATE * RATE_CONTROLLER;
    static final double DELTA_PRODUCT = MAX_DELTA_PRODUCT / 2;

    static final double FEW_BELOW_TARGET = TARGET - DELTA_PRODUCT;
    static final double DELTA_REAGENT = DELTA_PRODUCT / PRODUCT_PRODUCTION * REAGENT_CONSUMPTION;
    static final double THRESHOLD = DELTA_REAGENT / 2;
    static final double REAGENT = THRESHOLD + DELTA_REAGENT;

    static final Matrix THRESHOLDS = of(
            THRESHOLD,
            0,
            0,
            0
    );
    static final Matrix REAGENTS = of(
            REAGENT_CONSUMPTION,
            0,
            0,
            0
    );
    static final Matrix PRODUCTS = of(
            0,
            PRODUCT_PRODUCTION,
            0,
            0
    );
    static final Matrix SPEEDS = of(
            0,
            0,
            RATE,
            0
    );
    static final double GENE = 0.5;
    SimEngine engine;
    SimStatus status;

    /**
     * Given an engine
     * with a reagent, a product and a speed controller
     * and a simulation status
     * with 2 species populations with the same genes with
     * - an individual with few below target and sufficient resources
     * - an individual with few below target and sufficient resources
     */
    @BeforeEach
    void given() {
        Matrix masses = of(
                REAGENT_MASS,
                PRODUCT_MASS,
                RATE_CONTROLLER_MASS,
                ERG_MASS
        );
        Topology topology = Topology3.create(WIDTH, HEIGHT, LENGTH);
        Matrix diffusion = zeros(NUM_RESOURCES, 1);
        Matrix resourceFlows = zeros(NUM_RESOURCES, 1);
        engine = new SimEngine(masses, topology, diffusion, ENERGY_REF);

        Matrix quantities = zeros(NUM_RESOURCES, topology.getNoCells());

        Matrix individualQties0 = of(
                REAGENT,
                FEW_BELOW_TARGET,
                RATE_CONTROLLER,
                ERG
        );
        Matrix individualQties1 = of(
                REAGENT,
                FEW_BELOW_TARGET,
                RATE_CONTROLLER,
                ERG
        );
        int[] locations = new int[NUM_INDIVIDUALS];

        Reaction reaction = Reaction.create(REAGENTS, PRODUCTS, THRESHOLDS, SPEEDS);
        IPGene gene = new ResourceGene(PRODUCT_REF, MIN_LEVEL, MAX_LEVEL, reaction);
        List<IPGene> genes = List.of(gene);

        Species species =  Species.create(BASAL_METABOLIC_RATE, SURVIVE_MASS, 0, List.of(), genes, List.of(), List.of());

        List<Matrix> genotypes = List.of(of(GENE));
        Population population0 = new Population(individualQties0, List.of(), genotypes, List.of(), List.of(), locations, species);
        Population population1 = new Population(individualQties1,  List.of(), genotypes, List.of(), List.of(), locations, species);
        List<Population> populations = List.of(population0, population1);
        status = new SimStatus(0, quantities, populations);
    }

    @Test
    void processIndividuals() {
        /*
         * Given ... see given()
         */

        /*
         * When process individuals
         */
        SimStatus result = engine.processIndividuals(status, DT);

        /*
         * Then should return the individuals with resource changed
         */
        assertThat(result, sameInstance(status));
        assertThat(result.getPopulations(), hasSize(2));
        assertThat(result.getPopulations().get(0).getResources(),
                matrixCloseTo(new double[][]{
                        {REAGENT - DELTA_REAGENT},
                        {TARGET},
                        {RATE_CONTROLLER},
                        {ERG}
                }));
        assertThat(result.getPopulations().get(1).getResources(),
                matrixCloseTo(new double[][]{
                        {REAGENT - DELTA_REAGENT},
                        {TARGET},
                        {RATE_CONTROLLER},
                        {ERG}
                }));
    }

}