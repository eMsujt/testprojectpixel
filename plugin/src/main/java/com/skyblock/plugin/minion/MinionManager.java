package com.skyblock.plugin.minion;

import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * In-world registry of every placed minion, keyed by block location.
 *
 * <p>Register the singleton as a Bukkit listener and drive its lifecycle from
 * {@code onEnable}/{@code onDisable}. Placed minions are tracked when placed via
 * {@link #placeMinion(Location, Minion)} and removed via {@link #removeMinion(Location)}.</p>
 */
public final class MinionManager implements Listener {

    /** Pairs a placed minion with the block location it occupies. */
    public static final class MinionData {
        private final Location location;
        private final Minion minion;

        MinionData(Location location, Minion minion) {
            this.location = location;
            this.minion = minion;
        }

        public Location getLocation() {
            return location;
        }

        public Minion getMinion() {
            return minion;
        }
    }

    private static final MinionManager INSTANCE = new MinionManager();

    private final Map<String, MinionData> byLocation = new HashMap<>();

    private MinionManager() {}

    public static MinionManager getInstance() {
        return INSTANCE;
    }

    public void onEnable(JavaPlugin plugin) {
        // lifecycle hook — no persistent state to restore
    }

    public void onDisable() {
        byLocation.clear();
    }

    /**
     * Registers a placed minion at the given block location.
     *
     * @param location block the minion occupies
     * @param minion   the placed minion
     */
    public void placeMinion(Location location, Minion minion) {
        Objects.requireNonNull(location, "location");
        Objects.requireNonNull(minion, "minion");
        byLocation.put(key(location), new MinionData(location, minion));
    }

    /**
     * Returns the {@link MinionData} at the given block location, or {@code null}.
     *
     * @param location block location to look up
     */
    public MinionData getMinion(Location location) {
        Objects.requireNonNull(location, "location");
        return byLocation.get(key(location));
    }

    /**
     * Removes the minion at the given block location.
     *
     * @param location block location
     * @return {@code true} if a minion was removed
     */
    public boolean removeMinion(Location location) {
        Objects.requireNonNull(location, "location");
        return byLocation.remove(key(location)) != null;
    }

    /** Returns an unmodifiable view of all placed minions. */
    public Collection<MinionData> getMinions() {
        return Collections.unmodifiableCollection(byLocation.values());
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK || event.getClickedBlock() == null) {
            return;
        }
        MinionData data = getMinion(event.getClickedBlock().getLocation());
        if (data == null) {
            return;
        }
        event.setCancelled(true);
        Minion m = data.getMinion();
        event.getPlayer().sendMessage(m.type.getDisplayName() + " (tier " + (m.getTier().ordinal() + 1) + ").");
    }

    private static String key(Location loc) {
        return loc.getWorld().getName() + "," + loc.getBlockX() + "," + loc.getBlockY() + "," + loc.getBlockZ();
    }
}
