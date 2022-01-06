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
import java.util.Objects;

/**
 * The edge of two cells
 */
public class Edge {
    /**
     * Returns an edge between two cell
     *
     * @param from     from cell index
     * @param to       to cell index
     * @param distance distance
     */
    public static Edge create(int from, int to, Point2D distance) {
        return new Edge(from, to, distance);
    }

    private final int from;
    private final int to;
    private final Point2D distance;

    /**
     * @param from     from cell index
     * @param to       to cell index
     * @param distance distance
     */
    public Edge(int from, int to, Point2D distance) {
        assert from < to : String.format("from (%d) must be less then to (%d)");
        this.from = from;
        this.to = to;
        this.distance = distance;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Edge edge = (Edge) o;
        return from == edge.from && to == edge.to && Objects.equals(distance, edge.distance);
    }

    /**
     * Returns the distance vector between cell1 and cell2
     */
    public Point2D getDistance() {
        return distance;
    }

    /**
     * Returns the index of from cell
     */
    public int getFrom() {
        return from;
    }

    /**
     * Returns the index of to cell
     */
    public int getTo() {
        return to;
    }

    @Override
    public int hashCode() {
        return Objects.hash(from, to, distance);
    }

    @Override
    public String toString() {
        return "Edge{" +
                "from=" + from +
                ", to=" + to +
                ", distance=" + distance +
                '}';
    }

}
