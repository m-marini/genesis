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
import static org.hamcrest.Matchers.notNullValue;
import static org.mmarini.genesis.model3.Matrix.values;
import static org.mmarini.genesis.model3.MatrixMatchers.matrixCloseTo;

class SimEngineDiffuseTest {

    static final double MIN_VALUE = 5;
    static final int MAX_VALUE = 15;
    static final long SEED = 1234;
    static final double MIN_ALPHA = 0.2;
    static final double MAX_ALPHA = 0.4;
    static final double MIN_LENGTH = 1;
    static final double MAX_LENGTH = 3;
    static final double MIN_DT = 0.1;
    static final double MAX_DT = 0.2;

    static Stream<Arguments> forDifferential() {
        return ArgumentGenerator.create(SEED)
                .exponential(MIN_VALUE, MAX_VALUE)
                .exponential(MIN_VALUE, MAX_VALUE)
                .exponential(MIN_ALPHA, MAX_ALPHA) // alpha
                .exponential(MIN_LENGTH, MAX_LENGTH) // length
                .exponential(MIN_DT, MAX_DT) // dt
                .generate();
    }

    @ParameterizedTest
    @MethodSource("forDifferential")
    void differential(double value, double value1, double alpha, double length, double dt) {
        Topology3 top = Topology3.create(4, 2, length);
        Matrix alphaH = values(2, top.getNoCells(), alpha);

        Matrix c = values(2, top.getNoCells(), value)
                .set(0, 0, value1);

        Matrix diff = SimEngine.differential(c, dt, alphaH, top);

        assertThat(diff, notNullValue());
        double d0 = (value - value1) * dt / length * alpha;
        double d134 = -d0 / 3;

        assertThat(diff, matrixCloseTo(new double[][]{
                {d0, d134, 0, d134, d134, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0}
        }));
    }
}