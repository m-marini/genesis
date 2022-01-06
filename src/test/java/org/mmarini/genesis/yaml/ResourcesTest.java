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

import java.io.IOException;
import java.util.List;
import java.util.stream.Stream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.matchesPattern;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mmarini.genesis.model3.MatrixMatchers.matrixCloseTo;
import static org.mmarini.genesis.yaml.TestUtils.text;
import static org.mmarini.yaml.Utils.fromText;
import static org.mmarini.yaml.schema.Locator.root;

class ResourcesTest {
    private static final List<String> KEYS = List.of("A", "B", "C");

    static Stream<Arguments> argsForNotNegativeError() {
        return Stream.of(Arguments.of(text(
                        "---",
                        "0"
                ), " must be an object \\(NUMBER\\)"
        ), Arguments.of(text(
                        "---",
                        "A: a"
                ), "/A must be a number \\(STRING\\)"
        ), Arguments.of(text(
                        "---",
                        "A: -0.1"
                ), "/A must be >= 0.0 \\(-0.1\\)"
        ), Arguments.of(text(
                        "---",
                        "A: 1",
                        "B: 2",
                        "C: 3",
                        "D: 4"
                ), "/D resource undefined"
        ));
    }

    static Stream<Arguments> argsForPositiveError() {
        return Stream.of(Arguments.of(text(
                        "---",
                        "0"
                ), " must be an object \\(NUMBER\\)"
        ), Arguments.of(text(
                        "---",
                        "A: a"
                ), "/A must be a number \\(STRING\\)"
        ), Arguments.of(text(
                        "---",
                        "A: 0"
                ), "/A must be > 0.0 \\(0.0\\)"
        ), Arguments.of(text(
                        "---",
                        "A: 1.0",
                        "B: 2.0",
                        "C: 3.0",
                        "D: 4.0"
                ), "/D resource undefined"
        ), Arguments.of(text(
                        "---",
                        "A: 1.0",
                        "B: 2.0"
                ), "/C is missing"
        ));
    }

    @Test
    void validateNonNegative() throws IOException {
        JsonNode root = fromText(text(
                "---",
                "A: 0",
                "B: 2"
        ));
        SchemaValidators.nonNegativeResources()
                .apply(root())
                .andThen(CrossValidators.nonNegativeResources(KEYS).apply(root()))
                .accept(root);
        assertThat(Parsers.resources(root, KEYS), matrixCloseTo(0, 2, 0));
    }

    @ParameterizedTest
    @MethodSource("argsForNotNegativeError")
    void validateNotNegativeErrors(String text, String expectedPattern) {
        final IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                SchemaValidators.nonNegativeResources()
                        .apply(root())
                        .andThen(CrossValidators.nonNegativeResources(KEYS)
                                .apply(root()))
                        .accept(fromText(text)));
        assertThat(ex.getMessage(), matchesPattern(expectedPattern));
    }

    @Test
    void validatePositive() throws IOException {
        JsonNode root = fromText(text(
                "---",
                "A: 1.0",
                "C: 3.0",
                "B: 2.0"
        ));
        SchemaValidators.positiveResources()
                .apply(root())
                .andThen(CrossValidators.positiveResources(KEYS)
                        .apply(root()))
                .accept(root);
        assertThat(Parsers.resources(root, KEYS), matrixCloseTo(1, 2, 3));
    }

    @ParameterizedTest
    @MethodSource("argsForPositiveError")
    void validatePositiveErrors(String text, String expectedPattern) {
        final IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                SchemaValidators.positiveResources()
                        .apply(root())
                        .andThen(
                                CrossValidators.positiveResources(KEYS).apply(root())
                        )
                        .accept(fromText(text)));
        assertThat(ex.getMessage(), matchesPattern(expectedPattern));
    }
}