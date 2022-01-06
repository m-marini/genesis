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

package org.mmarini.genesis.alg;

import io.reactivex.rxjava3.functions.Function3;
import io.reactivex.rxjava3.functions.Function4;

import java.util.function.DoubleBinaryOperator;
import java.util.function.DoubleUnaryOperator;
import java.util.function.Supplier;
import java.util.function.ToDoubleBiFunction;

public class Matrix {
    /**
     * @param noRows number of rows
     * @param noCols number of columns
     * @param gen    generator
     */
    public static Matrix create(int noRows, int noCols, ToDoubleBiFunction<Integer, Integer> gen) {
        double[] data = new double[noRows * noCols];
        int idx = 0;
        for (int i = 0; i < noRows; i++) {
            for (int j = 0; j < noCols; j++) {
                data[idx++] = gen.applyAsDouble(i, j);
            }
        }
        return new Matrix(data, noRows, noCols, 0, noCols, 1);
    }

    /**
     * @param data the data
     */
    public static Matrix of(double[][] data) {
        int n = data.length;
        int m = data[0].length;
        double[] bfr = new double[n * m];
        int idx = 0;
        for (double[] datum : data) {
            System.arraycopy(datum, 0, bfr, idx, m);
            idx += m;
        }
        return new Matrix(bfr, n, m, 0, m, 1);
    }

    /**
     * @param data the data
     */
    public static Matrix of(double[] data) {
        return new Matrix(data, data.length, 1, 0, 1, 0);
    }

    /**
     * @param noRows number of rows
     * @param noCols number of columns
     */
    public static Matrix ones(int noRows, int noCols) {
        return new Matrix(new double[]{1}, noRows, noCols, 0, 0, 0);
    }

    /**
     * @param noRows number of rows
     * @param noCols number of columns
     */
    public static Matrix zeros(int noRows, int noCols) {
        return new Matrix(new double[]{0}, noRows, noCols, 0, 0, 0);
    }

    private final double[] data;
    private final int noRows;
    private final int noCols;
    private final int offset;
    private final int rowStride;
    private final int colStride;

    /**
     * @param data      the data
     * @param noRows    number of rows
     * @param noCols    number of columns
     * @param offset    the offset
     * @param rowStride the row stride
     * @param colStride the column stride
     */
    public Matrix(double[] data, int noRows, int noCols, int offset, int rowStride, int colStride) {
        assert noRows >= 0 && noCols >= 0
                : String.format("size %dx%d must be not negative",
                noRows, noCols);
        this.data = data;
        this.noRows = noRows;
        this.noCols = noCols;
        this.offset = offset;
        this.rowStride = rowStride;
        this.colStride = colStride;
    }

    /**
     * @param row the row
     * @param col the column
     */
    private double _safeGet(int row, int col) {
        return data[_safeIndex(row, col)];
    }

    /**
     * @param row the row
     * @param col the column
     */
    private int _safeIndex(int row, int col) {
        return offset + row * rowStride + col * colStride;
    }

    /**
     * @param other other value
     */
    public Matrix add(Matrix other) {
        return reduce(other, Double::sum);
    }

    /**
     * @param other other value
     */
    public Matrix add(double other) {
        return map(x -> x + other);
    }

    /**
     * @param other other value
     */
    public Matrix addi(Matrix other) {
        return reducei(other, Double::sum);
    }

    /**
     * @param other other value
     */
    public Matrix addi(double other) {
        return mapi(x -> x + other);
    }

    /**
     * @param col the column
     */
    Matrix assertForColIndex(int col) {
        assert col >= 0 && col < noCols
                : String.format("column %d not in range 0:%d", col, noCols);
        return this;
    }

    /**
     * @param row the row
     */
    Matrix assertForRowIndex(int row) {
        assert row >= 0 && row < noRows
                : String.format("row %d not in range 0:%d", row, noRows);
        return this;
    }

