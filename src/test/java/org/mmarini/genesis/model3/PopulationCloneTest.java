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

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.Random;
import java.util.stream.Stream;

import static java.lang.Math.max;
import static java.lang.Math.min;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mmarini.genesis.model3.Matrix.of;
import static org.mmarini.genesis.model3.Matrix.zeros;
import static org.mmarini.genesis.model3.MatrixMatchers.matrixCloseTo;

class PopulationCloneTest {
    public static final List<IPGene> IP_GENES = List.of(new IPGene() {
        @Override
        public Population execute(Population population, Matrix signals, double dt, Matrix resources, Matrix areas, Matrix masses) {
            return null;
        }

        @Override
        public int getNumSignals() {
            return 0;
        }
    });
    public static final List<EIPGene> EIP_GENES = List.of(new EIPGene() {
        @Override
        public Population execute(Population population, Matrix signals, double dt, Matrix envResources, Matrix areas, Matrix masses) {
            return null;
        }

        @Override
        public int getNumSignals() {
            return 0;
        }
    });
    public static final List<PIPGene> PIP_GENES = List.of(new PIPGene() {
        @Override
        public Population execute(Population population, Matrix signals, double dt, Matrix masses, Topology topology, Random random) {
            return null;
        }

        @Override
        public int getNumSignals() {
            return 0;
        }
    });
    public static final Species SPECIES = Species.create(0.0, 0, 0,
            List.of(), IP_GENES, EIP_GENES, PIP_GENES);
    static final int ENERGY_REF = 1;
    static final int SEED = 1234;
    static final int LOC1 = 0;
    static final int LOC2 = 2;
    static final double MAX_PROB = 0.999;
    static final double MIN_PROB = 1e-3;
    static final double MIN_SIGMA = 1e-3;
    static final double MAX_SIGMA = 0.5;
    static final double MIN_SIGNAL = 0.0;
    static final double MAX_SIGNAL = 1.0;
    static final int ADJ1 = 1;
    static final int ADJ2 = 2;
    static final double MIN_MASS = 1;
    static final double MAX_MASS = 10;
    static final double MIN_ERG = 1;
    static final double MAX_ERG = 10;
    static final double MIN_K = 1e-2;
    static final double MAX_K = 0.5;
    static final double MIN_KE = 1e-2;
    static final double MAX_KE = 0.5;
    static final int[] LOCATIONS = new int[]{0, LOC1, LOC2};

    static Stream<Arguments> argumentsForClone() {
        return ArgumentGenerator.create(SEED)
                .exponential(MIN_PROB, MAX_PROB)
                .exponential(MIN_SIGMA, MAX_SIGMA)
                .uniform(MIN_SIGNAL, MAX_SIGNAL)
                .uniform(MIN_SIGNAL, MAX_SIGNAL)
                .uniform(MIN_SIGNAL, MAX_SIGNAL)
                .exponential(MIN_MASS, MAX_MASS)
                .exponential(MIN_ERG, MAX_ERG)
                .exponential(MIN_K, MAX_K)
                .exponential(MIN_K, MAX_K)
                .exponential(MIN_KE, MAX_KE)
                .exponential(MIN_KE, MAX_KE)
                .gaussian(0, MAX_SIGMA)
                .generate();
    }

    static Stream<Arguments> argumentsForNoClones() {
        return ArgumentGenerator.create(SEED)
                .exponential(MIN_PROB, MAX_PROB)
                .exponential(MIN_SIGMA, MAX_SIGMA)
                .uniform(MIN_SIGNAL, MAX_SIGNAL)
                .uniform(MIN_SIGNAL, MAX_SIGNAL)
                .generate();
    }

    static Stream<Arguments> argumentsForNoMutations() {
        return ArgumentGenerator.create(SEED)
                .exponential(MIN_PROB, MAX_PROB)
                .exponential(MIN_SIGMA, MAX_SIGMA)
                .uniform(MIN_SIGNAL, MAX_SIGNAL)
                .uniform(MIN_SIGNAL, MAX_SIGNAL)
                .uniform(MIN_SIGNAL, MAX_SIGNAL)
                .exponential(MIN_MASS, MAX_MASS)
                .exponential(MIN_ERG, MAX_ERG)
                .exponential(MIN_K, MAX_K)
                .exponential(MIN_K, MAX_K)
                .exponential(MIN_KE, MAX_KE)
                .exponential(MIN_KE, MAX_KE)
                .generate();
    }

