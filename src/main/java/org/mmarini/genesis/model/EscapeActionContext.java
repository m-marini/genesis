/**
 * 
 */
package org.mmarini.genesis.model;

/**
 * @author US00852
 * 
 */
public class EscapeActionContext {
	private Cell target;
	private double risk;

	/**
	 * 
	 */
	public EscapeActionContext() {
	}

	/**
	 * @return the risk
	 */
	public double getRisk() {
		return risk;
	}

	/**
	 * @return the target
	 */
	public Cell getTarget() {
		return target;
	}

	/**
	 * @param risk
	 *            the risk to set
	 */
	public void setRisk(final double risk) {
		this.risk = risk;
	}

	/**
	 * @param target
	 *            the target to set
	 */
	public void setTarget(final Cell target) {
		this.target = target;
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "EscapeData [risk=" + risk + ", target=" + target + "]";
	}

}
