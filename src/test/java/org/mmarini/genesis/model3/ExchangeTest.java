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

import static java.lang.Math.exp;
import static java.lang.Math.log;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mmarini.genesis.model3.Matrix.of;
import static org.mmarini.genesis.model3.Matrix.ones;

class ExchangeTest {

    static final int NUM_INDIVIDUALS = 2;
    static final int ORG_REF = 0;
    static final long SEED = 1234L;
    static final double ERG_EXCHANGE_RATE = 0.0;
    static final double T = 0.0;
    static final double BASAL_METABOLIC_RATE = 1.0;
    static final double SURVIVING_MASS = 0.0;
    static final double AREAS_BY_MASS = 1.0;
    static final double ORGANIC_MASS = 1.0;
    static final double ERG_MASS = 0.0;
    static final double ORGANIC_DIFF = 1e-6;
    static final double ERG_DIFFUSION = 1e-6;
    static final int ENERGY_REF = 1;
    static final int WIDTH = 2;
    static final int HEIGHT = 2;
    static final int NUM_CELLS = WIDTH * HEIGHT;
    static final int[] LOCATION = new int[]{0, 1};
    static final double LENGTH = 2;
    static final double MIN_IND_ORG = 1;
    static final double MAX_IND_ORG = 10;
    static final double MIN_ALPHA1 = 1 - 0.5;
    static final double MAX_ALPHA1 = 1 - 0.1;
    static final double MIN_ALPHA2 = 1 + 0.1;
    static final double MAX_ALPHA2 = 1 + 0.5;
    static final double MIN_ORG_LEVEL = MIN_IND_ORG * MIN_ALPHA1;
    static final double MIN_ERG_LEVEL = 1;
    static final double MAX_ERG_LEVEL = 2;
    static final Matrix MIN_LEVELS = of(MIN_ORG_LEVEL, MIN_ERG_LEVEL);
    static final double MAX_ORG_LEVEL = MAX_IND_ORG * MAX_ALPHA2;
    static final Matrix LOG_LEVELS = of(
            log(MAX_ORG_LEVEL / MIN_ORG_LEVEL),
            log(MAX_ERG_LEVEL / MIN_ERG_LEVEL)
    );
    static final double MIN_DT = 0.1;
    static final double MAX_DT = 0.3;
    static final double MIN_ENV_ORG = 1;
    static final double MAX_ENV_ORG = 10;
    static final double MIN_RATE_ORG = 1e-3;
    static final double MAX_RATE_ORG = 10e-3;
    static final double ERG_SIGNAL = 1;

    static Stream<Arguments> arguments() {
        return ArgumentGenerator.create(SEED)
                .uniform(1, 2)
                .exponential(MIN_DT, MAX_DT)
                .exponential(MIN_ENV_ORG, MAX_ENV_ORG)
                .exponential(MIN_RATE_ORG, MAX_RATE_ORG)
                .exponential(MIN_IND_ORG, MAX_IND_ORG)
                .exponential(MIN_ALPHA1, MAX_ALPHA1)
                .exponential(MIN_ALPHA2, MAX_ALPHA2)
                .generate();
    }


    @ParameterizedTest
    @MethodSource("arguments")
    void survive(final int n,
                 final double dt,
                 final double envOrg,
                 final double orgExchangeRate,
                 final double indOrg,
                 final double orgAlpha1,
                 final double orgAlpha2) {
        /*
        Given an engine
         */
        final Matrix masses = of(ORGANIC_MASS, ERG_MASS);
        final Topology topology = Topology3.create(WIDTH, HEIGHT, LENGTH);
        final Matrix diffusion = of(ORGANIC_DIFF, ERG_DIFFUSION);
        final Matrix resourceFlows = of(ORGANIC_DIFF, ERG_DIFFUSION);
        final SimEngine engine = new SimEngine(masses, topology, diffusion, ENERGY_REF);
        /*
        And a single species with an exchange gene
         */
        Matrix rates = of(orgExchangeRate, ERG_EXCHANGE_RATE);
        final ExchangeResourceGene eipGene = new ExchangeResourceGene(MIN_LEVELS, LOG_LEVELS, rates);
        final List<EIPGene> eipGenes = List.of(eipGene);
        final Species species = new Species(BASAL_METABOLIC_RATE, SURVIVING_MASS, AREAS_BY_MASS,
                List.of(), List.of(), eipGenes, List.of());

        /*
         And an initial simulation status with
         very low diffusion speed of organic mass
         2 individuals both with sufficient energy for test duration,
         - 1st with organic mass below the required,
         - 2nd with organic mass above the required but above the environment
         */
        final Matrix quantities = of(envOrg, 0.0).prod(ones(1, NUM_CELLS));
        final double orgTarget1 = indOrg * orgAlpha1;
        final double orgTarget2 = indOrg * orgAlpha2;
        final double ergQty = orgTarget2 * BASAL_METABOLIC_RATE * n * 1.1;
        final Matrix indQties = of(new double[][]{
                {indOrg, indOrg},
                {ergQty, ergQty},
        });
        final double orgSignal1 = log(orgTarget1 / MIN_ORG_LEVEL) / log(MAX_ORG_LEVEL / MIN_ORG_LEVEL);
        final double orgSignal2 = log(orgTarget2 / MIN_ORG_LEVEL) / log(MAX_ORG_LEVEL / MIN_ORG_LEVEL);
        final Matrix eipSign = of(new double[][]{
                {orgSignal1, orgSignal2},
                {ERG_SIGNAL, ERG_SIGNAL}});
        final Population population = new Population(indQties,
                List.of(), List.of(), List.of(eipSign), List.of(),
                LOCATION, species);
        final List<Population> populations = List.of(population);
        final SimStatus initStatus = new SimStatus(T, quantities, populations);
        final Random random = new Random(SEED);

        /*
        When simulation for n step
         */
        SimStatus status = initStatus;
        double t = T;
        for (int i = 0; i < n; i++) {
            t += dt;
            status = engine.next(status, t, random);
            assertThat(status.getPopulations().get(0).getResources().getNumCols(),
                    equalTo(NUM_INDIVIDUALS));
        }

        /*
        Then the population should contain 2 individuals
         */
        assertThat(status, notNullValue());
        final Matrix quantities1 = status.getPopulations().get(0).getResources();
        assertThat(quantities1.getNumCols(), equalTo(NUM_INDIVIDUALS));

        /*
        Then the 1st individual should increment the organic mass to near the required level
         */
        // Let's compute the organic mass rate mol/s
        final double orgRate1 = indOrg * orgExchangeRate;
        // Compute the remaining exchanging organic
        final double dorg1 = (indOrg - orgTarget1) * exp(-t * orgRate1) * 1.1;
        assertThat(quantities1.get(ORG_REF, 0),
                closeTo(orgTarget1, dorg1));
        /*
        And the 2nd individual should decrease the organic mass to near the required level
         */
        // Let's compute the organic mass rate (1/s)
        final double orgRate2 = orgTarget2 * orgExchangeRate;
        // Compute the remaining exchanging organic
        // exp(-t rate)
        final double dorg2 = (orgTarget2 - indOrg) * exp(-t * orgRate2) * 1.1;
        assertThat(quantities1.get(ORG_REF, 1),
                closeTo(orgTarget2, dorg2));
    }

}