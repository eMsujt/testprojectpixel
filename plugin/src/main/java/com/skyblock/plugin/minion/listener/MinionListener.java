package com.skyblock.plugin.minion.listener;

import com.skyblock.core.minion.manager.MinionManager;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

/**
 * Listener that opens a {@link com.skyblock.core.minion.gui.MinionMenu} when a player
 * right-clicks the block occupied by one of their placed minions.
 */
public class MinionListener implements Listener {

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        Block block = event.getClickedBlock();
        if (block == null) {
            return;
        }

        String locationKey = block.getWorld().getName()
                + "," + block.getX()
                + "," + block.getY()
                + "," + block.getZ();

        MinionManager.MinionData data = MinionManager.getInstance().getMinionAtLocation(locationKey);
        if (data == null) {
            return;
        }

        event.setCancelled(true);
        Player player = event.getPlayer();
        new com.skyblock.core.minion.gui.MinionMenu(data).open(player);
    }
}
