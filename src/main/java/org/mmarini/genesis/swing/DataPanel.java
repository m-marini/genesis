/**
 *
 */
package org.mmarini.genesis.swing;

import javax.swing.*;
import java.awt.*;

/**
 * @author US00852
 *
 */
public class DataPanel extends JPanel {

    /**
     *
     */
    private static final long serialVersionUID = 1276978353788583752L;
    private final GridBagConstraints labelConstraints;
    private final GridBagConstraints fieldConstraints;

    /**
     *
     */
    public DataPanel() {
        setLayout(new GridBagLayout());
        labelConstraints = new GridBagConstraints();
        fieldConstraints = new GridBagConstraints();

        labelConstraints.anchor = GridBagConstraints.WEST;

        fieldConstraints.gridwidth = GridBagConstraints.REMAINDER;
        fieldConstraints.anchor = GridBagConstraints.WEST;
        fieldConstraints.fill = GridBagConstraints.BOTH;
    }

    /**
     *
     * @param label
     * @param component
     */
    public void addField(String label, Component component) {
        GridBagLayout layout = (GridBagLayout) getLayout();
        JLabel lab = new JLabel(label);
        layout.setConstraints(lab, labelConstraints);
        add(lab);
        layout.setConstraints(component, fieldConstraints);
        add(component);
    }

    /**
     * @return the fieldConstraint
     */
    public GridBagConstraints getFieldConstraints() {
        return fieldConstraints;
    }

    /**
     * @return the labelConstraint
     */
    public GridBagConstraints getLabelConstraints() {
        return labelConstraints;
    }
}
