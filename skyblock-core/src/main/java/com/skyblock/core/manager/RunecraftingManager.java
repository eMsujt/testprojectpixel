package com.skyblock.core.manager;

import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class RunecraftingManager {

    public enum RuneType {
        FIERY(3),
        ICY(3),
        GOLDEN(3),
        SMOKY(3),
        SLIMY(3),
        BLOOD(3),
        CLOUDY(3),
        ENCHANT(3),
        GRAND(3),
        AMBER(3),
        KINETIC(3),
        SPIRIT(3),
        WITHER(3),
        ZOMBIE(3),
        SPIDER(3),
        WOLF(3),
        ENDERMAN(3),
        BLAZE(3),
        PIGMAN(3),
        NETHER(3),
        RAINBOW(3),
        SPARKLING(3),
        THUNDER(3),
        DRAGON(3),
        CRYSTAL(3);

        private final int maxLevel;

        RuneType(int maxLevel) {
            this.maxLevel = maxLevel;
        }

        public int getMaxLevel() {
            return maxLevel;
        }
    }

    /** Cumulative XP required to reach each runecrafting skill level (index = level - 1, 25 levels). */
    public static final int[] XP_TABLE = {
            50, 150, 275, 435, 635,
            885, 1_200, 1_600, 2_100, 2_700,
            3_400, 4_200, 5_100, 6_100, 7_200,
            8_400, 9_750, 11_250, 12_900, 14_700,
            16_700, 18_950, 21_450, 24_200, 27_200
    };

    public static final int MAX_SKILL_LEVEL = XP_TABLE.length;

    private static final RunecraftingManager INSTANCE = new RunecraftingManager();

    /** Per-player runecrafting skill XP. */
    private final Map<UUID, Long> xpMap = new HashMap<>();
    /** Per-player rune XP, keyed by rune type. */
    private final Map<UUID, Map<RuneType, Long>> runeXp = new HashMap<>();
    /** Per-player rune unlock counts. */
    private final Map<UUID, Map<RuneType, Integer>> runeCount = new HashMap<>();

    private RunecraftingManager() {}

    public static RunecraftingManager getInstance() {
        return INSTANCE;
    }

    // --- skill XP ---

    public long addSkillXp(UUID playerId, long amount) {
        if (amount < 0) throw new IllegalArgumentException("amount must not be negative");
        long total = xpMap.getOrDefault(playerId, 0L) + amount;
        xpMap.put(playerId, total);
        return total;
    }

    public long getSkillXp(UUID playerId) {
        return xpMap.getOrDefault(playerId, 0L);
    }

    public int getSkillLevel(UUID playerId) {
        long xp = getSkillXp(playerId);
        int level = 0;
        while (level < MAX_SKILL_LEVEL && xp >= XP_TABLE[level]) {
            level++;
        }
        return level;
    }

    // --- rune XP ---

    public long addRuneXp(UUID playerId, RuneType type, long amount) {
        if (amount < 0) throw new IllegalArgumentException("amount must not be negative");
        Map<RuneType, Long> map = runeXp.computeIfAbsent(playerId, id -> new EnumMap<>(RuneType.class));
        long total = map.getOrDefault(type, 0L) + amount;
        map.put(type, total);
        return total;
    }

    public long getRuneXp(UUID playerId, RuneType type) {
        Map<RuneType, Long> map = runeXp.get(playerId);
        return map == null ? 0L : map.getOrDefault(type, 0L);
    }

    public int getRuneLevel(UUID playerId, RuneType type) {
        long xp = getRuneXp(playerId, type);
        int max = type.getMaxLevel();
        // Each rune level costs 500 * level XP; thresholds: L1=500, L2=1500, L3=3000
        int level = 0;
        long cumulative = 0;
        for (int l = 1; l <= max; l++) {
            cumulative += 500L * l;
            if (xp >= cumulative) level = l;
            else break;
        }
        return level;
    }

    // --- rune count (owned runes) ---

    public int addRune(UUID playerId, RuneType type, int count) {
        if (count < 0) throw new IllegalArgumentException("count must not be negative");
        Map<RuneType, Integer> map = runeCount.computeIfAbsent(playerId, id -> new EnumMap<>(RuneType.class));
        int total = map.getOrDefault(type, 0) + count;
        map.put(type, total);
        return total;
    }

    public int getRuneCount(UUID playerId, RuneType type) {
        Map<RuneType, Integer> map = runeCount.get(playerId);
        return map == null ? 0 : map.getOrDefault(type, 0);
    }

    public Map<RuneType, Integer> getAllRuneCounts(UUID playerId) {
        Map<RuneType, Integer> map = runeCount.get(playerId);
        return map == null ? Collections.emptyMap() : Collections.unmodifiableMap(map);
    }

    public boolean reset(UUID playerId) {
        boolean had = xpMap.remove(playerId) != null;
        had |= runeXp.remove(playerId) != null;
        had |= runeCount.remove(playerId) != null;
        return had;
    }

    public void load(File dataFolder) {
        File file = new File(dataFolder, "runecrafting.yml");
        if (!file.exists()) return;
        YamlConfiguration cfg = YamlConfiguration.loadConfiguration(file);
        xpMap.clear();
        runeXp.clear();
        runeCount.clear();
        boolean legacy = false;
        for (String key : cfg.getKeys(false)) {
            try {
                UUID uuid = UUID.fromString(key);
                // Prefer the current 'xpMap' key, fall back to the legacy 'skillXp' key.
                if (cfg.contains(key + ".xpMap")) {
                    xpMap.put(uuid, cfg.getLong(key + ".xpMap", 0L));
                } else {
                    xpMap.put(uuid, cfg.getLong(key + ".skillXp", 0L));
                    if (cfg.contains(key + ".skillXp")) legacy = true;
                }
                if (cfg.isConfigurationSection(key + ".runeXp")) {
                    Map<RuneType, Long> rxMap = new EnumMap<>(RuneType.class);
                    for (String typeName : cfg.getConfigurationSection(key + ".runeXp").getKeys(false)) {
                        try {
                            rxMap.put(RuneType.valueOf(typeName), cfg.getLong(key + ".runeXp." + typeName, 0L));
                        } catch (IllegalArgumentException ignored) {}
                    }
                    if (!rxMap.isEmpty()) runeXp.put(uuid, rxMap);
                }
                if (cfg.isConfigurationSection(key + ".runeCount")) {
                    Map<RuneType, Integer> rcMap = new EnumMap<>(RuneType.class);
                    for (String typeName : cfg.getConfigurationSection(key + ".runeCount").getKeys(false)) {
                        try {
                            rcMap.put(RuneType.valueOf(typeName), cfg.getInt(key + ".runeCount." + typeName, 0));
                        } catch (IllegalArgumentException ignored) {}
                    }
                    if (!rcMap.isEmpty()) runeCount.put(uuid, rcMap);
                }
            } catch (IllegalArgumentException ignored) {}
        }
        // Migrate legacy 'skillXp' keys to the current format on disk.
        if (legacy) save(dataFolder);
    }

    public void save(File dataFolder) {
        File file = new File(dataFolder, "runecrafting.yml");
        YamlConfiguration cfg = new YamlConfiguration();
        for (Map.Entry<UUID, Long> entry : xpMap.entrySet()) {
            cfg.set(entry.getKey().toString() + ".xpMap", entry.getValue());
        }
        for (Map.Entry<UUID, Map<RuneType, Long>> entry : runeXp.entrySet()) {
            String key = entry.getKey().toString();
            for (Map.Entry<RuneType, Long> rx : entry.getValue().entrySet()) {
                cfg.set(key + ".runeXp." + rx.getKey().name(), rx.getValue());
            }
        }
        for (Map.Entry<UUID, Map<RuneType, Integer>> entry : runeCount.entrySet()) {
            String key = entry.getKey().toString();
            for (Map.Entry<RuneType, Integer> rc : entry.getValue().entrySet()) {
                cfg.set(key + ".runeCount." + rc.getKey().name(), rc.getValue());
            }
        }
        try {
            cfg.save(file);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save runecrafting.yml", e);
        }
    }
}
