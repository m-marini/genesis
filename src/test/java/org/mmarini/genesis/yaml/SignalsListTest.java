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
import org.mmarini.genesis.model3.Matrix;

import java.io.IOException;
import java.util.List;
import java.util.stream.Stream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.matchesPattern;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mmarini.genesis.model3.MatrixMatchers.matrixCloseTo;
import static org.mmarini.genesis.yaml.TestUtils.text;
import static org.mmarini.yaml.Utils.fromText;
import static org.mmarini.yaml.schema.Locator.root;

class SignalsListTest {
    static Stream<Arguments> argsForError() {
        return Stream.of(Arguments.of(text(
                        "#1",
                        "0"
                ), " must be an array \\(NUMBER\\)"
        ), Arguments.of(text(
                        "#2",
                        "- [ 0.1, a, 5.6 ]",
                        "- [ 7.8, 9.0 ]"
                ), "/0/1 must be a number \\(STRING\\)"
        ), Arguments.of(text(
                        "#3",
                        "- [ 0.1, 0.3, 0.4 ]",
                        "- [ 0.5, 2 ]"
                ), "/1/1 must be <= 1.0 \\(2.0\\)"
        ), Arguments.of(text(
                        "#4",
                        "- [ 0.1, 0.4, 1 ]",
                        "- [ 0.5, 0]",
                        "- [ 0.5, 0]"
                ), " must have at most 2 items \\(3\\)"
        ), Arguments.of(text(
                        "#5",
                        "- [ 0.1, 0.4, 1 ]"
                ), " must have at least 2 items \\(1\\)"
        ), Arguments.of(text(
                        "#6",
                        "- [ 0.1, 0.4 ]",
                        "- [ 0.5, 1 ]"
                ), "/0 must have at least 3 items \\(2\\)"
        ), Arguments.of(text(
                        "#7",
                        "- [ 0.1, 0.4, 1 ]",
                        "- [ 0.5, 1 ,0]"
                ), "/1 must have at most 2 items \\(3\\)"
        ));
    }

    @Test
    void validate() throws IOException {
        JsonNode root = fromText(text(
                "---",
                "- [ 0, 0.5, 1 ]",
                "- [ 0.25, 0.75 ]"
        ));
        SchemaValidators.signalsList()
                .apply(root())
                .andThen(CrossValidators.signalsList(3, 2).apply(root()))
                .accept(root);
        List<Matrix> signals = Parsers.signalsList(root);
        assertThat(signals, contains(
                matrixCloseTo(0, 0.5, 1),
                matrixCloseTo(0.25, 0.75)
        ));
    }

    @ParameterizedTest
    @MethodSource("argsForError")
    void validateErrors(String text, String expectedPattern) {
        final IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                SchemaValidators.signalsList()
                        .apply(root())
                        .andThen(CrossValidators.signalsList(3, 2).apply(root()))
                        .accept(fromText(text)));
        assertThat(ex.getMessage(), matchesPattern(expectedPattern));
    }
}