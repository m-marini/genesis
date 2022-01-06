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

import static java.util.Objects.requireNonNull;

/**
 * A chemical reaction transforms the resources in each individual.
 * The reaction is defined by setting the balance of reagent resources
 * and product resources.
 * The speed of reaction depends on the presence of resources (the more resources are
 * present the more resources reacts in the individual).
 * The speed define the quantity of reference resource that reacts in a second for a
 * specific resource.
 * The reference resource should be a product of reaction.
 * The effective reaction speed is the lowest speed value by quantity of resources
 * The reaction can be activated only if a minimal threashold quantity of each single
 * resource is available
 * Only the exceeding resources from the thresholds are involved in the reaction
 */
public class Reaction {
    /**
     * Returns the reaction by defining the reagents, products, thresholds and speeds
     * The threshold values are filtered by reagents map
     *
     * @param reagents   the reagents (nr x 1)
     * @param products   the products (nr x 1)
     * @param thresholds the thresholds (nr x 1)
     * @param speeds     the speed (nr x 1)
     */
    public static Reaction create(final Matrix reagents,
                                  final Matrix products,
                                  final Matrix thresholds,
                                  final Matrix speeds) {
        final Matrix alpha = products.copy().subi(reagents);
        final int[] reagentMap = reagents.cellsOf(x -> x > 0.0);
        final int[] speedMap = speeds.cellsOf(x -> x > 0);

        final Matrix filteredReagents = reagents.extractRows(reagentMap);
        final Matrix filteredThr = thresholds.extractRows(reagentMap);
        final Matrix filteredSpeeds = speeds.extractRows(speedMap);
        return new Reaction(alpha, reagentMap, filteredReagents, filteredThr, speedMap, filteredSpeeds);
    }

    private final Matrix alpha;
    private final int[] reagentMap;
    private final Matrix reagents;
    private final int[] speedMap;
    private final Matrix speeds;
    private final Matrix thresholds;

    /**
     * Creates a reaction
     *
     * @param alpha      the quantities variations of the reaction
     * @param reagentMap the reagents map an array of quantity row indices
     * @param reagents   the reagents
     * @param thresholds the reagent thresholds to activate the reaction
     * @param speedMap   the speed map an array of speed row indices
     * @param speeds     the speeds of reaction by mol of substances
     */
    protected Reaction(final Matrix alpha,
                       final int[] reagentMap, final Matrix reagents,
                       final Matrix thresholds,
                       final int[] speedMap, final Matrix speeds) {
        this.alpha = requireNonNull(alpha);
        this.reagentMap = requireNonNull(reagentMap);
        this.reagents = requireNonNull(reagents);
        this.speedMap = requireNonNull(speedMap);
        this.speeds = requireNonNull(speeds);
        this.thresholds = requireNonNull(thresholds);
    }

    /**
     * Returns the changes of resources by applying a changes of referenced (nr x n)
     *
     * @param ref the reference substance index
     * @param dc  changes of concentration of reference substance (1 x n)
     */
    public Matrix apply(int ref, Matrix dc) {
        requireNonNull(dc);
        Matrix prod = alpha.prod(dc);
        return prod.divi(alpha.get(ref, 0));
    }

    /**
     * Returns the alpha parameters (nr
     */
    public Matrix getAlpha() {
        return alpha;
    }

    /**
     * return
     */
    public Matrix getReagents() {
        return reagents;
    }

    /**
     * Returns the reaction speeds 1/s
     */
    public Matrix getSpeeds() {
        return speeds;
    }

    /**
     * Returns the reaction thresholds
     */
    public Matrix getThresholds() {
        return thresholds;
    }

    /**
     * Returns the maximum reaction products (nr x n) for the given resources distributions,
     * and time interval
     *
     * @param ref       the reference substance
     * @param resources the concentrations (nr x n)
     * @param dt        the time interval
     */
    public Matrix max(int ref, Matrix resources, double dt) {
        requireNonNull(resources);
        // Computes the max reaction product limited by speeds
        Matrix maxBySpeed = resources
                .extractRows(speedMap)
                // computes the max speed resources for each factors
                .muli(speeds)
                .muli(dt)
                // Gets the minimum quantity
                .minCols();

        // Computes the max reaction product limited by resources
        Matrix effectiveReagents = resources
                .extractRows(reagentMap)
                .subi(thresholds)
                .maxi(0.0);
        Matrix maxByResources = effectiveReagents
                .divi(reagents)
                .muli(alpha.get(ref, 0))
                .minCols();

        return maxBySpeed.mini(maxByResources);
    }

    /**
     * Returns the maximum reaction reference resource (1 x n) for a given resources distributions,
     * and time interval limited by a references values
     *
     * @param ref           the reference resource index
     * @param resources     the resources (nr x n)
     * @param maxReferences the limited reference resources (1 x n)
     * @param dt            the time interval
     */
    public Matrix max(int ref, Matrix resources, Matrix maxReferences, double dt) {
        requireNonNull(resources);
        requireNonNull(maxReferences);
        // Computes the max reference resource limited by resource and speed (1 x n)
        Matrix maxBySpeed = resources
                .extractRows(speedMap)
                // computes the max speed resources for each factors
                .muli(speeds)
                .muli(dt)
                // Gets the minimum quantity
                .minCols();

        // Computes the max reference resource limited by thresholds (1 x n)
        Matrix effectiveReagents = resources
                .extractRows(reagentMap)
                .subi(thresholds)
                .maxi(0.0);
        Matrix maxByResources = effectiveReagents
                .divi(reagents)
                .muli(alpha.get(ref, 0))
                .minCols();

        // Computes the resulting reference resource
        return maxBySpeed.mini(maxByResources).mini(maxReferences);
    }
}
