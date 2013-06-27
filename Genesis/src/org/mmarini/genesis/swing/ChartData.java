package org.mmarini.genesis.swing;

/**
 * 
 * The data model for charts is based on grid table. Each row contains sample
 * that are laying in horizontal axis. The columns represent different chart.
 * The first column represents the value of x axis. Each column has a label that
 * describes the mean of the values
 * 
 * @author US00852
 * 
 */
public interface ChartData {

	/**
	 * 
	 * @return
	 */
	public abstract int getColumns();

	/**
	 * 
	 * @param col
	 * @return
	 */
	public abstract String getLabel(int col);

	/**
	 * 
	 * @return
	 */
	public abstract int getRows();

	/**
	 * 
	 * @param x
	 * @param y
	 * @return
	 */
	public abstract double getValue(int row, int col);

	/**
	 * 
	 * @param l
	 */
	public abstract void addCharDataListner(ChartDataListener l);

	/**
	 * 
	 * @param l
	 */
	public abstract void removeCharDataListner(ChartDataListener l);
}
