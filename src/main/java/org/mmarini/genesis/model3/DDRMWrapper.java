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
import org.ejml.data.DMatrixRMaj;
import org.ejml.dense.row.CommonOps_DDRM;

import java.util.function.BiFunction;
import java.util.function.DoublePredicate;
import java.util.function.DoubleUnaryOperator;
import java.util.stream.IntStream;

import static org.ejml.dense.row.CommonOps_DDRM.elementLog;

/**
 *
 */
public class DDRMWrapper implements Matrix {
    private final DMatrixRMaj matrix;

    /**
     * @param matrix the matrix
     */
    public DDRMWrapper(DMatrixRMaj matrix) {
        this.matrix = matrix;
    }

    @Override
    public Matrix add(Matrix other) {
        assert matrix.getNumRows() == other.getNumRows()
                && matrix.getNumCols() == other.getNumCols()
                : String.format("Matrix %dx%d != Matrix %dx%d",
                matrix.getNumRows(), matrix.getNumCols(),
                other.getNumRows(), other.getNumCols());
        return new DDRMWrapper(CommonOps_DDRM.add(matrix, ((DDRMWrapper) other).get(), null));
    }

    @Override
    public Matrix addi(double other) {
        return mapi(v -> v + other);
    }

    @Override
    public Matrix addi(Matrix other) {
        if (other.getNumRows() == 1) {
            if (other.getNumCols() == 1) {
                return addi(other.get(0, 0));
            } else {
                assert other.getNumCols() == matrix.getNumCols()
                        : String.format("Invalid operation %dx%d + %dx%d",
                        matrix.getNumRows(), matrix.getNumCols(),
                        other.getNumRows(), other.getNumCols());
                return mapi((v, i, j) -> v + other.get(0, j));
            }
        } else if (other.getNumCols() == 1) {
            assert other.getNumRows() == matrix.getNumRows()
                    : String.format("Invalid operation %dx%d + %dx%d",
                    matrix.getNumRows(), matrix.getNumCols(),
                    other.getNumRows(), other.getNumCols());
            return mapi((v, i, j) -> v + other.get(i, 0));
        } else {
            CommonOps_DDRM.addEquals(matrix, ((DDRMWrapper) other).get());
            return this;
        }
    }

    @Override
    public Matrix assign(Matrix other, int row, int col) {
        CommonOps_DDRM.extract(((DDRMWrapper) other).matrix, 0, other.getNumRows(), 0, other.getNumCols(), matrix, row, col);
        return this;
    }

    /**
     * @param col the column
     * @param f   the mapper
     */
    public Matrix assignCol(int col, BiFunction<Double, Integer, Double> f) {
        return mapiCols((v, i, j, k) -> f.apply(v, i), col);
    }

    @Override
    public Matrix assignCols(Matrix other, int... cols) {
        assert other.getNumRows() == matrix.getNumRows()
                : String.format("cannot insert columns %dx%d into %dx%d",
                other.getNumRows(), other.getNumCols(),
                matrix.getNumRows(), matrix.getNumCols());
        assert other.getNumCols() == cols.length
                : String.format("Mismatched indexing %d columns of %dx%d map",
                cols.length,
                other.getNumRows(), other.getNumCols());
        int[] rows = IntStream.range(0, matrix.getNumRows()).toArray();
        CommonOps_DDRM.insert(((DDRMWrapper) other).get(), matrix, rows, matrix.getNumRows(), cols, cols.length);
        return this;
    }

    /**
     * @param row the row
     * @param f   the mapper
     */
    public Matrix assignRow(int row, BiFunction<Double, Integer, Double> f) {
        return mapiRows((v, i, j, k) -> f.apply(v, j), row);
    }

    /**
     * @param other other matrix
     * @param rows  the destination columns
     */
    public Matrix assignRows(Matrix other, int... rows) {
        assert other.getNumCols() == matrix.getNumCols()
                : String.format("cannot insert columns %dx%d into %dx%d",
                other.getNumRows(), other.getNumCols(),
                matrix.getNumRows(), matrix.getNumCols());
        assert other.getNumRows() == rows.length
                : String.format("Mismatched indexing %d columns of %dx%d map",
                rows.length,
                other.getNumRows(), other.getNumCols());
        int[] cols = IntStream.range(0, matrix.getNumCols()).toArray();
        CommonOps_DDRM.insert(((DDRMWrapper) other).get(), matrix, rows, rows.length, cols, matrix.getNumCols());
        return this;
    }

