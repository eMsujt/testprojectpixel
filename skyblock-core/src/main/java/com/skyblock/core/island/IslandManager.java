package com.skyblock.core.island;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

/**
 * Singleton managing per-player SkyBlock islands.
 *
 * <p>Islands are keyed by owner UUID. Members are tracked in a secondary index
 * so that lookups by non-owner member are O(1).</p>
 *
 * <p>Each island owns a dedicated void {@link World} created via {@link WorldCreator}
 * and {@link IslandGenerator}. Worlds are stored in {@link #islandWorlds} and unloaded
 * when the island is deleted.</p>
 */
public final class IslandManager {

    public enum IslandUpgrade {
        MINION_SLOTS(10, "Minion Slots"),
        ISLAND_SIZE(5,  "Island Size"),
        CHEST_SIZE(5,   "Chest Size"),
        GUEST_LIMIT(4,  "Guest Limit"),
        COOP_SLOTS(4,   "Co-op Slots"),
        REDSTONE_LIMIT(5, "Redstone Limit"),
        CROP_GROWTH(5,  "Crop Growth"),
        MOB_SPAWN_RATE(5, "Mob Spawn Rate");

        private final int maxLevel;
        private final String displayName;

        IslandUpgrade(int maxLevel, String displayName) {
            this.maxLevel = maxLevel;
            this.displayName = displayName;
        }

