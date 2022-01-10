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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.stream.Stream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mmarini.genesis.model3.Matrix.of;
import static org.mmarini.genesis.model3.MatrixMatchers.matrixCloseTo;


class PopulationTest {
    static final double MIN_RANDOM_MASS = 1;
    static final double MAX_RANDOM_MASS = 10.0;
    static final double MIN_QUANTITY = 1.0;
    static final double MAX_QUANTITY = 11.0;
    static final int ENERGY_ROW = 1;
    static final double Q1 = 1.0;
    static final double Q2 = 2.0;
    static final double ERG1 = 3.0;
    static final double ERG2 = 4.0;
    static final double CONSUMPTION_SPEED = 0.0;
    static final double MIN_MASS = 0.1;
    static final double DT = 0.1;
    static final double SURVIVE_MASS = 0.1;
    static final int NUM_TESTS = 10;
    static final Matrix QUANTITIES = of(new double[][]{
            {Q1, Q2},
            {ERG1, ERG2}
    });
    static final long SEED = 1234;
    static final double AREAS_BY_MASS = 2.3;

    static Population population(Matrix qties) {
        int[] locations = new int[qties.getNumCols()];
        List<IPGene> genes = List.of();
        Species species = new Species(CONSUMPTION_SPEED, SURVIVE_MASS, AREAS_BY_MASS, List.of(), List.of(), List.of(), List.of());
        return new Population(qties,  List.of(), List.of(), List.of(), List.of(), locations, species);
    }

    static Stream<Arguments> q1q2m1() {
        return ArgumentGenerator.create(NUM_TESTS)
                .exponential(MIN_QUANTITY, MAX_QUANTITY)
                .exponential(MIN_QUANTITY, MAX_QUANTITY)
                .exponential(MIN_RANDOM_MASS, MAX_RANDOM_MASS)
                .generate();
    }

    static Stream<Arguments> qtieAndMasses() {
        return ArgumentGenerator.create(SEED)
                .exponential(MIN_QUANTITY, MAX_QUANTITY)
                .exponential(MIN_QUANTITY, MAX_QUANTITY)
                .exponential(MIN_QUANTITY, MAX_QUANTITY)
                .exponential(MIN_QUANTITY, MAX_QUANTITY)
                .exponential(MIN_QUANTITY, MAX_QUANTITY)
                .exponential(MIN_QUANTITY, MAX_QUANTITY)
                .generate();
    }

    private Population population;

    @ParameterizedTest
    @MethodSource("qtieAndMasses")
    void getMasses(double q11, double q12, double q21, double q22, double m1, double m2) {

        Matrix qties = of(new double[][]{
                {q11, q12},
                {q21, q22}
        });
        Matrix masses = of(m1, m2);

        // Given a population with individual quantities
        // And molecular mass
        population = population(qties);

        // When get masses
        Matrix m = population.getMasses(masses);

        // Then should return the sum of mass of resource per individual
        assertThat(m, matrixCloseTo(new double[][]{{
                qties.get(0, 0) * masses.get(0, 0)
                        + qties.get(ENERGY_ROW, 0) * masses.get(ENERGY_ROW, 0),
                qties.get(0, ENERGY_ROW) * masses.get(0, 0)
                        + qties.get(ENERGY_ROW, ENERGY_ROW) * masses.get(ENERGY_ROW, 0)
        }
        }));
    }

    @BeforeEach
    void init() {
        population = population(QUANTITIES);
    }

    @ParameterizedTest
    @MethodSource("q1q2m1")
    void maintain(double q1, double q2, double m1) {
        // Given a population with individual quantities insufficient for individual 0
        double e1 = q1 * m1 * CONSUMPTION_SPEED * DT;
        double e2 = q2 * m1 * CONSUMPTION_SPEED * DT;
        population = population(of(new double[][]{
                {q1, q2},
                {e1 * 0.9, e2 * 1.1}
        }));
        // And molecular mass
        Matrix masses = of(m1, 0);

        // When maintain
        Population result = population.maintain(DT, ENERGY_ROW, masses);

        // Then should reduce energy
        assertThat(result, sameInstance(population));
        assertThat(result.getResources(), matrixCloseTo(new double[][]{
                {q1, q2},
                {0, e2 * MIN_MASS}
        }));
    }

    @ParameterizedTest
    @MethodSource("q1q2m1")
    void survive(double q1, double q2, double m1) {
        // Given a population with individual quantities insufficient for individual 0
        population = population(of(new double[][]{
                {q1, MIN_MASS / m1 * 0.9, MIN_MASS / m1 + 1e-3},
                {0, 1, 1}
        }));
        // And molecular mass
        Matrix masses = of(m1, 0);
        // And environment quantities
        Matrix qties = of(new double[][]{
                {1, 2, 3},
                {0, 0, 0}
        });

        // When survive
        Population result = population.survive(ENERGY_ROW, masses, qties);

        // Then should reduce energy
        assertThat(result, not(sameInstance(population)));
        assertThat(result.getResources(), matrixCloseTo(new double[][]{
                {MIN_MASS / m1 + 1e-3},
                {1}
        }));
        assertThat(result.getLocations().length, equalTo(1));
        //assertThat(result.getIpGenes(), hasSize(1));
        assertThat(qties, matrixCloseTo(new double[][]{
                {1 + q1 + MIN_MASS / m1 * 0.9, 2, 3},
                {1, 0, 0}
        }));
    }

}