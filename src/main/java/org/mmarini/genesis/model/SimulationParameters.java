/**
 *
 */
package org.mmarini.genesis.model;

import java.util.Random;

/**
 * @author US00852
 *
 */
public class SimulationParameters implements SimulationConstants {

    private final double maxGlucoseLevel;
    private final double maxProbability;
    private final double maxRate;
    private final double maxSynthesisRate;
    private final double absorbingConsumtion;
    private final double maxAbsorbingRate;
    private final double consumptionRate;
    private final Random random;
    private int cols;
    private int rows;
    private double waterLevel;
    private double carbonDioxideLevel;
    private double energyLevel;
    private double carbonDioxideSpread;
    private double waterSpread;
    private double oxygenSpread;
    private double glucoseSpread;
    private double cloneEnergy;
    private double breedEnergy;
    private double mutationProbability;
    private double reactionInterval;
    private double updateInterval;
    private double moveEnergy;

    /**
     *
     */
    public SimulationParameters() {
        random = new Random();
        cloneEnergy = CLONE_ENERGY;
        breedEnergy = BREED_ENERGY;
        maxGlucoseLevel = MAX_GLUCOSE_LEVEL;
        maxProbability = MAX_PROBABILITY;
        maxRate = MAX_RATE;
        maxSynthesisRate = MAX_SYNTHESIS_RATE;
        absorbingConsumtion = ABSORBING_CONSUMPTION;
        maxAbsorbingRate = MAX_ABSORBING_RATE;
        moveEnergy = MOVE_ENERGY;
        consumptionRate = CONSUMPTION_RATE;
    }

    /**
     *
     * @param time
     * @return
     */
    public double computeAbsorbingConsumption(double time) {
        return time * absorbingConsumtion;
    }

    /**
     * /**
     *
     * @param time
     * @return
     */
    public double computeMaxAbsorbingGlucose(double time) {
        return time * maxAbsorbingRate;
    }

    /**
     *
     * @param genotype
     */
    public void generateAbsorbingCode(Genotype genotype) {
        int i = Genotype.ABSORBING_GENE_INDEX;
        genotype.setCode(i++, 0);
        genotype.setCode(i++, CODE_SYMBOLS - 1);
        genotype.setCode(i++, CODE_SYMBOLS - 1);
    }

    /**
     *
     * @param genotype
     */
    public void generateAttackCode(Genotype genotype) {
        int i = Genotype.ATTACK_GENE_INDEX;
        genotype.setCode(i++, 0);
        genotype.setCode(i++, 0);
        genotype.setCode(i++, 0);

        for (int j = 0; j < 8; ++j) {
            genotype.setCode(i++, CODE_SYMBOLS / 2);
            genotype.setCode(i++, CODE_SYMBOLS / 2);
            genotype.setCode(i++, CODE_SYMBOLS / 2);
        }
    }

    /**
     *
     * @param genotype
     */
    public void generateBreedCode(Genotype genotype) {
        int i = Genotype.BREED_GENE_INDEX;
        genotype.setCode(i++, 0);
        genotype.setCode(i++, 1);

        genotype.setCode(i++, 0);
        genotype.setCode(i++, 0);
        genotype.setCode(i++, 0);
        genotype.setCode(i++, 0);
        genotype.setCode(i++, CODE_SYMBOLS - 1);
        genotype.setCode(i++, CODE_SYMBOLS / 3);
    }

    /**
     *
     * @param genotype
     */
    public void generateCloneCode(Genotype genotype) {
        int i = Genotype.CLONE_GENE_INDEX;
        genotype.setCode(i++, 0);
        genotype.setCode(i++, 1);

        genotype.setCode(i++, 0);
        genotype.setCode(i++, 0);
        genotype.setCode(i++, 0);
        genotype.setCode(i++, 0);
        genotype.setCode(i++, CODE_SYMBOLS - 1);
        genotype.setCode(i++, CODE_SYMBOLS / 3);
    }

    /**
     *
     * @return
     */
    public int generateCode() {
        return random.nextInt(CODE_SYMBOLS);
    }

    /**
     *
     * @param genotype
     */
    public void generateDefenseCode(Genotype genotype) {
        int i = Genotype.DEFENSE_GENE_INDEX;
        genotype.setCode(i++, 0);
        genotype.setCode(i++, 0);

        for (int j = 0; j < 4; ++j) {
            genotype.setCode(i++, CODE_SYMBOLS / 2);
        }
    }

    /**
     *
     * @param code
     * @return
     */
    public double generateGlucoseLevel(int code) {
        return code * maxGlucoseLevel / (CODE_SYMBOLS - 1);
    }