    /**
     * @param noRows number of rows
     * @param noCols number of columns
     */
    public Matrix broadcast(int noRows, int noCols) {
        assert this.noRows == 1 && this.noCols == 1
                || this.noRows == 1 && noCols == this.noCols
                || this.noCols == 1 && noRows == this.noRows
                : String.format("cannod broadcast %dx%d to %dx%d",
                this.noRows, this.noCols,
                noRows, noCols);
        if (this.noRows > 1) {
            return new Matrix(expand().data, noRows, noCols, 0, 1, 0);
        } else if (this.noCols > 1) {
            return new Matrix(expand().data, noRows, noCols, 0, 0, 1);
        } else {
            return new Matrix(new double[]{data[offset]}, noRows, noCols, 0, 0, 0);
        }
    }

    /**
     * @param cols the columns
     */
    public Matrix cols(int... cols) {
        assert cols.length > 0 : "columns must not be empty";
        if (cols.length == 1) {
            return slice(0, cols[0], noRows, 1);
        } else {
            return create(noRows, cols.length, (i, j) -> get(i, cols[j]));
        }
    }

    /**
     *
     */
    public Matrix copy() {
        int n = noRows * noCols;
        if (data.length <= n) {
            double[] bfr = new double[data.length];
            System.arraycopy(data, 0, bfr, 0, data.length);
            return new Matrix(bfr, noRows, noCols, offset, noCols, noRows);
        } else {
            return expand();
        }
    }

    /**
     * @param other other value
     */
    public Matrix div(Matrix other) {
        return reduce(other, (x, y) -> x / y);
    }

    /**
     * @param other other value
     */
    public Matrix div(double other) {
        return map(x -> x / other);
    }

    /**
     * @param other other value
     */
    public Matrix divi(Matrix other) {
        return reducei(other, (x, y) -> x / y);
    }

    /**
     * @param other other value other value
     */
    public Matrix divi(double other) {
        return mapi(x -> x / other);
    }

    /**
     *
     */
    public Matrix expand() {
        if (isFull()) {
            return this;
        } else {
            double[] bfr = new double[noRows * noCols];
            int to = 0;
            int row0 = offset;
            for (int i = 0; i < noRows; i++) {
                int from = row0;
                for (int j = 0; j < noCols; j++) {
                    bfr[to++] = data[from];
                    from += colStride;
                }
                row0 += rowStride;
            }
            return new Matrix(bfr, noRows, noCols, 0, noCols, 1);
        }
    }

    /**
     * @param mapper the mapper
     */
    private Matrix expandAndMap(Function3<Double, Integer, Integer, Double> mapper) {
        double[] bfr = new double[noRows * noCols];
        int to = 0;
        int rowOffset = offset;
        for (int i = 0; i < noRows; ++i) {
            int from = rowOffset;
            for (int j = 0; j < noCols; ++j) {
                try {
                    bfr[to++] = mapper.apply(data[from], i, j);
                } catch (Throwable e) {
                    bfr[to++] = data[from];
                }
                from += colStride;
            }
            rowOffset += rowStride;
        }
        return new Matrix(bfr, noRows, noCols, 0, noCols, 1);
    }

    /**
     * @param row the row
     * @param col the column
     */
    public double get(int row, int col) {
        return data[index(row, col)];
    }

    /**
     *
     */
    public int getNoCols() {
        return noCols;
    }

    /**
     *
     */
    public int getNoRows() {
        return noRows;
    }

    /**
     * @param other other
     */
    boolean hasSameSize(Matrix other) {
        return noRows == other.noRows
                && noCols == other.noCols;
    }

    /**
     * @param other other
     */
    boolean hasSameStructure(Matrix other) {
        return noRows == other.noRows
                && noCols == other.noCols
                && offset == other.offset
                && rowStride == other.rowStride
                && colStride == other.colStride;
    }

    /**
     * @param row the row
     * @param col the column
     */
    public int index(int row, int col) {
        assertForRowIndex(row).assertForColIndex(col);
        return _safeIndex(row, col);
    }

