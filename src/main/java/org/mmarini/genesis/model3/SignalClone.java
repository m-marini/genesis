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
import java.util.Random;
import java.util.stream.Collectors;

import static org.mmarini.genesis.model3.Matrix.*;

public class SignalClone {

    /**
     * Returns the mutated clones of population signals
     *
     * @param signals     the signals
     * @param probability the mutation probability
     * @param sigma       the sigma value of mutation
     * @param random      the random generator
     */
    static Matrix clone(final Matrix signals,
                        final double probability,
                        final double sigma,
                        final Random random) {
        Matrix p = rand(signals.getNumRows(), signals.getNumCols(), random)
                .subi(probability)
                .lti();
        Matrix ds = randn(signals.getNumRows(), signals.getNumCols(), random).muli(sigma).muli(p);
        return ds.addi(signals).maxi(0).mini(1);
    }

    /**
     * Returns the list of population signals by cloning the parents
     *
     * @param signals     the list of gene signals
     * @param probability the mutation probability
     * @param sigma       the mutation sigma
     * @param random      the random generator
     * @param parents     the parent indices
     */
    static List<Matrix> clone(final List<Matrix> signals,
                              final double probability,
                              final double sigma,
                              final Random random,
                              final int... parents) {
        return signals.stream()
                .map(m -> clonePopulation(m, probability, sigma, random, parents))
                .collect(Collectors.toList());
    }

    /**
     * Returns the signals by cloning the parents
     *
     * @param signals     the gene signals
     * @param probability the mutation probability
     * @param sigma       the mutation sigma
     * @param random      the random generator
     * @param parents     the parent indices
     */
    static Matrix clonePopulation(final Matrix signals,
                                  final double probability,
                                  final double sigma,
                                  final Random random,
                                  final int... parents) {
        final Matrix c = clone(signals.extractCols(parents), probability, sigma, random);
        return hstack(signals, c);
    }

}
