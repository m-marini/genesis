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

import org.junit.jupiter.api.Test;

import static java.lang.Math.log;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mmarini.genesis.model3.Matrix.of;
import static org.mmarini.genesis.model3.Matrix.zeros;
import static org.mmarini.genesis.model3.MatrixMatchers.matrixCloseTo;

class MatrixTest {
    @Test
    void add() {
        Matrix a = of(new double[][]{
                {1, 2},
                {3, 4}
        });
        Matrix b = of(new double[][]{
                {1, 2},
                {3, 4}
        });
        Matrix ops = a.add(b);

        assertThat(ops, not(sameInstance(a)));
        assertThat(ops, matrixCloseTo(new double[][]{
                {2, 4},
                {6, 8}
        }));
        assertThat(a, matrixCloseTo(new double[][]{
                {1, 2},
                {3, 4}
        }));
    }

    @Test
    void addiCol() {
        Matrix a = of(new double[][]{
                {1, 2},
                {3, 4}
        });
        Matrix b = of(new double[][]{
                {1},
                {2}
        });
        Matrix val = a.addi(b);

        assertThat(val, sameInstance(a));
        assertThat(val, matrixCloseTo(new double[][]{
                {2, 3},
                {5, 6}
        }));
    }

    @Test
    void addiDouble() {
        Matrix a = of(new double[][]{
                {1, 2},
                {3, 4}
        });
        Matrix val = a.addi(2);

        assertThat(val, sameInstance(a));
        assertThat(val, matrixCloseTo(new double[][]{
                {3, 4},
                {5, 6}
        }));
    }

    @Test
    void addiMat() {
        Matrix a = of(new double[][]{
                {1, 2},
                {3, 4}
        });
        Matrix b = of(new double[][]{
                {1, 3},
                {2, 4}
        });
        Matrix val = a.addi(b);

        assertThat(val, sameInstance(a));
        assertThat(val, matrixCloseTo(new double[][]{
                {2, 5},
                {5, 8}
        }));
    }

    @Test
    void addiRow() {
        Matrix a = of(new double[][]{
                {1, 2},
                {3, 4}
        });
        Matrix b = of(new double[][]{
                {1, 3}
        });
        Matrix val = a.addi(b);

        assertThat(val, sameInstance(a));
        assertThat(val, matrixCloseTo(new double[][]{
                {2, 5},
                {4, 7}
        }));
    }

    @Test
    void addiScalar() {
        Matrix a = of(new double[][]{
                {1, 2},
                {3, 4}
        });
        Matrix b = of(new double[][]{{1}});

        Matrix val = a.addi(b);

        assertThat(val, sameInstance(a));
        assertThat(val, matrixCloseTo(new double[][]{
                {2, 3},
                {4, 5}
        }));
    }

    @Test
    void assign() {
        Matrix a = of(new double[][]{
                {1, 2, 3, 4},
                {1, 2, 3, 4},
                {1, 2, 3, 4},
                {1, 2, 3, 4}
        });
        Matrix b = of(new double[][]{
                {5, 6},
                {7, 8}
        });
        Matrix val = a.assign(b, 1, 2);

        assertThat(val, sameInstance(a));
        assertThat(val, matrixCloseTo(new double[][]{
                {1, 2, 3, 4},
                {1, 2, 5, 6},
                {1, 2, 7, 8},
                {1, 2, 3, 4}
        }));
    }

    @Test
    void assignCol() {
        Matrix a = of(new double[][]{
                {1, 2},
                {3, 4}
        });
        Matrix b = of(new double[][]{
                {1},
                {2}
        });
        Matrix val = a.assignCol(0, (x, i) -> x + b.get(i, 0));

        assertThat(val, sameInstance(a));
        assertThat(val, matrixCloseTo(new double[][]{
                {2, 2},
                {5, 4}
        }));
    }

    @Test
    void assignCols() {
        Matrix a = zeros(2, 4);
        Matrix b = of(new double[][]{
                {1, 2},
                {3, 4}
        });
        Matrix val = a.assignCols(b, 3, 1);

        assertThat(val, sameInstance(a));
        assertThat(val, matrixCloseTo(new double[][]{
                {0, 2, 0, 1},
                {0, 4, 0, 3}
        }));
    }

    @Test
    void assignRow() {
        Matrix a = of(new double[][]{
                {1, 2},
                {3, 4}
        });
        Matrix b = of(new double[][]{
                {1, 2}
        });
        Matrix val = a.assignRow(0, (x, i) -> x + b.get(0, i));

        assertThat(val, sameInstance(a));
        assertThat(val, matrixCloseTo(new double[][]{
                {2, 4},
                {3, 4}
        }));
    }

