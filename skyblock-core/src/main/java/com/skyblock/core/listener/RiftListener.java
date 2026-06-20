package com.skyblock.core.listener;

import com.skyblock.core.manager.RiftManager;
import org.bukkit.ChatColor;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

import java.util.UUID;

/**
 * Records Rift dimension mob kills.
 *
 * <p>When a player kills a mob whose custom name resolves to a
 * {@link RiftManager.RiftMobType} (e.g. a custom name of {@code "Bacte"} maps to
 * {@link RiftManager.RiftMobType#BACTE}), the kill is recorded against the
 * killer through {@link RiftManager#addKill}, but only while that player is
 * actually inside the Rift. Each kill costs a small amount of Rift time and
 * awards motes. Player death and quit are handled by {@link RiftManager} itself,
 * so they are deliberately not duplicated here.</p>
 */
public final class RiftListener implements Listener {

    /** Seconds of Rift time deducted per mob kill. */
    private static final long TIME_COST_SECONDS = 1L;

    /** Motes awarded per Rift mob kill. */
    private static final long MOTES_PER_KILL = 5L;

    private static final RiftListener INSTANCE = new RiftListener();

    private RiftListener() {}

    public static RiftListener getInstance() {
        return INSTANCE;
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        LivingEntity entity = event.getEntity();
        Player killer = entity.getKiller();
        if (killer == null) {
            return;
        }

        UUID id = killer.getUniqueId();
        RiftManager rift = RiftManager.getInstance();
        if (!rift.getRiftData(id).inRift) {
            return;
        }

        RiftManager.RiftMobType type = resolveMob(entity.getCustomName());
        if (type == null) {
            return;
        }

        rift.addKill(id, type, TIME_COST_SECONDS);
        rift.addMotes(id, MOTES_PER_KILL);
    }

    /**
     * Resolves a mob custom name to a Rift mob type, or {@code null} if the name
     * does not match a known Rift mob. Colour codes are stripped and spaces
     * normalised so {@code "§dRift Weirdo"} matches {@code RIFT_WEIRDO}.
     */
    private RiftManager.RiftMobType resolveMob(String customName) {
        if (customName == null) {
            return null;
        }
        String key = ChatColor.stripColor(customName).trim().toUpperCase().replace(' ', '_');
        try {
            return RiftManager.RiftMobType.valueOf(key);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
