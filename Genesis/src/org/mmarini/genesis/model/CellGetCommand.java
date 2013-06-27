/**
 * 
 */
package org.mmarini.genesis.model;

/**
 * @author US00852
 * 
 */
public interface CellGetCommand {
	public static CellGetCommand WATER_COMMAND = new CellGetCommand() {

		@Override
		public double retrieveData(Cell cell) {
			return cell.getWater();
		}
	};

	public static CellGetCommand OXYGEN_COMMAND = new CellGetCommand() {

		@Override
		public double retrieveData(Cell cell) {
			return cell.getOxygen();
		}
	};

	public static CellGetCommand GLUCOSE_COMMAND = new CellGetCommand() {

		@Override
		public double retrieveData(Cell cell) {
			return cell.getGlucose();
		}
	};

	public static CellGetCommand CARBON_DIOXIDE_COMMAND = new CellGetCommand() {

		@Override
		public double retrieveData(Cell cell) {
			return cell.getCarbonDioxide();
		}
	};

	/**
	 * 
	 * @param cell
	 * @return
	 */
	public abstract double retrieveData(Cell cell);
}
