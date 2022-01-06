/**
 *
 */
package org.mmarini.genesis.model;

import java.util.Arrays;

/**
 * @author US00852
 *
 */
public class Phenotype {
    private static final int GLUCOSE_MOVE_COUNT = 4;
    private static final int SYNTHESIS_MOVE_COUNT = 4;
    private static final int DEFENSE_RULE_COUNT = 4;
    private static final int ATTACK_RULE_COUNT = 8;
    private static final int BREED_RULE_COUNT = 3;
    private static final int CLONE_RULE_COUNT = 3;
    private static final int SYNTHESIS_RULE_COUNT = 3;
    private static final int ABSORBING_RULE_COUNT = 2;

    private final double[] synthesisGlucoseLevel;
    private final double[] synthesisRate;
    private final double[] cloneGlucoseLevel;
    private final CloneRule[] cloneRules;
    private final double[] breedGlucoseLevel;
    private final BreedRule[] breedRules;
    private final double[] absorbingProbability;
    private final AttackRule[] attackRules;
    private final double[] defenseEnergyRate;
    private final double[] synthesisMovePreferences;
    private final double[] glucoseMovePreferences;
    private double absorbingLevel;
    private double attackEnergyLevel;
    private double lightPreyEnergyLevel;
    private double heavyPreyEnergyLevel;
    private double attackingEnergyLevel;
    private double defenseEnergyLevel;
    private double synthesisMoveEnergy;
    private double synthesisMoveGridGlucose;
    private double glucoseMoveEnergy;
    private double glucoseMoveGridGlucose;

    /**
     *
     */
    public Phenotype() {
        synthesisGlucoseLevel = new double[SYNTHESIS_RULE_COUNT - 1];
        synthesisRate = new double[SYNTHESIS_RULE_COUNT];
        cloneGlucoseLevel = new double[CLONE_RULE_COUNT - 1];
        breedGlucoseLevel = new double[BREED_RULE_COUNT - 1];
        cloneRules = new CloneRule[CLONE_RULE_COUNT];
        breedRules = new BreedRule[BREED_RULE_COUNT];
        absorbingProbability = new double[ABSORBING_RULE_COUNT];
        attackRules = new AttackRule[ATTACK_RULE_COUNT];
        defenseEnergyRate = new double[DEFENSE_RULE_COUNT];
        synthesisMovePreferences = new double[SYNTHESIS_MOVE_COUNT];
        glucoseMovePreferences = new double[GLUCOSE_MOVE_COUNT];

        for (int i = 0; i < CLONE_RULE_COUNT; ++i)
            cloneRules[i] = new CloneRule();

        for (int i = 0; i < BREED_RULE_COUNT; ++i)
            breedRules[i] = new BreedRule();

        for (int i = 0; i < ATTACK_RULE_COUNT; ++i)
            attackRules[i] = new AttackRule();
    }

    /**
     *
     * @param glucose
     * @return
     */
    public double computeSynthesisRate(double glucose) {
        for (int i = 0; i < SYNTHESIS_RULE_COUNT - 1; ++i) {
            if (glucose < synthesisGlucoseLevel[i])
                return synthesisRate[i];
        }
        return synthesisRate[SYNTHESIS_RULE_COUNT - 1];
    }

    /**
     *
     * @param lightWeight
     * @param heavyWeight
     * @param availableEnergy
     * @return
     */
    public AttackRule findAttackRule(double lightWeight, double heavyWeight,
                                     double availableEnergy) {

        int idx = 0;
        if (heavyWeight >= heavyPreyEnergyLevel)
            idx += 4;
        if (lightWeight >= lightPreyEnergyLevel)
            idx += 2;
        if (availableEnergy >= attackEnergyLevel)
            ++idx;
        return attackRules[idx];
    }

    /**
     *
     * @param glucose
     * @return
     */
    public BreedRule findBreedRule(double glucose) {
        for (int i = 0; i < BREED_RULE_COUNT - 1; ++i) {
            if (glucose < breedGlucoseLevel[i])
                return breedRules[i];
        }
        return breedRules[BREED_RULE_COUNT - 1];
    }

    /**
     *
     * @param glucose
     * @return
     */
    public CloneRule findCloneRule(double glucose) {
        for (int i = 0; i < CLONE_RULE_COUNT - 1; ++i) {
            if (glucose < cloneGlucoseLevel[i])
                return cloneRules[i];
        }
        return cloneRules[CLONE_RULE_COUNT - 1];
    }

    /**
     *
     * @param attackEnergy
     * @param defenseEnergy
     * @return
     */
    public double findDefenseRate(double attackEnergy, double defenseEnergy) {
        int idx = 0;
        if (attackEnergy >= attackingEnergyLevel)
            idx += 2;
        if (defenseEnergy >= defenseEnergyLevel)
            ++idx;
        return defenseEnergyRate[idx];
    }

    /**
     *
     * @param energy
     * @param glucose
     * @return
     */
    public double findGlucoseMoveRule(double energy, double glucose) {
        int idx = 0;
        if (energy > glucoseMoveEnergy)
            idx += 2;
        if (glucose > glucoseMoveGridGlucose)
            ++idx;
        return glucoseMovePreferences[idx];
    }

