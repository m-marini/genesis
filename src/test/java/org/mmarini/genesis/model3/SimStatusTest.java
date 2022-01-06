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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.sameInstance;
import static org.mmarini.genesis.model3.Matrix.of;
import static org.mmarini.genesis.model3.MatrixMatchers.matrixCloseTo;

class SimStatusTest {

    private SimStatus status;

    @Test
    void copy() {
        /*
         * Given a simulation status
         */


        /*
         * When copy
         */
        SimStatus result = status.copy();

        /*
         * Then should return the locations
         */
        assertThat(result, not(sameInstance(status)));
        assertThat(result.getResources(), not(sameInstance(status.getResources())));
        assertThat(result.getResources(), matrixCloseTo(status.getResources()));
    }

    private Matrix createQuantities() {
        return of(new double[][]{
                {1, 2, 3, 4},
                {5, 6, 7, 8}
        });
    }

    @BeforeEach
    void init() {
        List<Population> species = List.of();
        status = new SimStatus(0, createQuantities(), species);
    }
}
