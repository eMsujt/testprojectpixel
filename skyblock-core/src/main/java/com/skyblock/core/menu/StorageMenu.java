package com.skyblock.core.menu;

import com.skyblock.core.manager.StorageManager;
import com.skyblock.core.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

/**
 * 6-row chest GUI titled 'Personal Vault' that exposes the player's 27 stored
 * items (page 0, slots 0-26 of {@link StorageManager}) and fills the remaining
 * rows with a decorative border.
 */
public final class StorageMenu extends AbstractSkyBlockMenu {

    private static final int VAULT_SLOTS = 27;
    private static final ItemStack PANE = new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE)
            .displayName("§r").build();

    public StorageMenu(Player player) {
        super(player, "Personal Vault", 6);
    }

    @Override
    protected void populate() {
        UUID playerId = player.getUniqueId();
        ItemStack[] contents = StorageManager.getInstance().getPage(playerId, 0);

        for (int slot = 0; slot < VAULT_SLOTS; slot++) {
            ItemStack item = (slot < contents.length) ? contents[slot] : null;
            if (item != null && item.getType() != Material.AIR) {
                setItem(slot, item);
            }
        }

        for (int slot = VAULT_SLOTS; slot < 54; slot++) {
            setItem(slot, PANE);
        }
    }

    @Override
    public void handleClick(InventoryClickEvent event) {
        if (event.getRawSlot() >= VAULT_SLOTS) {
            event.setCancelled(true);
        }
    }
}
