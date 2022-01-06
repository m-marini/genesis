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
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.List;
import java.util.stream.Stream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mmarini.genesis.model3.Matrix.ones;
import static org.mmarini.genesis.model3.Matrix.values;
import static org.mmarini.genesis.model3.MatrixMatchers.matrixCloseTo;
import static org.mmarini.genesis.model3.Topology3.radial;

/**
 * <pre>
 *     x---x---x
 *      \2/3\4/5\
 *       x---x---x
 *      /8\9/0\1/
 *     x---x---x
 *      \4/5\6/7\
 *       x---x---x
 *      /0\1/2\3/
 *     x---x---x
 * * </pre>
 */
class Topology3Test {

    public static final int NUM_TESTS = 10;
    public static final double MIN_VALUE = 5.0;
    public static final double MAX_VALUE = 15.0;
    private static final Point2D R30 = radial(2, Math.PI / 6);
    private static final Point2D R90 = radial(2, 3 * Math.PI / 6);
    private static final Point2D R150 = radial(2, 5 * Math.PI / 6);
    private static final Point2D R210 = radial(2, 7 * Math.PI / 6);
    private static final Point2D R270 = radial(2, 9 * Math.PI / 6);
    private static final Point2D R330 = radial(2, 11 * Math.PI / 6);

    static Stream<Arguments> fluxDataTest() {
        return ArgumentGenerator.create(NUM_TESTS)
                .exponential(MIN_VALUE, MAX_VALUE)
                .exponential(MIN_VALUE, MAX_VALUE)
                .exponential(MIN_VALUE, MAX_VALUE)
                .exponential(MIN_VALUE, MAX_VALUE)
                .generate();
    }

    @Test
    void create() {
        Topology3 top = Topology3.create(4, 4, 2);
        assertThat(top, hasProperty("length", equalTo(2.0)));
        assertThat(top, hasProperty("width", equalTo(4)));
        assertThat(top, hasProperty("height", equalTo(4)));
        assertThat(top, hasProperty("noCells", equalTo(16)));
        assertThat(top, hasProperty("bounds", equalTo(new Rectangle2D.Double(0, 0, 4.0 * 2 / 2 + 1, 4 * 2 * Math.sqrt(3) / 2))));
    }

    /**
     * <pre>
     * x---x---x
     *  \4/5\6/7\
     *   x---x---x
     *  /0\1/2\3/
     * x---x---x
     * </pre>
     */
    @ParameterizedTest
    @MethodSource("fluxDataTest")
    void flux1(double value00, double value01, double value10, double value11) {
        Topology3 top = Topology3.create(4, 2, 2);

        Matrix field = values(2, top.getNoCells(), value00)
                .set(0, 0, value01)
                .assignRow(1, (v, i) -> value10)
                .set(1, 0, value11);

        Matrix alpha = ones(2, top.getNoCells());

        Matrix flux = top.flux(field, alpha);

        double FLUX0_0 = (value00 - value01) / 2;
        double FLUX0_134 = (value01 - value00) / 6.0;
        double FLUX1_0 = (value10 - value11) / 2;
        double FLUX1_134 = (value11 - value10) / 6.0;

        assertThat(flux, matrixCloseTo(new double[][]{{
                FLUX0_0, FLUX0_134, 0.0, FLUX0_134, FLUX0_134, 0.0, 0.0, 0.0
        }, {
                FLUX1_0, FLUX1_134, 0.0, FLUX1_134, FLUX1_134, 0.0, 0.0, 0.0
        }}));
    }

