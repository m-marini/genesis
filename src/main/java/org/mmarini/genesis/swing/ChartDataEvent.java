/**
 *
 */
package org.mmarini.genesis.swing;

import java.util.EventObject;

/**
 * @author US00852
 *
 */
public class ChartDataEvent extends EventObject {
    private static final long serialVersionUID = -8507901752045842388L;
    private ChartData chartData;

    /**
     * @param source
     */
    public ChartDataEvent(Object source, ChartData chartData) {
        super(source);
        this.chartData = chartData;
    }

    /**
     *
     * @return
     */
    public ChartData getChartData() {
        return chartData;
    }

    /**
     *
     * @param chartData
     */
    public void setChartData(ChartData chartData) {
        this.chartData = chartData;
    }
}
