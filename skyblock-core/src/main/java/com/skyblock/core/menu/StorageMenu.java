package com.skyblock.core.menu;

import com.skyblock.core.manager.StorageManager;
import com.skyblock.core.util.SkyblockUtil.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

/**
 * GUI menu opened by {@code /storage}. Renders one ender-chest page from
 * {@link StorageManager} as a 6-row inventory: the top {@link StorageManager#PAGE_SIZE}
 * slots hold the page's stored items and the bottom row is a gray-pane border.
 */
public final class StorageMenu extends Menu {

    /** First inventory slot of the bottom border row. */
    static final int BORDER_ROW = StorageManager.PAGE_SIZE;

    private final UUID playerId;
    private final int page;

    public StorageMenu(Player player) {
        this(player.getUniqueId(), 0);
    }

    public StorageMenu(UUID playerId) {
        this(playerId, 0);
    }

    public StorageMenu(UUID playerId, int page) {
        super("§8Storage", 6);
        this.playerId = playerId;
        this.page = page;
    }

    @Override
    protected void build() {
        ItemStack[] contents = StorageManager.getInstance().getPage(playerId, page);
        for (int slot = 0; slot < StorageManager.PAGE_SIZE; slot++) {
            ItemStack item = contents[slot];
            if (item != null && item.getType() != Material.AIR) {
                setItem(slot, item);
            }
        }

        ItemStack pane = new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE).displayName("§r").build();
        for (int slot = BORDER_ROW; slot < 54; slot++) setItem(slot, pane);
    }

    @Override
    public void handleClick(InventoryClickEvent event) {
        event.setCancelled(true);
    }
}
