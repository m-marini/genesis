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
import java.util.stream.Stream;

import static java.lang.Math.log;
import static java.lang.Math.min;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.sameInstance;
import static org.mmarini.genesis.model3.Matrix.of;
import static org.mmarini.genesis.model3.MatrixMatchers.matrixCloseTo;

class ExchangeResourceGeneTest {

    static final int NO_INDIVIDUALS = 3;
    static final int NUM_INDIVIDUALS = NO_INDIVIDUALS;
    static final double HIGH_SCALE = 10.0;
    static final double LITTLE_HIGH_SCALE = 1.01;
    static final double LOW_SCALE = 0.1;
    static final long SEED = 1234;
    static final double MIN_IND_RESOURCE = 1;
    static final double MAX_IND_RESOURCES = 10;
    static final double MIN_AREA_BY_MASS = 1;
    static final double MAX_AREA_BY_MASS = 5;
    static final double MIN_ENV_RESOURCE = 1;
    static final double MAX_ENV_RESOURCES = 10;
    static final double MIN_DT = 0.1;
    static final double MAX_DT = 0.3;
    private static final double MIN_MASS = 1;
    private static final double MAX_MASS = 10;
    private static final double MIN_EXCHANGE_RATE = 1e-3;
    private static final double MAX_EXCHANGE_RATE = 10e-3;

    static Stream<Arguments> arguments() {
        return ArgumentGenerator.create(SEED)
                .exponential(MIN_IND_RESOURCE, MAX_IND_RESOURCES)
                .exponential(MIN_IND_RESOURCE, MAX_IND_RESOURCES)
                .exponential(MIN_AREA_BY_MASS, MAX_AREA_BY_MASS)
                .exponential(MIN_ENV_RESOURCE, MAX_ENV_RESOURCES)
                .exponential(MIN_ENV_RESOURCE, MAX_ENV_RESOURCES)
                .exponential(MIN_DT, MAX_DT)
                .exponential(MIN_MASS, MAX_MASS)
                .exponential(MIN_MASS, MAX_MASS)
                .exponential(MIN_EXCHANGE_RATE, MAX_EXCHANGE_RATE)
                .exponential(MIN_EXCHANGE_RATE, MAX_EXCHANGE_RATE)
                .generate();
    }

    @ParameterizedTest
    @MethodSource("arguments")
    void execute(double indResource1,
                 double indResource2,
                 double areaByMass,
                 double envResource1,
                 double envResource2,
                 double dt,
                 double molecularMass1,
                 double molecularMass2,
                 double exchangeRate1,
                 double exchangeRate2) {
        /*
        Given an exchange resource gene with
        medium target just over the individual resources
        And a min and max signal levels in a fixed range
        And relative exchange resource genes
         */
        final double midleResourceTarget1 = indResource1 * LITTLE_HIGH_SCALE;
        final double midleResourceTarget2 = indResource2 * LITTLE_HIGH_SCALE;
        final double minResourceTarget1 = midleResourceTarget1 * LOW_SCALE;
        final double minResourceTarget2 = midleResourceTarget2 * LOW_SCALE;
        final double maxResourceTarget1 = midleResourceTarget1 * HIGH_SCALE;
        final double maxResourceTarget2 = midleResourceTarget2 * HIGH_SCALE;
        final Matrix minLevels = of(
                minResourceTarget1, minResourceTarget2
        );
        final Matrix logsRates = of(
                log(maxResourceTarget1 / minResourceTarget1),
                log(maxResourceTarget2 / minResourceTarget2)
        );
        Matrix exchangeRates = of(
                exchangeRate1,
                exchangeRate2
        );
        final ExchangeResourceGene gene = new ExchangeResourceGene(
                minLevels, logsRates, exchangeRates);

        /*
         * And the quantities of individual resources for 3 individuals
         */
        final Matrix indResources = of(new double[][]{
                {indResource1, indResource1, indResource1},
                {indResource2, indResource2, indResource2}
        });

        /*
         And signal genes such as
           1st individual has the lower signals
           2nd individual has medium signals
           3rd individual has higher signals
         */
        final Matrix signals = of(new double[][]{
                {0, 0.5, 1},
                {0, 0.5, 1}
        });

        /*
         And population of the 3 individuals
         */
        final int[] locations = new int[NUM_INDIVIDUALS];
        final Species species = Species.create(0.0, 0, areaByMass, List.of(), List.of(), List.of(gene), List.of());
        final Population population = Population.create(indResources, List.of(), List.of(), List.of(signals), List.of(), locations, species);

        /* And quantities in the cells */
        final Matrix envResources = of(new double[][]{
                {envResource1, 0, 0, 0},
                {envResource2, 0, 0, 0}
        });

        /* And molecular masses */
        final Matrix molecularMasses = of(
                molecularMass1, molecularMass2
        );

        /* And areas by individual */
        final Matrix areasByIndividual = population.getIndividualSurface(molecularMasses)
                .trasposei().sumCols().trasposei();

        /* When executing */
        final Population result = gene.execute(population, signals, dt, envResources, areasByIndividual, molecularMasses);

        /* Then should result the same population */
        assertThat(result, sameInstance(population));

        /* And individuals with changed resources */
        final double dResource11 = min(minResourceTarget1 - indResource1, envResource1 / NO_INDIVIDUALS) * exchangeRate1 * dt;
        final double dResource21 = min(minResourceTarget2 - indResource2, envResource2 / NO_INDIVIDUALS) * exchangeRate2 * dt;

        final double dResource12 = min(midleResourceTarget1 - indResource1, envResource1 / NO_INDIVIDUALS) * exchangeRate1 * dt;
        final double dResource22 = min(midleResourceTarget2 - indResource2, envResource2 / NO_INDIVIDUALS) * exchangeRate2 * dt;

        final double dResource13 = min(maxResourceTarget1 - indResource1, envResource1 / NO_INDIVIDUALS) * exchangeRate1 * dt;
        final double dResource23 = min(maxResourceTarget2 - indResource2, envResource2 / NO_INDIVIDUALS) * exchangeRate2 * dt;

        assertThat(result.getResources(), matrixCloseTo(new double[][]{
                {indResource1 + dResource11, indResource1 + dResource12, indResource1 + dResource13},
                {indResource2 + dResource21, indResource2 + dResource22, indResource2 + dResource23}
        }));

        /* And environment with changed resources */
        assertThat(envResources, matrixCloseTo(new double[][]{
                {envResource1 - dResource11 - dResource12 - dResource13, 0, 0, 0},
                {envResource2 - dResource21 - dResource22 - dResource23, 0, 0, 0}
        }));
    }
}