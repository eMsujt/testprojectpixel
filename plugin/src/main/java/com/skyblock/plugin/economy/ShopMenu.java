package com.skyblock.plugin.economy;

import com.skyblock.plugin.gui.ItemBuilder;
import com.skyblock.plugin.gui.Menu;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

/**
 * Abstract base for coin-shop menus.
 *
 * <p>A shop presents a fixed list of {@link ShopItem}s, each laid out left-to-right
 * starting at the top-left slot. Every entry shows its purchase price as an appended
 * lore line. Clicking an entry dispatches to {@link #onPurchase(Player, ShopItem)},
 * which subclasses implement to debit coins and grant the item.</p>
 */
public abstract class ShopMenu extends Menu {

    /**
     * A single purchasable shop entry.
     *
     * @param icon  the item shown in the menu (also what the player receives)
     * @param price the coin cost to purchase
     */
    public record ShopItem(ItemStack icon, int price) {
    }

    private final List<ShopItem> shopItems;

    /**
     * Creates a shop with the given title and entries. The row count is sized to
     * hold every entry (one row per nine items, capped at the six-row maximum).
     *
     * @param title the inventory title (supports colour codes)
     * @param items the entries to display
     */
    protected ShopMenu(String title, List<ShopItem> items) {
        super(title, rowsFor(items));
        this.shopItems = items != null ? new ArrayList<>(items) : new ArrayList<>();
    }

    private static int rowsFor(List<ShopItem> items) {
        int count = items != null ? items.size() : 0;
        int rows = Math.max(1, (count + 8) / 9);
        return Math.min(rows, 6);
    }

    /**
     * Called when a player clicks a shop entry.
     *
     * @param player the purchasing player
     * @param item   the clicked entry
     */
    protected abstract void onPurchase(Player player, ShopItem item);

    @Override
    protected void build() {
        for (int i = 0; i < shopItems.size() && i < 54; i++) {
            ShopItem shopItem = shopItems.get(i);
            ItemStack display = new ItemBuilder(shopItem.icon())
                    .addLore("§7Price: §6" + shopItem.price() + " coins")
                    .build();
            setItem(i, display, event -> onPurchase((Player) event.getWhoClicked(), shopItem));
        }
    }
}