    /**
     *
     */
    public boolean isBroadcast() {
        return data.length < noRows * noCols;
    }

    /**
     *
     */
    public boolean isFull() {
        return data.length == noRows * noCols;
    }

    /**
     *
     */
    public boolean isSlice() {
        return data.length > noRows * noCols;
    }

    /**
     * @param mapper the mapper
     */
    public Matrix map(DoubleUnaryOperator mapper) {
        return isSlice()
                ? expandAndMap((x, i, j) -> mapper.applyAsDouble(x))
                : mapAllData(mapper);
    }

    /**
     * @param mapper the mapper
     */
    public Matrix map(Function3<Double, Integer, Integer, Double> mapper) {
        return expandAndMap(mapper);
    }

    /**
     * Retruns the mapped matrix of all data
     *
     * @param mapper the mapper
     */
    private Matrix mapAllData(DoubleUnaryOperator mapper) {
        double[] bfr = new double[data.length];
        for (int i = 0; i < data.length; i++) {
            bfr[i] = mapper.applyAsDouble(data[i]);
        }
        return new Matrix(bfr, noRows, noCols, offset, rowStride, colStride);
    }

    /**
     * @param mapper the mapper
     */
    public Matrix mapi(Function3<Double, Integer, Integer, Double> mapper) {
        return isBroadcast()
                ? expandAndMap(mapper)
                : mapiSlice(mapper);
    }

    /**
     * @param mapper the mapper
     */
    public Matrix mapi(DoubleUnaryOperator mapper) {
        return isSlice()
                ? mapiSlice(mapper)
                : mapiAllData(mapper);
    }

    /**
     * Retruns the in-place mapped matrix of all data
     *
     * @param mapper the mapper
     */
    private Matrix mapiAllData(DoubleUnaryOperator mapper) {
        for (int i = 0; i < data.length; i++) {
            data[i] = mapper.applyAsDouble(data[i]);
        }
        return this;
    }

    /**
     * Retruns the in-place mapped matrix of all data
     *
     * @param mapper the mapper
     */
    private Matrix mapiSlice(DoubleUnaryOperator mapper) {
        int row0 = offset;
        for (int i = 0; i < noRows; i++) {
            int idx = row0;
            for (int j = 0; j < noCols; j++) {
                data[idx] = mapper.applyAsDouble(data[idx]);
                idx += colStride;
            }
            row0 += rowStride;
        }
        return this;
    }

    /**
     * Returns the in-place mapped data for slice data
     *
     * @param mapper the mapper
     */
    private Matrix mapiSlice(Function3<Double, Integer, Integer, Double> mapper) {
        int rowOffset = offset;
        for (int i = 0; i < noRows; ++i) {
            int from = rowOffset;
            for (int j = 0; j < noCols; ++j) {
                try {
                    data[from] = mapper.apply(data[from], i, j);
                } catch (Throwable e) {
                    throw new IllegalArgumentException(e);
                }
                from += colStride;
            }
            rowOffset += rowStride;
        }
        return this;
    }

    /**
     * @param other other value
     */
    public Matrix max(Matrix other) {
        return reduce(other, Math::max);
    }

    /**
     * @param other other value
     */
    public Matrix max(double other) {
        return map(x -> Math.max(x, other));
    }

    /**
     * @param other other value
     */
    public Matrix maxi(Matrix other) {
        return reducei(other, Math::max);
    }

    /**
     * @param other other value
     */
    public Matrix maxi(double other) {
        return mapi(x -> Math.max(x, other));
    }

    /**
     * @param other other value
     */
    public Matrix min(Matrix other) {
        return reduce(other, Math::min);
    }

    /**
     * @param other other value
     */
    public Matrix min(double other) {
        return map(x -> Math.min(x, other));
    }

    /**
     * @param other other value
     */
    public Matrix mini(Matrix other) {
        return reducei(other, Math::min);
    }

