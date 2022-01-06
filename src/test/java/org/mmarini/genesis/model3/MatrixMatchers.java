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

import org.ejml.dense.row.MatrixFeatures_DDRM;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;

import static org.mmarini.genesis.model3.Matrix.of;

public class MatrixMatchers {
    private static final double EPSILON = 1e-6;

    public static Matcher<Matrix> matrixCloseTo(Matrix expected, double epsilon) {
        return new BaseMatcher<>() {
            @Override
            public void describeTo(Description description) {
                description.appendValue(expected);
            }

            @Override
            public boolean matches(Object o) {
                if (!(o instanceof Matrix)) {
                    return false;
                }
                return MatrixFeatures_DDRM.isIdentical(((DDRMWrapper) o).get(), ((DDRMWrapper) expected).get(), epsilon);
            }
        };
    }

    public static Matcher<Matrix> matrixCloseTo(Matrix expected) {
        return matrixCloseTo(expected, EPSILON);
    }

    public static Matcher<Matrix> matrixCloseTo(double[][] expected, double epsilon) {
        return matrixCloseTo(of(expected), epsilon);
    }

    public static Matcher<Matrix> matrixCloseTo(double[] expected, double epsilon) {
        return matrixCloseTo(of(expected), epsilon);
    }

    public static Matcher<Matrix> matrixCloseTo(double[]... expected) {
        return matrixCloseTo(of(expected), EPSILON);
    }

    public static Matcher<Matrix> matrixCloseTo(double... expected) {
        return matrixCloseTo(of(expected), EPSILON);
    }
}
