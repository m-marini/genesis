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
     * @param background
     *            the background to set
     */
    public void setBackground(double background) {
        this.background = background;
    }

    /**
     * @return the foreground
     */
    public double getForeground() {
        return foreground;
    }

    /**
     * @param foreground
     *            the foreground to set
     */
    public void setForeground(double foreground) {
        this.foreground = foreground;
    }

    /**
     * @return the livingBean
     */
    public boolean isLivingBean() {
        return livingBean;
    }

    /**
     * @param livingBean
     *            the livingBean to set
     */
    public void setLivingBean(boolean livingBean) {
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
