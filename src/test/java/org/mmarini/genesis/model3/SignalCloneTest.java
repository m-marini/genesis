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

import static java.lang.Math.max;
import static java.lang.Math.min;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.mmarini.genesis.model3.Matrix.of;
import static org.mmarini.genesis.model3.MatrixMatchers.matrixCloseTo;

class SignalCloneTest {

    static final double MIN_SIGMA = 0.1;
    static final double MAX_SIGMA = 0.5;
    static final double MIN_PROB = 1e-3;
    static final long SEED = 1234;
    static final double MAX_PROB = 1 - MIN_PROB;
    static final double MIN_SIGNAL = 0;
    static final double MAX_SIGNAL = 1;
    static final double CHANGE_SIGMA = 0.1;

    static Stream<Arguments> arguments1() {
        return ArgumentGenerator.create(SEED)
                .exponential(MIN_PROB, MAX_PROB)
                .exponential(MIN_SIGMA, MAX_SIGMA)
                .uniform(MIN_SIGNAL, MAX_SIGNAL)
                .uniform(MIN_SIGNAL, MAX_SIGNAL)
                .generate();
    }

    static Stream<Arguments> arguments2() {
        return ArgumentGenerator.create(SEED)
                .exponential(MIN_PROB, MAX_PROB)
                .exponential(MIN_SIGMA, MAX_SIGMA)
                .uniform(MIN_SIGNAL, MAX_SIGNAL)
                .uniform(MIN_SIGNAL, MAX_SIGNAL)
                .gaussian(0, CHANGE_SIGMA)
                .generate();
    }

    @ParameterizedTest
    @MethodSource("arguments1")
    void testClone(double prob, double sigma, double signal1, double signal2) {
        /*
        Given a gene signals
         */
        final Matrix matrix = of(new double[][]{
                {signal1, signal2},
                {signal2, signal1}
        });

        /*
        And a generator that creates full mutation probability
        and null mutation changes
         */
        final Random random = new MockRandomBuilder()
                .nextDouble(0)
                .nextDouble(0)
                .nextDouble(0)
                .nextDouble(0)
                .nextDouble(0)
                .nextDouble(0)
                .nextDouble(0)
                .nextDouble(0)
                .build();

        /*
        When clone the signals
         */
        final Matrix result = SignalClone.clone(matrix, prob, sigma, random);

        /*
        Then the clone should be the same of original
         */
        assertThat(result, matrixCloseTo(new double[][]{
                {signal1, signal2},
                {signal2, signal1}
        }));
    }

    @ParameterizedTest
    @MethodSource("arguments2")
    void testClone1(double prob, double sigma, double signal1, double signal2, double change) {
        /*
        Given a gene signals
         */
        final Matrix matrix = of(new double[][]{
                {signal1, signal2},
                {signal2, signal1}
        });

        /*
        And a generator that creates no mutation probability
        and a mutation changes
         */
        final Random random = new MockRandomBuilder()
                .nextDouble(prob)
                .nextDouble(prob)
                .nextDouble(prob)
                .nextDouble(prob)
                .nextDouble(change / sigma)
                .nextDouble(change / sigma)
                .nextDouble(change / sigma)
                .nextDouble(change / sigma)
                .build();

        /*
        When clone the signals
         */
        final Matrix result = SignalClone.clone(matrix, prob, sigma, random);

        /*
        Then the clone should be the same of original
         */
        assertThat(result, matrixCloseTo(new double[][]{
                {signal1, signal2},
                {signal2, signal1}
        }));
    }

    @ParameterizedTest
    @MethodSource("arguments2")
    void testClone2(double prob, double sigma, double signal1, double signal2, double change) {
        /*
        Given a gene signals
         */
        final Matrix matrix = of(new double[][]{
                {signal1, signal2},
                {signal2, signal1}
        });

        /*
        And a generator that creates no mutation probability
        and a mutation changes
         */
        final Random random = new MockRandomBuilder()
                .nextDouble(0)
                .nextDouble(0)
                .nextDouble(0)
                .nextDouble(0)
                .nextDouble(change / sigma)
                .nextDouble(change / sigma)
                .nextDouble(change / sigma)
                .nextDouble(change / sigma)
                .build();

        /*
        When clone the signals
         */
        final Matrix result = SignalClone.clone(matrix, prob, sigma, random);

        /*
        Then the clone should have changed signals
         */
        final double s1 = max(0, min(signal1 + change, 1));
        final double s2 = max(0, min(signal2 + change, 1));
        assertThat(result, matrixCloseTo(new double[][]{
                {s1, s2},
                {s2, s1}
        }));
    }

