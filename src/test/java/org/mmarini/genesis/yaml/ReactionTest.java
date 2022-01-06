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

import java.io.IOException;
import java.util.List;
import java.util.stream.Stream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.matchesPattern;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mmarini.genesis.model3.MatrixMatchers.matrixCloseTo;
import static org.mmarini.genesis.yaml.TestUtils.text;
import static org.mmarini.yaml.Utils.fromText;
import static org.mmarini.yaml.schema.Locator.root;

class ReactionTest {
    static final List<String> KEYS = List.of("A", "B", "C");

    static Stream<Arguments> argsForError() {
        return Stream.of(Arguments.of(text(
                        "#1",
                        "0"
                ), " must be an object \\(NUMBER\\)"
        ), Arguments.of(text("#2",
                        "  products: {}",
                        "  thresholds: {}",
                        "  speeds: {}"
                ), "/reagents is missing"
        ), Arguments.of(text("#3",
                        "  reagents: {}",
                        "  thresholds: {}",
                        "  speeds: {}"
                ), "/products is missing"
        ), Arguments.of(text("#4",
                        "  reagents: {}",
                        "  products: {}",
                        "  speeds: {}"
                ), "/thresholds is missing"
        ), Arguments.of(text("#5",
                        "  reagents:",
                        "    D: 1",
                        "  products: {}",
                        "  thresholds: {}",
                        "  speeds: {}"
                ), "/reagents/D resource undefined"
        ));
    }

    @Test
    void validate() throws IOException {
        JsonNode root = fromText(text(
                "reagents:",
                "  B: 1",
                "  A: 2",
                "products:",
                "  C: 2",
                "  A: 1",
                "thresholds:",
                "  A: 0.1",
                "  B: 0.2",
                "  C: 0.3",
                "speeds:",
                "  A: 1",
                "  B: 2"
        ));
        SchemaValidators.reaction().apply(root())
                .andThen(CrossValidators.reaction(KEYS).apply(root()))
                .accept(root);

        Reaction reaction = Parsers.reaction(root, KEYS);
        assertNotNull(reaction);
        assertThat(reaction.getReagents(), matrixCloseTo(2, 1));
        assertThat(reaction.getAlpha(), matrixCloseTo(-1, -1, 2));
        assertThat(reaction.getThresholds(), matrixCloseTo(0.1, 0.2));
        assertThat(reaction.getSpeeds(), matrixCloseTo(1, 2));
    }

    @ParameterizedTest
    @MethodSource("argsForError")
    void validateErrors(String text, String expectedPattern) {
        final IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                SchemaValidators.reaction().apply(root())
                        .andThen(CrossValidators.reaction(KEYS).apply(root()))
                        .accept(fromText(text)));
        assertThat(ex.getMessage(), matchesPattern(expectedPattern));
    }
}