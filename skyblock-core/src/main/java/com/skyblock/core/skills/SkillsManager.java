package com.skyblock.core.skills;

import java.io.File;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Singleton facade over {@link SkillManager}.
 *
 * <p>Exposes the same XP / level API under the {@code SkillsManager} name used
 * by other modules, delegating every call to the underlying {@link SkillManager}
 * singleton so there is a single source of truth for skill data.</p>
 */
public final class SkillsManager {

    /** Canonical skill list used by this facade. */
    public enum SkillType {
        FARMING("Farming"),
        MINING("Mining"),
        COMBAT("Combat"),
        FISHING("Fishing"),
        FORAGING("Foraging"),
        ENCHANTING("Enchanting"),
        ALCHEMY("Alchemy"),
        TAMING("Taming"),
        CARPENTRY("Carpentry"),
        RUNECRAFTING("Runecrafting"),
        SOCIAL("Social");

        private final String displayName;

        SkillType(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }

        /** Maps this value to the underlying {@link SkillManager.SkillType}. */
        public SkillManager.SkillType toSkillType() {
            return SkillManager.SkillType.valueOf(this.name());
        }
    }

    /**
     * XP required to reach each level (index 0 = level 1) for each skill.
     * Main skills (Farming, Mining, Combat, Foraging, Fishing, Enchanting, Alchemy, Taming)
     * cap at level 60; values match the standard SkyBlock XP table.
     */
    public static final Map<String, long[]> SKILL_XP_TABLE;

    static {
        long[] standard = {
                50, 125, 200, 300, 500, 750, 1000, 1500, 2000, 3500,
                5000, 7500, 10000, 15000, 20000, 30000, 50000, 75000, 100000, 150000,
                200000, 300000, 400000, 500000, 600000, 700000, 800000, 900000, 1000000, 1100000,
                1200000, 1300000, 1400000, 1500000, 1600000, 1700000, 1800000, 1900000, 2000000, 2100000,
                2200000, 2300000, 2400000, 2500000, 2600000, 2750000, 2900000, 3100000, 3400000, 3700000,
                4200000, 4700000, 5200000, 5700000, 6200000, 6700000, 7200000, 7700000, 8200000, 8700000
        };
        Map<String, long[]> m = new LinkedHashMap<>();
        m.put("Farming",    standard.clone());
        m.put("Mining",     standard.clone());
        m.put("Combat",     standard.clone());
        m.put("Foraging",   standard.clone());
        m.put("Fishing",    standard.clone());
        m.put("Enchanting", standard.clone());
        m.put("Alchemy",    standard.clone());
        m.put("Taming",     standard.clone());
        SKILL_XP_TABLE = Collections.unmodifiableMap(m);
    }

    private static final SkillsManager INSTANCE = new SkillsManager();

    private final SkillManager delegate = SkillManager.getInstance();

    private SkillsManager() {
    }

    public static SkillsManager getInstance() {
        return INSTANCE;
    }

    public void load(File dataFolder) {
        delegate.load(dataFolder);
    }

    public void save(File dataFolder) {
        delegate.save(dataFolder);
    }

    public double addXp(UUID playerId, SkillType skill, double amount) {
        return delegate.addXp(playerId, skill.toSkillType(), amount);
    }

    public double getXp(UUID playerId, SkillType skill) {
        return delegate.getXp(playerId, skill.toSkillType());
    }

    public int getLevel(UUID playerId, SkillType skill) {
        return delegate.getLevel(playerId, skill.toSkillType());
    }
}
