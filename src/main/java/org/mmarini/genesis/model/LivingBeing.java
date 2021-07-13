/**
 * 
 */
package org.mmarini.genesis.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author US00852
 * 
 */
public class LivingBeing implements SimulationConstants {
	private static final int GLUCOSE_MOVE_INDEX = 2;
	private static final int SYNTHESIS_MOVE_INDEX = 1;
	private static final int ESCAPE_MOVE_INDEX = 0;
	private static final int MOVE_TYPE_COUNT = 3;

	private static Logger log = LoggerFactory.getLogger(LivingBeing.class);

	private Cell cell;
	private double glucose;
	private double synthesisRate;
	private Phenotype phenotype;
	private Genotype genotype;
	private SimulationHandler handler;
	private MoveActionContext[] moveContexts;
	private EscapeActionContext escapeContext;
	private double attackCounter;
	private double absorbingCounter;
	private double synthesisCounter;

	/**
	 *  
	 */
	public LivingBeing() {
		phenotype = new Phenotype();
		genotype = new Genotype();
		moveContexts = new MoveActionContext[MOVE_TYPE_COUNT];
		escapeContext = new EscapeActionContext();

		for (int i = 0; i < MOVE_TYPE_COUNT; ++i)
			moveContexts[i] = new MoveActionContext();
	}

	/**
	 * 
	 * @return
	 */
	private void applyEscapeRule() {
		cell.computeEscape(escapeContext);
		moveContexts[ESCAPE_MOVE_INDEX].setPreference(0);
		moveContexts[ESCAPE_MOVE_INDEX].setTarget(escapeContext.getTarget());
	}

	/**
	 * 
	 * @param genotype
	 * @param parameters
	 */
	public void applyGenotype(Genotype genotype, SimulationParameters parameters) {
		this.genotype.setGenotype(genotype);
		phenotype.generate(this.genotype, parameters);
	}

	/**
	 * 
	 * @return
	 */
	private void applyGlucoseRule() {
		Cell target = cell.findGlucoseTarget();
		if (target == null) {
			moveContexts[GLUCOSE_MOVE_INDEX].setPreference(0);
			return;
		}
		double e = computeAvailableEnergy();
		double g1 = target.getGlucose();
		double preference = phenotype.findGlucoseMoveRule(e, g1);
		moveContexts[GLUCOSE_MOVE_INDEX].setPreference(preference);
		moveContexts[GLUCOSE_MOVE_INDEX].setTarget(target);
	}

	/**
	 * 
	 * @return
	 */
	private void applySyntesisRule() {
		Cell target = cell.findSyntesisTarget();
		if (target == null) {
			moveContexts[SYNTHESIS_MOVE_INDEX].setPreference(0);
			return;
		}
		double e = computeAvailableEnergy();
		double g1 = target.computeMaxSynthesis();
		double preference = phenotype.findSyntesisMoveRule(e, g1);
		moveContexts[SYNTHESIS_MOVE_INDEX].setPreference(preference);
		moveContexts[SYNTHESIS_MOVE_INDEX].setTarget(target);
	}

	/**
	 * 
	 * @param energy
	 * @return
	 */
	private boolean attack(double energy) {
		double availableEnergy = computeAvailableEnergy();
		if (availableEnergy <= 0)
			return true;
		double rule = phenotype.findDefenseRate(energy, availableEnergy);
		double defense = availableEnergy * rule;
		consume(defense);
		double pv = energy / (energy + defense);
		return handler.hasChance(pv);
	}

	/**
	 * 
	 * @param parent1
	 * @param parent2
	 */
	private void breedCode(LivingBeing parent1, LivingBeing parent2) {
		SimulationParameters parameters = handler.getParameters();
		genotype.breed(parent1.genotype, parent2.genotype, parameters);
		phenotype.generate(genotype, parameters);
	}

	/**
	 * 
	 * @param parent
	 * @param parameters
	 */
	private void cloneCode(LivingBeing parent) {
		SimulationParameters parameters = handler.getParameters();
		genotype.cloneCode(parent.genotype, parameters);
		phenotype.generate(genotype, parameters);
	}