    @Override
    public Matrix cdfiRows() {
        int n = matrix.getNumRows();
        int m = matrix.getNumCols();
        Matrix sum = sumCols();
        for (int i = 1; i < n; i++) {
            for (int j = 0; j < m; j++) {
                matrix.set(i, j, matrix.get(i, j) + matrix.get(i - 1, j));
            }
        }
        return divi(sum);
    }

    @Override
    public int[] cellsOf(DoublePredicate p) {
        return IntStream.range(0, matrix.getData().length)
                .filter(i -> p.test(matrix.get(i)))
                .toArray();
    }

    @Override
    public int[] choose(Matrix selector) {
        assert selector.getNumRows() == 1
                : String.format("selector %dx%d is not a row matrix ",
                selector.getNumRows(), selector.getNumCols());
        assert matrix.getNumCols() == selector.getNumCols()
                : String.format("selector %dx%d has different # columns than %dx%d",
                selector.getNumRows(), selector.getNumCols(),
                getNumRows(), getNumCols());
        final int n = getNumRows();
        final int m = getNumCols();
        final int[] result = new int[m];
        for (int j = 0; j < m; j++) {
            result[j] = n - 1;
            final double v = selector.get(0, j);
            for (int i = 0; i < n - 1; i++) {
                if (v < matrix.get(i, j)) {
                    result[j] = i;
                    break;
                }
            }
        }
        return result;
    }

    @Override
    public Matrix copy() {
        return new DDRMWrapper(matrix.copy());
    }

    @Override
    public Matrix createLike() {
        return new DDRMWrapper(matrix.createLike());
    }

    @Override
    public Matrix divi(double value) {
        CommonOps_DDRM.divide(matrix, value, matrix);
        return this;
    }

    @Override
    public Matrix divi(Matrix other) {
        if (other.getNumRows() == 1) {
            if (other.getNumCols() == 1) {
                return divi(other.get(0, 0));
            } else {
                assert other.getNumCols() == matrix.getNumCols()
                        : String.format("Invalid operation %dx%d * %dx%d",
                        matrix.getNumRows(), matrix.getNumCols(),
                        other.getNumRows(), other.getNumCols());
                return mapi((v, i, j) -> v / other.get(0, j));
            }
        } else if (other.getNumCols() == 1) {
            assert other.getNumRows() == matrix.getNumRows()
                    : String.format("Invalid operation %dx%d * %dx%d",
                    matrix.getNumRows(), matrix.getNumCols(),
                    other.getNumRows(), other.getNumCols());
            return mapi((v, i, j) -> v / other.get(i, 0));
        } else {
            CommonOps_DDRM.elementDiv(matrix, ((DDRMWrapper) other).get());
            return this;
        }
    }

    @Override
    public Matrix expi() {
        CommonOps_DDRM.elementExp(matrix, matrix);
        return this;
    }

    @Override
    public Matrix expm1i() {
        return mapi(Math::expm1);
    }

    @Override
    public Matrix extractCols(int... cols) {
        int n = matrix.getNumRows();
        int[] rows = IntStream.range(0, n).toArray();
        DMatrixRMaj result = CommonOps_DDRM.extract(matrix, rows, rows.length, cols, cols.length, null);
        return new DDRMWrapper(result);
    }

    @Override
    public Matrix extractColumn(int col) {
        return new DDRMWrapper(CommonOps_DDRM.extractColumn(matrix, col, null));
    }

    @Override
    public Matrix extractRow(int row) {
        return new DDRMWrapper(CommonOps_DDRM.extractRow(matrix, row, null));
    }

    /**
     * @param rows the rows
     */
    @Override
    public Matrix extractRows(int... rows) {
        int m = matrix.getNumCols();
        int[] cols = IntStream.range(0, m).toArray();
        DMatrixRMaj result = CommonOps_DDRM.extract(matrix, rows, rows.length, cols, cols.length, null);
        return new DDRMWrapper(result);
    }

    @Override
    public double get(final int i, final int j) {
        return matrix.get(i, j);
    }

    public DMatrixRMaj get() {
        return matrix;
    }

    /**
     *
     */
    DMatrixRMaj getMatrix() {
        return matrix;
    }

    @Override
    public int getNumCols() {
        return matrix.getNumCols();
    }

