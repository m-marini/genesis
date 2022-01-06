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

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.requireNonNull;

/**
 * Topology3 define a grid of triangular cells
 * <pre>
 *     x---x---x
 *      \4/5\6/7\
 *       x---x---x
 *      /0\1/2\3/
 *     x---x---x
 *
 *     6---7---8
 *      \ / \ / \
 *       3---4---5
 *      / \ / \ /
 *     0---1---2
 * </pre>
 */
public class Topology3 implements Topology {
    /**
     * Returns the topology
     *
     * @param width  the width of universe in cells
     * @param height the height of universe in cells
     * @param length the length of cell
     */
    public static Topology3 create(int width, int height, double length) {
        assert width % 2 == 0 : "width must be multiple of 2";
        assert height % 2 == 0 : "height must be multiple of 2";
        final Point2D[][] grid = createGrid(width, height, length);
        final Point2D[][] vertices = createVertices(width, height, grid);
        final List<Edge> edges = createEdges(width, height, length);
        final Point2D[] centers = createCenters(width, height, length);
        int[][] adjacents = createAdjacents(width, height);
        return new Topology3(width, height, length, vertices, centers, edges, adjacents);
    }

    /**
     * Returns the adjacents
     *
     * @param width  the width of universe in cells
     * @param height the height of universe in cells
     */
    static int[][] createAdjacents(int width, int height) {
        final int[][] result = new int[width * height][3];
        for (int i = 0; i < height; i += 2) {
            for (int j = 0; j < width; j += 2) {
                int idx0 = index(i, j, width, height);
                result[idx0][0] = index(i, j + 1, width, height);
                result[idx0][1] = index(i - 1, j, width, height);
                result[idx0][2] = index(i, j - 1, width, height);

                int idx1 = index(i, j + 1, width, height);
                result[idx1][0] = index(i, j + 2, width, height);
                result[idx1][1] = index(i, j, width, height);
                result[idx1][2] = index(i + 1, j + 1, width, height);

                int idx2 = index(i + 1, j, width, height);
                result[idx2][0] = index(i + 1, j + 1, width, height);
                result[idx2][1] = index(i + 1, j - 1, width, height);
                result[idx2][2] = index(i + 2, j, width, height);

                int idx3 = index(i + 1, j + 1, width, height);
                result[idx3][0] = index(i + 1, j + 2, width, height);
                result[idx3][1] = index(i, j + 1, width, height);
                result[idx3][2] = index(i + 1, j, width, height);
            }
        }
        return result;
    }

    /**
     * @param width  width
     * @param height height
     * @param length length
     */
    static Point2D[] createCenters(int width, int height, double length) {
        Point2D[] result = new Point2D[width * height];
        int idx = 0;
        double h = length * Math.sin(Math.PI / 3);
        double hy = length * Math.tan(Math.PI / 6) / 2;
        for (int i = 0; i < height; i += 2) {
            for (int j = 0; j < width; j += 2) {
                result[idx++] = new Point2D.Double(length * (j / 2 + 0.5), i * h + hy);
                result[idx++] = new Point2D.Double(length * (j / 2 + 1), i * h + h - hy);
            }
            for (int j = 0; j < width; j += 2) {
                result[idx++] = new Point2D.Double(length * (j / 2 + 0.5), (i + 2) * h - hy);
                result[idx++] = new Point2D.Double(length * (j / 2 + 1), (i + 1) * h + hy);
            }
        }
        return result;
    }

    /**
     * Returns the edges
     *
     * @param width  the width of universe in cells
     * @param height the height of universe in cells
     * @param length the length of cell
     */
    static List<Edge> createEdges(int width, int height, double length) {
        final Point2D r30 = radial(length, Math.PI / 6);
        final Point2D r90 = radial(length, 3 * Math.PI / 6);
        final Point2D r150 = radial(length, 5 * Math.PI / 6);
        final Point2D r210 = radial(length, 7 * Math.PI / 6);
        final Point2D r270 = radial(length, 9 * Math.PI / 6);
        final Point2D r330 = radial(length, 11 * Math.PI / 6);
        final List<Edge> result = new ArrayList<>();
        for (int i = 0; i < height; i += 2) {
            for (int j = 0; j < width; j += 2) {
                int idx = i * width + j;
                result.add(Edge.create(idx, idx + 1, r30));
                if (j == 0) {
                    result.add(Edge.create(idx, idx + width - 1, r150));
                }
                if (i == 0) {
                    result.add(Edge.create(idx, idx + (height - 1) * width, r270));
                }

                if (j < width - 2) {
                    result.add(Edge.create(idx + 1, idx + 2, r330));
                }
                result.add(Edge.create(idx + 1, idx + 1 + width, r90));
            }
            for (int j = 0; j < width; j += 2) {
                int idx = (i + 1) * width + j;
                result.add(Edge.create(idx, idx + 1, r330));
                if (j == 0) {
                    result.add(Edge.create(idx, idx + width - 1, r210));
                }
                if (i < height - 2) {
                    result.add(Edge.create(idx, idx + width, r90));
                }

                if (j < width - 2) {
                    result.add(Edge.create(idx + 1, idx + 2, r30));
                }
            }
        }
        return result;
    }

