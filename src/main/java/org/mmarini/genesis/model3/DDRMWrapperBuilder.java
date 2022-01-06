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

import org.ejml.data.DMatrixRMaj;
import org.ejml.dense.row.RandomMatrices_DDRM;

import java.util.Arrays;
import java.util.Random;
import java.util.stream.IntStream;

/**
 *
 */
public class DDRMWrapperBuilder implements MatrixBuilder {

    @Override
    public Matrix hstack(Matrix... matrices) {
        if (matrices.length == 0) {
            return zeros(0, 0);
        }
        final int n = matrices[0].getNumRows();
        for (Matrix mat : matrices) {
            assert n == ((DDRMWrapper) mat).getMatrix().getNumRows();
        }
        final int m = Arrays.stream(matrices)
                .mapToInt(Matrix::getNumCols)
                .sum();
        final Matrix result = zeros(n, m);
        int cols = 0;
        for (Matrix mat : matrices) {
            result.assignCols(mat, IntStream.range(cols, cols + mat.getNumCols()).toArray());
            cols += mat.getNumCols();
        }
        return result;
    }

    @Override
    public Matrix of(double[]... matrix) {
        return new DDRMWrapper(new DMatrixRMaj(matrix));
    }

    @Override
    public Matrix of(double matrix) {
        return new DDRMWrapper(new DMatrixRMaj(new double[][]{{matrix}}));
    }

    @Override
    public Matrix of(double... vector) {
        return new DDRMWrapper(new DMatrixRMaj(vector));
    }

    @Override
    public Matrix ones(int numRows, int numCols) {
        return values(numRows, numCols, 1);
    }

    @Override
    public Matrix rand(int numRows, int numCols, Random random) {
        return new DDRMWrapper(RandomMatrices_DDRM.rectangle(numRows, numCols, random));
    }

    @Override
    public Matrix randn(int numRows, int numCols, Random random) {
        return new DDRMWrapper(RandomMatrices_DDRM.rectangleGaussian(numRows, numCols, 0, 1, random));
    }

    @Override
    public Matrix values(int numRows, int numCols, double value) {
        DMatrixRMaj m = new DMatrixRMaj(numRows, numCols);
        m.fill(value);
        return new DDRMWrapper(m);
    }

    @Override
    public Matrix vstack(Matrix[] matrices) {
        if (matrices.length == 0) {
            return zeros(0, 0);
        }
        final int m = matrices[0].getNumCols();
        for (Matrix mat : matrices) {
            assert m == ((DDRMWrapper) mat).getMatrix().getNumCols();
        }
        final int n = Arrays.stream(matrices)
                .mapToInt(Matrix::getNumRows)
                .sum();
        final Matrix result = zeros(n, m);
        int rows = 0;
        for (Matrix mat : matrices) {
            result.assignRows(mat, IntStream.range(rows, rows + mat.getNumRows()).toArray());
            rows += mat.getNumRows();
        }
        return result;

    }

    @Override
    public Matrix zeros(int numRows, int numCols) {
        return new DDRMWrapper(new DMatrixRMaj(numRows, numCols));
    }
}