    @Override
    public int getNumRows() {
        return matrix.getNumRows();
    }

    @Override
    public Matrix insert(Matrix other, int row, int col) {
        CommonOps_DDRM.insert(((DDRMWrapper) other).get(), matrix, row, col);
        return this;
    }

    public Matrix logi() {
        elementLog(matrix, matrix);
        return this;
    }

    @Override
    public Matrix lti() {
        return mapi(v -> v < 0.0 ? 1.0 : 0.0);
    }

    @Override
    public Matrix mapi(DoubleUnaryOperator mapper) {
        CommonOps_DDRM.apply(matrix, mapper::applyAsDouble, matrix);
        return this;
    }

    /**
     * @param f the mapper
     */
    public Matrix mapi(Function3<Double, Integer, Integer, Double> f) {
        return mapi(0, matrix.getNumRows() - 1, 0, matrix.getNumCols() - 1, f);
    }

    @Override
    public Matrix mapi(int startRow, int endRow, int startCol, int endCol, Function3<Double, Integer, Integer, Double> mapper) {
        for (int i = startRow; i <= endRow; i++) {
            for (int j = startCol; j <= endCol; j++) {
                try {
                    double v = mapper.apply(matrix.get(i, j), i, j);
                    matrix.set(i, j, v);
                } catch (Throwable e) {
                    throw new IllegalArgumentException(e);
                }
            }
        }
        return this;
    }

    @Override
    public Matrix mapi(int startRow, int endRow, int startCol, int endCol, DoubleUnaryOperator mapper) {
        for (int i = startRow; i <= endRow; i++) {
            for (int j = startCol; j <= endCol; j++) {
                try {
                    double v = mapper.applyAsDouble(matrix.get(i, j));
                    matrix.set(i, j, v);
                } catch (Throwable e) {
                    throw new IllegalArgumentException(e);
                }
            }
        }
        return this;
    }

