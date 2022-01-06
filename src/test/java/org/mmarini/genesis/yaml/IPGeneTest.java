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
import org.mmarini.genesis.model3.IPGene;

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

class IPGeneTest {
    static final List<String> KEYS = List.of("A", "B");

    static Stream<Arguments> argsForError() {
        return Stream.of(Arguments.of(text(
                        "#1"
                ), " must be an object \\(MISSING\\)"
        ), Arguments.arguments(text(
                        "---",
                        "gene1:",
                        "  ref: A",
                        "  minLevel: 1",
                        "  maxLevel: 10",
                        "  reaction:",
                        "    reagents:",
                        "      A: 1",
                        "    products:",
                        "      B: 2",
                        "    thresholds:",
                        "      A: 0.5",
                        "    speeds:",
                        "      A: 0.1",
                        "gene2:",
                        "  ref: C",
                        "  minLevel: 2",
                        "  maxLevel: 20",
                        "  reaction:",
                        "    reagents:",
                        "      B: 2",
                        "    products:",
                        "      A: 1",
                        "    thresholds:",
                        "      A: 0.6",
                        "    speeds:",
                        "      A: 0.2"
                ), "/gene2/ref must match a value in \\[A, B\\] \\(C\\)"
        ));
    }

    @Test
    void validate() throws IOException {
        JsonNode root = fromText(text(
                "---",
                "gene1:",
                "  ref: A",
                "  minLevel: 1",
                "  maxLevel: 10",
                "  reaction:",
                "    reagents:",
                "      A: 1",
                "    products:",
                "      B: 2",
                "    thresholds:",
                "      A: 0.5",
                "    speeds:",
                "      A: 0.1",
                "gene2:",
                "  ref: B",
                "  minLevel: 2",
                "  maxLevel: 20",
                "  reaction:",
                "    reagents:",
                "      B: 2",
                "    products:",
                "      A: 1",
                "    thresholds:",
                "      A: 0.6",
                "    speeds:",
                "      A: 0.2"
        ));
        SchemaValidators.ipGenes()
                .apply(root())
                .andThen(CrossValidators.ipGenes(KEYS).apply(root()))
                .accept(root);
        Map<String, ? extends IPGene> geneByName = Parsers.ipGenes(root, KEYS);
        assertNotNull(geneByName);
        assertThat(geneByName.size(), equalTo(2));
        assertThat(geneByName, allOf(hasKey("gene1"), hasKey("gene2")));
        assertThat(geneByName.get("gene1"), hasProperty("ref", equalTo(0)));
        assertThat(geneByName.get("gene2"), hasProperty("ref", equalTo(1)));
    }

    @ParameterizedTest
    @MethodSource("argsForError")
    void validateErrors(String text, String expectedPattern) {
        final IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                SchemaValidators.ipGenes()
                        .apply(root())
                        .andThen(CrossValidators.ipGenes(KEYS).apply(root()))
                        .accept(fromText(text)));
        assertThat(ex.getMessage(), matchesPattern(expectedPattern));
    }
}