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

import static java.lang.Math.log;
import static java.lang.String.format;
import static java.util.Objects.requireNonNull;

/**
 * The reaction process transforms the resources by chemical reaction.
 * The reaction is activated only if the target resource has less than
 * the expected target level controlled by the genes.
 */
public class ReactionProcess {
    public static ReactionProcess create(final int ref,
                                         final double minLevel,
                                         final double maxLevel,
                                         final Reaction reaction) {
        return new ReactionProcess(ref, minLevel, maxLevel, reaction);
    }

    private final int ref;
    private final double minLevel;
    private final double maxLevel;
    private final Reaction reaction;

    /**
     * @param ref      the substance reference
     * @param minLevel the minimum target level
     * @param maxLevel the maximum target level
     * @param reaction the reaction
     */
    protected ReactionProcess(int ref,
                              double minLevel,
                              double maxLevel,
                              Reaction reaction) {
        this.ref = ref;
        this.minLevel = minLevel;
        this.maxLevel = maxLevel;
        this.reaction = requireNonNull(reaction);
    }

    /**
     * Returns the resource changes for each individual (noResources x noIndividuals)
     *
     * @param resources    the resources by individuals (noResources x noIndividuals)
     * @param targetLevels the reference resource target level by individual (1 x noIndividuals)
     * @param dt           the time interval
     */
    public Matrix computeChanges(Matrix resources, Matrix targetLevels, double dt) {
        requireNonNull(resources);
        requireNonNull(targetLevels);
        // Computes the current reference levels (1 x noIndividuals)
        Matrix currentRefLevels = resources.extractRow(ref);
        // Computes the maximum reference resource changes (1 x noIndividuals)
        // max(target - currentLevel, 0)
        Matrix maxDelta = currentRefLevels.subi(targetLevels).negi().maxi(0);
        // Compute the max target by reaction (1 x noIndividuals)
        Matrix maxReact = reaction.max(ref, resources, dt);
        // Compute the delta on reference substance (1 x noIndividuals)
        Matrix deltaRef = maxDelta.mini(maxReact);
        // Compute the delta quantities for individuals (noResources x noIndividuals)
        return reaction.apply(ref, deltaRef);
    }

    /**
     * Returns the signals for each individuals (1 x noIndividuals)
     *
     * @param levels the levels for each individual (1 x noIndividuals)
     */
    public Matrix createSignals(Matrix levels) {
        requireNonNull(levels);
        assert levels.getNumRows() == 1
                : format("signals must be (1 x n) (%d x %d)",
                levels.getNumRows(), levels.getNumCols());

        return levels.copy().divi(minLevel).logi().divi(maxLevel / minLevel);
    }

    /**
     * Returns the levels for each individual (1 x noIndividuals)
     *
     * @param signals the signals for each individual (1 x noIndividuals)
     */
    public Matrix createTargetLevels(Matrix signals) {
        requireNonNull(signals);
        assert signals.getNumRows() == 1
                : format("signals must be (1 x n) (%d x %d)",
                signals.getNumRows(), signals.getNumCols());

        return signals.copy().muli(log(maxLevel / minLevel)).expi().muli(minLevel);

    }

    /**
     * Returns the maximum target level
     */
    public double getMaxLevel() {
        return maxLevel;
    }

    /**
     * Returns the minimum target level
     */
    public double getMinLevel() {
        return minLevel;
    }

    /**
     * Returns the reaction
     */
    public Reaction getReaction() {
        return reaction;
    }

    /**
     * Returns the product resource index
     */
    public int getRef() {
        return ref;
    }
}
