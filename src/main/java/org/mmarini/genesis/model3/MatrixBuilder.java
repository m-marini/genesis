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

import java.util.Random;

/**
 *
 */
public interface MatrixBuilder {

    /**
     * Returns the horizontal stack composition of matrices
     *
     * @param matrices the matrices
     */
    Matrix hstack(Matrix... matrices);

    /**
     * Returns a matrix
     *
     * @param matrix the value rows
     */
    Matrix of(double[]... matrix);

    /**
     * Returns a scalar matrix
     *
     * @param matrix the source matrix
     */
    Matrix of(double matrix);

    /**
     * Returns a colum matrix
     *
     * @param vector the values
     */
    Matrix of(double... vector);

    /**
     * Returns a ones matrix
     *
     * @param numRows number of rows
     * @param numCols number of columns
     */
    Matrix ones(int numRows, int numCols);

    /**
     * Returns a random matrix
     *
     * @param numRows number of rows
     * @param numCols number of columns
     * @param random  the random generator
     */
    Matrix rand(int numRows, int numCols, Random random);

    /**
     * Returns a random matrix
     *
     * @param numRows number of rows
     * @param numCols number of columns
     * @param random  the random generator
     */
    Matrix randn(int numRows, int numCols, Random random);

    /**
     * Returns a matrix filled with a value
     *
     * @param numRows the number of rows
     * @param numCols the number of columns
     * @param value   the value of elements
     */
    Matrix values(int numRows, int numCols, double value);


    /**
     * Returns the vertical stack composition of matrices
     *
     * @param matrices the matrices
     */
    Matrix vstack(Matrix[] matrices);

    /**
     * Returns a zeros matrix
     *
     * @param numRows number of rows
     * @param numCols number of columns
     */
    Matrix zeros(int numRows, int numCols);
}
