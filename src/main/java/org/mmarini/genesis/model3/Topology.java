//
// Copyright (c) 2021 Marco Marini, marco.marini@mmarini.org
//
// Permission is hereby granted, free of charge, to any person
// obtaining a copy of this software and associated documentation
// files (the "Software"), to deal in the Software without
// restriction, including without limitation the rights to use,
// copy, modify, merge, publish, distribute, sublicense, and/or sell
// copies of the Software, and to permit persons to whom the
// Software is furnished to do so, subject to the following
// conditions:
//
// The above copyright notice and this permission notice shall be
// included in all copies or substantial portions of the Software.
//
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
// EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
// OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
// NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
// HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
// WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
// FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
// OTHER DEALINGS IN THE SOFTWARE.
//
//   END OF TERMS AND CONDITIONS

package org.mmarini.genesis.model3;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.List;

/**
 *
 */
public interface Topology {

    /**
     * Returns the flux of a field
     * The flux is defined as the integral among a closed surface [f(s0+ds)-f(s0)]/|ds|
     * for the case of substance concentration it defines the difference of
     * substance between the external and the internal volume.
     * If it is positive it means the concentration inside the cell is less than the external.
     * The diffusion of substance is proportional to the negate of flux.
     * dC = - K * flux(C) * dt
     *
     * @param field the field
     * @param alpha the field
     */
    Matrix flux(Matrix field, Matrix alpha);

    /**
     * Returns the cell adjacent to location at direction
     *
     * @param cell      the cell
     * @param direction the direction
     */
    int getAdjacent(int cell, int direction);

    /**
     * Returns the topology bounds
     */
    Rectangle2D getBounds();

    /**
     * Returns the edges
     */
    List<Edge> getEdges();

    /**
     * Returns the center of a cell
     *
     * @param idx cell index
     */
    Point2D getIncenter(int idx);

    /**
     * Returns the inradius
     */
    double getInradius();

    /**
     * Return the length of the grid
     */
    double getLength();

    /**
     * Returns the number of cells
     */
    int getNoCells();

    /**
     * Returns the vertices of a cell
     *
     * @param idx cell index
     */
    Point2D[] getVertices(int idx);
}
