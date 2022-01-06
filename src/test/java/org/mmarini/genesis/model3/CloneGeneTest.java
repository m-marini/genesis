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

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.Random;
import java.util.stream.Stream;

import static java.lang.Math.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mmarini.genesis.model3.Matrix.of;
import static org.mmarini.genesis.model3.MatrixMatchers.matrixCloseTo;

class CloneGeneTest {

    static final double IN_PLACE_PREFERENCE = 0.1;
    static final double ADJACENT_PREFERENCE = 0.2;
    static final int LOC1 = 0;
    static final int LOC2 = 2;
    static final int[] LOCATIONS = new int[]{0, LOC1, LOC2};
    static final double MIN_MASS_THRESHOLD = 0.1;
    static final double MAX_MASS_THRESHOLD = 10;
    static final double MIN_ENERGY_THRESHOLD = 0.2;
    static final double MAX_ENERGY_THRESHOLD = 20;
    static final int ENERGY_REF = 1;
    static final double MIN_MASS_PROBABILITY = 0.1;
    static final double MAX_MASS_PROBABILITY = 1;
    static final double MIN_ENERGY_PROBABILITY = 0.1;
    static final double MAX_ENERGY_PROBABILITY = 1;
    static final double MASS = 1;
    static final double ERG = 0;
    static final double MIN_PROB = 0.1;
    static final double MAX_PROB = 1 - MIN_PROB;
    static final double MIN_SIGMA = 0.01;
    static final double MAX_SIGMA = 0.5 - MIN_SIGMA;
    static final double MIN_MASS = 1;
    static final double MAX_MASS = 10;
    static final double MIN_ERG = 1;
    static final double MAX_ERG = 10;
    static final double MIN_DT = 0.1;
    static final double MAX_DT = 0.3;
    static final double MIN_K = 0.1;
    static final double MAX_K = 0.5;
    static final double MIN_KE = 0.1;
    static final double MAX_KE = 0.5;
    static final long SEED = 1234;
    static final double MIN_SIGNAL = 0;
    static final double MAX_SIGNAL = 1;
    private static final double EP1 = 1 + 1e-6;

    static Stream<Arguments> argumentsForCreate() {
        return ArgumentGenerator.create(SEED)
                .exponential(MIN_PROB, MAX_PROB)
                .exponential(MIN_SIGMA, MAX_SIGMA)
                .generate();
    }

    static Stream<Arguments> argumentsForExecution() {
        return ArgumentGenerator.create(SEED)
                .exponential(MIN_PROB, MAX_PROB)
                .exponential(MIN_SIGMA, MAX_SIGMA)
                .exponential(MIN_MASS, MAX_MASS)
                .exponential(MIN_ERG, MAX_ERG)
                .gaussian(0, MAX_SIGMA)
                .exponential(MIN_DT, MAX_DT)
                .exponential(MIN_K, MAX_K)
                .exponential(MIN_KE, MAX_KE)
                .uniform(MIN_SIGNAL, MAX_SIGNAL)
                .uniform(MIN_SIGNAL, MAX_SIGNAL)
                .generate();
    }

    @ParameterizedTest
    @MethodSource("argumentsForCreate")
    void create(final double mutationProb,
                final double mutuationSigma) {

        /*
        Given a clone gene
         */
        final CloneGene gene = CloneGene.create(
                MIN_MASS_THRESHOLD,
                MAX_MASS_THRESHOLD,
                MIN_ENERGY_THRESHOLD,
                MAX_ENERGY_THRESHOLD,
                MIN_MASS_PROBABILITY,
                MAX_MASS_PROBABILITY,
                MIN_ENERGY_PROBABILITY,
                MAX_ENERGY_PROBABILITY,
                ENERGY_REF,
                IN_PLACE_PREFERENCE,
                ADJACENT_PREFERENCE,
                mutationProb,
                mutuationSigma);

        assertThat(gene.getMinLevels(), matrixCloseTo(
                MIN_MASS_THRESHOLD,
                MIN_ENERGY_THRESHOLD,
                MIN_MASS_PROBABILITY,
                MIN_ENERGY_PROBABILITY
        ));
        assertThat(gene.getLevelRates(), matrixCloseTo(
                log(MAX_MASS_THRESHOLD / MIN_MASS_THRESHOLD),
                log(MAX_ENERGY_THRESHOLD / MIN_ENERGY_THRESHOLD),
                log(MAX_MASS_PROBABILITY / MIN_MASS_PROBABILITY),
                log(MAX_ENERGY_PROBABILITY / MIN_ENERGY_PROBABILITY)
        ));
        assertThat(gene.getEnergyRef(), equalTo(ENERGY_REF));
        assertThat(gene.getMutationProb(), equalTo(mutationProb));
        assertThat(gene.getMutationSigma(), equalTo(mutuationSigma));
    }


