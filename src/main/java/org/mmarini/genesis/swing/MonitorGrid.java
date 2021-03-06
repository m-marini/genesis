/**
 *
 */
package org.mmarini.genesis.swing;

import org.mmarini.genesis.model.GridData;

import javax.swing.*;
import java.awt.*;

/**
 * @author US00852
 *
 */
public class MonitorGrid extends JComponent {
    private static final int VERTEX_COUNT = 6;
    private static final long serialVersionUID = -8626872860003937592L;

    private final int[][] poly;
    private final Dimension inbound;
    private boolean showLivingBeing;
    private GridData[][] data;
    private double foregroundColorScale;
    private double backgroundColorScale;

    /**
     *
     */
    public MonitorGrid() {
        poly = new int[2][VERTEX_COUNT];
        inbound = new Dimension();
        showLivingBeing = true;
        setDoubleBuffered(true);
    }

    /**
     *
     * @param corner
     * @param xx
     * @param yy
     * @param xtick
     * @param ytick
     */
    private void computeCorner(Point corner, int xx, int yy, double xtick,
                               double ytick) {
        int x = xx * 2;
        boolean odd = (yy % 2) == 1;
        if (odd)
            ++x;
        x = x * 2 + 1;
        corner.y = yy * 3 + 1;
        corner.x = (int) Math.round(x * xtick / 2);
        corner.y = (int) Math.round(corner.y * ytick);
    }

    /**
     *
     * @param x
     * @return
     */
    private Color createColor(double x) {
        float h = (float) (0.8 * (1 - x));
        float b = (float) (0.7 * x + 0.3);
        return Color.getHSBColor(h, 1f, b);
    }

    /**
     *
     * @param value
     * @param min
     * @param max
     * @return
     */
    private Color createColor(double value, double min, double max) {
        if (value < min)
            value = min;
        if (value > max)
            value = max;
        double x = (value - min) / (max - min);
        return createColor(x);
    }

    /**
     * @return the showLivingBeing
     */
    public boolean isShowLivingBeing() {
        return showLivingBeing;
    }

    /**
     * @param showLivingBeing
     *            the showLivingBeing to set
     */
    public void setShowLivingBeing(boolean showLivingBeing) {
        this.showLivingBeing = showLivingBeing;
    }

    /**
     * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
     */
    @Override
    protected void paintComponent(Graphics g) {
        Dimension size = getSize();
        if (data == null)
            return;
        Insets insets = getInsets();
        if (insets == null) {
            inbound.setSize(size);
        } else {
            int x = insets.left;
            int y = insets.top;
            int w = size.width - insets.left - insets.right;
            int h = size.height - insets.top - insets.bottom;
            g = g.create(x, y, w, h);
            inbound.setSize(w, h);
        }
        size = inbound.getSize();
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, size.width, size.height);
        paintGrid(g);
    }

    /**
     *
     * @param g
     */
    private void paintGrid(Graphics g) {
        int rows = data.length;
        int cols = data[0].length;

        int w = inbound.width;
        int h = inbound.height;

        double xtick = (double) w / (cols * 2 + 1);
        double ytick = (double) h / (rows * 3 + 1);

        Point corner = new Point();
        int ow = (int) Math.round(xtick);
        int oh = (int) Math.round(ytick * 2);

        boolean paintGrid = xtick >= 5 && ytick >= 3;

        int i0 = 0;
        for (int i = 0; i < rows; i += 2) {
            poly[1][0] = (int) Math.round(i0 * ytick);
            poly[1][1] = poly[1][5] = (int) Math.round((i0 + 1) * ytick);
            poly[1][2] = poly[1][4] = (int) Math.round((i0 + 3) * ytick);
            poly[1][3] = (int) Math.round((i0 + 4) * ytick);

            int j0 = 0;
            for (int j = 0; j < cols; ++j) {
                poly[0][0] = poly[0][3] = (int) Math.round((j0 + 1) * xtick);
                poly[0][1] = poly[0][2] = (int) Math.round((j0 + 2) * xtick);
                poly[0][4] = poly[0][5] = (int) Math.round(j0 * xtick);
                GridData dt = data[i][j];
                Color c = createColor(dt.getBackground(), 0,
                        backgroundColorScale);
                g.setColor(c);
                g.fillPolygon(poly[0], poly[1], VERTEX_COUNT);
                if (paintGrid) {
                    g.setColor(Color.GRAY);
                    g.drawPolygon(poly[0], poly[1], VERTEX_COUNT);
                }
                if (showLivingBeing && dt.isLivingBean()) {
                    computeCorner(corner, j, i, xtick, ytick);
                    c = createColor(dt.getForeground(), 0, foregroundColorScale);
                    g.setColor(c);
                    g.fillOval(corner.x, corner.y, ow, oh);
                    if (paintGrid) {
                        g.setColor(Color.GRAY);
                        g.drawOval(corner.x, corner.y, ow, oh);
                    }
                }
                j0 += 2;
            }
            i0 += 6;
        }

        i0 = 3;
        for (int i = 1; i < rows; i += 2) {
            poly[1][0] = (int) Math.round(i0 * ytick);
            poly[1][1] = poly[1][5] = (int) Math.round((i0 + 1) * ytick);
            poly[1][2] = poly[1][4] = (int) Math.round((i0 + 3) * ytick);
            poly[1][3] = (int) Math.round((i0 + 4) * ytick);

            int j0 = 1;
            for (int j = 0; j < cols; ++j) {
                poly[0][0] = poly[0][3] = (int) Math.round((j0 + 1) * xtick);
                poly[0][1] = poly[0][2] = (int) Math.round((j0 + 2) * xtick);
                poly[0][4] = poly[0][5] = (int) Math.round(j0 * xtick);

                GridData dt = data[i][j];
                Color c = createColor(dt.getBackground(), 0,
                        backgroundColorScale);
                g.setColor(c);
                g.fillPolygon(poly[0], poly[1], VERTEX_COUNT);

                if (paintGrid) {
                    g.setColor(Color.GRAY);
                    g.drawPolygon(poly[0], poly[1], VERTEX_COUNT);
                }

                if (showLivingBeing && dt.isLivingBean()) {
                    computeCorner(corner, j, i, xtick, ytick);
                    c = createColor(dt.getForeground(), 0, foregroundColorScale);
                    g.setColor(c);
                    g.fillOval(corner.x, corner.y, ow, oh);
                    if (paintGrid) {
                        g.setColor(Color.GRAY);
                        g.drawOval(corner.x, corner.y, ow, oh);
                    }
                }
                j0 += 2;
            }
            i0 += 6;
        }
    }

    /**
     *
     */
    private void refresh() {
        // Compute the color scales
        double totFg = 0;
        double totBg = 0;
        int ctFg = 0;
        for (GridData[] row : data) {
            for (GridData dt : row) {
                if (dt.isLivingBean()) {
                    ++ctFg;
                    totFg += dt.getForeground();
                }
                totBg += dt.getBackground();
            }
        }
        if (ctFg > 0)
            foregroundColorScale = totFg * 2 / ctFg;
        else
            foregroundColorScale = 1;
        if (totBg == 0)
            backgroundColorScale = 1;
        else
            backgroundColorScale = 2 * totBg / data.length / data[0].length;
        repaint();
    }

    /**
     * @param data
     *            the data to set
     */
    public void setData(GridData[][] data) {
        this.data = data;
        refresh();
    }
}
