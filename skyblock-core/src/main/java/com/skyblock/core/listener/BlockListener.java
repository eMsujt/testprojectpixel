package com.skyblock.core.listener;

import com.skyblock.core.manager.CollectionManager;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import java.util.UUID;

/**
 * Generic block-break listener.
 *
 * <p>Forwards every block a player breaks to {@link CollectionManager} so the
 * material is credited to that player's collection, mirroring the routing used
 * by the gathering listeners.</p>
 */
public final class BlockListener implements Listener {

    private static final BlockListener INSTANCE = new BlockListener();

    private BlockListener() {}

    public static BlockListener getInstance() {
        return INSTANCE;
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (event.isCancelled()) return;
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();
        Material material = event.getBlock().getType();

        CollectionManager.getInstance().addCollection(uuid, material, 1);
    }
}