    @ParameterizedTest
    @MethodSource("argumentsForClone")
    void createClone(final double mutationProb,
                     final double mutationSigma,
                     final double s1,
                     final double s2,
                     final double s3,
                     final double mass,
                     final double erg,
                     final double k1,
                     final double k2,
                     final double ke1,
                     final double ke2,
                     final double change) {
        // Given a population with 3 individuals in 2 cells
        final Matrix qties = of(new double[][]{
                {mass, mass, mass},
                {erg, erg, erg},
        });
        final Matrix signals = of(new double[][]{
                {s1, s2, s3}
        });
        final List<Matrix> ipSignals = List.of(signals);
        final List<Matrix> eipSignals = List.of(signals);
        final List<Matrix> pipSignals = List.of(signals);
        final Population population = new Population(qties, List.of(), ipSignals, eipSignals, pipSignals, LOCATIONS, SPECIES);
        /*
        And a probability of adjacent locations
         */
        final Matrix prob = of(
                1 - 0.375,
                0.125,
                0.125,
                0.125
        ).cdfiRows();
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
        And mass coefficients by individual
         */
        final Matrix kMass = of(new double[][]{
                {k1, k2}
        });
        /*
        And remaining parent energy by individual
         */
        final Matrix energy = of(new double[][]{
                {erg * ke1, erg * ke2}
        });
        /*
        And a random builder such as it generates
         1 - 0.375, 0 for random location of 2 individuals
         mutProb x 2 for ip signals
         1 x 2 for ip changes
         mutProb x 2 for eip signals
         1 x 2 for eip changes
         mutProb x 2 for pip signals
         1 x 2 for pip changes
         */
        final Random random = new MockRandomBuilder()
                .nextDouble(1 - 0.375, 0)
                .nextDouble(0, 0)
                .nextDouble(change / mutationSigma, change / mutationSigma)
                .nextDouble(0, 0)
                .nextDouble(change / mutationSigma, change / mutationSigma)
                .nextDouble(0, 0)
                .nextDouble(change / mutationSigma, change / mutationSigma)
                .build();

        // When create clone locations
        final Population result = population.cloneIndividuals(random, prob, mutationProb, mutationSigma, topology, energy, kMass, ENERGY_REF, 1, 2);

        /*
         Then should return a different population
         */
        assertThat(result, not(sameInstance(population)));
        /*
        And quantities for 5 individuals
         */
        final double dm1 = k1 * mass;
        final double dm2 = k2 * mass;
        final double de1 = erg * ke1;
        final double de2 = erg * ke2;
        assertThat(result.getResources(), matrixCloseTo(new double[][]{
                {mass, mass - dm1, mass - dm2, dm1, dm2},
                {erg, erg - de1, erg - de2, de1, de2},
        }));
        /*
        And no gene signal mutations
         */
        final double es2 = max(0, min(s2 + change, 1));
        final double es3 = max(0, min(s3 + change, 1));
        assertThat(result.getIpSignals().get(0), matrixCloseTo(new double[][]{
                {s1, s2, s3, es2, es3}
        }));
        assertThat(result.getEipSignals().get(0), matrixCloseTo(new double[][]{
                {s1, s2, s3, es2, es3}
        }));
        assertThat(result.getPipSignals().get(0), matrixCloseTo(new double[][]{
                {s1, s2, s3, es2, es3}
        }));
    }

