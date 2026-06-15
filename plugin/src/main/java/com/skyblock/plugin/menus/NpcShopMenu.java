package com.skyblock.plugin.menus;

import com.skyblock.economy.CoinManager;
import com.skyblock.core.util.ItemBuilder;
import com.skyblock.plugin.gui.Menu;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Objects;

/**
 * @deprecated Use {@link com.skyblock.core.menu.ShopMenu} instead.
 *
 * Abstract 54-slot (6-row) chest base for NPC shops.
 *
 * <p>As a {@link Menu}, an {@code NpcShopMenu} is its own
 * {@link org.bukkit.inventory.InventoryHolder} and tracks a slot-to-click-handler
 * map, so a purchase click dispatches back to the menu. Concrete shops lay out
 * their stock in {@link #build()} by calling {@link #addStockItem(int, ItemStack, long)}
 * for each priced slot; clicking a stock item withdraws its price from the
 * player's purse (via {@link CoinManager}) and grants the item, or rejects the
 * purchase with a message when they can't afford it.</p>
 */
@Deprecated
public abstract class NpcShopMenu extends Menu {

    private final CoinManager coinManager;

    /**
     * Creates a shop using the shared {@link CoinManager} instance.
     *
     * @param title the inventory title (supports colour codes)
     */
    protected NpcShopMenu(String title) {
        this(title, CoinManager.getInstance());
    }

    /**
     * Creates a shop backed by the given {@link CoinManager}.
     *
     * @param title       the inventory title (supports colour codes)
     * @param coinManager the coin source charged on purchase
     */
    protected NpcShopMenu(String title, CoinManager coinManager) {
        super(title, 6);
        this.coinManager = Objects.requireNonNull(coinManager, "coinManager");
    }

    /**
     * Places a purchasable item in {@code slot}: the display shows {@code price}
     * as appended lore, and clicking it withdraws {@code price} coins from the
     * clicking player's purse and grants a copy of {@code item}.
     *
     * @param slot  the slot index (0-based)
     * @param item  the item shown and granted on purchase
     * @param price the coin cost to purchase
     */
    protected void addStockItem(int slot, ItemStack item, long price) {
        Objects.requireNonNull(item, "item");
        ItemStack display = new ItemBuilder(item)
                .addLore("§7Price: §6" + price + " coins")
                .addLore("§eClick to buy!")
                .build();
        setItem(slot, display, event -> purchase((Player) event.getWhoClicked(), item, price));
    }

    private void purchase(Player player, ItemStack item, long price) {
        if (coinManager.withdraw(player.getUniqueId(), price)) {
            player.getInventory().addItem(item.clone());
            player.sendMessage("§aPurchased §6" + item.getType() + " §afor §6" + price + " coins§a!");
        } else {
            player.sendMessage("§cYou don't have enough coins!");
        }
    }
}
