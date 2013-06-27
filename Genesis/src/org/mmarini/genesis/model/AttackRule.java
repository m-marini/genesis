/**
 * 
 */
package org.mmarini.genesis.model;

/**
 * @author US00852
 * 
 */
public class AttackRule {
	private double attackProbability;
	private double heavyProbability;
	private double attackEnergy;

	/**
	 * 
	 */
	public AttackRule() {
	}

	/**
	 * @return the attackEnergy
	 */
	public double getAttackEnergy() {
		return attackEnergy;
	}

	/**
	 * @return the attackProbability
	 */
	public double getAttackProbability() {
		return attackProbability;
	}

	/**
	 * @return the heavyProbability
	 */
	public double getHeavyProbability() {
		return heavyProbability;
	}

	/**
	 * @param attackEnergy
	 *            the attackEnergy to set
	 */
	public void setAttackEnergy(double attackEnergy) {
		this.attackEnergy = attackEnergy;
	}

	/**
	 * @param attackProbability
	 *            the attackProbability to set
	 */
	public void setAttackProbability(double attackProbability) {
		this.attackProbability = attackProbability;
	}

	/**
	 * @param heavyProbability
	 *            the heavyProbability to set
	 */
	public void setHeavyProbability(double heavyProbability) {
		this.heavyProbability = heavyProbability;
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "AttackRule [attackProbability=" + attackProbability
				+ ", heavyProbability=" + heavyProbability + ", attackEnergy="
				+ attackEnergy + "]";
	}

}
