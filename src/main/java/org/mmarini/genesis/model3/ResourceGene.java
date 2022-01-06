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
 *
 */
public class ResourceGene implements IPGene {
    private final int ref;
    private final Reaction reaction;
    private final double minLevel;
    private final double levelRate;

    /**
     * @param ref       the substance reference
     * @param minLevel  the minimum target level
     * @param levelRate the level rRate
     * @param reaction  the reaction
     */
    public ResourceGene(final int ref,
                        final double minLevel,
                        final double levelRate,
                        final Reaction reaction) {
        this.ref = ref;
        this.reaction = reaction;
        this.minLevel = minLevel;
        this.levelRate = levelRate;
    }

    @Override
    public Population execute(Population population, Matrix signals, double dt, Matrix resources, Matrix areas, Matrix masses) {
        // Compute resource target levels
        final Matrix targetLevels = signals
                .copy()
                .muli(levelRate)
                .expi()
                .muli(minLevel);
        return population.controlResources(dt, targetLevels, this);
    }

    /**
     *
     */
    public double getLevelRate() {
        return levelRate;
    }

    /**
     *
     */
    public double getMinLevel() {
        return minLevel;
    }

    @Override
    public int getNumSignals() {
        return 1;
    }

    /**
     *
     */
    public Reaction getReaction() {
        return reaction;
    }

    /**
     *
     */
    public int getRef() {
        return ref;
    }
}
