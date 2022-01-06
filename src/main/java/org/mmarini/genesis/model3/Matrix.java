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

import io.reactivex.rxjava3.functions.Function3;
import io.reactivex.rxjava3.functions.Function4;

import java.util.Random;
import java.util.function.BiFunction;
import java.util.function.DoublePredicate;
import java.util.function.DoubleUnaryOperator;

/**
 *
 */
public interface Matrix {
    MatrixBuilder builder = new DDRMWrapperBuilder();

    /**
     * Returns the horizontal stack composition of matrices
     *
     * @param matrices the matrices
     */
    static Matrix hstack(Matrix... matrices) {
        return builder.hstack(matrices);
    }

    /**
     * Returns a matrix (n x m)
     *
     * @param matrix the value rows
     */
    static Matrix of(double[]... matrix) {
        return builder.of(matrix);
    }

    /**
     * Returns a scalar matrix (1 x 1)
     *
     * @param value the source matrix
     */
    static Matrix of(double value) {
        return builder.of(value);
    }

    /**
     * Returns a column matrix (n x 1)
     *
     * @param vector the values
     */
    static Matrix of(double... vector) {
        return builder.of(vector);
    }

    /**
     * Returns a ones matrix (n x m)
     *
     * @param n number of rows
     * @param m number of columns
     */
    static Matrix ones(int n, int m) {
        return builder.values(n, m, 1);
    }

    /**
     * Returns a random matrix (n x m)
     *
     * @param n      number of rows
     * @param m      number of columns
     * @param random the random generator
     */
    static Matrix rand(int n, int m, Random random) {
        return builder.rand(n, m, random);
    }

    /**
     * Returns a normal distributed random matrix (n x m)
     *
     * @param n      number of rows
     * @param m      number of columns
     * @param random the random generator
     */
    static Matrix randn(int n, int m, Random random) {
        return builder.randn(n, m, random);
    }

    /**
     * Returns a matrix filled with a value (n x m)
     *
     * @param n     the number of rows
     * @param m     the number of columns
     * @param value the value of elements
     */
    static Matrix values(int n, int m, double value) {
        return builder.values(n, m, value);
    }

    /**
     * Returns the vertical stack composition of matrices
     *
     * @param matrices the matrices
     */
    static Matrix vstack(Matrix... matrices) {
        return builder.vstack(matrices);
    }

    /**
     * Returns a zeros matrix (n x m)
     *
     * @param n number of rows
     * @param m number of columns
     */
    static Matrix zeros(int n, int m) {
        return builder.zeros(n, m);
    }

    /**
     * Returns the sum of the matrix with another matrix
     *
     * @param other the other matrix
     */
    Matrix add(Matrix other);

    /**
     * Returns the in-place sum of the matrix with scalar
     *
     * @param other the other matrix
     */
    Matrix addi(double other);

    /**
     * Returns the in-place sum of the matrix with another matrix
     *
     * @param other the other matrix
     */
    Matrix addi(Matrix other);

    Matrix assign(Matrix other, int row, int col);

    /**
     * Returns matrix[i][col] = f(matrix[i][col], i)
     *
     * @param col the column
     * @param f   the mapper
     */
    Matrix assignCol(int col, BiFunction<Double, Integer, Double> f);

    /**
     * Returns the matrix with a set of the changed columns from another matrix
     *
     * @param other the other matrix
     * @param cols  the destination columns
     */
    Matrix assignCols(Matrix other, int... cols);

    /**
     * Returns matrix[row][j] = f(matrix[row][j], j)
     * Returns the matrix with rows assigned by a function
     *
     * @param row the row
     * @param f   the mapper
     */
    Matrix assignRow(int row, BiFunction<Double, Integer, Double> f);

    /**
     * Returns the matrix with a set of the changed rows from another matrix
     *
     * @param other other matrix
     * @param rows  the destination columns
     */
    Matrix assignRows(Matrix other, int... rows);

    /**
     * Returns the cumulative distribution function by columns
     */
    Matrix cdfiRows();

    /**
     * Returns the indices of cell matching the value predicate
     *
     * @param p the predicate
     */
    int[] cellsOf(DoublePredicate p);

    /**
     * Returns the raws index selected by selector
     *
     * @param selector the selector
     */
    int[] choose(Matrix selector);

    /**
     * Returns a copy of the matrix
     */
    Matrix copy();

    /**
     * Returns a zero matrix with the same shape
     */
    Matrix createLike();

    /**
     * Returns the in-place division of the matrix with a scalar
     *
     * @param value the value
     */
    Matrix divi(double value);

    /**
     * Returns the in-place division of the matrix with another matrix
     *
     * @param other the other matrix
     */
    Matrix divi(Matrix other);

    /**
     * Returns matrix[i][j] = exp(matrix[i][j])
     */
    Matrix expi();

    /**
     * Returns matrix[i][j] = expm1(matrix[i][j])
     */
    Matrix expm1i();

