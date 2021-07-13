package org.mmarini.genesis.swing;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author US00852
 * 
 */
public class DefaultChartData extends AbstractChartData {
	private final List<String> labels;
	private final List<double[]> rows;

	/**
	 * 
	 */
	public DefaultChartData() {
		labels = new ArrayList<String>();
		rows = new ArrayList<double[]>();
	}

	/**
	 * 
	 * @param label
	 */
	public void addLabel(final String label) {
		labels.add(label);
		fireDataChanged();
	}

	/**
	 * 
	 * @param label
	 */
	public void addRow(final double[] row) {
		rows.add(row);
		fireDataChanged();
	}

	/**
	 * 
	 */
	public void clearAll() {
		clearData();
		clearLabels();
	}

	/**
	 * 
	 */
	public void clearData() {
		rows.clear();
		fireDataChanged();
	}

	/**
	 * 
	 */
	public void clearLabels() {
		labels.clear();
		fireDataChanged();
	}

	/**
	 * @see org.mmarini.genesis.swing.ChartData#getColumns()
	 */
	@Override
	public int getColumns() {
		if (rows.isEmpty())
			return 0;
		return rows.get(0).length;
	}

	/**
	 * @see org.mmarini.genesis.swing.ChartData#getLabel(int)
	 */
	@Override
	public String getLabel(final int col) {
		if (col >= 0 && col < labels.size())
			return labels.get(col);
		return "?"; //$NON-NLS-1$
	}

	/**
	 * @see org.mmarini.genesis.swing.ChartData#getRows()
	 */
	@Override
	public int getRows() {
		return rows.size();
	}

	/**
	 * @see org.mmarini.genesis.swing.ChartData#getValue(int, int)
	 */
	@Override
	public double getValue(final int row, final int col) {
		return rows.get(row)[col];
	}
}
