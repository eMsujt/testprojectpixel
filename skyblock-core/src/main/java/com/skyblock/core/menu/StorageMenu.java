package com.skyblock.core.menu;

import com.skyblock.core.manager.BackpackManager.BackpackTier;
import com.skyblock.core.manager.StorageManager;
import com.skyblock.core.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

/**
 * 6-row chest GUI titled '§6Backpack §7(tier)' that exposes the item slots a
 * player's {@link BackpackTier} provides, drawing the stored {@link ItemStack}s
 * from page 0 of {@link StorageManager} and bordering the unused rows.
 */
public final class StorageMenu extends AbstractSkyBlockMenu {

    private static final ItemStack PANE = new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE)
            .displayName("§r").build();

    private final int slots;

    public StorageMenu(Player player) {
        super(player, "§8Storage", 6);
        this.slots = tierOf(player).getSlots();
    }

    private static BackpackTier tierOf(Player player) {
        return StorageManager.getInstance().getBackpackTier(player.getUniqueId());
    }

    @Override
    protected void populate() {
        UUID playerId = player.getUniqueId();
        ItemStack[] contents = StorageManager.getInstance().getPage(playerId, 0);

        for (int slot = 0; slot < slots; slot++) {
            ItemStack item = (slot < contents.length) ? contents[slot] : null;
            if (item != null && item.getType() != Material.AIR) {
                setItem(slot, item);
            }
        }

        for (int slot = slots; slot < 54; slot++) {
            setItem(slot, PANE);
        }
    }

    @Override
    public void handleClick(InventoryClickEvent event) {
        if (event.getRawSlot() >= slots) {
            event.setCancelled(true);
        }
    }
}