    @ParameterizedTest
    @MethodSource("fluxDataTest")
    void flux2(double value00, double value01, double value10, double value11) {
        Topology3 top = Topology3.create(4, 2, 2);

        Matrix field = values(2, top.getNoCells(), value00)
                .set(0, 0, value01)
                .assignRow(1, (v, i) -> value10)
                .set(1, 0, value11);

        Matrix alpha = ones(2, top.getNoCells())
                .set(0, 0, 0);

        Matrix flux = top.flux(field, alpha);

        double FLUX1_0 = (value10 - value11) / 2;
        double FLUX1_134 = (value11 - value10) / 6.0;

        assertThat(flux, matrixCloseTo(new double[][]{{
                0, 0, 0.0, 0, 0, 0.0, 0.0, 0.0
        }, {
                FLUX1_0, FLUX1_134, 0.0, FLUX1_134, FLUX1_134, 0.0, 0.0, 0.0
        }}));
    }

    /**
     * <pre>
     *     x---x---x
     *      \2/3\4/5\
     *       x---x---x
     *      /8\9/0\1/
     *     x---x---x
     *      \4/5\6/7\
     *       x---x---x
     *      /0\1/2\3/
     *     x---x---x
     * </pre>
     */
    @Test
    void getAdjacents() {
        Topology3 top = Topology3.create(4, 4, 2);

        assertThat(top.getAdjacent(0, 0), equalTo(1));
        assertThat(top.getAdjacent(0, 1), equalTo(12));
        assertThat(top.getAdjacent(0, 2), equalTo(3));

        assertThat(top.getAdjacent(1, 0), equalTo(2));
        assertThat(top.getAdjacent(1, 1), equalTo(0));
        assertThat(top.getAdjacent(1, 2), equalTo(5));

        assertThat(top.getAdjacent(2, 0), equalTo(3));
        assertThat(top.getAdjacent(2, 1), equalTo(14));
        assertThat(top.getAdjacent(2, 2), equalTo(1));

        assertThat(top.getAdjacent(3, 0), equalTo(0));
        assertThat(top.getAdjacent(3, 1), equalTo(2));
        assertThat(top.getAdjacent(3, 2), equalTo(7));

        assertThat(top.getAdjacent(4, 0), equalTo(5));
        assertThat(top.getAdjacent(4, 1), equalTo(7));
        assertThat(top.getAdjacent(4, 2), equalTo(8));

        assertThat(top.getAdjacent(5, 0), equalTo(6));
        assertThat(top.getAdjacent(5, 1), equalTo(1));
        assertThat(top.getAdjacent(5, 2), equalTo(4));

        assertThat(top.getAdjacent(6, 0), equalTo(7));
        assertThat(top.getAdjacent(6, 1), equalTo(5));
        assertThat(top.getAdjacent(6, 2), equalTo(10));

        assertThat(top.getAdjacent(7, 0), equalTo(4));
        assertThat(top.getAdjacent(7, 1), equalTo(3));
        assertThat(top.getAdjacent(7, 2), equalTo(6));

        assertThat(top.getAdjacent(8, 0), equalTo(9));
        assertThat(top.getAdjacent(8, 1), equalTo(4));
        assertThat(top.getAdjacent(8, 2), equalTo(11));

        assertThat(top.getAdjacent(9, 0), equalTo(10));
        assertThat(top.getAdjacent(9, 1), equalTo(8));
        assertThat(top.getAdjacent(9, 2), equalTo(13));

        assertThat(top.getAdjacent(10, 0), equalTo(11));
        assertThat(top.getAdjacent(10, 1), equalTo(6));
        assertThat(top.getAdjacent(10, 2), equalTo(9));

        assertThat(top.getAdjacent(11, 0), equalTo(8));
        assertThat(top.getAdjacent(11, 1), equalTo(10));
        assertThat(top.getAdjacent(11, 2), equalTo(15));

        assertThat(top.getAdjacent(12, 0), equalTo(13));
        assertThat(top.getAdjacent(12, 1), equalTo(15));
        assertThat(top.getAdjacent(12, 2), equalTo(0));

        assertThat(top.getAdjacent(13, 0), equalTo(14));
        assertThat(top.getAdjacent(13, 1), equalTo(9));
        assertThat(top.getAdjacent(13, 2), equalTo(12));

        assertThat(top.getAdjacent(14, 0), equalTo(15));
        assertThat(top.getAdjacent(14, 1), equalTo(13));
        assertThat(top.getAdjacent(14, 2), equalTo(2));

        assertThat(top.getAdjacent(15, 0), equalTo(12));
        assertThat(top.getAdjacent(15, 1), equalTo(11));
        assertThat(top.getAdjacent(15, 2), equalTo(14));
    }

