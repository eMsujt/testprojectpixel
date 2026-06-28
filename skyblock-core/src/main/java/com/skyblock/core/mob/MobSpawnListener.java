package com.skyblock.core.mob;

import com.skyblock.core.manager.MobManager;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;

/**
 * Turns natural vanilla spawns on the Hub island into their custom SkyBlock
 * counterparts so the Hub populates with 1:1 mobs: surface Zombies become
 * Graveyard Zombies and Wolves become Hub Wolves. The rarer, area-specific mobs
 * (Crypt Ghoul, Golden Ghoul, Zombie Villager, Old Wolf) are placed with the
 * {@code /spawnmob} admin command rather than blanket conversion, since their
 * spawn regions (Crypts, Ruins, night-only) aren't known from the world alone.
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
        if (!(event.getEntity() instanceof LivingEntity entity)) {
            return;
        }
        String mobId = switch (event.getEntityType()) {
            case ZOMBIE -> "graveyard_zombie";
            case WOLF -> "wolf";
            default -> null;
        };
        if (mobId == null) {
            return;
        }
        MobManager.MobDefinition def = MobManager.getInstance().getMob(mobId);
        if (def != null) {
            CustomMobManager.getInstance().applyTo(entity, def);
        }
    }
}