    @Test
    void assignRows() {
        Matrix a = zeros(4, 2);
        Matrix b = of(new double[][]{
                {1, 2},
                {3, 4}
        });
        Matrix val = a.assignRows(b, 3, 1);

        assertThat(val, sameInstance(a));
        assertThat(val, matrixCloseTo(new double[][]{
                {0, 0},
                {3, 4},
                {0, 0},
                {1, 2}
        }));
    }

    @Test
    void cdfiRows() {
        Matrix a = of(new double[][]{
                {1, 2},
                {3, 4}
        });

        Matrix val = a.cdfiRows();

        assertThat(val, sameInstance(a));
        assertThat(val, matrixCloseTo(new double[][]{
                {1.0 / 4, 2.0 / 6},
                {1, 1}
        }));
    }

    @Test
    void choose() {
        Matrix a = of(new double[][]{
                {1, 1, 1, 1, 1},
                {1, 1, 1, 1, 1},
                {2, 2, 2, 2, 2},
                {4, 4, 4, 4, 4}
        }).cdfiRows();
        int[] val = a.choose(of(new double[][]{{0, 1.0 / 8, 2.0 / 8, 4.0 / 8, 1}}));

        assertThat(val, equalTo(new int[]{
                0, 1, 2, 3, 3
        }));
    }

    @Test
    void copy() {
        Matrix matrix = of(new double[][]{
                {1, 2},
                {3, 4}
        });
        Matrix val = matrix.copy();

        assertThat(val, not(sameInstance(matrix)));
        assertThat(val, matrixCloseTo(new double[][]{
                {1, 2},
                {3, 4}
        }));
    }

    @Test
    void createLike() {
        Matrix val = of(new double[][]{
                {1, 2},
                {3, 4}
        }).createLike();

        assertThat(val, matrixCloseTo(new double[][]{
                {0, 0},
                {0, 0}
        }));
    }

    @Test
    void diviCol() {
        Matrix a = of(new double[][]{
                {1, 2},
                {3, 4}
        });
        Matrix b = of(2, 3);
        Matrix val = a.divi(b);

        assertThat(val, sameInstance(a));
        assertThat(val, matrixCloseTo(new double[][]{
                {0.5, 1},
                {1, 4.0 / 3}
        }));
    }

    @Test
    void diviDouble() {
        Matrix a = of(new double[][]{
                {1, 2},
                {3, 4}
        });
        Matrix val = a.divi(2);

        assertThat(val, sameInstance(a));
        assertThat(val, matrixCloseTo(new double[][]{
                {0.5, 1},
                {1.5, 2}
        }));
    }

    @Test
    void diviMat() {
        Matrix a = of(new double[][]{
                {1, 2},
                {3, 4}
        });
        Matrix b = of(new double[][]{
                {2, 3},
                {4, 5}
        });
        Matrix val = a.divi(b);

        assertThat(val, sameInstance(a));
        assertThat(val, matrixCloseTo(new double[][]{
                {0.5, 2.0 / 3},
                {0.75, 0.8}
        }));
    }

    @Test
    void diviRow() {
        Matrix a = of(new double[][]{
                {1, 2},
                {3, 4}
        });
        Matrix b = of(new double[][]{{2, 3}});
        Matrix val = a.divi(b);

        assertThat(val, sameInstance(a));
        assertThat(val, matrixCloseTo(new double[][]{
                {0.5, 2.0 / 3},
                {1.5, 4.0 / 3}
        }));
    }

    @Test
    void diviScalar() {
        Matrix a = of(new double[][]{
                {1, 2},
                {3, 4}
        });
        Matrix b = of(2);
        Matrix val = a.divi(b);

        assertThat(val, sameInstance(a));
        assertThat(val, matrixCloseTo(new double[][]{
                {0.5, 1},
                {1.5, 2}
        }));
    }

    @Test
    void expi() {
        Matrix a = of(new double[][]{
                {1, 2},
                {3, 4}
        });

        Matrix val = a.expi();

        assertThat(val, sameInstance(a));
        assertThat(val, matrixCloseTo(new double[][]{
                {Math.exp(1), Math.exp(2)},
                {Math.exp(3), Math.exp(4)}
        }));
    }

    @Test
    void expm1i() {
        Matrix a = of(new double[][]{
                {0, -1},
                {-2, -3}
        });

        Matrix val = a.expm1i();

        assertThat(val, sameInstance(a));
        assertThat(val, matrixCloseTo(new double[][]{
                {Math.expm1(0), Math.expm1(-1)},
                {Math.expm1(-2), Math.expm1(-3)}
        }));
    }

