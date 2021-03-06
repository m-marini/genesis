/**
 *
 */
package org.mmarini.genesis.model;

/**
 * The Cell specifies the status of a single location.
 * <p>
 * The status is determined bye the chemical component levels and the living
 * being staying in the location.
 * </p>
 * <p>
 * Moreover the cell maintains also links to the neighbors cell and the changes
 * of the chemical components.
 * </p>
 *
 * @author US00852
 *
 */
public class Cell implements SimulationConstants {
    private static final int NEIGHBOUR_COUNT = 6;
    private final Cell[] neighbors;
    private LivingBeing locator;
    private double deltaWater;
    private double deltaOxygen;
    private double deltaCarbonDioxide;
    private double deltaGlucose;
    private double water;
    private double oxygen;
    private double carbonDioxide;
    private double glucose;
    private SimulationHandler handler;

    /**
     * Create a cell
     */
    public Cell() {
        neighbors = new Cell[NEIGHBOUR_COUNT];
    }

    /**
     * Increase the carbon dioxide level
     *
     * @param value
     *            the value to add
     */
    public void addCarbonDioxide(double value) {
        carbonDioxide += value;
    }

    /**
     * Increase the glucose level
     *
     * @param value
     *            the value to add
     */
    public void addGlucose(double value) {
        glucose += value;
    }

    /**
     * Increase the oxygen level
     *
     * @param value
     *            the value to add
     */
    public void addOxygen(double value) {
        oxygen += value;
    }

    /**
     * Increase the water level
     *
     * @param value
     *            the value to add
     */
    public void addWater(double value) {
        water += value;
    }

    /**
     * Apply the changes to the chemical components
     */
    public void apply() {
        glucose += deltaGlucose;
        water += deltaWater;
        carbonDioxide += deltaCarbonDioxide;
        oxygen += deltaOxygen;
    }

    /**
     * Choose randomly a breeder.
     * <p>
     * The breeder is chosen with a probability proportional to its glucose
     * level.
     *
     * @return the chosen breeder
     */
    public LivingBeing chooseBreeder() {
        LivingBeing[] list = new LivingBeing[NEIGHBOUR_COUNT];
        int n = 0;
        for (Cell cell : neighbors) {
            if (cell.locator != null) {
                list[n] = cell.locator;
                ++n;
            }
        }
        if (n == 0)
            return null;
        if (n == 1)
            return list[0];

        /*
         * Select randomly the being. The probability of selection is
         * proportional to the glucose level of being.
         */
        double pp = 0;
        for (int i = 0; i < n; ++i) {
            pp += list[i].getGlucose();
        }
        double p = handler.nextRandomDouble() * pp;
        pp = 0;
        for (int i = 0; i < n - 1; ++i) {
            pp += list[i].getGlucose();
            if (p < pp)
                return list[i];
        }
        return list[n - 1];
    }

    /**
     * Choose randomly a free cell
     *
     * @return the free cell
     */
    public Cell chooseFreeCell() {
        Cell[] list = new Cell[NEIGHBOUR_COUNT];
        int n = 0;
        for (Cell cell : neighbors) {
            if (cell.locator == null)
                list[n] = cell;
            ++n;
        }
        if (n == 0)
            return null;
        if (n == 1)
            return list[0];
        return list[handler.nextRandomInt(n)];
    }

    /**
     * Clear all changes of chemical levels
     */
    public void clearChanges() {
        deltaCarbonDioxide = 0;
        deltaWater = 0;
        deltaOxygen = 0;
        deltaGlucose = 0;
    }

    /**
     * Compute the glucose level available to the absorbing activity
     *
     * @param time
     *            the time interval
     * @return the level
     */
    public double computeAvailableGlucose(double time) {
        return Math.min(glucose, handler.computeMaxAbsorbingGlucose(time));
    }

    /**
     * Compute the spread of carbon dioxide
     *
     * @param k
     *            the spread parameter
     */
    private void computeCarbonDioxideSpread(double k) {
        /*
         * Compute average
         */
        double avg = carbonDioxide;
        for (Cell cell : neighbors) {
            avg += cell.carbonDioxide;
        }
        avg /= NEIGHBOUR_COUNT + 1;

        /*
         * Update the differences
         */
        double v = 0;
        for (Cell cell : neighbors) {
            double dv = (avg - cell.carbonDioxide) * k;
            v += dv;
            cell.deltaCarbonDioxide += dv;
        }
        deltaCarbonDioxide -= v;
    }

