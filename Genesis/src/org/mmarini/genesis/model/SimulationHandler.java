/**
 * 
 */
package org.mmarini.genesis.model;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * The simulator handler manages a session of simulation.
 * <p>
 * The handler maintains a grid of cell containing the status of chemical
 * component and eventually the living being. The simulation consists of
 * continuous update the status at a specific time interval.
 * </p>
 * 
 * @author US00852
 * 
 */
public class SimulationHandler implements SimulationConstants {
	private static Log log = LogFactory.getLog(SimulationHandler.class);
	private SimulationParameters parameters;
	private Point[][] neighbours;
	private Cell[][] grid;
	private List<LivingBeing> list;
	private double time;

	/**
	 * Create the handler
	 */
	public SimulationHandler() {
		neighbours = new Point[2][6];
		list = new ArrayList<LivingBeing>();

		/**
		 * <pre>
		 * Even
		 *     / \ / \ 
		 *    |-,-|-,0|
		 *   / \ / \ / \
		 *  |0,-|0,0|0,+| 
		 *   \ / \ / \ /
		 *    |+,-|+,0|
		 *     \ / \ /
		 * Odd
		 *     / \ / \ 
		 *    |-,0|-,+|
		 *   / \ / \ / \
		 *  |0,-|0,0|0,+| 
		 *   \ / \ / \ /
		 *    |+,0|+,+|
		 *     \ / \ /
		 * </pre>
		 */
		neighbours[0][0] = neighbours[1][0] = new Point(1, 0);
		neighbours[0][1] = new Point(0, 1);
		neighbours[0][2] = new Point(-1, 1);
		neighbours[0][3] = neighbours[1][3] = new Point(-1, 0);
		neighbours[0][4] = new Point(-1, -1);
		neighbours[0][5] = new Point(0, -1);
		neighbours[1][1] = new Point(1, 1);
		neighbours[1][2] = new Point(0, 1);
		neighbours[1][4] = new Point(0, -1);
		neighbours[1][5] = new Point(1, -1);
	}

	/**
	 * Compute the consumption for absorbing.
	 * 
	 * @param time
	 *            the time interval
	 * @return the energy consumption to absorb glucose
	 */
	public double computeAbsorbingConsumption(double time) {
		return parameters.computeAbsorbingConsumption(time);
	}

	/**
	 * Compute the maximum glucose level that can be absorbed.
	 * 
	 * @param time
	 *            the time interval
	 * @return the maximum glucose level
	 */
	public double computeMaxAbsorbingGlucose(double time) {
		return parameters.computeMaxAbsorbingGlucose(time);
	}

	/**
	 * Create the initial living beings
	 */
	private void createBeings() {

		Genotype genotype = new Genotype();

		parameters.generateSynthesisCode(genotype);
		parameters.generateCloneCode(genotype);
		Cell cell = locateRandomFreeCell();
		LivingBeing lb = new LivingBeing();
		lb.setHandler(this);
		lb.applyGenotype(genotype, parameters);
		lb.moveTo(cell);

		cell = locateRandomFreeCell();
		parameters.generateSynthesisMoveCode(genotype);
		lb = new LivingBeing();
		lb.setHandler(this);
		lb.applyGenotype(genotype, parameters);
		lb.moveTo(cell);

		genotype = new Genotype();
		cell = locateRandomFreeCell();
		parameters.generateSynthesisCode(genotype);
		parameters.generateAbsorbingCode(genotype);
		parameters.generateAttackCode(genotype);
		parameters.generateGlucoseMoveCode(genotype);
		parameters.generateCloneCode(genotype);
		lb = new LivingBeing();
		lb.setHandler(this);
		lb.applyGenotype(genotype, parameters);
		lb.moveTo(cell);
	}

