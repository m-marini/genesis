/**
 * 
 */
package org.mmarini.genesis.model;

import java.util.Arrays;

/**
 * The genotype is the genetic code and is used to create the phenotype.
 * <p>
 * The genotype can be cloned or breed with another genotype. During the cloning
 * or breeding a mutation of code can occurs with a specific probability. In
 * case of mutation the code is randomly changed
 * </p>
 * 
 * @author US00852
 * 
 */
public class Genotype {
	private static final int SYNTHESIS_CODE_LENGTH = 2 + 3;
	private static final int CLONE_CODE_LENGTH = 2 + 2 * 3;
	private static final int BREED_CODE_LENGTH = 2 + 2 * 3;
	private static final int ABSORBING_CODE_LENGTH = 2 + 1;
	private static final int ATTACK_CODE_LENGTH = 3 + 3 * 8;
	private static final int DEFENSE_CODE_LENGTH = 2 + 4;
	private static final int SYNTHESIS_MOVE_CODE_LENGTH = 2 + 4;
	private static final int GLUCOSE_MOVE_CODE_LENGTH = 2 + 4;

	public static final int SYNTHESIS_GENE_INDEX = 0;
	public static final int CLONE_GENE_INDEX = SYNTHESIS_GENE_INDEX
			+ SYNTHESIS_CODE_LENGTH;
	public static final int BREED_GENE_INDEX = CLONE_GENE_INDEX
			+ CLONE_CODE_LENGTH;
	public static final int ABSORBING_GENE_INDEX = BREED_GENE_INDEX
			+ BREED_CODE_LENGTH;
	public static final int ATTACK_GENE_INDEX = ABSORBING_GENE_INDEX
			+ ABSORBING_CODE_LENGTH;
	public static final int DEFENSE_GENE_INDEX = ATTACK_GENE_INDEX
			+ ATTACK_CODE_LENGTH;
	public static final int SYNTHESIS_MOVE_GENE_INDEX = DEFENSE_GENE_INDEX
			+ DEFENSE_CODE_LENGTH;
	public static final int GLUCOSE_MOVE_GENE_INDEX = SYNTHESIS_MOVE_GENE_INDEX
			+ SYNTHESIS_MOVE_CODE_LENGTH;

	private static final int CODE_LENGTH = SYNTHESIS_CODE_LENGTH
			+ CLONE_CODE_LENGTH + BREED_CODE_LENGTH + ABSORBING_CODE_LENGTH
			+ ATTACK_CODE_LENGTH + DEFENSE_CODE_LENGTH
			+ SYNTHESIS_MOVE_CODE_LENGTH + GLUCOSE_MOVE_CODE_LENGTH;

	private final int[] code;

	/**
	 * Create a genotype
	 */
	public Genotype() {
		code = new int[CODE_LENGTH];
	}

	/**
	 * Set the current genotype breeding two other genotypes
	 * 
	 * @param parent1
	 *            the first parent
	 * @param parent2
	 *            the second parent
	 * @param mutationProbability
	 *            the mutation probability
	 * @param parameters
	 *            the simulation parameters
	 */
	public void breed(final Genotype parent1, final Genotype parent2,
			final SimulationParameters parameters) {
		final double propability = parameters.getMutationProbability();
		for (int i = 0; i < CODE_LENGTH; ++i) {
			if (parameters.hasChance(propability)) {
				code[i] = parameters.generateCode();
			} else if (parameters.hasChance(0.5)) {
				code[i] = parent1.code[i];
			} else {
				code[i] = parent2.code[i];
			}
		}
	}

	/**
	 * Set the current genotype cloning another genotype
	 * 
	 * @param parent
	 *            the parent
	 * @param mutationProbability
	 *            the mutatino probability
	 * @param parameters
	 *            the simulatino parameters
	 */
	public void cloneCode(final Genotype parent,
			final SimulationParameters parameters) {
		final double mutationProbability = parameters.getMutationProbability();
		for (int i = 0; i < CODE_LENGTH; ++i) {
			if (parameters.hasChance(mutationProbability)) {
				code[i] = parameters.generateCode();
			} else {
				code[i] = parent.code[i];
			}
		}
	}

	/**
	 * Get the code at a specific index
	 * 
	 * @param index
	 *            the index
	 * @return the code
	 */
	public int getCode(final int index) {
		return code[index];
	}

	/**
	 * Sets the code at a specific index
	 * 
	 * @param index
	 *            the index
	 * @param value
	 *            the code
	 */
	public void setCode(final int index, final int value) {
		code[index] = value;
	}

	/**
	 * Copy from a genotype
	 * 
	 * @param genotype
	 *            the source genotype
	 */
	public void setGenotype(final Genotype genotype) {
		System.arraycopy(genotype.code, 0, code, 0, code.length);
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Genotype [code=" + Arrays.toString(code) + "]";
	}
}
