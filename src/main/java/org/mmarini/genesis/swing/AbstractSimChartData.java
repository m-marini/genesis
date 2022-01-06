/**
 *
 */
package org.mmarini.genesis.swing;

import org.mmarini.genesis.model.Snapshot;

import java.util.ArrayList;
import java.util.List;

/**
 * @author US00852
 *
 */
public abstract class AbstractSimChartData extends AbstractChartData {
    private static final int DEFAULT_MAX_ITEM_COUNT = 600;
    private static final int DEFAULT_CLEAN_ITEM_COUNT = 60;

    private final List<Snapshot> list;
    private final int maxItemCount;
    private final int cleanItemCount;

    /**
     *
     */
    protected AbstractSimChartData() {
        list = new ArrayList<Snapshot>();
        maxItemCount = DEFAULT_MAX_ITEM_COUNT;
        cleanItemCount = DEFAULT_CLEAN_ITEM_COUNT;
    }

    /**
     *
     * @param snapshot
     */
    public void add(Snapshot snapshot) {
        list.add(snapshot);
        if (list.size() > maxItemCount) {
            list.subList(0, cleanItemCount).clear();
        }
        fireDataChanged();

    }

    /**
     *
     */
    public void clear() {
        list.clear();
        fireDataChanged();
    }

    /**
     * @see org.mmarini.genesis.swing.ChartData#getRows()
     */
    @Override
    public int getRows() {
        return list.size();
    }

    /**
     * @see org.mmarini.genesis.swing.ChartData#getValue(int, int)
     */
    @Override
    public double getValue(int row, int col) {
        Snapshot rowData = list.get(row);
        return getVaule(rowData, col);
    }

    /**
     *
     * @param rowData
     * @param col
     * @return
     */
    protected abstract double getVaule(Snapshot rowData, int col);
}
