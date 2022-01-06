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
    private static final String[] LABELS = new String[]{
            Messages.getString("ChemicalChartData.time.label"), Messages.getString("ChemicalChartData.h2o.label"), Messages.getString("ChemicalChartData.c2o.label"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
            Messages.getString("ChemicalChartData.o2.label"), Messages.getString("ChemicalChartData.glucose.label"), Messages.getString("ChemicalChartData.livingBeingGlucose.label")}; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

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
