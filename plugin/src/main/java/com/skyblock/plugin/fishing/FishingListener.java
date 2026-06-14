package com.skyblock.plugin.fishing;

import com.skyblock.plugin.managers.FishingManager;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerFishEvent;

import java.util.Map;

public final class FishingListener implements Listener {

    private static final Map<Material, Integer> FISH_XP = Map.of(
            Material.COD,           5,
            Material.SALMON,        6,
            Material.PUFFER_FISH,   8,
            Material.TROPICAL_FISH, 6
    );

    private final FishingManager fishingManager = FishingManager.getInstance();

    @EventHandler
    public void onPlayerFish(PlayerFishEvent event) {
        if (event.getState() != PlayerFishEvent.State.CAUGHT_FISH) {
            return;
        }
        if (!(event.getCaught() instanceof Item)) {
            return;
        }
        Item caught = (Item) event.getCaught();
        Player player = event.getPlayer();
        Material type = caught.getItemStack().getType();
        String fishType = type.name().toLowerCase();
        int xp = FISH_XP.getOrDefault(type, 3);
        fishingManager.addFishingXp(player.getUniqueId(), xp);
        fishingManager.addFishCount(player.getUniqueId(), fishType, 1L);
        fishingManager.recordCatchEvent(player.getUniqueId(), fishType + " COMMON");
    }
}