    @Test
    void extractCols() {
        Matrix a = of(new double[][]{
                {1, 2, 3, 4},
                {1, 2, 3, 4}
        });
        Matrix val = a.extractCols(3, 0);

        assertThat(val, matrixCloseTo(new double[][]{
                {4, 1},
                {4, 1}
        }));
    }

    @Test
    void extractColumn() {
        Matrix val = of(new double[][]{
                {1, 2},
                {3, 4}
        }).extractColumn(0);

        assertThat(val, matrixCloseTo(new double[][]{
                {1},
                {3}
        }));
    }

    @Test
    void extractRows() {
        Matrix a = of(new double[][]{
                {1, 2},
                {2, 3},
                {3, 4},
                {4, 5}
        });
        Matrix val = a.extractRows(1, 3);

        assertThat(val, matrixCloseTo(new double[][]{
                {2, 3},
                {4, 5}
        }));
    }

    @Test
    void hstack() {
        Matrix a = of(new double[][]{
                {1, 2},
                {3, 4}
        });
        Matrix val = Matrix.hstack(a, a, a);

        assertThat(val, not(sameInstance(a)));
        assertThat(val, matrixCloseTo(new double[][]{
                {1, 2, 1, 2, 1, 2},
                {3, 4, 3, 4, 3, 4}
        }));
    }

    @Test
    void insert() {
        Matrix a = zeros(2, 2);
        Matrix b = of(new double[][]{
                {1, 2}
        });
        Matrix val = a.insert(b, 1, 0);

        assertThat(val, sameInstance(a));
        assertThat(val, matrixCloseTo(new double[][]{
                {0, 0},
                {1, 2}
        }));
    }

    @Test
    void logi() {
        Matrix a = of(new double[][]{
                {0.1, 1},
                {2, 10}
        });

        Matrix val = a.logi();

        assertThat(val, sameInstance(a));
        assertThat(val, matrixCloseTo(new double[][]{
                {log(0.1), log(1)},
                {log(2), log(10)}
        }));
    }

    @Test
    void lti() {
        Matrix a = of(new double[][]{
                {-1, -0},
                {0, 2}
        });

        Matrix val = a.lti();

        assertThat(val, sameInstance(a));
        assertThat(val, matrixCloseTo(new double[][]{
                {1, 0},
                {0, 0}
        }));
    }

    @Test
    void mapi() {
        Matrix a = of(new double[][]{
                {1, 2},
                {3, 4}
        });
        Matrix val = a.mapi((v, i, j) -> i.equals(j) ? 1.0 : 0.0);

        assertThat(val, sameInstance(a));
        assertThat(val, matrixCloseTo(new double[][]{
                {1, 0},
                {0, 1}
        }));
    }

    @Test
    void mapiCols() {
        Matrix a = of(new double[][]{
                {1, 2, 3},
                {4, 5, 6}
        });
        Matrix b = of(new double[][]{
                {1, 2},
                {3, 4}
        });
        Matrix val = a.mapiCols((v, i, j, k) -> v + b.get(i, j),
                0, 2);

        assertThat(val, sameInstance(a));
        assertThat(val, matrixCloseTo(new double[][]{
                {2, 2, 5},
                {7, 5, 10}
        }));
    }

    @Test
    void mapiMat() {
        Matrix a = of(new double[][]{
                {1, 2, 3},
                {4, 5, 6},
                {7, 8, 9}
        });
        Matrix val = a.mapi(1, 2, 1, 2, (v, i, j) -> i.equals(j) ? v + 1 : v - 1);

        assertThat(val, sameInstance(a));
        assertThat(val, matrixCloseTo(new double[][]{
                {1, 2, 3},
                {4, 6, 5},
                {7, 7, 10}
        }));
    }

    @Test
    void mapiMat1() {
        Matrix a = of(new double[][]{
                {1, 2, 3},
                {4, 5, 6},
                {7, 8, 9}
        });
        Matrix val = a.mapi(1, 2, 1, 2, v -> v + 1);

        assertThat(val, sameInstance(a));
        assertThat(val, matrixCloseTo(new double[][]{
                {1, 2, 3},
                {4, 6, 7},
                {7, 9, 10}
        }));
    }

    @Test
    void mapiRows() {
        Matrix a = of(new double[][]{
                {1, 2},
                {3, 4},
                {5, 6}
        });
        Matrix b = of(new double[][]{
                {1, 2},
                {3, 4}
        });
        Matrix val = a.mapiRows((v, i, j, k) -> v + b.get(i, j),
                0, 2);

        assertThat(val, sameInstance(a));
        assertThat(val, matrixCloseTo(new double[][]{
                {2, 4},
                {3, 4},
                {8, 10}
        }));
    }

