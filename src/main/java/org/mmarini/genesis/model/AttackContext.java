/**
 * 
 */
package org.mmarini.genesis.model;

/**
 * @author US00852
 * 
 */
public class AttackContext {
	private LivingBeing lightPrey;
	private LivingBeing heavyPrey;

	/**
	 * 
	 */
	public AttackContext() {
	}

	/**
	 * @return the headyPrey
	 */
	public LivingBeing getHeavyPrey() {
		return heavyPrey;
	}

	/**
	 * @return the lightPrey
	 */
	public LivingBeing getLightPrey() {
		return lightPrey;
	}

	/**
	 * @param headyPrey
	 *            the headyPrey to set
	 */
	public void setHeavyPrey(final LivingBeing headyPrey) {
		this.heavyPrey = headyPrey;
	}

	/**
	 * @param lightPrey
	 *            the lightPrey to set
	 */
	public void setLightPrey(final LivingBeing lightPrey) {
		this.lightPrey = lightPrey;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "AttackInfo [lightPrey=" + lightPrey + ", headyPrey="
				+ heavyPrey + "]";
	}

}
