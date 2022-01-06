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

package org.mmarini.genesis.yaml;

import com.fasterxml.jackson.databind.JsonNode;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mmarini.genesis.model3.CloneGene;

import java.io.IOException;
import java.util.List;
import java.util.stream.Stream;

import static java.lang.Math.log;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.matchesPattern;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mmarini.genesis.model3.Matrix.of;
import static org.mmarini.genesis.model3.MatrixMatchers.matrixCloseTo;
import static org.mmarini.genesis.yaml.TestUtils.text;
import static org.mmarini.yaml.Utils.fromText;
import static org.mmarini.yaml.schema.Locator.root;

class CloneGeneTest {
    static final double MIN_MASS_THRESHOLD = 0.1;
    static final double MAX_MASS_THRESHOLD = 1.2;
    static final double MIN_ENERGY_THRESHOLD = 3.4;
    static final double MAX_ENERGY_THRESHOLD = 4.5;
    static final double MIN_MASS_PROBABILITY = 5.6;
    static final double MAX_MASS_PROBABILITY = 6.7;
    static final double MIN_ENERGY_PROBABILITY = 7.8;
    static final double MAX_ENERGY_PROBABILITY = 8.9;
    static final double IN_PLACE_PREFERENCE = 9.0;
    static final double ADJACENT_PREFERENCE = 0.9;
    static final double MUTATION_PROBABILITY = 1.0;
    static final double MUTATION_SIGMA = 0.3;
    static final double RATES_MASS_THRESHOLD = log(MAX_MASS_THRESHOLD / MIN_MASS_THRESHOLD);
    static final double RATE_ENERGY_THRESHOLD = log(MAX_ENERGY_THRESHOLD / MIN_ENERGY_THRESHOLD);
    static final double RATE_MASS_PROBABILITY = log(MAX_MASS_PROBABILITY / MIN_MASS_PROBABILITY);
    static final double RATE_ENERGY_PROBABILITY = log(MAX_ENERGY_PROBABILITY / MIN_ENERGY_PROBABILITY);
    static final int ENERGY_REF = 1;

    static final List<String> KEYS = List.of("A", "B");