    @Test
    void maxi() {
        Matrix a = of(new double[][]{
                {1, 2},
                {3, 4}
        });
        Matrix val = a.maxi(2.5);

        assertThat(val, sameInstance(a));
        assertThat(val, matrixCloseTo(new double[][]{
                {2.5, 2.5},
                {3, 4}
        }));
    }

    @Test
    void minCols() {
        Matrix a = of(new double[][]{
                {1, 4},
                {3, 2}
        });
        Matrix val = a.minCols();

        assertThat(val, not(sameInstance(a)));
        assertThat(val, matrixCloseTo(new double[][]{
                {1, 2}
        }));
    }

    @Test
    void mini() {
        Matrix a = of(new double[][]{
                {1},
                {4}
        });
        Matrix b = of(new double[][]{
                {2},
                {3}
        });
        Matrix val = a.mini(b);

        assertThat(val, sameInstance(a));
        assertThat(val, matrixCloseTo(new double[][]{
                {1},
                {3}
        }));
    }

    @Test
    void miniDouble() {
        Matrix a = of(new double[][]{
                {1, 2},
                {3, 4}
        });
        Matrix val = a.mini(2.5);

        assertThat(val, sameInstance(a));
        assertThat(val, matrixCloseTo(new double[][]{
                {1, 2},
                {2.5, 2.5}
        }));
    }

    @Test
    void muliCol() {
        Matrix a = of(new double[][]{
                {1, 2},
                {3, 4}
        });
        Matrix b = of(new double[][]{
                {2},
                {3}
        });
        Matrix val = a.muli(b);

        assertThat(val, sameInstance(a));
        assertThat(val, matrixCloseTo(new double[][]{
                {2, 4},
                {9, 12}
        }));
    }

    @Test
    void muliDouble() {
        Matrix a = of(new double[][]{
                {1, 2},
                {3, 4}
        });
        Matrix val = a.muli(2);

        assertThat(val, sameInstance(a));
        assertThat(val, matrixCloseTo(new double[][]{
                {2, 4},
                {6, 8}
        }));
    }

    @Test
    void muliMat() {
        Matrix a = of(new double[][]{
                {1, 2},
                {3, 4}
        });
        Matrix b = of(new double[][]{
                {2, 3},
                {4, 5}
        });
        Matrix val = a.muli(b);

        assertThat(val, sameInstance(a));
        assertThat(val, matrixCloseTo(new double[][]{
                {2, 6},
                {12, 20}
        }));
    }

    @Test
    void muliRow() {
        Matrix a = of(new double[][]{
                {1, 2},
                {3, 4}
        });
        Matrix b = of(new double[][]{
                {2, 3}
        });
        Matrix val = a.muli(b);

        assertThat(val, sameInstance(a));
        assertThat(val, matrixCloseTo(new double[][]{
                {2, 6},
                {6, 12}
        }));
    }

    @Test
    void muliScalar() {
        Matrix a = of(new double[][]{
                {1, 2},
                {3, 4}
        });
        Matrix b = of(new double[][]{{2}});
        Matrix val = a.muli(b);

        assertThat(val, sameInstance(a));
        assertThat(val, matrixCloseTo(new double[][]{
                {2, 4},
                {6, 8}
        }));
    }

    @Test
    void negi() {
        Matrix a = of(new double[][]{
                {1, -2},
                {-3, 4}
        });
        Matrix val = a.negi();

        assertThat(val, sameInstance(a));
        assertThat(val, matrixCloseTo(new double[][]{
                {-1, 2},
                {3, -4}
        }));
    }

    @Test
    void ofDouble() {
        Matrix val = of(1);

        assertThat(val, matrixCloseTo(new double[][]{{1}}));
    }

    @Test
    void ofDoubles() {
        Matrix val = of(new double[][]{
                {1, 2},
                {3, 4}
        });

        assertThat(val, matrixCloseTo(new double[][]{
                {1, 2},
                {3, 4}
        }));
    }

    @Test
    void powi() {
        Matrix a = of(new double[][]{
                {1, 2},
                {3, 4}
        });
        Matrix val = a.powi(2);

        assertThat(val, sameInstance(a));
        assertThat(val, matrixCloseTo(new double[][]{
                {1, 4},
                {9, 16}
        }));
    }

