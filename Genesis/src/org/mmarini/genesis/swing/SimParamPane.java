/**
 * 
 */
package org.mmarini.genesis.swing;

import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

import org.mmarini.genesis.model.SimulationParameters;

/**
 * @author US00852
 * 
 */
public class SimParamPane extends DataPanel {
	private static final long DEFAULT_SEED = 1l;
	private static final double DEFAULT_UPDATE_INTERVAL = 30e-3;
	private static final double MIN_UPDATE_INTERVAL = 1e-3;
	private static final double MAX_UPDATE_INTERVAL = 1.;
	private static final double UPDATE_INTERVAL_STEP = 1e-3;

	private static final double DEFAULT_REACTION_INTERVAL = 500e-3;
	private static final double MIN_REACTION_INTERVAL = 50e-3;
	private static final double MAX_REACTION_INTERVAL = 2.;
	private static final double REACTION_INTERVAL_STEP = 50e-3;

	private static final double DEFAULT_ENERGY_LEVEL = 200.;
	private static final double MIN_ENERGY_LEVEL = 1.;
	private static final double MAX_ENERGY_LEVEL = 1000.;
	private static final double ENERGY_LEVEL_STEP = 1.;

	private static final double DEFAULT_WATER_LEVEL = 300.;
	private static final double MIN_WATER_LEVEL = 1.;
	private static final double MAX_WATER_LEVEL = 1000.;
	private static final double WATER_LEVEL_STEP = 1.;

	private static final double DEFAULT_CARBON_DIOXIDE_LEVEL = 300.;
	private static final double MIN_CARBON_DIOXIDE_LEVEL = 1.;
	private static final double MAX_CARBON_DIOXIDE_LEVEL = 1000.;
	private static final double CARBON_DIOXIDE_LEVEL_STEP = 1.;

	private static final double DEFAULT_MUTATION_PROBABILITY = 1e-2;
	private static final double MIN_MUTATION_PROBABILITY = 0.;
	private static final double MAX_MUTATION_PROBABILITY = 1.;
	private static final double MUTATION_STEP = 1e-3;

	private static final double GLUCOSE_SPREAD_TIME = 5.;
	private static final double WATER_SPREAD_TIME = 1.;
	private static final double GAS_SPREAD_TIME = 10e-3;

	private static final double MIN_SPREAD_TIME = 10e-3;
	private static final double MAX_SPREAD_TIME = 10.;
	private static final double SPREAD_TIME_STEP = 10e-3;

	private SpinnerNumberModel numCols;
	private SpinnerNumberModel numRows;
	private SpinnerNumberModel carbonDioxideLevel;
	private SpinnerNumberModel waterLevel;
	private SpinnerNumberModel energyLevel;
	private SpinnerNumberModel waterSpread;
	private SpinnerNumberModel oxygenSpread;
	private SpinnerNumberModel glucoseSpread;
	private SpinnerNumberModel carbonDioxideSpread;
	private SpinnerNumberModel mutationProbability;
	private SpinnerNumberModel reactionInterval;
	private SpinnerNumberModel updateInterval;
	private SimulationParameters parameters;

	/**
	 * 
	 */
	private static final long serialVersionUID = -8561720210388593408L;

