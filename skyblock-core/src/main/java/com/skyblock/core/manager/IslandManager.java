package com.skyblock.core.manager;

import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
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

import com.skyblock.core.util.IslandGenerator;

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
     * Flat data record for a player's SkyBlock island.
     *
     * @param owner        UUID of the island owner
     * @param trustees     UUIDs of players trusted on the island (mutable copy held internally)
     * @param level        island upgrade level, at least 0
     * @param blocksPlaced total blocks placed on the island
     */
    public record IslandData(UUID owner, List<UUID> trustees, int level, long blocksPlaced) {
        public IslandData {
            Objects.requireNonNull(owner, "owner");
            Objects.requireNonNull(trustees, "trustees");
            trustees = new ArrayList<>(trustees);
        }

        @Override
        public List<UUID> trustees() {
            return Collections.unmodifiableList(trustees);
        }
    }

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

    /** Per-player IslandData records. */
    private final Map<UUID, IslandData> islandData = new HashMap<>();
    private final Map<UUID, List<String>> islandHistory = new HashMap<>();
    private final Map<UUID, String> islandBiome = new HashMap<>();
    private final Map<UUID, Boolean> islandUnlocked = new HashMap<>();
    private final Map<UUID, Integer> visitorCounts = new HashMap<>();
    private final Map<UUID, List<String>> visitLog = new HashMap<>();

    /** owner UUID → island */
    private final Map<UUID, SkyBlockIsland> islands = new HashMap<>();
    /** member UUID → owner UUID (owner is NOT in this map) */
    private final Map<UUID, UUID> memberIndex = new HashMap<>();
    /** owner UUID → island world */
    private final Map<UUID, World> islandWorlds = new HashMap<>();

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

        recordIslandEvent(owner, "Island created");
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
        recordIslandEvent(owner, "Member added: " + invitee);
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
            recordIslandEvent(owner, "Member removed: " + target);
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
        recordIslandEvent(owner, "Upgrade applied: " + upgrade.getDisplayName() + " -> " + (current + 1));
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
        int level = getIslandLevel(playerId);
        String topCrop = "None";
        for (String entry : islandHistory.getOrDefault(playerId, Collections.emptyList())) {
            if (entry.toLowerCase().contains("crop")) {
                topCrop = entry;
            }
        }
        return "Island Stats: Level: " + level + " | Visitors: 0 | Top Crop: " + topCrop;
    }

    // -------------------------------------------------------------------------
    // IslandData API
    // -------------------------------------------------------------------------

    /**
     * Returns the {@link IslandData} for the given owner, or empty if none exists.
     *
     * @param owner the island owner's UUID
     * @return the island data, or empty
     */
    public Optional<IslandData> getIslandData(UUID owner) {
        Objects.requireNonNull(owner, "owner");
        return Optional.ofNullable(islandData.get(owner));
    }

    /**
     * Creates a default {@link IslandData} record for the given owner at level 0
     * if one does not already exist.
     *
     * @param owner the island owner's UUID
     * @return the existing or newly created record
     */
    public IslandData getOrCreateIslandData(UUID owner) {
        Objects.requireNonNull(owner, "owner");
        return islandData.computeIfAbsent(owner,
                id -> new IslandData(id, new ArrayList<>(), 0, 0L));
    }

    /**
     * Returns the island level for {@code owner}, or 0 if no record exists.
     *
     * @param owner the island owner's UUID
     * @return the island level
     */
    public int getIslandLevel(UUID owner) {
        Objects.requireNonNull(owner, "owner");
        IslandData d = islandData.get(owner);
        return d == null ? 0 : d.level();
    }

    /**
     * Sets the level on the owner's island data record.
     *
     * @param owner the island owner's UUID
     * @param level the new level (must be >= 0)
     */
    public void setLevel(UUID owner, int level) {
        Objects.requireNonNull(owner, "owner");
        if (level < 0) throw new IllegalArgumentException("level must be >= 0, got " + level);
        IslandData d = getOrCreateIslandData(owner);
        islandData.put(owner, new IslandData(d.owner(), d.trustees, level, d.blocksPlaced()));
    }

    /**
     * Adds a trustee to the owner's island data, if not already present.
     *
     * @param owner   the island owner's UUID
     * @param trustee the trustee's UUID
     * @return {@code true} if the trustee was added
     */
    public boolean addTrustee(UUID owner, UUID trustee) {
        Objects.requireNonNull(owner, "owner");
        Objects.requireNonNull(trustee, "trustee");
        IslandData d = getOrCreateIslandData(owner);
        if (d.trustees.contains(trustee)) return false;
        d.trustees.add(trustee);
        return true;
    }

    /**
     * Removes a trustee from the owner's island data.
     *
     * @param owner   the island owner's UUID
     * @param trustee the trustee's UUID
     * @return {@code true} if the trustee was removed
     */
    public boolean removeTrustee(UUID owner, UUID trustee) {
        Objects.requireNonNull(owner, "owner");
        Objects.requireNonNull(trustee, "trustee");
        IslandData d = islandData.get(owner);
        return d != null && d.trustees.remove(trustee);
    }

    /**
     * Increments the blocks-placed counter for the owner's island data.
     *
     * @param owner  the island owner's UUID
     * @param amount the number of blocks to add (must be >= 0)
     */
    public void addBlocksPlaced(UUID owner, long amount) {
        Objects.requireNonNull(owner, "owner");
        if (amount < 0) throw new IllegalArgumentException("amount must be >= 0, got " + amount);
        IslandData d = getOrCreateIslandData(owner);
        islandData.put(owner, new IslandData(d.owner(), d.trustees, d.level(), d.blocksPlaced() + amount));
    }

    // -------------------------------------------------------------------------
    // Biome / unlock / visitor / visit-log API
    // -------------------------------------------------------------------------

    public String getIslandBiome(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        return islandBiome.getOrDefault(playerId, "PLAINS");
    }

    public void setIslandBiome(UUID playerId, String biome) {
        Objects.requireNonNull(playerId, "playerId");
        islandBiome.put(playerId, biome);
    }

    public boolean isIslandUnlocked(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        return islandUnlocked.getOrDefault(playerId, false);
    }

    public void setIslandUnlocked(UUID playerId, boolean unlocked) {
        Objects.requireNonNull(playerId, "playerId");
        islandUnlocked.put(playerId, unlocked);
    }

    public int getVisitorCount(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        return visitorCounts.getOrDefault(playerId, 0);
    }

    public void addVisitor(UUID islandOwner) {
        Objects.requireNonNull(islandOwner, "islandOwner");
        visitorCounts.merge(islandOwner, 1, Integer::sum);
    }

    public void setVisitorCount(UUID islandOwner, int count) {
        Objects.requireNonNull(islandOwner, "islandOwner");
        visitorCounts.put(islandOwner, count);
    }

    public void recordVisit(UUID visitorId, String islandOwnerName) {
        Objects.requireNonNull(visitorId, "visitorId");
        visitLog.computeIfAbsent(visitorId, k -> new ArrayList<>()).add(islandOwnerName);
    }

    public List<String> getVisitLog(UUID visitorId) {
        Objects.requireNonNull(visitorId, "visitorId");
        return Collections.unmodifiableList(visitLog.getOrDefault(visitorId, Collections.emptyList()));
    }

    public Map<UUID, String> getAllIslandBiomes() {
        return Collections.unmodifiableMap(islandBiome);
    }

    public Map<UUID, Boolean> getAllIslandUnlocked() {
        return Collections.unmodifiableMap(islandUnlocked);
    }

    public Map<UUID, Integer> getAllIslandLevels() {
        Map<UUID, Integer> result = new HashMap<>();
        for (Map.Entry<UUID, IslandData> e : islandData.entrySet()) {
            result.put(e.getKey(), e.getValue().level());
        }
        return Collections.unmodifiableMap(result);
    }

    public Map<UUID, Integer> getAllVisitorCounts() {
        return Collections.unmodifiableMap(visitorCounts);
    }

    public Map<UUID, List<String>> getAllVisitLog() {
        return Collections.unmodifiableMap(visitLog);
    }

    public Map<UUID, List<UUID>> getAllIslandMembers() {
        Map<UUID, List<UUID>> result = new HashMap<>();
        for (Map.Entry<UUID, SkyBlockIsland> e : islands.entrySet()) {
            result.put(e.getKey(), e.getValue().getMembers());
        }
        return Collections.unmodifiableMap(result);
    }

    // -------------------------------------------------------------------------
    // IslandData persistence
    // -------------------------------------------------------------------------

    public void load(File dataFolder) {
        File file = new File(dataFolder, "island.yml");
        if (!file.exists()) {
            return;
        }
        YamlConfiguration cfg = YamlConfiguration.loadConfiguration(file);
        islandData.clear();
        islandHistory.clear();
        islandBiome.clear();
        islandUnlocked.clear();
        visitorCounts.clear();
        visitLog.clear();
        for (String key : cfg.getKeys(false)) {
            try {
                UUID owner = UUID.fromString(key);
                int level = cfg.getInt(key + ".level", 0);
                long blocks = cfg.getLong(key + ".blocksPlaced", 0L);
                List<UUID> trustees = new ArrayList<>();
                List<?> raw = cfg.getList(key + ".trustees", Collections.emptyList());
                for (Object o : raw) {
                    try {
                        trustees.add(UUID.fromString(String.valueOf(o)));
                    } catch (IllegalArgumentException ignored) {
                        // skip malformed trustee entries
                    }
                }
                islandData.put(owner, new IslandData(owner, trustees, level, blocks));
            } catch (IllegalArgumentException ignored) {
                // skip malformed UUID keys
            }
        }
        if (cfg.isConfigurationSection("islandHistory")) {
            for (String key : cfg.getConfigurationSection("islandHistory").getKeys(false)) {
                try {
                    List<String> entries = cfg.getStringList("islandHistory." + key);
                    islandHistory.put(UUID.fromString(key), new ArrayList<>(entries));
                } catch (IllegalArgumentException ignored) {}
            }
        }
        if (cfg.isConfigurationSection("islandBiome")) {
            for (String key : cfg.getConfigurationSection("islandBiome").getKeys(false)) {
                try {
                    islandBiome.put(UUID.fromString(key), cfg.getString("islandBiome." + key, "PLAINS"));
                } catch (IllegalArgumentException ignored) {}
            }
        }
        if (cfg.isConfigurationSection("islandUnlocked")) {
            for (String key : cfg.getConfigurationSection("islandUnlocked").getKeys(false)) {
                try {
                    islandUnlocked.put(UUID.fromString(key), cfg.getBoolean("islandUnlocked." + key));
                } catch (IllegalArgumentException ignored) {}
            }
        }
        if (cfg.isConfigurationSection("visitorCounts")) {
            for (String key : cfg.getConfigurationSection("visitorCounts").getKeys(false)) {
                try {
                    visitorCounts.put(UUID.fromString(key), cfg.getInt("visitorCounts." + key));
                } catch (IllegalArgumentException ignored) {}
            }
        }
        if (cfg.isConfigurationSection("visitLog")) {
            for (String key : cfg.getConfigurationSection("visitLog").getKeys(false)) {
                try {
                    List<String> entries = cfg.getStringList("visitLog." + key);
                    visitLog.put(UUID.fromString(key), new ArrayList<>(entries));
                } catch (IllegalArgumentException ignored) {}
            }
        }
    }

    public void save(File dataFolder) {
        File file = new File(dataFolder, "island.yml");
        YamlConfiguration cfg = new YamlConfiguration();
        for (Map.Entry<UUID, IslandData> entry : islandData.entrySet()) {
            String key = entry.getKey().toString();
            IslandData d = entry.getValue();
            cfg.set(key + ".level", d.level());
            cfg.set(key + ".blocksPlaced", d.blocksPlaced());
            List<String> trusteeStrings = new ArrayList<>();
            for (UUID t : d.trustees) {
                trusteeStrings.add(t.toString());
            }
            cfg.set(key + ".trustees", trusteeStrings);
        }
        for (Map.Entry<UUID, List<String>> entry : islandHistory.entrySet()) {
            cfg.set("islandHistory." + entry.getKey().toString(), entry.getValue());
        }
        for (Map.Entry<UUID, String> entry : islandBiome.entrySet()) {
            cfg.set("islandBiome." + entry.getKey().toString(), entry.getValue());
        }
        for (Map.Entry<UUID, Boolean> entry : islandUnlocked.entrySet()) {
            cfg.set("islandUnlocked." + entry.getKey().toString(), entry.getValue());
        }
        for (Map.Entry<UUID, Integer> entry : visitorCounts.entrySet()) {
            cfg.set("visitorCounts." + entry.getKey().toString(), entry.getValue());
        }
        for (Map.Entry<UUID, List<String>> entry : visitLog.entrySet()) {
            cfg.set("visitLog." + entry.getKey().toString(), entry.getValue());
        }
        try {
            cfg.save(file);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save island.yml", e);
        }
    }

    // -------------------------------------------------------------------------
    // World-based island API
    // -------------------------------------------------------------------------

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
        recordIslandEvent(owner, "Island deleted");
        return true;
    }
}