    @Test
    void prodcr() {
        Matrix a = of(new double[][]{
                {1, 2}
        });
        Matrix b = of(new double[][]{
                {2},
                {3}
        });
        Matrix val = b.prod(a);

        assertThat(val, not(sameInstance(b)));
        assertThat(val, matrixCloseTo(new double[][]{
                {2, 4},
                {3, 6}
        }));
    }

    @Test
    void prodrc() {
        Matrix a = of(new double[][]{
                {1, 2}
        });
        Matrix b = of(new double[][]{
                {2},
                {3}
        });
        Matrix val = a.prod(b);

        assertThat(val, not(sameInstance(a)));
        assertThat(val, matrixCloseTo(new double[][]{
                {2, 4},
                {3, 6}
        }));
    }

    @Test
    void rowsOf() {
        Matrix a = of(new double[][]{
                {1},
                {2},
                {3},
                {4}
        });

        int[] val = a.cellsOf(x -> x > 2);

        assertThat(val, equalTo(new int[]{2, 3}));
    }

    @Test
    void set() {
        Matrix a = of(new double[][]{
                {1, 2},
                {3, 4}
        });

        Matrix val = a.set(0, 0, 10);

        assertThat(val, sameInstance(a));
        assertThat(val, matrixCloseTo(new double[][]{
                {10, 2},
                {3, 4}
        }));
    }

    @Test
    void softmaxCol() {
        Matrix a = of(log(0.1), log(10));

        Matrix val = a.softmaxi();

        assertThat(val, sameInstance(a));
        assertThat(val, matrixCloseTo(0.1 / 10.1, 10 / 10.1));
    }

    @Test
    void softmaxRow() {
        Matrix a = of(new double[][]{
                {log(0.1), log(10)},
        });

        Matrix val = a.softmaxi();

        assertThat(val, sameInstance(a));
        assertThat(val, matrixCloseTo(new double[][]{
                {0.1 / 10.1, 10 / 10.1}
        }));
    }

    @Test
    void subiCol() {
        Matrix a = of(new double[][]{
                {1, 2},
                {3, 4}
        });
        Matrix b = of(new double[][]{
                {1},
                {2}
        });
        Matrix val = a.subi(b);

        assertThat(val, sameInstance(a));
        assertThat(val, matrixCloseTo(new double[][]{
                {0, 1},
                {1, 2}
        }));
    }

    @Test
    void subiDouble() {
        Matrix a = of(new double[][]{
                {1, 2},
                {3, 4}
        });
        Matrix val = a.subi(2);

        assertThat(val, sameInstance(a));
        assertThat(val, matrixCloseTo(new double[][]{
                {-1, 0},
                {1, 2}
        }));
    }

    @Test
    void subiMat() {
        Matrix a = of(new double[][]{
                {1, 2},
                {3, 4}
        });
        Matrix b = of(new double[][]{
                {1, 3},
                {2, 4}
        });
        Matrix val = a.subi(b);

        assertThat(val, sameInstance(a));
        assertThat(val, matrixCloseTo(new double[][]{
                {0, -1},
                {1, 0}
        }));
    }

    @Test
    void subiRow() {
        Matrix a = of(new double[][]{
                {1, 2},
                {3, 4}
        });
        Matrix b = of(new double[][]{
                {1, 3}
        });
        Matrix val = a.subi(b);

        assertThat(val, sameInstance(a));
        assertThat(val, matrixCloseTo(new double[][]{
                {0, -1},
                {2, 1}
        }));
    }

    @Test
    void subiScalar() {
        Matrix a = of(new double[][]{
                {1, 2},
                {3, 4}
        });
        Matrix b = of(new double[][]{{1}});
        Matrix val = a.subi(b);

        assertThat(val, sameInstance(a));
        assertThat(val, matrixCloseTo(new double[][]{
                {0, 1},
                {2, 3}
        }));
    }

    @Test
    void sumCols() {
        Matrix a = of(new double[][]{
                {1, 2},
                {3, 4}
        });
        Matrix val = a.sumCols();

        assertThat(val, not(sameInstance(a)));
        assertThat(val, matrixCloseTo(new double[][]{
                {4, 6},
        }));
    }

    @Test
    void trasposei() {
        Matrix a = of(new double[][]{
                {1, 2},
                {3, 4}
        });
        Matrix val = a.trasposei();

        assertThat(val, sameInstance(a));
        assertThat(val, matrixCloseTo(new double[][]{
                {1, 3},
                {2, 4}
        }));
    }

    @Test
    void values() {
        Matrix val = Matrix.values(2, 3, 3);

        assertThat(val, matrixCloseTo(new double[][]{
                {3, 3, 3},
                {3, 3, 3}
        }));
    }
}