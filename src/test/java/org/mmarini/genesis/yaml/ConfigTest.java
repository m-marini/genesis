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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mmarini.genesis.model3.Matrix.of;
import static org.mmarini.genesis.model3.Matrix.ones;
import static org.mmarini.genesis.model3.MatrixMatchers.matrixCloseTo;
import static org.mmarini.genesis.yaml.TestUtils.text;
import static org.mmarini.yaml.Utils.fromText;
import static org.mmarini.yaml.schema.Locator.root;

class ConfigTest {
    static final double MASS_A = 1;
    static final double MASS_B = 0;
    static final double DIFFUSION_A = 2;
    static final double DIFFUSION_B = 0;
    static final double ENV_A = 1;
    static final double ENV_B = 0;
    static final double FRACTAL_DIMENSION = 2;
    static final double IND_A = 6000;
    static final double IND_B = 0;
    static final int ENERGY_REF = 1;

    static final String YAML = text(
            "---",
            "mass:",
            "  A: 1",
            "  B: 0",
            "photoGenes:",
            "  photo:",
            "    ref: B",
            "    speed: 3",
            "    minLevel: 1",
            "    maxLevel: 10",
            "    reaction:",
            "      reagents:",
            "        A: 1",
            "      products:",
            "        B: 1",
            "      thresholds:",
            "        A: 0.1",
            "      speeds:",
            "        A: 0.1",
            "ipgenes:",
            "  ipgene:",
            "    ref: B",
            "    minLevel: 1",
            "    maxLevel: 10",
            "    reaction:",
            "      reagents:",
            "        A: 1",
            "      products:",
            "        B: 1",
            "      thresholds:",
            "        A: 0.1",
            "      speeds:",
            "        A: 0.1",
            "eipgenes:",
            "  eipgene:",
            "    minLevels:",
            "      A: 1",
            "      B: 2",
            "    maxLevels:",
            "      A: 3",
            "      B: 4",
            "    rates:",
            "      A: 0.5",
            "      B: 1.5",
            "pipgenes:",
            "  pipgene:",
            "    energyRef: B",
            "    minMassThreshold: 0.1",
            "    maxMassThreshold: 1.2",
            "    minEnergyThreshold: 3.4",
            "    maxEnergyThreshold: 4.5",
            "    minMassProbability: 5.6",
            "    maxMassProbability: 6.7",
            "    minEnergyProbability: 7.8",
            "    maxEnergyProbability: 8.9",
            "    inPlacePreference: 9.0",
            "    adjacentPreference: 0.9",
            "    mutationProb: 1.0",
            "    mutationSigma: 0.3",
            "populations:",
            "  - species:",
            "      basalMetabolicRate: 0.1",
            "      surviveMass: 0.2",
            "      fractalDimension: 2",
            "      photoGenes:",
            "        - photo",
            "      IPGenes:",
            "        - ipgene",
            "      EIPGenes:",
            "        - eipgene",
            "      PIPGenes:",
            "        - pipgene",
            "    individuals:",
            "      - location: 2",
            "        resources:",
            "          A: 6000",
            "        photoSignals:",
            "          - [0.5]",
            "        IPSignals:",
            "          - [ 0.1 ]",
            "        EIPSignals:",
            "          - [ 0.2, 0.4 ]",
            "        PIPSignals:",
            "          - [ 0.3, 0.4, 0.5, 0.6 ]",
            "environ:",
            "  width: 4",
            "  height: 4",
            "  length: 2",
            "  resources:",
            "    A: 1",
            "  diffusion:",
            "    A: 2",
            "energyRef: B"
    );

