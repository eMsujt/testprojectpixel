package com.skyblock.core.listener;

import com.skyblock.core.manager.FishingManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerFishEvent;

import java.util.UUID;

public final class FishingListener implements Listener {

    private static final FishingListener INSTANCE = new FishingListener();

    private static final double CATCH_XP = 10.0;

    private final FishingManager fishingManager = FishingManager.getInstance();

    private FishingListener() {}

    public static FishingListener getInstance() {
        return INSTANCE;
    }

    @EventHandler
    public void onPlayerFish(PlayerFishEvent event) {
        if (event.getState() != PlayerFishEvent.State.CAUGHT_FISH) {
            return;
        }
        UUID uuid = event.getPlayer().getUniqueId();
        fishingManager.addXp(uuid, CATCH_XP);
        fishingManager.addFishCaught(uuid);
    }
}
