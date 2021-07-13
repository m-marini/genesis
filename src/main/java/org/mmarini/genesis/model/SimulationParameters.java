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

	private final double maxGlucoseLevel;
	private final double maxProbability;
	private final double maxRate;
	private final double maxSynthesisRate;

	private final double absorbingConsumtion;
	private final double maxAbsorbingRate;
	private double moveEnergy;
	private final double consumptionRate;
	private final Random random;

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
	public double computeAbsorbingConsumption(final double time) {
		return time * absorbingConsumtion;
	}

	/**
	 * /**
	 * 
	 * @param time
	 * @return
	 */
	public double computeMaxAbsorbingGlucose(final double time) {
		return time * maxAbsorbingRate;
	}

	/**
	 * 
	 * @param genotype
	 */
	public void generateAbsorbingCode(final Genotype genotype) {
		int i = Genotype.ABSORBING_GENE_INDEX;
		genotype.setCode(i++, 0);
		genotype.setCode(i++, CODE_SYMBOLS - 1);
		genotype.setCode(i++, CODE_SYMBOLS - 1);
	}

	/**
	 * 
	 * @param genotype
	 */
	public void generateAttackCode(final Genotype genotype) {
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
	public void generateBreedCode(final Genotype genotype) {
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
	public void generateCloneCode(final Genotype genotype) {
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
	public void generateDefenseCode(final Genotype genotype) {
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
	public double generateGlucoseLevel(final int code) {
		return code * maxGlucoseLevel / (CODE_SYMBOLS - 1);
	}

	/**
	 * 
	 * @param genotype
	 */
	public void generateGlucoseMoveCode(final Genotype genotype) {
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
	public double generateProbability(final int code) {
		return code * maxProbability / (CODE_SYMBOLS - 1);
	}

	/**
	 * 
	 * @return
	 */
	public double generateRate(final int code) {
		return code * maxRate / (CODE_SYMBOLS - 1);
	}

	/**
	 * 
	 * @param genotype
	 */
	public void generateSynthesisCode(final Genotype genotype) {
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
	public void generateSynthesisMoveCode(final Genotype genotype) {
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
	public double generateSynthesisRate(final int code) {
		return code * maxSynthesisRate / (CODE_SYMBOLS - 1);
	}

	/**
	 * 
	 * @param genotype
	 * @return
	 */
	public Genotype generateTestCode(final Genotype genotype) {
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
	 * @return the co2Level
	 */
	public double getCarbonDioxideLevel() {
		return carbonDioxideLevel;
	}

	/**
	 * @return the carbonDioxideSpread
	 */
	public double getCarbonDioxideSpread() {
		return carbonDioxideSpread;
	}

	/**
	 * @return the cloneEnergy
	 */
	public double getCloneEnergy() {
		return cloneEnergy;
	}

	/**
	 * @return the cols
	 */
	public int getCols() {
		return cols;
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
	 * @return the glucoseSpread
	 */
	public double getGlucoseSpread() {
		return glucoseSpread;
	}

	/**
	 * @return the moveEnergy
	 */
	public double getMoveEnergy() {
		return moveEnergy;
	}

	/**
	 * @return the mutationProbability
	 */
	public double getMutationProbability() {
		return mutationProbability;
	}

	/**
	 * @return the oxygenSpread
	 */
	public double getOxygenSpread() {
		return oxygenSpread;
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
	 * @return the rows
	 */
	public int getRows() {
		return rows;
	}

	/**
	 * @return the timeInterval
	 */
	public double getUpdateInterval() {
		return updateInterval;
	}

	/**
	 * @return the h2oLevel
	 */
	public double getWaterLevel() {
		return waterLevel;
	}

	/**
	 * @return the waterSpread
	 */
	public double getWaterSpread() {
		return waterSpread;
	}

	/**
	 * 
	 * @param probability
	 * @return
	 */
	public boolean hasChance(final double probability) {
		return random.nextDouble() < probability;
	}

	/**
	 * 
	 * @param time
	 * @return
	 */
	public boolean hasReaction(final double time) {
		final double pr = -Math.expm1(-time / reactionInterval);
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
	public int nextRandomInt(final double[] probabilities) {
		double tot = 0;
		for (final double v : probabilities)
			tot += v;
		final double p = random.nextDouble() * tot;
		tot = 0;
		final int n = probabilities.length - 1;
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
	public int nextRandomInt(final int n) {
		return random.nextInt(n);
	}

	/**
	 * @param breedEnergy
	 *            the breedEnergy to set
	 */
	public void setBreedEnergy(final double breedEnergy) {
		this.breedEnergy = breedEnergy;
	}

	/**
	 * @param co2Level
	 *            the co2Level to set
	 */
	public void setCarbonDioxideLevel(final double co2Level) {
		this.carbonDioxideLevel = co2Level;
	}

	/**
	 * @param carbonDioxideSpread
	 *            the carbonDioxideSpread to set
	 */
	public void setCarbonDioxideSpread(final double carbonDioxideSpread) {
		this.carbonDioxideSpread = carbonDioxideSpread;
	}

	/**
	 * @param cloneEnergy
	 *            the cloneEnergy to set
	 */
	public void setCloneEnergy(final double cloneEnergy) {
		this.cloneEnergy = cloneEnergy;
	}

	/**
	 * @param cols
	 *            the cols to set
	 */
	public void setCols(final int cols) {
		this.cols = cols;
	}

	/**
	 * @param energyLevel
	 *            the energyLevel to set
	 */
	public void setEnergyLevel(final double energyLevel) {
		this.energyLevel = energyLevel;
	}

	/**
	 * @param glucoseSpread
	 *            the glucoseSpread to set
	 */
	public void setGlucoseSpread(final double glucoseSpread) {
		this.glucoseSpread = glucoseSpread;
	}

	/**
	 * @param moveEnergy
	 *            the moveEnergy to set
	 */
	public void setMoveEnergy(final double moveEnergy) {
		this.moveEnergy = moveEnergy;
	}

	/**
	 * @param mutationProbability
	 *            the mutationProbability to set
	 */
	public void setMutationProbability(final double mutationProbability) {
		this.mutationProbability = mutationProbability;
	}

	/**
	 * @param oxygenSpread
	 *            the oxygenSpread to set
	 */
	public void setOxygenSpread(final double oxygenSpread) {
		this.oxygenSpread = oxygenSpread;
	}

	/**
	 * @param reactionInterval
	 *            the reactionInterval to set
	 */
	public void setReactionInterval(final double reactionInterval) {
		this.reactionInterval = reactionInterval;
	}

	/**
	 * @param rows
	 *            the rows to set
	 */
	public void setRows(final int rows) {
		this.rows = rows;
	}

	/**
	 * @param seed
	 * @see java.util.Random#setSeed(long)
	 */
	public void setSeed(final long seed) {
		random.setSeed(seed);
	}

	/**
	 * @param updateInterval
	 *            the timeInterval to set
	 */
	public void setUpdateInterval(final double updateInterval) {
		this.updateInterval = updateInterval;
	}

	/**
	 * @param h2oLevel
	 *            the h2oLevel to set
	 */
	public void setWaterLevel(final double h2oLevel) {
		this.waterLevel = h2oLevel;
	}

	/**
	 * @param waterSpread
	 *            the waterSpread to set
	 */
	public void setWaterSpread(final double waterSpread) {
		this.waterSpread = waterSpread;
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
