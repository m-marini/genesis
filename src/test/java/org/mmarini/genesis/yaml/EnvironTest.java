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

import java.awt.*;
import java.io.IOException;
import java.util.List;
import java.util.stream.Stream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.matchesPattern;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mmarini.genesis.model3.MatrixMatchers.matrixCloseTo;
import static org.mmarini.genesis.yaml.TestUtils.text;
import static org.mmarini.yaml.Utils.fromText;
import static org.mmarini.yaml.schema.Locator.root;

class EnvironTest {
    static final List<String> KEYS = List.of("A", "B");

    static Stream<Arguments> argsForError() {
        return Stream.of(Arguments.of(text(
                        "#1",
                        "0"
                ), " must be an object \\(NUMBER\\)"
        ), Arguments.of(text(
                        "#2",
                        "height: 8",
                        "length: 2",
                        "resources:",
                        "  A: 1.0",
                        "  B: 2.0",
                        "diffusion:",
                        "  B: 0.2",
                        "  A: 0.1"
                ), "/width is missing"
        ), Arguments.of(text(
                        "#3",
                        "  width: 4",
                        "  length: 2",
                        "  resources:",
                        "    A: 1.0",
                        "    B: 2.0",
                        "  resourceFlows:",
                        "    B: 2.0",
                        "  diffusion:",
                        "    B: 0.2",
                        "    A: 0.1"
                ), "/height is missing"
        ), Arguments.of(text(
                        "#4",
                        "  width: 4",
                        "  height: 8",
                        "  resources:",
                        "    A: 1.0",
                        "    B: 2.0",
                        "  resourceFlows:",
                        "    B: 2.0",
                        "  diffusion:",
                        "    B: 0.2",
                        "    A: 0.1"
                ), "/length is missing"
        ), Arguments.of(text(
                        "#5",
                        "  width: 4",
                        "  height: 8",
                        "  length: 2",
                        "  resourceFlows:",
                        "    B: 2.0",
                        "  diffusion:",
                        "    B: 0.2",
                        "    A: 0.1"
                ), "/resources is missing"
        ), Arguments.of(text(
                        "#6",
                        "  width: 4",
                        "  height: 8",
                        "  length: 2",
                        "  resources:",
                        "    A: 1.0",
                        "    B: 2.0",
                        "  resourceFlows:",
                        "    B: 2.0"
                ), "/diffusion is missing"
        ), Arguments.of(text(
                        "#7",
                        "  width: 0",
                        "  height: 8",
                        "  length: 2",
                        "  resources:",
                        "    A: 1.0",
                        "    B: 2.0",
                        "  resourceFlows:",
                        "    B: 2.0",
                        "  diffusion:",
                        "    B: 0.2",
                        "    A: 0.1"
                ), "/width must be > 0 \\(0\\)"
        ), Arguments.of(text(
                        "#8",
                        "  width: 4",
                        "  height: 0",
                        "  length: 2",
                        "  resources:",
                        "    A: 1.0",
                        "    B: 2.0",
                        "  resourceFlows:",
                        "    B: 2.0",
                        "  diffusion:",
                        "    B: 0.2",
                        "    A: 0.1"
                ), "/height must be > 0 \\(0\\)"
        ), Arguments.of(text(
                        "#9",
                        "  width: 4",
                        "  height: 8",
                        "  length: 0",
                        "  resources:",
                        "    A: 1.0",
                        "    B: 2.0",
                        "  resourceFlows:",
                        "    B: 2.0",
                        "  diffusion:",
                        "    B: 0.2",
                        "    A: 0.1"
                ), "/length must be > 0.0 \\(0.0\\)"
        ), Arguments.of(text(
                        "#10",
                        "  width: 4",
                        "  height: 8",
                        "  length: 2",
                        "  resources:",
                        "    A: -0.1",
                        "    B: 2.0",
                        "  resourceFlows:",
                        "    B: 2.0",
                        "  diffusion:",
                        "    B: 0.2",
                        "    A: 0.1"
                ), "/resources/A must be >= 0.0 \\(-0.1\\)"
        ), Arguments.of(text(
                        "#11",
                        "  width: 4",
                        "  height: 8",
                        "  length: 2",
                        "  resources:",
                        "    A: 1.0",
                        "    B: 2.0",
                        "  resourceFlows:",
                        "    B: 2.0",
                        "  diffusion:",
                        "    B: 0.2",
                        "    A: 0"
                ), "/diffusion/A must be > 0.0 \\(0.0\\)"
        ), Arguments.of(text(
                        "#12",
                        "  width: 1",
                        "  height: 8",
                        "  length: 2",
                        "  resources:",
                        "    A: 1.0",
                        "    B: 2.0",
                        "  resourceFlows:",
                        "    B: 2.0",
                        "  diffusion:",
                        "    B: 0.2",
                        "    A: 0.1"
                ), "/width must be an even integer \\(1\\)"
        ), Arguments.of(text(
                        "#13",
                        "  width: 4",
                        "  height: 3",
                        "  length: 2",
                        "  resources:",
                        "    A: 1.0",
                        "    B: 2.0",
                        "resourceFlows:",
                        "  B: 2.0",
                        "  diffusion:",
                        "    B: 0.2",
                        "    A: 0.1"
                ), "/height must be an even integer \\(3\\)"
        ));
    }

    @Test
    void validate() throws IOException {
        JsonNode root = fromText(text(
                "---",
                "width: 4",
                "height: 8",
                "length: 2",
                "resources:",
                "  A: 1.0",
                "diffusion:",
                "  B: 0.2",
                "  A: 0.1",
                "resourceFlows:",
                "  B: 2.0"
        ));
        SchemaValidators.environ().apply(root())
                .andThen(CrossValidators.environ(KEYS).apply(root()))
                .accept(root);

        assertThat(root.path("length").asDouble(0), equalTo(2.0));
        assertThat(Parsers.resources(root.path("resources"), KEYS), matrixCloseTo(1, 0));
        assertThat(Parsers.resources(root.path("diffusion"), KEYS), matrixCloseTo(0.1, 0.2));
        assertThat(Parsers.resources(root.path("resourceFlows"), KEYS), matrixCloseTo(0, 2));
        assertThat(Parsers.dimension(root), equalTo(new Dimension(4, 8)));
    }

    @ParameterizedTest
    @MethodSource("argsForError")
    void validateErrors(String text, String expectedPattern) {
        final IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                SchemaValidators.environ().apply(root())
                        .andThen(CrossValidators.environ(KEYS).apply(root()))
                        .accept(fromText(text)));
        assertThat(ex.getMessage(), matchesPattern(expectedPattern));
    }
}