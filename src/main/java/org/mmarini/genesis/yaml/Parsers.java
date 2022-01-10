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
import org.mmarini.Tuple2;
import org.mmarini.genesis.model3.*;

import java.awt.*;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.mmarini.Tuple2.toMap;
import static org.mmarini.Tuple2.zip;
import static org.mmarini.Utils.*;
import static org.mmarini.genesis.model3.Matrix.hstack;
import static org.mmarini.genesis.model3.Matrix.ones;

public class Parsers {
    /**
     * @param node the json node
     * @param keys the resource keys
     */
    public static CloneGene cloneGene(JsonNode node, List<String> keys) {
        return CloneGene.create(
                node.path("minMassThreshold").asDouble(0),
                node.path("maxMassThreshold").asDouble(0),
                node.path("minEnergyThreshold").asDouble(0),
                node.path("maxEnergyThreshold").asDouble(0),
                node.path("minMassProbability").asDouble(0),
                node.path("maxMassProbability").asDouble(0),
                node.path("minEnergyProbability").asDouble(0),
                node.path("maxEnergyProbability").asDouble(0),
                keys.indexOf(node.path("energyRef").asText()),
                node.path("inPlacePreference").asDouble(0),
                node.path("adjacentPreference").asDouble(0),
                node.path("mutationProb").asDouble(0),
                node.path("mutationSigma").asDouble(0)
        );
    }

    /**
     * @param individuals the individual nodes
     * @param signalsKey  the signal keyword
     * @param noGenes     the number of genes
     */
    private static List<Matrix> createSignals(
            List<JsonNode> individuals,
            String signalsKey,
            int noGenes) {
        List<List<Matrix>> signalsGenes = individuals.stream()
                .map(n -> n.path(signalsKey))
                .map(Parsers::signalsList)
                .collect(Collectors.toList());
        return IntStream.range(0, noGenes)
                // For each gene
                .mapToObj(i ->
                        // transforms the individual gene signals in a single matrix
                        hstack(signalsGenes
                                .stream()
                                .map(signals -> signals.get(i))
                                .toArray(Matrix[]::new)))
                .collect(Collectors.toList());
    }


    /**
     * Returns the dimensions of environment
     *
     * @param node the json node
     */
    public static Dimension dimension(JsonNode node) {
        return new Dimension(
                node.path("width").asInt(0),
                node.path("height").asInt(0)
        );
    }

    /**
     * @param node the json node
     * @param keys the resource keys
     */
    public static Map<String, ? extends EIPGene> eipGenes(JsonNode node, List<String> keys) {
        return iter2List(node.fieldNames())
                .stream()
                .map(name -> Tuple2.of(name, Parsers.exchangeResourceGene(node.path(name), keys)))
                .collect(toMap());
    }

    /**
     * @param node the json node
     */
    public static SimEngine engine(JsonNode node) {
        List<String> keys = names(node.path("mass"));
        Matrix mass = resources(node.path("mass"));
        JsonNode environ = node.path("environ");
        Dimension size = dimension(environ);
        JsonNode lengthNode = environ.path("length");
        double length = lengthNode.asDouble(0);
        final Topology3 top = Topology3.create(size.width, size.height, length);
        final Matrix alpha = resources(environ.path("diffusion"), keys);
        final int energyReg = keys.indexOf(node.path("energyRef").asText(""));
        return SimEngine.create(mass, top, alpha, energyReg);
    }

    /**
     * @param node the json node
     * @param keys the resource keys
     */
    public static ExchangeResourceGene exchangeResourceGene(JsonNode node, List<String> keys) {
        final Matrix min = resources(node.path("minLevels"), keys);
        final Matrix max = resources(node.path("maxLevels"), keys);
        final Matrix log = max.divi(min).logi();
        final Matrix rates = resources(node.path("rates"), keys);
        return new ExchangeResourceGene(min, log, rates);
    }

    /**
     * @param node the json node
     */
    static Stream<String> fromArray(JsonNode node) {
        return iter2List(node.elements())
                .stream()
                .map(JsonNode::asText);
    }

    /**
     * Returns the individual from json node
     *
     * @param node              the jason node
     * @param keys              the resource keys
     * @param photoProcesses    the list of photo processes
     * @param reactionProcesses the list of reaction processes
     */
    public static Individual individual(JsonNode node, List<String> keys,
                                        List<? extends PhotoReactionProcess> photoProcesses,
                                        List<? extends ReactionProcess> reactionProcesses) {
        int location = node.path("location").asInt(0);
        Matrix resources = Parsers.resources(node.path("resources"), keys);
        List<Matrix> photoSignals = Parsers.signalsList(node.path("photoGenes"));
        List<Matrix> reactionSignals = Parsers.signalsList(node.path("reactionGenes"));
        Matrix photoTargetLevels = Matrix.vstack(
                zip(photoSignals, photoProcesses)
                        .map(t ->
                                t._2.createTargetLevels(t._1))
                        .toArray(Matrix[]::new));
        Matrix reactionTargetLevels1 = Matrix.vstack(
                zip(reactionSignals, reactionProcesses)
                        .map(t ->
                                t._2.createTargetLevels(t._1))
                        .toArray(Matrix[]::new));
        return Individual.create(location, resources, photoTargetLevels, reactionTargetLevels1);
    }