    static Stream<Arguments> argsForError() {
        return Stream.of(Arguments.of(text(
                        "#1",
                        "0"
                ), " must be an object \\(NUMBER\\)"
        ), Arguments.of(text(
                        "#2",
                        "minMassThreshold: 0.1",
                        "maxMassThreshold: 1.2",
                        "minEnergyThreshold: 3.4",
                        "maxEnergyThreshold: 4.5",
                        "minMassProbability: 5.6",
                        "maxMassProbability: 6.7",
                        "minEnergyProbability: 7.8",
                        "maxEnergyProbability: 8.9",
                        "inPlacePreference: 9.0",
                        "adjacentPreference: 0.9",
                        "mutationProb: 1.0",
                        "mutationSigma: 0.3"
                ), "/energyRef is missing"
        ), Arguments.of(text(
                        "#3",
                        "energyRef: B",
                        "maxMassThreshold: 1.2",
                        "minEnergyThreshold: 3.4",
                        "maxEnergyThreshold: 4.5",
                        "minMassProbability: 5.6",
                        "maxMassProbability: 6.7",
                        "minEnergyProbability: 7.8",
                        "maxEnergyProbability: 8.9",
                        "inPlacePreference: 9.0",
                        "adjacentPreference: 0.9",
                        "mutationProb: 1.0",
                        "mutationSigma: 0.3"
                ), "/minMassThreshold is missing"
        ), Arguments.of(text(
                        "#4",
                        "energyRef: B",
                        "minMassThreshold: 0.1",
                        "minEnergyThreshold: 3.4",
                        "maxEnergyThreshold: 4.5",
                        "minMassProbability: 5.6",
                        "maxMassProbability: 6.7",
                        "minEnergyProbability: 7.8",
                        "maxEnergyProbability: 8.9",
                        "inPlacePreference: 9.0",
                        "adjacentPreference: 0.9",
                        "mutationProb: 1.0",
                        "mutationSigma: 0.3"
                ), "/maxMassThreshold is missing"
        ), Arguments.of(text(
                        "#5",
                        "energyRef: B",
                        "minMassThreshold: 0.1",
                        "maxMassThreshold: 1.2",
                        "maxEnergyThreshold: 4.5",
                        "minMassProbability: 5.6",
                        "maxMassProbability: 6.7",
                        "minEnergyProbability: 7.8",
                        "maxEnergyProbability: 8.9",
                        "inPlacePreference: 9.0",
                        "adjacentPreference: 0.9",
                        "mutationProb: 1.0",
                        "mutationSigma: 0.3"
                ), "/minEnergyThreshold is missing"
        ), Arguments.of(text(
                        "#6",
                        "energyRef: B",
                        "minMassThreshold: 0.1",
                        "maxMassThreshold: 1.2",
                        "minEnergyThreshold: 3.4",
                        "minMassProbability: 5.6",
                        "maxMassProbability: 6.7",
                        "minEnergyProbability: 7.8",
                        "maxEnergyProbability: 8.9",
                        "inPlacePreference: 9.0",
                        "adjacentPreference: 0.9",
                        "mutationProb: 1.0",
                        "mutationSigma: 0.3"
                ), "/maxEnergyThreshold is missing"
        ), Arguments.of(text(
                        "#7",
                        "energyRef: B",
                        "minMassThreshold: 0.1",
                        "maxMassThreshold: 1.2",
                        "minEnergyThreshold: 3.4",
                        "maxEnergyThreshold: 4.5",
                        "maxMassProbability: 6.7",
                        "minEnergyProbability: 7.8",
                        "maxEnergyProbability: 8.9",
                        "inPlacePreference: 9.0",
                        "adjacentPreference: 0.9",
                        "mutationProb: 1.0",
                        "mutationSigma: 0.3"
                ), "/minMassProbability is missing"
        ), Arguments.of(text(
                        "#8",
                        "energyRef: B",
                        "minMassThreshold: 0.1",
                        "maxMassThreshold: 1.2",
                        "minEnergyThreshold: 3.4",
                        "maxEnergyThreshold: 4.5",
                        "minMassProbability: 5.6",
                        "minEnergyProbability: 7.8",
                        "maxEnergyProbability: 8.9",
                        "inPlacePreference: 9.0",
                        "adjacentPreference: 0.9",
                        "mutationProb: 1.0",
                        "mutationSigma: 0.3"
                ), "/maxMassProbability is missing"
        ), Arguments.of(text(
                        "#9",
                        "energyRef: B",
                        "minMassThreshold: 0.1",
                        "maxMassThreshold: 1.2",
                        "minEnergyThreshold: 3.4",
                        "maxEnergyThreshold: 4.5",
                        "minMassProbability: 5.6",
                        "maxMassProbability: 6.7",
                        "maxEnergyProbability: 8.9",
                        "inPlacePreference: 9.0",
                        "adjacentPreference: 0.9",
                        "mutationProb: 1.0",
                        "mutationSigma: 0.3"
                ), "/minEnergyProbability is missing"
        ), Arguments.of(text(
                        "#10",
                        "energyRef: B",
                        "minMassThreshold: 0.1",
                        "maxMassThreshold: 1.2",
                        "minEnergyThreshold: 3.4",
                        "maxEnergyThreshold: 4.5",
                        "minMassProbability: 5.6",
                        "maxMassProbability: 6.7",
                        "minEnergyProbability: 7.8",
                        "inPlacePreference: 9.0",
                        "adjacentPreference: 0.9",
                        "mutationProb: 1.0",
                        "mutationSigma: 0.3"
                ), "/maxEnergyProbability is missing"
        ), Arguments.of(text(
                        "#11",
                        "energyRef: B",
                        "minMassThreshold: 0.1",
                        "maxMassThreshold: 1.2",
                        "minEnergyThreshold: 3.4",
                        "maxEnergyThreshold: 4.5",
                        "minMassProbability: 5.6",
                        "maxMassProbability: 6.7",
                        "minEnergyProbability: 7.8",
                        "maxEnergyProbability: 8.9",
                        "adjacentPreference: 0.9",
                        "mutationProb: 1.0",
                        "mutationSigma: 0.3"
                ), "/inPlacePreference is missing"
        ), Arguments.of(text(
                        "#12",
                        "energyRef: B",
                        "minMassThreshold: 0.1",
                        "maxMassThreshold: 1.2",
                        "minEnergyThreshold: 3.4",
                        "maxEnergyThreshold: 4.5",
                        "minMassProbability: 5.6",
                        "maxMassProbability: 6.7",
                        "minEnergyProbability: 7.8",
                        "maxEnergyProbability: 8.9",
                        "inPlacePreference: 9.0",
                        "mutationProb: 1.0",
                        "mutationSigma: 0.3"
                ), "/adjacentPreference is missing"
        ), Arguments.of(text(
                        "#13",
                        "energyRef: B",
                        "minMassThreshold: 0.1",
                        "maxMassThreshold: 1.2",
                        "minEnergyThreshold: 3.4",
                        "maxEnergyThreshold: 4.5",
                        "minMassProbability: 5.6",
                        "maxMassProbability: 6.7",
                        "minEnergyProbability: 7.8",
                        "maxEnergyProbability: 8.9",
                        "inPlacePreference: 9.0",
                        "adjacentPreference: 0.9",
                        "mutationSigma: 0.3"
                ), "/mutationProb is missing"
        ), Arguments.of(text(
                        "#14",
                        "energyRef: B",
                        "minMassThreshold: 0.1",
                        "maxMassThreshold: 1.2",
                        "minEnergyThreshold: 3.4",
                        "maxEnergyThreshold: 4.5",
                        "minMassProbability: 5.6",
                        "maxMassProbability: 6.7",
                        "minEnergyProbability: 7.8",
                        "maxEnergyProbability: 8.9",
                        "inPlacePreference: 9.0",
                        "adjacentPreference: 0.9",
                        "mutationProb: 1.0"
                ), "/mutationSigma is missing"
        ), Arguments.of(text(
                        "#15",
                        "energyRef: C",
                        "minMassThreshold: 0.1",
                        "maxMassThreshold: 1.2",
                        "minEnergyThreshold: 3.4",
                        "maxEnergyThreshold: 4.5",
                        "minMassProbability: 5.6",
                        "maxMassProbability: 6.7",
                        "minEnergyProbability: 7.8",
                        "maxEnergyProbability: 8.9",
                        "inPlacePreference: 9.0",
                        "adjacentPreference: 0.9",
                        "mutationProb: 1.0",
                        "mutationSigma: 0.3"
                ), "/energyRef must match a value in \\[A, B\\] \\(C\\)"
        ), Arguments.of(text(
                        "#16",
                        "energyRef: B",
                        "minMassThreshold: 0",
                        "maxMassThreshold: 1.2",
                        "minEnergyThreshold: 3.4",
                        "maxEnergyThreshold: 4.5",
                        "minMassProbability: 5.6",
                        "maxMassProbability: 6.7",
                        "minEnergyProbability: 7.8",
                        "maxEnergyProbability: 8.9",
                        "inPlacePreference: 9.0",
                        "adjacentPreference: 0.9",
                        "mutationProb: 1.0",
                        "mutationSigma: 0.3"
                ), "/minMassThreshold must be > 0.0 \\(0.0\\)"
        ));
    }

