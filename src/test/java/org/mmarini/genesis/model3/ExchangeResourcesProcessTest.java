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

import java.util.stream.Stream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mmarini.genesis.model3.MatrixMatchers.matrixCloseTo;

public class ExchangeResourcesProcessTest {
    static final double MAX_TARGET = 10;
    static final double MIN_LEVEL = 0.1;
    static final double MAX_LEVEL = 10;
    static final int NO_RESOURCES = 2;
    static final int NO_CELLS = 4;
    static final int NO_INDIVIDUALS = 2;
    static final int LOCATION = 1;
    static final double MIN_TARGET = 1;
    static final double MIN_RATE = 0;
    static final double MAX_RATE = 1;
    static final double MIN_RESOURCE = 0.1;
    static final double MAX_RESOURCE = 10;
    static final double DISTRIBUTION1 = 0.5;
    static final double DISTRIBUTION2 = 0.5;
    static final double MIN_DT = 0.1;
    static final double MAX_DT = 1;

    static Stream<Arguments> argsForComputeChanges() {
        return ArgumentGenerator.create(1234)
                .uniform(MIN_RATE, MAX_RATE)
                .uniform(MIN_RATE, MAX_RATE)
                .exponential(MIN_RESOURCE, MAX_RESOURCE) // speed
                .exponential(MIN_RESOURCE, MAX_RESOURCE) // speed
                .exponential(MIN_TARGET, MAX_TARGET) // resource2
                .exponential(MIN_DT, MAX_DT) // dt
                .generate();
    }

    @ParameterizedTest
    @MethodSource("argsForComputeChanges")
    void computeChanges(double rate1, double rate2,
                        double resource, double individual,
                        double target, double dt) {
        /*
        Given an exchange resource process
         */
        Matrix minLevels = Matrix.of(MIN_LEVEL, MIN_LEVEL);
        Matrix maxLevels = Matrix.of(MAX_LEVEL, MAX_LEVEL);
        Matrix rates = Matrix.of(rate1, rate2);
        ExchangeResourcesProcess process = ExchangeResourcesProcess.create(minLevels, maxLevels, rates);

        /*
        And resources for 4 cells (2 x 4)
        And resources for 2 individuals (2 x 2)
        And location of 2 individuals
        And target for 2 individuals (2 x 2)
        And distribution for 2 individual (1 x 2)
         */
        Matrix resources = Matrix.values(NO_RESOURCES, NO_CELLS, resource);
        Matrix individuals = Matrix.values(NO_RESOURCES, NO_INDIVIDUALS, individual);

        int[] locations = new int[]{LOCATION, LOCATION};
        Matrix targetLevels = Matrix.values(NO_RESOURCES, NO_INDIVIDUALS, target);
        Matrix distributions = Matrix.of(DISTRIBUTION1, DISTRIBUTION2).trasposei();

        /*
        When computes changes
         */
        double expectedDeltaInd = 0;
        double expectedDeltaEnv = -expectedDeltaInd * 2;
        ExchangeResourcesProcess.ExchangeResources result = process.computeChanges(resources, individuals, locations, targetLevels, dt, distributions);

        /*
        Then the changes should be ...
         */
        assertNotNull(result);
        assertThat(result.environmentResources, matrixCloseTo(new double[][]{
                {0, expectedDeltaEnv, 0, 0},
                {0, expectedDeltaEnv, 0, 0},
        }));
        assertThat(result.individualResources, matrixCloseTo(new double[][]{
                {expectedDeltaInd, expectedDeltaInd},
                {expectedDeltaInd, expectedDeltaInd},
        }));
    }
}
