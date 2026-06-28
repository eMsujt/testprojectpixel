package com.skyblock.core.mob;

import org.bukkit.entity.Monster;
import org.bukkit.entity.Wolf;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;

/**
 * Keeps the Hub free of random vanilla mobs: hostile mobs there come only from
 * the {@code /setmobspawn} spawn points (the Hypixel model), so this cancels
 * natural vanilla hostile/wolf spawns on the Hub island. Custom mobs spawned by
 * {@link CustomMobManager} use the {@code CUSTOM} reason and are never cancelled.
 */
public final class MobSpawnListener implements Listener {

    @EventHandler
    public void onSpawn(CreatureSpawnEvent event) {
        if (event.getSpawnReason() != CreatureSpawnEvent.SpawnReason.NATURAL) {
            return;
        }
        if (event.getLocation().getWorld() == null
                || !event.getLocation().getWorld().getName().equalsIgnoreCase("Hub")) {
            return;
        }
        if (event.getEntity() instanceof Monster || event.getEntity() instanceof Wolf) {
            event.setCancelled(true);
        }
    }
}