	/**
	 * 
	 * @return
	 */
	private double computeAvailableEnergy() {
		return Math.min(cell.computeConsumtionLimits(), glucose);
	}

	/**
	 * 
	 * @param demand
	 */
	private void consume(double demand) {
		double energy = Math.min(cell.computeConsumtionLimits(), demand);
		energy = Math.min(energy, glucose);
		glucose -= energy;
		cell.addCarbonDioxide(energy * CARBON_DIOXIDE_REQUEST);
		cell.addWater(energy * WATER_REQUEST);
		cell.addOxygen(-energy * OXYGEN_REQUEST);
		if (log.isDebugEnabled()) {
			log.debug(this + " consumed " + energy + ", requested " + demand);
		}
		if (energy < demand) {
			cell.addGlucose(glucose);
			moveTo(null);
			if (log.isDebugEnabled()) {
				log.debug(this + " dead");
			}
		}
	}

	/**
	 * 
	 * @return
	 */
	private MoveActionContext findMoveRule() {
		applyEscapeRule();
		applyGlucoseRule();
		applySyntesisRule();
		double tot = 0;
		for (MoveActionContext data : moveContexts)
			tot += data.getPreference();
		if (tot == 0)
			return null;
		double p = tot * handler.nextRandomDouble();
		tot = 0;
		MoveActionContext data = null;
		for (MoveActionContext d : moveContexts) {
			data = d;
			tot += data.getPreference();
			if (p < tot) {
				break;
			}
		}
		return data;
	}

	/**
	 * @return the glucose
	 */
	public double getGlucose() {
		return glucose;
	}

	/**
	 * 
	 * @return
	 */
	public boolean isAbsorber() {
		return (absorbingCounter > synthesisCounter && absorbingCounter >= attackCounter);
	}

	/**
	 * 
	 * @return
	 */
	public boolean isSynthesizer() {
		return (synthesisCounter >= absorbingCounter && synthesisCounter >= attackCounter);
	}

	/**
	 * @param newCell
	 *            the cell to set
	 */
	public void moveTo(Cell newCell) {
		if (cell != null)
			cell.setLocator(null);
		cell = newCell;
		if (newCell != null)
			newCell.setLocator(this);
	}

	/**
	 * synthesis
	 * 
	 * @param time
	 */
	public void react(double time) {
		if (cell == null || !handler.hasReaction(time))
			return;
		reactSynthesis();
		reactToMove();
		// TODO mettere a posto il tempo di assorbimento
		reactToAbsorbe(time);
		reactToAttack();
		reactToClone();
		reactToBreed();
	}

	/**
	 * 
	 */
	private void reactSynthesis() {
		synthesisRate = phenotype.computeSynthesisRate(glucose);
	}

	/**
	 * 
	 * @param time
	 */
	private void reactToAbsorbe(double time) {
		double availableGlucose = cell.computeAvailableGlucose(time);
		if (availableGlucose <= 0)
			return;
		double availableEnergy = computeAvailableEnergy();
		double absorbingEnergy = handler.computeAbsorbingConsumption(time);
		if (availableEnergy <= absorbingEnergy)
			return;
		double rho = availableGlucose / (availableGlucose + absorbingEnergy);
		double probability;
		if (rho <= phenotype.getAbsorbingLevel()) {
			probability = phenotype.getAbsorbingProbability(0);
		} else {
			probability = phenotype.getAbsorbingProbability(1);
		}
		if (handler.hasChance(probability)) {
			cell.addGlucose(-availableGlucose);
			glucose += availableGlucose;
			consume(absorbingEnergy);
			absorbingCounter += availableGlucose;
		}
	}

