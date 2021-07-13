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
	public ChartDataEvent(final Object source, final ChartData chartData) {
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
	public void setChartData(final ChartData chartData) {
		this.chartData = chartData;
	}
}