    @ParameterizedTest
    @MethodSource("argumentsForExecution")
    void execute(final double mutationProb,
                 final double mutuationSigma,
                 final double mass,
                 final double erg,
                 final double change,
                 final double dt,
                 final double kMass,
                 final double kErg,
                 final double massProbSignal,
                 final double energyProbSignal) {

        /*
        Given a clone gene
         */
        final CloneGene gene = CloneGene.create(
                MIN_MASS_THRESHOLD,
                MAX_MASS_THRESHOLD,
                MIN_ENERGY_THRESHOLD,
                MAX_ENERGY_THRESHOLD,
                MIN_MASS_PROBABILITY,
                MAX_MASS_PROBABILITY,
                MIN_ENERGY_PROBABILITY,
                MAX_ENERGY_PROBABILITY,
                ENERGY_REF,
                IN_PLACE_PREFERENCE,
                ADJACENT_PREFERENCE,
                mutationProb,
                mutuationSigma);
        /*
        And clone gene signals
         */
        double massThs = mass * kMass;
        double ergThs = erg * kErg;
        double s11 = log(massThs / MIN_MASS_THRESHOLD) / log(MAX_MASS_THRESHOLD / MIN_MASS_THRESHOLD);
        double s21 = log(ergThs / MIN_ENERGY_THRESHOLD) / log(MAX_ENERGY_THRESHOLD / MIN_ENERGY_THRESHOLD);

        final Matrix signals = of(new double[][]{
                {s11, s11, s11},
                {s21, s21, s21},
                {massProbSignal, massProbSignal, massProbSignal},
                {energyProbSignal, energyProbSignal, energyProbSignal},
        });
        final List<Matrix> pipSignals = List.of(signals);
        // And a population with 3 individuals in 2 cells
        final Matrix qties = of(new double[][]{
                {mass, mass, mass},
                {erg, erg, erg},
        });
        List<PIPGene> pipGenes = List.of(gene);
        final Species species = Species.create(0.0, 0, 0,
                List.of(), List.of(), List.of(), pipGenes);

        final Population population = Population.create(qties,
                List.of(), List.of(), List.of(), pipSignals,
                LOCATIONS, species);
        /*
        And a topology of 4 x 4 cells
          x---x---x
           \2/3\4/5\
            x---x---x
           /8\9/0\1/
          x---x---x
           \4/5\6/7\
            x---x---x
           /0\1/2\3/
          x---x---x
         */
        final Topology topology = Topology3.create(4, 4, 2);

        /*
        And molecular masses
         */
        Matrix masses = of(MASS, ERG);

        /*
        And a random builder such as it generates
         cloneProb, 0, 0
         1 - 0.375, 0 for random location of 2 individuals
         mutProb x 2 x 4 for pip signals
         2 x 4 for pip changes
         */
        final double massProbSpeed = exp(massProbSignal * log(MAX_MASS_PROBABILITY / MIN_MASS_PROBABILITY)) * MIN_MASS_PROBABILITY;
        final double ergProbSpeed = exp(energyProbSignal * log(MAX_ENERGY_PROBABILITY / MIN_ENERGY_PROBABILITY)) * MIN_ENERGY_PROBABILITY;
        final double massProb = -expm1(-(mass - massThs) * massProbSpeed * dt);
        final double ergProb = -expm1(-(erg - ergThs) * ergProbSpeed * dt);
        final double cloneProb = min(massProb, ergProb);
        final Random random = new MockRandomBuilder()
                .nextDouble(cloneProb * EP1, 0, 0) // clonebale
                .nextDouble(1 - 0.375, 0) // location
                .nextDouble(0, 0, 0, 0, 0, 0, 0, 0) // activate mutation
                .nextDouble(change / mutuationSigma,
                        change / mutuationSigma,
                        change / mutuationSigma,
                        change / mutuationSigma,
                        change / mutuationSigma,
                        change / mutuationSigma,
                        change / mutuationSigma,
                        change / mutuationSigma) // compute changes
                .build();

        // When execute gene code
        final Population result = gene.execute(population, signals, dt, masses, topology, random);

        /*
         Then should return a different population
         */
        assertThat(result, not(sameInstance(population)));
        /*
        And quantities for 5 individuals
         */
        final double dm1 = mass * (1 - kMass);
        final double dm2 = dm1;
        final double de1 = erg * (1 - kErg);
        final double de2 = de1;
        assertThat(result.getResources(), matrixCloseTo(new double[][]{
                {mass, mass - dm1, mass - dm2, dm1, dm2},
                {erg, erg - de1, erg - de2, de1, de2},
        }));
        /*
        And no gene signal mutations
         */
        final double es12 = max(0, min(s11 + change, 1));
        final double es22 = max(0, min(s21 + change, 1));
        final double es32 = max(0, min(massProbSignal + change, 1));
        final double es42 = max(0, min(energyProbSignal + change, 1));
        assertThat(result.getPipSignals().get(0), matrixCloseTo(new double[][]{
                {s11, s11, s11, es12, es12},
                {s21, s21, s21, es22, es22},
                {massProbSignal, massProbSignal, massProbSignal, es32, es32},
                {energyProbSignal, energyProbSignal, energyProbSignal, es42, es42},
        }));
    }
}