    /**
     *
     * @param genotype
     */
    public void generateGlucoseMoveCode(Genotype genotype) {
        int i = Genotype.GLUCOSE_MOVE_GENE_INDEX;
        genotype.setCode(i++, 0);
        genotype.setCode(i++, 0);
        genotype.setCode(i++, 0);
        genotype.setCode(i++, 0);
        genotype.setCode(i++, 0);
        genotype.setCode(i++, CODE_SYMBOLS - 1);
    }

    /**
     *
     * @return
     */
    public double generateProbability(int code) {
        return code * maxProbability / (CODE_SYMBOLS - 1);
    }

    /**
     *
     * @return
     */
    public double generateRate(int code) {
        return code * maxRate / (CODE_SYMBOLS - 1);
    }

    /**
     *
     * @param genotype
     */
    public void generateSynthesisCode(Genotype genotype) {
        int i = 0;
        genotype.setCode(i++, CODE_SYMBOLS - 1);
        genotype.setCode(i++, CODE_SYMBOLS - 1);
        genotype.setCode(i++, CODE_SYMBOLS - 1);
        genotype.setCode(i++, CODE_SYMBOLS - 1);
        genotype.setCode(i++, CODE_SYMBOLS - 1);
    }

    /**
     *
     * @param genotype
     */
    public void generateSynthesisMoveCode(Genotype genotype) {
        int i = Genotype.SYNTHESIS_MOVE_GENE_INDEX;
        genotype.setCode(i++, 0);
        genotype.setCode(i++, 0);
        genotype.setCode(i++, 0);
        genotype.setCode(i++, 0);
        genotype.setCode(i++, 0);
        genotype.setCode(i++, CODE_SYMBOLS - 1);
    }

    /**
     *
     * @param code
     * @return
     */
    public double generateSynthesisRate(int code) {
        return code * maxSynthesisRate / (CODE_SYMBOLS - 1);
    }

    /**
     *
     * @param genotype
     * @return
     */
    public Genotype generateTestCode(Genotype genotype) {
        // Synthesis rules
        generateSynthesisCode(genotype);

        // Clone rules
        generateCloneCode(genotype);

        // Breed rules
        generateBreedCode(genotype);

        // Absorbing rules
        generateAbsorbingCode(genotype);

        // Attack rules
        generateAttackCode(genotype);

        // Defense rules
        generateDefenseCode(genotype);

        // Synthesis move rules
        generateSynthesisMoveCode(genotype);
        return genotype;
    }

    /**
     * @return the breedEnergy
     */
    public double getBreedEnergy() {
        return breedEnergy;
    }

    /**
     * @param breedEnergy
     *            the breedEnergy to set
     */
    public void setBreedEnergy(double breedEnergy) {
        this.breedEnergy = breedEnergy;
    }

    /**
     * @return the co2Level
     */
    public double getCarbonDioxideLevel() {
        return carbonDioxideLevel;
    }

    /**
     * @param co2Level
     *            the co2Level to set
     */
    public void setCarbonDioxideLevel(double co2Level) {
        this.carbonDioxideLevel = co2Level;
    }

    /**
     * @return the carbonDioxideSpread
     */
    public double getCarbonDioxideSpread() {
        return carbonDioxideSpread;
    }

    /**
     * @param carbonDioxideSpread
     *            the carbonDioxideSpread to set
     */
    public void setCarbonDioxideSpread(double carbonDioxideSpread) {
        this.carbonDioxideSpread = carbonDioxideSpread;
    }

    /**
     * @return the cloneEnergy
     */
    public double getCloneEnergy() {
        return cloneEnergy;
    }

    /**
     * @param cloneEnergy
     *            the cloneEnergy to set
     */
    public void setCloneEnergy(double cloneEnergy) {
        this.cloneEnergy = cloneEnergy;
    }

    /**
     * @return the cols
     */
    public int getCols() {
        return cols;
    }

    /**
     * @param cols
     *            the cols to set
     */
    public void setCols(int cols) {
        this.cols = cols;
    }

    /**
     * @return the consumptionRate
     */
    public double getConsumptionRate() {
        return consumptionRate;
    }

    /**
     * @return the energyLevel
     */
    public double getEnergyLevel() {
        return energyLevel;
    }

    /**
     * @param energyLevel
     *            the energyLevel to set
     */
    public void setEnergyLevel(double energyLevel) {
        this.energyLevel = energyLevel;
    }

    /**
     * @return the glucoseSpread
     */
    public double getGlucoseSpread() {
        return glucoseSpread;
    }