    /**
     * Returns the grid
     *
     * @param width  the width of universe in cells
     * @param height the height of universe in cells
     * @param length the length of cell
     */
    static Point2D[][] createGrid(int width, int height, double length) {
        final Point2D[][] grid = new Point2D.Double[height + 1][width / 2 + 1];
        final double h = length * Math.sin(Math.PI / 3);
        final double dx = length / 2;
        for (int i = 0; i < height / 2; i++) {
            for (int j = 0; j <= width / 2; j++) {
                grid[i * 2][j] = new Point2D.Double(j * length, i * h * 2);
            }
            for (int j = 0; j <= width / 2; j++) {
                grid[i * 2 + 1][j] = new Point2D.Double(j * length + dx, (i * 2 + 1) * h);
            }
        }
        for (int j = 0; j <= width / 2; j++) {
            grid[height][j] = new Point2D.Double(j * length, height * h);
        }
        return grid;
    }

    /**
     * Returns the vertices per cell
     *
     * @param width  the width of universe in cells
     * @param height the height of universe in cells
     * @param grid   the vertices grid
     */
    public static Point2D[][] createVertices(int width, int height, Point2D[][] grid) {
        final int n = height * width;
        final Point2D[][] vertices = new Point2D[n][];
        int idx = 0;
        for (int i = 0; i < height; i += 2) {
            for (int j = 0; j < width / 2; j++) {
                vertices[idx++] = new Point2D[]{
                        grid[i][j],
                        grid[i + 1][j],
                        grid[i][j + 1]
                };
                vertices[idx++] = new Point2D[]{
                        grid[i][j + 1],
                        grid[i + 1][j],
                        grid[i + 1][j + 1]
                };
            }
            for (int j = 0; j < width / 2; j++) {
                vertices[idx++] = new Point2D[]{
                        grid[i + 1][j],
                        grid[i + 2][j],
                        grid[i + 2][j + 1]
                };
                vertices[idx++] = new Point2D[]{
                        grid[i + 1][j],
                        grid[i + 2][j + 1],
                        grid[i + 1][j + 1]
                };
            }
        }
        return vertices;
    }

    static int index(int i, int j, int width, int height) {
        return ((i + height) % height) * width + ((j + width) % width);
    }

    /**
     * @param l   the distance from origin
     * @param rad the direction in radiant
     */
    public static Point2D radial(double l, double rad) {
        return new Point2D.Double(l * Math.cos(rad), l * Math.sin(rad));
    }

    private final int width;
    private final int height;
    private final double length;
    private final Point2D[][] vertices;
    private final List<Edge> edges;
    private final Rectangle2D.Double bounds;
    private final Point2D[] centers;
    private final int[][] adjacents;

    /**
     * Creates a topology
     *
     * @param width     the width of universe in cells
     * @param height    the height of universe in cells
     * @param length    the length of cell
     * @param vertices  the vertex points
     * @param centers   the centers
     * @param edges     the edges
     * @param adjacents
     */
    protected Topology3(int width, int height, double length, Point2D[][] vertices, Point2D[] centers, List<Edge> edges, int[][] adjacents) {
        this.width = width;
        this.height = height;
        this.length = length;
        this.vertices = vertices;
        this.centers = centers;
        this.edges = edges;
        this.adjacents = adjacents;
        this.bounds = new Rectangle2D.Double(0, 0, (width + 1) * length / 2, height * length * Math.sin(Math.PI / 3));
    }

    @Override
    public Matrix flux(Matrix field, Matrix alpha) {
        requireNonNull(field);
        requireNonNull(alpha);
        assert field.getNumRows() == alpha.getNumRows()
                && field.getNumCols() == alpha.getNumCols();
        Matrix flux = field.createLike();
        for (Edge edge : edges) {
            int from = edge.getFrom();
            int to = edge.getTo();
            Matrix alp = alpha.extractColumn(from).mini(alpha.extractColumn(from));
            Matrix df = field.extractColumn(to)
                    .subi(field.extractColumn(from))
                    .muli(alp);
            flux.assignCol(from, (v, i) ->
                            v + df.get(i, 0)
                    )
                    .assignCol(to, (v, i) ->
                            v - df.get(i, 0)
                    );
        }
        return flux.divi(3 * length);
    }

    @Override
    public int getAdjacent(int cell, int direction) {
        return adjacents[cell][direction];
    }

    @Override
    public Rectangle2D getBounds() {
        return bounds;
    }

    @Override
    public List<Edge> getEdges() {
        return edges;
    }

    /**
     * Returns the height of universe in cells
     */
    public int getHeight() {
        return height;
    }

    @Override
    public Point2D getIncenter(int idx) {
        return centers[idx];
    }

    @Override
    public double getInradius() {
        return length * Math.tan(Math.PI / 6) / 2;
    }

    /**
     * Returns the length of cell
     */
    public double getLength() {
        return length;
    }

    @Override
    public int getNoCells() {
        return width * height;
    }

    @Override
    public Point2D[] getVertices(int idx) {
        return vertices[idx];
    }

    /**
     * Returns the width of universe in cells
     */
    public int getWidth() {
        return width;
    }
}