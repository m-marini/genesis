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

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.Random;
import java.util.stream.Stream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.mmarini.genesis.model3.Matrix.of;
import static org.mmarini.genesis.model3.Matrix.ones;
import static org.mmarini.genesis.model3.MatrixMatchers.matrixCloseTo;
import static org.mmarini.genesis.model3.TestUtils.expSignal;

class CloneTest {

    static final int MIN_NO_CLONES = 1;
    static final int MAX_NO_CLONES = 2;
    static final double MUTATION_SIGMA = 0.0;
    static final double MUTATION_PROB = 0.0;
    static final double ADJACENT_PREFERENCE = 0.0;
    static final double IN_PLACE_PREFERENCE = 0.0;
    static final double MAX_ENERGY_PROB_RATE = 0.0;
    static final double MIN_ENERGY_PROB_RATE = 0.0;
    static final double MAX_MASS_PROB_RATE = 0.0;
    static final double MIN_MASS_PROB_RATE = 0.0;
    static final double MAX_ENERGY_THRESHOLD = 0.0;
    static final double MIN_ENERGY_THRESHOLD = 0.0;
    static final double MAX_MASS_THRESHOLD = 0.0;
    static final double MIN_MASS_THRESHOLD = 0.0;
    static final double T = 0.0;
    static final double SURVIVING_MASS = 0.0;
    static final double ERG_MASS = 0.0;
    static final int NUM_INDIVIDUALS = 1;
    static final long SEED = 1234L;
    static final double BASAL_METABOLIC_RATE = 1.0;
    static final double AREAS_BY_MASS = 1.0;
    static final double ORGANIC_MASS = 1.0;
    static final double ORGANIC_DIFF = 1e-6;
    static final double ERG_DIFFUSION = 1e-6;
    static final int ENERGY_REF = 1;
    static final int WIDTH = 2;
    static final int HEIGHT = 2;
    static final int NUM_CELLS = WIDTH * HEIGHT;
    static final int NO_INDIVIDUALS = 1;
    static final int[] LOCATION = new int[NO_INDIVIDUALS];
    static final double LENGTH = 2;
    static final double MIN_DT = 0.1;
    static final double MAX_DT = 0.3;
    static final double MIN_ENV_ORG = 1;
    static final double MAX_ENV_ORG = 10;
    static final double MIN_MASS_RATE_ORG = 1e-3;
    static final double MAX_MASS_RATE_ORG = 10e-3;
    static final int STEP_CONTINGENCY = 2;
    static final double MIN_CLONE_MASS_THRESHOLD = 1.0;
    static final double MAX_CLONE_MASS_THRESHOLD = 5.0;
    static final double MIN_CLONE_ERG_THRESHOLD = 1.0;
    static final double MAX_CLONE_ERG_THRESHOLD = 5.0;
    static final double MIN_ERG_RATE_ORG = 1e-3;
    static final double MAX_ERG_RATE_ORG = 10e-3;
    static final double MIN_ENV_ERG = 1;
    static final double MAX_ENV_ERG = 10;

    static Stream<Arguments> arguments() {
        return ArgumentGenerator.create(SEED)
                .uniform(MIN_NO_CLONES, MAX_NO_CLONES)
                .exponential(MIN_DT, MAX_DT)
                .exponential(MIN_ENV_ORG, MAX_ENV_ORG)
                .exponential(MIN_ENV_ERG, MAX_ENV_ERG)
                .exponential(MIN_CLONE_MASS_THRESHOLD, MAX_CLONE_MASS_THRESHOLD)
                .exponential(MIN_CLONE_ERG_THRESHOLD, MAX_CLONE_ERG_THRESHOLD)
                .exponential(MIN_MASS_RATE_ORG, MAX_MASS_RATE_ORG)
                .exponential(MIN_ERG_RATE_ORG, MAX_ERG_RATE_ORG)
                .generate().limit(1);
    }