    @Test
    void createCloneLocations() {
        // Given a population with 3 individuals in 2 cells
        Matrix qties = zeros(1, 3);
        Population population = Population.create(qties, List.of(), List.of(), List.of(), List.of(), LOCATIONS,
                Species.create(0.0, 0, 0,
                        List.of(), List.of(), List.of(), List.of())
        );
        /*
        And a probability of adjacent locations
         */
        Matrix prob = of(
                1 - 0.375,
                0.125,
                0.125,
                0.125
        ).cdfiRows();
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
        Topology topology = Topology3.create(4, 4, 2);
        /*
        And a random builder such as it generates 1 - 0.375, 0
         */
        Random random = new MockRandomBuilder()
                .nextDouble(1 - 0.375, 0)
                .build();

        // When create clone locations
        int[] result = population.createCloneLocations(random, prob, topology, 1, 2);

        // Then should return location
        assertThat(result, equalTo(new int[]{ADJ1, ADJ2}));
    }

    @ParameterizedTest
    @MethodSource("argumentsForNoMutations")
    void createCloneNoChanges(final double mutationProb,
                              final double mutationSigma,
                              final double s1,
                              final double s2,
                              final double s3,
                              final double mass,
                              final double erg,
                              final double k1,
                              final double k2,
                              final double ke1,
                              final double ke2) {
        // Given a population with 3 individuals in 2 cells
        final Matrix qties = of(new double[][]{
                {mass, mass, mass},
                {erg, erg, erg},
        });
        final Matrix signals = of(new double[][]{
                {s1, s2, s3}
        });
        final List<Matrix> ipSignals = List.of(signals);
        final List<Matrix> eipSignals = List.of(signals);
        final List<Matrix> pipSignals = List.of(signals);
        final Population population = new Population(qties, List.of(), ipSignals, eipSignals, pipSignals, LOCATIONS, SPECIES);
        /*
        And a probability of adjacent locations
         */
        final Matrix prob = of(
                1 - 0.375,
                0.125,
                0.125,
                0.125
        ).cdfiRows();
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
        And mass coefficients by individual
         */
        final Matrix kMass = of(new double[][]{
                {k1, k2}
        });
        /*
        And remaining parent energy by individual
         */
        final Matrix energy = of(new double[][]{
                {erg * ke1, erg * ke2}
        });
        /*
        And a random builder such as it generates
         1 - 0.375, 0 for random location of 2 individuals
         mutProb x 2 for ip signals
         1 x 2 for ip changes
         mutProb x 2 for eip signals
         1 x 2 for eip changes
         mutProb x 2 for pip signals
         1 x 2 for pip changes
         */
        final Random random = new MockRandomBuilder()
                .nextDouble(1 - 0.375, 0)
                .nextDouble(0, 0)
                .nextDouble(0, 0)
                .nextDouble(0, 0)
                .nextDouble(0, 0)
                .nextDouble(0, 0)
                .nextDouble(0, 0)
                .build();

        // When create clone locations
        final Population result = population.cloneIndividuals(random, prob, mutationProb, mutationSigma, topology, energy, kMass, ENERGY_REF, 1, 2);

        /*
         Then should return a different population
         */
        assertThat(result, not(sameInstance(population)));
        /*
        And quantities for 5 individuals
         */
        final double dm1 = k1 * mass;
        final double dm2 = k2 * mass;
        final double de1 = erg * ke1;
        final double de2 = erg * ke2;
        assertThat(result.getResources(), matrixCloseTo(new double[][]{
                {mass, mass - dm1, mass - dm2, dm1, dm2},
                {erg, erg - de1, erg - de2, de1, de2},
        }));
        /*
        And no gene signal mutations
         */
        assertThat(result.getIpSignals().get(0), matrixCloseTo(new double[][]{
                {s1, s2, s3, s2, s3}
        }));
        assertThat(result.getEipSignals().get(0), matrixCloseTo(new double[][]{
                {s1, s2, s3, s2, s3}
        }));
        assertThat(result.getPipSignals().get(0), matrixCloseTo(new double[][]{
                {s1, s2, s3, s2, s3}
        }));
    }

