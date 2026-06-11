package com.skyblock.skills;

import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Manages per-player skill XP.
 *
 * <p>XP is stored in a {@link HashMap} keyed by player UUID, mapping each
 * {@link SkillType} to the total XP earned in that skill. All access goes
 * through {@code synchronized} methods so XP grants are safe to call from
 * any thread.</p>
 */
public final class SkillManager {

    private final HashMap<UUID, Map<SkillType, Double>> xpMap = new HashMap<>();

    /**
     * Adds XP to the player's skill.
     *
     * @param playerId the player's UUID
     * @param skill    the skill to grant XP in
     * @param amount   the amount of XP to add, must be non-negative
     * @return the player's total XP in the skill after the grant
     */
    public synchronized double addXp(UUID playerId, SkillType skill, double amount) {
        requireNonNegative(amount);
        Map<SkillType, Double> skillXp = xpMap.computeIfAbsent(playerId, id -> new EnumMap<>(SkillType.class));
        double updated = skillXp.getOrDefault(skill, 0.0) + amount;
        skillXp.put(skill, updated);
        return updated;
    }

    /**
     * Returns the player's total XP in the given skill, or {@code 0} if the
     * player has earned none.
     *
     * @param playerId the player's UUID
     * @param skill    the skill to look up
     * @return the total XP earned in the skill
     */
    public synchronized double getXp(UUID playerId, SkillType skill) {
        Map<SkillType, Double> skillXp = xpMap.get(playerId);
        return skillXp != null ? skillXp.getOrDefault(skill, 0.0) : 0.0;
    }

    /**
     * Sets the player's total XP in the given skill directly.
     *
     * @param playerId the player's UUID
     * @param skill    the skill to update
     * @param amount   the new total XP, must be non-negative
     */
    public synchronized void setXp(UUID playerId, SkillType skill, double amount) {
        requireNonNegative(amount);
        xpMap.computeIfAbsent(playerId, id -> new EnumMap<>(SkillType.class)).put(skill, amount);
    }

    /**
     * Returns a snapshot of all skill XP the player has earned. The returned
     * map is a copy; changes to it do not affect the manager.
     *
     * @param playerId the player's UUID
     * @return the player's XP per skill, empty if the player has earned none
     */
    public synchronized Map<SkillType, Double> getAllXp(UUID playerId) {
        Map<SkillType, Double> skillXp = xpMap.get(playerId);
        if (skillXp == null) {
            return Collections.emptyMap();
        }
        return new EnumMap<>(skillXp);
    }

    /**
     * Removes all skill XP for the player (e.g. on data wipe).
     *
     * @param playerId the player's UUID
     * @return {@code true} if the player had any XP recorded
     */
    public synchronized boolean clear(UUID playerId) {
        return xpMap.remove(playerId) != null;
    }

    private static void requireNonNegative(double amount) {
        if (amount < 0 || Double.isNaN(amount)) {
            throw new IllegalArgumentException("amount must be non-negative: " + amount);
        }
    }
}
