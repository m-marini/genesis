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
import org.mmarini.genesis.model3.Reaction;
import org.mmarini.genesis.model3.ResourceGene;

import java.io.IOException;
import java.util.List;
import java.util.stream.Stream;

import static java.lang.Math.log;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.matchesPattern;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mmarini.genesis.model3.MatrixMatchers.matrixCloseTo;
import static org.mmarini.genesis.yaml.TestUtils.text;
import static org.mmarini.yaml.Utils.fromText;
import static org.mmarini.yaml.schema.Locator.root;

class ResourceGeneTest {
    static final List<String> KEYS = List.of("A", "B");

    static Stream<Arguments> argsForError() {
        return Stream.of(Arguments.of(text(
                        "#1",
                        "0"
                ), " must be an object \\(NUMBER\\)"
        ), Arguments.of(text(
                        "#2",
                        "  minLevel: 1",
                        "  maxLevel: 2",
                        "  reaction:",
                        "    reagents:",
                        "      B: 1",
                        "    products:",
                        "      A: 1",
                        "    thresholds:",
                        "      B: 0.2",
                        "    speeds:",
                        "      A: 1",
                        "      B: 2"
                ), "/ref is missing"
        ), Arguments.of(text(
                        "#3",
                        "  ref: B",
                        "  maxLevel: 2",
                        "  reaction:",
                        "    reagents:",
                        "      B: 1",
                        "    products:",
                        "      A: 1",
                        "    thresholds:",
                        "      B: 0.2",
                        "    speeds:",
                        "      A: 1",
                        "      B: 2"
                ), "/minLevel is missing"
        ), Arguments.of(text(
                        "#4",
                        "  ref: D",
                        "  minLevel: 1",
                        "  maxLevel: 2",
                        "  reaction:",
                        "    reagents:",
                        "      B: 1",
                        "    products:",
                        "      A: 1",
                        "    thresholds:",
                        "      B: 0.2",
                        "    speeds:",
                        "      A: 1",
                        "      B: 2"
                ), "/ref must match a value in \\[A, B\\] \\(D\\)"
        ), Arguments.of(text(
                        "#5",
                        "  ref: B",
                        "  minLevel: 1",
                        "  maxLevel: 2"
                ), "/reaction is missing"
        ));
    }

    @Test
    void validate() throws IOException {
        JsonNode root = fromText(text(
                "---",
                "  ref: B",
                "  minLevel: 1",
                "  maxLevel: 2",
                "  reaction:",
                "    reagents:",
                "      B: 1",
                "    products:",
                "      A: 1",
                "    thresholds:",
                "      B: 0.2",
                "    speeds:",
                "      A: 1",
                "      B: 2"
        ));
        SchemaValidators.resourceGene()
                .apply(root())
                .andThen(CrossValidators.resourceGene(KEYS).apply(root()))
                .accept(root);

        ResourceGene gene = Parsers.resourceGene(root, KEYS);
        assertNotNull(gene);
        assertThat(gene.getRef(), equalTo(1));
        assertThat(gene.getMinLevel(), equalTo(1.0));
        assertThat(gene.getLevelRate(), equalTo(log((2.0 / 1.0))));
        assertThat(gene.getNumSignals(), equalTo(1));
        Reaction reaction = gene.getReaction();
        assertNotNull(reaction);
        assertThat(reaction.getAlpha(), matrixCloseTo(1, -1));
        assertThat(reaction.getReagents(), matrixCloseTo(1));
        assertThat(reaction.getThresholds(), matrixCloseTo(0.2));
        assertThat(reaction.getSpeeds(), matrixCloseTo(1, 2));
    }

    @ParameterizedTest
    @MethodSource("argsForError")
    void validateErrors(String text, String expectedPattern) {
        final IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                SchemaValidators.resourceGene()
                        .apply(root())
                        .andThen(CrossValidators.resourceGene(KEYS).apply(root()))
                        .accept(fromText(text)));
        assertThat(ex.getMessage(), matchesPattern(expectedPattern));
    }
}