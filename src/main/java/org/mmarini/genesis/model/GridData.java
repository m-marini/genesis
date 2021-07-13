/**
 * 
 */
package org.mmarini.genesis.model;

/**
 * @author US00852
 * 
 */
public class GridData {
	private double background;
	private boolean livingBean;
	private double foreground;

	/**
	 * 
	 */
	public GridData() {
	}

	/**
	 * @return the background
	 */
	public double getBackground() {
		return background;
	}

	/**
	 * @return the foreground
	 */
	public double getForeground() {
		return foreground;
	}

	/**
	 * @return the livingBean
	 */
	public boolean isLivingBean() {
		return livingBean;
	}

	/**
	 * @param background
	 *            the background to set
	 */
	public void setBackground(final double background) {
		this.background = background;
	}

	/**
	 * @param foreground
	 *            the foreground to set
	 */
	public void setForeground(final double foreground) {
		this.foreground = foreground;
	}

	/**
	 * @param livingBean
	 *            the livingBean to set
	 */
	public void setLivingBean(final boolean livingBean) {
		this.livingBean = livingBean;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "GridData [background=" + background + ", livingBean="
				+ livingBean + ", foreground=" + foreground + "]";
	}
}
