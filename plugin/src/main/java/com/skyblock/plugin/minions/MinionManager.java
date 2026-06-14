package com.skyblock.plugin.minions;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * Singleton tracking the minions each player has placed.
 *
 * <p>Backed by a {@code Map<UUID, List<PlacedMinion>>} keyed by owner. Not
 * thread-safe; synchronize externally if accessed from multiple threads.</p>
 */
public final class MinionManager {

    /** All minion types available in SkyBlock. */
    public enum MinionType {
        COBBLESTONE("Cobblestone Minion"),
        WHEAT("Wheat Minion"),
        COAL("Coal Minion"),
        IRON("Iron Minion"),
        GOLD("Gold Minion"),
        DIAMOND("Diamond Minion"),
        LAPIS("Lapis Minion"),
        REDSTONE("Redstone Minion"),
        EMERALD("Emerald Minion"),
        SNOW("Snow Minion"),
        CLAY("Clay Minion"),
        FISHING("Fishing Minion"),
        LOG("Log Minion"),
        OAK("Oak Minion"),
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

    /** Upgrade tiers a minion can reach. Higher tiers produce faster. */
    public enum MinionTier {
        TIER_1(30), TIER_2(27), TIER_3(24), TIER_4(22), TIER_5(20),
        TIER_6(18), TIER_7(16), TIER_8(14), TIER_9(12), TIER_10(10),
        TIER_11(8);

        private final int intervalSeconds;

        MinionTier(int intervalSeconds) {
            this.intervalSeconds = intervalSeconds;
        }

        /** Seconds between each resource this tier produces. */
        public int getIntervalSeconds() {
            return intervalSeconds;
        }

        /** Ticks between each resource this tier produces. */
        public long getIntervalTicks() {
            return intervalSeconds * 20L;
        }
    }

    /** Mutable state for a single placed minion. */
    public static final class PlacedMinion {
        public final UUID id;
        public final UUID owner;
        public final MinionType type;
        private MinionTier tier;
        private String location;
        private int stored;
        private long ticksRemaining;

        public PlacedMinion(UUID id, UUID owner, MinionType type, MinionTier tier) {
            this.id = Objects.requireNonNull(id, "id");
            this.owner = Objects.requireNonNull(owner, "owner");
            this.type = Objects.requireNonNull(type, "type");
            this.tier = Objects.requireNonNull(tier, "tier");
            this.ticksRemaining = tier.getIntervalTicks();
        }

        public MinionType getType() {
            return type;
        }

        public MinionTier getTier() {
            return tier;
        }

        public void setTier(MinionTier tier) {
            this.tier = Objects.requireNonNull(tier, "tier");
        }

        /** Location key {@code "world,x,y,z"}, or {@code null} if not placed in the world yet. */
        public String getLocation() {
            return location;
        }

        public void setLocation(String location) {
            this.location = location;
        }

        /** Number of resources this minion has produced and not yet collected. */
        public int getStored() {
            return stored;
        }

        /**
         * Advances this minion by the given number of elapsed ticks, producing
         * one resource into storage each time its tier interval is reached.
         *
         * @param ticks elapsed ticks since the last advance
         */
        void advance(long ticks) {
            ticksRemaining -= ticks;
            while (ticksRemaining <= 0) {
                stored++;
                ticksRemaining += tier.getIntervalTicks();
            }
        }

        /**
         * Collects all stored resources, resetting the count to zero.
         *
         * @return the number of resources collected
         */
        public int collect() {
            int amount = stored;
            stored = 0;
            return amount;
        }
    }

    /** Base number of minion slots each player is allowed. */
    public static final int MAX_SLOTS = 11;

    /** Ticks between successive production runs of the global minion task. */
    private static final long TICK_PERIOD = 20L;

    private static final MinionManager INSTANCE = new MinionManager();

    /** Placed minions keyed by owner UUID. */
    private final Map<UUID, List<PlacedMinion>> minions = new HashMap<>();

