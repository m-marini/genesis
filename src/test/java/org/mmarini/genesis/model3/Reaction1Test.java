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

import static java.lang.Math.min;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mmarini.genesis.model3.MatrixMatchers.matrixCloseTo;

class Reaction1Test {
    public static final int REF = 1;
    private static final double REAGENT = 2;
    private static final double PRODUCT = 3;
    private static final double MIN_THRESHOLD = 0;
    private static final double MAX_THRESHOLD = 10;
    private static final double MIN_SPEED = 0.1;
    private static final double MAX_SPEED = 10;
    private static final double MIN_RESOURCE1 = 1;
    private static final double MAX_RESOURCE1 = 10;
    private static final double MIN_RESOURCE2 = 1;
    private static final double MAX_RESOURCE2 = 10;
    private static final double MIN_LIMIT = 0.1;
    private static final double MAX_LIMIT = 10;
    private static final double MIN_DT = 0.1;
    private static final double MAX_DT = 1;

    static Stream<Arguments> argsForMax() {
        return ArgumentGenerator.create(1234)
                .uniform(MIN_THRESHOLD, MAX_THRESHOLD) // reagentThreshold
                .exponential(MIN_SPEED, MAX_SPEED) // reagentSpeed
                .exponential(MIN_RESOURCE1, MAX_RESOURCE1) // resource1
                .exponential(MIN_RESOURCE2, MAX_RESOURCE2) // resource2
                .exponential(MIN_LIMIT, MAX_LIMIT) // resource limit
                .exponential(MIN_DT, MAX_DT) // dt
                .generate();
    }

    @ParameterizedTest
    @MethodSource("argsForMax")
    void max(double reagentThreshold,
             double reagentSpeed,
             double resource1,
             double resource2,
             double maxResource1,
             double dt) {
        /*
        Given a reaction
         */
        Matrix reagents = Matrix.of(REAGENT, 0);
        Matrix products = Matrix.of(0, PRODUCT);
        Matrix thresholds = Matrix.of(reagentThreshold);
        Matrix speeds = Matrix.of(reagentSpeed);
        Reaction reaction = Reaction.create(reagents, products, thresholds, speeds);

        /*
        And 2 individual resources
        and a reference resource limits
         */
        Matrix resources = Matrix.of(new double[][]{
                {resource1, resource1},
                {resource2, resource2}
        });
        Matrix limits = Matrix.of(maxResource1, maxResource1).trasposei();

        /*
         * When computes max reaction reference resource
         */
        Matrix result = reaction.max(REF, resources, limits, dt);

        /*
        Then the result should be the minimum
        between the limit resource, resource1 * speed * dt and
        resource1 - threshold
         */
        double expectedBySpeed = resource1 * reagentSpeed * dt;
        double expectedByAvailability = (resource1 - reagentThreshold) * PRODUCT / REAGENT;
        double expected1 = Math.max(
                min(
                        min(expectedByAvailability, expectedBySpeed),
                        maxResource1),
                0);
        assertThat(result, matrixCloseTo(new double[][]{
                {expected1, expected1}
        }));
    }

}