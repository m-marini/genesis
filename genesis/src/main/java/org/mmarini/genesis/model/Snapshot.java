/**
 * 
 */
package org.mmarini.genesis.model;

/**
 * @author US00852
 * 
 */
public class Snapshot implements Cloneable {
	private double time;
	private double glucose;
	private double water;
	private double carbonDioxide;
	private double oxygen;
	private double livingBeingsGlucose;
	private int synthesizerCounter;
	private int predatorCounter;
	private int absorberCounter;

	/**
	 * 
	 */
	public Snapshot() {
	}

	/**
	 * 
	 * @param snapshot
	 */
	public Snapshot(Snapshot snapshot) {
		setSnapshot(snapshot);
	}

	/**
	 * 
	 * @param snapshot
	 */
	private void setSnapshot(Snapshot snapshot) {
		time = snapshot.time;
		glucose = snapshot.glucose;
		water = snapshot.water;
		carbonDioxide = snapshot.carbonDioxide;
		oxygen = snapshot.oxygen;
		livingBeingsGlucose = snapshot.livingBeingsGlucose;
		synthesizerCounter = snapshot.synthesizerCounter;
		predatorCounter = snapshot.predatorCounter;
		absorberCounter = snapshot.absorberCounter;
	}

	/**
	 * @return the glucose
	 */
	public double getGlucose() {
		return glucose;
	}

	/**
	 * @param glucose
	 *            the glucose to set
	 */
	public void setGlucose(double glucose) {
		this.glucose = glucose;
	}

	/**
	 * @return the water
	 */
	public double getWater() {
		return water;
	}

	/**
	 * @param water
	 *            the water to set
	 */
	public void setWater(double water) {
		this.water = water;
	}

	/**
	 * @return the carbonDioxide
	 */
	public double getCarbonDioxide() {
		return carbonDioxide;
	}

	/**
	 * @param carbonDioxide
	 *            the carbonDioxide to set
	 */
	public void setCarbonDioxide(double carbonDioxide) {
		this.carbonDioxide = carbonDioxide;
	}

	/**
	 * @return the oxygen
	 */
	public double getOxygen() {
		return oxygen;
	}

	/**
	 * @param oxygen
	 *            the oxygen to set
	 */
	public void setOxygen(double oxygen) {
		this.oxygen = oxygen;
	}

	/**
	 * @return the livingBeingsGlucose
	 */
	public double getLivingBeingsGlucose() {
		return livingBeingsGlucose;
	}

	/**
	 * @param livingBeingsGlucose
	 *            the livingBeingsGlucose to set
	 */
	public void setLivingBeingsGlucose(double livingBeingsGlucose) {
		this.livingBeingsGlucose = livingBeingsGlucose;
	}

	/**
	 * @return the synthesizerCounter
	 */
	public int getSynthesizerCounter() {
		return synthesizerCounter;
	}

	/**
	 * @param synthesizerCounter
	 *            the synthesizerCounter to set
	 */
	public void setSynthesizerCounter(int synthesizerCounter) {
		this.synthesizerCounter = synthesizerCounter;
	}

	/**
	 * @return the predatorCounter
	 */
	public int getPredatorCounter() {
		return predatorCounter;
	}

	/**
	 * @param predatorCounter
	 *            the predatorCounter to set
	 */
	public void setPredatorCounter(int predatorCounter) {
		this.predatorCounter = predatorCounter;
	}

	/**
	 * @return the absorberCounter
	 */
	public int getAbsorberCounter() {
		return absorberCounter;
	}

	/**
	 * @param absorberCounter
	 *            the absorberCounter to set
	 */
	public void setAbsorberCounter(int absorberCounter) {
		this.absorberCounter = absorberCounter;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Snapshot [time=" + time + ", glucose=" + glucose + ", water="
				+ water + ", carbonDioxide=" + carbonDioxide + ", oxygen="
				+ oxygen + ", livingBeingsGlucose=" + livingBeingsGlucose
				+ ", synthesizerCounter=" + synthesizerCounter
				+ ", predatorCounter=" + predatorCounter + ", absorberCounter="
				+ absorberCounter + "]";
	}

	/**
	 * 
	 * @return
	 */
	public int getTotalBeingCount() {
		return absorberCounter + synthesizerCounter + predatorCounter;
	}

	/**
	 * @return the time
	 */
	public double getTime() {
		return time;
	}

	/**
	 * @param time
	 *            the time to set
	 */
	public void setTime(double time) {
		this.time = time;
	}

	/**
	 * @see java.lang.Object#clone()
	 */
	@Override
	public Snapshot clone() {
		return new Snapshot(this);
	}

}