	/**
	 * Create the grid
	 */
	private void createGrid() {
		/*
		 * Create the grid
		 */
		int n = parameters.getRows();
		int m = parameters.getCols();
		grid = new Cell[n][m];
		double water = parameters.getWaterLevel();
		double carbonDioxide = parameters.getCarbonDioxideLevel();
		for (int i = 0; i < n; ++i) {
			for (int j = 0; j < m; ++j) {
				Cell cell = new Cell();
				cell.setWater(water);
				cell.setCarbonDioxide(carbonDioxide);
				cell.setHandler(this);
				grid[i][j] = cell;
			}
		}

		/*
		 * Create the neighbors
		 */
		Point[] ng = neighbours[0];
		for (int i = 0; i < n; i += 2) {
			for (int j = 0; j < m; ++j) {
				Cell cell = grid[i][j];
				for (int k = 0; k < ng.length; ++k) {
					int x = (j + ng[k].x + m) % m;
					int y = (i + ng[k].y + n) % n;
					cell.setNeighbor(k, grid[y][x]);
				}
			}
		}
		ng = neighbours[1];
		for (int i = 1; i < n; i += 2) {
			for (int j = 0; j < m; ++j) {
				Cell cell = grid[i][j];
				for (int k = 0; k < ng.length; ++k) {
					int x = (j + ng[k].x + m) % m;
					int y = (i + ng[k].y + n) % n;
					cell.setNeighbor(k, grid[y][x]);
				}
			}
		}
	}

	/**
	 * Create the simulation session
	 * 
	 * @param parameters
	 *            the parameters of simulation session
	 */
	public void createSession(SimulationParameters parameters) {
		this.parameters = parameters;
		createGrid();
		createBeings();

		list.clear();
		for (Cell[] row : grid) {
			for (Cell cell : row) {
				LivingBeing locator = cell.getLocator();
				if (locator != null) {
					list.add(locator);
				}
			}
		}

		for (LivingBeing lb : list) {
			lb.react(Double.POSITIVE_INFINITY);
		}
		time = 0;
	}

	/**
	 * Return the breed energy
	 * 
	 * @return the breed energy
	 */
	public double getBreedEnergy() {
		return parameters.getBreedEnergy();
	}

	/**
	 * Return the clone energy
	 * 
	 * @return the clone energy
	 */
	public double getCloneEnergy() {
		return parameters.getCloneEnergy();
	}

	/**
	 * Return the initial energy level
	 * 
	 * @return the initial energy level
	 */
	public double getEnergyLevel() {
		return parameters.getEnergyLevel();
	}

	/**
	 * 
	 * @return
	 */
	public double getMoveEnergy() {
		return parameters.getMoveEnergy();
	}

	/**
	 * Return the simulation parameters
	 * 
	 * @return the parameters
	 */
	public SimulationParameters getParameters() {
		return parameters;
	}

	/**
	 * Return random true or false value with a probability
	 * 
	 * @param probability
	 *            probability of return true
	 * @return true or false
	 */
	public boolean hasChance(double probability) {
		return parameters.hasChance(probability);
	}

	/**
	 * Return if the grid has no living beings
	 * 
	 * @return true if grid has no living being
	 */
	public synchronized boolean isEmpty() {
		for (Cell[] row : grid) {
			for (Cell cell : row) {
				LivingBeing locator = cell.getLocator();
				if (locator != null)
					return false;
			}
		}
		return true;
	}

	private Cell locateRandomFreeCell() {
		Cell cell;
		int x;
		int y;
		Random random = parameters.getRandom();
		int n = parameters.getRows();
		int m = parameters.getCols();
		do {
			x = random.nextInt(m);
			y = random.nextInt(n);
			cell = grid[y][x];
		} while (cell.getLocator() != null);
		return cell;
	}

	/**
	 * Return a random value between 0 and 1
	 * 
	 * @return a random value
	 */
	public double nextRandomDouble() {
		return parameters.nextRandomDouble();
	}

	/**
	 * 
	 * @param probabilities
	 * @return
	 */
	public int nextRandomInt(double[] probabilities) {
		return parameters.nextRandomInt(probabilities);
	}

	/**
	 * Return a random integer between 0 and (range-1)
	 * 
	 * @param range
	 *            the range
	 * @return the random value
	 */
	public int nextRandomInt(int range) {
		return parameters.nextRandomInt(range);
	}

