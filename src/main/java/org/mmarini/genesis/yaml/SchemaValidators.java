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


import org.mmarini.yaml.schema.Validator;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mmarini.yaml.schema.Validator.*;


/**
 *
 */
public class SchemaValidators {

    public static final Validator EVEN_INT = locator -> root -> {
        int value = locator.getNode(root).asInt(0);
        assertFor((value % 2) == 0, locator, "must be an even integer (%s)", value);
    };

    /**
     *
     */
    static Validator cloneGene() {
        Map<String, Validator> props = new HashMap<>(Map.of(
                "energyRef", string(),
                "inPlacePreference", positiveNumber(),
                "adjacentPreference", positiveNumber(),
                "mutationProb", nonNegativeNumber(),
                "mutationSigma", nonNegativeNumber()));
        props.putAll(Map.of(
                "minMassThreshold", positiveNumber(),
                "minEnergyThreshold", positiveNumber(),
                "minMassProbability", positiveNumber(),
                "minEnergyProbability", positiveNumber(),
                "maxMassThreshold", positiveNumber(),
                "maxEnergyThreshold", positiveNumber(),
                "maxMassProbability", positiveNumber(),
                "maxEnergyProbability", positiveNumber()));
        return objectPropertiesRequired(props,
                List.of(
                        "energyRef",
                        "minMassThreshold",
                        "minEnergyThreshold",
                        "minMassProbability",
                        "minEnergyProbability",
                        "maxEnergyThreshold",
                        "maxMassProbability",
                        "maxEnergyProbability",
                        "maxMassThreshold",
                        "adjacentPreference",
                        "mutationProb",
                        "mutationSigma",
                        "inPlacePreference",
                        "adjacentPreference"
                ));
    }

    /**
     *
     */
    public static Validator config() {
        return objectPropertiesRequired(Map.of(
                "mass", resources(),
                "environ", environ(),
                "photoGenes", photoGenes(),
                "ipgenes", ipGenes(),
                "eipgenes", eipGenes(),
                "pipgenes", pipGenes(),
                "populations", arrayItems(population()),
                "energyRef", string()
        ), List.of(
                "mass",
                "environ",
                "photoGenes",
                "ipgenes",
                "eipgenes",
                "pipgenes",
                "populations",
                "energyRef"
        ));
    }

    /**
     *
     */
    static Validator eipGenes() {
        return objectAdditionalProperties(exchangeResourceGene());
    }

    /**
     *
     */
    static Validator environ() {
        return objectPropertiesRequired(Map.of(
                "width", allOf(positiveInteger(), EVEN_INT),
                "height", allOf(positiveInteger(), EVEN_INT),
                "length", positiveNumber(),
                "resources", nonNegativeResources(),
                "diffusion", positiveResources()
        ), List.of(
                "width",
                "height",
                "length",
                "resources",
                "diffusion"));
    }

    /**
     *
     */
    static Validator exchangeResourceGene() {
        return objectPropertiesRequired(Map.of(
                        "minLevels", positiveResources(),
                        "maxLevels", positiveResources(),
                        "rates", positiveResources()
                ), List.of(
                        "minLevels",
                        "maxLevels",
                        "rates"
                )
        );
    }

    /**
     *
     */
    public static Validator individual() {
        return objectPropertiesRequired(Map.of(
                "location", nonNegativeInteger(),
                "resources", nonNegativeResources(),
                "photoSignals", signalsList(),
                "IPSignals", signalsList(),
                "EIPSignals", signalsList(),
                "PIPSignals", signalsList()
        ), List.of(
                "location",
                "resources",
                "photoSignals",
                "IPSignals",
                "EIPSignals",
                "PIPSignals"
        ));
    }

    /**
     *
     */
    static Validator ipGenes() {
        return objectAdditionalProperties(resourceGene());
    }

    /**
     *
     */
    static Validator nonNegativeResources() {
        return objectAdditionalProperties(nonNegativeNumber());
    }

    public static Validator photoGene() {
        return objectPropertiesRequired(Map.of(
                "speed", positiveNumber(),
                "minLevel", positiveNumber(),
                "maxLevel", positiveNumber(),
                "reaction", reaction(),
                "ref", string()
        ), List.of(
                "minLevel",
                "maxLevel",
                "reaction",
                "speed",
                "ref"
        ));
    }

    /**
     *
     */
    static Validator photoGenes() {
        return objectAdditionalProperties(photoGene());
    }

    /**
     *
     */
    static Validator pipGenes() {
        return objectAdditionalProperties(cloneGene());
    }


    /**
     *
     */
    static Validator population() {
        // Compute the shape of signal for each gene
        return objectPropertiesRequired(Map.of(
                "species", species(),
                "individuals", arrayItems(individual())
        ), List.of(
                "species",
                "individuals"
        ));
    }

    /**
     *
     */
    static Validator positiveResources() {
        return objectAdditionalProperties(positiveNumber());
    }

    /**
     *
     */
    static Validator reaction() {
        return objectPropertiesRequired(Map.of(
                "reagents", nonNegativeResources(),
                "products", nonNegativeResources(),
                "thresholds", nonNegativeResources(),
                "speeds", nonNegativeResources()
        ), List.of(
                "reagents",
                "products",
                "thresholds",
                "speeds"
        ));
    }

    /**
     *
     */
    static Validator resourceGene() {
        return objectPropertiesRequired(Map.of(
                "minLevel", positiveNumber(),
                "maxLevel", positiveNumber(),
                "reaction", reaction(),
                "ref", string()
        ), List.of(
                "minLevel",
                "maxLevel",
                "reaction",
                "ref"
        ));
    }

    /**
     *
     */
    static Validator resources() {
        return objectAdditionalProperties(nonNegativeNumber());
    }

    /**
     *
     */
    static Validator signals() {
        return arrayItems(allOf(nonNegativeNumber(), maximum(1d)));
    }

    /**
     *
     */
    public static Validator signalsList() {
        return arrayItems(signals());
    }

    /**
     *
     */
    static Validator species() {
        return objectPropertiesRequired(Map.of(
                        "surviveMass", nonNegativeNumber(),
                        "basalMetabolicRate", positiveNumber(),
                        "fractalDimension", positiveNumber(),
                        "photoGenes", arrayItems(string()),
                        "IPGenes", arrayItems(string()),
                        "EIPGenes", arrayItems(string()),
                        "PIPGenes", arrayItems(string())
                ), List.of(
                        "surviveMass",
                        "basalMetabolicRate",
                        "fractalDimension",
                        "photoGenes",
                        "IPGenes",
                        "EIPGenes",
                        "PIPGenes"
                )
        );
    }
}
