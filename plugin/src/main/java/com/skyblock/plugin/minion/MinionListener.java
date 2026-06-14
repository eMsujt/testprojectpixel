package com.skyblock.plugin.minion;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

/**
 * Listener that opens a {@link MinionMenu} when a player right-clicks the block
 * occupied by one of their placed minions.
 *
 * <p>Placed minions are tracked by {@link MinionManager} keyed by the block
 * location they occupy, so the clicked block is resolved straight from that map.
 * The interaction is cancelled so no block-use side effect occurs.</p>
 */
public final class MinionListener implements Listener {

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        Block block = event.getClickedBlock();
        if (block == null) {
            return;
        }

        MinionManager.MinionData data = MinionManager.getInstance().getMinion(block.getLocation());
        if (data == null) {
            return;
        }

        event.setCancelled(true);

        Player player = event.getPlayer();
        new MinionMenu(data.getMinion()).open(player);
    }
}
