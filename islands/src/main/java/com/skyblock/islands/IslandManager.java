package com.skyblock.islands;

import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.bukkit.Location;

/**
 * Singleton registry of player islands, keyed by owner UUID.
 *
 * <p>Each player owns at most one {@link PlayerIsland}. Islands are immutable
 * snapshots; updates such as {@link #setIslandLevel(UUID, int)} replace the
 * stored record. Not thread-safe; synchronize externally if accessed from
 * multiple threads.</p>
 */
public final class IslandManager {

    public static final Map<String, int[]> UPGRADE_DATA;

    static {
        Map<String, int[]> m = new HashMap<>();
        m.put("MINION_SLOTS",   new int[]{10, 500});
        m.put("COOP_SLOTS",     new int[]{4,  1000});
        m.put("ISLAND_SIZE",    new int[]{5,  2000});
        m.put("CHEST_SIZE",     new int[]{5,  750});
        m.put("GUEST_LIMIT",    new int[]{4,  250});
        m.put("REDSTONE_LIMIT", new int[]{5,  500});
        m.put("CROP_GROWTH",    new int[]{5,  1500});
        m.put("MOB_SPAWN_RATE", new int[]{5,  1500});
        m.put("BEACON_RANGE",   new int[]{3,  2500});
        m.put("WARP_LIMIT",     new int[]{3,  1000});
        m.put("BANK_SIZE",      new int[]{5,  3000});
        m.put("STORAGE",        new int[]{4,  2000});
        UPGRADE_DATA = Collections.unmodifiableMap(m);
    }

    /**
     * Immutable snapshot of a single player island.
     *
     * @param owner         player UUID of the island owner
     * @param spawnLocation location players are sent to when visiting
     * @param level         the island's upgrade level, at least 1
     * @param members       member UUIDs, not including the owner
     */
    public record PlayerIsland(UUID owner, Location spawnLocation, int level, Set<UUID> members) {

        /**
         * Validates arguments and defensively copies mutable state.
         *
         * @throws IllegalArgumentException if {@code level} is less than 1
         */
        public PlayerIsland {
            Objects.requireNonNull(owner, "owner");
            Objects.requireNonNull(spawnLocation, "spawnLocation");
            Objects.requireNonNull(members, "members");
            if (level < 1) {
                throw new IllegalArgumentException("level must be at least 1, got " + level);
            }
            spawnLocation = spawnLocation.clone();
            members = Set.copyOf(members);
        }

        /**
         * Returns a copy of the island spawn location.
         *
         * @return the spawn location
         */
        @Override
        public Location spawnLocation() {
            return spawnLocation.clone();
        }

        /**
         * Checks whether a player is the owner or a member of this island.
         *
         * @param playerId the player to check
         * @return {@code true} if the player belongs to this island
         */
        public boolean isMember(UUID playerId) {
            return owner.equals(playerId) || members.contains(playerId);
        }
    }

    private static final IslandManager INSTANCE = new IslandManager();

    private final Map<UUID, PlayerIsland> islands = new HashMap<>();
    public final ConcurrentHashMap<UUID, Location> islandHomes = new ConcurrentHashMap<>();
    /** Levels loaded from disk; consumed by {@link #createIsland} to restore saved levels. */
    private final Map<UUID, Integer> islandLevels = new HashMap<>();
    private final Map<UUID, List<String>> islandHistory = new HashMap<>();

    private IslandManager() {
    }

    /**
     * Returns the single shared {@code IslandManager} instance.
     *
     * @return the singleton instance
     */
    public static IslandManager getInstance() {
        return INSTANCE;
    }

    /**
     * Creates and registers a new level-1 island for the given owner.
     *
     * @param owner         player UUID of the island owner
     * @param spawnLocation initial spawn location of the island
     * @return the newly created island
     * @throws IllegalStateException if the owner already has an island
     */
    public PlayerIsland createIsland(UUID owner, Location spawnLocation) {
        Objects.requireNonNull(owner, "owner");
        if (islands.containsKey(owner)) {
            throw new IllegalStateException("Player " + owner + " already owns an island");
        }
        PlayerIsland island = new PlayerIsland(owner, spawnLocation, islandLevels.getOrDefault(owner, 1), Set.of());
        islands.put(owner, island);
        recordIslandEvent(owner, "Island created");
        return island;
    }

    /**
     * Looks up the island owned by the given player.
     *
     * @param owner the owner's player UUID
     * @return the island, or empty if the player owns no island
     */
    public Optional<PlayerIsland> getIsland(UUID owner) {
        return Optional.ofNullable(islands.get(owner));
    }

    /**
     * Returns whether the given player owns an island.
     *
     * @param owner the owner's player UUID
     * @return {@code true} if the player owns an island
     */
    public boolean hasIsland(UUID owner) {
        return islands.containsKey(owner);
    }

    /**
     * Sets the upgrade level of a player's island.
     *
     * @param owner the owner's player UUID
     * @param level the new level, must be at least 1
     * @return the updated island
     * @throws IllegalArgumentException if {@code level} is less than 1
     * @throws IllegalStateException    if the player owns no island
     */
    public PlayerIsland setIslandLevel(UUID owner, int level) {
        PlayerIsland island = requireIsland(owner);
        PlayerIsland updated =
                new PlayerIsland(island.owner(), island.spawnLocation(), level, island.members());
        islands.put(owner, updated);
        recordIslandEvent(owner, "Island level set to: " + level);
        return updated;
    }

