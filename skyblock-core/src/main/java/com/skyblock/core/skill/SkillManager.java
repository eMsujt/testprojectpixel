package com.skyblock.core.skill;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * Singleton tracking each player's XP and level for every {@link SkillType}.
 * Uses {@link SkillLevelManager} for the XP-to-level table.
 */
public final class SkillManager {

    public enum SkillType {
        COMBAT("Combat"),
        FARMING("Farming"),
        MINING("Mining"),
        FORAGING("Foraging"),
        FISHING("Fishing"),
        ENCHANTING("Enchanting"),
        ALCHEMY("Alchemy"),
        TAMING("Taming"),
        CARPENTRY("Carpentry"),
        RUNECRAFTING("Runecrafting");

        private final String displayName;

        SkillType(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    public enum SkyBlockSkill {
        FARMING("Farming"),
        MINING("Mining"),
        COMBAT("Combat"),
        FISHING("Fishing"),
        FORAGING("Foraging"),
        ENCHANTING("Enchanting"),
        ALCHEMY("Alchemy"),
        TAMING("Taming"),
        CARPENTRY("Carpentry"),
        RUNECRAFTING("Runecrafting");

        private final String displayName;

        SkyBlockSkill(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }

        public SkillType toSkillType() {
            return SkillType.valueOf(this.name());
        }
    }

    public enum Skill {
        FARMING("Farming",      60),
        MINING("Mining",        60),
        COMBAT("Combat",        60),
        FORAGING("Foraging",    50),
        FISHING("Fishing",      50),
        ENCHANTING("Enchanting", 60),
        ALCHEMY("Alchemy",      50),
        TAMING("Taming",        50),
        CARPENTRY("Carpentry",  50),
        RUNECRAFTING("Runecrafting", 25);

        private final String displayName;
        private final int maxLevel;

        Skill(String displayName, int maxLevel) {
            this.displayName = displayName;
            this.maxLevel = maxLevel;
        }

        public String getDisplayName() { return displayName; }
        public int getMaxLevel() { return maxLevel; }
    }

    public static final int MAX_LEVEL = SkillLevelManager.MAX_LEVEL;

    private static final SkillManager INSTANCE = new SkillManager();

    private final Map<UUID, Map<SkillType, Long>> xpMap = new HashMap<>();

    private SkillManager() {}

    public static SkillManager getInstance() {
        return INSTANCE;
    }

    /**
     * Adds XP to the player's total for the given skill.
     *
     * @param playerId player receiving XP
     * @param skill    skill being progressed
     * @param amount   XP to add, must not be negative
     * @return new total XP for the skill
     */
    public long addXp(UUID playerId, SkillType skill, long amount) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(skill, "skill");
        if (amount < 0) {
            throw new IllegalArgumentException("amount must not be negative, got " + amount);
        }
        Map<SkillType, Long> xp = xpMap.computeIfAbsent(
                playerId, id -> new EnumMap<>(SkillType.class));
        return xp.merge(skill, amount, Long::sum);
    }

    /**
     * Returns the player's current total XP for the given skill.
     *
     * @param playerId player to look up
     * @param skill    skill to look up
     * @return total XP, {@code 0} if none recorded
     */
    public long getXp(UUID playerId, SkillType skill) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(skill, "skill");
        Map<SkillType, Long> xp = xpMap.get(playerId);
        return xp == null ? 0L : xp.getOrDefault(skill, 0L);
    }

    /**
     * Returns the player's current level (1–{@value #MAX_LEVEL}) for the given skill.
     *
     * @param playerId player to look up
     * @param skill    skill to look up
     * @return skill level
     */
    public int getLevel(UUID playerId, SkillType skill) {
        return SkillLevelManager.getInstance().levelForXp(getXp(playerId, skill));
    }
}
