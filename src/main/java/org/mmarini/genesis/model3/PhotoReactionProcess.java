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
 * The photo reaction process transforms the resources in the individual by consumption of photo energy
 * applying a chemical reaction limited by the light energy available.
 * The process produces the reference resource to reach a target level of resources for each individual.
 * The generation of resource is limited by a speed (resource units/sec) distributed over the whole populations
 * by individual surfaces.
 * The signals of the genes is exponential mapped to the target level in the range of minimum and maximum
 */
public class PhotoReactionProcess {
    /**
     * Returns a photo reaction
     *
     * @param ref      reference resource index
     * @param speed    the reference resource production speed (resource units / sec)
     * @param minLevel the minimum target level
     * @param maxLevel the maximum target level
     * @param reaction the chemical reaction
     */
    public static PhotoReactionProcess create(int ref, double speed, double minLevel, double maxLevel, Reaction reaction) {
        return new PhotoReactionProcess(ref, speed, minLevel, maxLevel, reaction);
    }

    private final int ref;
    private final double speed;
    private final double minLevel;
    private final double maxLevel;
    private final Reaction reaction;

    /**
     * Create a photo reaction
     *
     * @param ref      the reference resource index
     * @param speed    the reference resource production speed (resource units / sec)
     * @param minLevel the minimum target level
     * @param maxLevel the maximum target level
     * @param reaction the chemical reaction
     */
    protected PhotoReactionProcess(int ref, double speed, double minLevel, double maxLevel, Reaction reaction) {
        this.ref = ref;
        this.speed = speed;
        this.minLevel = minLevel;
        this.maxLevel = maxLevel;
        this.reaction = requireNonNull(reaction);
        assert maxLevel > minLevel
                : format("minLevel (%s) must be < maxLevel (%s)",
                minLevel, maxLevel);
        assert speed > 0
                : format("speed must be > 0 (%s)",
                speed);
    }

    public int getRef() {
        return ref;
    }

    public double getSpeed() {
        return speed;
    }

    public double getMinLevel() {
        return minLevel;
    }

    public double getMaxLevel() {
        return maxLevel;
    }

    public Reaction getReaction() {
        return reaction;
    }

    /**
     * Returns the changes of resources for each individual (noResources x noIndividuals)
     *
     * @param resources    the resources by individuals (noResources x noIndividuals)
     * @param targetLevel  the reference resource target level by individual (1 x noIndividuals)
     * @param dt           the time interval
     * @param distribution the distribution of speed by individual (1 x noIndividuals)
     */
    public Matrix computeChanges(Matrix resources, Matrix targetLevel, double dt, Matrix distribution) {
        requireNonNull(resources);
        requireNonNull(targetLevel);
        requireNonNull(distribution);
        assert targetLevel.getNumRows() == 1
                : format("targetLevel must be 1 x n (%d x %d)",
                targetLevel.getNumRows(), targetLevel.getNumCols());
        assert distribution.getNumRows() == 1
                : format("distribution must be 1 x n (%d x %d)",
                distribution.getNumRows(), distribution.getNumCols());
        // Computes the max resources limited by resource flux availability  (1 x noIndividuals)
        Matrix maxResourcesForSpeed = distribution.copy().muli(speed).muli(dt);
        // Computes the need for reference resource (1 x noIndividuals)
        Matrix maxResourceNeed = resources.extractRow(ref)
                .subi(targetLevel)
                .negi()
                .maxi(0)
                .mini(maxResourcesForSpeed);
        // Computes the reference resource changes limited by reaction (1 x noIndividuals)
        Matrix resourceToProduce = reaction.max(ref, resources, maxResourceNeed, dt);
        // Compute the resource changes
        return reaction.apply(ref, resourceToProduce);
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
}