    @Test
    void getCenters() {
        Topology3 top = Topology3.create(4, 4, 2);
        final double hy = Math.tan(Math.PI / 6);
        final double h = Math.sin(Math.PI / 3) * 2;

        assertThat(top.getIncenter(0), equalTo(
                new Point2D.Double(1, hy)));
        assertThat(top.getIncenter(1), equalTo(
                new Point2D.Double(2, h - hy)));
        assertThat(top.getIncenter(2), equalTo(
                new Point2D.Double(3, hy)));
        assertThat(top.getIncenter(3), equalTo(
                new Point2D.Double(4, h - hy)));

        assertThat(top.getIncenter(4), equalTo(
                new Point2D.Double(1, 2 * h - hy)));
        assertThat(top.getIncenter(5), equalTo(
                new Point2D.Double(2, h + hy)));
        assertThat(top.getIncenter(6), equalTo(
                new Point2D.Double(3, 2 * h - hy)));
        assertThat(top.getIncenter(7), equalTo(
                new Point2D.Double(4, h + hy)));

        //------------------------
        assertThat(top.getIncenter(8), equalTo(
                new Point2D.Double(1, 2 * h + hy)));
        assertThat(top.getIncenter(9), equalTo(
                new Point2D.Double(2, 2 * h + h - hy)));
        assertThat(top.getIncenter(10), equalTo(
                new Point2D.Double(3, 2 * h + hy)));
        assertThat(top.getIncenter(11), equalTo(
                new Point2D.Double(4, 2 * h + h - hy)));

        assertThat(top.getIncenter(12), equalTo(
                new Point2D.Double(1, 2 * h + 2 * h - hy)));
        assertThat(top.getIncenter(13), equalTo(
                new Point2D.Double(2, 2 * h + h + hy)));
        assertThat(top.getIncenter(14), equalTo(
                new Point2D.Double(3, 2 * h + 2 * h - hy)));
        assertThat(top.getIncenter(15), equalTo(
                new Point2D.Double(4, 2 * h + h + hy)));
    }

    @Test
    void getEdges() {
        final Topology3 top = Topology3.create(4, 4, 2);
        final List<Edge> edges = top.getEdges();

        assertThat(edges, hasSize(16 * 3 / 2));

        assertThat(edges.get(0), equalTo(new Edge(0, 1, R30)));
        assertThat(edges.get(1), equalTo(new Edge(0, 3, R150)));
        assertThat(edges.get(2), equalTo(new Edge(0, 12, R270)));

        assertThat(edges.get(3), equalTo(new Edge(1, 2, R330)));
        assertThat(edges.get(4), equalTo(new Edge(1, 5, R90)));

        assertThat(edges.get(5), equalTo(new Edge(2, 3, R30)));
        assertThat(edges.get(6), equalTo(new Edge(2, 14, R270)));

        assertThat(edges.get(7), equalTo(new Edge(3, 7, R90)));

        assertThat(edges.get(8), equalTo(new Edge(4, 5, R330)));
        assertThat(edges.get(9), equalTo(new Edge(4, 7, R210)));
        assertThat(edges.get(10), equalTo(new Edge(4, 8, R90)));

        assertThat(edges.get(11), equalTo(new Edge(5, 6, R30)));

        assertThat(edges.get(12), equalTo(new Edge(6, 7, R330)));
        assertThat(edges.get(13), equalTo(new Edge(6, 10, R90)));

        assertThat(edges.get(14), equalTo(new Edge(8, 9, R30)));
        assertThat(edges.get(15), equalTo(new Edge(8, 11, R150)));

        assertThat(edges.get(16), equalTo(new Edge(9, 10, R330)));
        assertThat(edges.get(17), equalTo(new Edge(9, 13, R90)));

        assertThat(edges.get(18), equalTo(new Edge(10, 11, R30)));

        assertThat(edges.get(19), equalTo(new Edge(11, 15, R90)));

        assertThat(edges.get(20), equalTo(new Edge(12, 13, R330)));
        assertThat(edges.get(21), equalTo(new Edge(12, 15, R210)));

        assertThat(edges.get(22), equalTo(new Edge(13, 14, R30)));

        assertThat(edges.get(23), equalTo(new Edge(14, 15, R330)));
    }