    /**
     * Returns the properties names
     *
     * @param node the json node
     */
    static List<String> names(JsonNode node) {
        return iter2List(node.fieldNames());
    }

    /**
     * Returns the photo gene
     *
     * @param node the json node
     * @param keys the resource keys
     */
    public static PhotoReactionProcess photoGene(JsonNode node, List<String> keys) {
        String ref = node.path("ref").asText("");
        double minLevel = node.path("minLevel").asDouble(0);
        double maxLevel = node.path("maxLevel").asDouble(0);
        double speed = node.path("speed").asDouble(0);

        return PhotoReactionProcess.create(keys.indexOf(ref),
                speed,
                minLevel, maxLevel,
                reaction(node.path("reaction"), keys));
    }

    /**
     * @param node the json node
     * @param keys the resource keys
     */
    static Map<String, ? extends PhotoReactionProcess> photoGenes(JsonNode node, List<String> keys) {
        return iter2List(node.fieldNames())
                .stream()
                .map(name -> Tuple2.of(name, Parsers.photoGene(node.path(name), keys)))
                .collect(toMap());
    }

    /**
     * @param node the json node
     * @param keys the resource keys
     */
    static Map<String, ? extends PIPGene> pipGenes(JsonNode node, List<String> keys) {
        return iter2List(node.fieldNames())
                .stream()
                .map(name -> Tuple2.of(name, Parsers.cloneGene(node.path(name), keys)))
                .collect(toMap());
    }

    /**
     * @param node              the population json node
     * @param keys              the resource keys
     * @param photoGenes        the photo reaction genes
     * @param reactionProcesses the ip genes
     * @param eipGenes          the eip genes
     * @param pipGenes          the pip genes
     */
    public static Population population(JsonNode node,
                                        List<String> keys,
                                        Map<? super String, ? extends PhotoReactionProcess> photoGenes,
                                        final Map<? super String, ? extends ReactionProcess> reactionProcesses,
                                        final Map<String, ? extends EIPGene> eipGenes,
                                        final Map<String, ? extends PIPGene> pipGenes) {
        Species species = species(node.path("species"), photoGenes, reactionProcesses, eipGenes, pipGenes);
        List<Individual> individuals = stream(node.path("individuals").elements())
                .map(n -> individual(n, keys,
                        species.getPhotoProcesses(),
                        species.getReactionProcesses()
                ))
                .collect(Collectors.toList());
        Matrix resources = hstack(individuals.stream()
                .map(Individual::getResources)
                .toArray(Matrix[]::new));
        int[] locations = individuals.stream().mapToInt(Individual::getLocation).toArray();

        // Extracts photo target levels
        Matrix photoTargetLevelMatrix = hstack(individuals.stream()
                .map(Individual::getPhotoTargetLevels)
                .toArray(Matrix[]::new));
        List<Matrix> photoTargetLevels = IntStream.range(0, photoTargetLevelMatrix.getNumRows())
                .mapToObj(photoTargetLevelMatrix::extractRow)
                .collect(Collectors.toList());

        // Extracts reaction target levels
        Matrix reactionTargetLevelMatrix = hstack(individuals.stream()
                .map(Individual::getReactionTargetLevels)
                .toArray(Matrix[]::new));
        List<Matrix> reactionTargetLevels = IntStream.range(0, reactionTargetLevelMatrix.getNumRows())
                .mapToObj(reactionTargetLevelMatrix::extractRow)
                .collect(Collectors.toList());

        List<JsonNode> individualNodes = iter2List(node.path("individuals").elements());

        List<Matrix> eipSignals = createSignals(individualNodes,
                "EIPSignals",
                species.getEipGenes().size());
        List<Matrix> pipSignals = createSignals(individualNodes,
                "PIPSignals",
                species.getPipGenes().size());
        return Population.create(resources, photoTargetLevels, reactionTargetLevels, eipSignals, pipSignals,
                locations, species);
    }

    static List<Population> populations(JsonNode node,
                                        List<String> keys,
                                        Map<? super String, ? extends PhotoReactionProcess> photoProcesses,
                                        Map<? super String, ? extends ReactionProcess> reactionProcesses,
                                        final Map<String, ? extends EIPGene> eipGenes,
                                        final Map<String, ? extends PIPGene> pipGenes) {
        return stream(node.elements())
                .map(nod -> population(nod, keys, photoProcesses, reactionProcesses, eipGenes, pipGenes))
                .collect(Collectors.toList());
    }

