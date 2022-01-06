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

import static java.lang.Math.pow;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mmarini.genesis.model3.Matrix.of;
import static org.mmarini.genesis.model3.Matrix.zeros;
import static org.mmarini.genesis.model3.MatrixMatchers.matrixCloseTo;

class SimStatusIndividualsTest {

    static final int SEED = 1234;
    static final int NUM_RESOURCES = 1;
    static final int NUM_CELLS = 4;
    static final int LOCATION1 = 1;
    static final int LOCATION2 = 1;
    static final int LOCATION3 = 3;
    static final double MIN_RESOURCES = 0.1;
    static final double MAX_RESOURCES = 10;
    static final double MIN_MASS = 0.1;
    static final double MAX_MASS = 10;
    static final double MIN_FRACTAL_DIMENSION = 2;
    static final double MAX_FRACTAL_DIMENSION = 2.5;

    static Stream<Arguments> arguments() {
        return ArgumentGenerator.create(SEED)
                .exponential(MIN_RESOURCES, MAX_RESOURCES)
                .exponential(MIN_RESOURCES, MAX_RESOURCES)
                .exponential(MIN_RESOURCES, MAX_RESOURCES)
                .exponential(MIN_MASS, MAX_MASS)
                .exponential(MIN_FRACTAL_DIMENSION, MAX_FRACTAL_DIMENSION)
                .exponential(MIN_FRACTAL_DIMENSION, MAX_FRACTAL_DIMENSION)
                .generate();
    }

    @ParameterizedTest
    @MethodSource("arguments")
    void getTotalIndividualSurface(double qt1,
                                   double qt2,
                                   double qt3,
                                   double mass,
                                   double dim1,
                                   double dim2
    ) {
        /*
         * Given a simulation status with 3 individuals in 4 cells
         * an individual of a species in cell 1
         * an individual of another species in cell 1
         * an individual of last species in cell 3
         */
        Matrix qties1 = of(qt1);
        int[] locations1 = new int[]{LOCATION1};
        Species species1 = Species.create(0, 0, dim1, List.of(), List.of(), List.of(), List.of());
        Population pop1 = new Population(qties1, List.of(), List.of(), List.of(), List.of(), locations1, species1);
        Matrix qties2 = of(new double[][]{
                {qt2, qt3}
        });
        int[] locations2 = new int[]{LOCATION2, LOCATION3};
        Species species2 = new Species(0, 0, dim2, List.of(), List.of(), List.of(), List.of());
        Population pop2 = new Population(qties2, List.of(), List.of(), List.of(), List.of(), locations2, species2);
        List<Population> populations = List.of(pop1, pop2);
        Matrix quantities = zeros(NUM_RESOURCES, NUM_CELLS);
        SimStatus status = new SimStatus(0, quantities, populations);
        Matrix masses = of(mass);

        /*
         * When getTotalIndividualSurface
         */
        Matrix result = status.getTotalIndividualSurface(NUM_CELLS, masses);

        /*
         * Then should return total individual surface
         */
        assertThat(result, matrixCloseTo(new double[][]{
                {0, pow(qt1 * mass, dim1 / 3) + pow(qt2 * mass, dim2 / 3), 0, pow(qt3 * mass, dim2 / 3)}
        }));
    }
}
