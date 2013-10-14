/**
 * 
 */
package org.mmarini.genesis.model;

/**
 * @author US00852
 * 
 */
public class GridSnapshot {
	private GridData[][] dataGrid;
	private Snapshot snapshot;

	/**
	 * 
	 */
	public GridSnapshot() {
		snapshot = new Snapshot();
	}

	/**
	 * @return
	 * @see org.mmarini.genesis.model.Snapshot#getAbsorberCounter()
	 */
	public int getAbsorberCounter() {
		return snapshot.getAbsorberCounter();
	}

	/**
	 * @return
	 * @see org.mmarini.genesis.model.Snapshot#getCarbonDioxide()
	 */
	public double getCarbonDioxide() {
		return snapshot.getCarbonDioxide();
	}

	/**
	 * @return the dataGrid
	 */
	public GridData[][] getDataGrid() {
		return dataGrid;
	}

	/**
	 * @return
	 * @see org.mmarini.genesis.model.Snapshot#getGlucose()
	 */
	public double getGlucose() {
		return snapshot.getGlucose();
	}

	/**
	 * @return
	 * @see org.mmarini.genesis.model.Snapshot#getLivingBeingsGlucose()
	 */
	public double getLivingBeingsGlucose() {
		return snapshot.getLivingBeingsGlucose();
	}

	/**
	 * @return
	 * @see org.mmarini.genesis.model.Snapshot#getOxygen()
	 */
	public double getOxygen() {
		return snapshot.getOxygen();
	}

	/**
	 * @return
	 * @see org.mmarini.genesis.model.Snapshot#getPredatorCounter()
	 */
	public int getPredatorCounter() {
		return snapshot.getPredatorCounter();
	}

	/**
	 * @return the snapshot
	 */
	public Snapshot getSnapshot() {
		return snapshot;
	}

	/**
	 * @return
	 * @see org.mmarini.genesis.model.Snapshot#getSynthesizerCounter()
	 */
	public int getSynthesizerCounter() {
		return snapshot.getSynthesizerCounter();
	}

	/**
	 * @return
	 * @see org.mmarini.genesis.model.Snapshot#getTime()
	 */
	public double getTime() {
		return snapshot.getTime();
	}

	/**
	 * @return
	 * @see org.mmarini.genesis.model.Snapshot#getTotalBeingCount()
	 */
	public int getTotalBeingCount() {
		return snapshot.getTotalBeingCount();
	}

	/**
	 * @return
	 * @see org.mmarini.genesis.model.Snapshot#getWater()
	 */
	public double getWater() {
		return snapshot.getWater();
	}

	/**
	 * @param absorberCounter
	 * @see org.mmarini.genesis.model.Snapshot#setAbsorberCounter(int)
	 */
	public void setAbsorberCounter(int absorberCounter) {
		snapshot.setAbsorberCounter(absorberCounter);
	}

	/**
	 * @param carbonDioxide
	 * @see org.mmarini.genesis.model.Snapshot#setCarbonDioxide(double)
	 */
	public void setCarbonDioxide(double carbonDioxide) {
		snapshot.setCarbonDioxide(carbonDioxide);
	}

	/**
	 * @param dataGrid
	 *            the dataGrid to set
	 */
	public void setDataGrid(GridData[][] dataGrid) {
		this.dataGrid = dataGrid;
	}

	/**
	 * @param glucose
	 * @see org.mmarini.genesis.model.Snapshot#setGlucose(double)
	 */
	public void setGlucose(double glucose) {
		snapshot.setGlucose(glucose);
	}

	/**
	 * @param livingBeingsGlucose
	 * @see org.mmarini.genesis.model.Snapshot#setLivingBeingsGlucose(double)
	 */
	public void setLivingBeingsGlucose(double livingBeingsGlucose) {
		snapshot.setLivingBeingsGlucose(livingBeingsGlucose);
	}

	/**
	 * @param oxygen
	 * @see org.mmarini.genesis.model.Snapshot#setOxygen(double)
	 */
	public void setOxygen(double oxygen) {
		snapshot.setOxygen(oxygen);
	}

	/**
	 * @param predatorCounter
	 * @see org.mmarini.genesis.model.Snapshot#setPredatorCounter(int)
	 */
	public void setPredatorCounter(int predatorCounter) {
		snapshot.setPredatorCounter(predatorCounter);
	}

	/**
	 * @param synthesizerCounter
	 * @see org.mmarini.genesis.model.Snapshot#setSynthesizerCounter(int)
	 */
	public void setSynthesizerCounter(int synthesizerCounter) {
		snapshot.setSynthesizerCounter(synthesizerCounter);
	}

	/**
	 * @param time
	 * @see org.mmarini.genesis.model.Snapshot#setTime(double)
	 */
	public void setTime(double time) {
		snapshot.setTime(time);
	}

	/**
	 * @param water
	 * @see org.mmarini.genesis.model.Snapshot#setWater(double)
	 */
	public void setWater(double water) {
		snapshot.setWater(water);
	}

}