    @ParameterizedTest
    @MethodSource("argumentsForNoClones")
    void createCloneNoClones(final double mutationProb,
                             final double mutationSigma,
                             final double s1,
                             final double s2) {
        // Given a population with 3 individuals in 2 cells
        final Matrix qties = zeros(1, 3);
        final Matrix signals = of(new double[][]{
                {s1, s2}
        });
        final List<Matrix> ipSignals = List.of(signals);
        final List<Matrix> eipSignals = List.of(signals);
        final List<Matrix> pipSignals = List.of(signals);
        final Population population = new Population(qties, List.of(), ipSignals, eipSignals, pipSignals, LOCATIONS, SPECIES);
        /*
        And a probability of adjacent locations
         */
        final Matrix prob = of(
                1 - 0.375,
                0.125,
                0.125,
                0.125
        ).cdfiRows();
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
        final Matrix energy = zeros(1, 0);
        final Matrix kMass = zeros(2, 0);
        /*
        And a random builder such as it generates 1 - 0.375, 0
         */
        final Random random = new MockRandomBuilder()
                .build();

        // When create clone locations
        final Population result = population.cloneIndividuals(random, prob, mutationProb, mutationSigma, topology, energy, kMass, ENERGY_REF);

        /*
         Then should return the same population
         */
        assertThat(result, sameInstance(population));
    }

    /*
    noMutation
    noChanges
     */
    @ParameterizedTest
    @MethodSource("argumentsForNoMutations")
    void createCloneNoMutation(final double mutationProb,
                               final double mutationSigma,
                               final double s1,
                               final double s2,
                               final double s3,
                               final double mass,
                               final double erg,
                               final double k1,
                               final double k2,
                               final double ke1,
                               final double ke2) {
        // Given a population with 3 individuals in 2 cells
        final Matrix qties = of(new double[][]{
                {mass, mass, mass},
                {erg, erg, erg},
        });
        final Matrix signals = of(new double[][]{
                {s1, s2, s3}
        });
        final List<Matrix> ipSignals = List.of(signals);
        final List<Matrix> eipSignals = List.of(signals);
        final List<Matrix> pipSignals = List.of(signals);
        final Population population = new Population(qties,
                List.of(), ipSignals, eipSignals, pipSignals,
                LOCATIONS, SPECIES);
        /*
        And a probability of adjacent locations
         */
        final Matrix prob = of(
                1 - 0.375,
                0.125,
                0.125,
                0.125
        ).cdfiRows();
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
        And mass coefficients by individual
         */
        final Matrix kMass = of(new double[][]{
                {k1, k2}
        });
        /*
        And remaining parent energy by individual
         */
        final Matrix energy = of(new double[][]{
                {erg * ke1, erg * ke2}
        });
        /*
        And a random builder such as it generates
         1 - 0.375, 0 for random location of 2 individuals
         mutProb x 2 for ip signals
         1 x 2 for ip changes
         mutProb x 2 for eip signals
         1 x 2 for eip changes
         mutProb x 2 for pip signals
         1 x 2 for pip changes
         */
        final Random random = new MockRandomBuilder()
                .nextDouble(1 - 0.375, 0)
                .nextDouble(mutationProb, mutationProb)
                .nextDouble(1, 1)
                .nextDouble(mutationProb, mutationProb)
                .nextDouble(1, 1)
                .nextDouble(mutationProb, mutationProb)
                .nextDouble(1, 1)
                .build();

        // When create clone locations
        final Population result = population.cloneIndividuals(random, prob, mutationProb, mutationSigma, topology, energy, kMass, ENERGY_REF, 1, 2);

        /*
         Then should return a different population
         */
        assertThat(result, not(sameInstance(population)));
        /*
        And quantities for 5 individuals
         */
        final double dm1 = k1 * mass;
        final double dm2 = k2 * mass;
        final double de1 = erg * ke1;
        final double de2 = erg * ke2;
        assertThat(result.getResources(), matrixCloseTo(new double[][]{
                {mass, mass - dm1, mass - dm2, dm1, dm2},
                {erg, erg - de1, erg - de2, de1, de2},
        }));
        /*
        And no gene signal mutations
         */
        assertThat(result.getIpSignals().get(0), matrixCloseTo(new double[][]{
                {s1, s2, s3, s2, s3}
        }));
        assertThat(result.getEipSignals().get(0), matrixCloseTo(new double[][]{
                {s1, s2, s3, s2, s3}
        }));
        assertThat(result.getPipSignals().get(0), matrixCloseTo(new double[][]{
                {s1, s2, s3, s2, s3}
        }));
    }
}