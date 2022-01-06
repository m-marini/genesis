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

import com.fasterxml.jackson.core.JsonPointer;
import com.fasterxml.jackson.databind.JsonNode;
import org.mmarini.genesis.model3.*;

import java.awt.*;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.mmarini.genesis.model3.Matrix.ones;

public class Config extends AbstractConfig {

    public static final String ENERGY_REF = "energyRef";

    /**
     * @param root json node
     * @param at   location
     */
    public Config(final JsonNode root, final JsonPointer at) {
        super(root, at);
    }

    /**
     *
     */
    public SimEngine createEngine() {
        final EnvironConfig env = environ();
        final Dimension s = env.size();
        final Topology3 top = Topology3.create(s.width, s.height, env.length());
        final List<String> k = keys().collect(Collectors.toList());
        final Matrix alpha = env.diffusion().createMatrix(k);
        final int energyReg = k.indexOf(energyRef());
        return new SimEngine(mass().createMatrix(), top, alpha, energyReg);
    }

    /**
     *
     */
    public SimStatus createStatus() {
        final List<String> keys = keys().collect(Collectors.toList());

        final EnvironConfig environ = environ();

        final int n = environ.noCells();
        final Matrix substances = environ.substances().createMatrix(keys);
        final Matrix qties = ones(1, n)
                .prod(substances);

        final Map<String, IPGene> ipgenes = ipgenes().createGeneMap(keys);
        final Map<String, EIPGene> eipgenes = eipgenes().createGeneMap(keys);
        final Map<String, PIPGene> pipgenes = pipgenes().createGeneMap(keys);

        final List<Population> populations = populations().items()
                .map(populationConfig ->
                        populationConfig.createPopulation(keys, ipgenes, eipgenes, pipgenes))
                .collect(Collectors.toList());
        return new SimStatus(0, qties, populations);
    }

    /**
     *
     */
    public EIPGenesConfig eipgenes() {
        return new EIPGenesConfig(getRoot(), path("eipgenes"));
    }

    /**
     *
     */
    public String energyRef() {
        return at(path(ENERGY_REF)).asText();
    }

    /**
     *
     */
    public EnvironConfig environ() {
        return new EnvironConfig(getRoot(), path("environ"));
    }

    /**
     *
     */
    public IPGenesConfig ipgenes() {
        return new IPGenesConfig(getRoot(), path("ipgenes"));
    }

    /**
     *
     */
    public Stream<String> keys() {
        return mass().keys();
    }

    /**
     *
     */
    public SubstancesConfig mass() {
        return new SubstancesConfig(getRoot(), path("mass"));
    }

    /**
     *
     */
    public PIPGenesConfig pipgenes() {
        return new PIPGenesConfig(getRoot(), path("pipgenes"));
    }

    /**
     *
     */
    public ArrayConfig<PopulationConfig> populations() {
        return new ArrayConfig<>(getRoot(), path("populations"), PopulationConfig::new);
    }

    /**
     *
     */
    public Config validate() {
        requireObject();
        mass().validate();

        final List<String> keys = keys().collect(Collectors.toList());
        requireString(ENERGY_REF);
        final String ref = energyRef();
        if (!keys.contains(ref)) {
            throwError(path(ENERGY_REF), "resource \"%s\" undefined", ref);
        }

        environ().validate(keys);

        ipgenes().validate(keys);
        eipgenes().validate(keys);
        pipgenes().validate(keys);

        populations().validate();
        final int noCells = environ().noCells();
        final Map<String, IPGene> ipgenes = ipgenes().createGeneMap(keys);
        final Map<String, EIPGene> eipgenes = eipgenes().createGeneMap(keys);
        final Map<String, PIPGene> pipgenes = pipgenes().createGeneMap(keys);
        populations().items().forEach(populationConfig ->
                populationConfig.validate(keys, noCells, ipgenes, eipgenes, pipgenes));
        return this;
    }
}
