package com.skyblock.skills;

import java.util.UUID;

/**
 * Facade over {@link SkillManager} providing the canonical entry point for
 * skill XP and level operations used by other modules.
 */
public final class SkillsManager {

    private final SkillManager delegate = new SkillManager();

    /**
     * Adds XP to the player's progress for the given skill.
     *
     * @param playerId the player gaining XP
     * @param skill    the skill being progressed
     * @param amount   the XP to add, must not be negative
     * @return the player's total XP for the skill after the addition
     */
    public double addExperience(UUID playerId, SkillType skill, double amount) {
        return delegate.addExperience(playerId, skill, amount);
    }

    /**
     * Returns how much XP the player has earned in the given skill.
     *
     * @param playerId the player to look up
     * @param skill    the skill to look up
     * @return the total XP earned, {@code 0} if the player has none
     */
    public double getExperience(UUID playerId, SkillType skill) {
        return delegate.getExperience(playerId, skill);
    }

    /**
     * Returns the level the player has reached in the given skill.
     *
     * @param playerId the player to look up
     * @param skill    the skill to look up
     * @return the level between {@code 0} and {@link SkillManager#MAX_LEVEL}
     */
    public int getLevel(UUID playerId, SkillType skill) {
        return delegate.getLevel(playerId, skill);
    }

    /**
     * Returns how much more XP the player needs to reach the next level.
     *
     * @param playerId the player to look up
     * @param skill    the skill to look up
     * @return the missing XP, or {@code 0} if the skill is at max level
     */
    public double getExperienceToNextLevel(UUID playerId, SkillType skill) {
        return delegate.getExperienceToNextLevel(playerId, skill);
    }

    /**
     * Resets all of the player's skill progress back to zero.
     *
     * @param playerId the player to reset
     * @return {@code true} if the player had progress to reset
     */
    public boolean reset(UUID playerId) {
        return delegate.reset(playerId);
    }
}
