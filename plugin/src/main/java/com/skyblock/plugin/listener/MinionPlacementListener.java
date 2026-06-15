package com.skyblock.plugin.listener;

import com.skyblock.core.manager.MinionManager;
import com.skyblock.core.manager.MinionManager.MinionTier;
import com.skyblock.core.manager.MinionManager.MinionType;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/**
 * Listener that places a minion when a player right-clicks a block while
 * holding a minion item.
 *
 * <p>The held item is recognised as a minion by its display name, which ends in
 * "Minion" (e.g. "§aWheat Minion"); the leading colour codes are stripped to
 * resolve the {@link MinionType}. A matched minion is registered with
 * {@link MinionManager} at {@link MinionTier#TIER_1} on the block above the
 * clicked face, and the interaction is cancelled so no default block-use
 * behaviour occurs.</p>
 */
public final class MinionPlacementListener implements Listener {

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        ItemStack item = event.getItem();
        if (item == null) {
            return;
        }
        ItemMeta meta = item.getItemMeta();
        if (meta == null || !meta.hasDisplayName()) {
            return;
        }

        MinionType type = matchType(meta.getDisplayName());
        if (type == null) {
            return;
        }

        Block clicked = event.getClickedBlock();
        if (clicked == null) {
            return;
        }

        event.setCancelled(true);

        Player player = event.getPlayer();
        Location loc = clicked.getRelative(event.getBlockFace()).getLocation();
        String locationKey = loc.getWorld().getName()
                + "," + loc.getBlockX()
                + "," + loc.getBlockY()
                + "," + loc.getBlockZ();

        MinionManager manager = MinionManager.getInstance();
        MinionManager.MinionData data = manager.placeMinion(player.getUniqueId(), type, MinionTier.TIER_1);
        manager.setPlacement(player.getUniqueId(), locationKey, type);
        manager.setMinionLocation(data.id, locationKey);
    }

    /**
     * Returns the {@link MinionType} whose display name matches the held item's
     * display name (ignoring colour codes), or {@code null} if it does not denote
     * a known minion type.
     */
    private static MinionType matchType(String displayName) {
        String stripped = displayName.replaceAll("§.", "");
        if (!stripped.endsWith("Minion")) {
            return null;
        }
        for (MinionType type : MinionType.values()) {
            if (type.getDisplayName().equals(stripped)) {
                return type;
            }
        }
        return null;
    }
}
