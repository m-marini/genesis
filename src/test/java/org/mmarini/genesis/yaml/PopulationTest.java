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
import java.util.Map;
import java.util.Random;
import java.util.stream.Stream;

import static java.lang.Math.exp;
import static java.lang.Math.log;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mmarini.genesis.model3.MatrixMatchers.matrixCloseTo;
import static org.mmarini.genesis.yaml.TestUtils.text;
import static org.mmarini.yaml.Utils.fromText;
import static org.mmarini.yaml.schema.Locator.root;

class PopulationTest {
    public static final double MIN_LEVEL = 1d;
    public static final int MAX_LEVEL = 2;
    static final double SIGNAL1 = 0.1;
    public static final double PHOTO_TARGET_LEVEL1 = exp(SIGNAL1 * log(MAX_LEVEL / MIN_LEVEL)) * MIN_LEVEL;
    static final double SIGNAL2 = 0.2;
    private static final double PHOTO_TARGET_LEVEL2 =  exp(SIGNAL2 * log(MAX_LEVEL / MIN_LEVEL)) * MIN_LEVEL;;
    static final double SIGNAL8 = 0.8;
    static final double SIGNAL9 = 0.9;
    static final double BASAL_METABOLIC_RATE = 0.1;
    static final double SURVIVING_MASS = 0.2;
    static final int LOCATION0 = 2;
    static final int LOCATION1 = 0;
    static final double SUBSTANCE_A_0 = 6000;
    static final double SUBSTANCE_A_1 = 3000;
    static final double AREA_BY_MASS = 1.4;
    static final IPGene IP_GENE = new IPGene() {

        @Override
        public Population execute(Population population, Matrix signals, double dt, Matrix resources, Matrix areas, Matrix masses) {
            return null;
        }

        @Override
        public int getNumSignals() {
            return 1;
        }
    };
    static final EIPGene EIP_GENE = new EIPGene() {
        @Override
        public Population execute(Population population, Matrix signals, double dt, Matrix envResources, Matrix areas, Matrix masses) {
            return null;
        }

        @Override
        public int getNumSignals() {
            return 2;
        }
    };
    static final PIPGene PIP_GENE = new PIPGene() {
        @Override
        public Population execute(Population population, Matrix signals, double dt, Matrix masses, Topology topology, Random random) {
            return null;
        }

        @Override
        public int getNumSignals() {
            return 1;
        }
    };
    static final List<String> SUBSTANCES = List.of("A", "B");
    static final int NO_CELLS = 4;
    static final Map<String, IPGene> IP_GENES = Map.of("ipgene", IP_GENE);
    static final Map<String, EIPGene> EIP_GENES = Map.of("eipgene", EIP_GENE);
    static final Map<String, PIPGene> PIP_GENES = Map.of("pipgene", PIP_GENE);
    static final List<String> KEYS = List.of("A", "B");
    private static final PhotoProcess PHOTO_GENE = PhotoProcess.create(0, 1, MIN_LEVEL, MAX_LEVEL,
            Reaction.create(
                    Matrix.of(0, 0),
                    Matrix.of(0, 0),
                    Matrix.of(0, 0),
                    Matrix.of(0, 0)
            ));
    static final Map<String, ? extends PhotoProcess> PHOTO_GENES = Map.of("photo", PHOTO_GENE);

    static Stream<Arguments> argsForErrors() {
        return Stream.of(Arguments.of(text(
                        "#1",
                        "0"
                ), " must be an object \\(NUMBER\\)"
        ), Arguments.of(text(
                        "#2",
                        "{}"
                ), "/species is missing"
        ), Arguments.of(text(
                        "#2",
                        "species:",
                        "  basalMetabolicRate: 0.1",
                        "  surviveMass: 0.2",
                        "  fractalDimension: 1.4",
                        "  photoGenes:",
                        "    - photo",
                        "  IPGenes:",
                        "    - ipgene",
                        "  EIPGenes:",
                        "    - eipgene",
                        "  PIPGenes:",
                        "    - pipgene"
                ), "/individuals is missing"
        ), Arguments.of(text(
                        "#3",
                        "species:",
                        "  basalMetabolicRate: 0.1",
                        "  surviveMass: 0.2",
                        "  fractalDimension: 1.4",
                        "  photoGenes:",
                        "    - photo",
                        "  IPGenes:",
                        "    - ipgene",
                        "  EIPGenes:",
                        "    - eipgene",
                        "  PIPGenes:",
                        "    - pipgene",
                        "individuals:",
                        "  - location: 2",
                        "    resources:",
                        "      A: 6000",
                        "    photoSignals:",
                        "      - [ ]",
                        "    IPSignals:",
                        "      - [ 0.1 ]",
                        "    EIPSignals:",
                        "      - [ 0.1, 0.2 ]",
                        "    PIPSignals:",
                        "      - [ 0.1 ]",
                        "  - location: 0",
                        "    resources:",
                        "      A: 3000",
                        "    photoSignals:",
                        "      - [ 0.2 ]",
                        "    IPSignals:",
                        "        - [ 0.9 ]",
                        "    EIPSignals:",
                        "      - [ 0.9, 0.8 ]",
                        "    PIPSignals:",
                        "      - [ 0.9 ]"
                ), "/individuals/0/photoSignals/0 must have at least 1 items \\(0\\)"
        ));
    }