    /**
     *
     * @param energy
     * @param synthesisGlucose
     * @return
     */
    public double findSyntesisMoveRule(double energy, double synthesisGlucose) {
        int idx = 0;
        if (energy > synthesisMoveEnergy)
            idx += 2;
        if (synthesisGlucose > synthesisMoveGridGlucose)
            ++idx;
        return synthesisMovePreferences[idx];
    }

    /**
     *
     * @param genotype
     * @param parameters
     */
    public void generate(Genotype genotype, SimulationParameters parameters) {
        int idx = 0;
        for (int i = 0; i < SYNTHESIS_RULE_COUNT - 1; ++i) {
            int code = genotype.getCode(idx);
            synthesisGlucoseLevel[i] = parameters.generateGlucoseLevel(code);
            ++idx;
        }
        for (int i = 0; i < SYNTHESIS_RULE_COUNT; ++i) {
            int code = genotype.getCode(idx);
            synthesisRate[i] = parameters.generateSynthesisRate(code);
            ++idx;
        }

        for (int i = 0; i < CLONE_RULE_COUNT - 1; ++i) {
            int code = genotype.getCode(idx);
            cloneGlucoseLevel[i] = parameters.generateGlucoseLevel(code);
            ++idx;
        }
        for (CloneRule rule : cloneRules) {
            int code = genotype.getCode(idx);
            double value = parameters.generateProbability(code);
            rule.setProbability(value);
            ++idx;

            code = genotype.getCode(idx);
            value = parameters.generateRate(code);
            rule.setGlucoseRate(value);
            ++idx;
        }

        for (int i = 0; i < BREED_RULE_COUNT - 1; ++i) {
            int code = genotype.getCode(idx);
            breedGlucoseLevel[i] = parameters.generateGlucoseLevel(code);
            ++idx;
        }
        for (BreedRule rule : breedRules) {
            int code = genotype.getCode(idx);
            double value = parameters.generateProbability(code);
            rule.setProbability(value);
            ++idx;

            code = genotype.getCode(idx);
            value = parameters.generateRate(code);
            rule.setGlucoseRate(value);
            ++idx;

        }

        absorbingLevel = parameters.generateRate(genotype.getCode(idx++));
        for (int i = 0; i < ABSORBING_RULE_COUNT; ++i) {
            int code = genotype.getCode(idx);
            double value = parameters.generateProbability(code);
            absorbingProbability[i] = value;
            ++idx;
        }

        heavyPreyEnergyLevel = parameters.generateGlucoseLevel(genotype
                .getCode(idx++));
        lightPreyEnergyLevel = parameters.generateGlucoseLevel(genotype
                .getCode(idx++));
        attackEnergyLevel = parameters.generateGlucoseLevel(genotype
                .getCode(idx++));
        for (AttackRule rule : attackRules) {
            int code = genotype.getCode(idx);
            double value = parameters.generateProbability(code);
            rule.setAttackProbability(value);
            ++idx;

            code = genotype.getCode(idx);
            value = parameters.generateProbability(code);
            rule.setHeavyProbability(value);
            ++idx;

            code = genotype.getCode(idx);
            value = parameters.generateRate(code);
            rule.setAttackEnergy(value);
            ++idx;
        }

        attackingEnergyLevel = parameters.generateGlucoseLevel(genotype
                .getCode(idx++));
        defenseEnergyLevel = parameters.generateGlucoseLevel(genotype
                .getCode(idx++));
        for (int i = 0; i < DEFENSE_RULE_COUNT; ++i) {
            int code = genotype.getCode(idx);
            double value = parameters.generateRate(code);
            defenseEnergyRate[i] = value;
            ++idx;
        }

        synthesisMoveEnergy = parameters.generateGlucoseLevel(genotype
                .getCode(idx++));
        synthesisMoveGridGlucose = parameters.generateGlucoseLevel(genotype
                .getCode(idx++));
        for (int i = 0; i < SYNTHESIS_MOVE_COUNT; ++i) {
            int code = genotype.getCode(idx);
            double value = parameters.generateRate(code);
            synthesisMovePreferences[i] = value;
            ++idx;
        }

        glucoseMoveEnergy = parameters.generateGlucoseLevel(genotype
                .getCode(idx++));
        glucoseMoveGridGlucose = parameters.generateGlucoseLevel(genotype
                .getCode(idx++));
        for (int i = 0; i < GLUCOSE_MOVE_COUNT; ++i) {
            int code = genotype.getCode(idx);
            double value = parameters.generateRate(code);
            glucoseMovePreferences[i] = value;
            ++idx;
        }
    }

    /**
     * @return the absorbingLevel
     */
    public double getAbsorbingLevel() {
        return absorbingLevel;
    }

    /**
     *
     * @param index
     * @return
     */
    public double getAbsorbingProbability(int index) {
        return absorbingProbability[index];
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "Phenotype [synthesisGlucoseLevel="
                + Arrays.toString(synthesisGlucoseLevel) + ", synthesisRate="
                + Arrays.toString(synthesisRate) + ", cloneGlucoseLevel="
                + Arrays.toString(cloneGlucoseLevel) + ", cloneRules="
                + Arrays.toString(cloneRules) + ", breedGlucoseLevel="
                + Arrays.toString(breedGlucoseLevel) + ", breedRules="
                + Arrays.toString(breedRules) + "]";
    }
}
