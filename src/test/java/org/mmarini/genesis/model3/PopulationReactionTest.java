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

import static java.lang.Math.max;
import static java.lang.Math.min;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.sameInstance;
import static org.mmarini.genesis.model3.MatrixMatchers.matrixCloseTo;

class PopulationReactionTest {
    public static final double REACTION_SPEED = 1d;
    public static final int REAGENT = 1;
    public static final int PRODUCT = 1;
    static final double MIN_SURFACE = 0.1;
    static final int SEED = 1234;
    static final int NUM_CELLS = 4;
    static final double MIN_RESOURCES = 1;
    static final double MAX_RESOURCES = 10.0;
    static final double MAX_SURFACE = 10;
    static final double MASS1 = 1;
    static final double MASS2 = 1;
    static final int LOCATION1 = 0;
    static final int LOCATION2 = 0;
    static final int RESOURCE2 = 1;
    static final double THRESHOLD = 0.1;
    static final double MIN_DT = 0.1;
    static final double MAX_DT = 1;
    static final double MIN_TARGET = 1;
    private static final double MAX_TARGET = 5;

    static Stream<Arguments> argsForPhoto() {
        return ArgumentGenerator.create(SEED)
                .exponential(MIN_RESOURCES, MAX_RESOURCES)
                .exponential(MIN_RESOURCES, MAX_RESOURCES)
                .exponential(MIN_SURFACE, MAX_SURFACE)
                .exponential(MIN_DT, MAX_DT)
                .exponential(MIN_TARGET, MAX_TARGET)
                .generate();
    }

    @ParameterizedTest
    @MethodSource("argsForPhoto")
    void processPhoto1(double resource1,
                       double resource2,
                       double areasByMass,
                       double dt,
                       double target) {
        // Given a population with reaction process transforming resource1 to resource2
        Matrix resources = Matrix.of(new double[][]{
                {resource1, resource1},
                {resource2, resource2}
        });
        List<Matrix> targetLevels = List.of(Matrix.of(target, target).trasposei());
        int[] locations = new int[]{LOCATION1, LOCATION2};
        Reaction reaction = Reaction.create(
                Matrix.of(REAGENT, 0),
                Matrix.of(0, PRODUCT),
                Matrix.of(THRESHOLD, 0),
                Matrix.of(REACTION_SPEED, 0)
        );
        ReactionProcess reactionProcess = ReactionProcess.create(RESOURCE2, 1, 2, reaction);
        List<? extends ReactionProcess> photoProcesses = List.of(reactionProcess);
        Species species = Species.create(0d, 0d, areasByMass, List.of(), photoProcesses, List.of(), List.of());
        Population population = Population.create(
                resources,
                List.of(),
                targetLevels,
                List.of(),
                List.of(),
                locations,
                species
        );
        /*
        When process photo reaction
         */
        double limitedByTarget = max(target - resource2, 0);
        double limitedByReagents = max(resource1 - THRESHOLD, 0) * PRODUCT / REAGENT;
        double limitedBySpeed = resource1 * REACTION_SPEED * dt;
        double delta2 = min(limitedByReagents,
                min(limitedBySpeed, limitedByTarget));
        double delta1 = delta2 * REAGENT / PRODUCT;

        double expected1 = resource1 - delta1;
        double expected2 = resource2 + delta2;
        Population result = population.processReactions(dt);

        // Then should return the sum of mass of resource per individual
        assertThat(result, sameInstance(population));
        assertThat(result.getResources(), matrixCloseTo(new double[][]{
                {expected1, expected1},
                {expected2, expected2}
        }));
    }
}