package com.skyblock.core.listener;

import com.skyblock.core.item.SkyblockItems;
import com.skyblock.core.manager.CollectionManager;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.inventory.ItemStack;

/**
 * Bukkit listener that routes gathered items to {@link CollectionManager}.
 *
 * <p>Covers block-break (mining, farming, foraging), fishing, and mob kills.</p>
 */
public final class CollectionListener implements Listener {

    private final CollectionManager collectionManager;

    public CollectionListener(CollectionManager collectionManager) {
        this.collectionManager = collectionManager;
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Material material = event.getBlock().getType();
        collectionManager.addCollection(player.getUniqueId(), material, 1);
    }

    @EventHandler
    public void onFish(PlayerFishEvent event) {
        if (event.getState() != PlayerFishEvent.State.CAUGHT_FISH) {
            return;
        }
        if (!(event.getCaught() instanceof Item item)) {
            return;
        }
        ItemStack stack = item.getItemStack();
        collectionManager.addItems(
                event.getPlayer().getUniqueId(),
                collectionKey(stack),
                stack.getAmount());
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        Player killer = event.getEntity().getKiller();
        if (killer == null || event.getEntity() instanceof Player) {
            return;
        }
        // Credit the collection of what the mob actually dropped (vanilla by material, custom by
        // its stamped id), not the mob's type — which never matched a collection.
        for (ItemStack drop : event.getDrops()) {
            if (drop == null || drop.getType() == Material.AIR) {
                continue;
            }
            collectionManager.addItems(killer.getUniqueId(), collectionKey(drop), drop.getAmount());
        }
    }

    /** Collection key for an item: its stamped SkyBlock id if present, else its material name. */
    private static String collectionKey(ItemStack stack) {
        String id = SkyblockItems.idOf(stack);
        return id != null ? id : stack.getType().name();
    }
}
