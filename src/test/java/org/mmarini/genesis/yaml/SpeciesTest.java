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
import java.util.Map;
import java.util.stream.Stream;

import static java.lang.Math.log;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mmarini.genesis.yaml.TestUtils.text;
import static org.mmarini.yaml.Utils.fromText;
import static org.mmarini.yaml.schema.Locator.root;

class SpeciesTest {
    static final double BASAL_METABOLIC_RATE = 0.1;
    static final double SURVIVING_MASS = 0.2;
    static final double AREA_BY_MASS = 1.4;

    static Stream<Arguments> argsForErrors() {
        return Stream.of(Arguments.of(text(
                        "#1",
                        "0"
                ), " must be an object \\(NUMBER\\)"
        ), Arguments.of(text(
                        "#2",
                        "surviveMass: 0.2",
                        "fractalDimension: 1.4",
                        "IPGenes:",
                        "  - ipgene",
                        "EIPGenes:",
                        "  - eipgene",
                        "PIPGenes:",
                        "  - pipgene"
                ), "/basalMetabolicRate is missing"
        ), Arguments.of(text(
                        "#3",
                        "basalMetabolicRate: 0.1",
                        "fractalDimension: 1.4",
                        "IPGenes:",
                        "  - ipgene",
                        "EIPGenes:",
                        "  - eipgene",
                        "PIPGenes:",
                        "  - pipgene"
                ), "/surviveMass is missing"
        ), Arguments.of(text(
                        "#4",
                        "basalMetabolicRate: 0.1",
                        "surviveMass: 0.2",
                        "IPGenes:",
                        "  - ipgene",
                        "EIPGenes:",
                        "  - eipgene",
                        "PIPGenes:",
                        "  - pipgene"
                ), "/fractalDimension is missing"
        ), Arguments.of(text(
                        "#5",
                        "basalMetabolicRate: 0.1",
                        "surviveMass: 0.2",
                        "fractalDimension: 1.4",
                        "photoGenes:",
                        "  - photo",
                        "EIPGenes:",
                        "  - eipgene",
                        "PIPGenes:",
                        "  - pipgene"
                ), "/IPGenes is missing"
        ), Arguments.of(text(
                        "#6",
                        "basalMetabolicRate: 0.1",
                        "surviveMass: 0.2",
                        "fractalDimension: 1.4",
                        "photoGenes:",
                        "  - photo",
                        "IPGenes:",
                        "  - ipgene",
                        "PIPGenes:",
                        "  - pipgene"
                ), "/EIPGenes is missing"
        ), Arguments.of(text(
                        "#7",
                        "basalMetabolicRate: 0.1",
                        "surviveMass: 0.2",
                        "fractalDimension: 1.4",
                        "photoGenes:",
                        "  - photo",
                        "IPGenes:",
                        "  - ipgene",
                        "EIPGenes:",
                        "  - eipgene"
                ), "/PIPGenes is missing"
        ), Arguments.of(text(
                        "#8",
                        "basalMetabolicRate: 0.1",
                        "surviveMass: 0.2",
                        "fractalDimension: 1.4",
                        "photoGenes:",
                        "  - photo",
                        "IPGenes:",
                        "  - ipgene1",
                        "EIPGenes:",
                        "  - eipgene",
                        "PIPGenes:",
                        "  - pipgene"
                ), "/IPGenes/0 must match a value in \\[ipgene\\] \\(ipgene1\\)"
        ), Arguments.of(text(
                        "#9",
                        "basalMetabolicRate: 0.1",
                        "surviveMass: 0.2",
                        "fractalDimension: 1.4",
                        "photoGenes:",
                        "  - photo",
                        "IPGenes:",
                        "  - ipgene",
                        "EIPGenes:",
                        "  - eipgene1",
                        "PIPGenes:",
                        "  - pipgene"
                ), "/EIPGenes/0 must match a value in \\[eipgene\\] \\(eipgene1\\)"
        ), Arguments.of(text(
                        "#10",
                        "basalMetabolicRate: 0.1",
                        "surviveMass: 0.2",
                        "fractalDimension: 1.4",
                        "photoGenes:",
                        "  - photo",
                        "IPGenes:",
                        "  - ipgene",
                        "EIPGenes:",
                        "  - eipgene",
                        "PIPGenes:",
                        "  - pipgene1"
                ), "/PIPGenes/0 must match a value in \\[pipgene\\] \\(pipgene1\\)"
        ), Arguments.of(text(
                        "#11",
                        "basalMetabolicRate: 0.1",
                        "surviveMass: 0.2",
                        "fractalDimension: 1.4",
                        "IPGenes:",
                        "  - ipgene",
                        "EIPGenes:",
                        "  - eipgene",
                        "PIPGenes:",
                        "  - pipgene"
                ), "/photoGenes is missing"
        ), Arguments.of(text(
                        "#12",
                        "basalMetabolicRate: 0.1",
                        "surviveMass: 0.2",
                        "fractalDimension: 1.4",
                        "photoGenes:",
                        "  - photo1",
                        "IPGenes:",
                        "  - ipgene",
                        "EIPGenes:",
                        "  - eipgene",
                        "PIPGenes:",
                        "  - pipgene"
                ), "/photoGenes/0 must match a value in \\[photo\\] \\(photo1\\)"
        ));
    }