    /**
     * @param glucoseSpread
     *            the glucoseSpread to set
     */
    public void setGlucoseSpread(double glucoseSpread) {
        this.glucoseSpread = glucoseSpread;
    }

    /**
     * @return the moveEnergy
     */
    public double getMoveEnergy() {
        return moveEnergy;
    }

    /**
     * @param moveEnergy
     *            the moveEnergy to set
     */
    public void setMoveEnergy(double moveEnergy) {
        this.moveEnergy = moveEnergy;
    }

    /**
     * @return the mutationProbability
     */
    public double getMutationProbability() {
        return mutationProbability;
    }

    /**
     * @param mutationProbability
     *            the mutationProbability to set
     */
    public void setMutationProbability(double mutationProbability) {
        this.mutationProbability = mutationProbability;
    }

    /**
     * @return the oxygenSpread
     */
    public double getOxygenSpread() {
        return oxygenSpread;
    }

    /**
     * @param oxygenSpread
     *            the oxygenSpread to set
     */
    public void setOxygenSpread(double oxygenSpread) {
        this.oxygenSpread = oxygenSpread;
    }

    /**
     * @return the random
     */
    public Random getRandom() {
        return random;
    }

    /**
     * @return the reactionInterval
     */
    public double getReactionInterval() {
        return reactionInterval;
    }

    /**
     * @param reactionInterval
     *            the reactionInterval to set
     */
    public void setReactionInterval(double reactionInterval) {
        this.reactionInterval = reactionInterval;
    }

    /**
     * @return the rows
     */
    public int getRows() {
        return rows;
    }

    /**
     * @param rows
     *            the rows to set
     */
    public void setRows(int rows) {
        this.rows = rows;
    }

    /**
     * @return the timeInterval
     */
    public double getUpdateInterval() {
        return updateInterval;
    }

    /**
     * @param updateInterval
     *            the timeInterval to set
     */
    public void setUpdateInterval(double updateInterval) {
        this.updateInterval = updateInterval;
    }

    /**
     * @return the h2oLevel
     */
    public double getWaterLevel() {
        return waterLevel;
    }

    /**
     * @param h2oLevel
     *            the h2oLevel to set
     */
    public void setWaterLevel(double h2oLevel) {
        this.waterLevel = h2oLevel;
    }

    /**
     * @return the waterSpread
     */
    public double getWaterSpread() {
        return waterSpread;
    }

    /**
     * @param waterSpread
     *            the waterSpread to set
     */
    public void setWaterSpread(double waterSpread) {
        this.waterSpread = waterSpread;
    }

    /**
     *
     * @param probability
     * @return
     */
    public boolean hasChance(double probability) {
        return random.nextDouble() < probability;
    }

    /**
     *
     * @param time
     * @return
     */
    public boolean hasReaction(double time) {
        double pr = -Math.expm1(-time / reactionInterval);
        return hasChance(pr);
    }

    /**
     *
     * @return
     */
    public double nextRandomDouble() {
        return random.nextDouble();
    }

    /**
     *
     * @param probabilities
     * @return
     */
    public int nextRandomInt(double[] probabilities) {
        double tot = 0;
        for (double v : probabilities)
            tot += v;
        double p = random.nextDouble() * tot;
        tot = 0;
        int n = probabilities.length - 1;
        for (int i = 0; i < n; ++i) {
            tot += probabilities[i];
            if (p < tot)
                return i;
        }
        return n;
    }

    /**
     *
     * @param n
     * @return
     */
    public int nextRandomInt(int n) {
        return random.nextInt(n);
    }

    /**
     * @param seed
     * @see java.util.Random#setSeed(long)
     */
    public void setSeed(long seed) {
        random.setSeed(seed);
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "SimulationParameters [cols=" + cols + ", rows=" + rows
                + ", waterLevel=" + waterLevel + ", carbonDioxideLevel="
                + carbonDioxideLevel + ", energyLevel=" + energyLevel
                + ", carbonDioxideSpread=" + carbonDioxideSpread
                + ", waterSpread=" + waterSpread + ", oxygenSpread="
                + oxygenSpread + ", glucoseSpread=" + glucoseSpread
                + ", cloneEnergy=" + cloneEnergy + ", breedEnergy="
                + breedEnergy + ", mutationProbability=" + mutationProbability
                + ", reactionInterval=" + reactionInterval
                + ", updateInterval=" + updateInterval + ", maxGlucoseLevel="
                + maxGlucoseLevel + ", maxProbability=" + maxProbability
                + ", maxRate=" + maxRate + ", maxSynthesisRate="
                + maxSynthesisRate + "]";
    }
}
