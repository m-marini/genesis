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
import static org.mmarini.genesis.model3.MatrixMatchers.matrixCloseTo;

public class PhotoReactionProcessTest {
    public static final int REF = 1;
    private static final double REAGENT = 2;
    private static final double PRODUCT = 3;
    private static final double DISTRIBUTION1 = 0.5;
    private static final double DISTRIBUTION2 = 0.5;
    private static final double MIN_THRESHOLD = 0;
    private static final double MAX_THRESHOLD = 10;
    private static final double MIN_REAGENT_SPEED = 0.1;
    private static final double MAX_REAGENT_SPEED = 10;
    private static final double MIN_REAGENT = 1;
    private static final double MAX_REAGENT = 10;
    private static final double MIN_PRODUCT = 1;
    private static final double MAX_PRODUCT = 10;
    private static final double MIN_DT = 0.1;
    private static final double MAX_DT = 1;
    private static final double MIN_SPEED = 0.1;
    private static final double MAX_SPEED = 10;
    private static final double MIN_TARGET = 1;
    private static final double MAX_TARGET = 10;

    static Stream<Arguments> argsForComputeChanges() {
        return ArgumentGenerator.create(1234)
                .uniform(MIN_THRESHOLD, MAX_THRESHOLD) // reagentThreshold
                .exponential(MIN_REAGENT_SPEED, MAX_REAGENT_SPEED) // reagentSpeed
                .exponential(MIN_SPEED, MAX_SPEED) // speed
                .exponential(MIN_REAGENT, MAX_REAGENT) // resource1
                .exponential(MIN_PRODUCT, MAX_PRODUCT) // resource2
                .exponential(MIN_DT, MAX_DT) // dt
                .exponential(MIN_TARGET, MAX_TARGET) // resource2
                .generate();
    }

    @ParameterizedTest
    @MethodSource("argsForComputeChanges")
    void computeChanges(double reagentThreshold,
                        double reagentSpeed,
                        double speed,
                        double reagent,
                        double product,
                        double dt,
                        double target) {
        /*
        Given a photo reaction
         */
        Matrix reagents = Matrix.of(REAGENT, 0);
        Matrix products = Matrix.of(0, PRODUCT);
        Matrix thresholds = Matrix.of(reagentThreshold, 0);
        Matrix speeds = Matrix.of(reagentSpeed, 0);
        Reaction reaction = Reaction.create(reagents, products, thresholds, speeds);
        PhotoReactionProcess photoReactionProcess = PhotoReactionProcess.create(REF, speed, 0, 1, reaction);

        /*
        And resources for 2 individuals (2 x 2)
        And target for 2 individuals (1 x 2)
        And distribution for 2 individual (1 x 2)
         */
        Matrix resources = Matrix.of(new double[][]{
                        {reagent, reagent},
                        {product, product}
                }
        );
        Matrix targets = Matrix.of(target, target).trasposei();
        Matrix distributions = Matrix.of(DISTRIBUTION1, DISTRIBUTION2).trasposei();

        /*
        When computes changes
         */
        double maxByTarget = max(target - product, 0);
        double maxByFlux = speed * dt * DISTRIBUTION1;
        double maxByResource = max(reagent - reagentThreshold, 0) * PRODUCT / REAGENT;
        double maxBySpeed = reagent * reagentSpeed * dt;
        double expectedProduct = min(
                maxByFlux,
                min(maxByResource,
                        min(maxByTarget,
                                maxBySpeed)));
        double expectedReagent = expectedProduct * REAGENT / PRODUCT;
        Matrix delta = photoReactionProcess.computeChanges(resources, targets, dt, distributions);

        /*
        Then the changes should be ...
         */
        assertThat(delta, matrixCloseTo(new double[][]{
                {-expectedReagent, -expectedReagent},
                {expectedProduct, expectedProduct}
        }));
    }
}