    static Map<String, ? extends EIPGene> createEIPGenes() {
        return Map.of(
                "eipgene", new ExchangeResourceGene(
                        Matrix.of(1, 2),
                        Matrix.of(log(3), log(2)),
                        Matrix.of(0.5, 1.5)
                )
        );
    }

    static Map<String, ? extends IPGene> createIPGenes() {
        return Map.of(
                "ipgene", new ResourceGene(
                        0,
                        1,
                        log(10),
                        Reaction.create(
                                Matrix.of(1, 0),
                                Matrix.of(0, 2),
                                Matrix.of(0.5, 0),
                                Matrix.of(0.1, 0)
                        )
                )
        );
    }

    static Map<String, ? extends PIPGene> createPIPGenes() {
        return Map.of(
                "pipgene", CloneGene.create(
                        0.1, 1.2,
                        3.4, 4.5,
                        5.6, 6.7,
                        7.8, 8.9,
                        0,
                        9, 0.9,
                        1, 0.4)
        );
    }

    private Map<String, ? extends PhotoProcess> createPhotoGenes() {
        return Map.of(
                "photo", PhotoProcess.create(0,
                        3,
                        1, 2,
                        Reaction.create(
                                Matrix.of(1, 0),
                                Matrix.of(0, 2),
                                Matrix.of(0.5, 0),
                                Matrix.of(0.1, 0)
                        )
                ));
    }

    @Test
    void validate() throws IOException {
        JsonNode root = fromText(text(
                "---",
                "basalMetabolicRate: 0.1",
                "surviveMass: 0.2",
                "fractalDimension: 1.4",
                "photoGenes:",
                "  - photo",
                "IPGenes:",
                "  - ipgene",
                "EIPGenes:",
                "  - eipgene",
                "PIPGenes:",
                "  - pipgene"
        ));
        Map<String, ? extends PhotoProcess> photoGenes = createPhotoGenes();
        Map<String, ? extends IPGene> ipGenes = createIPGenes();
        Map<String, ? extends EIPGene> eipGenes = createEIPGenes();
        Map<String, ? extends PIPGene> pipGenes = createPIPGenes();
        SchemaValidators.species()
                .apply(root())
                .andThen(CrossValidators.species(photoGenes.keySet(), ipGenes.keySet(), eipGenes.keySet(), pipGenes.keySet())
                        .apply(root()))
                .accept(root);

        Species species = Parsers.species(root, photoGenes, ipGenes, eipGenes, pipGenes);
        assertNotNull(species);
        assertThat(species.getBasalMetabolicRate(), equalTo(BASAL_METABOLIC_RATE));

        assertThat(species.getSurvivingMass(), equalTo(SURVIVING_MASS));
        assertThat(species.getFractalDimension(), equalTo(AREA_BY_MASS));

        assertThat(species.getIpGenes(), contains(ipGenes.get("ipgene")));
        assertThat(species.getEipGenes(), contains(eipGenes.get("eipgene")));
        assertThat(species.getPipGenes(), contains(pipGenes.get("pipgene")));
        assertThat(species.getPhotoProcess(), contains(photoGenes.get("photo")));
    }

    @ParameterizedTest
    @MethodSource("argsForErrors")
    void validateErrors(String text, String expectedPattern) throws IOException {
        Map<String, ? extends PhotoProcess> photoGenes = createPhotoGenes();
        Map<String, ? extends IPGene> ipGenes = createIPGenes();
        Map<String, ? extends EIPGene> eipGenes = createEIPGenes();
        Map<String, ? extends PIPGene> pipGenes = createPIPGenes();
        final IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                SchemaValidators.species()
                        .apply(root())
                        .andThen(CrossValidators.species(photoGenes.keySet(), ipGenes.keySet(), eipGenes.keySet(), pipGenes.keySet())
                                .apply(root()))
                        .accept(fromText(text)));
        assertThat(ex.getMessage(), matchesPattern(expectedPattern));
    }
}