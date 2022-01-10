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

import static java.lang.Math.log;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mmarini.genesis.model3.Matrix.of;
import static org.mmarini.genesis.model3.Matrix.zeros;

class MetabolicTest {

    static final int SEED = 1234;
    static final int MAX_NUM_STEPS = 10;
    static final int MIN_NUM_STEPS = 1;
    static final double MIN_DT = 0.1;
    static final double MAX_DT = 1;
    static final double MIN_ALPHA = 0.1;
    static final double MAX_ALPHA = 0.9;
    static final int WIDTH = 2;
    static final int HEIGHT = 2;
    static final int NUM_RESOURCES = 4;
    static final int NUM_INDIVIDUALS = 1;
    static final int FOOD_REF = 0;
    static final int WAST_REF = 1;
    static final int ENERGY_REF = 2;
    static final double LENGTH = 2;
    static final double FOOD_MASS = 3;
    static final double WAST_MASS = 3;
    static final double BODY_MASS = 1;
    static final double SURVIVE_MASS = 0.1;
    static final double BODY = 1;
    static final double ENERGY_PRODUCTION = 2.0;
    static final double FOOD_CONSUMPTION = 1.0;

    static Stream<Arguments> arguments() {
        return ArgumentGenerator.create(SEED)
                .exponential(MIN_DT, MAX_DT)
                .uniform(MIN_NUM_STEPS, MAX_NUM_STEPS)
                .exponential(MIN_ALPHA, MAX_ALPHA)
                .generate();
    }

    SimStatus result;

    /*
    @TODO il test salta per morte prima del previsto
     */
    @ParameterizedTest
    @MethodSource("arguments")
    void survive(double dt,
                 int noSteps,
                 double metabolicAlpha) {
        /*
        Given an engine and a simulation status
        with an individual with energy sufficient for a single step
        and food sufficient for n step
        */

        // Energy required for n steps of dt interval for the mass of individuals for a consumption rate
        // energy = rate dt n mass

        // Food required:
        // food EP / FC = rate dt n (food FM + bodyMass)
        // food EP / FC = rate dt n food FM + rate dt n bodyMass
        // food (EP / FC - rate dt n FM) = rate dt n bodyMass
        // food = rate dt n bodyMass / (EP / FC - rate dt n FM)

        // rate dt n FM < EP / FC
        // rate < EP / (dt n FM FC)
        double metabolicRate = metabolicAlpha * ENERGY_PRODUCTION / dt / noSteps / FOOD_MASS / FOOD_CONSUMPTION;

        double foods = metabolicRate * BODY_MASS * BODY * dt * noSteps
                / (ENERGY_PRODUCTION / FOOD_CONSUMPTION - metabolicRate * FOOD_MASS * dt * noSteps);

        final double energyByStep = metabolicRate * (BODY * BODY_MASS + foods * FOOD_MASS) * dt;

        final double erg = energyByStep * 10001 / 10000;
        final double minLevel = erg / 2;
        final double maxLevel = erg * 2;
        final double conversionRate = erg / dt / BODY / BODY_MASS;

        final Matrix masses = of(
                FOOD_MASS,
                WAST_MASS,
                0,
                BODY_MASS
        );
        final Topology topology = Topology3.create(WIDTH, HEIGHT, LENGTH);
        final Matrix diffusion = zeros(NUM_RESOURCES, 1);
        final SimEngine engine = new SimEngine(masses, topology, diffusion, ENERGY_REF);

        final Matrix quantities = zeros(NUM_RESOURCES, topology.getNoCells());

        final Matrix individualQties = of(
                foods,
                0,
                erg,
                BODY
        );
        final int[] locations = new int[NUM_INDIVIDUALS];
        final Matrix reagents = of(
                FOOD_CONSUMPTION,
                0,
                0,
                0
        );
        final Matrix products = of(
                0,
                FOOD_CONSUMPTION,
                ENERGY_PRODUCTION,
                0
        );
        final Matrix thresholds = of(
                0,
                0,
                0,
                0
        );
        final Matrix speeds = of(
                0,
                0,
                0,
                conversionRate
        );
        final Reaction reaction = Reaction.create(reagents, products, thresholds, speeds);
        ReactionProcess reactionProcess = ReactionProcess.create(ENERGY_REF, minLevel, maxLevel, reaction);
        final List<ReactionProcess> genes = List.of(reactionProcess);
        final Species species = Species.create(metabolicRate, SURVIVE_MASS, 0, List.of(), genes, List.of(), List.of());
        final List<Matrix> genotypes = List.of(reactionProcess.createTargetLevels(of(0.5)));
        final Population population = new Population(individualQties, List.of(), genotypes, List.of(), List.of(), locations, species);
        final List<Population> populations = List.of(population);
        final Random random = new Random(SEED);

        /*
         When running simulation engine for n steps
         */
        result = new SimStatus(0, quantities, populations);
        for (int i = 0; i <= noSteps; i++) {
            result = engine.next(result, (i + 1) * dt, random);
        }
        SimStatusProperties simStatusProperties = new SimStatusProperties(result);
        final int noIndividualsBeforeDeath = simStatusProperties.noIndividuals();

        /*
         Then the individual should be alive before last step
         */
        assertThat(noIndividualsBeforeDeath, equalTo(1));

        final double energyBeforeDeath = simStatusProperties.individualsResource(ENERGY_REF);
        final double foodBeforeDeath = simStatusProperties.individualsResource(FOOD_REF);

        /*
         And with energy and food insufficient for next step
         */
        assertThat(energyBeforeDeath, lessThan(energyByStep));
        assertThat(foodBeforeDeath, lessThan(energyByStep * FOOD_CONSUMPTION / ENERGY_PRODUCTION));

        /*
         When running simulation engine for final step
         */
        result = engine.next(result, (noSteps + 2) * dt, random);
        final int noIndividualsAfterDeath = new SimStatusProperties(result).noIndividuals();

        /*
        Then no more individuals after the last step
         */
        assertThat(noIndividualsAfterDeath, equalTo(0));
        /*
        And wast resource released in the environment close
         to foods initially owned by the individual (full available food consumed)
         */
        assertThat(
                new SimStatusProperties(result).environResource(WAST_REF),
                closeTo(foods, energyByStep));
        /*
        And food released in the environment less than the food consumption
         */
        assertThat(
                new SimStatusProperties(result).environResource(FOOD_REF),
                lessThan(energyByStep * FOOD_CONSUMPTION / ENERGY_PRODUCTION));
    }
}