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
	private final Phenotype phenotype;
	private final Genotype genotype;
	private SimulationHandler handler;
	private final MoveActionContext[] moveContexts;
	private final EscapeActionContext escapeContext;
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
	public void applyGenotype(final Genotype genotype,
			final SimulationParameters parameters) {
		this.genotype.setGenotype(genotype);
		phenotype.generate(this.genotype, parameters);
	}

	/**
	 * 
	 * @return
	 */
	private void applyGlucoseRule() {
		final Cell target = cell.findGlucoseTarget();
		if (target == null) {
			moveContexts[GLUCOSE_MOVE_INDEX].setPreference(0);
			return;
		}
		final double e = computeAvailableEnergy();
		final double g1 = target.getGlucose();
		final double preference = phenotype.findGlucoseMoveRule(e, g1);
		moveContexts[GLUCOSE_MOVE_INDEX].setPreference(preference);
		moveContexts[GLUCOSE_MOVE_INDEX].setTarget(target);
	}

	/**
	 * 
	 * @return
	 */
	private void applySyntesisRule() {
		final Cell target = cell.findSyntesisTarget();
		if (target == null) {
			moveContexts[SYNTHESIS_MOVE_INDEX].setPreference(0);
			return;
		}
		final double e = computeAvailableEnergy();
		final double g1 = target.computeMaxSynthesis();
		final double preference = phenotype.findSyntesisMoveRule(e, g1);
		moveContexts[SYNTHESIS_MOVE_INDEX].setPreference(preference);
		moveContexts[SYNTHESIS_MOVE_INDEX].setTarget(target);
	}

	/**
	 * 
	 * @param energy
	 * @return
	 */
	private boolean attack(final double energy) {
		final double availableEnergy = computeAvailableEnergy();
		if (availableEnergy <= 0)
			return true;
		final double rule = phenotype.findDefenseRate(energy, availableEnergy);
		final double defense = availableEnergy * rule;
		consume(defense);
		final double pv = energy / (energy + defense);
		return handler.hasChance(pv);
	}

	/**
	 * 
	 * @param parent1
	 * @param parent2
	 */
	private void breedCode(final LivingBeing parent1, final LivingBeing parent2) {
		final SimulationParameters parameters = handler.getParameters();
		genotype.breed(parent1.genotype, parent2.genotype, parameters);
		phenotype.generate(genotype, parameters);
	}

	/**
	 * 
	 * @param parent
	 * @param parameters
	 */
	private void cloneCode(final LivingBeing parent) {
		final SimulationParameters parameters = handler.getParameters();
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
	private void consume(final double demand) {
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
		for (final MoveActionContext data : moveContexts)
			tot += data.getPreference();
		if (tot == 0)
			return null;
		final double p = tot * handler.nextRandomDouble();
		tot = 0;
		MoveActionContext data = null;
		for (final MoveActionContext d : moveContexts) {
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
	public void moveTo(final Cell newCell) {
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
	public void react(final double time) {
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
	private void reactToAbsorbe(final double time) {
		final double availableGlucose = cell.computeAvailableGlucose(time);
		if (availableGlucose <= 0)
			return;
		final double availableEnergy = computeAvailableEnergy();
		final double absorbingEnergy = handler
				.computeAbsorbingConsumption(time);
		if (availableEnergy <= absorbingEnergy)
			return;
		final double rho = availableGlucose
				/ (availableGlucose + absorbingEnergy);
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
		final double availableEnergy = computeAvailableEnergy();
		if (availableEnergy <= 0)
			return;
		final AttackContext info = cell.findPreys();
		if (info == null)
			return;
		final LivingBeing heavyPrey = info.getHeavyPrey();
		final LivingBeing lightPrey = info.getLightPrey();
		final double heavyWeight = heavyPrey.glucose;
		final double lightWeight = lightPrey.glucose;
		final AttackRule rule = phenotype.findAttackRule(lightWeight,
				heavyWeight, availableEnergy);
		if (handler.hasChance(rule.getAttackProbability())) {
			LivingBeing prey;
			if (handler.hasChance(rule.getHeavyProbability())) {
				prey = heavyPrey;
			} else {
				prey = lightPrey;
			}
			final double energy = availableEnergy * rule.getAttackEnergy();
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
		final double breedEnergy = handler.getBreedEnergy();
		final double energy = computeAvailableEnergy();
		if (energy <= breedEnergy)
			return;
		final Cell freeLocation = cell.chooseFreeCell();
		if (freeLocation == null)
			return;
		final LivingBeing parent2 = cell.chooseBreeder();
		if (parent2 == null)
			return;

		final BreedRule rule = phenotype.findBreedRule(glucose);
		if (handler.hasChance(rule.getProbability())) {
			consume(breedEnergy);
			final LivingBeing child = new LivingBeing();
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
		final double cloneEnergy = handler.getCloneEnergy();
		final double energy = computeAvailableEnergy();
		if (energy <= cloneEnergy)
			return;
		final Cell freeLocation = cell.chooseFreeCell();
		if (freeLocation == null)
			return;

		final CloneRule rule = phenotype.findCloneRule(glucose);
		if (handler.hasChance(rule.getProbability())) {
			if (log.isDebugEnabled())
				log.debug(this + " cloning");
			consume(cloneEnergy);
			final LivingBeing clone = new LivingBeing();
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
		final double availableEnergy = computeAvailableEnergy();
		final double moveEnergy = handler.getMoveEnergy();
		if (availableEnergy <= moveEnergy || !cell.hasFreeNeighbors()) {
			return;
		}

		final MoveActionContext data = findMoveRule();
		if (data == null)
			return;
		consume(moveEnergy);
		moveTo(data.getTarget());
	}

	/**
	 * @param handler
	 *            the handler to set
	 */
	public void setHandler(final SimulationHandler handler) {
		this.handler = handler;
	}

	/**
	 * Synthesis
	 * 
	 * @param time
	 */
	private void syntetize(final double time) {
		final double glucose = Math.min(cell.computeSynthesisLimit(time), time
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
	public void update(final double time) {
		if (cell == null)
			return;
		syntetize(time);
		consume(time * handler.getConsumptionRate());
	}
}
