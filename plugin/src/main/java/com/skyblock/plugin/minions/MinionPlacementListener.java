package com.skyblock.plugin.minions;

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
 * Listener that places a minion when a player right-clicks any solid block
 * while holding a minion item.
 *
 * <p>The held item is matched to a {@link MinionManager.MinionType} by its
 * display name (e.g. "Wheat Minion"). On a successful placement the minion
 * is registered with {@link MinionManager} on top of the clicked block and
 * one item is consumed from the player's hand.</p>
 */
public final class MinionPlacementListener implements Listener {

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;

        Block block = event.getClickedBlock();
        if (block == null || !block.getType().isSolid()) return;

        ItemStack item = event.getItem();
        if (item == null) return;
        ItemMeta meta = item.getItemMeta();
        if (meta == null || !meta.hasDisplayName()) return;

        MinionManager.MinionType minionType = matchType(meta.getDisplayName());
        if (minionType == null) return;

        event.setCancelled(true);

        Player player = event.getPlayer();
        UUID owner = player.getUniqueId();
        MinionManager manager = MinionManager.getInstance();

        if (manager.getMinions(owner).size() >= MinionManager.MAX_SLOTS) {
            player.sendMessage("You have reached the minion slot cap (" + MinionManager.MAX_SLOTS + ").");
            return;
        }

        Block placed = block.getRelative(0, 1, 0);
        String location = placed.getWorld().getName() + ","
                + placed.getX() + "," + placed.getY() + "," + placed.getZ();

        manager.placeMinion(owner, minionType, MinionManager.MinionTier.TIER_1);
        manager.setPlacement(owner, location, minionType);
        item.setAmount(item.getAmount() - 1);
        player.sendMessage("Placed a " + minionType.getDisplayName() + ".");
    }

    /** Returns the minion type whose display name matches, or {@code null}. */
    private static MinionManager.MinionType matchType(String displayName) {
        for (MinionManager.MinionType candidate : MinionManager.MinionType.values()) {
            if (candidate.getDisplayName().equalsIgnoreCase(displayName)) {
                return candidate;
            }
        }
        return null;
    }
}