    /**
     * @param other other value
     */
    public Matrix mini(double other) {
        return mapi(x -> Math.min(x, other));
    }

    /**
     * @param other other value
     */
    public Matrix mul(Matrix other) {
        return reduce(other, (x, y) -> x * y);
    }

    /**
     * @param other other value
     */
    public Matrix mul(double other) {
        return map(x -> x * other);
    }

    /**
     * @param other other value
     */
    public Matrix muli(Matrix other) {
        return reducei(other, (x, y) -> x * y);
    }

    /**
     * @param other other value
     */
    public Matrix muli(double other) {
        return mapi(x -> x * other);
    }

    /**
     *
     */
    public Matrix neg() {
        return map(x -> -x);
    }

    /**
     *
     */
    public Matrix negi() {
        return mapi(x -> -x);
    }

    /**
     * @param other other value
     */
    public Matrix reduce(Matrix other, DoubleBinaryOperator f) {
        if (hasSameStructure(other)) {
            return reduceDD(other, f);
        } else if (hasSameSize(other)) {
            return reduceSS(other, (x, y, i, j) -> f.applyAsDouble(x, y));
        } else {
            Supplier<String> message = () -> String.format("%dx%d reduce %dx%d wrong size",
                    noRows, noCols, other.noRows, other.noCols);
            if (noCols == 1) {
                if (noRows == 1) {
                    // broadcast
                    double x = get(0, 0);
                    return other.map(y -> f.applyAsDouble(x, y));
                } else {
                    // broadcast
                    assert noRows == other.noRows : message.get();
                    return other.map((y, i, j) -> f.applyAsDouble(_safeGet(i, 0), y));
                }
            } else if (noRows == 1) {
                // broadcast
                assert noCols == other.noCols : message.get();
                return other.map((y, i, j) -> f.applyAsDouble(_safeGet(0, j), y));
            } else if (other.noCols == 1) {
                if (other.noRows == 1) {
                    // broadcast
                    double y = other.get(0, 0);
                    return map(x -> f.applyAsDouble(x, y));
                } else {
                    // broadcast
                    assert noRows == other.noRows : message.get();
                    return map((x, i, j) -> f.applyAsDouble(x, _safeGet(j, 0)));
                }
            } else {
                // broadcast
                assert other.noRows == 1 && noCols == other.noCols : message.get();
                return map((x, i, j) -> f.applyAsDouble(x, _safeGet(0, j)));
            }
        }
    }

    /**
     * @param other other
     * @param f     the mapper
     */
    private Matrix reduceDD(Matrix other, DoubleBinaryOperator f) {
        double[] bfr = new double[data.length];
        for (int i = 0; i < data.length; i++) {
            bfr[i] = f.applyAsDouble(data[i], other.data[i]);
        }
        return new Matrix(bfr, noRows, noCols, offset, rowStride, colStride);
    }

    /**
     * @param other the other
     * @param f     the mapper
     */
    private Matrix reduceSS(Matrix other, Function4<Double, Double, Integer, Integer, Double> f) {
        double[] bfr = new double[data.length];
        int toRow = offset;
        int fromRow = other.offset;
        for (int i = 0; i < noRows; i++) {
            int to = toRow;
            int from = fromRow;
            for (int j = 0; j < noCols; j++) {
                try {
                    bfr[to] = f.apply(data[to], other.data[from], i, j);
                } catch (Throwable e) {
                    throw new IllegalArgumentException(e);
                }
                to += colStride;
                from += other.colStride;
            }
            toRow += rowStride;
            fromRow += other.rowStride;
        }
        return new Matrix(bfr, noRows, noCols, offset, rowStride, colStride);
    }

