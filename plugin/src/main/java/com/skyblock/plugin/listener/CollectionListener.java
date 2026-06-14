package com.skyblock.plugin.listener;

import com.skyblock.plugin.collection.CollectionManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

/**
 * Awards collection progress through {@link CollectionManager} whenever a player
 * breaks a block and announces any newly unlocked collection tiers.
 */
public final class CollectionListener implements Listener {

    private final CollectionManager collectionManager = CollectionManager.getInstance();

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        int unlocked = collectionManager.addCollection(player.getUniqueId(), event.getBlock().getType(), 1L);
        if (unlocked > 0) {
            int tier = collectionManager.getTier(player.getUniqueId(), event.getBlock().getType());
            player.sendMessage("§a§lCOLLECTION UNLOCKED §7"
                    + event.getBlock().getType().name().toLowerCase() + " §eTier " + tier);
        }
    }
}