    /**
     * @param mapper the mapper
     * @param cols   the columns
     */
    public Matrix mapiCols(Function4<Double, Integer, Integer, Integer, Double> mapper, int... cols) {
        int n = matrix.getNumRows();
        int m = cols.length;
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                try {
                    int k = cols[j];
                    double v = mapper.apply(matrix.get(i, k), i, j, k);
                    matrix.set(i, k, v);
                } catch (Throwable e) {
                    throw new IllegalArgumentException(e);
                }
            }
        }
        return this;
    }

    /**
     * @param mapper the mapper
     * @param rows   the rows
     */
    public Matrix mapiRows(Function4<Double, Integer, Integer, Integer, Double> mapper, int... rows) {
        int n = rows.length;
        int m = matrix.getNumCols();
        for (int i = 0; i < n; i++) {
            int k = rows[i];
            for (int j = 0; j < m; j++) {
                try {
                    double v = mapper.apply(matrix.get(k, j), i, j, k);
                    matrix.set(k, j, v);
                } catch (Throwable e) {
                    throw new IllegalArgumentException(e);
                }
            }
        }
        return this;
    }

    @Override
    public double max() {
        return CommonOps_DDRM.elementMax(matrix);
    }

    /**
     * Returns matrix[i][j] = max(matrix[i][j], value)
     *
     * @param value the value
     */
    public Matrix maxi(double value) {
        return mapi(v -> Math.max(v, value));
    }

    @Override
    public double min() {
        return CommonOps_DDRM.elementMin(matrix);
    }

    /**
     *
     */
    public Matrix minCols() {
        return new DDRMWrapper(CommonOps_DDRM.minCols(matrix, null));
    }

    @Override
    public Matrix mini(double value) {
        return mapi(v -> Math.min(v, value));
    }

    /**
     * @param other the other matrix
     */
    public Matrix mini(Matrix other) {
        assert matrix.getNumRows() == other.getNumRows()
                && matrix.getNumCols() == other.getNumCols()
                : String.format("mini between different martrices %dx%d, %dx%d ",
                matrix.getNumRows(), matrix.getNumCols(),
                other.getNumRows(), other.getNumCols());
        return mapi((x, i, j) -> Math.min(x, other.get(i, j)));
    }

    /**
     * @param other the other matrix
     */
    public Matrix muli(double other) {
        CommonOps_DDRM.scale(other, matrix);
        return this;
    }

    /**
     * @param other the other matrix
     */
    public Matrix muli(Matrix other) {
        if (other.getNumRows() == 1) {
            if (other.getNumCols() == 1) {
                return muli(other.get(0, 0));
            } else {
                assert other.getNumCols() == matrix.getNumCols()
                        : String.format("Invalid operation %dx%d * %dx%d",
                        matrix.getNumRows(), matrix.getNumCols(),
                        other.getNumRows(), other.getNumCols());
                return mapi((v, i, j) -> v * other.get(0, j));
            }
        } else if (other.getNumCols() == 1) {
            assert other.getNumRows() == matrix.getNumRows()
                    : String.format("Invalid operation %dx%d * %dx%d",
                    matrix.getNumRows(), matrix.getNumCols(),
                    other.getNumRows(), other.getNumCols());
            return mapi((v, i, j) -> v * other.get(i, 0));
        } else {
            CommonOps_DDRM.elementMult(matrix, ((DDRMWrapper) other).get());
            return this;
        }
    }

    /**
     * Returns the negative in place
     */
    public Matrix negi() {
        CommonOps_DDRM.changeSign(matrix);
        return this;
    }

    /**
     * Returns matrix[i][j] = pow(matrix[i][j], value)
     *
     * @param value the value
     */
    public Matrix powi(double value) {
        CommonOps_DDRM.elementPower(matrix, value, matrix);
        return this;
    }

    /**
     * @param other the other matrix
     */
    public Matrix prod(Matrix other) {
        assert matrix.getNumRows() == 1 && other.getNumCols() == 1
                || matrix.getNumCols() == 1 && other.getNumRows() == 1
                : String.format("External product %dx%d ^ %dx%d not allowed",
                matrix.getNumRows(), matrix.getNumCols(),
                other.getNumRows(), other.getNumCols());
        if (matrix.getNumRows() == 1 && other.getNumCols() == 1) {
            int n = other.getNumRows();
            int m = matrix.getNumCols();
            DMatrixRMaj result = new DMatrixRMaj(n, m);
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < m; j++) {
                    result.set(i, j, matrix.get(0, j) * other.get(i, 0));
                }
            }
            return new DDRMWrapper(result);
        } else {
            int n = matrix.getNumRows();
            int m = other.getNumCols();
            DMatrixRMaj result = new DMatrixRMaj(n, m);
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < m; j++) {
                    result.set(i, j, other.get(0, j) * matrix.get(i, 0));
                }
            }
            return new DDRMWrapper(result);
        }
    }

    /**
     * @param row   the row
     * @param col   the column
     * @param value the value
     */
    public Matrix set(int row, int col, double value) {
        matrix.set(row, col, value);
        return this;
    }

    @Override
    public Matrix softmaxi() {
        expi();
        double sum = CommonOps_DDRM.sumCols(
                CommonOps_DDRM.sumRows(matrix, null),
                null
        ).get(0);
        return divi(sum);
    }

    /**
     * @param value the value
     */
    public Matrix subi(double value) {
        return mapi(v -> v - value);
    }

    /**
     * @param other the other matrix
     */
    public Matrix subi(Matrix other) {
        if (other.getNumRows() == 1) {
            if (other.getNumCols() == 1) {
                return subi(other.get(0, 0));
            } else {
                assert other.getNumCols() == matrix.getNumCols()
                        : String.format("Invalid operation %dx%d - %dx%d",
                        matrix.getNumRows(), matrix.getNumCols(),
                        other.getNumRows(), other.getNumCols());
                return mapi((v, i, j) -> v - other.get(0, j));
            }
        } else if (other.getNumCols() == 1) {
            assert other.getNumRows() == matrix.getNumRows()
                    : String.format("Invalid operation %dx%d - %dx%d",
                    matrix.getNumRows(), matrix.getNumCols(),
                    other.getNumRows(), other.getNumCols());
            return mapi((v, i, j) -> v - other.get(i, 0));
        } else {
            CommonOps_DDRM.subtractEquals(matrix, ((DDRMWrapper) other).get());
            return this;
        }
    }

    /**
     *
     */
    public Matrix sumCols() {
        return new DDRMWrapper(CommonOps_DDRM.sumCols(matrix, null));
    }

    @Override
    public String toString() {
        return String.valueOf(matrix);
    }

    /**
     *
     */
    public Matrix trasposei() {
        CommonOps_DDRM.transpose(matrix);
        return this;
    }
}
