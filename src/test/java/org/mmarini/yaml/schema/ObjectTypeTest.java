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
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Stream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.matchesPattern;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mmarini.genesis.yaml.TestUtils.text;
import static org.mmarini.yaml.Utils.fromText;
import static org.mmarini.yaml.schema.Locator.root;
import static org.mmarini.yaml.schema.Validator.*;

class ObjectTypeTest {
    static Stream<Arguments> argsForErrors() {
        return Stream.of(Arguments.of(
                object(string()),
                text("#1",
                        "a"
                ),
                " must be an object \\(STRING\\)"
        ), Arguments.of(
                object(string()),
                text("#2",
                        "0"
                ),
                " must be an object \\(NUMBER\\)"
        ), Arguments.of(
                object(string()),
                text("#3",
                        "1.5"
                ),
                " must be an object \\(NUMBER\\)"
        ), Arguments.of(
                object(string()),
                text("#4",
                        "[]"
                ),
                " must be an object \\(ARRAY\\)"
        ), Arguments.of(
                object(string()),
                text("#5"
                ),
                " must be an object \\(MISSING\\)"
        ), Arguments.of(
                object(string()),
                text("#6",
                        "null"
                ),
                " must be an object \\(NULL\\)"
        ), Arguments.of(
                objectAdditionalProperties(string()),
                text("#7",
                        "a: 0"
                ),
                "/a must be a string \\(NUMBER\\)"
        ), Arguments.of(
                objectAdditionalProperties(string()),
                text("#8",
                        "a: a",
                        "b: 0"
                ),
                "/b must be a string \\(NUMBER\\)"
        ), Arguments.of(
                objectProperties(Map.of("a", string())),
                text("#9",
                        "a: 0"
                ),
                "/a must be a string \\(NUMBER\\)"
        ), Arguments.of(
                objectProperties(Map.of("a", string(), "b", number())),
                text("#10",
                        "a: a",
                        "b: b"
                ),
                "/b must be a number \\(STRING\\)"
        ), Arguments.of(
                objectPropertiesRequired(Map.of("a", string(), "b", number()), List.of("a")),
                text("#11",
                        "b: 0"
                ),
                "/a is missing"
        ), Arguments.of(
                objectPropertiesRequiredAdditionalProperties(Map.of("a", string(), "b", number()), List.of("a"), string()),
                text("#12",
                        "a: a",
                        "c: 0"
                ),
                "/c must be a string \\(NUMBER\\)"
        ), Arguments.of(
                object(additionalProperties(string()), minProperties(2)),
                text("#13",
                        "a: a"
                ),
                " must have at least 2 properties \\(1\\)"
        ), Arguments.of(
                object(additionalProperties(string()), maxProperties(1)),
                text("#14",
                        "a: a",
                        "b: b"
                ), " must have at most 1 properties \\(2\\)"
        ), Arguments.of(
                objectProperties(Map.of("a",
                        objectPropertiesRequired(Map.of("b", string()),
                                List.of("b")))),
                text("#8",
                        "a:",
                        "  c: d"
                ), "/a/b is missing"
        ));
    }

    static Stream<Arguments> argsForValidate() {
        return Stream.of(Arguments.of(
                objectAdditionalProperties(string()),
                text("#1",
                        "a: a",
                        "b: b"
                )
        ), Arguments.of(
                objectProperties(Map.of("a", string(), "b", integer())),
                text("#2",
                        "a: a",
                        "b: 0"
                )
        ), Arguments.of(
                objectPropertiesRequired(Map.of("a", string(), "b", integer()), List.of("a")),
                text("#3",
                        "a: a"
                )
        ), Arguments.of(
                objectPropertiesRequired(Map.of("a", string(), "b", integer()), List.of("a")),
                text("#4",
                        "a: a",
                        "b: 0"
                )
        ), Arguments.of(
                objectPropertiesRequiredAdditionalProperties(Map.of("a", string(), "b", integer()), List.of("a"), number()),
                text("#5",
                        "a: a",
                        "b: 0",
                        "c: 1.5"
                )
        ), Arguments.of(
                object(additionalProperties(string()), minProperties(1)),
                text("#6",
                        "a: a"
                )
        ), Arguments.of(
                object(additionalProperties(string()), maxProperties(1)),
                text("#7",
                        "a: a"
                )
        ), Arguments.of(
                objectProperties(Map.of("a",
                        objectPropertiesRequired(Map.of("b", string()),
                                List.of("b")))),
                text("#8",
                        "a:",
                        "  b: c"
                )
        ));
    }

    @ParameterizedTest
    @MethodSource("argsForValidate")
    void validate(Validator schema, String text) throws IOException {
        // Given a schema validation
        // and a yaml document
        // and the generated validator
        Consumer<JsonNode> validator = schema.apply(root());

        // When applying the validation
        validator.accept(fromText(text));
        // than no error should be thrown
        assertNotNull(validator);
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