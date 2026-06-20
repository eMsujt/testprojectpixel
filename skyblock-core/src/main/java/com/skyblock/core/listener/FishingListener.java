package com.skyblock.core.listener;

import com.skyblock.core.manager.FishingManager;
import org.bukkit.Material;
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

        int level = fishingManager.getLevel(uuid);
        ItemStack loot = fishingManager.rollLoot(level);

        boolean isTreasure = loot.getType() == Material.MAP;
        double xp = isTreasure ? FishingManager.XP_TREASURE : FishingManager.XP_PER_CATCH;
        fishingManager.addXp(uuid, xp);
        fishingManager.addFishCaught(uuid);

        player.getWorld().dropItemNaturally(event.getHook().getLocation(), loot);

        FishingManager.SeaCreature creature = fishingManager.rollSeaCreature(level);

        String summary = "Caught " + loot.getType().name()
                + (creature != null ? " + sea creature: " + creature.name() : "");
        fishingManager.recordCatchEvent(uuid, summary);

        player.sendMessage("§9[Fishing] §fYou caught §e" + loot.getType().name().replace('_', ' ')
                + "§f! §7(+" + (int) xp + " XP)"
                + (creature != null ? " §c+ " + creature.name().replace('_', ' ') : ""));
    }
}
