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
import org.mmarini.genesis.model3.*;

import java.io.IOException;
import java.util.List;
import java.util.stream.Stream;

import static java.lang.Math.sqrt;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mmarini.genesis.model3.MatrixMatchers.matrixCloseTo;
import static org.mmarini.genesis.yaml.TestUtils.text;
import static org.mmarini.yaml.Utils.fromText;
import static org.mmarini.yaml.schema.Locator.root;

class IndividualTest {
    static final List<String> KEYS = List.of("A", "B");
    private static final double MIN_LEVEL = 1;
    private static final double MAX_LEVEL = 10;

    static Stream<Arguments> argsForErrors() {
        return Stream.of(Arguments.of(text(
                        "#1",
                        "0"
                ), " must be an object \\(NUMBER\\)"
        ), Arguments.of(text(
                        "# 2",
                        "reactionGenes:",
                        "  - [ 0.1 ]",
                        "EIPSignals:",
                        "  - [ 0.3, 0.4 ]",
                        "  - [ 0.35, 0.45 ]",
                        "PIPSignals:",
                        "  - [ 0.5, 0.6 ]",
                        "resources:",
                        "  A: 6000"
                ), "/location is missing"
        ), Arguments.of(text(
                        "# 3",
                        "location: 2",
                        "reactionGenes:",
                        "  - [ 0.1 ]",
                        "EIPSignals:",
                        "  - [ 0.3, 0.4 ]",
                        "  - [ 0.35, 0.45 ]",
                        "PIPSignals:",
                        "  - [ 0.5, 0.6 ]"
                ), "/resources is missing"
        ), Arguments.of(text(
                        "# 4",
                        "location: 2",
                        "photoGenes: []",
                        "EIPSignals:",
                        "  - [ 0.3, 0.4 ]",
                        "  - [ 0.35, 0.45 ]",
                        "PIPSignals:",
                        "  - [ 0.5, 0.6 ]",
                        "resources:",
                        "  A: 6000"
                ), "/reactionGenes is missing"
        ), Arguments.of(text(
                        "# 5",
                        "location: 2",
                        "photoGenes: []",
                        "reactionGenes:",
                        "  - [ 0.1 ]",
                        "PIPSignals:",
                        "  - [ 0.5, 0.6 ]",
                        "resources:",
                        "  A: 6000"
                ), "/EIPSignals is missing"
        ), Arguments.of(text(
                        "# 6",
                        "location: 2",
                        "photoGenes: []",
                        "reactionGenes:",
                        "  - [ 0.1 ]",
                        "EIPSignals:",
                        "  - [ 0.3 ]",
                        "  - [ 0.35 ]",
                        "resources:",
                        "  A: 6000"
                ), "/PIPSignals is missing"
        ), Arguments.of(text(
                        "# 7",
                        "location: -1",
                        "photoGenes: []",
                        "reactionGenes:",
                        "  - [ 0.1 ]",
                        "EIPSignals:",
                        "  - [ 0.3, 0.4 ]",
                        "  - [ 0.35, 0.45 ]",
                        "PIPSignals:",
                        "  - [ 0.5, 0.6 ]",
                        "resources:",
                        "  A: 6000"
                ), "/location must be >= 0 \\(-1\\)"
        ), Arguments.of(text(
                        "# 8",
                        "location: 4",
                        "photoGenes: []",
                        "reactionGenes:",
                        "  - [ 0.1 ]",
                        "EIPSignals:",
                        "  - [ 0.3, 0.4 ]",
                        "  - [ 0.35, 0.45 ]",
                        "PIPSignals:",
                        "  - [ 0.5, 0.6 ]",
                        "resources:",
                        "  A: 6000"
                ), "/location must be < 4 \\(4\\)"
        ), Arguments.of(text(
                        "# 9",
                        "location: 2",
                        "photoGenes:",
                        "  - [ 1 ]",
                        "reactionGenes:",
                        "  - [ 0.1 ]",
                        "  - [ 0.1 ]",
                        "EIPSignals:",
                        "  - [ 0.3, 0.4 ]",
                        "  - [ 0.35, 0.45 ]",
                        "PIPSignals:",
                        "  - [ 0.5, 0.6 ]",
                        "resources:",
                        "  A: 6000"
                ), "/reactionGenes must have at most 1 items \\(2\\)"
        ), Arguments.of(text(
                        "# 10",
                        "location: 2",
                        "photoGenes:",
                        "  - [ 1 ]",
                        "reactionGenes:",
                        "  - [ 0.1 ]",
                        "EIPSignals:",
                        "  - [ 0.3 ]",
                        "PIPSignals:",
                        "  - [ 0.4, 0.5, 0.6, 0.7 ]",
                        "resources:",
                        "  A: 6000"
                ), "/EIPSignals must have at least 2 items \\(1\\)"
        ), Arguments.of(text(
                        "# 11",
                        "location: 2",
                        "photoGenes:",
                        "  - [ 1 ]",
                        "reactionGenes:",
                        "  - [ 0.1 ]",
                        "EIPSignals:",
                        "  - [ 0.3 ]",
                        "  - [ 0.35 ]",
                        "PIPSignals: []",
                        "resources:",
                        "  A: 6000"
                ), "/PIPSignals must have at least 1 items \\(0\\)"
        ), Arguments.of(text(
                        "---",
                        "# 12",
                        "location: 2",
                        "photoGenes:",
                        "  - [ 1 ]",
                        "reactionGenes:",
                        "  - [ 0.1, 0.2 ]",
                        "EIPSignals:",
                        "  - [ 0.3 ]",
                        "  - [ 0.35 ]",
                        "PIPSignals:",
                        "  - [ 0.4, 0.5, 0.6, 0.7 ]",
                        "resources:",
                        "  A: 6000"
                ), "/reactionGenes/0 must have at most 1 items \\(2\\)"
        ), Arguments.of(text(
                        "---",
                        "# 13",
                        "photoGenes:",
                        "  - [ 1 ]",
                        "location: 2",
                        "reactionGenes:",
                        "  - [ 0.1 ]",
                        "EIPSignals:",
                        "  - [ 0.3]",
                        "  - [ 0.35, 0.4 ]",
                        "PIPSignals:",
                        "  - [ 0.5, 0.6 ]",
                        "resources:",
                        "  A: 6000"
                ), "/EIPSignals/1 must have at most 1 items \\(2\\)"
        ), Arguments.of(text(
                        "---",
                        "# 14",
                        "location: 2",
                        "photoGenes:",
                        "  - [ 1 ]",
                        "reactionGenes:",
                        "  - [ 0.1 ]",
                        "EIPSignals:",
                        "  - [ 0.3 ]",
                        "  - [ 0.35 ]",
                        "PIPSignals:",
                        "  - [ 0.5, 0.6, 0.7]",
                        "resources:",
                        "  A: 6000"
                ), "/PIPSignals/0 must have at least 4 items \\(3\\)"
        ), Arguments.of(text(
                        "---",
                        "# 15",
                        "location: 2",
                        "reactionGenes:",
                        "  - [ 0.1 ]",
                        "EIPSignals:",
                        "  - [ 0.3 ]",
                        "  - [ 0.35 ]",
                        "PIPSignals:",
                        "  - [ 0.5, 0.6, 0.7]",
                        "resources:",
                        "  A: 6000"
                ), "/photoGenes is missing"
        ), Arguments.of(text(
                        "---",
                        "# 16",
                        "location: 2",
                        "photoGenes: []",
                        "reactionGenes:",
                        "  - [ 0.1 ]",
                        "EIPSignals:",
                        "  - [ 0.3 ]",
                        "  - [ 0.35 ]",
                        "PIPSignals:",
                        "  - [ 0.5, 0.6, 0.7]",
                        "resources:",
                        "  A: 6000"
                ), "/photoGenes must have at least 1 items \\(0\\)"
        ), Arguments.of(text(
                        "---",
                        "# 16",
                        "location: 2",
                        "photoGenes:",
                        "  - [ 1 ]",
                        "  - [ 1 ]",
                        "reactionGenes:",
                        "  - [ 0.1 ]",
                        "EIPSignals:",
                        "  - [ 0.3 ]",
                        "  - [ 0.35 ]",
                        "PIPSignals:",
                        "  - [ 0.5, 0.6, 0.7]",
                        "resources:",
                        "  A: 6000"
                ), "/photoGenes must have at most 1 items \\(2\\)"
        ), Arguments.of(text(
                        "---",
                        "# 17",
                        "location: 2",
                        "photoGenes:",
                        "  - [ ]",
                        "reactionGenes:",
                        "  - [ 0.1 ]",
                        "EIPSignals:",
                        "  - [ 0.3 ]",
                        "  - [ 0.35 ]",
                        "PIPSignals:",
                        "  - [ 0.5, 0.6, 0.7]",
                        "resources:",
                        "  A: 6000"
                ), "/photoGenes/0 must have at least 1 items \\(0\\)"
        ), Arguments.of(text(
                        "---",
                        "# 18",
                        "location: 2",
                        "photoGenes:",
                        "  - [ 1, 0]",
                        "reactionGenes:",
                        "  - [ 0.1 ]",
                        "EIPSignals:",
                        "  - [ 0.3 ]",
                        "  - [ 0.35 ]",
                        "PIPSignals:",
                        "  - [ 0.5, 0.6, 0.7]",
                        "resources:",
                        "  A: 6000"
                ), "/photoGenes/0 must have at most 1 items \\(2\\)"
        ));
    }

