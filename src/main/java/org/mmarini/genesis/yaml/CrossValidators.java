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
import org.mmarini.genesis.model3.EIPGene;
import org.mmarini.genesis.model3.PIPGene;
import org.mmarini.genesis.model3.PhotoReactionProcess;
import org.mmarini.genesis.model3.ReactionProcess;
import org.mmarini.yaml.schema.Locator;
import org.mmarini.yaml.schema.Validator;

import java.awt.*;
import java.util.List;
import java.util.*;

import static java.util.Objects.requireNonNull;
import static org.mmarini.Utils.getValue;
import static org.mmarini.Utils.iterable;
import static org.mmarini.yaml.schema.Validator.*;

/**
 *
 */
public class CrossValidators {

    /**
     * @param keys resource keys
     */
    public static Validator cloneGene(List<String> keys) {
        return property("energyRef", values(keys));
    }

    /**
     *
     */
    public static Validator config() {
        return deferred((root, locator) -> {
            List<String> keys = Parsers.names(root.path("mass"));

            Validator populations = deferred((root1, locator1) -> {
                Dimension size = Parsers.dimension(root.path("environ"));
                int noCells = size.width * size.height;
                Map<String, ? extends PhotoReactionProcess> photoProcesses = Parsers.photoGenes(root.path("photoProcesses"), keys);
                Map<String, ? extends ReactionProcess> reactionProcesses = Parsers.reactionProcesses(root.path("reactionProcesses"), keys);
                Map<String, ? extends EIPGene> eipGenes = Parsers.eipGenes(root.path("eipgenes"), keys);
                Map<String, ? extends PIPGene> pipGenes = Parsers.pipGenes(root.path("pipgenes"), keys);
                return arrayItems(population(keys, noCells,
                        photoProcesses, reactionProcesses, eipGenes, pipGenes));
            });

            return object(
                    property("energyRef", string(values(keys))),
                    property("environ", environ(keys)),
                    property("photoProcesses", photoProcesses(keys)),
                    property("reactionProcesses", reactionProcesses(keys)),
//                    property("ipgenes", reactionProcesses(keys)),
                    property("eipgenes", eipGenes(keys)),
                    property("pipgenes", pipGenes(keys)),
                    property("populations", populations)
            );
        });
    }

    /**
     * @param keys resource keys
     */
    public static Validator eipGenes(List<String> keys) {
        return additionalProperties(exchangeResourceGene(keys));
    }

    /**
     * @param keys resource keys
     */
    public static Validator environ(List<String> keys) {
        return allOf(
                property("resources", nonNegativeResources(keys)),
                property("diffusion", nonNegativeResources(keys))
        );
    }

    /**
     * @param keys resource keys
     */
    public static Validator exchangeResourceGene(List<String> keys) {
        return allOf(
                property("minLevels", positiveResources(keys)),
                property("maxLevels", positiveResources(keys)),
                property("rates", positiveResources(keys))
        );
    }

    /**
     * @param keys             resource keys
     * @param noCells          the number of cells in the environment
     * @param photoSignalSizes the ip signal sizes
     * @param ipSizes          the ip signal sizes
     * @param eipSizes         the eip signal sizes
     * @param pipSizes         the pip signal sizes
     */
    public static Validator individual(List<String> keys, int noCells,
                                       int[] photoSignalSizes,
                                       int[] ipSizes,
                                       int[] eipSizes,
                                       int[] pipSizes) {
        return allOf(
                property("location", allOf(
                        minimum(0),
                        exclusiveMaximum(noCells))),
                property("photoGenes", signalsList(photoSignalSizes)),
                property("reactionGenes", signalsList(ipSizes)),
                property("EIPSignals", signalsList(eipSizes)),
                property("PIPSignals", signalsList(pipSizes)),
                property("resources", nonNegativeResources(keys))
        );
    }

    /**
     * @param keys resource keys
     */
    public static Validator nonNegativeResources(List<String> keys) {
        return locator -> root -> {
            JsonNode node = locator.getNode(root);
            for (String name : iterable(node.fieldNames())) {
                assertFor(keys.contains(name), locator.path(name), "resource undefined");
            }
        };
    }

    public static Validator photoProcess(List<String> keys) {
        return allOf(
                property("ref", values(keys)),
                property("reaction", reaction(keys))
        );
    }

    /**
     * @param keys resource keys
     */
    public static Validator photoProcesses(List<String> keys) {
        return additionalProperties(photoProcess(keys));
    }

    public static Validator pipGenes(List<String> keys) {
        return additionalProperties(cloneGene(keys));
    }

