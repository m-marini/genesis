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

/**
 * The individual in a cell index location,
 * with resources, photo reaction target levels,
 * reaction target levels
 */
public class Individual {
    /**
     * Returns an individual
     *
     * @param location             the location cell index
     * @param resources            the resources (noResources x 1)
     * @param photoTargetLevels    the photo target levels (noGenes x 1)
     * @param reactionTargetLevels the reaction target parameter
     */
    public static Individual create(int location, Matrix resources, Matrix photoTargetLevels, Matrix reactionTargetLevels) {
        return new Individual(location, resources, photoTargetLevels, reactionTargetLevels);
    }

    private final int location;
    private final Matrix resources;
    private final Matrix photoTargetLevels;
    private final Matrix reactionTargetLevels;

    /**
     * Creates an individual
     *
     * @param location             the location cell index
     * @param resources            the resources (noResources x 1)
     * @param photoTargetLevels    the photo target levels (noGenes x 1)
     * @param reactionTargetLevels the reaction target parameter
     */
    protected Individual(int location, Matrix resources, Matrix photoTargetLevels, Matrix reactionTargetLevels) {
        this.location = location;
        this.resources = resources;
        this.photoTargetLevels = photoTargetLevels;
        this.reactionTargetLevels = reactionTargetLevels;
    }

    /**
     * Returns the location cell index
     */
    public int getLocation() {
        return location;
    }

    /**
     * Returns the photo target levels (noGenes x 1)
     */
    public Matrix getPhotoTargetLevels() {
        return photoTargetLevels;
    }

    /**
     * Returns the reaction target levels (noGenes x 1)
     */
    public Matrix getReactionTargetLevels() {
        return reactionTargetLevels;
    }

    /**
     * Returns the resources (noResources x 1)
     */
    public Matrix getResources() {
        return resources;
    }
}
