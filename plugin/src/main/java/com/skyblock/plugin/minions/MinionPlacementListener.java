package com.skyblock.plugin.minions;

import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.UUID;

/**
 * @deprecated Use {@link com.skyblock.plugin.minion.MinionPlacementListener} instead.
 */
@Deprecated
public final class MinionPlacementListener implements Listener {

    /** Maximum number of minions a single player may have placed at once. */
    private static final int MAX_MINIONS = 5;

    private static final NamespacedKey MINION_TYPE_KEY =
            new NamespacedKey("skyblock", "minion_type");

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;

        ItemStack item = event.getItem();
        if (item == null) return;
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return;

        String type = matchType(meta.getPersistentDataContainer());
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
     * Returns the minion type stored in the item's PDC under
     * {@code skyblock:minion_type}, or {@code null} if absent.
     */
    private static String matchType(PersistentDataContainer pdc) {
        return pdc.get(MINION_TYPE_KEY, PersistentDataType.STRING);
    }
}