    /**
     * Compute the consumption limits
     *
     * @return the max energy
     */
    public double computeConsumtionLimits() {
        return oxygen / OXYGEN_REQUEST;
    }

    /**
     *
     * @param escapeData
     */
    public void computeEscape(EscapeActionContext escapeData) {
        double risk = computeRisk();
        Cell target = null;
        double targetRisk = Double.POSITIVE_INFINITY;
        for (Cell cell : neighbors) {
            double r = cell.computeRisk();
            if (r < targetRisk) {
                target = cell;
                targetRisk = r;
            }
        }
        if (locator != null)
            targetRisk -= locator.getGlucose();
        // TODO Check for no targetRisk and no risk
        escapeData.setRisk(risk / (targetRisk + risk));
        escapeData.setTarget(target);
    }

    /**
     * Compute the spread of glucose
     *
     * @param k
     *            the spread parameter
     */
    private void computeGlucoseSpread(double k) {
        /*
         * Compute average
         */
        double avg = glucose;
        for (Cell cell : neighbors) {
            avg += cell.glucose;
        }
        avg /= NEIGHBOUR_COUNT + 1;

        /*
         * Update the differences
         */
        double v = 0;
        for (Cell cell : neighbors) {
            double dv = (avg - cell.glucose) * k;
            v += dv;
            cell.deltaGlucose += dv;
        }
        deltaGlucose -= v;
    }

    /**
     *
     * @return
     */
    public double computeMaxSynthesis() {
        double c = carbonDioxide / CARBON_DIOXIDE_REQUEST;
        double w = water / WATER_REQUEST;
        return Math.min(c, w);
    }

    /**
     * Compute the spread of oxygen
     *
     * @param k
     *            the spread parameter
     */
    private void computeOxygenSpread(double k) {
        /*
         * Compute average
         */
        double avg = oxygen;
        for (Cell cell : neighbors) {
            avg += cell.oxygen;
        }
        avg /= NEIGHBOUR_COUNT + 1;

        /*
         * Update the differences
         */
        double v = 0;
        for (Cell cell : neighbors) {
            double dv = (avg - cell.oxygen) * k;
            v += dv;
            cell.deltaOxygen += dv;
        }
        deltaOxygen -= v;
    }

    /**
     *
     * @return
     */
    private double computeRisk() {
        double risk = 0;
        for (Cell cell : neighbors) {
            if (cell.locator != null) {
                risk += cell.locator.getGlucose();
            }
        }
        return risk;
    }

    /**
     * Compute the spread of chemical components
     *
     * @param waterParm
     *            water spread parameter
     * @param glucoseParm
     *            glucose spread parameter
     * @param carbonDioxideParm
     *            carbon dioxide spread parameter
     * @param oxygenParm
     *            oxygen spread parameter
     */
    public void computeSpread(double waterParm, double glucoseParm,
                              double carbonDioxideParm, double oxygenParm) {
        computeWaterSpread(waterParm / (NEIGHBOUR_COUNT + 1));
        computeOxygenSpread(oxygenParm / (NEIGHBOUR_COUNT + 1));
        computeCarbonDioxideSpread(carbonDioxideParm / (NEIGHBOUR_COUNT + 1));
        computeGlucoseSpread(glucoseParm / (NEIGHBOUR_COUNT + 1));
    }

    /**
     * Compute the synthesis limits
     *
     * @param time
     *            the time interval
     * @return the glucose level
     */
    public double computeSynthesisLimit(double time) {
        double el = handler.getEnergyLevel() * time;
        double max = computeMaxSynthesis();
        return Math.min(el, max);
    }

