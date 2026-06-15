package com.skyblock.plugin.minion;

import com.skyblock.core.manager.MinionManager;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

/**
 * Listener that opens a {@link com.skyblock.core.menu.MinionMenu} when a player
 * right-clicks the block occupied by one of their placed minions.
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

        com.skyblock.plugin.minion.MinionManager.MinionData data =
                com.skyblock.plugin.minion.MinionManager.getInstance().getMinion(block.getLocation());
        if (data == null) {
            return;
        }

        event.setCancelled(true);

        Player player = event.getPlayer();
        Minion m = data.getMinion();
        MinionManager.MinionType coreType = MinionManager.MinionType.valueOf(m.type.name());
        MinionManager.MinionTier coreTier = MinionManager.MinionTier.valueOf(m.getTier().name());
        MinionManager.MinionData coreData = new MinionManager.MinionData(m.id, m.owner, coreType, coreTier);
        new com.skyblock.core.menu.MinionMenu(coreData).open(player);
    }
}
