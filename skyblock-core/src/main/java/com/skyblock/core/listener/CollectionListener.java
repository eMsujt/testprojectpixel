package com.skyblock.core.listener;

import com.skyblock.core.manager.CollectionManager;
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
        String collection = event.getBlock().getType().name();
        collectionManager.addItems(player.getUniqueId(), collection, 1);
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
                stack.getType().name(),
                stack.getAmount());
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        Player killer = event.getEntity().getKiller();
        if (killer == null) {
            return;
        }
        String collection = event.getEntity().getType().name();
        collectionManager.addItems(killer.getUniqueId(), collection, 1);
    }
}
