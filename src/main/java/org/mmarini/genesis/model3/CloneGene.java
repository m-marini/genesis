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

import java.util.Random;

import static org.mmarini.genesis.model3.Matrix.of;

/**
 *
 */
public class CloneGene implements PIPGene {

    private static final int MASS_THRESHOLD_IDX = 0;
    private static final int ENERGY_THRESHOLD_IDX = 1;
    private static final int MASS_PROB_RATE_IDX = 2;
    private static final int ENERGY_PROB_RATE_IDX = 3;

    /**
     * @param minMassThreshold     minimum mass threshold
     * @param maxMassThreshold     maximum mass threshold
     * @param minEnergyThreshold   minimum energy threshold
     * @param maxEnergyThreshold   maximum energy threshold
     * @param minMassProbability   minimum mass probability speed
     * @param maxMassProbability   maximum mass probability speed
     * @param minEnergyProbability minimum energy probability speed
     * @param maxEnergyProbability maximum energy probability speed
     * @param energyRef            energy resource index
     * @param inPlacePreference    clone in-place location preference
     * @param adjacentPreference   clone adjacent location preference
     * @param mutationProb         mutation probability
     * @param mutationSigma        mutation sigma
     */
    public static CloneGene create(final double minMassThreshold,
                                   final double maxMassThreshold,
                                   final double minEnergyThreshold,
                                   final double maxEnergyThreshold,
                                   final double minMassProbability,
                                   final double maxMassProbability,
                                   final double minEnergyProbability,
                                   final double maxEnergyProbability,
                                   final int energyRef,
                                   final double inPlacePreference,
                                   final double adjacentPreference,
                                   final double mutationProb,
                                   final double mutationSigma) {
        final Matrix minLevels = of(
                minMassThreshold,
                minEnergyThreshold,
                minMassProbability,
                minEnergyProbability
        );

        final Matrix maxLevels = of(
                maxMassThreshold,
                maxEnergyThreshold,
                maxMassProbability,
                maxEnergyProbability
        );
        final Matrix locationProb = of(
                inPlacePreference,
                adjacentPreference,
                adjacentPreference,
                adjacentPreference
        ).softmaxi().cdfiRows();
        return create(minLevels, maxLevels, energyRef,
                locationProb, mutationProb, mutationSigma);
    }

    /**
     * @param minLevels     minimum level of signals (4 x 1)
     * @param maxLevels     maximum level of signals (4 x 1)
     * @param energyRef     energy resource index
     * @param locationProb  location probabilities (in-place, 3 x adjacent) (4 x 1)
     * @param mutationProb  mutation probability
     * @param mutationSigma mutation sigma
     */
    public static CloneGene create(final Matrix minLevels,
                                   final Matrix maxLevels,
                                   final int energyRef,
                                   final Matrix locationProb,
                                   final double mutationProb,
                                   final double mutationSigma) {
        return new CloneGene(minLevels,
                maxLevels.copy().divi(minLevels).logi(),
                energyRef,
                locationProb, mutationProb, mutationSigma);
    }

    private final Matrix minLevels;
    private final Matrix levelRates;
    private final int energyRef;
    private final Matrix locationProb;
    private final double mutationProb;
    private final double mutationSigma;

    /**
     * @param minLevels     minimum level of signals (4 x 1)
     * @param levelRates    rates of signals (4 x 1)
     * @param energyRef     energy resource index
     * @param locationProb  location probabilities  (4 x 1)
     * @param mutationProb  mutation probability
     * @param mutationSigma mutation sigma
     */
    public CloneGene(final Matrix minLevels, final Matrix levelRates, final int energyRef, Matrix locationProb, double mutationProb, double mutationSigma) {
        this.minLevels = minLevels;
        this.levelRates = levelRates;
        this.energyRef = energyRef;
        this.locationProb = locationProb;
        this.mutationProb = mutationProb;
        this.mutationSigma = mutationSigma;
    }

    @Override
    public Population execute(final Population population,
                              final Matrix signals,
                              final double dt,
                              final Matrix molecularMasses,
                              final Topology topology,
                              final Random random) {
        // Computes the actual signal levels
        final Matrix values = signals.copy().muli(levelRates).expi().muli(minLevels);
        // Extracts the mass thresholds
        final Matrix massThs = values.extractRow(MASS_THRESHOLD_IDX);
        // Extracts the energy thresholds
        final Matrix energyThs = values.extractRows(ENERGY_THRESHOLD_IDX);
        // Extracts the mass thresholds
        final Matrix massCloneProbRate = values.extractRow(MASS_PROB_RATE_IDX);
        // Extracts the energy thresholds
        final Matrix energyCloneProbRate = values.extractRows(ENERGY_PROB_RATE_IDX);
        return population.performClone(dt, molecularMasses, topology, random,
                massThs, energyThs, massCloneProbRate, energyCloneProbRate,
                this);
    }

    /**
     *
     */
    public int getEnergyRef() {
        return energyRef;
    }

    /**
     *
     */
    public Matrix getLevelRates() {
        return levelRates;
    }

    /**
     *
     */
    public Matrix getLocationProb() {
        return locationProb;
    }

    /**
     *
     */
    public Matrix getMinLevels() {
        return minLevels;
    }

    /**
     *
     */
    public double getMutationProb() {
        return mutationProb;
    }

    /**
     *
     */
    public double getMutationSigma() {
        return mutationSigma;
    }

    @Override
    public int getNumSignals() {
        return 4;
    }
}