    static Stream<Arguments> argsForErrors() {
        return Stream.of(Arguments.of(text(
                        "#1",
                        "{}"
                ), "/mass is missing"
        ), Arguments.of(text(
                        "#2",
                        "mass:",
                        "  A: 0"
                ), "/environ is missing"
        ), Arguments.of(text(
                        "#3",
                        "mass:",
                        "  A: 0",
                        "environ:",
                        "  width: 4",
                        "  height: 4",
                        "  length: 2",
                        "  resources:",
                        "    A: 1",
                        "  resourceFlows:",
                        "    B: 1",
                        "  diffusion:",
                        "    A: 2",
                        "photoGenes: {}"
                ), "/ipgenes is missing"
        ), Arguments.of(text(
                        "#4",
                        "mass:",
                        "  A: 0",
                        "environ:",
                        "  width: 4",
                        "  height: 4",
                        "  length: 2",
                        "  resources:",
                        "    A: 1",
                        "  resourceFlows:",
                        "    B: 1",
                        "  diffusion:",
                        "    A: 2",
                        "photoGenes: {}",
                        "ipgenes: {}"
                ), "/eipgenes is missing"
        ), Arguments.of(text(
                        "#5",
                        "mass:",
                        "  A: 0",
                        "environ:",
                        "  width: 4",
                        "  height: 4",
                        "  length: 2",
                        "  resources:",
                        "    A: 1",
                        "  resourceFlows:",
                        "    B: 1",
                        "  diffusion:",
                        "    A: 2",
                        "photoGenes: {}",
                        "ipgenes: {}",
                        "eipgenes: {}"
                ), "/pipgenes is missing"
        ), Arguments.of(text(
                        "#6",
                        "mass:",
                        "  A: 0",
                        "environ:",
                        "  width: 4",
                        "  height: 4",
                        "  length: 2",
                        "  resources:",
                        "    A: 1",
                        "  resourceFlows:",
                        "    B: 1",
                        "  diffusion:",
                        "    A: 2",
                        "photoGenes: {}",
                        "ipgenes: {}",
                        "eipgenes: {}",
                        "pipgenes: {}"
                ), "/populations is missing"
        ), Arguments.of(text(
                        "#7",
                        "mass:",
                        "  A: 0",
                        "environ:",
                        "  width: 4",
                        "  height: 4",
                        "  length: 2",
                        "  resources:",
                        "    A: 1",
                        "  resourceFlows:",
                        "    B: 1",
                        "  diffusion:",
                        "    A: 2",
                        "photoGenes: {}",
                        "ipgenes: {}",
                        "eipgenes: {}",
                        "pipgenes: {}",
                        "populations: []"
                ), "/energyRef is missing"
        ), Arguments.of(text(
                        "#8",
                        "mass:",
                        "  A: 0",
                        "environ:",
                        "  width: 4",
                        "  height: 4",
                        "  length: 2",
                        "  resources:",
                        "    A: 1",
                        "  resourceFlows:",
                        "    B: 1",
                        "  diffusion:",
                        "    A: 2",
                        "photoGenes: {}",
                        "ipgenes: {}",
                        "eipgenes: {}",
                        "pipgenes: {}",
                        "populations: []",
                        "energyRef: B"
                ), "/energyRef must match a value in \\[A\\] \\(B\\)"
        ), Arguments.of(text(
                        "#9",
                        "mass:",
                        "  A: 0",
                        "environ:",
                        "  width: 4",
                        "  height: 4",
                        "  length: 2",
                        "  resources:",
                        "    A: 1",
                        "    B: 1",
                        "  resourceFlows:",
                        "    B: 1",
                        "  diffusion:",
                        "    A: 2",
                        "photoGenes: {}",
                        "ipgenes: {}",
                        "eipgenes: {}",
                        "pipgenes: {}",
                        "populations: []",
                        "energyRef: A"
                ), "/environ/resources/B resource undefined"
        ), Arguments.of(text(
                        "#10",
                        "mass:",
                        "  A: 0",
                        "environ:",
                        "  width: 4",
                        "  height: 4",
                        "  length: 2",
                        "  resources:",
                        "    A: 1",
                        "  resourceFlows:",
                        "    B: 1",
                        "  diffusion:",
                        "    A: 2",
                        "photoGenes: {}",
                        "ipgenes:",
                        "  ipgene:",
                        "    ref: A",
                        "    minLevel: 1",
                        "    maxLevel: 10",
                        "    reaction:",
                        "      reagents:",
                        "        A: 1",
                        "      products:",
                        "        B: 1",
                        "      thresholds:",
                        "        A: 0.1",
                        "      speeds:",
                        "        A: 0.1",
                        "eipgenes: {}",
                        "pipgenes: {}",
                        "populations: []",
                        "energyRef: A"
                ), "/ipgenes/ipgene/reaction/products/B resource undefined"
        ), Arguments.of(text(
                        "#11",
                        "mass:",
                        "  A: 0",
                        "environ:",
                        "  width: 4",
                        "  height: 4",
                        "  length: 2",
                        "  resources:",
                        "    A: 1",
                        "  resourceFlows:",
                        "    B: 1",
                        "  diffusion:",
                        "    A: 2",
                        "photoGenes: {}",
                        "ipgenes: {}",
                        "eipgenes:",
                        "  eipgene:",
                        "    minLevels:",
                        "      A: 1",
                        "      B: 2",
                        "    maxLevels:",
                        "      A: 3",
                        "    rates:",
                        "      A: 0.5",
                        "pipgenes: {}",
                        "populations: []",
                        "energyRef: A"
                ), "/eipgenes/eipgene/minLevels/B resource undefined"
        ), Arguments.of(text(
                        "#12",
                        "mass:",
                        "  A: 0",
                        "environ:",
                        "  width: 4",
                        "  height: 4",
                        "  length: 2",
                        "  resources:",
                        "    A: 1",
                        "  resourceFlows:",
                        "    B: 1",
                        "  diffusion:",
                        "    A: 2",
                        "photoGenes: {}",
                        "photoGenes: {}",
                        "ipgenes: {}",
                        "eipgenes: {}",
                        "pipgenes:",
                        "  pipgene:",
                        "    energyRef: B",
                        "    minMassThreshold: 0.1",
                        "    maxMassThreshold: 1.2",
                        "    minEnergyThreshold: 3.4",
                        "    maxEnergyThreshold: 4.5",
                        "    minMassProbability: 5.6",
                        "    maxMassProbability: 6.7",
                        "    minEnergyProbability: 7.8",
                        "    maxEnergyProbability: 8.9",
                        "    inPlacePreference: 9.0",
                        "    adjacentPreference: 0.9",
                        "    mutationProb: 1.0",
                        "    mutationSigma: 0.3",
                        "populations: []",
                        "energyRef: A"
                ), "/pipgenes/pipgene/energyRef must match a value in \\[A\\] \\(B\\)"
        ), Arguments.of(text(
                        "#13",
                        "mass:",
                        "  A: 0",
                        "environ:",
                        "  width: 4",
                        "  height: 4",
                        "  length: 2",
                        "  resources:",
                        "    A: 1",
                        "  resourceFlows:",
                        "    B: 1",
                        "  diffusion:",
                        "    A: 2",
                        "photoGenes: {}",
                        "ipgenes:",
                        "  ipgene:",
                        "    ref: A",
                        "    minLevel: 1",
                        "    maxLevel: 10",
                        "    reaction:",
                        "      reagents:",
                        "        A: 1",
                        "      products:",
                        "        A: 1",
                        "      thresholds:",
                        "        A: 0.1",
                        "      speeds:",
                        "        A: 0.1",
                        "eipgenes: {}",
                        "pipgenes: {}",
                        "energyRef: A",
                        "populations:",
                        "  - species:",
                        "      basalMetabolicRate: 0.1",
                        "      surviveMass: 0.2",
                        "      fractalDimension: 1.4",
                        "      photoGenes: []",
                        "      IPGenes:",
                        "        - ipgene1",
                        "      EIPGenes: []",
                        "      PIPGenes: []",
                        "    individuals: []"
                ), "/populations/0/species/IPGenes/0 must match a value in \\[ipgene\\] \\(ipgene1\\)"
        ), Arguments.of(text(
                        "#14",
                        "mass:",
                        "  A: 0",
                        "environ:",
                        "  width: 4",
                        "  height: 4",
                        "  length: 2",
                        "  resources:",
                        "    A: 1",
                        "  resourceFlows:",
                        "    B: 1",
                        "  diffusion:",
                        "    A: 2",
                        "photoGenes: {}",
                        "ipgenes: {}",
                        "eipgenes:",
                        "  eipgene:",
                        "    minLevels:",
                        "      A: 1",
                        "    maxLevels:",
                        "      A: 3",
                        "    rates:",
                        "      A: 0.5",
                        "pipgenes: {}",
                        "energyRef: A",
                        "populations:",
                        "  - species:",
                        "      basalMetabolicRate: 0.1",
                        "      surviveMass: 0.2",
                        "      fractalDimension: 1.4",
                        "      photoGenes: []",
                        "      IPGenes: []",
                        "      EIPGenes:",
                        "        - a",
                        "      PIPGenes: []",
                        "    individuals: []"
                ), "/populations/0/species/EIPGenes/0 must match a value in \\[eipgene\\] \\(a\\)"
        ), Arguments.of(text(
                        "#15",
                        "mass:",
                        "  A: 0",
                        "environ:",
                        "  width: 4",
                        "  height: 4",
                        "  length: 2",
                        "  resources:",
                        "    A: 1",
                        "  resourceFlows:",
                        "    B: 1",
                        "  diffusion:",
                        "    A: 2",
                        "photoGenes: {}",
                        "ipgenes: {}",
                        "eipgenes: {}",
                        "pipgenes:",
                        "  pipgene:",
                        "    energyRef: A",
                        "    minMassThreshold: 0.1",
                        "    maxMassThreshold: 1.2",
                        "    minEnergyThreshold: 3.4",
                        "    maxEnergyThreshold: 4.5",
                        "    minMassProbability: 5.6",
                        "    maxMassProbability: 6.7",
                        "    minEnergyProbability: 7.8",
                        "    maxEnergyProbability: 8.9",
                        "    inPlacePreference: 9.0",
                        "    adjacentPreference: 0.9",
                        "    mutationProb: 1.0",
                        "    mutationSigma: 0.3",
                        "energyRef: A",
                        "populations:",
                        "  - species:",
                        "      basalMetabolicRate: 0.1",
                        "      surviveMass: 0.2",
                        "      fractalDimension: 1.4",
                        "      photoGenes: []",
                        "      IPGenes: []",
                        "      EIPGenes: []",
                        "      PIPGenes:",
                        "        - a",
                        "    individuals: []"
                ), "/populations/0/species/PIPGenes/0 must match a value in \\[pipgene\\] \\(a\\)"
        ), Arguments.of(text(
                        "#16",
                        "mass:",
                        "  A: 1",
                        "  B: 0",
                        "photoGenes: {}",
                        "ipgenes:",
                        "  ipgene:",
                        "    ref: B",
                        "    minLevel: 1",
                        "    maxLevel: 10",
                        "    reaction:",
                        "      reagents:",
                        "        A: 1",
                        "      products:",
                        "        B: 1",
                        "      thresholds:",
                        "        A: 0.1",
                        "      speeds:",
                        "        A: 0.1",
                        "eipgenes:",
                        "  eipgene:",
                        "    minLevels:",
                        "      A: 1",
                        "      B: 2",
                        "    maxLevels:",
                        "      A: 3",
                        "      B: 4",
                        "    rates:",
                        "      A: 0.5",
                        "      B: 1.5",
                        "pipgenes:",
                        "  pipgene:",
                        "    energyRef: B",
                        "    minMassThreshold: 0.1",
                        "    maxMassThreshold: 1.2",
                        "    minEnergyThreshold: 3.4",
                        "    maxEnergyThreshold: 4.5",
                        "    minMassProbability: 5.6",
                        "    maxMassProbability: 6.7",
                        "    minEnergyProbability: 7.8",
                        "    maxEnergyProbability: 8.9",
                        "    inPlacePreference: 9.0",
                        "    adjacentPreference: 0.9",
                        "    mutationProb: 1.0",
                        "    mutationSigma: 0.3",
                        "populations:",
                        "  - species:",
                        "      basalMetabolicRate: 0.1",
                        "      surviveMass: 0.2",
                        "      fractalDimension: 1.4",
                        "      photoGenes: []",
                        "      IPGenes:",
                        "        - ipgene",
                        "      EIPGenes:",
                        "        - eipgene",
                        "      PIPGenes:",
                        "        - pipgene",
                        "    individuals:",
                        "      - location: 16",
                        "        resources:",
                        "          A: 6000",
                        "        photoSignals: []",
                        "        IPSignals:",
                        "          - [ 0.1 ]",
                        "        EIPSignals:",
                        "          - [ 0.2 ]",
                        "        PIPSignals:",
                        "          - [ 0.3, 0.4, 0.5, 0.6 ]",
                        "environ:",
                        "  width: 4",
                        "  height: 4",
                        "  length: 2",
                        "  resources:",
                        "    A: 1",
                        "  resourceFlows:",
                        "    B: 1",
                        "  diffusion:",
                        "    A: 2",
                        "energyRef: B"
                ), "/populations/0/individuals/0/location must be < 16 \\(16\\)"
        ), Arguments.of(text(
                        "#17",
                        "mass:",
                        "  A: 1",
                        "  B: 0",
                        "photoGenes: {}",
                        "ipgenes:",
                        "  ipgene:",
                        "    ref: B",
                        "    minLevel: 1",
                        "    maxLevel: 10",
                        "    reaction:",
                        "      reagents:",
                        "        A: 1",
                        "      products:",
                        "        B: 1",
                        "      thresholds:",
                        "        A: 0.1",
                        "      speeds:",
                        "        A: 0.1",
                        "eipgenes:",
                        "  eipgene:",
                        "    minLevels:",
                        "      A: 1",
                        "      B: 2",
                        "    maxLevels:",
                        "      A: 3",
                        "      B: 4",
                        "    rates:",
                        "      A: 0.5",
                        "      B: 1.5",
                        "pipgenes:",
                        "  pipgene:",
                        "    energyRef: B",
                        "    minMassThreshold: 0.1",
                        "    maxMassThreshold: 1.2",
                        "    minEnergyThreshold: 3.4",
                        "    maxEnergyThreshold: 4.5",
                        "    minMassProbability: 5.6",
                        "    maxMassProbability: 6.7",
                        "    minEnergyProbability: 7.8",
                        "    maxEnergyProbability: 8.9",
                        "    inPlacePreference: 9.0",
                        "    adjacentPreference: 0.9",
                        "    mutationProb: 1.0",
                        "    mutationSigma: 0.3",
                        "populations:",
                        "  - species:",
                        "      basalMetabolicRate: 0.1",
                        "      surviveMass: 0.2",
                        "      fractalDimension: 1.4",
                        "      photoGenes: []",
                        "      IPGenes:",
                        "        - ipgene",
                        "      EIPGenes:",
                        "        - eipgene",
                        "      PIPGenes:",
                        "        - pipgene",
                        "    individuals:",
                        "      - location: 15",
                        "        resources:",
                        "          A: 6000",
                        "        photoSignals: []",
                        "        IPSignals:",
                        "          - [ 0.1, 0.2 ]",
                        "        EIPSignals:",
                        "          - [ 0.2 ]",
                        "        PIPSignals:",
                        "          - [ 0.3, 0.4, 0.5, 0.6 ]",
                        "environ:",
                        "  width: 4",
                        "  height: 4",
                        "  length: 2",
                        "  resources:",
                        "    A: 1",
                        "  resourceFlows:",
                        "    B: 1",
                        "  diffusion:",
                        "    A: 2",
                        "energyRef: B"
                ), "/populations/0/individuals/0/IPSignals/0 must have at most 1 items \\(2\\)"
        ), Arguments.of(text(
                        "#18",
                        "mass:",
                        "  A: 1",
                        "  B: 0",
                        "photoGenes: {}",
                        "ipgenes:",
                        "  ipgene:",
                        "    ref: B",
                        "    minLevel: 1",
                        "    maxLevel: 10",
                        "    reaction:",
                        "      reagents:",
                        "        A: 1",
                        "      products:",
                        "        B: 1",
                        "      thresholds:",
                        "        A: 0.1",
                        "      speeds:",
                        "        A: 0.1",
                        "eipgenes:",
                        "  eipgene:",
                        "    minLevels:",
                        "      A: 1",
                        "      B: 2",
                        "    maxLevels:",
                        "      A: 3",
                        "      B: 4",
                        "    rates:",
                        "      A: 0.5",
                        "      B: 1.5",
                        "pipgenes:",
                        "  pipgene:",
                        "    energyRef: B",
                        "    minMassThreshold: 0.1",
                        "    maxMassThreshold: 1.2",
                        "    minEnergyThreshold: 3.4",
                        "    maxEnergyThreshold: 4.5",
                        "    minMassProbability: 5.6",
                        "    maxMassProbability: 6.7",
                        "    minEnergyProbability: 7.8",
                        "    maxEnergyProbability: 8.9",
                        "    inPlacePreference: 9.0",
                        "    adjacentPreference: 0.9",
                        "    mutationProb: 1.0",
                        "    mutationSigma: 0.3",
                        "populations:",
                        "  - species:",
                        "      basalMetabolicRate: 0.1",
                        "      surviveMass: 0.2",
                        "      fractalDimension: 1.4",
                        "      photoGenes: []",
                        "      IPGenes:",
                        "        - ipgene",
                        "      EIPGenes:",
                        "        - eipgene",
                        "      PIPGenes:",
                        "        - pipgene",
                        "    individuals:",
                        "      - location: 2",
                        "        resources:",
                        "          A: 6000",
                        "        photoSignals: []",
                        "        IPSignals:",
                        "          - [ 0.1 ]",
                        "        EIPSignals:",
                        "          - [ 0.2 ]",
                        "        PIPSignals:",
                        "          - [ 0.3, 0.4, 0.5, 0.6 ]",
                        "environ:",
                        "  width: 4",
                        "  height: 4",
                        "  length: 2",
                        "  resources:",
                        "    A: 1",
                        "  resourceFlows:",
                        "    B: 1",
                        "  diffusion:",
                        "    A: 2",
                        "energyRef: B"
                ), "/populations/0/individuals/0/EIPSignals/0 must have at least 2 items \\(1\\)"
        ));
    }

