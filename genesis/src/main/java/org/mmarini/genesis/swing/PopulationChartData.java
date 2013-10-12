/**
 * 
 */
package org.mmarini.genesis.swing;

import org.mmarini.genesis.model.Snapshot;

/**
 * @author US00852
 * 
 */
public class PopulationChartData extends AbstractSimChartData {
	private static final String[] LABELS = new String[] { "Time", "Total",
			"Synthesiser", "Absorber", "Predator" };

	/**
	 * 
	 */
	public PopulationChartData() {
	}

	/**
	 * @see org.mmarini.genesis.swing.ChartData#getColumns()
	 */
	@Override
	public int getColumns() {
		return LABELS.length;
	}

	/**
	 * @see org.mmarini.genesis.swing.ChartData#getLabel(int)
	 */
	@Override
	public String getLabel(int col) {
		return LABELS[col];
	}

	/**
	 * @see org.mmarini.genesis.swing.AbstractSimChartData#getVaule(org.mmarini.genesis
	 *      .model.Snapshot, int)
	 */
	@Override
	protected double getVaule(Snapshot snapshot, int col) {
		switch (col) {
		case 0:
			return snapshot.getTime();
		case 1:
			return snapshot.getTotalBeingCount();
		case 2:
			return snapshot.getSynthesizerCounter();
		case 3:
			return snapshot.getAbsorberCounter();
		case 4:
			return snapshot.getPredatorCounter();
		default:
			return 0;
		}
	}
}