    /**
     * @param node the json node
     * @param keys the resource keys
     */
    public static Reaction reaction(JsonNode node, List<String> keys) {
        Matrix reagents = resources(node.path("reagents"), keys);
        Matrix products = resources(node.path("products"), keys);
        Matrix thresholds = resources(node.path("thresholds"), keys);
        Matrix speeds = resources(node.path("speeds"), keys);
        return Reaction.create(reagents, products, thresholds, speeds);
    }

    /**
     * Returns the reaction process for a json node
     *
     * @param node the json node
     * @param keys the resource keys
     */
    public static ReactionProcess reactionProcess(JsonNode node, List<String> keys) {
        String ref = node.path("ref").asText("");
        double minLevel = node.path("minLevel").asDouble(0);
        double maxLevel = node.path("maxLevel").asDouble(0);
        return ReactionProcess.create(keys.indexOf(ref),
                minLevel, maxLevel,
                reaction(node.path("reaction"), keys));
    }

    /**
     * @param node the json node
     * @param keys the resource keys
     */
    public static Map<String, ? extends ReactionProcess> reactionProcesses(JsonNode node, List<String> keys) {
        return iter2List(node.fieldNames())
                .stream()
                .map(name -> Tuple2.of(name, Parsers.reactionProcess(node.path(name), keys)))
                .collect(toMap());
    }

    /**
     * Returns the matrix with resources
     *
     * @param node the json node
     */
    static Matrix resources(JsonNode node) {
        return Matrix.of(iter2List(node.fieldNames()).stream()
                .map(node::path)
                .mapToDouble(JsonNode::asDouble)
                .toArray());
    }

    /**
     * Returns the matrix with resources
     *
     * @param node the json node
     * @param keys the resource keys
     */
    static Matrix resources(JsonNode node, List<String> keys) {
        return Matrix.of(keys.stream()
                .map(node::path)
                .mapToDouble(node1 -> node1.asDouble(0))
                .toArray());
    }

    /**
     * @param node the json node
     */
    static Matrix signals(JsonNode node) {
        return Matrix.of(iter2List(node.elements()).stream()
                .mapToDouble(JsonNode::asDouble)
                .toArray());
    }

    /**
     * @param node the json node
     */
    public static List<Matrix> signalsList(JsonNode node) {
        return iter2List(node.elements()).stream()
                .map(Parsers::signals)
                .collect(Collectors.toList());
    }

    /**
     * @param node              the species json node
     * @param photoProcesses    the photo reaction processes
     * @param reactionProcesses the reaction processes
     * @param eipGenes          the eip genes
     * @param pipGenes          the pip genes
     */
    public static Species species(JsonNode node,
                                  Map<? super String, ? extends PhotoReactionProcess> photoProcesses,
                                  Map<? super String, ? extends ReactionProcess> reactionProcesses,
                                  Map<String, ? extends EIPGene> eipGenes,
                                  Map<String, ? extends PIPGene> pipGenes) {
        List<? extends EIPGene> speciesEipGenes = fromArray(node.path("EIPGenes"))
                .map(getValue(eipGenes))
                .flatMap(Optional::stream)
                .collect(Collectors.toList());
        List<? extends PIPGene> speciesPipGenes = fromArray(node.path("PIPGenes"))
                .map(getValue(pipGenes))
                .flatMap(Optional::stream)
                .collect(Collectors.toList());
        List<? extends PhotoReactionProcess> photoGene = fromArray(node.path("photoProcesses"))
                .map(getValue(photoProcesses))
                .flatMap(Optional::stream)
                .collect(Collectors.toList());
        List<? extends ReactionProcess> reactionProcesses1 = fromArray(node.path("reactionProcesses"))
                .map(getValue(reactionProcesses))
                .flatMap(Optional::stream)
                .collect(Collectors.toList());
        return Species.create(node.path("basalMetabolicRate").asDouble(0),
                node.path("surviveMass").asDouble(0),
                node.path("fractalDimension").asDouble(0),
                photoGene, reactionProcesses1, speciesEipGenes, speciesPipGenes);
    }

    /**
     * @param node the json node
     */
    public static SimStatus status(JsonNode node) {
        List<String> keys = names(node.path("mass"));

        JsonNode environ = node.path("environ");
        Dimension size = dimension(environ);
        int n = size.width * size.height;
        Matrix envResources = resources(environ.path("resources"), keys);
        Matrix resources = ones(1, n).prod(envResources);

        Map<String, ? extends PhotoReactionProcess> photoGenes = photoGenes(node.path("photoProcesses"), keys);
        Map<String, ? extends ReactionProcess> ipgenes = reactionProcesses(node.path("reactionProcesses"), keys);
        Map<String, ? extends EIPGene> eipgenes = eipGenes(node.path("eipgenes"), keys);
        Map<String, ? extends PIPGene> pipgenes = pipGenes(node.path("pipgenes"), keys);

        final List<Population> populations = populations(node.path("populations"),
                keys, photoGenes, ipgenes, eipgenes, pipgenes);
        return SimStatus.create(0, resources, populations);
    }
}
