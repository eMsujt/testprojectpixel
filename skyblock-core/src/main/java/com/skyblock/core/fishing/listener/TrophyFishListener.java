package com.skyblock.core.fishing.listener;

import com.skyblock.core.fishing.manager.FishingManager;
import com.skyblock.core.fishing.manager.TrophyFishManager;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * Bukkit listener that hooks into {@link PlayerFishEvent} to award trophy
 * fish catches via {@link TrophyFishManager}.
 *
 * <p>On every successful catch, the player's fishing level is used to roll
 * {@link TrophyFishManager#rollTrophyFish(int)}. If a trophy fish drops the
 * catch is recorded and the player is notified. Player data is cleaned up on
 * quit via {@link PlayerQuitEvent}.</p>
 */
public final class TrophyFishListener implements Listener {

    private final TrophyFishManager trophyFishManager;
    private final FishingManager fishingManager;

    /**
     * Creates a listener backed by the given managers.
     *
     * @param trophyFishManager trophy fish manager, must not be null
     * @param fishingManager    fishing manager used to look up player levels, must not be null
     * @throws IllegalArgumentException if either argument is null
     */
    public TrophyFishListener(TrophyFishManager trophyFishManager, FishingManager fishingManager) {
        if (trophyFishManager == null) {
            throw new IllegalArgumentException("trophyFishManager must not be null");
        }
        if (fishingManager == null) {
            throw new IllegalArgumentException("fishingManager must not be null");
        }
        this.trophyFishManager = trophyFishManager;
        this.fishingManager = fishingManager;
    }

    /**
     * Checks for a trophy fish drop on every successful fish catch.
     *
     * @param event the fishing event fired by the server
     */
    @EventHandler
    public void onPlayerFish(PlayerFishEvent event) {
        if (event.getState() != PlayerFishEvent.State.CAUGHT_FISH) {
            return;
        }

        Player player = event.getPlayer();
        int level = fishingManager.getLevel(player.getUniqueId());
        TrophyFishManager.TrophyFish caught = trophyFishManager.rollTrophyFish(level);
        if (caught == null) {
            return;
        }

        trophyFishManager.addCatch(player.getUniqueId(), caught);
        player.sendMessage("§6[Trophy Fishing] §eYou caught a §f"
                + caught.name().replace('_', ' ') + "§e!");
    }

    /**
     * Removes cached trophy fish data when a player disconnects.
     *
     * @param event the quit event
     */
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        trophyFishManager.remove(event.getPlayer().getUniqueId());
    }
}