    /**
     * @param other other value
     */
    public Matrix reducei(Matrix other, DoubleBinaryOperator f) {
        if (hasSameStructure(other)) {
            return reduceiDD(other, f);
        } else if (hasSameSize(other)) {
            return reduceiSS(other, (x, y, i, j) -> f.applyAsDouble(x, y));
        } else {
            Supplier<String> message = () -> String.format("%dx%d reducei %dx%d wrong size",
                    noRows, noCols, other.noRows, other.noCols);
            if (noCols == 1) {
                if (noRows == 1) {
                    // broadcast
                    double x = get(0, 0);
                    return other.map(y -> f.applyAsDouble(x, y));
                } else {
                    // broadcast
                    assert noRows == other.noRows : message.get();
                    return other.map((y, i, j) -> f.applyAsDouble(get(i, 0), y));
                }
            } else if (noRows == 1) {
                // broadcast
                assert noCols == other.noCols : message.get();
                return other.map((y, i, j) -> f.applyAsDouble(get(0, j), y));
            } else if (other.noCols == 1) {
                if (other.noRows == 1) {
                    // broadcast
                    double y = other.get(0, 0);
                    return mapi(x -> f.applyAsDouble(x, y));
                } else {
                    // broadcast
                    assert noRows == other.noRows : message.get();
                    return mapi((x, i, j) -> f.applyAsDouble(x, get(j, 0)));
                }
            } else {
                // broadcast
                assert other.noRows == 1 && noCols == other.noCols : message.get();
                return mapi((x, i, j) -> f.applyAsDouble(x, get(0, j)));
            }
        }
    }

    /**
     * @param other the other
     * @param f     the mapper
     */
    private Matrix reduceiDD(Matrix other, DoubleBinaryOperator f) {
        for (int i = 0; i < data.length; i++) {
            data[i] = f.applyAsDouble(data[i], other.data[i]);
        }
        return this;
    }

    /**
     * @param other the other
     * @param f     the mapper
     */
    private Matrix reduceiSS(Matrix other, Function4<Double, Double, Integer, Integer, Double> f) {
        int toRow = offset;
        int fromRow = other.offset;
        for (int i = 0; i < noRows; i++) {
            int to = toRow;
            int from = fromRow;
            for (int j = 0; j < noCols; j++) {
                try {
                    data[to] = f.apply(data[to], other.data[from], i, j);
                } catch (Throwable e) {
                    throw new IllegalArgumentException(e);
                }
                to += colStride;
                from += other.colStride;
            }
            toRow += rowStride;
            toRow += other.rowStride;
        }
        return this;
    }

    /**
     * @param rows the rows
     */
    public Matrix rows(int... rows) {
        assert rows.length > 0 : "rows must not be empty";
        if (rows.length == 1) {
            return slice(rows[0], 0, 1, noCols);
        } else {
            return create(rows.length, noCols, (i, j) -> get(rows[i], j));
        }
    }

    /**
     * @param row    the row
     * @param col    the column
     * @param noRows number of rows
     * @param noCols number of columns
     */
    public Matrix slice(int row, int col, int noRows, int noCols) {
        assertForRowIndex(row);
        assertForColIndex(col);
        assert noRows <= this.noRows - row
                && noCols <= this.noCols - col :
                String.format("slice %d,%d %dx%d of %dx%d does not exist",
                        row, col, noRows, noCols,
                        this.noRows, this.noCols);
        int offset = _safeIndex(row, col);
        return new Matrix(data, noRows, noCols, offset, rowStride, colStride);
    }

    /**
     * @param other other value
     */
    public Matrix sub(Matrix other) {
        return reduce(other, (x, y) -> x - y);
    }

    /**
     * @param other other value
     */
    public Matrix sub(double other) {
        return map(x -> x - other);
    }

    /**
     * @param other other value
     */
    public Matrix subi(Matrix other) {
        return reducei(other, (x, y) -> x - y);
    }

    /**
     * @param other other value
     */
    public Matrix subi(double other) {
        return mapi(x -> x - other);
    }

    /**
     *
     */
    public Matrix transpose() {
        return new Matrix(data, noCols, noRows, offset, colStride, rowStride);
    }
}
