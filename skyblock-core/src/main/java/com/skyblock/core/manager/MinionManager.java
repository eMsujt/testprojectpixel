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
import java.util.UUID;

/**
 * Canonical singleton for per-player SkyBlock minion management.
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
        TIER_11, TIER_12
    }

    /**
     * Fuels that can be inserted to speed up a minion's production.
     *
     * <p>Each fuel multiplies the production rate while it lasts and is
     * consumed after {@code durationTicks} production ticks; {@link #NONE}
     * represents an empty fuel slot.</p>
     */
    public enum MinionFuel {
        NONE(1.0, 0),
        COAL(1.05, 30 * 60),
        BLOCK_OF_COAL(1.10, 5 * 60 * 60),
        ENCHANTED_COAL(1.10, 3 * 60 * 60),
        ENCHANTED_LAVA_BUCKET(1.25, 24 * 60 * 60),
        ENCHANTED_BREAD(1.05, 12 * 60 * 60);

        private final double speedMultiplier;
        private final int durationTicks;

        MinionFuel(double speedMultiplier, int durationTicks) {
            this.speedMultiplier = speedMultiplier;
            this.durationTicks = durationTicks;
        }

        public double getSpeedMultiplier() {
            return speedMultiplier;
        }

        public int getDurationTicks() {
            return durationTicks;
        }
    }

    /**
     * Items that can be placed in a minion's two upgrade slots.
     *
     * <p>Most upgrades alter how a minion produces or compacts its yield;
     * the two hopper variants additionally auto-sell stored resources to the
     * NPC at their {@code hopperSellRate} (a rate of {@code 0} means the
     * upgrade is not a hopper). {@link #NONE} represents an empty slot.</p>
     */
    public enum MinionUpgrade {
        NONE(0.0),
        AUTO_SMELTER(0.0),
        COMPACTOR(0.0),
        SUPER_COMPACTOR_3000(0.0),
        DIAMOND_SPREADING(0.0),
        FLYCATCHER(0.0),
        BUDGET_HOPPER(0.50),
        ENCHANTED_HOPPER(0.90);

        private final double hopperSellRate;

        MinionUpgrade(double hopperSellRate) {
            this.hopperSellRate = hopperSellRate;
        }

        /** Fraction of an item's value paid out when auto-selling; 0 if not a hopper. */
        public double getHopperSellRate() {
            return hopperSellRate;
        }

        /** Whether this upgrade auto-sells stored resources to the NPC. */
        public boolean isHopper() {
            return hopperSellRate > 0.0;
        }
    }

    /** Number of upgrade slots every minion has. */
    public static final int UPGRADE_SLOTS = 2;

    /** Mutable state for a single placed minion. */
    public static final class MinionData {
        public final UUID id;
        public final UUID owner;
        public final MinionType type;
        private MinionTier tier;
        private int storedResources;
        private int productionProgress;
        private MinionFuel fuel = MinionFuel.NONE;
        private int fuelTicksRemaining;
        private final MinionUpgrade[] upgrades =
                {MinionUpgrade.NONE, MinionUpgrade.NONE};

        public MinionData(UUID id, UUID owner, MinionType type, MinionTier tier) {
            this.id = Objects.requireNonNull(id, "id");
            this.owner = Objects.requireNonNull(owner, "owner");
            this.type = Objects.requireNonNull(type, "type");
            this.tier = Objects.requireNonNull(tier, "tier");
        }

        /** The upgrade installed in the given slot (0 or 1), or {@link MinionUpgrade#NONE}. */
        public MinionUpgrade getUpgrade(int slot) {
            return upgrades[slot];
        }

        public MinionTier getTier() {
            return tier;
        }

        public void setTier(MinionTier tier) {
            this.tier = Objects.requireNonNull(tier, "tier");
        }

        /** Number of resources currently sitting in this minion's storage. */
        public int getStoredResources() {
            return storedResources;
        }

        /** The fuel currently powering this minion, or {@link MinionFuel#NONE}. */
        public MinionFuel getFuel() {
            return fuel;
        }

        /** Remaining production ticks before the active fuel is exhausted. */
        public int getFuelTicksRemaining() {
            return fuelTicksRemaining;
        }
    }

    /** Base number of minion slots each player is allowed. */
    public static final int MAX_SLOTS = 12;

    /** Production ticks a TIER_1 minion needs to produce one resource. */
    public static final int BASE_PRODUCTION_TICKS = 14;

    /** Storage capacity granted by the first tier; scales linearly per tier. */
    public static final int BASE_STORAGE = 64;

    private static final MinionManager INSTANCE = new MinionManager();

    /** All minions keyed by their UUID. */
    private final Map<UUID, MinionData> minions = new HashMap<>();

    /** Index from owner UUID to their list of minion UUIDs. */
    private final Map<UUID, List<UUID>> ownerIndex = new HashMap<>();

    /** Per-player placements: location key "world,x,y,z" → MinionType. */
    private final Map<UUID, Map<String, MinionType>> placements = new HashMap<>();

    /** Location key → minion UUID for placed-minion lookup by block position. */
    private final Map<String, UUID> locationIndex = new HashMap<>();

    /** Per-player slot cap overrides set by island upgrades; absent = MAX_SLOTS. */
    private final Map<UUID, Integer> playerMaxSlots = new HashMap<>();

    private MinionManager() {
    }

    public static MinionManager getInstance() {
        return INSTANCE;
    }

    /** Returns the effective minion slot cap for the given player (default {@link #MAX_SLOTS}). */
    public int getMaxSlots(UUID owner) {
        Objects.requireNonNull(owner, "owner");
        return playerMaxSlots.getOrDefault(owner, MAX_SLOTS);
    }

    /**
     * Sets a player's minion slot cap (used by island upgrade progression).
     * The new cap must be at least {@link #MAX_SLOTS}.
     */
    public void setMaxSlots(UUID owner, int slots) {
        Objects.requireNonNull(owner, "owner");
        if (slots < MAX_SLOTS) {
            throw new IllegalArgumentException("slots must be >= MAX_SLOTS (" + MAX_SLOTS + ")");
        }
        playerMaxSlots.put(owner, slots);
    }

    public MinionData placeMinion(UUID owner, MinionType type, MinionTier tier) {
        Objects.requireNonNull(owner, "owner");
        Objects.requireNonNull(type, "type");
        Objects.requireNonNull(tier, "tier");
        List<UUID> existing = ownerIndex.getOrDefault(owner, Collections.emptyList());
        int cap = getMaxSlots(owner);
        if (existing.size() >= cap) {
            throw new IllegalStateException("Minion slot cap reached (" + cap + ")");
        }
        UUID id = UUID.randomUUID();
        MinionData data = new MinionData(id, owner, type, tier);
        minions.put(id, data);
        ownerIndex.computeIfAbsent(owner, k -> new ArrayList<>()).add(id);
        return data;
    }

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
        locationIndex.values().remove(minionId);
        return true;
    }

    public MinionData getMinion(UUID minionId) {
        Objects.requireNonNull(minionId, "minionId");
        return minions.get(minionId);
    }

    public List<UUID> getMinions(UUID owner) {
        Objects.requireNonNull(owner, "owner");
        List<UUID> list = ownerIndex.get(owner);
        return list == null ? Collections.emptyList() : Collections.unmodifiableList(list);
    }

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

    /** Storage capacity for a minion at the given tier. */
    public int getStorageCapacity(MinionTier tier) {
        Objects.requireNonNull(tier, "tier");
        return BASE_STORAGE * (1 + tier.ordinal());
    }

    /**
     * Effective number of production ticks the minion needs to yield one
     * resource, accounting for its tier and any active fuel boost.
     */
    public int getProductionIntervalTicks(MinionData data) {
        Objects.requireNonNull(data, "data");
        int base = Math.max(1, BASE_PRODUCTION_TICKS - data.getTier().ordinal());
        double mult = (data.fuel != MinionFuel.NONE && data.fuelTicksRemaining > 0)
                ? data.fuel.getSpeedMultiplier() : 1.0;
        return Math.max(1, (int) Math.round(base / mult));
    }

    /**
     * Inserts fuel into the minion, replacing any currently active fuel and
     * resetting its remaining duration. Returns {@code false} if the minion is
     * unknown or {@code fuel} is {@link MinionFuel#NONE}.
     */
    public boolean addFuel(UUID minionId, MinionFuel fuel) {
        Objects.requireNonNull(minionId, "minionId");
        Objects.requireNonNull(fuel, "fuel");
        MinionData data = minions.get(minionId);
        if (data == null || fuel == MinionFuel.NONE) {
            return false;
        }
        data.fuel = fuel;
        data.fuelTicksRemaining = fuel.getDurationTicks();
        return true;
    }

    /**
     * Installs an upgrade in the given slot (0 or 1) of the minion, replacing
     * whatever was there. Returns {@code false} if the minion is unknown.
     */
    public boolean setUpgrade(UUID minionId, int slot, MinionUpgrade upgrade) {
        Objects.requireNonNull(minionId, "minionId");
        Objects.requireNonNull(upgrade, "upgrade");
        if (slot < 0 || slot >= UPGRADE_SLOTS) {
            throw new IllegalArgumentException("slot out of range: " + slot);
        }
        MinionData data = minions.get(minionId);
        if (data == null) {
            return false;
        }
        data.upgrades[slot] = upgrade;
        return true;
    }

    /**
     * Best hopper sell rate among the minion's upgrade slots, or {@code 0.0}
     * if no hopper is installed.
     */
    public double getHopperSellRate(MinionData data) {
        Objects.requireNonNull(data, "data");
        double rate = 0.0;
        for (MinionUpgrade upgrade : data.upgrades) {
            if (upgrade.getHopperSellRate() > rate) {
                rate = upgrade.getHopperSellRate();
            }
        }
        return rate;
    }

    /**
     * Auto-sells the minion's stored resources to the NPC via an installed
     * hopper, valuing each resource at {@code pricePerResource}. Empties storage
     * and returns the coins earned (floored) at the hopper's sell rate, or
     * {@code 0} if the minion is unknown, has no hopper, or is empty.
     */
    public long autoSell(UUID minionId, int pricePerResource) {
        Objects.requireNonNull(minionId, "minionId");
        MinionData data = minions.get(minionId);
        if (data == null) {
            return 0L;
        }
        double rate = getHopperSellRate(data);
        if (rate <= 0.0 || data.storedResources == 0) {
            return 0L;
        }
        long coins = (long) Math.floor((long) data.storedResources * pricePerResource * rate);
        data.storedResources = 0;
        return coins;
    }

    /**
     * Empties the minion's storage and returns the number of resources that
     * were collected. Returns {@code 0} if the minion is unknown or empty.
     */
    public int collectResources(UUID minionId) {
        Objects.requireNonNull(minionId, "minionId");
        MinionData data = minions.get(minionId);
        if (data == null) {
            return 0;
        }
        int amount = data.storedResources;
        data.storedResources = 0;
        return amount;
    }

    /**
     * Advances a single minion by one production tick: consumes fuel, accrues
     * production progress and, once the production interval is reached, adds a
     * resource to storage (unless full). Returns the number of resources
     * produced this tick (0 or 1).
     */
    public int tick(MinionData data) {
        Objects.requireNonNull(data, "data");
        int interval = getProductionIntervalTicks(data);
        if (data.fuel != MinionFuel.NONE && data.fuelTicksRemaining > 0) {
            data.fuelTicksRemaining--;
            if (data.fuelTicksRemaining == 0) {
                data.fuel = MinionFuel.NONE;
            }
        }
        int capacity = getStorageCapacity(data.getTier());
        if (data.storedResources >= capacity) {
            return 0;
        }
        data.productionProgress++;
        if (data.productionProgress < interval) {
            return 0;
        }
        data.productionProgress = 0;
        data.storedResources++;
        return 1;
    }

    /**
     * Advances the minion with the given ID by one production tick.
     * Returns the number of resources produced, or {@code 0} if unknown.
     */
    public int tick(UUID minionId) {
        Objects.requireNonNull(minionId, "minionId");
        MinionData data = minions.get(minionId);
        return data == null ? 0 : tick(data);
    }

    public int clearMinions(UUID owner) {
        Objects.requireNonNull(owner, "owner");
        List<UUID> list = ownerIndex.remove(owner);
        if (list == null) {
            return 0;
        }
        for (UUID id : list) {
            minions.remove(id);
            locationIndex.values().remove(id);
        }
        return list.size();
    }

    /** Returns an unmodifiable view of all placed minions across all owners. */
    public java.util.Collection<MinionData> getAllMinions() {
        return Collections.unmodifiableCollection(minions.values());
    }

    /**
     * Associates a placed minion with its block location key ("world,x,y,z").
     * Used to support fast lookup by block position.
     */
    public void setMinionLocation(UUID minionId, String locationKey) {
        Objects.requireNonNull(minionId, "minionId");
        Objects.requireNonNull(locationKey, "locationKey");
        locationIndex.put(locationKey, minionId);
    }

    /**
     * Returns the {@link MinionData} for the minion placed at the given location
     * key, or {@code null} if no minion is tracked there.
     */
    public MinionData getMinionAtLocation(String locationKey) {
        Objects.requireNonNull(locationKey, "locationKey");
        UUID id = locationIndex.get(locationKey);
        return id == null ? null : minions.get(id);
    }

    public MinionType getPlacement(UUID owner, String location) {
        Objects.requireNonNull(owner, "owner");
        Objects.requireNonNull(location, "location");
        Map<String, MinionType> map = placements.get(owner);
        return map == null ? null : map.get(location);
    }

    public void setPlacement(UUID owner, String location, MinionType type) {
        Objects.requireNonNull(owner, "owner");
        Objects.requireNonNull(location, "location");
        Objects.requireNonNull(type, "type");
        placements.computeIfAbsent(owner, k -> new HashMap<>()).put(location, type);
    }

    public boolean removePlacement(UUID owner, String location) {
        Objects.requireNonNull(owner, "owner");
        Objects.requireNonNull(location, "location");
        Map<String, MinionType> map = placements.get(owner);
        if (map == null) return false;
        boolean removed = map.remove(location) != null;
        if (map.isEmpty()) placements.remove(owner);
        return removed;
    }

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
        locationIndex.clear();
        playerMaxSlots.clear();
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
                data.storedResources = Math.max(0, cfg.getInt(key + ".stored", 0));
                data.productionProgress = Math.max(0, cfg.getInt(key + ".progress", 0));
                String fuelName = cfg.getString(key + ".fuel");
                if (fuelName != null) {
                    try {
                        data.fuel = MinionFuel.valueOf(fuelName);
                        data.fuelTicksRemaining = Math.max(0, cfg.getInt(key + ".fuelTicks", 0));
                    } catch (IllegalArgumentException ignored) {
                        // unknown fuel: leave at NONE
                    }
                }
                for (int slot = 0; slot < UPGRADE_SLOTS; slot++) {
                    String upgradeName = cfg.getString(key + ".upgrade" + slot);
                    if (upgradeName != null) {
                        try {
                            data.upgrades[slot] = MinionUpgrade.valueOf(upgradeName);
                        } catch (IllegalArgumentException ignored) {
                            // unknown upgrade: leave at NONE
                        }
                    }
                }
                minions.put(id, data);
                ownerIndex.computeIfAbsent(owner, k -> new ArrayList<>()).add(id);
            } catch (IllegalArgumentException ignored) {
                // skip malformed entries
            }
        }
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
        if (cfg.isConfigurationSection("locations")) {
            for (String locKey : cfg.getConfigurationSection("locations").getKeys(false)) {
                String minionIdStr = cfg.getString("locations." + locKey);
                if (minionIdStr != null) {
                    try {
                        locationIndex.put(locKey, UUID.fromString(minionIdStr));
                    } catch (IllegalArgumentException ignored) {
                        // skip malformed UUID
                    }
                }
            }
        }
        if (cfg.isConfigurationSection("slotOverrides")) {
            for (String ownerStr : cfg.getConfigurationSection("slotOverrides").getKeys(false)) {
                try {
                    UUID owner = UUID.fromString(ownerStr);
                    int slots = cfg.getInt("slotOverrides." + ownerStr, MAX_SLOTS);
                    if (slots >= MAX_SLOTS) {
                        playerMaxSlots.put(owner, slots);
                    }
                } catch (IllegalArgumentException ignored) {
                    // skip malformed UUID
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
            cfg.set(key + ".stored", data.storedResources);
            cfg.set(key + ".progress", data.productionProgress);
            if (data.fuel != MinionFuel.NONE) {
                cfg.set(key + ".fuel", data.fuel.name());
                cfg.set(key + ".fuelTicks", data.fuelTicksRemaining);
            }
            for (int slot = 0; slot < UPGRADE_SLOTS; slot++) {
                if (data.upgrades[slot] != MinionUpgrade.NONE) {
                    cfg.set(key + ".upgrade" + slot, data.upgrades[slot].name());
                }
            }
        }
        for (Map.Entry<UUID, Map<String, MinionType>> entry : placements.entrySet()) {
            String path = "placements." + entry.getKey().toString();
            for (Map.Entry<String, MinionType> loc : entry.getValue().entrySet()) {
                cfg.set(path + "." + loc.getKey(), loc.getValue().name());
            }
        }
        for (Map.Entry<String, UUID> entry : locationIndex.entrySet()) {
            cfg.set("locations." + entry.getKey(), entry.getValue().toString());
        }
        for (Map.Entry<UUID, Integer> entry : playerMaxSlots.entrySet()) {
            cfg.set("slotOverrides." + entry.getKey().toString(), entry.getValue());
        }
        try {
            cfg.save(file);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save minions.yml", e);
        }
    }
}