    private MinionManager() {
    }

    public static MinionManager getInstance() {
        return INSTANCE;
    }

    /**
     * Starts the repeating production task. Every {@link #TICK_PERIOD} ticks
     * each placed minion is advanced, producing resources into its storage
     * once it reaches its tier's production interval.
     *
     * @param plugin the plugin used to schedule the task
     */
    public void start(JavaPlugin plugin) {
        Objects.requireNonNull(plugin, "plugin");
        new BukkitRunnable() {
            @Override
            public void run() {
                tick();
            }
        }.runTaskTimer(plugin, TICK_PERIOD, TICK_PERIOD);
    }

    /** Advances every placed minion by one {@link #TICK_PERIOD} production cycle. */
    void tick() {
        for (List<PlacedMinion> list : minions.values()) {
            for (PlacedMinion minion : list) {
                minion.advance(TICK_PERIOD);
            }
        }
    }

    /**
     * Places a new minion for the given player.
     *
     * @param owner the player placing the minion
     * @param type  the type of minion to place
     * @param tier  the starting tier of the minion
     * @return the newly created {@link PlacedMinion}
     * @throws IllegalStateException if the player is already at the minion slot cap
     */
    public PlacedMinion placeMinion(UUID owner, MinionType type, MinionTier tier) {
        Objects.requireNonNull(owner, "owner");
        Objects.requireNonNull(type, "type");
        Objects.requireNonNull(tier, "tier");
        List<PlacedMinion> list = minions.computeIfAbsent(owner, k -> new ArrayList<>());
        if (list.size() >= MAX_SLOTS) {
            throw new IllegalStateException("Minion slot cap reached (" + MAX_SLOTS + ")");
        }
        PlacedMinion minion = new PlacedMinion(UUID.randomUUID(), owner, type, tier);
        list.add(minion);
        return minion;
    }

    /**
     * Removes a tracked minion from the given player.
     *
     * @param owner  the player who owns the minion
     * @param minion the minion to remove
     * @return {@code true} if the minion was tracked and removed
     */
    public boolean removeMinion(UUID owner, PlacedMinion minion) {
        Objects.requireNonNull(owner, "owner");
        Objects.requireNonNull(minion, "minion");
        List<PlacedMinion> list = minions.get(owner);
        if (list == null) {
            return false;
        }
        boolean removed = list.remove(minion);
        if (list.isEmpty()) {
            minions.remove(owner);
        }
        return removed;
    }

    /**
     * Returns an unmodifiable view of the minions placed by the given player.
     *
     * @param owner the player to look up
     * @return the player's minions, empty if they have none
     */
    public List<PlacedMinion> getMinions(UUID owner) {
        Objects.requireNonNull(owner, "owner");
        List<PlacedMinion> list = minions.get(owner);
        return list == null ? Collections.emptyList() : Collections.unmodifiableList(list);
    }

    /**
     * Records the world location of the most recently placed, not-yet-located
     * minion of the given type for the player.
     *
     * @param owner    the player
     * @param location {@code "world,x,y,z"} key
     * @param type     the minion type that was placed
     */
    public void setPlacement(UUID owner, String location, MinionType type) {
        Objects.requireNonNull(owner, "owner");
        Objects.requireNonNull(location, "location");
        Objects.requireNonNull(type, "type");
        List<PlacedMinion> list = minions.get(owner);
        if (list == null) {
            return;
        }
        for (int i = list.size() - 1; i >= 0; i--) {
            PlacedMinion minion = list.get(i);
            if (minion.getType() == type && minion.getLocation() == null) {
                minion.setLocation(location);
                return;
            }
        }
    }

    /**
     * Removes all minions belonging to the given player.
     *
     * @param owner the player whose minions should be cleared
     * @return the number of minions removed
     */
    public int clearMinions(UUID owner) {
        Objects.requireNonNull(owner, "owner");
        List<PlacedMinion> list = minions.remove(owner);
        return list == null ? 0 : list.size();
    }
}
