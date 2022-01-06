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

import java.util.List;

import static java.lang.Math.log;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.sameInstance;
import static org.mmarini.genesis.model3.Matrix.of;
import static org.mmarini.genesis.model3.MatrixMatchers.matrixCloseTo;

class ResourceGeneTest {

    private static final int NUM_INDIVIDUALS = 4;
    private static final double REACTION_RATE = 1.2;
    private static final double BASAL_METABOLIC_RATE = 0;
    private static final double DT = 0.1;
    private static final double MIN_LEVEL = 50e3;
    private static final double MAX_LEVEL = 200e3;
    private static final double TARGET = Math.sqrt(MIN_LEVEL * MAX_LEVEL);
    private static final double SURVIVE_MASS = 0.0;
    private static final int RESOURCE_REF = 1;
    private static final double RATE_CONTROLLER = 2.0;
    private static final double GENE0 = 0.5;
    private static final double GENE1 = 0.5;
    private static final double GENE2 = 0.5;
    private static final double GENE3 = 0.5;
    private static final double REAGENT_CONSUME = 2;
    private static final double REAGENT_PRODUCTION = 1;
    private static final double PROD_PRODUCTION = 1;
    private static final double DELTA_PROD = REACTION_RATE * RATE_CONTROLLER * DT;
    private static final double REQUIRED_REAGENT = DELTA_PROD / PROD_PRODUCTION * REAGENT_CONSUME;
    private static final double THRESHOLD = REQUIRED_REAGENT * 0.9;
    private static final double BELOW_THRESHOLD = THRESHOLD * 0.9;
    private static final double REAGENT = (REQUIRED_REAGENT + THRESHOLD) * 1.1;
    private static final double DELTA_REAGENT = DELTA_PROD / PROD_PRODUCTION * (REAGENT_PRODUCTION - REAGENT_CONSUME);
    private static final double FEW_BELOW_TARGET = TARGET - DELTA_PROD / 2;
    private static final double VERY_BELOW_TARGET = TARGET - DELTA_PROD * 1.5;
    private static final double ABOVE_TARGET = TARGET + DELTA_PROD * 0.1;

    @Test
    void execute() {
        /*
        Given a resource map composed by
          a reagent,
          a product,
          a speed controller
         */
        Matrix reagents = of(
                REAGENT_CONSUME,
                0,
                0
        );
        Matrix products = of(
                REAGENT_PRODUCTION,
                PROD_PRODUCTION,
                0
        );
        Matrix thresholds = of(
                THRESHOLD,
                0,
                0
        );
        Matrix speeds = of(
                0,
                0,
                REACTION_RATE
        );
        Reaction reaction = Reaction.create(reagents, products, thresholds, speeds);

        /*
        And a resource control gene
         */
        ResourceGene gene = new ResourceGene(RESOURCE_REF, MIN_LEVEL, log(MAX_LEVEL / MIN_LEVEL), reaction);

        /*
        And a population composed by
          an individual very below target resource with insufficient reagent threshold
          an individual very below target resource with sufficient reagent threshold
          an individual a few below target resource with sufficient reagent threshold
          an individual above target resource
         */
        Species species = new Species(BASAL_METABOLIC_RATE,
                SURVIVE_MASS, 0, List.of(), List.of(gene),
                List.of(),
                List.of());
        Matrix quantities = of(new double[][]{
                {BELOW_THRESHOLD, REAGENT, REAGENT, REAGENT},
                {VERY_BELOW_TARGET, VERY_BELOW_TARGET, FEW_BELOW_TARGET, ABOVE_TARGET},
                {RATE_CONTROLLER, RATE_CONTROLLER, RATE_CONTROLLER, RATE_CONTROLLER}
        });
        Matrix signals = of(new double[][]{
                {GENE0, GENE1, GENE2, GENE3}
        });
        List<Matrix> genotypes = List.of(signals);
        int[] locations = new int[NUM_INDIVIDUALS];
        Population population = new Population(quantities,  List.of(), genotypes, List.of(), List.of(), locations, species);

        /*
         * When execute behavior
         */
        Population result = gene.execute(population, signals, DT, null, null, null);

        /*
         * Then should generate individual quantities as:
         *   an individual with no changes
         *   an individual below target level
         *   an individual at target resource
         *   an individual with no changes
         */
        assertThat(result, sameInstance(population));
        double prod1 = VERY_BELOW_TARGET + DELTA_PROD;
        double reagent1 = REAGENT + DELTA_REAGENT;
        double delta2 = (TARGET - FEW_BELOW_TARGET) / PROD_PRODUCTION * (REAGENT_PRODUCTION - REAGENT_CONSUME);
        double regent2 = REAGENT + delta2;
        assertThat(result.getResources(), matrixCloseTo(new double[][]{
                {BELOW_THRESHOLD, reagent1, regent2, REAGENT},
                {VERY_BELOW_TARGET, prod1, TARGET, ABOVE_TARGET},
                {RATE_CONTROLLER, RATE_CONTROLLER, RATE_CONTROLLER, RATE_CONTROLLER}
        }));
    }
}