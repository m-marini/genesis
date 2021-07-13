/**
 * 
 */
package org.mmarini.genesis.swing;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.text.DecimalFormat;
import java.text.NumberFormat;

import javax.swing.JComponent;

/**
 * @author US00852
 * 
 */
public class Chart extends JComponent {
	private static final int LABEL_TICK_HGAP = 2;
	private static final int LABEL_TICK_VGAP = 2;
	private static final int MAJOR_TICK_LENGTH = 15;
	private static final int MEDIUM_TICK_LENGTH = 10;
	private static final int MINOR_TICK_LENGTH = 5;
	private static final int MIN_GRID_COUNT = 3;

	private static final long serialVersionUID = 6128025266589737076L;

	private ChartData table;
	private Rectangle componentArea;
	private Rectangle chartArea;
	private Rectangle labelsArea;
	private Rectangle axisArea;
	private Insets labelsInsets;
	private Insets chartInsets;
	private Color[] chartColor;
	private double scaleX;
	private double scaleY;
	private double baselineX;
	private double maximumX;
	private double baselineY;
	private double maximumY;
	private double stepY;
	private double stepX;
	private Color axisColor;
	private Color minorTickColor;
	private Color majorTickColor;
	private int lineWidth;
	private String labelPattern;
	private ChartDataListener listener;

	/**
	 * 
	 */
	public Chart() {
		componentArea = new Rectangle();
		chartArea = new Rectangle();
		labelsArea = new Rectangle();
		axisArea = new Rectangle();
		setBackground(Color.WHITE);
		labelsInsets = new Insets(15, 15, 15, 15);
		chartInsets = new Insets(5, 5, 5, 0);
		minorTickColor = new Color(224, 224, 224);
		majorTickColor = Color.GRAY;
		axisColor = Color.BLACK;
		chartColor = new Color[] { Color.BLUE, Color.RED, Color.GREEN,
				Color.ORANGE, Color.CYAN, Color.MAGENTA, Color.PINK,
				Color.YELLOW };
		lineWidth = 2;
		labelPattern = "#,##0.#########"; //$NON-NLS-1$
		listener = new ChartDataListener() {

			@Override
			public void dataChanged(ChartDataEvent event) {
				update();
			}
		};
	}

	/**
	 * 
	 * @param gr
	 */
	private void computeAreas(Graphics2D gr) {
		int width = 0;
		FontMetrics fm = gr.getFontMetrics();
		if (table != null) {
			int cols = table.getColumns();
			for (int i = 1; i < cols; ++i) {
				width = Math.max(fm.stringWidth(table.getLabel(i)), width);
			}
		}
		labelsArea.setFrame(componentArea);
		labelsArea.width = width + 15;
		labelsArea.height -= labelsInsets.top + labelsInsets.bottom;
		labelsArea.x += componentArea.width - labelsArea.width
				- labelsInsets.right;
		labelsArea.y += labelsInsets.top;

		NumberFormat labelFormat = new DecimalFormat(labelPattern);
		width = fm.stringWidth(labelFormat.format(baselineX)) / 2
				- MAJOR_TICK_LENGTH - LABEL_TICK_HGAP;
		for (double y = baselineY; y < maximumY; y += stepY) {
			String txt = labelFormat.format(y);
			width = Math.max(fm.stringWidth(txt), width);
		}
		String txt = labelFormat.format(maximumY);
		width = Math.max(fm.stringWidth(txt), width);

		int w1 = fm.stringWidth(labelFormat.format(maximumX));

		axisArea.setFrame(componentArea);
		axisArea.width -= labelsArea.width + labelsInsets.right
				+ labelsInsets.left;

		chartArea.setFrame(axisArea);
		chartArea.width -= width + MAJOR_TICK_LENGTH + LABEL_TICK_HGAP
				+ chartInsets.left + w1 / 2 + chartInsets.right;
		chartArea.x += width + MAJOR_TICK_LENGTH + LABEL_TICK_HGAP
				+ chartInsets.left;
		chartArea.height -= fm.getHeight() * 2 + MAJOR_TICK_LENGTH
				+ LABEL_TICK_VGAP + chartInsets.bottom + chartInsets.top
				+ fm.getAscent();
		chartArea.y += chartInsets.top + fm.getAscent();

		scaleX = chartArea.width / (maximumX - baselineX);
		scaleY = chartArea.height / (maximumY - baselineY);
	}

