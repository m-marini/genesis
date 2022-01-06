package org.mmarini.genesis.swing;

/**
 * The data model for charts is based on grid table. Each row contains sample
 * that are laying in horizontal axis. The columns represent different chart.
 * The first column represents the value of x axis. Each column has a label that
 * describes the mean of the values
 *
 * @author US00852
 */
public interface ChartData {

    /**
     * @param l
     */
    void addCharDataListner(ChartDataListener l);

    /**
     * @return
     */
    int getColumns();

    /**
     * @param col
     * @return
     */
    String getLabel(int col);

    /**
     * @return
     */
    int getRows();

    /**
     * @param x
     * @param y
     * @return
     */
    double getValue(int row, int col);

    /**
     * @param l
     */
    void removeCharDataListner(ChartDataListener l);
}
