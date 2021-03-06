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
import org.mmarini.genesis.model3.PIPGene;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mmarini.genesis.yaml.TestUtils.text;
import static org.mmarini.yaml.Utils.fromText;
import static org.mmarini.yaml.schema.Locator.root;

class PIPGeneTest {
    static final List<String> KEYS = List.of("A", "B");

    static Stream<Arguments> argsForError() {
        return Stream.of(Arguments.of(text(
                        "#1"
                ), " must be an object \\(MISSING\\)"
        ), Arguments.of(text(
                        "#2",
                        "gene1:",
                        "  energyRef: A",
                        "  minMassThreshold: 0.1",
                        "  maxMassThreshold: 1.2",
                        "  minEnergyThreshold: 3.4",
                        "  maxEnergyThreshold: 4.5",
                        "  minMassProbability: 5.6",
                        "  maxMassProbability: 6.7",
                        "  minEnergyProbability: 7.8",
                        "  maxEnergyProbability: 8.9",
                        "  inPlacePreference: 9.0",
                        "  adjacentPreference: 0.9",
                        "  mutationProb: 1.0",
                        "  mutationSigma: 0.3",
                        "gene2:",
                        "  energyRef: C",
                        "  minMassThreshold: 0.1",
                        "  maxMassThreshold: 1.2",
                        "  minEnergyThreshold: 3.4",
                        "  maxEnergyThreshold: 4.5",
                        "  minMassProbability: 5.6",
                        "  maxMassProbability: 6.7",
                        "  minEnergyProbability: 7.8",
                        "  maxEnergyProbability: 8.9",
                        "  inPlacePreference: 9.0",
                        "  adjacentPreference: 0.9",
                        "  mutationProb: 1.0",
                        "  mutationSigma: 0.3"
                ), "/gene2/energyRef must match a value in \\[A, B\\] \\(C\\)"
        ));
    }

    @Test
    void validate() throws IOException {
        JsonNode root = fromText(text(
                "---",
                "gene1:",
                "  energyRef: A",
                "  minMassThreshold: 0.1",
                "  maxMassThreshold: 1.2",
                "  minEnergyThreshold: 3.4",
                "  maxEnergyThreshold: 4.5",
                "  minMassProbability: 5.6",
                "  maxMassProbability: 6.7",
                "  minEnergyProbability: 7.8",
                "  maxEnergyProbability: 8.9",
                "  inPlacePreference: 9.0",
                "  adjacentPreference: 0.9",
                "  mutationProb: 1.0",
                "  mutationSigma: 0.3",
                "gene2:",
                "  energyRef: B",
                "  minMassThreshold: 0.1",
                "  maxMassThreshold: 1.2",
                "  minEnergyThreshold: 3.4",
                "  maxEnergyThreshold: 4.5",
                "  minMassProbability: 5.6",
                "  maxMassProbability: 6.7",
                "  minEnergyProbability: 7.8",
                "  maxEnergyProbability: 8.9",
                "  inPlacePreference: 9.0",
                "  adjacentPreference: 0.9",
                "  mutationProb: 1.0",
                "  mutationSigma: 0.3"
        ));
        SchemaValidators.pipGenes()
                .apply(root())
                .andThen(CrossValidators.pipGenes(KEYS).apply(root()))
                .accept(root);

        Map<String, ? extends PIPGene> geneByName = Parsers.pipGenes(root, KEYS);
        assertNotNull(geneByName);
        assertThat(geneByName.size(), equalTo(2));
        assertThat(geneByName, allOf(hasKey("gene1"), hasKey("gene2")));
        assertThat(geneByName.get("gene1"),
                hasProperty("energyRef", equalTo(0)));
        assertThat(geneByName.get("gene2"),
                hasProperty("energyRef", equalTo(1)));
    }

    @ParameterizedTest
    @MethodSource("argsForError")
    void validateErrors(String text, String expectedPattern) {
        final IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                SchemaValidators.pipGenes()
                        .apply(root())
                        .andThen(CrossValidators.pipGenes(KEYS).apply(root()))
                        .accept(fromText(text)));
        assertThat(ex.getMessage(), matchesPattern(expectedPattern));
    }
}