	private void computeComponentArea() {
		Dimension size = getSize();
		Insets insets = getInsets();
		if (insets == null) {
			componentArea.setFrame(0, 0, size.width, size.height);
		} else {
			componentArea.setFrame(insets.left, insets.top, size.width
					- insets.left - insets.right, size.height - insets.top
					- insets.bottom);
		}
	}

	/**
	 * 
	 * @param point
	 * @param x
	 * @param y
	 */
	private void computePoint(Point point, double x, double y) {
		point.x = (int) Math.round((x - baselineX) * scaleX);
		point.y = chartArea.height - (int) Math.round((y - baselineY) * scaleY);
	}

	/**
	 * 
	 * @param point
	 * @param row
	 * @param col
	 */
	private void computePoint(Point point, int row, int col) {
		computePoint(point, table.getValue(row, 0), table.getValue(row, col));
	}

	/**
	 * 
	 * @param baseline
	 * @param maximum
	 * @return
	 */
	private double computeStep(double baseline, double maximum) {
		double range = maximum - baseline;
		if (range > 0)
			return Math.pow(10, Math.ceil(Math.log10(range)) - 1);
		if (Math.abs(baseline) > 0)
			return Math.pow(10, Math.ceil(Math.log10(baseline)) - 1);
		return 0.1;
	}

	/**
	 * 
	 * @param gr
	 */
	private void paintAxis(Graphics2D gr) {
		gr = (Graphics2D) gr.create();
		gr.translate(chartArea.x, chartArea.y);
		Point p0 = new Point();
		FontMetrics fm = gr.getFontMetrics();
		gr.setColor(Color.BLACK);
		int n = (int) Math.round((maximumX - baselineX) / stepX);
		int m = (int) Math.round((maximumY - baselineY) / stepY);
		NumberFormat labelFormat = new DecimalFormat(labelPattern);
		for (int i = 0; i <= n; ++i) {
			int tick = i % 10;
			double x = baselineX + i * stepX;
			computePoint(p0, x, baselineY);
			if (tick == 0)
				gr.drawLine(p0.x, p0.y + MAJOR_TICK_LENGTH, p0.x, p0.y);
			else if (tick == 5)
				gr.drawLine(p0.x, p0.y + MEDIUM_TICK_LENGTH, p0.x, p0.y);
			else
				gr.drawLine(p0.x, p0.y + MINOR_TICK_LENGTH, p0.x, p0.y);
			if (tick == 0) {
				String txt = labelFormat.format(x);
				int w = fm.stringWidth(txt);
				gr.drawString(txt, p0.x - w / 2, p0.y + MAJOR_TICK_LENGTH
						+ LABEL_TICK_VGAP + fm.getAscent());
			}
		}

		for (int i = 0; i <= m; ++i) {
			int tick = i % 10;
			double y = baselineY + i * stepY;
			computePoint(p0, baselineX, y);
			if (y == 0)
				gr.setColor(Color.BLACK);
			else
				gr.setColor(Color.GRAY);
			if (tick == 0)
				gr.drawLine(p0.x - MAJOR_TICK_LENGTH, p0.y, p0.x, p0.y);
			else if (tick == 5)
				gr.drawLine(p0.x - MEDIUM_TICK_LENGTH, p0.y, p0.x, p0.y);
			else
				gr.drawLine(p0.x - MINOR_TICK_LENGTH, p0.y, p0.x, p0.y);
			if (tick == 0) {
				String txt = labelFormat.format(y);
				int w = fm.stringWidth(txt);
				gr.setColor(Color.BLACK);
				gr.drawString(txt, p0.x - MAJOR_TICK_LENGTH - LABEL_TICK_HGAP
						- w, p0.y);
			}
		}
		String txt = table.getLabel(0);
		gr.drawString(txt, (chartArea.width - fm.stringWidth(txt)) / 2,
				chartArea.height + fm.getHeight() + fm.getAscent()
						+ MAJOR_TICK_LENGTH + LABEL_TICK_VGAP);
	}

	/**
	 * 
	 * @param gr
	 */
	private void paintCharts(Graphics2D gr) {
		// gr = (Graphics2D) gr.create(chartArea.x, chartArea.y,
		// chartArea.width,
		// chartArea.height);
		gr = (Graphics2D) gr.create();
		gr.translate(chartArea.x, chartArea.y);
		int cols = table.getColumns();
		Point p0 = new Point();
		Point p1 = new Point();
		int rows = table.getRows();
		gr.setStroke(new BasicStroke(lineWidth));
		for (int col = 1; col < cols; ++col) {
			computePoint(p0, 0, col);
			gr.setColor(chartColor[col - 1]);
			for (int row = 1; row < rows; ++row) {
				computePoint(p1, row, col);
				gr.drawLine(p0.x, p0.y, p1.x, p1.y);
				p0.setLocation(p1);
			}
		}
	}

