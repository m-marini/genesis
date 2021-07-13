/**
 * 
 */
package org.mmarini.genesis.swing;

import java.util.ArrayList;
import java.util.List;

/**
 * @author US00852
 * 
 */
public abstract class AbstractChartData implements ChartData {

	private List<ChartDataListener> listeners;
	private final ChartDataEvent event;

	/**
	 * 
	 */
	protected AbstractChartData() {
		event = new ChartDataEvent(this, this);
	}

	/**
	 * @see org.mmarini.genesis.swing.ChartData#addCharDataListner(org.mmarini.genesis
	 *      .swing.ChartDataListener)
	 */
	@Override
	public void addCharDataListner(final ChartDataListener l) {
		List<ChartDataListener> ls = listeners;
		if (ls == null) {
			ls = new ArrayList<ChartDataListener>(1);
		} else if (ls.contains(l)) {
			return;
		} else {
			ls = new ArrayList<ChartDataListener>(ls);
		}
		ls.add(l);
		listeners = ls;
	}

	/**
	 * 
	 */
	protected void fireDataChanged() {
		final List<ChartDataListener> ls = listeners;
		if (ls == null)
			return;
		for (final ChartDataListener l : ls) {
			l.dataChanged(event);
		}
	}

	/**
	 * @see org.mmarini.genesis.swing.ChartData#removeCharDataListner(org.mmarini
	 *      .genesis.swing.ChartDataListener)
	 */
	@Override
	public void removeCharDataListner(final ChartDataListener l) {
		List<ChartDataListener> ls = listeners;
		if (ls == null || !ls.contains(l))
			return;
		ls = new ArrayList<ChartDataListener>(ls);
		ls.remove(l);
		listeners = ls;
	}
}
