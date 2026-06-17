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
 * Singleton tracking King's commissions handed out in the Dwarven Mines and
 * Crystal Hollows: each player's currently assigned commissions (with their
 * progress) and how many they have completed in total.
 *
 * <p>Not thread-safe; synchronize externally if accessed from multiple threads.</p>
 */
public final class CommissionManager {

    /** The two mining areas that issue King's commissions. */
    public enum CommissionLocation {
        DWARVEN_MINES("Dwarven Mines"),
        CRYSTAL_HOLLOWS("Crystal Hollows");

        private final String displayName;

        CommissionLocation(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    /**
     * The commissions the King can assign, each tied to the area it is issued
     * in and the target amount that completes it.
     */
    public enum CommissionType {
        // Dwarven Mines
        MITHRIL_MINER("Mithril Miner", CommissionLocation.DWARVEN_MINES, 500),
        TITANIUM_MINER("Titanium Miner", CommissionLocation.DWARVEN_MINES, 15),
        LAVA_SPRINGS_SLAYER("Lava Springs Slayer", CommissionLocation.DWARVEN_MINES, 250),
        CLIFFSIDE_VEINS_SLAYER("Cliffside Veins Slayer", CommissionLocation.DWARVEN_MINES, 250),
        GOBLIN_SLAYER("Goblin Slayer", CommissionLocation.DWARVEN_MINES, 13),
        ICE_WALKER_SLAYER("Ice Walker Slayer", CommissionLocation.DWARVEN_MINES, 50),
        // Crystal Hollows
        RUBY_GEMSTONE_COLLECTOR("Ruby Gemstone Collector", CommissionLocation.CRYSTAL_HOLLOWS, 1000),
        AMETHYST_GEMSTONE_COLLECTOR("Amethyst Gemstone Collector", CommissionLocation.CRYSTAL_HOLLOWS, 1000),
        JADE_GEMSTONE_COLLECTOR("Jade Gemstone Collector", CommissionLocation.CRYSTAL_HOLLOWS, 1000),
        AMBER_GEMSTONE_COLLECTOR("Amber Gemstone Collector", CommissionLocation.CRYSTAL_HOLLOWS, 1000),
        SAPPHIRE_GEMSTONE_COLLECTOR("Sapphire Gemstone Collector", CommissionLocation.CRYSTAL_HOLLOWS, 1000),
        TOPAZ_GEMSTONE_COLLECTOR("Topaz Gemstone Collector", CommissionLocation.CRYSTAL_HOLLOWS, 1000),
        GOBLIN_RAID_SLAYER("Goblin Raid Slayer", CommissionLocation.CRYSTAL_HOLLOWS, 100),
        AUTOMATON_SLAYER("Automaton Slayer", CommissionLocation.CRYSTAL_HOLLOWS, 50),
        TEAM_TREASURITE_MEMBER_SLAYER("Team Treasurite Member Slayer", CommissionLocation.CRYSTAL_HOLLOWS, 50);

        private final String displayName;
        private final CommissionLocation location;
        private final int target;

        CommissionType(String displayName, CommissionLocation location, int target) {
            this.displayName = displayName;
            this.location = location;
            this.target = target;
        }

        public String getDisplayName() {
            return displayName;
        }

        /** The area in which this commission is issued. */
        public CommissionLocation getLocation() {
            return location;
        }

        /** The amount of progress that completes this commission. */
        public int getTarget() {
            return target;
        }
    }

    /** A single assigned commission and the progress accumulated toward it. */
    public static final class Commission {
        private final CommissionType type;
        private int progress;

        public Commission(CommissionType type) {
            this.type = Objects.requireNonNull(type, "type");
        }

        public CommissionType getType() {
            return type;
        }

        public int getProgress() {
            return progress;
        }

        /**
         * Adds progress, clamped to the commission's target so it never reports
         * more than 100% complete.
         *
         * @param amount the progress to add (negative amounts are ignored)
         * @return the new progress total
         */
        public int addProgress(int amount) {
            if (amount > 0) {
                progress = Math.min(type.getTarget(), progress + amount);
            }
            return progress;
        }

        /** True once progress has reached the commission's target. */
        public boolean isComplete() {
            return progress >= type.getTarget();
        }
    }

    private static final CommissionManager INSTANCE = new CommissionManager();

    /** The number of commissions the King assigns at once. */
    public static final int COMMISSION_SLOTS = 2;

    /**
     * Stride used to rotate through a location's commission pool on each
     * generation so a player is not handed the same slots every time. Coprime
     * to both pool sizes (6 Dwarven Mines, 9 Crystal Hollows) to walk the full
     * pool before repeating.
     */
    private static final int GENERATION_STRIDE = 1;

    /** Per-player currently assigned commissions. */
    private final Map<UUID, List<Commission>> active = new HashMap<>();

    /** Per-player total commissions completed. */
    private final Map<UUID, Long> completed = new HashMap<>();

    private CommissionManager() {
    }

    public static CommissionManager getInstance() {
        return INSTANCE;
    }

    // -------------------------------------------------------------------------
    // Generation
    // -------------------------------------------------------------------------

    /**
     * Assigns a fresh set of {@link #COMMISSION_SLOTS} commissions drawn from the
     * given location's pool, replacing any the player currently holds. The slots
     * are distinct and rotate based on the player's completed total so repeated
     * generations cycle through the pool.
     *
     * @param playerId the player to assign commissions to
     * @param location the area issuing the commissions
     * @return the newly assigned commissions
     */
    public List<Commission> generateCommissions(UUID playerId, CommissionLocation location) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(location, "location");
        List<CommissionType> pool = new ArrayList<>();
        for (CommissionType type : CommissionType.values()) {
            if (type.getLocation() == location) {
                pool.add(type);
            }
        }
        List<Commission> assigned = new ArrayList<>();
        int base = (int) (getCompletedCount(playerId) % pool.size());
        for (int i = 0; i < COMMISSION_SLOTS && i < pool.size(); i++) {
            int index = (base + i * GENERATION_STRIDE) % pool.size();
            assigned.add(new Commission(pool.get(index)));
        }
        active.put(playerId, assigned);
        return Collections.unmodifiableList(assigned);
    }

