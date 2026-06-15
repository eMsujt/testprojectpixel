package com.skyblock.core.mob;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.world.EntitiesUnloadEvent;

import java.util.UUID;

/**
 * Bukkit listener for custom-mob lifecycle events.
 *
 * <p>On entity death it grants the killer coin and XP rewards defined in the
 * mob's {@link MobManager.MobDefinition} and removes the entity from
 * {@link CustomMobManager}. On chunk/entity unload it cleans up any tracked
 * entities that are no longer in the world.</p>
 */
public final class CustomMobListener implements Listener {

    private final CustomMobManager customMobManager;

    /**
     * Creates a listener backed by the given {@link CustomMobManager}.
     *
     * @param customMobManager the manager; must not be null
     */
    public CustomMobListener(CustomMobManager customMobManager) {
        if (customMobManager == null) {
            throw new IllegalArgumentException("customMobManager must not be null");
        }
        this.customMobManager = customMobManager;
    }

    /**
     * Grants coin and XP rewards to the killer and removes the entity from
     * tracking when a custom mob dies.
     *
     * @param event the entity death event
     */
    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        UUID entityId = event.getEntity().getUniqueId();
        if (!customMobManager.isCustomMob(entityId)) {
            return;
        }

        MobManager.MobDefinition def = customMobManager.getDefinition(entityId);
        customMobManager.remove(entityId);

        Player killer = event.getEntity().getKiller();
        if (killer == null || def == null) {
            return;
        }

        if (def.getCoinReward() > 0) {
            com.skyblock.core.economy.manager.EconomyManager.getInstance()
                    .addBalance(killer.getUniqueId(), def.getCoinReward());
            killer.sendMessage("§6+" + def.getCoinReward() + " coins");
        }

        if (def.getXpReward() > 0) {
            killer.giveExp(def.getXpReward());
            killer.sendMessage("§a+" + def.getXpReward() + " XP");
        }
    }

    /**
     * Cleans up tracked custom-mob entries when entities are unloaded with
     * their chunk so the map does not accumulate stale entries.
     *
     * @param event the entities-unload event
     */
    @EventHandler
    public void onEntitiesUnload(EntitiesUnloadEvent event) {
        for (org.bukkit.entity.Entity entity : event.getEntities()) {
            customMobManager.remove(entity.getUniqueId());
        }
    }
}