	/**
	 * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
	 */
	@Override
	protected void paintComponent(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
		computeComponentArea();
		g2.setColor(getBackground());
		g2.fill(componentArea);
		if (table != null && table.getColumns() >= 2 && table.getRows() >= 2) {
			computeAreas(g2);
			paintGrid(g2);
			paintAxis(g2);
			paintLabels(g2);
			paintCharts(g2);
		}
	}

	/**
	 * 
	 * @param gr
	 */
	private void paintGrid(Graphics2D gr) {
		gr = (Graphics2D) gr.create();
		gr.translate(chartArea.x, chartArea.y);
		paintGrid(gr, 1, minorTickColor);
		paintGrid(gr, 5, majorTickColor);
	}

	/**
	 * 
	 * @param gr
	 * @param step
	 * @param tickColor
	 */
	private void paintGrid(Graphics2D gr, int step, Color tickColor) {
		Point p0 = new Point();
		Point p1 = new Point();
		int n = (int) Math.round((maximumX - baselineX) / stepX);
		int m = (int) Math.round((maximumY - baselineY) / stepY);
		for (int i = 0; i <= n; i += step) {
			double x = baselineX + i * stepX;
			computePoint(p0, x, baselineY);
			computePoint(p1, x, maximumY);
			if (Math.abs(x) < stepX / 2)
				gr.setColor(axisColor);
			else
				gr.setColor(tickColor);
			gr.drawLine(p0.x, p0.y, p1.x, p1.y);
		}
		for (int i = 0; i <= m; i += step) {
			double y = baselineY + i * stepY;
			computePoint(p0, baselineX, y);
			computePoint(p1, maximumX, y);
			if (Math.abs(y) < stepY / 2)
				gr.setColor(axisColor);
			else
				gr.setColor(tickColor);
			gr.drawLine(p0.x, p0.y, p1.x, p1.y);
		}
	}

	/**
	 * 
	 * @param gr
	 */
	private void paintLabels(Graphics2D gr) {
		gr = (Graphics2D) gr.create(labelsArea.x, labelsArea.y,
				labelsArea.width, labelsArea.height);
		int cols = table.getColumns();
		FontMetrics fm = gr.getFontMetrics();
		int fh = fm.getHeight();
		int y = fm.getAscent();
		for (int i = 1; i < cols; ++i) {
			gr.setColor(chartColor[i - 1]);
			gr.fillRect(0, y - 10, 10, 10);
			gr.setColor(getForeground());
			gr.drawRect(0, y - 10, 10, 10);
			gr.drawString(table.getLabel(i), 15, y);
			y += fh;
		}
	}

	/**
	 * @param table
	 *            the table to set
	 */
	public void setTable(ChartData table) {
		if (this.table != null)
			table.removeCharDataListner(listener);
		this.table = table;
		if (this.table != null)
			table.addCharDataListner(listener);
		update();
	}

	/**
	 * 
	 */
	private void update() {
		if (table == null)
			return;
		int rowCount = table.getRows();
		int colCount = table.getColumns();
		baselineX = baselineY = Double.POSITIVE_INFINITY;
		maximumX = maximumY = Double.NEGATIVE_INFINITY;
		for (int i = 0; i < rowCount; ++i) {
			double value = table.getValue(i, 0);
			baselineX = Math.min(value, baselineX);
			maximumX = Math.max(value, maximumX);
			for (int j = 1; j < colCount; ++j) {
				value = table.getValue(i, j);
				baselineY = Math.min(value, baselineY);
				maximumY = Math.max(value, maximumY);
			}
		}
		stepX = computeStep(baselineX, maximumX);
		baselineX = Math.floor(baselineX / stepX) * stepX;
		maximumX = Math.ceil(maximumX / stepX) * stepX;
		stepX /= 10;

		stepY = computeStep(baselineY, maximumY);
		double b = Math.floor(baselineY / stepY) * stepY;
		double m = Math.ceil(maximumY / stepY) * stepY;
		if ((m - b) / stepY >= MIN_GRID_COUNT) {
			baselineY = b;
			maximumY = m;
		} else {
			stepY /= 10;
			baselineY = Math.floor(baselineY / stepY) * stepY;
			maximumY = Math.ceil(maximumY / stepY) * stepY;
		}
		stepY /= 10;

		repaint();
	}
}
