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

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.util.List;
import java.util.stream.Stream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.matchesPattern;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mmarini.genesis.yaml.TestUtils.text;
import static org.mmarini.yaml.Utils.fromText;
import static org.mmarini.yaml.schema.Locator.root;
import static org.mmarini.yaml.schema.Validator.*;

class ArrayTypeTest {
    static Stream<Arguments> argsForErrors() {
        return Stream.of(Arguments.of(
                array(string()),
                text("#1",
                        "a"
                ),
                " must be an array \\(STRING\\)"
        ), Arguments.of(
                array(string()),
                text("#2",
                        "0"
                ),
                " must be an array \\(NUMBER\\)"
        ), Arguments.of(
                array(string()),
                text("#3",
                        "0.5"
                ),
                " must be an array \\(NUMBER\\)"
        ), Arguments.of(
                array(string()),
                text("#4",
                        "a: a"
                ),
                " must be an array \\(OBJECT\\)"
        ), Arguments.of(
                array(string()),
                text("#5"
                ),
                " must be an array \\(MISSING\\)"
        ), Arguments.of(
                array(string()),
                text("#6",
                        "null"
                ),
                " must be an array \\(NULL\\)"
        ), Arguments.of(
                arrayItems(string()),
                text("#7",
                        "- 0"
                ),
                "/0 must be a string \\(NUMBER\\)"
        ), Arguments.of(
                arrayItems(string()),
                text("#8",
                        "- a",
                        "- 0"
                ),
                "/1 must be a string \\(NUMBER\\)"
        ), Arguments.of(
                arrayPrefixItems(List.of(integer(), number())),
                text("#9",
                        "- a"
                ),
                "/0 must be an integer \\(STRING\\)"
        ), Arguments.of(
                arrayPrefixItems(List.of(integer(), number())),
                text("#10",
                        "- 0",
                        "- a"
                ),
                "/1 must be a number \\(STRING\\)"
        ), Arguments.of(
                arrayPrefixItemsAndItems(List.of(integer(), number()), string()),
                text("#11",
                        "- a"
                ),
                "/0 must be an integer \\(STRING\\)"
        ), Arguments.of(
                arrayPrefixItemsAndItems(List.of(integer(), number()), string()),
                text("#12",
                        "- 0",
                        "- a"
                ),
                "/1 must be a number \\(STRING\\)"
        ), Arguments.of(
                arrayPrefixItemsAndItems(List.of(integer(), number()), string()),
                text("#13",
                        "- 0",
                        "- 1.2",
                        "- 0"
                ),
                "/2 must be a string \\(NUMBER\\)"
        ), Arguments.of(
                arrayPrefixItemsAndItems(List.of(integer(), number()), string()),
                text("#14",
                        "- 0",
                        "- 1.2",
                        "- a",
                        "- 0"
                ),
                "/3 must be a string \\(NUMBER\\)"
        ), Arguments.of(
                array(items(string()), minItems(2)),
                text("#15",
                        "- a"
                ),
                " must have at least 2 items \\(1\\)"
        ), Arguments.of(
                array(items(string()), maxItems(1)),
                text("#16",
                        "- a",
                        "- b"
                ),
                " must have at most 1 items \\(2\\)"
        ));
    }

    static Stream<Arguments> argsForValidate() {
        return Stream.of(Arguments.of(
                arrayItems(string()),
                text("#1",
                        "[]"
                )
        ), Arguments.of(
                arrayItems(string()),
                text("#2",
                        "- a",
                        "- b"
                )
        ), Arguments.of(
                arrayPrefixItems(List.of(integer(), number())),
                text("#3",
                        "- 0"
                )
        ), Arguments.of(
                arrayPrefixItems(List.of(integer(), number())),
                text("#3",
                        "- 0",
                        "- 0.5"
                )
        ), Arguments.of(
                arrayPrefixItems(List.of(integer(), number())),
                text("#3",
                        "- 0",
                        "- 0.5",
                        "- 0"
                )
        ), Arguments.of(
                arrayPrefixItemsAndItems(List.of(integer(), number()), string()),
                text("#3",
                        "- 0"
                )
        ), Arguments.of(
                arrayPrefixItemsAndItems(List.of(integer(), number()), string()),
                text("#3",
                        "- 0",
                        "- 1.2"
                )
        ), Arguments.of(
                arrayPrefixItemsAndItems(List.of(integer(), number()), string()),
                text("#3",
                        "- 0",
                        "- 1.2",
                        "- a",
                        "- b"
                )
        ), Arguments.of(
                array(items(string()), minItems(2)),
                text("#3",
                        "- a",
                        "- b"
                )
        ), Arguments.of(
                array(items(string()), maxItems(2)),
                text("#3",
                        "- a",
                        "- b"
                )
        ));
    }

    @ParameterizedTest
    @MethodSource("argsForValidate")
    void validate(Validator schema, String text) throws IOException {
        // Given a string type
        // and the generated validator
        // When validating a string node
        schema.apply(root())
                .accept(fromText(text));
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