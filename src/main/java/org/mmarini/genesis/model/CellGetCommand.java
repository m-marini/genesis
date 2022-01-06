/**
 *
 */
package org.mmarini.genesis.model;

/**
 * @author US00852
 *
 */
public interface CellGetCommand {
    CellGetCommand WATER_COMMAND = new CellGetCommand() {

        @Override
        public double retrieveData(Cell cell) {
            return cell.getWater();
        }
    };

    CellGetCommand OXYGEN_COMMAND = new CellGetCommand() {

        @Override
        public double retrieveData(Cell cell) {
            return cell.getOxygen();
        }
    };

    CellGetCommand GLUCOSE_COMMAND = new CellGetCommand() {

        @Override
        public double retrieveData(Cell cell) {
            return cell.getGlucose();
        }
    };

    CellGetCommand CARBON_DIOXIDE_COMMAND = new CellGetCommand() {

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
    double retrieveData(Cell cell);
}
