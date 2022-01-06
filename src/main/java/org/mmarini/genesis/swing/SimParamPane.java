/**
 *
 */
package org.mmarini.genesis.swing;

import org.mmarini.genesis.model.SimulationParameters;

import javax.swing.*;

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
    /**
     *
     */
    private static final long serialVersionUID = -8561720210388593408L;
    private final SpinnerNumberModel numCols;
    private final SpinnerNumberModel numRows;
    private final SpinnerNumberModel carbonDioxideLevel;
    private final SpinnerNumberModel waterLevel;
    private final SpinnerNumberModel energyLevel;
    private final SpinnerNumberModel waterSpread;
    private final SpinnerNumberModel oxygenSpread;
    private final SpinnerNumberModel glucoseSpread;
    private final SpinnerNumberModel carbonDioxideSpread;
    private final SpinnerNumberModel mutationProbability;
    private final SpinnerNumberModel reactionInterval;
    private final SpinnerNumberModel updateInterval;
    private final SimulationParameters parameters;

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
        addField(
                Messages.getString("SimParamPane.columns.label"), new JSpinner(numCols)); //$NON-NLS-1$
        addField(
                Messages.getString("SimParamPane.rows.label"), new JSpinner(numRows)); //$NON-NLS-1$
        addField(
                Messages.getString("SimParamPane.waterLevel.label"), new JSpinner(waterLevel)); //$NON-NLS-1$
        addField(
                Messages.getString("SimParamPane.carbonDioxideLevel.label"), new JSpinner( //$NON-NLS-1$
                        carbonDioxideLevel));
        addField(
                Messages.getString("SimParamPane.energyLevel.label"), new JSpinner(energyLevel)); //$NON-NLS-1$
        addField(
                Messages.getString("SimParamPane.waterSpreadTime.label"), new JSpinner(waterSpread)); //$NON-NLS-1$
        addField(
                Messages.getString("SimParamPane.carbonDioxideSpreadTime.label"), new JSpinner(carbonDioxideSpread)); //$NON-NLS-1$
        addField(
                Messages.getString("SimParamPane.glucoseSpreadTime.label"), new JSpinner(glucoseSpread)); //$NON-NLS-1$
        addField(
                Messages.getString("SimParamPane.oxygenSpreadTime.label"), new JSpinner(oxygenSpread)); //$NON-NLS-1$
        addField(
                Messages.getString("SimParamPane.mutationProb.label"), new JSpinner(mutationProbability)); //$NON-NLS-1$
        addField(
                Messages.getString("SimParamPane.updateInterval.label"), new JSpinner(updateInterval)); //$NON-NLS-1$
        addField(
                Messages.getString("SimParamPane.reactionInterval.label"), new JSpinner(reactionInterval)); //$NON-NLS-1$
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