        public int getMaxLevel() {
            return maxLevel;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    public static final class SkyBlockIsland {

        private final UUID owner;
        private final List<UUID> members = new ArrayList<>();
        private final Map<IslandUpgrade, Integer> upgrades = new HashMap<>();
        private String warpName;

        SkyBlockIsland(UUID owner) {
            this.owner = owner;
        }

        public UUID getOwner() {
            return owner;
        }

        public List<UUID> getMembers() {
            return Collections.unmodifiableList(members);
        }

        public int getUpgradeLevel(IslandUpgrade upgrade) {
            return upgrades.getOrDefault(upgrade, 0);
        }

        public Map<IslandUpgrade, Integer> getUpgrades() {
            return Collections.unmodifiableMap(upgrades);
        }

        /** Returns the public warp name for this island, or {@code null} if none is set. */
        public String getWarpName() {
            return warpName;
        }

        void setWarpName(String warpName) {
            this.warpName = warpName;
        }
    }

    private static final IslandManager INSTANCE = new IslandManager();

    /** owner UUID → island */
    private final Map<UUID, SkyBlockIsland> islands = new HashMap<>();
    /** member UUID → owner UUID (owner is NOT in this map) */
    private final Map<UUID, UUID> memberIndex = new HashMap<>();
    /** owner UUID → island world */
    private final Map<UUID, World> islandWorlds = new HashMap<>();

    /** Per-player island statistics. */
    private final Map<UUID, IslandData> playerData = new HashMap<>();

    private IslandManager() {
    }

    public static IslandManager getInstance() {
        return INSTANCE;
    }

    /**
     * Creates a new island owned by {@code owner}, including its void world.
     *
     * @return the new island
     * @throws IllegalStateException if the player already owns an island
     */
    public SkyBlockIsland createIsland(UUID owner) {
        Objects.requireNonNull(owner, "owner");
        if (islands.containsKey(owner)) {
            throw new IllegalStateException("Player already owns an island");
        }
        SkyBlockIsland island = new SkyBlockIsland(owner);
        islands.put(owner, island);

        World world = new WorldCreator("island_" + owner)
                .generator(new IslandGenerator())
                .createWorld();
        islandWorlds.put(owner, world);

        return island;
    }

    /**
     * Returns the island world for {@code owner}, if any.
     *
     * <p>Use this to obtain the spawn {@link Location} for teleportation:
     * {@code world.getSpawnLocation()}.</p>
     */
    public Optional<World> getIslandWorld(UUID owner) {
        Objects.requireNonNull(owner, "owner");
        return Optional.ofNullable(islandWorlds.get(owner));
    }

    /** Returns the island owned by {@code owner}, if any. */
    public Optional<SkyBlockIsland> getIsland(UUID owner) {
        Objects.requireNonNull(owner, "owner");
        return Optional.ofNullable(islands.get(owner));
    }

    /** Returns the island that {@code player} belongs to (as owner or member). */
    public Optional<SkyBlockIsland> getIslandByMember(UUID player) {
        Objects.requireNonNull(player, "player");
        if (islands.containsKey(player)) {
            return Optional.of(islands.get(player));
        }
        UUID ownerUuid = memberIndex.get(player);
        return ownerUuid == null ? Optional.empty() : Optional.ofNullable(islands.get(ownerUuid));
    }

    /** Returns {@code true} if {@code owner} already has an island. */
    public boolean hasIsland(UUID owner) {
        Objects.requireNonNull(owner, "owner");
        return islands.containsKey(owner);
    }

    /**
     * Adds {@code invitee} as a member of {@code owner}'s island.
     *
     * @return {@code false} if the island does not exist or the player is already a member
     */
    public boolean addMember(UUID owner, UUID invitee) {
        Objects.requireNonNull(owner, "owner");
        Objects.requireNonNull(invitee, "invitee");
        SkyBlockIsland island = islands.get(owner);
        if (island == null || island.members.contains(invitee) || islands.containsKey(invitee)) {
            return false;
        }
        island.members.add(invitee);
        memberIndex.put(invitee, owner);
        return true;
    }

    /**
     * Removes {@code target} from {@code owner}'s island members.
     *
     * @return {@code false} if the island does not exist or the player is not a member
     */
    public boolean removeMember(UUID owner, UUID target) {
        Objects.requireNonNull(owner, "owner");
        Objects.requireNonNull(target, "target");
        SkyBlockIsland island = islands.get(owner);
        if (island == null) {
            return false;
        }
        boolean removed = island.members.remove(target);
        if (removed) {
            memberIndex.remove(target);
        }
        return removed;
    }

    /**
     * Removes {@code member} from whichever island they belong to.
     * Has no effect if the player owns an island (owners cannot leave their own island).
     *
     * @return {@code false} if the player is not a member of any island
     */
    public boolean leaveIsland(UUID member) {
        Objects.requireNonNull(member, "member");
        UUID ownerUuid = memberIndex.get(member);
        if (ownerUuid == null) {
            return false;
        }
        SkyBlockIsland island = islands.get(ownerUuid);
        if (island != null) {
            island.members.remove(member);
        }
        memberIndex.remove(member);
        return true;
    }

    /**
     * Increments the level of {@code upgrade} on {@code owner}'s island by 1.
     *
     * @return {@code false} if the island does not exist or the upgrade is already at max level
     */
    public boolean applyUpgrade(UUID owner, IslandUpgrade upgrade) {
        Objects.requireNonNull(owner, "owner");
        Objects.requireNonNull(upgrade, "upgrade");
        SkyBlockIsland island = islands.get(owner);
        if (island == null) {
            return false;
        }
        int current = island.upgrades.getOrDefault(upgrade, 0);
        if (current >= upgrade.getMaxLevel()) {
            return false;
        }
        island.upgrades.put(upgrade, current + 1);
        return true;
    }

    /**
     * Sets a public warp name for {@code owner}'s island.
     * Pass {@code null} to clear the warp name.
     *
     * @return {@code false} if the owner has no island
     */
    public boolean setWarpName(UUID owner, String warpName) {
        Objects.requireNonNull(owner, "owner");
        SkyBlockIsland island = islands.get(owner);
        if (island == null) {
            return false;
        }
        island.setWarpName(warpName);
        return true;
    }

    /**
     * Returns the warp name for {@code owner}'s island, or {@code null} if none is set.
     *
     * @return warp name or {@code null}
     */
    public String getWarpName(UUID owner) {
        Objects.requireNonNull(owner, "owner");
        SkyBlockIsland island = islands.get(owner);
        return island == null ? null : island.getWarpName();
    }

    // -------------------------------------------------------------------------
    // Per-player IslandData
    // -------------------------------------------------------------------------

    /** Returns the {@link IslandData} for {@code owner}, defaulting to {@link IslandData#EMPTY}. */
    public IslandData getIslandData(UUID owner) {
        Objects.requireNonNull(owner, "owner");
        return playerData.getOrDefault(owner, IslandData.EMPTY);
    }

    /** Replaces the {@link IslandData} for {@code owner}. */
    public void setIslandData(UUID owner, IslandData data) {
        Objects.requireNonNull(owner, "owner");
        Objects.requireNonNull(data, "data");
        playerData.put(owner, data);
    }

    /** Adds {@code amount} to the blocks-placed counter for {@code owner}. */
    public void addBlocksPlaced(UUID owner, long amount) {
        Objects.requireNonNull(owner, "owner");
        IslandData current = playerData.getOrDefault(owner, IslandData.EMPTY);
        playerData.put(owner, current.withBlocksPlaced(current.blocksPlaced() + amount));
    }

    /** Sets the island level for {@code owner}. */
    public void setIslandLevel(UUID owner, int level) {
        Objects.requireNonNull(owner, "owner");
        IslandData current = playerData.getOrDefault(owner, IslandData.EMPTY);
        playerData.put(owner, current.withIslandLevel(level));
    }

    /** Increments the visitor count for {@code owner} by 1. */
    public void incrementVisitorCount(UUID owner) {
        Objects.requireNonNull(owner, "owner");
        IslandData current = playerData.getOrDefault(owner, IslandData.EMPTY);
        playerData.put(owner, current.withVisitorCount(current.visitorCount() + 1));
    }

    // -------------------------------------------------------------------------
    // Persistence
    // -------------------------------------------------------------------------

    public void load(File dataFolder) {
        File file = new File(dataFolder, "island.yml");
        if (!file.exists()) {
            return;
        }
        YamlConfiguration cfg = YamlConfiguration.loadConfiguration(file);
        playerData.clear();
        for (String key : cfg.getKeys(false)) {
            try {
                UUID uuid = UUID.fromString(key);
                long blocksPlaced = cfg.getLong(key + ".blocksPlaced", 0L);
                int islandLevel   = cfg.getInt(key + ".islandLevel", 0);
                int visitorCount  = cfg.getInt(key + ".visitorCount", 0);
                playerData.put(uuid, new IslandData(blocksPlaced, islandLevel, visitorCount));
            } catch (IllegalArgumentException ignored) {
                // skip malformed entries
            }
        }
    }

    public void save(File dataFolder) {
        File file = new File(dataFolder, "island.yml");
        YamlConfiguration cfg = new YamlConfiguration();
        for (Map.Entry<UUID, IslandData> entry : playerData.entrySet()) {
            String key = entry.getKey().toString();
            IslandData data = entry.getValue();
            cfg.set(key + ".blocksPlaced", data.blocksPlaced());
            cfg.set(key + ".islandLevel",  data.islandLevel());
            cfg.set(key + ".visitorCount", data.visitorCount());
        }
        try {
            cfg.save(file);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save island.yml", e);
        }
    }

    /**
     * Deletes the island owned by {@code owner}, removes all its members, and unloads the world.
     *
     * @return {@code false} if the owner had no island
     */
    public boolean deleteIsland(UUID owner) {
        Objects.requireNonNull(owner, "owner");
        SkyBlockIsland island = islands.remove(owner);
        if (island == null) {
            return false;
        }
        for (UUID member : island.members) {
            memberIndex.remove(member);
        }
        World world = islandWorlds.remove(owner);
        if (world != null) {
            Bukkit.unloadWorld(world, false);
        }
        return true;
    }
}
