package com.skyblock.core.listener;

import com.skyblock.core.manager.FishingManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public final class FishingListener implements Listener {

    private static final FishingListener INSTANCE = new FishingListener();

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

        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();

        fishingManager.addXp(uuid, FishingManager.XP_PER_CATCH);
        fishingManager.addFishCaught(uuid);

        int level = fishingManager.getLevel(uuid);

        ItemStack loot = fishingManager.rollLoot(level);
        player.getWorld().dropItemNaturally(event.getHook().getLocation(), loot);

        FishingManager.SeaCreature creature = fishingManager.rollSeaCreature(level);

        String summary = "Caught " + loot.getType().name()
                + (creature != null ? " + sea creature: " + creature.name() : "");
        fishingManager.recordCatchEvent(uuid, summary);
    }
}