    @Test
    void validate() throws IOException {
        JsonNode root = fromText(text(
                "---",
                "energyRef: B",
                "minMassThreshold: 0.1",
                "maxMassThreshold: 1.2",
                "minEnergyThreshold: 3.4",
                "maxEnergyThreshold: 4.5",
                "minMassProbability: 5.6",
                "maxMassProbability: 6.7",
                "minEnergyProbability: 7.8",
                "maxEnergyProbability: 8.9",
                "inPlacePreference: 9.0",
                "adjacentPreference: 0.9",
                "mutationProb: 1.0",
                "mutationSigma: 0.3"
        ));
        SchemaValidators.cloneGene().apply(root())
                .andThen(CrossValidators.cloneGene(KEYS).apply(root()))
                .accept(root);
        CloneGene gene = Parsers.cloneGene(root, KEYS);

        assertNotNull(gene);
        assertThat(gene.getMinLevels(), matrixCloseTo(
                MIN_MASS_THRESHOLD,
                MIN_ENERGY_THRESHOLD,
                MIN_MASS_PROBABILITY,
                MIN_ENERGY_PROBABILITY));
        assertThat(gene.getLevelRates(), matrixCloseTo(
                RATES_MASS_THRESHOLD,
                RATE_ENERGY_THRESHOLD,
                RATE_MASS_PROBABILITY,
                RATE_ENERGY_PROBABILITY));
        assertThat(gene.getLocationProb(), matrixCloseTo(
                of(IN_PLACE_PREFERENCE,
                        ADJACENT_PREFERENCE,
                        ADJACENT_PREFERENCE,
                        ADJACENT_PREFERENCE).softmaxi().cdfiRows()));
        assertThat(gene.getEnergyRef(), equalTo(ENERGY_REF));
        assertThat(gene.getMutationProb(), equalTo(MUTATION_PROBABILITY));
        assertThat(gene.getMutationSigma(), equalTo(MUTATION_SIGMA));
    }

    @ParameterizedTest
    @MethodSource("argsForError")
    void validateErrors(String text, String expectedPattern) {
        final IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                SchemaValidators.cloneGene().apply(root())
                        .andThen(CrossValidators.cloneGene(KEYS).apply(root()))
                        .accept(fromText(text)));
        assertThat(ex.getMessage(), matchesPattern(expectedPattern));
    }
}