    /**
     * Adds a player to the member roster of an island.
     *
     * @param owner    the island owner's player UUID
     * @param playerId the player to add, must not be the owner
     * @return the updated island
     * @throws IllegalArgumentException if {@code playerId} is the owner
     * @throws IllegalStateException    if the owner owns no island
     */
    public PlayerIsland addMember(UUID owner, UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        PlayerIsland island = requireIsland(owner);
        if (owner.equals(playerId)) {
            throw new IllegalArgumentException("owner cannot be added as a member");
        }
        Set<UUID> members = new LinkedHashSet<>(island.members());
        members.add(playerId);
        PlayerIsland updated =
                new PlayerIsland(island.owner(), island.spawnLocation(), island.level(), members);
        islands.put(owner, updated);
        recordIslandEvent(owner, "Member added: " + playerId);
        return updated;
    }

    /**
     * Removes a player from the member roster of an island.
     *
     * @param owner    the island owner's player UUID
     * @param playerId the player to remove
     * @return the updated island
     * @throws IllegalStateException if the owner owns no island
     */
    public PlayerIsland removeMember(UUID owner, UUID playerId) {
        PlayerIsland island = requireIsland(owner);
        Set<UUID> members = new LinkedHashSet<>(island.members());
        members.remove(playerId);
        PlayerIsland updated =
                new PlayerIsland(island.owner(), island.spawnLocation(), island.level(), members);
        islands.put(owner, updated);
        recordIslandEvent(owner, "Member removed: " + playerId);
        return updated;
    }

    /**
     * Removes a player's island from the registry.
     *
     * @param owner the owner's player UUID
     * @return {@code true} if the player owned an island
     */
    public boolean deleteIsland(UUID owner) {
        boolean removed = islands.remove(owner) != null;
        if (removed) {
            recordIslandEvent(owner, "Island deleted");
        }
        return removed;
    }

    /**
     * Sets the home teleport location for a player's island.
     *
     * @param owner the island owner's player UUID
     * @param home  the new home location
     */
    public void setHome(UUID owner, Location home) {
        Objects.requireNonNull(owner, "owner");
        Objects.requireNonNull(home, "home");
        islandHomes.put(owner, home.clone());
    }

    /**
     * Returns the home teleport location for a player's island.
     *
     * @param owner the island owner's player UUID
     * @return the home location, or empty if none is set
     */
    public Optional<Location> getHome(UUID owner) {
        Objects.requireNonNull(owner, "owner");
        Location loc = islandHomes.get(owner);
        return loc == null ? Optional.empty() : Optional.of(loc.clone());
    }

    public void recordIslandEvent(UUID playerUuid, String summary) {
        islandHistory.computeIfAbsent(playerUuid, k -> new ArrayList<>()).add(summary);
    }

    public List<String> getIslandHistory(UUID playerUuid) {
        return Collections.unmodifiableList(islandHistory.getOrDefault(playerUuid, Collections.emptyList()));
    }

    public Map<UUID, List<String>> getAllIslandHistory() {
        return Collections.unmodifiableMap(islandHistory);
    }

    public String getIslandStats(UUID playerId) {
        int level = getIsland(playerId).map(PlayerIsland::level).orElse(1);
        String topCrop = "None";
        for (String entry : islandHistory.getOrDefault(playerId, Collections.emptyList())) {
            if (entry.toLowerCase().contains("crop")) {
                topCrop = entry;
            }
        }
        return "Island Stats: Level: " + level + " | Visitors: 0 | Top Crop: " + topCrop;
    }

    public void load(File dataFolder) {
        File file = new File(dataFolder, "island.yml");
        if (!file.exists()) {
            return;
        }
        YamlConfiguration cfg = YamlConfiguration.loadConfiguration(file);
        islandLevels.clear();
        for (String key : cfg.getKeys(false)) {
            try {
                UUID uuid = UUID.fromString(key);
                islandLevels.put(uuid, cfg.getInt(key + ".level", 1));
            } catch (IllegalArgumentException ignored) {}
        }
        islandHistory.clear();
        if (cfg.isConfigurationSection("islandHistory")) {
            for (String key : cfg.getConfigurationSection("islandHistory").getKeys(false)) {
                try {
                    List<String> entries = cfg.getStringList("islandHistory." + key);
                    islandHistory.put(UUID.fromString(key), new ArrayList<>(entries));
                } catch (IllegalArgumentException ignored) {}
            }
        }
    }

    public void save(File dataFolder) {
        File file = new File(dataFolder, "island.yml");
        YamlConfiguration cfg = new YamlConfiguration();
        for (Map.Entry<UUID, PlayerIsland> entry : islands.entrySet()) {
            cfg.set(entry.getKey().toString() + ".level", entry.getValue().level());
        }
        for (Map.Entry<UUID, List<String>> entry : islandHistory.entrySet()) {
            cfg.set("islandHistory." + entry.getKey().toString(), entry.getValue());
        }
        try {
            cfg.save(file);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save island.yml", e);
        }
    }

    private PlayerIsland requireIsland(UUID owner) {
        Objects.requireNonNull(owner, "owner");
        PlayerIsland island = islands.get(owner);
        if (island == null) {
            throw new IllegalStateException("Player " + owner + " owns no island");
        }
        return island;
    }
}
