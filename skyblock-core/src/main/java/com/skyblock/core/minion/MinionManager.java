package com.skyblock.core.minion;

import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * Singleton managing placed minions for each player.
 *
 * <p>Tracks every {@link MinionData} instance by its unique ID, and
 * maintains an index from owner {@link UUID} to their minion list.
 * Not thread-safe; synchronize externally if accessed from multiple threads.</p>
 */
public final class MinionManager {

    /** All minion types available in SkyBlock. */
    public enum MinionType {
        WHEAT("Wheat Minion"),
        COBBLESTONE("Cobblestone Minion"),
        SNOW("Snow Minion"),
        CLAY("Clay Minion"),
        FISHING("Fishing Minion"),
        LOG("Log Minion"),
        OAK("Oak Minion"),
        COAL("Coal Minion"),
        IRON("Iron Minion"),
        GOLD("Gold Minion"),
        DIAMOND("Diamond Minion"),
        LAPIS("Lapis Minion"),
        REDSTONE("Redstone Minion"),
        EMERALD("Emerald Minion"),
        CARROT("Carrot Minion"),
        POTATO("Potato Minion"),
        MELON("Melon Minion"),
        PUMPKIN("Pumpkin Minion"),
        SUGAR_CANE("Sugar Cane Minion"),
        MUSHROOM("Mushroom Minion"),
        CACTUS("Cactus Minion"),
        FLOWER("Flower Minion"),
        SAND("Sand Minion"),
        GLOWSTONE("Glowstone Minion"),
        NETHER_WART("Nether Wart Minion"),
        QUARTZ("Quartz Minion"),
        CHICKEN("Chicken Minion"),
        COW("Cow Minion"),
        PIG("Pig Minion"),
        SHEEP("Sheep Minion"),
        RABBIT("Rabbit Minion"),
        ZOMBIE("Zombie Minion"),
        SKELETON("Skeleton Minion"),
        SPIDER("Spider Minion"),
        CREEPER("Creeper Minion"),
        BLAZE("Blaze Minion"),
        MAGMA_CUBE("Magma Cube Minion"),
        ENDERMAN("Enderman Minion"),
        GHAST("Ghast Minion"),
        SLIME("Slime Minion"),
        TARANTULA("Tarantula Minion"),
        ICE("Ice Minion"),
        GRAVEL("Gravel Minion"),
        OBSIDIAN("Obsidian Minion"),
        BIRCH("Birch Minion"),
        SPRUCE("Spruce Minion"),
        DARK_OAK("Dark Oak Minion"),
        JUNGLE("Jungle Minion"),
        ACACIA("Acacia Minion"),
        MITHRIL("Mithril Minion"),
        HARD_STONE("Hard Stone Minion"),
        GEMSTONE("Gemstone Minion");

        private final String displayName;

