package com.skyblock.core.manager;

import com.skyblock.core.manager.GardenManager.GardenCrop;

import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

/**
 * Singleton tracking Garden pests: each player's accumulated farming activity
 * (which drives the pest spawn chance), the pests currently infesting their
 * Garden, and the SkyMart pesticide they hold to exterminate them.
 *
 * <p>Not thread-safe; synchronize externally if accessed from multiple threads.</p>
 */
public final class PestManager {

    /**
     * Garden pests, each tied to the {@link GardenCrop} whose plot it infests.
     * Farming a crop accrues activity that can spawn that crop's pest.
     */
    public enum PestType {
        FLY("Fly", GardenCrop.WHEAT),
        CRICKET("Cricket", GardenCrop.CARROT),
        LOCUST("Locust", GardenCrop.POTATO),
        EARTHWORM("Earthworm", GardenCrop.MELON),
        RAT("Rat", GardenCrop.PUMPKIN),
        MOSQUITO("Mosquito", GardenCrop.SUGAR_CANE),
        MOTH("Moth", GardenCrop.COCOA_BEANS),
        MITE("Mite", GardenCrop.CACTUS),
        SLUG("Slug", GardenCrop.MUSHROOM),
        BEETLE("Beetle", GardenCrop.NETHER_WART),
        MOUSE("Mouse", GardenCrop.CABBAGE);

        private final String displayName;
        private final GardenCrop crop;

        PestType(String displayName, GardenCrop crop) {
            this.displayName = displayName;
            this.crop = crop;
        }

        public String getDisplayName() {
            return displayName;
        }

        /** The Garden crop whose plot this pest infests. */
        public GardenCrop getCrop() {
            return crop;
        }

        /** Returns the pest that infests the given crop, or {@code null} if none. */
        public static PestType forCrop(GardenCrop crop) {
            Objects.requireNonNull(crop, "crop");
            for (PestType type : values()) {
                if (type.crop == crop) {
                    return type;
                }
            }
            return null;
        }
    }

    private static final PestManager INSTANCE = new PestManager();

    /** Maximum number of pests that can simultaneously infest a Garden. */
    public static final int MAX_PESTS = 8;

    /** Additional spawn chance contributed by each unit of farming activity. */
    private static final double SPAWN_CHANCE_PER_ACTIVITY = 0.0001D;

    /** Upper bound on the per-action pest spawn chance, regardless of activity. */
    private static final double MAX_SPAWN_CHANCE = 0.05D;

    /** Per-player accumulated farming activity since the last pest spawned. */
    private final Map<UUID, Long> farmingActivity = new HashMap<>();

    /** Per-player active pest counts, indexed by {@link PestType} ordinal. */
    private final Map<UUID, int[]> pests = new HashMap<>();

    /** Per-player SkyMart pesticide held. */
    private final Map<UUID, Integer> pesticides = new HashMap<>();

    /** Per-player total pests exterminated. */
    private final Map<UUID, Long> pestsKilled = new HashMap<>();

    private PestManager() {
    }

    /**
     * Returns the single shared {@code PestManager} instance.
     *
     * @return the singleton instance
     */
    public static PestManager getInstance() {
        return INSTANCE;
    }

    // -------------------------------------------------------------------------
    // Farming activity
    // -------------------------------------------------------------------------

    /**
     * Returns the player's accumulated farming activity since their last spawn.
     *
     * @param playerId the player to look up
     * @return the farming activity, {@code 0} if not set
     */
    public long getFarmingActivity(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        return farmingActivity.getOrDefault(playerId, 0L);
    }

    /**
     * Records farming activity for the player (e.g. one per crop broken).
     *
     * @param playerId the player to update
     * @param amount   the activity to add (clamped so the total stays {@code >= 0})
     * @return the new farming activity total
     */
    public long recordFarmingActivity(UUID playerId, long amount) {
        Objects.requireNonNull(playerId, "playerId");
        long updated = Math.max(0L, getFarmingActivity(playerId) + amount);
        farmingActivity.put(playerId, updated);
        return updated;
    }

