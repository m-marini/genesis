/**
 * 
 */
package org.mmarini.genesis.swing;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.text.DecimalFormat;
import java.text.NumberFormat;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JFormattedTextField;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;

import org.mmarini.genesis.model.CellGetCommand;
import org.mmarini.genesis.model.SimulationHandler;
import org.mmarini.genesis.model.GridSnapshot;

/**
 * @author US00852
 * 
 */
public class MonitorPane extends JPanel {
	private static final int FIELD_COLUMNS = 8;

	private static final String FORMAT_PATTERN = "#,##0.00";

	private static final long serialVersionUID = 1424128424156001994L;

	private MonitorGrid grid;
	private JRadioButton oxygenButton;
	private JRadioButton waterButton;
	private JRadioButton carbonDioxideButton;
	private JRadioButton glucoseButton;
	private JCheckBox showLivingBeingButton;
	private AbstractAction showLivingBeingAction;
	private AbstractAction oxygenAction;
	private AbstractAction waterAction;
	private AbstractAction carbonDioxideAction;
	private AbstractAction glucoseAction;
	private ButtonGroup monitorSelection;
	private JFormattedTextField waterField;
	private JFormattedTextField oxygenField;
	private JFormattedTextField glucoseField;
	private JFormattedTextField carbonDioxideField;
	private JFormattedTextField refreshRateField;
	private JFormattedTextField updateRateField;
	private JFormattedTextField beingGlucoseField;
	private JFormattedTextField beingField;
	private JFormattedTextField synthesierField;
	private JFormattedTextField predatorField;
	private JFormattedTextField absorberField;

	private SimulationHandler handler;
	private GridSnapshot snapshot;
	private CellGetCommand command;