    @Test
    void validate() throws IOException {
        JsonNode root = fromText(text(
                "---",
                "species:",
                "  basalMetabolicRate: 0.1",
                "  surviveMass: 0.2",
                "  fractalDimension: 1.4",
                "  photoGenes:",
                "    - photo",
                "  IPGenes:",
                "    - ipgene",
                "  EIPGenes:",
                "    - eipgene",
                "  PIPGenes:",
                "    - pipgene",
                "individuals:",
                "  - location: 2",
                "    resources:",
                "      A: 6000",
                "    photoSignals:",
                "      - [ 0.1 ]",
                "    IPSignals:",
                "      - [ 0.1 ]",
                "    EIPSignals:",
                "      - [ 0.1, 0.2 ]",
                "    PIPSignals:",
                "      - [ 0.1 ]",
                "  - location: 0",
                "    resources:",
                "      A: 3000",
                "    photoSignals:",
                "      - [ 0.2 ]",
                "    IPSignals:",
                "        - [ 0.9 ]",
                "    EIPSignals:",
                "      - [ 0.9, 0.8 ]",
                "    PIPSignals:",
                "      - [ 0.9 ]"
        ));

        SchemaValidators.population()
                .apply(root())
                .andThen(CrossValidators.population(KEYS, NO_CELLS, PHOTO_GENES, IP_GENES, EIP_GENES, PIP_GENES).apply(root()))
                .accept(root);

        Population pop = Parsers.population(root, KEYS, PHOTO_GENES, IP_GENES, EIP_GENES, PIP_GENES);
        assertNotNull(pop);
        Species species = pop.getSpecies();
        assertNotNull(species);
        assertThat(species.getBasalMetabolicRate(), equalTo(BASAL_METABOLIC_RATE));
        assertThat(species.getSurvivingMass(), equalTo(SURVIVING_MASS));
        assertThat(species.getFractalDimension(), equalTo(AREA_BY_MASS));
        assertThat(species.getPhotoProcess(), hasSize(1));
        assertThat(species.getPhotoProcess(), contains(PHOTO_GENE));
        assertThat(species.getIpGenes(), hasSize(1));
        assertThat(species.getIpGenes(), contains(IP_GENE));
        assertThat(species.getEipGenes(), hasSize(1));
        assertThat(species.getEipGenes(), contains(EIP_GENE));
        assertThat(species.getPipGenes(), hasSize(1));
        assertThat(species.getPipGenes(), contains(PIP_GENE));

        assertThat(pop.getResources(), matrixCloseTo(new double[][]{
                {SUBSTANCE_A_0, SUBSTANCE_A_1},
                {0, 0}
        }));
        assertThat(pop.getLocations(), equalTo(new int[]{LOCATION0, LOCATION1}));

        assertThat(pop.getPhotoTargetLevels(), hasSize(1));
        assertThat(pop.getPhotoTargetLevels(), contains(matrixCloseTo(new double[][]{
                {PHOTO_TARGET_LEVEL1, PHOTO_TARGET_LEVEL2}
        })));

        assertThat(pop.getIpSignals(), hasSize(1));
        assertThat(pop.getIpSignals(), contains(matrixCloseTo(new double[][]{
                {SIGNAL1, SIGNAL9}
        })));

        assertThat(pop.getEipSignals(), hasSize(1));
        assertThat(pop.getEipSignals(), contains(matrixCloseTo(new double[][]{
                {SIGNAL1, SIGNAL9},
                {SIGNAL2, SIGNAL8}
        })));

        assertThat(pop.getPipSignals(), hasSize(1));
        assertThat(pop.getPipSignals(), contains(matrixCloseTo(new double[][]{
                {SIGNAL1, SIGNAL9}
        })));
    }

    @ParameterizedTest
    @MethodSource("argsForErrors")
    void validateOnErrors(final String text, final String regEx) {
        final IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            SchemaValidators.population()
                    .apply(root())
                    .andThen(CrossValidators.population(KEYS, NO_CELLS, PHOTO_GENES, IP_GENES, EIP_GENES, PIP_GENES).apply(root()))
                    .accept(fromText(text));
        });

        assertThat(ex.getMessage(), matchesPattern(regEx));
    }
}