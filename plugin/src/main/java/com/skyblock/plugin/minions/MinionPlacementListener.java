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
 * in "Minion" (e.g. "Wheat Minion"). The minion is placed on top of the
 * right-clicked block; right-clicks in the air are ignored so accidental
 * swings do not consume the item. A player may have at most
 * {@link #MAX_MINIONS} minions placed at once (per the Hypixel wiki). The
 * minion is registered with {@link MinionManager} at tier 1 and one item is
 * consumed from the hand.</p>
 */
public final class MinionPlacementListener implements Listener {

    /** Maximum number of minions a single player may have placed at once. */
    private static final int MAX_MINIONS = 5;

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;

        ItemStack item = event.getItem();
        if (item == null) return;
        ItemMeta meta = item.getItemMeta();
        if (meta == null || !meta.hasDisplayName()) return;

        String type = matchType(meta.getDisplayName());
        if (type == null) return;

        Block block = event.getClickedBlock();
        if (block == null) return;

        event.setCancelled(true);

        Player player = event.getPlayer();
        UUID owner = player.getUniqueId();

        if (MinionManager.getInstance().getMinions(owner).size() >= MAX_MINIONS) {
            player.sendMessage("You have reached the minion limit of " + MAX_MINIONS + ".");
            return;
        }

        Location loc = block.getRelative(0, 1, 0).getLocation();

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