    /**
     * Returns the player's currently assigned commissions.
     *
     * @param playerId the player to look up
     * @return the active commissions, empty if none assigned
     */
    public List<Commission> getActiveCommissions(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        List<Commission> list = active.get(playerId);
        return list == null ? Collections.emptyList() : Collections.unmodifiableList(list);
    }

    // -------------------------------------------------------------------------
    // Progress
    // -------------------------------------------------------------------------

    /**
     * Adds progress toward the player's active commission of the given type.
     *
     * @param playerId the player to update
     * @param type     the commission to advance
     * @param amount   the progress to add
     * @return the new progress total, or {@code -1} if the player has no active
     *         commission of that type
     */
    public int addProgress(UUID playerId, CommissionType type, int amount) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(type, "type");
        List<Commission> list = active.get(playerId);
        if (list != null) {
            for (Commission commission : list) {
                if (commission.getType() == type) {
                    return commission.addProgress(amount);
                }
            }
        }
        return -1;
    }

    /**
     * Claims a completed commission, removing it from the player's active slots
     * and incrementing their completed total.
     *
     * @param playerId the player claiming the commission
     * @param type     the commission to claim
     * @return {@code true} if a complete commission was claimed, {@code false} if
     *         the player has no such commission or it is not yet complete
     */
    public boolean claimCommission(UUID playerId, CommissionType type) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(type, "type");
        List<Commission> list = active.get(playerId);
        if (list == null) {
            return false;
        }
        for (int i = 0; i < list.size(); i++) {
            Commission commission = list.get(i);
            if (commission.getType() == type) {
                if (!commission.isComplete()) {
                    return false;
                }
                list.remove(i);
                completed.merge(playerId, 1L, Long::sum);
                return true;
            }
        }
        return false;
    }

    /**
     * Returns the total number of commissions the player has completed.
     *
     * @param playerId the player to look up
     * @return the completed total, {@code 0} if none
     */
    public long getCompletedCount(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        return completed.getOrDefault(playerId, 0L);
    }

    // -------------------------------------------------------------------------
    // Persistence
    // -------------------------------------------------------------------------

    public void load(File dataFolder) {
        File file = new File(dataFolder, "commissions.yml");
        if (!file.exists()) {
            return;
        }
        YamlConfiguration cfg = YamlConfiguration.loadConfiguration(file);
        active.clear();
        completed.clear();
        for (String key : cfg.getKeys(false)) {
            try {
                UUID uuid = UUID.fromString(key);
                if (cfg.isSet(key + ".completed")) {
                    completed.put(uuid, cfg.getLong(key + ".completed", 0L));
                }
                if (cfg.isConfigurationSection(key + ".active")) {
                    List<Commission> list = new ArrayList<>();
                    for (String typeName : cfg.getConfigurationSection(key + ".active").getKeys(false)) {
                        try {
                            CommissionType type = CommissionType.valueOf(typeName);
                            Commission commission = new Commission(type);
                            commission.addProgress(cfg.getInt(key + ".active." + typeName, 0));
                            list.add(commission);
                        } catch (IllegalArgumentException ignored) {
                            // skip unknown commission names
                        }
                    }
                    if (!list.isEmpty()) {
                        active.put(uuid, list);
                    }
                }
            } catch (IllegalArgumentException ignored) {
                // skip malformed UUIDs
            }
        }
    }

    public void save(File dataFolder) {
        File file = new File(dataFolder, "commissions.yml");
        YamlConfiguration cfg = new YamlConfiguration();
        for (Map.Entry<UUID, Long> entry : completed.entrySet()) {
            cfg.set(entry.getKey().toString() + ".completed", entry.getValue());
        }
        for (Map.Entry<UUID, List<Commission>> entry : active.entrySet()) {
            String key = entry.getKey().toString();
            for (Commission commission : entry.getValue()) {
                cfg.set(key + ".active." + commission.getType().name(), commission.getProgress());
            }
        }
        try {
            cfg.save(file);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save commissions.yml", e);
        }
    }

    // -------------------------------------------------------------------------
    // Lifecycle
    // -------------------------------------------------------------------------

    /**
     * Resets all commission data for the given player.
     *
     * @param playerId the player to reset
     */
    public void reset(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        active.remove(playerId);
        completed.remove(playerId);
    }

    /**
     * Removes all commission data for the given player (e.g. on quit).
     *
     * @param playerId the player to remove
     * @return {@code true} if the player had data, {@code false} otherwise
     */
    public boolean remove(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        boolean had = active.remove(playerId) != null;
        had |= completed.remove(playerId) != null;
        return had;
    }
}
