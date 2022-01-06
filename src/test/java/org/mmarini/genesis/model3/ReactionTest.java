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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.mmarini.genesis.model3.Matrix.of;
import static org.mmarini.genesis.model3.MatrixMatchers.matrixCloseTo;

class ReactionTest {
    static final int REF = SubTest.ATP.ordinal();

    private Reaction react;

    @Test
    void apply() {

        Matrix dc = of(new double[][]{
                {1, 2}
        });

        Matrix result = react.apply(REF, dc);

        assertThat(result, matrixCloseTo(new double[][]{
                {-1, -2}, // ADP
                {-1, -2}, // PO4
                {-1, -2}, // OH
                {1, 2}, // H
                {1, 2}, // O2
                {1, 2}, // ATP
                {0, 0}, // GLY
                {0, 0}, // LUX
        }));
    }

    @BeforeEach
    void init() {
        Matrix reagents = of(new double[][]{
                {2}, // ADP
                {2}, // PO4
                {2}, // OH
                {2}, // H
                {0}, // O2
                {0}, // ATP
                {0}, // GLY
                {0}  // LUX
        });
        Matrix threshold = of(new double[][]{
                {1}, // ADP
                {1}, // PO4
                {1}, // OH
                {1}, // H
                {0}, // O2
                {0}, // ATP
                {0}, // GLY
                {0}  // LUX
        });
        Matrix products = of(new double[][]{
                {0}, // ADP
                {0}, // PO4
                {0}, // OH
                {4}, // H
                {2}, // O2
                {2}, // ATP
                {0}, // GLY
                {0}  // LUX
        });
        Matrix speeds = of(new double[][]{
                {0}, // ADP
                {0}, // PO4
                {0}, // OH
                {0}, // H
                {0}, // O2
                {0}, // ATP
                {0.5}, // GLY
                {1}, // LUX
        });
        react = Reaction.create(reagents, products, threshold, speeds);
    }

    @Test
    void max0() {

        Matrix sub = of(new double[][]{
                {0.5, 1}, // ADP
                {1, 1}, // PO4
                {0, 0}, // OH
                {1, 1}, // H
                {0, 0}, // O2
                {0, 0}, // ATP
                {1, 10}, // GLY
                {1, 1}, // LUX
        });
        double dt = 10;

        Matrix max = react.max(REF, sub, dt);

        assertThat(max, matrixCloseTo(new double[][]{
                {0, 0}
        }));
    }

    @Test
    void maxBySpeed() {

        Matrix sub = of(new double[][]{
                {2, 2}, // ADP
                {2, 2}, // PO4
                {2, 2}, // OH
                {2, 2}, // H
                {1, 1}, // O2
                {1, 1}, // ATP
                {1, 10}, // GLY
                {1, 1}, // LUX
        });
        double dt = 0.1;
        Matrix max = react.max(REF, sub, dt);

        assertThat(max, matrixCloseTo(new double[][]{
                {0.5 * 0.1, 1 * 0.1}
        }));
    }

    @Test
    void maxByThreshold() {
        Matrix sub = of(new double[][]{
                {2, 1.1}, // ADP
                {2, 2}, // PO4
                {2, 2}, // OH
                {2, 2}, // H
                {1, 1}, // O2
                {1, 1}, // ATP
                {1, 10}, // GLY
                {1, 1}, // LUX
        });
        double dt = 10;
        Matrix max = react.max(REF, sub, dt);

        assertThat(max, matrixCloseTo(new double[][]{
                {1, 0.1}
        }));
    }

    @Test
    void props() {
        assertThat(react.getAlpha(), matrixCloseTo(new double[][]{
                {-2}, // ADP
                {-2}, // PO4
                {-2}, // OH
                {2}, // H
                {2}, // O2
                {2}, // ATP
                {0}, // GLY
                {0}  // LUX
        }));
        assertThat(react.getReagents(), matrixCloseTo(new double[][]{
                {2}, // ADP
                {2}, // PO4
                {2}, // OH
                {2} // H
        }));
        assertThat(react.getThresholds(), matrixCloseTo(new double[][]{
                {1}, // ADP
                {1}, // PO4
                {1}, // OH
                {1} // H
        }));
        assertThat(react.getSpeeds(), matrixCloseTo(new double[][]{
                {0.5}, // GLY
                {1}, // LUX
        }));
    }

    enum SubTest {ADP, PO4, OH, H, O2, ATP, GLY, LUX}

}