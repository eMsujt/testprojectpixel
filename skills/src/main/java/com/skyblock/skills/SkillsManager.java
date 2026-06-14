package com.skyblock.skills;

import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Facade over {@link SkillManager} providing the canonical entry point for
 * skill XP and level operations used by other modules.
 */
public final class SkillsManager {

    /** Cumulative XP required to reach each level (index 0 = level 1) for each skill. */
    public static final Map<String, long[]> XP_REQUIREMENTS;

    static {
        long[] cumulative = {
                50L, 175L, 375L, 675L, 1_175L, 1_925L, 2_925L, 4_425L, 6_425L, 9_925L,
                14_925L, 22_425L, 32_425L, 47_425L, 67_425L, 97_425L, 147_425L, 222_425L, 322_425L, 472_425L,
                672_425L, 972_425L, 1_372_425L, 1_872_425L, 2_472_425L, 3_172_425L, 3_972_425L, 4_872_425L, 5_872_425L, 6_972_425L,
                8_172_425L, 9_472_425L, 10_872_425L, 12_372_425L, 13_972_425L, 15_672_425L, 17_472_425L, 19_372_425L, 21_372_425L, 23_472_425L,
                25_672_425L, 27_972_425L, 30_372_425L, 32_872_425L, 35_472_425L, 38_222_425L, 41_122_425L, 44_222_425L, 47_622_425L, 51_322_425L,
                55_522_425L, 60_222_425L, 65_422_425L, 71_122_425L, 77_322_425L, 84_022_425L, 91_222_425L, 98_922_425L, 107_122_425L, 115_822_425L
        };
        Map<String, long[]> m = new LinkedHashMap<>();
        m.put("Farming",    cumulative.clone());
        m.put("Mining",     cumulative.clone());
        m.put("Combat",     cumulative.clone());
        m.put("Foraging",   cumulative.clone());
        m.put("Fishing",    cumulative.clone());
        m.put("Enchanting", cumulative.clone());
        m.put("Alchemy",    cumulative.clone());
        m.put("Taming",     cumulative.clone());
        m.put("Carpentry",  cumulative.clone());
        XP_REQUIREMENTS = Collections.unmodifiableMap(m);
    }

    private final SkillManager delegate = new SkillManager();
    private final Map<UUID, List<String>> skillsHistory = new HashMap<>();

    /**
     * Adds XP to the player's progress for the given skill.
     *
     * @param playerId the player gaining XP
     * @param skill    the skill being progressed
     * @param amount   the XP to add, must not be negative
     * @return the player's total XP for the skill after the addition
     */
    public double addExperience(UUID playerId, SkillType skill, double amount) {
        int levelBefore = delegate.getLevel(playerId, skill);
        double total = delegate.addExperience(playerId, skill, amount);
        recordSkillEvent(playerId, "Gained " + amount + " XP in " + skill.name());
        int levelAfter = delegate.getLevel(playerId, skill);
        if (levelAfter > levelBefore) {
            recordSkillEvent(playerId, "Leveled up " + skill.name() + " to level " + levelAfter);
        }
        return total;
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

    public void load(File dataFolder) {
        delegate.load(dataFolder);
        File file = new File(dataFolder, "skills-history.yml");
        skillsHistory.clear();
        if (file.exists()) {
            YamlConfiguration cfg = YamlConfiguration.loadConfiguration(file);
            if (cfg.isConfigurationSection("skillsHistory")) {
                for (String key : cfg.getConfigurationSection("skillsHistory").getKeys(false)) {
                    try {
                        List<String> entries = cfg.getStringList("skillsHistory." + key);
                        if (!entries.isEmpty()) {
                            skillsHistory.put(UUID.fromString(key), new ArrayList<>(entries));
                        }
                    } catch (IllegalArgumentException ignored) {
                        // skip malformed entries
                    }
                }
            }
        }
    }

    public void save(File dataFolder) {
        delegate.save(dataFolder);
        File file = new File(dataFolder, "skills-history.yml");
        YamlConfiguration cfg = new YamlConfiguration();
        for (Map.Entry<UUID, List<String>> entry : skillsHistory.entrySet()) {
            if (!entry.getValue().isEmpty()) {
                cfg.set("skillsHistory." + entry.getKey().toString(), entry.getValue());
            }
        }
        try {
            cfg.save(file);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save skills-history.yml", e);
        }
    }

    public void recordSkillEvent(UUID playerUuid, String summary) {
        skillsHistory.computeIfAbsent(playerUuid, k -> new ArrayList<>()).add(summary);
    }

    public List<String> getSkillsHistory(UUID playerUuid) {
        return Collections.unmodifiableList(skillsHistory.getOrDefault(playerUuid, Collections.emptyList()));
    }

    public Map<UUID, List<String>> getAllSkillsHistory() {
        return Collections.unmodifiableMap(skillsHistory);
    }

    public String getSkillsStats(UUID playerId) {
        StringBuilder sb = new StringBuilder("Skills Stats:");
        for (SkillType skill : SkillType.values()) {
            int level = getLevel(playerId, skill);
            long xp = (long) getExperience(playerId, skill);
            String name = skill.name().charAt(0) + skill.name().substring(1).toLowerCase();
            sb.append(" | ").append(name).append(" Lvl ").append(level).append(" (").append(xp).append(" XP)");
        }
        return sb.toString();
    }
}
