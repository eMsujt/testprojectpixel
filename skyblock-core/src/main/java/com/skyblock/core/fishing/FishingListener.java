package com.skyblock.core.fishing;

import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.inventory.ItemStack;

/**
 * Bukkit listener that intercepts {@link PlayerFishEvent}, replaces vanilla
 * loot with SkyBlock loot from {@link FishingManager#rollLoot}, and awards
 * fishing XP on every successful catch.
 */
public final class FishingListener implements Listener {

    private final FishingManager fishingManager;

    /**
     * Creates a listener backed by the given {@link FishingManager}.
     *
     * @param fishingManager the fishing manager, must not be null
     * @throws IllegalArgumentException if {@code fishingManager} is null
     */
    public FishingListener(FishingManager fishingManager) {
        if (fishingManager == null) {
            throw new IllegalArgumentException("fishingManager must not be null");
        }
        this.fishingManager = fishingManager;
    }

    /**
     * Replaces vanilla fish drops with SkyBlock loot and awards fishing XP
     * when a player successfully catches something.
     *
     * @param event the fishing event fired by the server
     */
    @EventHandler
    public void onPlayerFish(PlayerFishEvent event) {
        if (event.getState() != PlayerFishEvent.State.CAUGHT_FISH) {
            return;
        }
        if (!(event.getCaught() instanceof Item)) {
            return;
        }

        Player player = event.getPlayer();
        int level = fishingManager.getLevel(player.getUniqueId());
        ItemStack loot = fishingManager.rollLoot(level);

        ((Item) event.getCaught()).setItemStack(loot);

        fishingManager.addXp(player.getUniqueId(), FishingManager.XP_PER_CATCH);
    }
}