    @ParameterizedTest
    @MethodSource("arguments2")
    void testCloneMutations(double prob, double sigma, double signal1, double signal2, double change) {
         /*
        Given a list of 2 signal genes
         */
        final Matrix matrix = of(new double[][]{
                {signal1, signal2},
                {signal2, signal1}
        });
        final List<Matrix> signals = List.of(matrix, matrix);

        /*
        And a generator that creates no mutation probability
        and a mutation changes
         */
        final Random random = new MockRandomBuilder()
                .nextDouble(0, 0)
                .nextDouble(0, 0)
                .nextDouble(change / sigma, change / sigma)
                .nextDouble(change / sigma, change / sigma)
                .nextDouble(0, 0)
                .nextDouble(0, 0)
                .nextDouble(change / sigma, change / sigma)
                .nextDouble(change / sigma, change / sigma)
                .build();

        /*
        When clone the signals
         */
        final List<Matrix> result = SignalClone.clone(signals, prob, sigma, random, 0, 1);

        /*
        Then the clone should have changed signals
         */
        final double s1 = max(0, min(signal1 + change, 1));
        final double s2 = max(0, min(signal2 + change, 1));
        assertThat(result, hasSize(2));
        assertThat(result.get(0), matrixCloseTo(new double[][]{
                {signal1, signal2, s1, s2},
                {signal2, signal1, s2, s1}
        }));
        assertThat(result.get(1), matrixCloseTo(new double[][]{
                {signal1, signal2, s1, s2},
                {signal2, signal1, s2, s1}
        }));
    }

    @ParameterizedTest
    @MethodSource("arguments2")
    void testCloneNoMutations(double prob, double sigma, double signal1, double signal2, double change) {
        /*
        Given a gene signals
         */
        /*
        Given a list of 2 signal genes
         */
        final Matrix matrix = of(new double[][]{
                {signal1, signal2},
                {signal2, signal1}
        });
        final List<Matrix> signals = List.of(matrix, matrix);

        /*
        And a generator that creates no mutation probability
        and a mutation changes
         */
        final Random random = new MockRandomBuilder()
                .nextDouble(prob, prob)
                .nextDouble(prob, prob)
                .nextDouble(change / sigma, change / sigma)
                .nextDouble(change / sigma, change / sigma)
                .nextDouble(prob, prob)
                .nextDouble(prob, prob)
                .nextDouble(change / sigma, change / sigma)
                .nextDouble(change / sigma, change / sigma)
                .build();

        /*
        When clone the signals
         */
        final List<Matrix> result = SignalClone.clone(signals, prob, sigma, random, 0, 1);

        /*
        Then the clone should be the same of original
         */
        assertThat(result, hasSize(2));
        assertThat(result.get(0), matrixCloseTo(new double[][]{
                {signal1, signal2, signal1, signal2},
                {signal2, signal1, signal2, signal1}
        }));
        assertThat(result.get(1), matrixCloseTo(new double[][]{
                {signal1, signal2, signal1, signal2},
                {signal2, signal1, signal2, signal1}
        }));
    }

    @ParameterizedTest
    @MethodSource("arguments1")
    void testCloneNochanges(double prob, double sigma, double signal1, double signal2) {
        /*
        Given a list of 2 signal genes
         */
        final Matrix matrix = of(new double[][]{
                {signal1, signal2},
                {signal2, signal1}
        });
        final List<Matrix> signals = List.of(matrix, matrix);

        /*
        And a generator that creates full mutation probability
        and null mutation changes
         */
        final Random random = new MockRandomBuilder()
                // mutations (all trues)
                .nextDouble(0, 0)
                .nextDouble(0, 0)
                // changes
                .nextDouble(0, 0)
                .nextDouble(0, 0)
                // mutations (all trues)
                .nextDouble(0, 0)
                .nextDouble(0, 0)
                // changes
                .nextDouble(0, 0)
                .nextDouble(0, 0)
                .build();

        /*
        When clone the signals
         */
        final List<Matrix> result = SignalClone.clone(signals, prob, sigma, random, 0, 1);

        /*
        Then the clone should be the same of original
         */
        assertThat(result, hasSize(2));
        assertThat(result.get(0), matrixCloseTo(new double[][]{
                {signal1, signal2, signal1, signal2},
                {signal2, signal1, signal2, signal1}
        }));
        assertThat(result.get(1), matrixCloseTo(new double[][]{
                {signal1, signal2, signal1, signal2},
                {signal2, signal1, signal2, signal1}
        }));
    }
}