	/**
	 * 
	 */
	public SimParamPane() {
		numCols = new SpinnerNumberModel(30, 3, 50, 1);
		numRows = new SpinnerNumberModel(30, 3, 50, 1);

		carbonDioxideLevel = new SpinnerNumberModel(
				DEFAULT_CARBON_DIOXIDE_LEVEL, MIN_CARBON_DIOXIDE_LEVEL,
				MAX_CARBON_DIOXIDE_LEVEL, CARBON_DIOXIDE_LEVEL_STEP);
		waterLevel = new SpinnerNumberModel(DEFAULT_WATER_LEVEL,
				MIN_WATER_LEVEL, MAX_WATER_LEVEL, WATER_LEVEL_STEP);
		energyLevel = new SpinnerNumberModel(DEFAULT_ENERGY_LEVEL,
				MIN_ENERGY_LEVEL, MAX_ENERGY_LEVEL, ENERGY_LEVEL_STEP);

		waterSpread = new SpinnerNumberModel(WATER_SPREAD_TIME,
				MIN_SPREAD_TIME, MAX_SPREAD_TIME, SPREAD_TIME_STEP);
		oxygenSpread = new SpinnerNumberModel(GAS_SPREAD_TIME, MIN_SPREAD_TIME,
				MAX_SPREAD_TIME, MIN_SPREAD_TIME);
		carbonDioxideSpread = new SpinnerNumberModel(GAS_SPREAD_TIME,
				MIN_SPREAD_TIME, MAX_SPREAD_TIME, SPREAD_TIME_STEP);
		glucoseSpread = new SpinnerNumberModel(GLUCOSE_SPREAD_TIME,
				MIN_SPREAD_TIME, MAX_SPREAD_TIME, SPREAD_TIME_STEP);

		mutationProbability = new SpinnerNumberModel(
				DEFAULT_MUTATION_PROBABILITY, MIN_MUTATION_PROBABILITY,
				MAX_MUTATION_PROBABILITY, MUTATION_STEP);

		reactionInterval = new SpinnerNumberModel(DEFAULT_REACTION_INTERVAL,
				MIN_REACTION_INTERVAL, MAX_REACTION_INTERVAL,
				REACTION_INTERVAL_STEP);

		updateInterval = new SpinnerNumberModel(DEFAULT_UPDATE_INTERVAL,
				MIN_UPDATE_INTERVAL, MAX_UPDATE_INTERVAL, UPDATE_INTERVAL_STEP);

		parameters = new SimulationParameters();
		createContent();
	}

	/**
	 * 
	 */
	private void createContent() {
		addField("Columns", new JSpinner(numCols));
		addField("Rows", new JSpinner(numRows));
		addField("Water Level(mol/cell)", new JSpinner(waterLevel));
		addField("Carbon Dioxide Level (mol/cell)", new JSpinner(
				carbonDioxideLevel));
		addField("Energy level", new JSpinner(energyLevel));
		addField("Water Spread (s)", new JSpinner(waterSpread));
		addField("Carbon Dioxide Spread (s)", new JSpinner(carbonDioxideSpread));
		addField("Glucose Spread (s)", new JSpinner(glucoseSpread));
		addField("Oxygen Spread (s)", new JSpinner(oxygenSpread));
		addField("Mutation probability", new JSpinner(mutationProbability));
		addField("Update Interval (s.)", new JSpinner(updateInterval));
		addField("Reaction Interval(s.)", new JSpinner(reactionInterval));
	}

	/**
	 * 
	 * @return
	 */
	public SimulationParameters getParameters() {
		parameters.setCols(numCols.getNumber().intValue());
		parameters.setRows(numRows.getNumber().intValue());

		parameters.setCarbonDioxideLevel(carbonDioxideLevel.getNumber()
				.doubleValue());
		parameters.setWaterLevel(waterLevel.getNumber().doubleValue());
		parameters.setEnergyLevel(energyLevel.getNumber().doubleValue());

		parameters.setCarbonDioxideSpread(carbonDioxideSpread.getNumber()
				.doubleValue());
		parameters.setWaterSpread(waterSpread.getNumber().doubleValue());
		parameters.setOxygenSpread(oxygenSpread.getNumber().doubleValue());
		parameters.setGlucoseSpread(glucoseSpread.getNumber().doubleValue());

		parameters.setMutationProbability(mutationProbability.getNumber()
				.doubleValue());

		parameters.setReactionInterval(reactionInterval.getNumber()
				.doubleValue());
		parameters.setUpdateInterval(updateInterval.getNumber().doubleValue());
		parameters.setSeed(DEFAULT_SEED);
		return parameters;
	}
}
