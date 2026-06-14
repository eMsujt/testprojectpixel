package com.skyblock.plugin.collection;

import com.skyblock.plugin.collections.CollectionManager;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

/**
 * Tracks per-player collection counts, incrementing the collection for the
 * broken block's material and announcing any tiers newly unlocked.
 */
public final class CollectionListener implements Listener {

    private final CollectionManager collectionManager = CollectionManager.getInstance();

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Material material = event.getBlock().getType();
        int unlocked = collectionManager.addCollection(player.getUniqueId(), material, 1L);
        if (unlocked > 0) {
            int tier = collectionManager.getTier(player.getUniqueId(), material);
            player.sendMessage("§a§lCOLLECTION UNLOCKED §7"
                    + material.name().toLowerCase() + " §eTier " + tier);
        }
    }
}
