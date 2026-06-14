package com.skyblock.plugin.collections;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public final class CollectionListener implements Listener {

    private final CollectionManager collectionManager = CollectionManager.getInstance();

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        collectionManager.addCollection(player.getUniqueId(), event.getBlock().getType(), 1L);
    }
}
