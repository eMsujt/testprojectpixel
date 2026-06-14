package com.skyblock.plugin.managers;

import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public final class WarpManager {

    public enum Warp {
        HUB,
        GARDEN,
        DEEP_CAVERNS,
        DWARVEN_MINES,
        CRYSTAL_HOLLOWS,
        THE_END,
        CRIMSON_ISLE,
        SPIDER_DEN,
        GOLD_MINE,
        BARN,
        MUSHROOM_DESERT,
        GRAVEYARD,
        DUNGEON_HUB
    }

    private static final WarpManager INSTANCE = new WarpManager();

    private final Map<UUID, Warp> unlockedWarps = new HashMap<>();

    private WarpManager() {}

    public static WarpManager getInstance() {
        return INSTANCE;
    }

    public void unlockWarp(UUID playerId, Warp warp) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(warp, "warp");
        unlockedWarps.put(playerId, warp);
    }

    public boolean hasWarp(UUID playerId, Warp warp) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(warp, "warp");
        return unlockedWarps.containsKey(playerId) && unlockedWarps.get(playerId) == warp;
    }

    public Warp[] getWarps() {
        return Warp.values();
    }

    public void load(File dataFolder) {
        File file = new File(dataFolder, "warps.yml");
        if (!file.exists()) {
            return;
        }
        YamlConfiguration cfg = YamlConfiguration.loadConfiguration(file);
        unlockedWarps.clear();
        if (cfg.isConfigurationSection("unlocked")) {
            for (String key : cfg.getConfigurationSection("unlocked").getKeys(false)) {
                try {
                    UUID uuid = UUID.fromString(key);
                    String warpName = cfg.getString("unlocked." + key);
                    if (warpName != null) {
                        try {
                            unlockedWarps.put(uuid, Warp.valueOf(warpName));
                        } catch (IllegalArgumentException ignored) {
                            // skip unknown warp name
                        }
                    }
                } catch (IllegalArgumentException ignored) {
                    // skip malformed UUID
                }
            }
        }
    }

    public void save(File dataFolder) {
        File file = new File(dataFolder, "warps.yml");
        YamlConfiguration cfg = new YamlConfiguration();
        for (Map.Entry<UUID, Warp> entry : unlockedWarps.entrySet()) {
            cfg.set("unlocked." + entry.getKey().toString(), entry.getValue().name());
        }
        try {
            cfg.save(file);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save warps.yml", e);
        }
    }
}
