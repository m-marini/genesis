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

package org.mmarini.genesis.model3;

import java.util.List;

import static java.util.Objects.requireNonNull;

/**
 * The species has all the parameter that define the behaviour of a species.
 * Each individual consumes energy in a time interval to maintain the life,
 * the rate of consumption is the basal metabolic rate in unit of energy per second per gram.
 * Each individual can survive only if it has a minimal quantities of resource (surviving mass).
 * Each individual has a surface that allows to move resources and energy with the environment,
 * the areas by mass is the area of surface and per mass (gram).
 * Each individual can transform resources using the environment energy (light),
 * the photo processes is the list of such process
 */
public class Species {
    /**
     * Returns a new species
     *
     * @param basalMetabolicRate basal metabolic rate unit/s/g
     * @param survivingMass      the surviving mass unit
     * @param fractalDimension   the fractal dimension of exchange resources surface
     * @param photoProcesses     the list of photo processes
     * @param ipGenes            the list of ip genes
     * @param eipGenes           the list of eip genes
     * @param pipGenes           the list of pip genes
     */
    public static Species create(double basalMetabolicRate,
                                 double survivingMass,
                                 double fractalDimension,
                                 List<? extends PhotoProcess> photoProcesses,
                                 List<? extends IPGene> ipGenes,
                                 List<? extends EIPGene> eipGenes,
                                 List<? extends PIPGene> pipGenes) {
        return new Species(basalMetabolicRate, survivingMass, fractalDimension, photoProcesses, ipGenes, eipGenes, pipGenes);
    }

    private final double basalMetabolicRate;
    private final double survivingMass;
    private final double fractalDimension;
    private final List<? extends PhotoProcess> photoProcesses;
    private final List<? extends IPGene> ipGenes;
    private final List<? extends EIPGene> eipGenes;
    private final List<? extends PIPGene> pipGenes;

    /**
     * Creates a species
     *
     * @param basalMetabolicRate basal metabolic rate unit/s/g
     * @param survivingMass      the surviving mass unit
     * @param fractalDimension   the fractal dimension of exchange resources surface
     * @param photoProcesses     the list of photo processes
     * @param ipGenes            the list of ip genes
     * @param eipGenes           the list of eip genes
     * @param pipGenes           the list of pip genes
     */
    protected Species(double basalMetabolicRate,
                      double survivingMass,
                      double fractalDimension,
                      List<? extends PhotoProcess> photoProcesses, List<? extends IPGene> ipGenes,
                      List<? extends EIPGene> eipGenes,
                      List<? extends PIPGene> pipGenes) {
        this.basalMetabolicRate = basalMetabolicRate;
        this.survivingMass = survivingMass;
        this.fractalDimension = fractalDimension;
        this.photoProcesses = requireNonNull(photoProcesses);
        this.ipGenes = requireNonNull(ipGenes);
        this.eipGenes = requireNonNull(eipGenes);
        this.pipGenes = requireNonNull(pipGenes);
    }

    /**
     *
     */
    public double getBasalMetabolicRate() {
        return basalMetabolicRate;
    }

    /**
     *
     */
    public List<? extends EIPGene> getEipGenes() {
        return eipGenes;
    }

    /**
     *
     */
    public double getFractalDimension() {
        return fractalDimension;
    }

    /**
     *
     */
    public List<? extends IPGene> getIpGenes() {
        return ipGenes;
    }

    /**
     *
     */
    public List<? extends PhotoProcess> getPhotoProcess() {
        return this.photoProcesses;
    }

    /**
     *
     */
    public List<? extends PIPGene> getPipGenes() {
        return pipGenes;
    }

    /**
     *
     */
    public double getSurvivingMass() {
        return survivingMass;
    }
}
