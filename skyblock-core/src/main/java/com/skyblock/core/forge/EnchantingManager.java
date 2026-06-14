package com.skyblock.core.forge;

import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Singleton tracking enchantment levels applied by each player.
 *
 * <p>Enchantment data is stored in memory only; it is not persisted across
 * server restarts in this implementation.</p>
 *
 * <p>Not thread-safe; access from the main server thread only.</p>
 */
public final class EnchantingManager {

    /** Static catalogue: enchant name → {maxLevel, bookshelvesRequired}. */
    public static final Map<String, int[]> ENCHANT_DATA;
    static {
        Map<String, int[]> m = new HashMap<>();
        // Combat
        m.put("SHARPNESS",          new int[]{7, 15});
        m.put("CRITICAL",           new int[]{7, 12});
        m.put("SMITE",              new int[]{7, 10});
        m.put("BANE_OF_ARTHROPODS", new int[]{7, 10});
        m.put("FIRST_STRIKE",       new int[]{4,  8});
        m.put("GIANT_KILLER",       new int[]{7, 12});
        m.put("ENDER_SLAYER",       new int[]{7, 15});
        m.put("DRAGON_HUNTER",      new int[]{5, 15});
        m.put("THUNDERLORD",        new int[]{7, 12});
        m.put("EXECUTE",            new int[]{5, 10});
        // Utility / Special
        m.put("TELEKINESIS",        new int[]{1,  5});
        m.put("LOOTING",            new int[]{4,  8});
        m.put("POWER",              new int[]{5, 10});
        m.put("SMELTING_TOUCH",     new int[]{1,  8});
        m.put("MAGNET",             new int[]{1,  5});
        m.put("LIFE_STEAL",         new int[]{5, 10});
        // Fishing
        m.put("LUCK_OF_THE_SEA",    new int[]{7, 10});
        m.put("ANGLER",             new int[]{6, 12});
        m.put("FRAIL",              new int[]{5, 10});
        m.put("EXPERTISE",          new int[]{10, 15});
        // Farming
        m.put("CULTIVATING",        new int[]{10, 15});
        m.put("GREEN_THUMB",        new int[]{5, 10});
        m.put("HARVESTING",         new int[]{6, 10});
        // Mining / Tool
        m.put("EFFICIENCY",         new int[]{5, 10});
        m.put("FORTUNE",            new int[]{4, 12});
        m.put("SILK_TOUCH",         new int[]{1,  8});
        // Armor
        m.put("PROTECTION",         new int[]{7, 15});
        m.put("THORNS",             new int[]{3,  8});
        m.put("GROWTH",             new int[]{7, 12});
        m.put("FEATHER_FALLING",    new int[]{7, 10});
        m.put("REJUVENATE",         new int[]{5, 12});
        ENCHANT_DATA = Collections.unmodifiableMap(m);
    }

    private static final EnchantingManager INSTANCE = new EnchantingManager();

    /** Per-player enchantment levels keyed by enchantment name (lower-case). */
    private final Map<UUID, Map<String, Integer>> enchantments = new HashMap<>();

    private EnchantingManager() {}

    public static EnchantingManager getInstance() {
        return INSTANCE;
    }

    // -------------------------------------------------------------------------
    // Enchantment tracking
    // -------------------------------------------------------------------------

    /**
     * Sets the enchantment level for {@code enchantName} on the given player.
     *
     * @param playerId    the player's UUID
     * @param enchantName the enchantment identifier (e.g. "sharpness", "fortune")
     * @param level       the enchantment level (must be >= 0)
     */
    public void setEnchantment(UUID playerId, String enchantName, int level) {
        if (playerId == null || enchantName == null || enchantName.isEmpty() || level < 0) {
            return;
        }
        String key = enchantName.toLowerCase();
        if (level == 0) {
            Map<String, Integer> entry = enchantments.get(playerId);
            if (entry != null) {
                entry.remove(key);
            }
            return;
        }
        enchantments.computeIfAbsent(playerId, k -> new HashMap<>()).put(key, level);
    }

    /**
     * Returns the enchantment level the player has for the given enchantment.
     *
     * @param playerId    the player's UUID
     * @param enchantName the enchantment identifier
     * @return enchantment level, or 0 if none recorded
     */
    public int getEnchantmentLevel(UUID playerId, String enchantName) {
        if (playerId == null || enchantName == null) {
            return 0;
        }
        Map<String, Integer> entry = enchantments.get(playerId);
        if (entry == null) {
            return 0;
        }
        return entry.getOrDefault(enchantName.toLowerCase(), 0);
    }

    /**
     * Returns an unmodifiable view of all enchantments for the given player.
     *
     * @param playerId the player's UUID
     * @return map of enchantment name to level; empty if none recorded
     */
    public Map<String, Integer> getAllEnchantments(UUID playerId) {
        if (playerId == null) {
            return Collections.emptyMap();
        }
        Map<String, Integer> entry = enchantments.get(playerId);
        return entry != null ? Collections.unmodifiableMap(entry) : Collections.emptyMap();
    }

    /**
     * Resets all enchantments for the given player.
     *
     * @param playerId the player's UUID
     */
    public void resetEnchantments(UUID playerId) {
        enchantments.remove(playerId);
    }

    // -------------------------------------------------------------------------
    // Cleanup
    // -------------------------------------------------------------------------

    /**
     * Removes all state for the given player.
     *
     * @param playerId the player's UUID
     */
    public void remove(UUID playerId) {
        enchantments.remove(playerId);
    }

    public void load(File dataFolder) {
        File file = new File(dataFolder, "enchanting.yml");
        if (!file.exists()) {
            return;
        }
        YamlConfiguration cfg = YamlConfiguration.loadConfiguration(file);
        enchantments.clear();
        for (String key : cfg.getKeys(false)) {
            try {
                UUID uuid = UUID.fromString(key);
                if (cfg.isConfigurationSection(key)) {
                    Map<String, Integer> map = new HashMap<>();
                    for (String enchantName : cfg.getConfigurationSection(key).getKeys(false)) {
                        int level = cfg.getInt(key + "." + enchantName, 0);
                        if (level > 0) {
                            map.put(enchantName, level);
                        }
                    }
                    if (!map.isEmpty()) {
                        enchantments.put(uuid, map);
                    }
                }
            } catch (IllegalArgumentException ignored) {
                // skip malformed entries
            }
        }
    }

    public void save(File dataFolder) {
        File file = new File(dataFolder, "enchanting.yml");
        YamlConfiguration cfg = new YamlConfiguration();
        for (Map.Entry<UUID, Map<String, Integer>> entry : enchantments.entrySet()) {
            String key = entry.getKey().toString();
            for (Map.Entry<String, Integer> e : entry.getValue().entrySet()) {
                cfg.set(key + "." + e.getKey(), e.getValue());
            }
        }
        try {
            cfg.save(file);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save enchanting.yml", e);
        }
    }
}
