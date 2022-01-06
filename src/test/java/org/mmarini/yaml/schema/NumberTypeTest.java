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

package org.mmarini.yaml.schema;

import com.fasterxml.jackson.databind.JsonNode;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.util.function.Consumer;
import java.util.stream.Stream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.matchesPattern;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mmarini.genesis.yaml.TestUtils.text;
import static org.mmarini.yaml.Utils.fromText;
import static org.mmarini.yaml.schema.Locator.root;
import static org.mmarini.yaml.schema.Validator.*;

class NumberTypeTest {
    static Stream<Arguments> argsForErrors() {
        return Stream.of(Arguments.of(
                number(),
                text("#1",
                        "a"
                ), " must be a number \\(STRING\\)"
        ), Arguments.of(
                number(),
                text("#2",
                        "[]"
                ), " must be a number \\(ARRAY\\)"
        ), Arguments.of(
                number(),
                text("#3",
                        "{}"
                ), " must be a number \\(OBJECT\\)"
        ), Arguments.of(
                number(),
                text("#4"
                ), " must be a number \\(MISSING\\)"
        ), Arguments.of(
                number(),
                text("#5",
                        "null"
                ), " must be a number \\(NULL\\)"
        ), Arguments.of(
                number(minimum(0d)),
                text("#6",
                        "-1e-6"
                ), " must be >= 0.0 \\(-1.0E-6\\)"
        ), Arguments.of(
                number(exclusiveMinimum(0d)),
                text("#7",
                        "0"
                ), " must be > 0.0 \\(0.0\\)"
        ), Arguments.of(
                number(maximum(0d)),
                text("#8",
                        "1e-6"
                ), " must be <= 0.0 \\(1.0E-6\\)"
        ), Arguments.of(
                number(exclusiveMaximum(0d)),
                text("#9",
                        "0"
                ), " must be < 0.0 \\(0.0\\)"
        ), Arguments.of(
                nonNegativeNumber(),
                text("#10",
                        "-1"
                ), " must be >= 0.0 \\(-1.0\\)"
        ), Arguments.of(
                positiveNumber(),
                text("#11",
                        "0"
                ), " must be > 0.0 \\(0.0\\)"
        ), Arguments.of(
                nonPositiveNumber(),
                text("#12",
                        "1e-6"
                ), " must be <= 0.0 \\(1.0E-6\\)"
        ), Arguments.of(
                negativeNumber(),
                text("#13",
                        "0"
                ), " must be < 0.0 \\(0.0\\)"
        ));
    }

    static Stream<Arguments> argsForValidate() {
        return Stream.of(Arguments.of(
                number(),
                text("#1",
                        "0"
                )
        ), Arguments.of(
                number(minimum(0d)),
                text("#2",
                        "0"
                )
        ), Arguments.of(
                number(exclusiveMinimum(0d)),
                text("#3",
                        "1e-6"
                )
        ), Arguments.of(
                number(maximum(0d)),
                text("#4",
                        "0"
                )
        ), Arguments.of(
                number(exclusiveMaximum(0d)),
                text("#5",
                        "-1e-6"
                )
        ));
    }

    @ParameterizedTest
    @MethodSource("argsForValidate")
    void validate(Validator schema, String text) throws IOException {
        // Given a string type
        // and the generated validator
        Consumer<JsonNode> validator = schema.apply(root());

        // When validating a string node
        validator.accept(fromText(text));
    }

    @ParameterizedTest
    @MethodSource("argsForErrors")
    void validateOnErrors(Validator schema, String text, String expectedPattern) {
        final IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                schema.apply(root())
                        .accept(fromText(text)));
        assertThat(ex.getMessage(), matchesPattern(expectedPattern));
    }
}