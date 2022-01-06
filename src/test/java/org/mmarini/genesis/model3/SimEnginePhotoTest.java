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
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mmarini.genesis.model3.MatrixMatchers.matrixCloseTo;

class SimEnginePhotoTest {

    static final double FRACTAL_DIMENSION = 2;
    static final int WIDTH = 2;
    static final int HEIGHT = 2;
    static final int NUM_RESOURCES = 2;
    static final int NUM_INDIVIDUALS = 2;
    static final int ENERGY_REF = 1;
    static final double LENGTH = 2;
    static final int NUM_CELLS = WIDTH * HEIGHT;
    static final double MIN_MASS = 1;
    static final double MAX_MASS = 4;
    static final double MIN_RESOURCE = 0.1;
    static final double MAX_RESOURCE = 10;
    static final double MIN_TARGET = 1;
    static final double MAX_TARGET = 10;
    static final double MIN_SPEED = 0.2;
    static final double MAX_SPEED = 2;
    static final double MIN_THRESHOLD = 0.1;
    static final double MAX_THRESHOLD = 2;
    static final double MIN_REACTION_SPEED = 0.1;
    static final double MAX_REACTION_SPEED = 2;
    static final double MIN_DT = 0.1;
    static final double MAX_DT = 1;
    static final double MIN_REAGENT = 1;
    static final double MAX_REAGENT = 3;
    static final double MIN_PRODUCT = 1;
    static final double MAX_PRODUCT = 3;

    static Stream<Arguments> argsForPhoto() {
        return ArgumentGenerator.create(1234)
                .exponential(MIN_MASS, MAX_MASS)
                .exponential(MIN_MASS, MAX_MASS)
                .exponential(MIN_RESOURCE, MAX_RESOURCE)
                .exponential(MIN_RESOURCE, MAX_RESOURCE)
                .exponential(MIN_TARGET, MAX_TARGET)
                .exponential(MIN_SPEED, MAX_SPEED)
                .exponential(MIN_THRESHOLD, MAX_THRESHOLD)
                .exponential(MIN_REACTION_SPEED, MAX_REACTION_SPEED)
                .exponential(MIN_DT, MAX_DT)
                .exponential(MIN_REAGENT, MAX_REAGENT)
                .exponential(MIN_PRODUCT, MAX_PRODUCT)
                .generate()
                ;
    }

    @ParameterizedTest
    @MethodSource("argsForPhoto")
    void photo(double mass1, double mass2,
               double resource11, double resource12,
               double target,
               double speed,
               double threshold,
               double reactionSpeed,
               double dt,
               double reagent,
               double product) {
        /*
        Given a simulation engine
         */
        Matrix masses = Matrix.of(mass1, mass2);
        Topology topology = Topology3.create(WIDTH, HEIGHT, LENGTH);
        SimEngine engine = SimEngine.create(masses, topology, Matrix.zeros(2, 1), 1);
        /*
        And a status with 2 populations
         */
        Matrix resources = Matrix.ones(NUM_RESOURCES, NUM_CELLS);
        Matrix indResources = Matrix.of(new double[][]{
                {resource11, resource11},
                {resource12, resource12}
        });

        List<Matrix> targetLevels = List.of(Matrix.of(target, target).trasposei());
        int[] location = new int[NUM_INDIVIDUALS];
        Matrix reagents = Matrix.of(reagent, 0);
        Matrix products = Matrix.of(0, product);
        Matrix thresholds = Matrix.of(threshold, 0);
        Matrix speeds = Matrix.of(reactionSpeed, 0);
        Reaction reaction = Reaction.create(reagents, products, thresholds, speeds);
        List<? extends PhotoProcess> photoProcesses = List.of(PhotoProcess.create(ENERGY_REF, speed, 1, 2, reaction));
        Species species = Species.create(0, 0, FRACTAL_DIMENSION, photoProcesses, List.of(), List.of(), List.of());
        Population population = Population.create(indResources, targetLevels, List.of(), List.of(), List.of(), location, species);

        List<Population> populations = List.of(population);
        SimStatus status = SimStatus.create(0, resources, populations);

        /*
        When process photo
         */
        double maxBySpeed = speed * dt * 0.5;
        double maxByTarget = max(target - resource12, 0);
        double maxByReagent = max(resource11 - threshold, 0) * product / reagent;
        double maxByReaction = resource11 * reactionSpeed * dt;
        double delta2 = min(maxBySpeed,
                min(maxByTarget,
                        min(maxByReaction, maxByReagent))
        );
        double delta1 = delta2 * reagent / product;
        double expected1 = resource11 - delta1;
        double expected2 = resource12 + delta2;
        SimStatus result = engine.processPhoto(status, dt);

        /*
        Then ...
         */
        assertNotNull(result);
        assertThat(result.getPopulations(), hasSize(1));
        assertThat(result.getPopulations().get(0).getResources(), matrixCloseTo(new double[][]{
                {expected1, expected1},
                {expected2, expected2}
        }));

    }
}