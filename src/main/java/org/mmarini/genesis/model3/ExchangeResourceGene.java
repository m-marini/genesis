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
 * The exchange resource gene controls the quantities of resource exchanged with the environment
 * It has a signal for each resource that controls the target level of resources.
 * If the resources present in the individual exceed the target it releases the resource to the environment
 * If the resources present in the individual is less than the target it absorbes the resource from the environment
 */
public class ExchangeResourceGene implements EIPGene {
    private final Matrix minLevels;
    private final Matrix logRates;
    private final Matrix rates;

    /**
     * @param minLevels minimum levels (nr x 1)
     * @param logRates  log rate levels (nr x 1)
     * @param rates     exchange rates in (nr x 1)
     */
    public ExchangeResourceGene(final Matrix minLevels,
                                final Matrix logRates,
                                final Matrix rates) {
        this.minLevels = minLevels;
        this.logRates = logRates;
        this.rates = rates;
    }

    @Override
    public Population execute(final Population population,
                              final Matrix signals,
                              final double dt,
                              final Matrix envResources,
                              final Matrix areas,
                              final Matrix masses) {
        final Matrix signals1 = signals.copy();
        final Matrix qt = signals1.muli(logRates).expi().muli(minLevels);
        return population.exchangeResources(dt, qt, envResources, areas, masses, rates);
    }

    /**
     *
     */
    public Matrix getLogRates() {
        return logRates;
    }

    /**
     *
     */
    public Matrix getMinLevels() {
        return minLevels;
    }

    @Override
    public int getNumSignals() {
        return minLevels.getNumRows();
    }

    /**
     *
     */
    public Matrix getRates() {
        return rates;
    }
}
