package com.skyblock.plugin.collections;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

/**
 * @deprecated Use {@link com.skyblock.plugin.collection.CollectionListener} instead.
 */
@Deprecated
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
