/**
 *
 */
package org.mmarini.genesis.swing;

import java.util.EventListener;

/**
 * @author US00852
 *
 */
public interface ChartDataListener extends EventListener {

    /**
     *
     * @param event
     */
    void dataChanged(ChartDataEvent event);
}