    @Test
    void validate() throws IOException {
        JsonNode root = fromText(YAML);
        SchemaValidators.config().apply(root())
                .andThen(CrossValidators.config().apply(root()))
                .accept(root);
        SimEngine engine = Parsers.engine(root);

        assertNotNull(engine);
        assertThat(engine.getEnergyRef(), equalTo(ENERGY_REF));

        final Topology t = engine.getTopology();
        assertThat(t.getNoCells(), equalTo(16));

        final Matrix c = engine.getDiffusion();
        assertThat(c, matrixCloseTo(
                DIFFUSION_A, DIFFUSION_B
        ));

        final Matrix m = engine.getMasses();
        assertThat(m, matrixCloseTo(MASS_A, MASS_B));


        final SimStatus status = Parsers.status(root);
        assertNotNull(status);

        final Matrix expected = ones(1, 16).prod(of(
                ENV_A,
                ENV_B
        ));
        assertThat(status.getResources(), matrixCloseTo(expected));

        assertThat(status.getT(), equalTo(0.0));

        final List<Population> pops = status.getPopulations();
        assertThat(pops, hasSize(1));

        final Population pop = pops.get(0);
        assertThat(pop.getResources(), matrixCloseTo(
                IND_A, IND_B
        ));

        final Species species = pop.getSpecies();
        assertThat(species.getFractalDimension(), equalTo(FRACTAL_DIMENSION));
        assertThat(species.getIpGenes(), hasSize(1));


    }

    @ParameterizedTest
    @MethodSource("argsForErrors")
    void validateErrors(String text, String expectedPattern) {
        final IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                SchemaValidators.config().apply(root())
                        .andThen(CrossValidators.config().apply(root()))
                        .accept(fromText(text)));
        assertThat(ex.getMessage(), matchesPattern(expectedPattern));
    }
}