	/**
	 * 
	 */
	private void reactToAttack() {
		/*
		 * Check for clone availability
		 */
		double availableEnergy = computeAvailableEnergy();
		if (availableEnergy <= 0)
			return;
		AttackContext info = cell.findPreys();
		if (info == null)
			return;
		LivingBeing heavyPrey = info.getHeavyPrey();
		LivingBeing lightPrey = info.getLightPrey();
		double heavyWeight = heavyPrey.glucose;
		double lightWeight = lightPrey.glucose;
		AttackRule rule = phenotype.findAttackRule(lightWeight, heavyWeight,
				availableEnergy);
		if (handler.hasChance(rule.getAttackProbability())) {
			LivingBeing prey;
			if (handler.hasChance(rule.getHeavyProbability())) {
				prey = heavyPrey;
			} else {
				prey = lightPrey;
			}
			double energy = availableEnergy * rule.getAttackEnergy();
			consume(energy);
			if (prey.attack(energy)) {
				glucose += prey.glucose;
				attackCounter += prey.glucose;
				prey.moveTo(null);
			}
		}
	}

	/**
	 * 
	 */
	private void reactToBreed() {
		/*
		 * Check for clone availability
		 */
		double breedEnergy = handler.getBreedEnergy();
		double energy = computeAvailableEnergy();
		if (energy <= breedEnergy)
			return;
		Cell freeLocation = cell.chooseFreeCell();
		if (freeLocation == null)
			return;
		LivingBeing parent2 = cell.chooseBreeder();
		if (parent2 == null)
			return;

		BreedRule rule = phenotype.findBreedRule(glucose);
		if (handler.hasChance(rule.getProbability())) {
			consume(breedEnergy);
			LivingBeing child = new LivingBeing();
			child.handler = handler;
			child.glucose = glucose * rule.getGlucoseRate();
			child.breedCode(this, parent2);
			child.moveTo(freeLocation);
			glucose -= child.glucose;
		}
	}

	/**
	 */
	private void reactToClone() {
		/*
		 * Check for clone availability
		 */
		double cloneEnergy = handler.getCloneEnergy();
		double energy = computeAvailableEnergy();
		if (energy <= cloneEnergy)
			return;
		Cell freeLocation = cell.chooseFreeCell();
		if (freeLocation == null)
			return;

		CloneRule rule = phenotype.findCloneRule(glucose);
		if (handler.hasChance(rule.getProbability())) {
			if (log.isDebugEnabled())
				log.debug(this + " cloning");
			consume(cloneEnergy);
			LivingBeing clone = new LivingBeing();
			clone.handler = handler;
			clone.glucose = glucose * rule.getGlucoseRate();
			clone.cloneCode(this);
			clone.moveTo(freeLocation);
			glucose -= clone.glucose;
			if (log.isDebugEnabled()) {
				log.debug(this + " cloned");
				log.debug(clone + " born");
			}
		}
	}

	/**
	 * 
	 */
	private void reactToMove() {
		/*
		 * Check for move availability
		 */
		double availableEnergy = computeAvailableEnergy();
		double moveEnergy = handler.getMoveEnergy();
		if (availableEnergy <= moveEnergy || !cell.hasFreeNeighbors()) {
			return;
		}

		MoveActionContext data = findMoveRule();
		if (data == null)
			return;
		consume(moveEnergy);
		moveTo(data.getTarget());
	}

	/**
	 * @param handler
	 *            the handler to set
	 */
	public void setHandler(SimulationHandler handler) {
		this.handler = handler;
	}

	/**
	 * Synthesis
	 * 
	 * @param time
	 */
	private void syntetize(double time) {
		double glucose = Math.min(cell.computeSynthesisLimit(time), time
				* synthesisRate);
		if (glucose > 0) {
			this.glucose += glucose;
			synthesisCounter += glucose;
			cell.addWater(-glucose * WATER_REQUEST);
			cell.addCarbonDioxide(-glucose * CARBON_DIOXIDE_REQUEST);
			cell.addOxygen(glucose * OXYGEN_REQUEST);
			if (log.isDebugEnabled()) {
				log.debug(this + " synthesized " + glucose + ", weight "
						+ this.glucose);
			}
		}
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "LivingBeing@" + System.identityHashCode(this) + " [glucose="
				+ glucose + "]";
	}

	/**
	 * 
	 * @param time
	 */
	public void update(double time) {
		if (cell == null)
			return;
		syntetize(time);
		consume(time * handler.getConsumptionRate());
	}
}
