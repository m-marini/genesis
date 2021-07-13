/**
 * 
 */
package org.mmarini.genesis.model;

/**
 * @author US00852
 * 
 */
public class CloneRule {
	private double probability;
	private double glucoseRate;

	/**
	 * 
	 */
	public CloneRule() {
	}

	/**
	 * @return the cloneEnergy
	 */
	public double getGlucoseRate() {
		return glucoseRate;
	}

	/**
	 * @return the probability
	 */
	public double getProbability() {
		return probability;
	}

	/**
	 * @param cloneEnergy
	 *            the cloneEnergy to set
	 */
	public void setGlucoseRate(final double cloneEnergy) {
		this.glucoseRate = cloneEnergy;
	}

	/**
	 * @param probability
	 *            the probability to set
	 */
	public void setProbability(final double probability) {
		this.probability = probability;
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "CloneRule [probability=" + probability + ", glucoseRate="
				+ glucoseRate + "]";
	}

}
