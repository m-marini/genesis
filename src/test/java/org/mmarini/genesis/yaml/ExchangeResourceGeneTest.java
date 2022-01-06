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
import org.mmarini.genesis.model3.ExchangeResourceGene;

import java.io.IOException;
import java.util.List;
import java.util.stream.Stream;

import static java.lang.Math.log;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.matchesPattern;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mmarini.genesis.model3.MatrixMatchers.matrixCloseTo;
import static org.mmarini.genesis.yaml.TestUtils.text;
import static org.mmarini.yaml.Utils.fromText;
import static org.mmarini.yaml.schema.Locator.root;

class ExchangeResourceGeneTest {
    static final List<String> KEYS = List.of("A", "B");

    static Stream<Arguments> argsForError() {
        return Stream.of(Arguments.of(text(
                        "#1",
                        "0"
                ), " must be an object \\(NUMBER\\)"
        ), Arguments.of(text(
                        "#2",
                        "maxLevels:",
                        "  A: 3",
                        "  B: 4",
                        "rates:",
                        "  A: 0.5",
                        "  B: 1.5"
                ), "/minLevels is missing"
        ), Arguments.of(text(
                        "#3",
                        "minLevels:",
                        "  A: 1",
                        "  B: 2",
                        "rates:",
                        "  A: 0.5",
                        "  B: 1.5"
                ), "/maxLevels is missing"
        ), Arguments.of(text(
                        "#4",
                        "minLevels:",
                        "  A: 1",
                        "  B: 2",
                        "maxLevels:",
                        "  A: 3",
                        "  B: 4"
                ), "/rates is missing"
        ), Arguments.of(text(
                        "#5",
                        "minLevels:",
                        "  A: 0",
                        "  B: 2",
                        "maxLevels:",
                        "  A: 3",
                        "  B: 4",
                        "rates:",
                        "  A: 1",
                        "  B: 1.5"
                ), "/minLevels/A must be > 0.0 \\(0.0\\)"
        ), Arguments.of(text(
                        "#6",
                        "minLevels:",
                        "  A: 1",
                        "  B: 2",
                        "maxLevels:",
                        "  A: 3",
                        "  B: 0",
                        "rates:",
                        "  A: 1",
                        "  B: 1.5"
                ), "/maxLevels/B must be > 0.0 \\(0.0\\)"
        ), Arguments.of(text(
                        "#7",
                        "minLevels:",
                        "  A: 1",
                        "  B: 2",
                        "maxLevels:",
                        "  A: 3",
                        "  B: 4",
                        "rates:",
                        "  A: 0",
                        "  B: 1.5"
                ), "/rates/A must be > 0.0 \\(0.0\\)"
        ), Arguments.of(text(
                        "#8",
                        "minLevels:",
                        "  A: a",
                        "  B: 2",
                        "maxLevels:",
                        "  A: 3",
                        "  B: 4",
                        "rates:",
                        "  A: 0",
                        "  B: 1.5"
                ), "/minLevels/A must be a number \\(STRING\\)"
        ), Arguments.of(text(
                        "#9",
                        "minLevels:",
                        "  A: 1",
                        "  B: 2",
                        "  D: 2",
                        "maxLevels:",
                        "  A: 3",
                        "  B: 4",
                        "rates:",
                        "  A: 1",
                        "  B: 1.5"
                ), "/minLevels/D resource undefined"
        ), Arguments.of(text(
                        "#10",
                        "minLevels:",
                        "  A: 1",
                        "  B: 2",
                        "maxLevels:",
                        "  A: 3",
                        "  B: 4",
                        "  D: 2",
                        "rates:",
                        "  A: 1",
                        "  B: 1.5"
                ), "/maxLevels/D resource undefined"
        ), Arguments.of(text(
                        "#11",
                        "minLevels:",
                        "  A: 1",
                        "  B: 2",
                        "maxLevels:",
                        "  A: 3",
                        "  B: 4",
                        "rates:",
                        "  A: 1",
                        "  B: 1.5",
                        "  D: 2"
                ), "/rates/D resource undefined"
        ));
    }

    @Test
    void validate() throws IOException {
        JsonNode root = fromText(text(
                "---",
                "minLevels:",
                "  A: 1",
                "  B: 2",
                "maxLevels:",
                "  A: 3",
                "  B: 4",
                "rates:",
                "  A: 0.5",
                "  B: 1.5"
        ));
        SchemaValidators.exchangeResourceGene()
                .apply(root())
                .andThen(CrossValidators.exchangeResourceGene(KEYS).apply(root()))
                .accept(root);
        ExchangeResourceGene gene = Parsers.exchangeResourceGene(root, KEYS);
        assertNotNull(gene);
        assertThat(gene.getMinLevels(), matrixCloseTo(1, 2));
        assertThat(gene.getLogRates(), matrixCloseTo(
                log(3.0), log(4.0 / 2)
        ));
        assertThat(gene.getRates(), matrixCloseTo(
                0.5, 1.5
        ));
    }

    @ParameterizedTest
    @MethodSource("argsForError")
    void validateErrors(String text, String expectedPattern) {
        final IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                SchemaValidators.exchangeResourceGene()
                        .apply(root())
                        .andThen(CrossValidators.exchangeResourceGene(KEYS).apply(root()))
                        .accept(fromText(text)));
        assertThat(ex.getMessage(), matchesPattern(expectedPattern));
    }
}