    @Test
    void validate() throws IOException {
        JsonNode root = fromText(text(
                "---",
                "location: 2",
                "photoGenes:",
                "  - [ 0.5 ]",
                "reactionGenes:",
                "  - [ 0.5 ]",
                "EIPSignals:",
                "  - [ 0.3 ]",
                "  - [ 0.35 ]",
                "PIPSignals:",
                "  - [ 0.4, 0.5, 0.6, 0.7 ]",
                "resources:",
                "  A: 6000"
        ));
        SchemaValidators.individual()
                .apply(root())
                .andThen(CrossValidators.individual(KEYS, 4, new int[]{1}, new int[]{1}, new int[]{1, 1}, new int[]{4}).apply(root()))
                .accept(root);

        Reaction reaction = Reaction.create(Matrix.of(1, 0),
                Matrix.of(0, 1),
                Matrix.ones(2, 1),
                Matrix.ones(2, 1)
        );
        PhotoReactionProcess photoProcess = PhotoReactionProcess.create(1, 1, MIN_LEVEL, MAX_LEVEL, reaction);
        ReactionProcess reactionProcess = ReactionProcess.create(1, MIN_LEVEL, MAX_LEVEL, reaction);

        Individual result = Parsers.individual(root, KEYS, List.of(photoProcess), List.of(reactionProcess));

        assertNotNull(result);
        assertThat(result.getLocation(), equalTo(2));
        assertThat(result.getResources(), matrixCloseTo(6000, 0));
        assertThat(result.getPhotoTargetLevels(), matrixCloseTo(sqrt(MIN_LEVEL * MAX_LEVEL)));
        assertThat(result.getReactionTargetLevels(), matrixCloseTo(sqrt(MIN_LEVEL * MAX_LEVEL)));

        assertThat(Parsers.signalsList(root.path("EIPSignals")), contains(
                matrixCloseTo(0.3),
                matrixCloseTo(0.35)
        ));
        assertThat(Parsers.signalsList(root.path("PIPSignals")), contains(
                matrixCloseTo(0.4, 0.5, 0.6, 0.7)
        ));
    }


    @ParameterizedTest
    @MethodSource("argsForErrors")
    void validateErrors(String text, String expectedPattern) throws IOException {
        final IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                SchemaValidators.individual()
                        .apply(root())
                        .andThen(CrossValidators.individual(KEYS, 4, new int[]{1}, new int[]{1}, new int[]{1, 1}, new int[]{4}).apply(root()))
                        .accept(fromText(text)));
        assertThat(ex.getMessage(), matchesPattern(expectedPattern));
    }
}