    /**
     * Returns a set of columns of matrix
     *
     * @param cols the columns
     */
    Matrix extractCols(int... cols);

    /**
     * Returns a column of matrix
     *
     * @param col the column
     */
    Matrix extractColumn(int col);

    /**
     * Returns a row of matrix
     *
     * @param row the row
     */
    Matrix extractRow(int row);

    /**
     * Returns a set of rows of matrix
     *
     * @param rows the rows
     */
    Matrix extractRows(int... rows);

    /**
     * Returns the value of a cell
     *
     * @param row number of row
     * @param col number of column
     */
    double get(int row, int col);

    /**
     * Returns the number of columns
     */
    int getNumCols();

    /**
     * Returns the number of rows
     */
    int getNumRows();

    /**
     * @param other the other matrix
     * @param row   the row
     * @param col   the column
     */
    Matrix insert(Matrix other, int row, int col);

    /**
     * Returns matrix[i][j] = log(matrix[i][j])
     */
    Matrix logi();


    /**
     * Returns matrix[i][j] = matrix[i][j] < 0.0
     */
    Matrix lti();

    /**
     * Returns matrix[i][j] = mapper(matrix[i][j])
     *
     * @param mapper the mapper
     */
    Matrix mapi(DoubleUnaryOperator mapper);

    /**
     * Returns matrix[i][j] = mapper(matrix[i][j], i, j)
     *
     * @param mapper the mapper
     */
    Matrix mapi(Function3<Double, Integer, Integer, Double> mapper);

    /**
     * Returns matrix[i][j] = mapper(matrix[i][j], i, j)
     * for all i between startRow end endRow
     * and j from startCol to endCol
     *
     * @param startRow start row
     * @param endRow   end row
     * @param startCol start col
     * @param endCol   end col
     * @param mapper   the mapper
     */
    Matrix mapi(int startRow, int endRow, int startCol, int endCol, Function3<Double, Integer, Integer, Double> mapper);

    /**
     * Returns matrix[i][j] = mapper(matrix[i][j])
     * for all i between startRow end endRow
     * and j from startCol to endCol
     *
     * @param startRow start row
     * @param endRow   end row
     * @param startCol start col
     * @param endCol   end col
     * @param mapper   the mapper
     */
    Matrix mapi(int startRow, int endRow, int startCol, int endCol, DoubleUnaryOperator mapper);

    /**
     * Returns matrix[i][cols[j]] = mapper(matrix[i][cols[j]], i, j, cols[j])
     *
     * @param mapper the mapper
     * @param cols   the columns
     */
    Matrix mapiCols(Function4<Double, Integer, Integer, Integer, Double> mapper, int... cols);

    /**
     * Returns matrix[cols[i]][j] = mapper(matrix[cols[i]][j], i, j, cols[i])
     *
     * @param mapper the mapper
     * @param rows   the rows
     */
    Matrix mapiRows(Function4<Double, Integer, Integer, Integer, Double> mapper, int... rows);

    /**
     * Returns the maximum value
     */
    double max();

    /**
     * Returns matrix[i][j] = max(matrix[i][j], value)
     *
     * @param value the value
     */
    Matrix maxi(double value);

    /**
     * Returns the minimum value
     */
    double min();

    /**
     * Returns the minimum values by column
     */
    Matrix minCols();

    /**
     * Returns matrix[i][j] = mini(matrix[i][j], value)
     *
     * @param value the value
     */
    Matrix mini(double value);

    /**
     * Returns matrix[i][j] = min(matrix[i][j], other[i][j])
     *
     * @param other the other matrix
     */
    Matrix mini(Matrix other);

    /**
     * @param other the other matrix
     */
    Matrix muli(double other);

    /**
     * @param other the other matrix
     */
    Matrix muli(Matrix other);

    /**
     * Returns matrix[i][j] = -matrix[i][j]
     */
    Matrix negi();

    /**
     * Returns matrix[i][j] = pow(matrix[i][j], value)
     *
     * @param value the value
     */
    Matrix powi(double value);

    /**
     * Returns the external matrix multiplication
     *
     * @param other the other matrix
     */
    Matrix prod(Matrix other);

    /**
     * Returns the matrix with a cell value changes
     *
     * @param row   the row
     * @param col   the column
     * @param value the value
     */
    Matrix set(int row, int col, double value);

    /**
     * Returns the softmax value
     */
    Matrix softmaxi();

    /**
     * Returns the in-place sum of the matrix with scalar
     *
     * @param value the value
     */
    Matrix subi(double value);

    /**
     * Returns the in-place sum of the matrix with another matrix
     *
     * @param other the other matrix
     */
    Matrix subi(Matrix other);

    /**
     * Returns a row with the sum of columns
     */
    Matrix sumCols();

    /**
     * Returns the transpose matrix
     */
    Matrix trasposei();
}
