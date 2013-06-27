/**
 * 
 */
package org.mmarini.genesis.swing;

import org.mmarini.genesis.model.Snapshot;

/**
 * @author US00852
 * 
 */
public class ChemicalChartData extends AbstractSimChartData {
	private static final String[] LABELS = new String[] { "Time", "H2O", "CO2",
			"O2", "Glucose", "Living Being Glucose" };

	/**
	 * 
	 */
	public ChemicalChartData() {
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
			return snapshot.getWater();
		case 2:
			return snapshot.getCarbonDioxide();
		case 3:
			return snapshot.getOxygen();
		case 4:
			return snapshot.getGlucose();
		case 5:
			return snapshot.getLivingBeingsGlucose();
		default:
			return 0;
		}
	}
}