	/**
	 * 
	 */
	public MonitorPane() {
		snapshot = new GridSnapshot();
		waterField = new JFormattedTextField(new DecimalFormat(FORMAT_PATTERN));
		oxygenField = new JFormattedTextField(new DecimalFormat(FORMAT_PATTERN));
		glucoseField = new JFormattedTextField(
				new DecimalFormat(FORMAT_PATTERN));
		carbonDioxideField = new JFormattedTextField(new DecimalFormat(
				FORMAT_PATTERN));
		refreshRateField = new JFormattedTextField(new DecimalFormat(
				FORMAT_PATTERN));
		updateRateField = new JFormattedTextField(new DecimalFormat(
				FORMAT_PATTERN));
		beingGlucoseField = new JFormattedTextField(new DecimalFormat(
				FORMAT_PATTERN));
		beingField = new JFormattedTextField(NumberFormat.getIntegerInstance());
		absorberField = new JFormattedTextField(
				NumberFormat.getIntegerInstance());
		synthesierField = new JFormattedTextField(
				NumberFormat.getIntegerInstance());
		predatorField = new JFormattedTextField(
				NumberFormat.getIntegerInstance());
		monitorSelection = new ButtonGroup();
		oxygenButton = new JRadioButton();
		waterButton = new JRadioButton();
		carbonDioxideButton = new JRadioButton();
		glucoseButton = new JRadioButton();
		showLivingBeingButton = new JCheckBox();
		grid = new MonitorGrid();
		showLivingBeingAction = new AbstractAction() {

			/**
			 * 
			 */
			private static final long serialVersionUID = 7344936185806310085L;

			@Override
			public void actionPerformed(ActionEvent e) {
				grid.setShowLivingBeing(showLivingBeingButton.isSelected());
				grid.repaint();
			}
		};
		glucoseAction = new AbstractAction() {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				command = CellGetCommand.GLUCOSE_COMMAND;
				grid.repaint();
			}
		};
		waterAction = new AbstractAction() {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				command = CellGetCommand.WATER_COMMAND;
				grid.repaint();
			}
		};
		oxygenAction = new AbstractAction() {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				command = CellGetCommand.OXYGEN_COMMAND;
				grid.repaint();
			}
		};
		carbonDioxideAction = new AbstractAction() {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				command = CellGetCommand.CARBON_DIOXIDE_COMMAND;
				grid.repaint();
			}
		};

		waterField.setEditable(false);
		oxygenField.setEditable(false);
		carbonDioxideField.setEditable(false);
		glucoseField.setEditable(false);
		refreshRateField.setEditable(false);
		updateRateField.setEditable(false);
		beingField.setEditable(false);
		absorberField.setEditable(false);
		synthesierField.setEditable(false);
		predatorField.setEditable(false);
		beingGlucoseField.setEditable(false);

		waterField.setHorizontalAlignment(SwingConstants.RIGHT);
		oxygenField.setHorizontalAlignment(SwingConstants.RIGHT);
		carbonDioxideField.setHorizontalAlignment(SwingConstants.RIGHT);
		glucoseField.setHorizontalAlignment(SwingConstants.RIGHT);
		refreshRateField.setHorizontalAlignment(SwingConstants.RIGHT);
		updateRateField.setHorizontalAlignment(SwingConstants.RIGHT);
		beingField.setHorizontalAlignment(SwingConstants.RIGHT);
		absorberField.setHorizontalAlignment(SwingConstants.RIGHT);
		synthesierField.setHorizontalAlignment(SwingConstants.RIGHT);
		predatorField.setHorizontalAlignment(SwingConstants.RIGHT);
		beingGlucoseField.setHorizontalAlignment(SwingConstants.RIGHT);

		waterField.setColumns(FIELD_COLUMNS);
		oxygenField.setColumns(FIELD_COLUMNS);
		carbonDioxideField.setColumns(FIELD_COLUMNS);
		glucoseField.setColumns(FIELD_COLUMNS);
		refreshRateField.setColumns(FIELD_COLUMNS);
		updateRateField.setColumns(FIELD_COLUMNS);
		beingField.setColumns(FIELD_COLUMNS);
		absorberField.setColumns(FIELD_COLUMNS);
		synthesierField.setColumns(FIELD_COLUMNS);
		predatorField.setColumns(FIELD_COLUMNS);
		beingGlucoseField.setColumns(FIELD_COLUMNS);

		waterAction.putValue(Action.NAME, "Water");
		glucoseAction.putValue(Action.NAME, "Glucose");
		oxygenAction.putValue(Action.NAME, "Oxygen");
		carbonDioxideAction.putValue(Action.NAME, "Carbon Dioxide");
		showLivingBeingAction.putValue(Action.NAME, "Show Living Beings");

		carbonDioxideButton.setAction(carbonDioxideAction);
		waterButton.setAction(waterAction);
		oxygenButton.setAction(oxygenAction);
		glucoseButton.setAction(glucoseAction);
		showLivingBeingButton.setAction(showLivingBeingAction);

		monitorSelection.add(carbonDioxideButton);
		monitorSelection.add(oxygenButton);
		monitorSelection.add(waterButton);
		monitorSelection.add(glucoseButton);

		oxygenButton.setSelected(true);
		command = CellGetCommand.OXYGEN_COMMAND;
		showLivingBeingButton.setSelected(true);
		grid.setBorder(BorderFactory.createTitledBorder("Grid Map"));

		createContent();
	}

	/**
	 * 
	 * @param gbc
	 * @param c
	 */
	private void addComponentToGrid(GridBagConstraints gbc, Component c) {
		GridBagLayout gbl = (GridBagLayout) getLayout();
		gbl.setConstraints(c, gbc);
		add(c);
	}

	/**
	 * 
	 */
	private void createContent() {

		GridBagLayout gbl = new GridBagLayout();
		setLayout(gbl);
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridheight = GridBagConstraints.REMAINDER;
		gbc.anchor = GridBagConstraints.NORTHWEST;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.weightx = 1;
		gbc.weighty = 1;
		addComponentToGrid(gbc, grid);

		Box vb1 = Box.createVerticalBox();
		vb1.setBorder(BorderFactory.createTitledBorder("Grid Map Controls"));
		vb1.add(showLivingBeingButton);
		vb1.add(new JSeparator());
		vb1.add(waterButton);
		vb1.add(carbonDioxideButton);
		vb1.add(glucoseButton);
		vb1.add(oxygenButton);

		DataPanel pan = new DataPanel();
		pan.setBorder(BorderFactory.createTitledBorder("Info"));

		pan.addField("Water (mol)", waterField);
		pan.addField("Carbon Dioxide (mol)", carbonDioxideField);
		pan.addField("Glucose (mol)", glucoseField);
		pan.addField("Oxygen (mol)", oxygenField);
		pan.addField("# Syntesier", synthesierField);
		pan.addField("# Absorber", absorberField);
		pan.addField("# Predator", predatorField);
		pan.addField("# Total Living Being", beingField);
		pan.addField("Living Being Glucose (mol)", beingGlucoseField);
		pan.addField("Refresh Rate (fps)", refreshRateField);
		pan.addField("Update Rate (cps)", updateRateField);

		gbc.gridwidth = GridBagConstraints.REMAINDER;
		gbc.gridheight = 1;
		gbc.anchor = GridBagConstraints.NORTHWEST;
		gbc.fill = GridBagConstraints.NONE;
		gbc.weightx = 0;
		gbc.weighty = 0;

		addComponentToGrid(gbc, vb1);
		gbc.gridwidth = GridBagConstraints.REMAINDER;
		gbc.gridheight = 1;
		gbc.anchor = GridBagConstraints.NORTHWEST;
		gbc.fill = GridBagConstraints.NONE;
		gbc.weightx = 0;
		gbc.weighty = 0;
		addComponentToGrid(gbc, pan);
	}

	/**
	 * 
	 */
	public void refresh() {
		handler.retrieveSnapshot(snapshot, command);
		grid.setData(snapshot.getDataGrid());
		glucoseField.setValue(snapshot.getGlucose());
		waterField.setValue(snapshot.getWater());
		carbonDioxideField.setValue(snapshot.getCarbonDioxide());
		oxygenField.setValue(snapshot.getOxygen());
		synthesierField.setValue(snapshot.getSynthesizerCounter());
		absorberField.setValue(snapshot.getAbsorberCounter());
		predatorField.setValue(snapshot.getPredatorCounter());
		beingField.setValue(snapshot.getTotalBeingCount());
		beingGlucoseField.setValue(snapshot.getLivingBeingsGlucose());
	}

	/**
	 * @param handler
	 *            the handler to set
	 */
	public void setHandler(SimulationHandler handler) {
		this.handler = handler;
	}

	/**
	 * 
	 * @param d
	 */
	public void setRefreshRate(double refreshRate) {
		refreshRateField.setValue(refreshRate);
	}

	/**
	 * 
	 * @param d
	 */
	public void setUpdateRate(double refreshRate) {
		updateRateField.setValue(refreshRate);
	}

	/**
	 * @return the snapshot
	 */
	public GridSnapshot getSnapshot() {
		return snapshot;
	}
}
