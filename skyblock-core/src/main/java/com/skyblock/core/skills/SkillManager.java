package com.skyblock.core.skills;

import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * Singleton tracking each player's XP and level for every {@link SkillType}.
 *
 * <p>Level {@code n} requires {@code 50 * n^2} cumulative XP (same curve as
 * {@code FishingManager}). Max level is 50 for all skills.</p>
 *
 * <p>Not thread-safe; synchronize externally if needed.</p>
 */
public final class SkillManager {

    /** Every skill tracked in SkyBlock. */
    public enum SkillType {
        FARMING("Farming"),
        MINING("Mining"),
        COMBAT("Combat"),
        FORAGING("Foraging"),
        FISHING("Fishing"),
        ENCHANTING("Enchanting"),
        ALCHEMY("Alchemy"),
        TAMING("Taming"),
        CARPENTRY("Carpentry"),
        RUNECRAFTING("Runecrafting"),
        SOCIAL("Social"),
        DUNGEONEERING("Dungeoneering");

        /** Human-readable display name. */
        public final String displayName;

        SkillType(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    public static final int MAX_LEVEL = 50;

    private static final SkillManager INSTANCE = new SkillManager();

    /** Per-player XP map: player → (skill → total XP). */
    private final Map<UUID, Map<SkillType, Double>> xpMap = new HashMap<>();
    /** Per-player level cache: player → (skill → level). */
    private final Map<UUID, Map<SkillType, Integer>> levelMap = new HashMap<>();

    private SkillManager() {
    }

    /**
     * Returns the single shared {@code SkillManager} instance.
     *
     * @return the singleton instance
     */
    public static SkillManager getInstance() {
        return INSTANCE;
    }

    /**
     * Adds XP to the player's total for the given skill and updates the level.
     *
     * @param playerId the player receiving XP
     * @param skill    the skill being progressed
     * @param amount   XP to add, must not be negative
     * @return the player's new total XP for the skill
     * @throws IllegalArgumentException if {@code amount} is negative
     */
    public double addXp(UUID playerId, SkillType skill, double amount) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(skill, "skill");
        if (amount < 0) {
            throw new IllegalArgumentException("amount must not be negative, got " + amount);
        }
        Map<SkillType, Double> xp = xpMap.computeIfAbsent(
                playerId, id -> new EnumMap<>(SkillType.class));
        double total = xp.merge(skill, amount, Double::sum);
        levelMap.computeIfAbsent(playerId, id -> new EnumMap<>(SkillType.class))
                .put(skill, computeLevel(total));
        return total;
    }

    /**
     * Returns the player's current XP for the given skill.
     *
     * @param playerId the player to look up
     * @param skill    the skill to look up
     * @return total XP, {@code 0.0} if none recorded
     */
    public double getXp(UUID playerId, SkillType skill) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(skill, "skill");
        Map<SkillType, Double> xp = xpMap.get(playerId);
        return xp == null ? 0.0 : xp.getOrDefault(skill, 0.0);
    }

    /**
     * Returns the player's current level for the given skill (1–{@value #MAX_LEVEL}).
     *
     * @param playerId the player to look up
     * @param skill    the skill to look up
     * @return skill level
     */
    public int getLevel(UUID playerId, SkillType skill) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(skill, "skill");
        Map<SkillType, Integer> levels = levelMap.get(playerId);
        return levels == null ? 1 : levels.getOrDefault(skill, 1);
    }

    public void load(File dataFolder) {
        File file = new File(dataFolder, "skills.yml");
        if (!file.exists()) {
            return;
        }
        YamlConfiguration cfg = YamlConfiguration.loadConfiguration(file);
        xpMap.clear();
        levelMap.clear();
        for (String key : cfg.getKeys(false)) {
            try {
                UUID uuid = UUID.fromString(key);
                if (cfg.isConfigurationSection(key + ".xp")) {
                    Map<SkillType, Double> xp = new EnumMap<>(SkillType.class);
                    for (String typeName : cfg.getConfigurationSection(key + ".xp").getKeys(false)) {
                        try {
                            SkillType type = SkillType.valueOf(typeName);
                            xp.put(type, cfg.getDouble(key + ".xp." + typeName, 0.0));
                        } catch (IllegalArgumentException ignored) {
                            // skip unknown skill types
                        }
                    }
                    if (!xp.isEmpty()) {
                        xpMap.put(uuid, xp);
                    }
                }
                if (cfg.isConfigurationSection(key + ".level")) {
                    Map<SkillType, Integer> levels = new EnumMap<>(SkillType.class);
                    for (String typeName : cfg.getConfigurationSection(key + ".level").getKeys(false)) {
                        try {
                            SkillType type = SkillType.valueOf(typeName);
                            levels.put(type, cfg.getInt(key + ".level." + typeName, 1));
                        } catch (IllegalArgumentException ignored) {
                            // skip unknown skill types
                        }
                    }
                    if (!levels.isEmpty()) {
                        levelMap.put(uuid, levels);
                    }
                }
            } catch (IllegalArgumentException ignored) {
                // skip malformed entries
            }
        }
    }

    public void save(File dataFolder) {
        File file = new File(dataFolder, "skills.yml");
        YamlConfiguration cfg = new YamlConfiguration();
        for (Map.Entry<UUID, Map<SkillType, Double>> entry : xpMap.entrySet()) {
            String key = entry.getKey().toString();
            for (Map.Entry<SkillType, Double> xp : entry.getValue().entrySet()) {
                cfg.set(key + ".xp." + xp.getKey().name(), xp.getValue());
            }
        }
        for (Map.Entry<UUID, Map<SkillType, Integer>> entry : levelMap.entrySet()) {
            String key = entry.getKey().toString();
            for (Map.Entry<SkillType, Integer> lv : entry.getValue().entrySet()) {
                cfg.set(key + ".level." + lv.getKey().name(), lv.getValue());
            }
        }
        try {
            cfg.save(file);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save skills.yml", e);
        }
    }

    /**
     * Computes the level for the given total XP.
     * Formula: level {@code n} requires {@code 50 * n^2} cumulative XP.
     *
     * @param totalXp total accumulated XP
     * @return level between 1 and {@value #MAX_LEVEL}
     */
    private static int computeLevel(double totalXp) {
        int level = 1;
        while (level < MAX_LEVEL) {
            double threshold = 50.0 * (level + 1) * (level + 1);
            if (totalXp < threshold) {
                break;
            }
            level++;
        }
        return level;
    }
}