    @Test
    void getVertices() {
        Topology3 top = Topology3.create(4, 4, 2);
        final double h = Math.sin(Math.PI / 3) * 2;

        assertThat(top.getVertices(0), arrayContaining(
                new Point2D.Double(),
                new Point2D.Double(1, h),
                new Point2D.Double(2, 0)));

        assertThat(top.getVertices(1), arrayContaining(
                new Point2D.Double(2, 0),
                new Point2D.Double(1, h),
                new Point2D.Double(3, h)));

        assertThat(top.getVertices(2), arrayContaining(
                new Point2D.Double(2, 0),
                new Point2D.Double(3, h),
                new Point2D.Double(4, 0)));

        assertThat(top.getVertices(3), arrayContaining(
                new Point2D.Double(4, 0),
                new Point2D.Double(3, h),
                new Point2D.Double(5, h)));

        assertThat(top.getVertices(4), arrayContaining(
                new Point2D.Double(1, h),
                new Point2D.Double(0, h * 2),
                new Point2D.Double(2, h * 2)));

        assertThat(top.getVertices(5), arrayContaining(
                new Point2D.Double(1, h),
                new Point2D.Double(2, h * 2),
                new Point2D.Double(3, h)));

        assertThat(top.getVertices(6), arrayContaining(
                new Point2D.Double(3, h),
                new Point2D.Double(2, h * 2),
                new Point2D.Double(4, h * 2)));

        assertThat(top.getVertices(7), arrayContaining(
                new Point2D.Double(3, h),
                new Point2D.Double(4, h * 2),
                new Point2D.Double(5, h)));

        //--------------------------------

        assertThat(top.getVertices(8), arrayContaining(
                new Point2D.Double(0, 2 * h + 0),
                new Point2D.Double(1, 2 * h + h),
                new Point2D.Double(2, 2 * h + 0)));

        assertThat(top.getVertices(9), arrayContaining(
                new Point2D.Double(2, 2 * h + 0),
                new Point2D.Double(1, 2 * h + h),
                new Point2D.Double(3, 2 * h + h)));

        assertThat(top.getVertices(10), arrayContaining(
                new Point2D.Double(2, 2 * h + 0),
                new Point2D.Double(3, 2 * h + h),
                new Point2D.Double(4, 2 * h + 0)));

        assertThat(top.getVertices(11), arrayContaining(
                new Point2D.Double(4, 2 * h + 0),
                new Point2D.Double(3, 2 * h + h),
                new Point2D.Double(5, 2 * h + h)));

        assertThat(top.getVertices(12), arrayContaining(
                new Point2D.Double(1, 2 * h + h),
                new Point2D.Double(0, 2 * h + h * 2),
                new Point2D.Double(2, 2 * h + h * 2)));

        assertThat(top.getVertices(13), arrayContaining(
                new Point2D.Double(1, 2 * h + h),
                new Point2D.Double(2, 2 * h + h * 2),
                new Point2D.Double(3, 2 * h + h)));

        assertThat(top.getVertices(14), arrayContaining(
                new Point2D.Double(3, 2 * h + h),
                new Point2D.Double(2, 2 * h + h * 2),
                new Point2D.Double(4, 2 * h + h * 2)));

        assertThat(top.getVertices(15), arrayContaining(
                new Point2D.Double(3, 2 * h + h),
                new Point2D.Double(4, 2 * h + h * 2),
                new Point2D.Double(5, 2 * h + h)));
    }

}