    public static Validator population(List<String> keys, int noCells,
                                       final Map<String, ? extends PhotoReactionProcess> photoProcesses,
                                       final Map<String, ? extends ReactionProcess> reactionProcesses,
                                       final Map<String, ? extends EIPGene> eipGenes,
                                       final Map<String, ? extends PIPGene> pipGenes) {
        Validator deferredIndividual = deferred((root, locator) -> {
            Locator speciesLoc = locator.parent(2).path("species");
            int[] photoSizes = speciesLoc.path("photoProcesses")
                    .elements(root)
                    .map(Locator::getPointer)
                    .map(root::at)
                    .map(JsonNode::asText)
                    .map(getValue(photoProcesses))
                    .flatMap(Optional::stream)
                    .mapToInt(x -> 1)
                    .toArray();
            int[] reactionSizes = speciesLoc.path("reactionProcesses")
                    .elements(root)
                    .map(Locator::getPointer)
                    .map(root::at)
                    .map(JsonNode::asText)
                    .map(getValue(reactionProcesses))
                    .flatMap(Optional::stream)
                    .mapToInt(x -> 1)
                    .toArray();
            int[] eipSizes = speciesLoc.path("EIPGenes").elements(root)
                    .map(Locator::getPointer)
                    .map(root::at)
                    .map(JsonNode::asText)
                    .map(getValue(eipGenes))
                    .flatMap(Optional::stream)
                    .mapToInt(EIPGene::getNumSignals)
                    .toArray();
            int[] pipSizes = speciesLoc.path("PIPGenes").elements(root)
                    .map(Locator::getPointer)
                    .map(root::at)
                    .map(JsonNode::asText)
                    .map(getValue(pipGenes))
                    .flatMap(Optional::stream)
                    .mapToInt(PIPGene::getNumSignals)
                    .toArray();
            return individual(keys, noCells, photoSizes, reactionSizes, eipSizes, pipSizes);
        });
        return allOf(
                property("species", species(
                        photoProcesses.keySet(),
                        reactionProcesses.keySet(),
                        eipGenes.keySet(),
                        pipGenes.keySet())),
                property("individuals",
                        items(deferredIndividual)
                )
        );
    }

    /**
     * @param keys resource keys
     */
    public static Validator positiveResources(List<String> keys) {
        return locator -> root -> {
            for (String key : keys) {
                Locator child = locator.path(key);
                JsonNode node1 = child.getNode(root);
                assertFor(!node1.isMissingNode(), child, "is missing");
            }
            JsonNode node = locator.getNode(root);
            for (String name : iterable(node.fieldNames())) {
                Locator child = locator.path(name);
                assertFor(keys.contains(name), child, "resource undefined");
            }
        };
    }

    /**
     * @param keys resource keys
     */
    public static Validator reaction(List<String> keys) {
        return allOf(
                property("reagents", nonNegativeResources(keys)),
                property("products", nonNegativeResources(keys)),
                property("thresholds", nonNegativeResources(keys)),
                property("speeds", nonNegativeResources(keys))
        );
    }

    /**
     * @param keys resource keys
     */
    public static Validator reactionProcess(List<String> keys) {
        return allOf(
                property("ref", values(keys)),
                property("reaction", reaction(keys))
        );
    }

    /**
     * @param keys resource keys
     */
    public static Validator reactionProcesses(List<String> keys) {
        return additionalProperties(reactionProcess(keys));
    }

    /**
     * @param size the number of signals
     */
    public static Validator signals(int size) {
        return allOf(
                minItems(size),
                maxItems(size)
        );
    }

    /**
     * @param sizes the sizes
     */
    public static Validator signalsList(int... sizes) {
        requireNonNull(sizes);
        Validator[] prefixItems = Arrays.stream(sizes)
                .mapToObj(CrossValidators::signals)
                .toArray(Validator[]::new);
        return allOf(
                minItems(sizes.length),
                maxItems(sizes.length),
                prefixItems(prefixItems)
        );
    }

    /**
     * @param photoKeys    the ip genes keys
     * @param reactionKeys the ip genes keys
     * @param eipKeys      the eip genes keys
     * @param pipKeys      the pip genes keys
     */
    public static Validator species(Collection<String> photoKeys, Collection<String> reactionKeys, Collection<String> eipKeys, Collection<String> pipKeys) {
        return allOf(
                property("photoProcesses", items(values(photoKeys))),
                property("reactionProcesses", items(values(reactionKeys))),
                property("EIPGenes", items(values(eipKeys))),
                property("PIPGenes", items(values(pipKeys)))
        );
    }
}
