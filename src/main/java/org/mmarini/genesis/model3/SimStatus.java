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

import java.util.List;
import java.util.stream.Collectors;

/**
 * The status of simulator is defined by the quantity of substances in the space and
 * the list of populations.
 */
public class SimStatus {
    /**
     * Returns a status
     *
     * @param t           the time
     * @param resources   the substance quantities
     * @param populations the list of populations
     */
    public static SimStatus create(final double t, final Matrix resources, final List<Population> populations) {
        return new SimStatus(t, resources, populations);
    }

    private final Matrix resources;
    private final double t;
    private final List<Population> populations;

    /**
     * Creates a status
     *
     * @param t           the time
     * @param resources   the substance quantities
     * @param populations the list of populations
     */
    protected SimStatus(final double t, final Matrix resources, final List<Population> populations) {
        this.populations = populations;
        this.resources = resources;
        this.t = t;
    }

    /**
     * Returns a deep copy of status
     */
    public SimStatus copy() {
        List<Population> pop = populations.stream().map(Population::copy).collect(Collectors.toList());
        return new SimStatus(t, resources.copy(), pop);
    }

    /**
     * Returns the number of individuals
     */
    public int getIndividualCount() {
        return populations.stream()
                .mapToInt(Population::getIndividualCount)
                .sum();
    }

    /**
     * Returns the populations
     */
    public List<Population> getPopulations() {
        return populations;
    }

    /**
     * Returns the resource quantities for a given cell locations
     *
     * @param locations the locations of individuals
     */
    public Matrix getResources(int... locations) {
        return resources.extractCols(locations);
    }

    /**
     * Returns the resource values for each cell (nr x nc)
     */
    public Matrix getResources() {
        return resources;
    }

    /**
     * Return the simulation time
     */
    public double getT() {
        return t;
    }

    /**
     * Returns the total surface of individuals per cell (1 x noCells)
     *
     * @param numCells number of cells
     * @param masses   the molar masses (noResources x 1)
     */
    public Matrix getTotalIndividualSurface(int numCells, Matrix masses) {
        return populations.stream()
                .map(p -> p.getTotalSurface(masses, numCells))
                .reduce((a, b) -> a.addi(b))
                .orElseThrow();
    }

    /**
     * @param populations the populations
     */
    public SimStatus setPopulation(List<Population> populations) {
        return new SimStatus(t, resources, populations);
    }

    /**
     * @param t the time
     */
    public SimStatus time(double t) {
        return new SimStatus(t, resources, populations);
    }
}
