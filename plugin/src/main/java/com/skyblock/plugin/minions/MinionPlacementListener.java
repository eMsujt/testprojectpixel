package com.skyblock.plugin.minions;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.UUID;

/**
 * Listener that places a minion when a player right-clicks while holding a
 * minion item.
 *
 * <p>The held item is recognised as a minion by its display name, which ends
 * in "Minion" (e.g. "Wheat Minion"). On a right-click against a block the
 * minion is placed on top of that block; on a right-click in the air it is
 * placed at the player's location. The minion is registered with
 * {@link MinionManager} at tier 1 and one item is consumed from the hand.</p>
 */
public final class MinionPlacementListener implements Listener {

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Action action = event.getAction();
        if (action != Action.RIGHT_CLICK_BLOCK && action != Action.RIGHT_CLICK_AIR) return;

        ItemStack item = event.getItem();
        if (item == null) return;
        ItemMeta meta = item.getItemMeta();
        if (meta == null || !meta.hasDisplayName()) return;

        String type = matchType(meta.getDisplayName());
        if (type == null) return;

        event.setCancelled(true);

        Player player = event.getPlayer();
        UUID owner = player.getUniqueId();

        Location loc;
        Block block = event.getClickedBlock();
        if (action == Action.RIGHT_CLICK_BLOCK && block != null) {
            loc = block.getRelative(0, 1, 0).getLocation();
        } else {
            loc = player.getLocation();
        }

        MinionManager.getInstance().addMinion(new MinionManager.MinionData(owner, loc, type, 1));
        item.setAmount(item.getAmount() - 1);
        player.sendMessage("Placed a " + type + ".");
    }

    /**
     * Returns the minion type for a held item's display name, or {@code null}
     * if the name does not denote a minion.
     */
    private static String matchType(String displayName) {
        return displayName.endsWith("Minion") ? displayName : null;
    }
}
