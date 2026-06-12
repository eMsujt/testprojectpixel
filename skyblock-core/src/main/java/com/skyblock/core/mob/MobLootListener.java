package com.skyblock.core.mob;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

/**
 * Bukkit listener that replaces vanilla mob drops with SkyBlock loot from
 * {@link MobLootManager} whenever a player kills a mob.
 */
public final class MobLootListener implements Listener {

    private final MobLootManager mobLootManager;

    /**
     * Creates a listener backed by the given {@link MobLootManager}.
     *
     * @param mobLootManager the loot manager, must not be null
     */
    public MobLootListener(MobLootManager mobLootManager) {
        if (mobLootManager == null) {
            throw new IllegalArgumentException("mobLootManager must not be null");
        }
        this.mobLootManager = mobLootManager;
    }

    /**
     * Replaces vanilla drops with SkyBlock loot when a player kills a mob
     * whose type is registered in {@link MobLootManager}.
     *
     * @param event the entity death event fired by the server
     */
    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        Player killer = event.getEntity().getKiller();
        if (killer == null) {
            return;
        }

        List<ItemStack> customDrops = mobLootManager.rollLoot(event.getEntityType());
        if (customDrops.isEmpty()) {
            return;
        }

        event.getDrops().clear();
        event.getDrops().addAll(customDrops);
    }
}
