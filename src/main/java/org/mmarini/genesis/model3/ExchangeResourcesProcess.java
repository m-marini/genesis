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

import static java.lang.String.format;
import static java.util.Objects.requireNonNull;

/**
 * The exchange resource gene controls the quantities of resource exchanged with the environment
 * It has a signal for each resource that controls the target level of resources.
 * If the resources present in the individual exceed the target it releases the resource to the environment
 * If the resources present in the individual is less than the target it absorbs the resource from the environment
 */
public class ExchangeResourcesProcess {
    /**
     * Returns an exchange resource process
     *
     * @param minLevels minimum levels (noResources x 1)
     * @param maxLevel  log rate levels (noResources x 1)
     * @param rates     exchange rates in unit/sec (noResources x 1)
     */
    public static ExchangeResourcesProcess create(final Matrix minLevels,
                                                  final Matrix maxLevel,
                                                  final Matrix rates) {
        return new ExchangeResourcesProcess(minLevels, maxLevel, rates);
    }

    private final Matrix minLevels;
    private final Matrix maxLevel;
    private final Matrix rates;

    /**
     * Creates an exchange resource process
     *
     * @param minLevels minimum levels (noResources x 1)
     * @param maxLevel  log rate levels (noResources x 1)
     * @param rates     exchange rates in unit/sec (noResources x 1)
     */
    protected ExchangeResourcesProcess(final Matrix minLevels,
                                       final Matrix maxLevel,
                                       final Matrix rates) {
        this.minLevels = requireNonNull(minLevels);
        this.maxLevel = requireNonNull(maxLevel);
        this.rates = requireNonNull(rates);
        assert minLevels.getNumCols() == 1
                : format("# cols of minLevel (%d) must be 1",
                minLevels.getNumCols());
        assert maxLevel.getNumCols() == 1
                : format("# cols of maxLevel (%d) must be 1",
                maxLevel.getNumCols());
        assert rates.getNumCols() == 1
                : format("# cols of rates (%d) must be 1",
                rates.getNumCols());
        assert minLevels.getNumRows() == maxLevel.getNumRows()
                : format("# rows of minLevel (%d) must be equal to # rows of maxLevel (%d)",
                minLevels.getNumRows(),
                maxLevel.getNumRows());
        assert minLevels.getNumRows() == rates.getNumRows()
                : format("# rows of minLevel (%d) must be equal to # rows of rates (%d)",
                minLevels.getNumRows(),
                rates.getNumRows());
    }

    /**
     * Returns the resources exchanges
     *
     * @param environment  the environment resources (noResources x noCells)
     * @param individuals  the individuals resources (noResources x noIndividuals)
     * @param locations    the locations of individuals (noIndividuals)
     * @param targetLevels the target levels of individual resources  (noResources x noIndividuals)
     * @param dt           the time interval
     * @param distribution the distribution of environment resources by area (noResources x noIndividuals)
     */
    public ExchangeResources computeChanges(Matrix environment,
                                            Matrix individuals,
                                            int[] locations,
                                            Matrix targetLevels,
                                            double dt,
                                            Matrix distribution) {

        // Computes the maximum individual resource changes (nr x ni):
        // environmentResources[location] * distribution,
        final Matrix maxIndExchanges = environment.extractCols(locations)
                .muli(distribution);
        // Computes the resource changes by individual:
        // min(resourceTargets - quantities, qa) * rates * dt
        final Matrix dIndResources = targetLevels.copy().subi(individuals)
                .mini(maxIndExchanges)
                .muli(rates)
                .muli(dt);
        Matrix dEnvResources = environment.createLike()
                .mapiCols((v, i, j, k) -> dIndResources.get(i, j), locations);
        return ExchangeResources.of(dEnvResources, dIndResources);
    }

    /**
     * Returns the signals for each individual (noResources x noIndividuals)
     *
     * @param levels the levels for each individual (noResources x noIndividuals)
     */
    public Matrix createSignals(Matrix levels) {
        requireNonNull(levels);
        assert levels.getNumRows() == minLevels.getNumRows()
                : format("levels must be (%d x n) (%d x %d)",
                minLevels.getNumRows(),
                levels.getNumRows(),
                levels.getNumCols());

        return levels.copy()
                .divi(minLevels)
                .logi()
                .muli(maxLevel)
                .divi(minLevels);
    }

    /**
     * Returns the levels for each individual (noResources x noIndividuals)
     *
     * @param signals the signals for each individual (noResources x noIndividuals)
     */
    public Matrix createTargetLevels(Matrix signals) {
        requireNonNull(signals);
        assert signals.getNumRows() == minLevels.getNumRows()
                : format("signals must be (%d x n) (%d x %d)",
                minLevels.getNumRows(),
                signals.getNumRows(),
                signals.getNumCols());

        return maxLevel.copy()
                .divi(minLevels)
                .logi()
                .muli(signals)
                .expi()
                .muli(minLevels);
    }

    /**
     * Returns the maximum levels of individual resource targets
     */
    public Matrix getMaxLevel() {
        return maxLevel;
    }

    /**
     * Returns the minimum levels of individual resource targets
     */
    public Matrix getMinLevels() {
        return minLevels;
    }

    /**
     * Returns the number of resources
     */
    public int getNumTargetLevels() {
        return minLevels.getNumRows();
    }

    /**
     * Returns the exchange rates in units / sec
     */
    public Matrix getRates() {
        return rates;
    }

    /**
     * The exchange resources between environment and individual
     */
    public static class ExchangeResources {
        /**
         * Returns the exchange resources
         *
         * @param environmentResources the environment resources (noResource x noCells)
         * @param individualResources  the individual resources (noResources x noIndividuals)
         */
        public static ExchangeResources of(Matrix environmentResources, Matrix individualResources) {
            return new ExchangeResources(environmentResources, individualResources);
        }

        public final Matrix environmentResources;
        public final Matrix individualResources;

        /**
         * Creates the exchange resources
         *
         * @param environmentResources the environment resources (noResource x noCells)
         * @param individualResources  the individual resources (noResources x noIndividuals)
         */
        protected ExchangeResources(Matrix environmentResources, Matrix individualResources) {
            this.environmentResources = environmentResources;
            this.individualResources = individualResources;
        }
    }

}
