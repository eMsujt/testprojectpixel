package com.skyblock.core.menu;

import com.skyblock.core.backpack.BackpackManager.BackpackTier;
import com.skyblock.core.manager.StorageManager;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.UUID;

/**
 * Backpack GUI opened by {@code /storage}. The inventory is sized to the
 * player's {@link BackpackTier} (SMALL=9, MEDIUM=18, LARGE=27, JUMBO=36 slots)
 * and rendered with the stored items from page 0 of {@link StorageManager}.
 */
public final class StorageMenu extends AbstractMenu {

    private final BackpackTier tier;

    public StorageMenu(JavaPlugin plugin, Player player, BackpackTier tier) {
        super(plugin, player, "§8Backpack (" + tier.name() + ")", tier.slots);
        this.tier = tier;
    }

    @Override
    protected void populate() {
        UUID playerId = player.getUniqueId();
        ItemStack[] contents = StorageManager.getInstance().getPage(playerId, 0);
        for (int slot = 0; slot < tier.slots && slot < contents.length; slot++) {
            ItemStack item = contents[slot];
            if (item != null && item.getType() != Material.AIR) {
                setItem(slot, item);
            }
        }
    }

    @Override
    public void handleClick(InventoryClickEvent event) {
        event.setCancelled(true);
    }
}
