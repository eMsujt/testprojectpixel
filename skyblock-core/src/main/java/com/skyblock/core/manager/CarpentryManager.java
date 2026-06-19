package com.skyblock.core.manager;

import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class CarpentryManager {

    /** Carpentry recipes, each gated behind a minimum Carpentry skill level. */
    public enum CarpentryRecipe {
        OAK_DOOR("Oak Door", 1),
        CRAFTING_TABLE("Crafting Table", 1),
        CHEST("Chest", 1),
        ITEM_FRAME("Item Frame", 2),
        ARMOR_STAND("Armor Stand", 3),
        PAINTING("Painting", 4),
        BOOKSHELF("Bookshelf", 5),
        NOTE_BLOCK("Note Block", 6),
        JUKEBOX("Jukebox", 7),
        LOOM("Loom", 8),
        CARTOGRAPHY_TABLE("Cartography Table", 9),
        FLETCHING_TABLE("Fletching Table", 10),
        SMITHING_TABLE("Smithing Table", 12),
        LECTERN("Lectern", 14),
        BREWING_STAND("Brewing Stand", 16),
        BEACON("Beacon", 18),
        ENCHANTING_TABLE("Enchanting Table", 20),
        ANVIL("Anvil", 25),
        END_PORTAL_FRAME("End Portal Frame", 30);

        private final String displayName;
        private final int requiredLevel;

        CarpentryRecipe(String displayName, int requiredLevel) {
            this.displayName = displayName;
            this.requiredLevel = requiredLevel;
        }

        public String getDisplayName() {
            return displayName;
        }

        public int getRequiredLevel() {
            return requiredLevel;
        }
    }

    /** Cumulative XP required to reach each carpentry skill level (index = level - 1, 50 levels). */
    public static final int[] XP_TABLE = {
            50, 150, 300, 500, 750,
            1_050, 1_400, 1_800, 2_250, 2_750,
            3_300, 3_900, 4_550, 5_250, 6_000,
            6_800, 7_650, 8_550, 9_500, 10_500,
            11_550, 12_650, 13_800, 15_000, 16_250,
            17_550, 18_900, 20_300, 21_750, 23_250,
            24_800, 26_400, 28_050, 29_750, 31_500,
            33_300, 35_150, 37_050, 39_000, 41_000,
            43_050, 45_150, 47_300, 49_500, 51_750,
            54_050, 56_400, 58_800, 61_250, 63_750
    };

    public static final int MAX_SKILL_LEVEL = XP_TABLE.length;

    private static final CarpentryManager INSTANCE = new CarpentryManager();

    /** Per-player carpentry skill XP. */
    private final Map<UUID, Long> skillXp = new HashMap<>();
    /** Per-player craft counts, keyed by recipe. */
    private final Map<UUID, Map<CarpentryRecipe, Integer>> craftCount = new HashMap<>();

    private CarpentryManager() {}

    public static CarpentryManager getInstance() {
        return INSTANCE;
    }

    // --- skill XP ---

    public long addSkillXp(UUID playerId, long amount) {
        if (amount < 0) throw new IllegalArgumentException("amount must not be negative");
        long total = skillXp.getOrDefault(playerId, 0L) + amount;
        skillXp.put(playerId, total);
        return total;
    }

    public long getSkillXp(UUID playerId) {
        return skillXp.getOrDefault(playerId, 0L);
    }

    public int getSkillLevel(UUID playerId) {
        long xp = getSkillXp(playerId);
        int level = 0;
        while (level < MAX_SKILL_LEVEL && xp >= XP_TABLE[level]) {
            level++;
        }
        return level;
    }

    // --- recipe unlocks ---

    public boolean isUnlocked(UUID playerId, CarpentryRecipe recipe) {
        return getSkillLevel(playerId) >= recipe.getRequiredLevel();
    }

    /**
     * Crafts a recipe for the player, incrementing the craft count.
     *
     * @return the player's new craft count for that recipe
     * @throws IllegalStateException if the recipe is not yet unlocked
     */
    public int craft(UUID playerId, CarpentryRecipe recipe) {
        if (!isUnlocked(playerId, recipe)) {
            throw new IllegalStateException("recipe " + recipe.name() + " requires Carpentry level "
                    + recipe.getRequiredLevel());
        }
        Map<CarpentryRecipe, Integer> map = craftCount.computeIfAbsent(playerId, id -> new EnumMap<>(CarpentryRecipe.class));
        int total = map.getOrDefault(recipe, 0) + 1;
        map.put(recipe, total);
        return total;
    }

    public int getCraftCount(UUID playerId, CarpentryRecipe recipe) {
        Map<CarpentryRecipe, Integer> map = craftCount.get(playerId);
        return map == null ? 0 : map.getOrDefault(recipe, 0);
    }

    public Map<CarpentryRecipe, Integer> getAllCraftCounts(UUID playerId) {
        Map<CarpentryRecipe, Integer> map = craftCount.get(playerId);
        return map == null ? Collections.emptyMap() : Collections.unmodifiableMap(map);
    }

    public boolean reset(UUID playerId) {
        boolean had = skillXp.remove(playerId) != null;
        had |= craftCount.remove(playerId) != null;
        return had;
    }

    public void load(File dataFolder) {
        File file = new File(dataFolder, "carpentry.yml");
        if (!file.exists()) return;
        YamlConfiguration cfg = YamlConfiguration.loadConfiguration(file);
        skillXp.clear();
        craftCount.clear();
        for (String key : cfg.getKeys(false)) {
            try {
                UUID uuid = UUID.fromString(key);
                skillXp.put(uuid, cfg.getLong(key + ".skillXp", 0L));
                if (cfg.isConfigurationSection(key + ".craftCount")) {
                    Map<CarpentryRecipe, Integer> ccMap = new EnumMap<>(CarpentryRecipe.class);
                    for (String recipeName : cfg.getConfigurationSection(key + ".craftCount").getKeys(false)) {
                        try {
                            ccMap.put(CarpentryRecipe.valueOf(recipeName), cfg.getInt(key + ".craftCount." + recipeName, 0));
                        } catch (IllegalArgumentException ignored) {}
                    }
                    if (!ccMap.isEmpty()) craftCount.put(uuid, ccMap);
                }
            } catch (IllegalArgumentException ignored) {}
        }
    }

    public void save(File dataFolder) {
        File file = new File(dataFolder, "carpentry.yml");
        YamlConfiguration cfg = new YamlConfiguration();
        for (Map.Entry<UUID, Long> entry : skillXp.entrySet()) {
            cfg.set(entry.getKey().toString() + ".skillXp", entry.getValue());
        }
        for (Map.Entry<UUID, Map<CarpentryRecipe, Integer>> entry : craftCount.entrySet()) {
            String key = entry.getKey().toString();
            for (Map.Entry<CarpentryRecipe, Integer> cc : entry.getValue().entrySet()) {
                cfg.set(key + ".craftCount." + cc.getKey().name(), cc.getValue());
            }
        }
        try {
            cfg.save(file);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save carpentry.yml", e);
        }
    }
}
