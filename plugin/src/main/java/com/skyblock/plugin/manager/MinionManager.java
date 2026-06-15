package com.skyblock.plugin.manager;

import org.bukkit.Location;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * In-memory registry of every active minion players have placed.
 *
 * <p>Keyed by the owning player's UUID, each mapping to the list of that
 * player's {@link PlacedMinion}s in placement order. Not thread-safe;
 * access from the main server thread.</p>
 */
public final class MinionManager {

    /** A single placed minion. */
    public static final class PlacedMinion {

        private final UUID owner;
        private final Location location;
        private final String type;
        private int tier;

        public PlacedMinion(UUID owner, Location location, String type, int tier) {
            this.owner = Objects.requireNonNull(owner, "owner");
            this.location = Objects.requireNonNull(location, "location");
            this.type = Objects.requireNonNull(type, "type");
            this.tier = tier;
        }

        public UUID getOwner() {
            return owner;
        }

        public Location getLocation() {
            return location;
        }

        public String getType() {
            return type;
        }

        public int getTier() {
            return tier;
        }

        public void setTier(int tier) {
            this.tier = tier;
        }
    }

    /** How often (in ticks) the production tick fires for every placed minion. */
    private static final long TICK_PERIOD = 200L;

    private static final MinionManager INSTANCE = new MinionManager();

    private final Map<UUID, List<PlacedMinion>> minions = new HashMap<>();

    private MinionManager() {
    }

    public static MinionManager getInstance() {
        return INSTANCE;
    }

    /**
     * Starts the repeating production-tick task.
     *
     * @param plugin the owning plugin used to schedule the task
     */
    public void startTicking(Plugin plugin) {
        new BukkitRunnable() {
            @Override
            public void run() {
                for (List<PlacedMinion> list : minions.values()) {
                    for (PlacedMinion minion : list) {
                        tickMinion(minion);
                    }
                }
            }
        }.runTaskTimer(plugin, 0L, TICK_PERIOD);
    }

    private void tickMinion(PlacedMinion minion) {
        // production logic per minion type and tier goes here
    }

    /**
     * Tracks a newly placed minion for its owner.
     *
     * @param minion the minion to add
     */
    public void addMinion(PlacedMinion minion) {
        Objects.requireNonNull(minion, "minion");
        minions.computeIfAbsent(minion.getOwner(), k -> new ArrayList<>()).add(minion);
    }

    /**
     * Stops tracking a placed minion.
     *
     * @param minion the minion to remove
     * @return {@code true} if the minion was tracked and removed
     */
    public boolean removeMinion(PlacedMinion minion) {
        Objects.requireNonNull(minion, "minion");
        List<PlacedMinion> list = minions.get(minion.getOwner());
        if (list == null) {
            return false;
        }
        boolean removed = list.remove(minion);
        if (list.isEmpty()) {
            minions.remove(minion.getOwner());
        }
        return removed;
    }

    /**
     * Returns the first minion owned by the given player at the given block
     * location, or {@code null} if none.
     *
     * @param owner    the owning player's UUID
     * @param location the block location to look up
     * @return the matching {@link PlacedMinion}, or {@code null}
     */
    public PlacedMinion getMinion(UUID owner, Location location) {
        Objects.requireNonNull(owner, "owner");
        Objects.requireNonNull(location, "location");
        List<PlacedMinion> list = minions.get(owner);
        if (list == null) {
            return null;
        }
        for (PlacedMinion m : list) {
            Location loc = m.getLocation();
            if (loc.getWorld() == location.getWorld()
                    && loc.getBlockX() == location.getBlockX()
                    && loc.getBlockY() == location.getBlockY()
                    && loc.getBlockZ() == location.getBlockZ()) {
                return m;
            }
        }
        return null;
    }

    /**
     * Returns an unmodifiable view of the minions placed by the given player,
     * in placement order.
     *
     * @param owner the owning player's UUID
     * @return the player's minions, empty if they have none
     */
    public List<PlacedMinion> getMinions(UUID owner) {
        Objects.requireNonNull(owner, "owner");
        List<PlacedMinion> list = minions.get(owner);
        return list == null ? Collections.emptyList() : Collections.unmodifiableList(list);
    }
}