    // -------------------------------------------------------------------------
    // Spawn chance
    // -------------------------------------------------------------------------

    /**
     * Returns the current pest spawn chance for the player, derived from their
     * accumulated farming activity and capped at {@value #MAX_SPAWN_CHANCE}.
     *
     * @param playerId the player to look up
     * @return the spawn chance, a probability in {@code [0, MAX_SPAWN_CHANCE]}
     */
    public double getSpawnChance(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        double chance = getFarmingActivity(playerId) * SPAWN_CHANCE_PER_ACTIVITY;
        return Math.min(MAX_SPAWN_CHANCE, chance);
    }

    // -------------------------------------------------------------------------
    // Pests
    // -------------------------------------------------------------------------

    /**
     * Returns the number of the given pest currently infesting the player's Garden.
     *
     * @param playerId the player to look up
     * @param type     the pest type
     * @return the pest count, {@code 0} if none
     */
    public int getPestCount(UUID playerId, PestType type) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(type, "type");
        int[] counts = pests.get(playerId);
        return counts == null ? 0 : counts[type.ordinal()];
    }

    /**
     * Returns the total number of pests infesting the player's Garden.
     *
     * @param playerId the player to look up
     * @return the total active pest count, {@code 0} if none
     */
    public int getTotalPests(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        int[] counts = pests.get(playerId);
        if (counts == null) {
            return 0;
        }
        int total = 0;
        for (int count : counts) {
            total += count;
        }
        return total;
    }

    /**
     * Spawns one pest of the given type in the player's Garden and resets their
     * accumulated farming activity, mirroring how a spawn consumes the built-up
     * pest progress.
     *
     * <p>Does nothing if the Garden is already at {@link #MAX_PESTS}.</p>
     *
     * @param playerId the player whose Garden is infested
     * @param type     the pest to spawn
     * @return {@code true} if a pest spawned, {@code false} if the Garden was full
     */
    public boolean spawnPest(UUID playerId, PestType type) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(type, "type");
        if (getTotalPests(playerId) >= MAX_PESTS) {
            return false;
        }
        int[] counts = pests.computeIfAbsent(playerId, id -> new int[PestType.values().length]);
        counts[type.ordinal()]++;
        farmingActivity.put(playerId, 0L);
        return true;
    }

    // -------------------------------------------------------------------------
    // SkyMart pesticide
    // -------------------------------------------------------------------------

    /**
     * Returns the amount of SkyMart pesticide the player holds.
     *
     * @param playerId the player to look up
     * @return the pesticide count, {@code 0} if none
     */
    public int getPesticides(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        return pesticides.getOrDefault(playerId, 0);
    }

    /**
     * Adds SkyMart pesticide to the player's stock (e.g. on purchase).
     *
     * @param playerId the player to update
     * @param amount   the amount to add (clamped so the total stays {@code >= 0})
     * @return the new pesticide count
     */
    public int addPesticides(UUID playerId, int amount) {
        Objects.requireNonNull(playerId, "playerId");
        int updated = Math.max(0, getPesticides(playerId) + amount);
        pesticides.put(playerId, updated);
        return updated;
    }

    /**
     * Returns the total number of pests the player has exterminated.
     *
     * @param playerId the player to look up
     * @return the pests-killed total, {@code 0} if none
     */
    public long getPestsKilled(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        return pestsKilled.getOrDefault(playerId, 0L);
    }

    /**
     * Consumes one SkyMart pesticide to exterminate one pest of the given type.
     *
     * <p>Requires both a held pesticide and an active pest of that type; if
     * either is missing nothing changes and {@code false} is returned.</p>
     *
     * @param playerId the player applying the pesticide
     * @param type     the pest to exterminate
     * @return {@code true} if a pest was killed, {@code false} otherwise
     */
    public boolean usePesticide(UUID playerId, PestType type) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(type, "type");
        if (getPesticides(playerId) <= 0 || getPestCount(playerId, type) <= 0) {
            return false;
        }
        pesticides.put(playerId, getPesticides(playerId) - 1);
        int[] counts = pests.get(playerId);
        counts[type.ordinal()]--;
        pestsKilled.merge(playerId, 1L, Long::sum);
        return true;
    }

    // -------------------------------------------------------------------------
    // Persistence
    // -------------------------------------------------------------------------

    public void load(File dataFolder) {
        File file = new File(dataFolder, "pests.yml");
        if (!file.exists()) {
            return;
        }
        YamlConfiguration cfg = YamlConfiguration.loadConfiguration(file);
        farmingActivity.clear();
        pests.clear();
        pesticides.clear();
        pestsKilled.clear();
        for (String key : cfg.getKeys(false)) {
            try {
                UUID uuid = UUID.fromString(key);
                if (cfg.isSet(key + ".farmingActivity")) {
                    farmingActivity.put(uuid, cfg.getLong(key + ".farmingActivity", 0L));
                }
                if (cfg.isSet(key + ".pesticides")) {
                    pesticides.put(uuid, cfg.getInt(key + ".pesticides", 0));
                }
                if (cfg.isSet(key + ".pestsKilled")) {
                    pestsKilled.put(uuid, cfg.getLong(key + ".pestsKilled", 0L));
                }
                if (cfg.isConfigurationSection(key + ".pests")) {
                    int[] counts = new int[PestType.values().length];
                    for (String typeName : cfg.getConfigurationSection(key + ".pests").getKeys(false)) {
                        try {
                            PestType type = PestType.valueOf(typeName);
                            counts[type.ordinal()] = cfg.getInt(key + ".pests." + typeName, 0);
                        } catch (IllegalArgumentException ignored) {}
                    }
                    pests.put(uuid, counts);
                }
            } catch (IllegalArgumentException ignored) {}
        }
    }

    public void save(File dataFolder) {
        File file = new File(dataFolder, "pests.yml");
        YamlConfiguration cfg = new YamlConfiguration();
        Set<UUID> allUuids = new HashSet<>();
        allUuids.addAll(farmingActivity.keySet());
        allUuids.addAll(pests.keySet());
        allUuids.addAll(pesticides.keySet());
        allUuids.addAll(pestsKilled.keySet());
        for (UUID uuid : allUuids) {
            String key = uuid.toString();
            if (farmingActivity.containsKey(uuid)) {
                cfg.set(key + ".farmingActivity", farmingActivity.get(uuid));
            }
            if (pesticides.containsKey(uuid)) {
                cfg.set(key + ".pesticides", pesticides.get(uuid));
            }
            if (pestsKilled.containsKey(uuid)) {
                cfg.set(key + ".pestsKilled", pestsKilled.get(uuid));
            }
            int[] counts = pests.get(uuid);
            if (counts != null) {
                PestType[] types = PestType.values();
                for (int i = 0; i < types.length; i++) {
                    if (counts[i] != 0) {
                        cfg.set(key + ".pests." + types[i].name(), counts[i]);
                    }
                }
            }
        }
        try {
            cfg.save(file);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save pests.yml", e);
        }
    }

    // -------------------------------------------------------------------------
    // Lifecycle
    // -------------------------------------------------------------------------

    /**
     * Resets all pest data for the given player.
     *
     * @param playerId the player to reset
     */
    public void reset(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        farmingActivity.remove(playerId);
        pests.remove(playerId);
        pesticides.remove(playerId);
        pestsKilled.remove(playerId);
    }

    /**
     * Removes all pest data for the given player (e.g. on quit).
     *
     * @param playerId the player to remove
     * @return {@code true} if the player had data, {@code false} otherwise
     */
    public boolean remove(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        boolean had = farmingActivity.remove(playerId) != null;
        had |= pests.remove(playerId) != null;
        had |= pesticides.remove(playerId) != null;
        had |= pestsKilled.remove(playerId) != null;
        return had;
    }
}
