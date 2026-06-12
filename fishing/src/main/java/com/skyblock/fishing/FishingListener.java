package com.skyblock.fishing;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerFishEvent;

/**
 * Bukkit listener that bridges {@link PlayerFishEvent} into the fishing
 * progression tracked by {@link FishingManager}.
 *
 * <p>Only events with state {@link PlayerFishEvent.State#CAUGHT_FISH} are
 * handled; every successful catch records the catch and awards
 * {@link #CATCH_XP} fishing experience to the player.</p>
 */
public final class FishingListener implements Listener {

    /** Fishing experience awarded per successful catch. */
    static final double CATCH_XP = 5.0;

    private final FishingManager fishingManager;

    /**
     * Creates a listener that records catches on the given manager.
     *
     * @param fishingManager the manager to record catches on, must not be null
     * @throws IllegalArgumentException if {@code fishingManager} is null
     */
    public FishingListener(FishingManager fishingManager) {
        if (fishingManager == null) {
            throw new IllegalArgumentException("fishingManager must not be null");
        }
        this.fishingManager = fishingManager;
    }

    /**
     * Records a catch when a player successfully reels in a fish.
     *
     * @param event the fish event fired by the server
     */
    @EventHandler
    public void onPlayerFish(PlayerFishEvent event) {
        if (event.getState() != PlayerFishEvent.State.CAUGHT_FISH) {
            return;
        }
        Player player = event.getPlayer();
        fishingManager.recordCatch(player.getUniqueId(), CATCH_XP);
    }
}
