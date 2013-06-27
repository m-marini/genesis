/**
 * 
 */
package org.mmarini.genesis.model;

/**
 * @author US00852
 * 
 */
public class MoveActionContext {
	private double preference;
	private Cell target;

	/**
	 * 
	 */
	public MoveActionContext() {
	}

	/**
	 * @return the preference
	 */
	public double getPreference() {
		return preference;
	}

	/**
	 * @return the target
	 */
	public Cell getTarget() {
		return target;
	}

	/**
	 * @param preference
	 *            the preference to set
	 */
	public void setPreference(double preference) {
		this.preference = preference;
	}

	/**
	 * @param target
	 *            the target to set
	 */
	public void setTarget(Cell target) {
		this.target = target;
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "MoveData [preference=" + preference + ", target=" + target
				+ "]";
	}
}
