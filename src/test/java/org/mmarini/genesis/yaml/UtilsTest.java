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

import org.junit.jupiter.api.Test;
import org.mmarini.genesis.model3.Matrix;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mmarini.Utils.toList;
import static org.mmarini.genesis.model3.Matrix.of;
import static org.mmarini.genesis.model3.MatrixMatchers.matrixCloseTo;

class UtilsTest {

    @Test
    static void iter2List() {
        List<String> from = List.of("A", "B", "C");
        List<String> result = toList(from.iterator());
        assertThat(result, contains("A", "B", "C"));
    }

    @Test
    void iter2ListEmpty() {
        List<String> from = List.of();
        List<String> result = toList(from.iterator());
        assertThat(result, hasSize(0));
    }

    @Test
    void keysMap() {
        List<String> from = List.of("A", "B", "C");
        List<String> to = List.of("B", "D", "C");
        int[][] result = Utils.keysMap(from, to);
        assertThat(result, equalTo(new int[][]{
                {1, 0},
                {2, 2}
        }));
    }

    @Test
    void matrixMap() {
        List<String> from = List.of("A", "B", "C");
        List<String> to = List.of("C", "D", "B");
        Matrix source = of(new double[][]{
                {1, 2, 3},
                {2, 3, 4},
                {3, 4, 5}
        });
        Matrix result = Utils.matrixMap(from, to, 3, source);
        assertThat(result, matrixCloseTo(new double[][]{
                {3, 4, 5},
                {0, 0, 0},
                {2, 3, 4}
        }));
    }
}