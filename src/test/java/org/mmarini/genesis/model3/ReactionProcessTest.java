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

import static java.lang.Math.max;
import static java.lang.Math.min;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mmarini.genesis.model3.Matrix.of;
import static org.mmarini.genesis.model3.MatrixMatchers.matrixCloseTo;

class ReactionProcessTest {

    public static final double MIN_LEVEL = 1d;
    public static final double MIN_THRESHOLD = 0.1;
    public static final double MAX_THRESHOLD = 1d;
    public static final double MAX_SPEED = 10d;
    public static final double MIN_SPEED = 0.1;
    private static final double MAX_LEVEL = 200e3;
    private static final int RESOURCE_REF = 1;
    private static final double MIN_RESOURCE = 1;
    private static final double MAX_RESOURCE = 10;
    private static final double MIN_TARGET = 1;
    private static final double MAX_TARGET = 10;
    private static final double MIN_DT = 0.1;
    private static final double MAX_DT = 1;
    private static final double MIN_REAGENT = 1;
    private static final double MAX_REAGENT = 3;
    private static final double MIN_PRODUCT = 1;
    private static final double MAX_PRODUCT = 3;

    static Stream<Arguments> argsForComputeChanges() {
        return ArgumentGenerator.create(1234)
                .exponential(MIN_RESOURCE, MAX_RESOURCE)
                .exponential(MIN_RESOURCE, MAX_RESOURCE)
                .exponential(MIN_TARGET, MAX_TARGET)
                .exponential(MIN_DT, MAX_DT)
                .exponential(MIN_REAGENT, MAX_REAGENT)
                .exponential(MIN_PRODUCT, MAX_PRODUCT)
                .exponential(MIN_THRESHOLD, MAX_THRESHOLD)
                .exponential(MIN_SPEED, MAX_SPEED)
                .generate();
    }

    @ParameterizedTest
    @MethodSource("argsForComputeChanges")
    void computeChanges(double resource1, double resource2,
                        double target,
                        double dt,
                        double reagent, double product,
                        double threshold,
                        double speed) {
        /*
        Given a reaction process
         */
        Matrix reagents = of(reagent, 0);
        Matrix products = of(0, product);
        Matrix thresholds = of(threshold, 0);
        Matrix speeds = of(0, speed);
        Reaction reaction = Reaction.create(reagents, products, thresholds, speeds);
        ReactionProcess process = ReactionProcess.create(RESOURCE_REF, MIN_LEVEL, MAX_LEVEL, reaction);

        /*
        And resources by individual
        and target level by individuals
         */
        Matrix resources = Matrix.of(new double[][]{
                {resource1, resource1},
                {resource2, resource2}
        });
        Matrix targetLevels = Matrix.of(target, target).trasposei();
        /*
        And expected results
         */
        double maxByTarget = max(target - resource2, 0);
        double maxByThreshold = max(resource1 - threshold, 0) * product / reagent;
        double maxBySpeed = resource2 * speed * dt;
        double expected2 = min(maxBySpeed,
                min(maxByThreshold, maxByTarget));
        double expected1 = expected2 * reagent / product;

        /*
        When computeChanges
         */
        Matrix result = process.computeChanges(resources, targetLevels, dt);

        /*
         * Then should generate expected individual resource changes
         */
        assertThat(result, matrixCloseTo(new double[][]{
                {-expected1, -expected1},
                {expected2, expected2}
        }));
    }
}