	/**
	 * Update the simulation status
	 */
	public synchronized void update(double time) {
		updateGrid(time);

		list.clear();
		for (Cell[] row : grid) {
			for (Cell cell : row) {
				LivingBeing locator = cell.getLocator();
				if (locator != null) {
					list.add(locator);
				}
			}
		}

		for (LivingBeing lb : list) {
			lb.react(time);
		}

		for (LivingBeing lb : list) {
			lb.update(time);
		}
		this.time += time;
	}

	/**
	 * Update the grid
	 * 
	 * @param time
	 *            the time interval
	 */
	private void updateGrid(double time) {
		for (Cell[] row : grid) {
			for (Cell cell : row) {
				cell.clearChanges();
			}
		}

		double glucoseParm = -Math.expm1(-time / parameters.getGlucoseSpread());
		double waterParm = -Math.expm1(-time / parameters.getWaterSpread());
		double carbonDioxideParm = -Math.expm1(-time
				/ parameters.getCarbonDioxideSpread());
		double oxygenParm = -Math.expm1(-time / parameters.getOxygenSpread());

		for (Cell[] row : grid) {
			for (Cell cell : row) {
				cell.computeSpread(waterParm, glucoseParm, carbonDioxideParm,
						oxygenParm);
			}
		}
		for (Cell[] row : grid) {
			for (Cell cell : row) {
				cell.apply();
			}
		}
	}

	/**
	 * 
	 * @param time
	 * @return
	 */
	public boolean hasReaction(double time) {
		return parameters.hasReaction(time);
	}

	/**
	 * 
	 * @param snapshot
	 */
	public synchronized void retrieveSnapshot(GridSnapshot snapshot,
			CellGetCommand command) {
		double totGlucose = 0;
		double totH2O = 0;
		double totCO2 = 0;
		double totO2 = 0;
		double livingBeingsGlucose = 0;
		int synthesizers = 0;
		int predators = 0;
		int absorbesr = 0;
		for (Cell[] row : grid)
			for (Cell cell : row) {
				totCO2 += cell.getCarbonDioxide();
				totO2 += cell.getOxygen();
				totH2O += cell.getWater();
				totGlucose += cell.getGlucose();
				LivingBeing locator = cell.getLocator();
				if (locator != null) {
					livingBeingsGlucose += locator.getGlucose();
					if (locator.isSynthesizer()) {
						++synthesizers;
					} else if (locator.isAbsorber()) {
						++absorbesr;
					} else
						++predators;
				}
			}
		snapshot.setDataGrid(retrieveDataGrid(snapshot.getDataGrid(), command));
		snapshot.setGlucose(totGlucose);
		snapshot.setWater(totH2O);
		snapshot.setCarbonDioxide(totCO2);
		snapshot.setOxygen(totO2);
		snapshot.setAbsorberCounter(absorbesr);
		snapshot.setPredatorCounter(predators);
		snapshot.setSynthesizerCounter(synthesizers);
		snapshot.setLivingBeingsGlucose(livingBeingsGlucose);
		snapshot.setTime(time);
	}

	/**
	 * 
	 * @param data
	 * @param command
	 * @return
	 */
	private GridData[][] retrieveDataGrid(GridData[][] data,
			CellGetCommand command) {
		if (grid == null)
			return null;
		int n = grid.length;
		int m = grid[0].length;
		if (data == null || data.length != n || data[0].length != m) {
			data = new GridData[n][m];
			for (int i = 0; i < n; ++i) {
				for (int j = 0; j < n; ++j) {
					data[i][j] = new GridData();
				}
			}
		}
		for (int i = 0; i < n; ++i) {
			for (int j = 0; j < n; ++j) {
				GridData gridData = data[i][j];
				Cell cell = grid[i][j];
				gridData.setBackground(command.retrieveData(cell));
				LivingBeing locator = cell.getLocator();
				if (locator != null) {
					gridData.setLivingBean(true);
					gridData.setForeground(locator.getGlucose());
				} else
					gridData.setLivingBean(false);
			}
		}
		return data;
	}

	/**
	 * Update the simulation status
	 */
	public void update() {
		log.debug("Start update");
		update(parameters.getUpdateInterval());
	}

	/**
	 * 
	 * @return
	 */
	public double getConsumptionRate() {
		return parameters.getConsumptionRate();
	}
}
