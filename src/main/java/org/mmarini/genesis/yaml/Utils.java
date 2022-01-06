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

package org.mmarini.genesis.yaml;

import org.mmarini.genesis.model3.Matrix;

import java.util.List;

import static org.mmarini.genesis.model3.Matrix.zeros;

public class Utils {

    /**
     * @param from source list of key
     * @param to   target list of key
     */
    static int[][] keysMap(List<String> from, List<String> to) {
        return from.stream().filter(to::contains).map(
                n -> new int[]{from.indexOf(n), to.indexOf(n)}
        ).toArray(int[][]::new);
    }

    /**
     * @param map    the map between source and target
     * @param noRows the number of target rows
     * @param source the source
     */
    static Matrix matrixMap(int[][] map, int noRows, Matrix source) {
        Matrix result = zeros(noRows, source.getNumCols());
        for (int[] a : map) {
            result.insert(source.extractRow(a[0]), a[1], 0);
        }
        return result;
    }

    /**
     * @param from   source list of key
     * @param to     target list of key
     * @param noRows the number of target rows
     * @param source the source
     */
    static Matrix matrixMap(List<String> from, List<String> to, int noRows, Matrix source) {
        return matrixMap(keysMap(from, to), noRows, source);
    }
}
