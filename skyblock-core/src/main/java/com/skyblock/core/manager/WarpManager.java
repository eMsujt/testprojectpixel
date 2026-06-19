package com.skyblock.core.manager;

import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

/**
 * Per-player warp unlock registry backed by YAML persistence.
 *
 * <p>HUB is unlocked by default for every player. All other warps must be
 * explicitly unlocked. The singleton is safe for single-threaded Bukkit use.</p>
 */
public final class WarpManager {

    /** Pre-defined SkyBlock warp destinations a player can unlock. */
    public enum WarpLocation {
        HUB("Hub"),
        FARMING_ISLAND("Farming Island"),
        GOLD_MINE("Gold Mine"),
        DEEP_CAVERNS("Deep Caverns"),
        DWARVEN_MINES("Dwarven Mines"),
        CRYSTAL_HOLLOWS("Crystal Hollows"),
        THE_PARK("The Park"),
        SPIDER_DEN("Spider's Den"),
        BLAZING_FORTRESS("Blazing Fortress"),
        THE_END("The End"),
        CRIMSON_ISLE("Crimson Isle"),
        THE_RIFT("The Rift"),
        DUNGEON_HUB("Dungeon Hub"),
        MUSHROOM_DESERT("Mushroom Desert");

        public final String displayName;

        WarpLocation(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    private static final WarpManager INSTANCE = new WarpManager();

    private final Map<UUID, Set<WarpLocation>> unlockedWarps = new HashMap<>();

    private WarpManager() {}

    public static WarpManager getInstance() {
        return INSTANCE;
    }

    /**
     * Unlocks a warp for the given player.
     *
     * @param playerId the player's UUID
     * @param location the warp to unlock
     */
    public void unlockWarp(UUID playerId, WarpLocation location) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(location, "location");
        unlockedWarps.computeIfAbsent(playerId, k -> EnumSet.noneOf(WarpLocation.class)).add(location);
    }

    /**
     * Returns {@code true} if the player has the given warp unlocked.
     * HUB is always considered unlocked.
     *
     * @param playerId the player's UUID
     * @param location the warp to check
     */
    public boolean hasWarp(UUID playerId, WarpLocation location) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(location, "location");
        if (location == WarpLocation.HUB) return true;
        Set<WarpLocation> set = unlockedWarps.get(playerId);
        return set != null && set.contains(location);
    }

    /**
     * Returns an unmodifiable view of all warps unlocked by the given player,
     * including HUB which is always present.
     *
     * @param playerId the player's UUID
     * @return unmodifiable set of unlocked warp locations
     */
    public Set<WarpLocation> getUnlockedWarps(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        Set<WarpLocation> result = EnumSet.of(WarpLocation.HUB);
        Set<WarpLocation> stored = unlockedWarps.get(playerId);
        if (stored != null) result.addAll(stored);
        return Collections.unmodifiableSet(result);
    }

    /** Clears all warp data for the given player (e.g. on quit). */
    public void clearPlayer(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        unlockedWarps.remove(playerId);
    }

    /**
     * Loads per-player warp unlock data from {@code warps.yml} in the given folder.
     *
     * @param dataFolder the plugin data folder
     */
    public void load(File dataFolder) {
        Objects.requireNonNull(dataFolder, "dataFolder");
        unlockedWarps.clear();
        File file = new File(dataFolder, "warps.yml");
        if (!file.exists()) return;
        YamlConfiguration cfg = YamlConfiguration.loadConfiguration(file);
        if (!cfg.isConfigurationSection("unlocked")) return;
        for (String key : cfg.getConfigurationSection("unlocked").getKeys(false)) {
            UUID uuid;
            try {
                uuid = UUID.fromString(key);
            } catch (IllegalArgumentException ignored) {
                continue;
            }
            java.util.List<String> names = cfg.getStringList("unlocked." + key);
            Set<WarpLocation> set = EnumSet.noneOf(WarpLocation.class);
            for (String name : names) {
                try {
                    set.add(WarpLocation.valueOf(name));
                } catch (IllegalArgumentException ignored) {
                    // skip unknown warp names from old data
                }
            }
            if (!set.isEmpty()) unlockedWarps.put(uuid, set);
        }
    }

    /**
     * Saves per-player warp unlock data to {@code warps.yml} in the given folder.
     *
     * @param dataFolder the plugin data folder
     * @throws IOException if the file cannot be written
     */
    public void save(File dataFolder) throws IOException {
        Objects.requireNonNull(dataFolder, "dataFolder");
        YamlConfiguration cfg = new YamlConfiguration();
        for (Map.Entry<UUID, Set<WarpLocation>> entry : unlockedWarps.entrySet()) {
            java.util.List<String> names = new java.util.ArrayList<>();
            for (WarpLocation loc : entry.getValue()) names.add(loc.name());
            cfg.set("unlocked." + entry.getKey(), names);
        }
        cfg.save(new File(dataFolder, "warps.yml"));
    }
}
