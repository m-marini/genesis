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
	public Snapshot(final Snapshot snapshot) {
		setSnapshot(snapshot);
	}

	/**
	 * @see java.lang.Object#clone()
	 */
	@Override
	public Snapshot clone() {
		return new Snapshot(this);
	}

	/**
	 * @return the absorberCounter
	 */
	public int getAbsorberCounter() {
		return absorberCounter;
	}

	/**
	 * @return the carbonDioxide
	 */
	public double getCarbonDioxide() {
		return carbonDioxide;
	}

	/**
	 * @return the glucose
	 */
	public double getGlucose() {
		return glucose;
	}

	/**
	 * @return the livingBeingsGlucose
	 */
	public double getLivingBeingsGlucose() {
		return livingBeingsGlucose;
	}

	/**
	 * @return the oxygen
	 */
	public double getOxygen() {
		return oxygen;
	}

	/**
	 * @return the predatorCounter
	 */
	public int getPredatorCounter() {
		return predatorCounter;
	}

	/**
	 * @return the synthesizerCounter
	 */
	public int getSynthesizerCounter() {
		return synthesizerCounter;
	}

	/**
	 * @return the time
	 */
	public double getTime() {
		return time;
	}

	/**
	 * 
	 * @return
	 */
	public int getTotalBeingCount() {
		return absorberCounter + synthesizerCounter + predatorCounter;
	}

	/**
	 * @return the water
	 */
	public double getWater() {
		return water;
	}

	/**
	 * @param absorberCounter
	 *            the absorberCounter to set
	 */
	public void setAbsorberCounter(final int absorberCounter) {
		this.absorberCounter = absorberCounter;
	}

	/**
	 * @param carbonDioxide
	 *            the carbonDioxide to set
	 */
	public void setCarbonDioxide(final double carbonDioxide) {
		this.carbonDioxide = carbonDioxide;
	}

	/**
	 * @param glucose
	 *            the glucose to set
	 */
	public void setGlucose(final double glucose) {
		this.glucose = glucose;
	}

	/**
	 * @param livingBeingsGlucose
	 *            the livingBeingsGlucose to set
	 */
	public void setLivingBeingsGlucose(final double livingBeingsGlucose) {
		this.livingBeingsGlucose = livingBeingsGlucose;
	}

	/**
	 * @param oxygen
	 *            the oxygen to set
	 */
	public void setOxygen(final double oxygen) {
		this.oxygen = oxygen;
	}

	/**
	 * @param predatorCounter
	 *            the predatorCounter to set
	 */
	public void setPredatorCounter(final int predatorCounter) {
		this.predatorCounter = predatorCounter;
	}

	/**
	 * 
	 * @param snapshot
	 */
	private void setSnapshot(final Snapshot snapshot) {
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
	 * @param synthesizerCounter
	 *            the synthesizerCounter to set
	 */
	public void setSynthesizerCounter(final int synthesizerCounter) {
		this.synthesizerCounter = synthesizerCounter;
	}

	/**
	 * @param time
	 *            the time to set
	 */
	public void setTime(final double time) {
		this.time = time;
	}

	/**
	 * @param water
	 *            the water to set
	 */
	public void setWater(final double water) {
		this.water = water;
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

}