    @ParameterizedTest
    @MethodSource("arguments")
    void clone(final int noClones,
               final double dt,
               final double envOrg,
               final double envErg,
               final double cloneMassThreshold,
               final double cloneErgThreshold,
               final double massProbRate,
               final double ergProbRate) {
        /*
        Given an engine
         */
        final Matrix masses = of(ORGANIC_MASS, ERG_MASS);
        final Topology topology = Topology3.create(WIDTH, HEIGHT, LENGTH);
        final Matrix diffusion = of(ORGANIC_DIFF, ERG_DIFFUSION);
        final Matrix resourceFlows = of(ORGANIC_DIFF, ERG_DIFFUSION);
        final SimEngine engine = new SimEngine(masses, topology, diffusion, ENERGY_REF);
        /*
        And a single species with a clone gene
         */
        final CloneGene pipGene = CloneGene.create(MIN_MASS_THRESHOLD,
                MAX_MASS_THRESHOLD,
                MIN_ENERGY_THRESHOLD,
                MAX_ENERGY_THRESHOLD,
                MIN_MASS_PROB_RATE,
                MAX_MASS_PROB_RATE,
                MIN_ENERGY_PROB_RATE,
                MAX_ENERGY_PROB_RATE,
                ENERGY_REF,
                IN_PLACE_PREFERENCE,
                ADJACENT_PREFERENCE,
                MUTATION_PROB,
                MUTATION_SIGMA);
        final List<PIPGene> pipGenes = List.of(pipGene);
        final Species species = new Species(BASAL_METABOLIC_RATE, SURVIVING_MASS, AREAS_BY_MASS,
                List.of(), List.of(), List.of(), pipGenes);

        /*
         And an initial simulation status with
         very low diffusion speed of organic mass
         an individual with organic resources sufficient for clone noClones individuals
         and energy sufficient for test duration and clone noClones individuals
         */
        final int noSteps = noClones * STEP_CONTINGENCY;
        final Matrix envResources = of(envOrg, envErg).prod(ones(MIN_NO_CLONES, NUM_CELLS));
        final double indOrg = cloneMassThreshold * (noClones + 0.5);
        final double indErg = cloneErgThreshold * (noClones + 0.5) + noSteps * BASAL_METABOLIC_RATE;
        final Matrix indResources = of(indOrg, indErg);
        final double massThresholdSignal = expSignal(cloneMassThreshold, MIN_MASS_THRESHOLD, MAX_MASS_THRESHOLD);
        final double ergThresholdSignal = expSignal(cloneErgThreshold, MIN_ENERGY_THRESHOLD, MAX_ENERGY_THRESHOLD);
        final double massProbRateSignal = expSignal(massProbRate, MIN_ENERGY_THRESHOLD, MAX_ENERGY_THRESHOLD);
        final double ergProbRateSignal = expSignal(ergProbRate, MIN_ENERGY_THRESHOLD, MAX_ENERGY_THRESHOLD);
        final Matrix pipSign = of(
                massThresholdSignal,
                ergThresholdSignal,
                massProbRateSignal,
                ergProbRateSignal);
        final Population population = new Population(indResources,
                List.of(), List.of(), List.of(), List.of(pipSign),
                LOCATION, species);
        final List<Population> populations = List.of(population);
        final Random random = new Random(SEED);

        /*
        When simulation for n steps
         */
        SimStatus status = new SimStatus(T, envResources, populations);
        double t = T;
        for (int i = 0; i < noSteps; i++) {
            t += dt;
            status = engine.next(status, t, random);
            assertThat(status.getPopulations().get(0).getResources().getNumCols(),
                    greaterThanOrEqualTo(NUM_INDIVIDUALS));
        }

        /*
        Then the population should contain noClones individuals
        And the mass of individuals should be close to mass threshold
        And the energy of individuals should be close to energy threshold
         */
        final Matrix expected = of(cloneMassThreshold, cloneErgThreshold).prod(ones(1, noClones + MIN_NO_CLONES));
        assertThat(status.getPopulations().get(0).getResources(), matrixCloseTo(expected));

        /*
        And the locations should be distributed proportionally to the
        probabilities
         */

        /*
        And the cloned genes should change accordingly to the mutation rules
        (prob, sigma)
         */
    }
}