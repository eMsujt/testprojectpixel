package com.skyblock.plugin.minion;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.UUID;

/**
 * Listener that places a {@link Minion} when a player places a block whose held
 * item is a minion.
 *
 * <p>The held item is recognised as a minion by its display name, which ends in
 * "Minion" (e.g. "§aWheat Minion"); the leading colour codes and the trailing
 * " Minion" are stripped to resolve the {@link Minion.MinionType}. A matched
 * minion is registered with {@link MinionManager} at {@link
 * Minion.MinionTier#TIER_1} and the block placement is cancelled so a plain
 * block is not left in the world.</p>
 */
public final class MinionPlacementListener implements Listener {

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        ItemStack item = event.getItemInHand();
        ItemMeta meta = item.getItemMeta();
        if (meta == null || !meta.hasDisplayName()) {
            return;
        }

        Minion.MinionType type = matchType(meta.getDisplayName());
        if (type == null) {
            return;
        }

        event.setCancelled(true);

        Player player = event.getPlayer();
        Minion minion = new Minion(UUID.randomUUID(), player.getUniqueId(), type, Minion.MinionTier.TIER_1);
        MinionManager.getInstance().addMinion(player.getUniqueId(), minion);
    }

    /**
     * Returns the {@link Minion.MinionType} whose display name matches the held
     * item's display name (ignoring colour codes), or {@code null} if the name
     * does not denote a minion.
     */
    private static Minion.MinionType matchType(String displayName) {
        String stripped = displayName.replaceAll("§.", "");
        if (!stripped.endsWith("Minion")) {
            return null;
        }
        for (Minion.MinionType type : Minion.MinionType.values()) {
            if (type.getDisplayName().equals(stripped)) {
                return type;
            }
        }
        return null;
    }
}