        MinionType(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    /** Upgrade tiers a minion can reach. */
    public enum MinionTier {
        TIER_1, TIER_2, TIER_3, TIER_4, TIER_5,
        TIER_6, TIER_7, TIER_8, TIER_9, TIER_10,
        TIER_11
    }

    /** Mutable state for a single placed minion. */
    public static final class MinionData {
        public final UUID id;
        public final UUID owner;
        public final MinionType type;
        private MinionTier tier;

        public MinionData(UUID id, UUID owner, MinionType type, MinionTier tier) {
            this.id = Objects.requireNonNull(id, "id");
            this.owner = Objects.requireNonNull(owner, "owner");
            this.type = Objects.requireNonNull(type, "type");
            this.tier = Objects.requireNonNull(tier, "tier");
        }

        public MinionTier getTier() {
            return tier;
        }

        public void setTier(MinionTier tier) {
            this.tier = Objects.requireNonNull(tier, "tier");
        }
    }

    private static final MinionManager INSTANCE = new MinionManager();

    /** All minions keyed by their UUID. */
    private final Map<UUID, MinionData> minions = new HashMap<>();

    /** Index from owner UUID to their list of minion UUIDs. */
    private final Map<UUID, List<UUID>> ownerIndex = new HashMap<>();

    /** Per-player placements: location key "world,x,y,z" → MinionType. */
    private final Map<UUID, Map<String, MinionType>> placements = new HashMap<>();

    private MinionManager() {
    }

    /**
     * Returns the single shared {@code MinionManager} instance.
     *
     * @return the singleton instance
     */
    public static MinionManager getInstance() {
        return INSTANCE;
    }

    /**
     * Places a new minion for the given player.
     *
     * @param owner the player placing the minion
     * @param type  the type of minion to place
     * @param tier  the starting tier of the minion
     * @return the newly created {@link MinionData}
     */
    public MinionData placeMinion(UUID owner, MinionType type, MinionTier tier) {
        Objects.requireNonNull(owner, "owner");
        Objects.requireNonNull(type, "type");
        Objects.requireNonNull(tier, "tier");
        UUID id = UUID.randomUUID();
        MinionData data = new MinionData(id, owner, type, tier);
        minions.put(id, data);
        ownerIndex.computeIfAbsent(owner, k -> new ArrayList<>()).add(id);
        return data;
    }

    /**
     * Removes the minion with the given ID.
     *
     * @param minionId the minion to remove
     * @return {@code true} if the minion existed and was removed
     */
    public boolean removeMinion(UUID minionId) {
        Objects.requireNonNull(minionId, "minionId");
        MinionData data = minions.remove(minionId);
        if (data == null) {
            return false;
        }
        List<UUID> ownerList = ownerIndex.get(data.owner);
        if (ownerList != null) {
            ownerList.remove(minionId);
            if (ownerList.isEmpty()) {
                ownerIndex.remove(data.owner);
            }
        }
        return true;
    }

    /**
     * Returns the {@link MinionData} for the given ID, or {@code null} if not found.
     *
     * @param minionId the minion ID to look up
     * @return the minion data, or {@code null}
     */
    public MinionData getMinion(UUID minionId) {
        Objects.requireNonNull(minionId, "minionId");
        return minions.get(minionId);
    }

    /**
     * Returns an unmodifiable list of all minion IDs placed by the given player.
     *
     * @param owner the player to look up
     * @return an unmodifiable list of minion UUIDs, empty if the player has none
     */
    public List<UUID> getMinions(UUID owner) {
        Objects.requireNonNull(owner, "owner");
        List<UUID> list = ownerIndex.get(owner);
        return list == null ? Collections.emptyList() : Collections.unmodifiableList(list);
    }

    /**
     * Upgrades the given minion to the next tier, if one exists.
     *
     * @param minionId the minion to upgrade
     * @return {@code true} if the minion was upgraded, {@code false} if already at max tier or not found
     */
    public boolean upgradeMinion(UUID minionId) {
        Objects.requireNonNull(minionId, "minionId");
        MinionData data = minions.get(minionId);
        if (data == null) {
            return false;
        }
        MinionTier[] tiers = MinionTier.values();
        int next = data.getTier().ordinal() + 1;
        if (next >= tiers.length) {
            return false;
        }
        data.setTier(tiers[next]);
        return true;
    }

    /**
     * Removes all minions belonging to the given player.
     *
     * @param owner the player whose minions should be cleared
     * @return the number of minions removed
     */
    public int clearMinions(UUID owner) {
        Objects.requireNonNull(owner, "owner");
        List<UUID> list = ownerIndex.remove(owner);
        if (list == null) {
            return 0;
        }
        for (UUID id : list) {
            minions.remove(id);
        }
        return list.size();
    }

    /**
     * Returns the {@link MinionType} placed at the given location key by the player, or {@code null}.
     *
     * @param owner    the player
     * @param location "world,x,y,z" string
     */
    public MinionType getPlacement(UUID owner, String location) {
        Objects.requireNonNull(owner, "owner");
        Objects.requireNonNull(location, "location");
        Map<String, MinionType> map = placements.get(owner);
        return map == null ? null : map.get(location);
    }

    /**
     * Records that the player placed the given minion type at the location key.
     *
     * @param owner    the player
     * @param location "world,x,y,z" string
     * @param type     the minion type placed
     */
    public void setPlacement(UUID owner, String location, MinionType type) {
        Objects.requireNonNull(owner, "owner");
        Objects.requireNonNull(location, "location");
        Objects.requireNonNull(type, "type");
        placements.computeIfAbsent(owner, k -> new HashMap<>()).put(location, type);
    }

    /**
     * Removes the placement record at the given location for the player.
     *
     * @param owner    the player
     * @param location "world,x,y,z" string
     * @return {@code true} if a record existed and was removed
     */
    public boolean removePlacement(UUID owner, String location) {
        Objects.requireNonNull(owner, "owner");
        Objects.requireNonNull(location, "location");
        Map<String, MinionType> map = placements.get(owner);
        if (map == null) return false;
        boolean removed = map.remove(location) != null;
        if (map.isEmpty()) placements.remove(owner);
        return removed;
    }

    /**
     * Returns an unmodifiable view of all placement entries for the given player.
     *
     * @param owner the player
     * @return map of "world,x,y,z" → MinionType, empty if none
     */
    public Map<String, MinionType> getPlacements(UUID owner) {
        Objects.requireNonNull(owner, "owner");
        Map<String, MinionType> map = placements.get(owner);
        return map == null ? Collections.emptyMap() : Collections.unmodifiableMap(map);
    }

    public void load(File dataFolder) {
        File file = new File(dataFolder, "minions.yml");
        if (!file.exists()) {
            return;
        }
        YamlConfiguration cfg = YamlConfiguration.loadConfiguration(file);
        minions.clear();
        ownerIndex.clear();
        placements.clear();
        for (String key : cfg.getKeys(false)) {
            try {
                UUID id = UUID.fromString(key);
                String ownerStr = cfg.getString(key + ".owner");
                String typeName = cfg.getString(key + ".type");
                String tierName = cfg.getString(key + ".tier");
                if (ownerStr == null || typeName == null || tierName == null) continue;
                UUID owner = UUID.fromString(ownerStr);
                MinionType type = MinionType.valueOf(typeName);
                MinionTier tier = MinionTier.valueOf(tierName);
                MinionData data = new MinionData(id, owner, type, tier);
                minions.put(id, data);
                ownerIndex.computeIfAbsent(owner, k -> new ArrayList<>()).add(id);
            } catch (IllegalArgumentException ignored) {
                // skip malformed entries
            }
        }
        // load per-player placements
        if (cfg.isConfigurationSection("placements")) {
            for (String ownerStr : cfg.getConfigurationSection("placements").getKeys(false)) {
                try {
                    UUID owner = UUID.fromString(ownerStr);
                    Map<String, MinionType> map = new HashMap<>();
                    String path = "placements." + ownerStr;
                    if (cfg.isConfigurationSection(path)) {
                        for (String loc : cfg.getConfigurationSection(path).getKeys(false)) {
                            String typeName = cfg.getString(path + "." + loc);
                            if (typeName != null) {
                                try {
                                    map.put(loc, MinionType.valueOf(typeName));
                                } catch (IllegalArgumentException ignored) {
                                    // skip unknown type
                                }
                            }
                        }
                    }
                    if (!map.isEmpty()) {
                        placements.put(owner, map);
                    }
                } catch (IllegalArgumentException ignored) {
                    // skip malformed owner UUID
                }
            }
        }
    }

    public void save(File dataFolder) {
        File file = new File(dataFolder, "minions.yml");
        YamlConfiguration cfg = new YamlConfiguration();
        for (MinionData data : minions.values()) {
            String key = data.id.toString();
            cfg.set(key + ".owner", data.owner.toString());
            cfg.set(key + ".type", data.type.name());
            cfg.set(key + ".tier", data.getTier().name());
        }
        for (Map.Entry<UUID, Map<String, MinionType>> entry : placements.entrySet()) {
            String path = "placements." + entry.getKey().toString();
            for (Map.Entry<String, MinionType> loc : entry.getValue().entrySet()) {
                cfg.set(path + "." + loc.getKey(), loc.getValue().name());
            }
        }
        try {
            cfg.save(file);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save minions.yml", e);
        }
    }
}