    /**
     * Compute the spread of water
     *
     * @param k
     *            the spread parameter
     */
    private void computeWaterSpread(double k) {
        /*
         * Compute average
         */
        double avg = water;
        for (Cell cell : neighbors) {
            avg += cell.water;
        }
        avg /= NEIGHBOUR_COUNT + 1;

        /*
         * Update the differences
         */
        double v = 0;
        for (Cell cell : neighbors) {
            double dv = (avg - cell.water) * k;
            v -= dv;
            cell.deltaWater += dv;
        }
        deltaWater += v;
    }

    /**
     *
     * @return
     */
    public Cell findGlucoseTarget() {
        return findGlucoseTargetMax();
    }

    /**
     *
     * @return
     */
    private Cell findGlucoseTargetMax() {
        double max = Double.NEGATIVE_INFINITY;
        Cell target = null;
        for (Cell cell : neighbors) {
            if (cell.locator == null) {
                double p = cell.glucose;
                if (p > max) {
                    max = p;
                    target = cell;
                }
            }
        }
        if (max < glucose)
            return null;
        return target;
    }

    /**
     * Find preys between the neighbors
     *
     * @return the attack info
     */
    public AttackContext findPreys() {
        LivingBeing heavy = null;
        LivingBeing light = null;
        for (Cell cell : neighbors) {
            LivingBeing target = cell.getLocator();
            if (target != null) {
                double weight = target.getGlucose();
                if (heavy == null || weight > heavy.getGlucose()) {
                    heavy = target;
                }
                if (light == null || weight < light.getGlucose()) {
                    light = target;
                }
            }
        }
        if (heavy == null && light == null)
            return null;
        AttackContext result = new AttackContext();
        result.setHeavyPrey(heavy);
        result.setLightPrey(light);
        return result;
    }

    /**
     *
     * @return
     */
    public Cell findSyntesisTarget() {
        return findSynthesisMax();
    }

    /**
     *
     * @return
     */
    private Cell findSynthesisMax() {
        Cell target = null;
        double max = Double.NEGATIVE_INFINITY;
        for (Cell cell : neighbors) {
            if (cell.locator == null) {
                double p = cell.computeMaxSynthesis();
                if (p > max) {
                    max = p;
                    target = cell;
                }
            }
        }
        if (max < computeMaxSynthesis())
            return null;
        return target;
    }

    /**
     * Return the carbon dioxide level
     *
     * @return the carbonDioxide
     */
    public double getCarbonDioxide() {
        return carbonDioxide;
    }

    /**
     * Set the carbon dioxide level
     *
     * @param carbonDioxide
     *            the carbonDioxide to set
     */
    public void setCarbonDioxide(double carbonDioxide) {
        this.carbonDioxide = carbonDioxide;
    }

    /**
     * Return the glucose level
     *
     * @return the glucose
     */
    public double getGlucose() {
        return glucose;
    }

    /**
     * Return the cell locator
     *
     * @return the locator or null if the cell is empty
     */
    public LivingBeing getLocator() {
        return locator;
    }

    /**
     * Set the locator of a cell
     *
     * @param locator
     *            the locator to set
     */
    public void setLocator(LivingBeing locator) {
        this.locator = locator;
    }

    /**
     * Return the oxygen level
     *
     * @return the oxygen
     */
    public double getOxygen() {
        return oxygen;
    }

    /**
     * Return the water level
     *
     * @return the water
     */
    public double getWater() {
        return water;
    }

    /**
     * Set the water level
     *
     * @param water
     *            the water to set
     */
    public void setWater(double water) {
        this.water = water;
    }

    /**
     *
     * @return
     */
    public boolean hasFreeNeighbors() {
        for (Cell cell : neighbors) {
            if (cell.locator == null)
                return true;
        }
        return false;
    }

    /**
     * Set the simulation handler
     *
     * @param handler
     *            the handler to set
     */
    public void setHandler(SimulationHandler handler) {
        this.handler = handler;
    }

    /**
     * Set the neighbor
     *
     * @param index
     *            of neighbor
     * @param neighbor
     *            the neighbor
     */
    public void setNeighbor(int index, Cell neighbor) {
        neighbors[index] = neighbor;
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "Cell [carbonDioxide=" + carbonDioxide + ", water=" + water
                + ", oxygen=" + oxygen + ", glucose=" + glucose + ", locator="
                + locator + "]";
    }
}