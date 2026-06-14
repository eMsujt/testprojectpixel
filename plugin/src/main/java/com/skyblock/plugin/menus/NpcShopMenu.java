package com.skyblock.plugin.menus;

import com.skyblock.economy.CoinManager;
import com.skyblock.plugin.gui.ItemBuilder;
import com.skyblock.plugin.gui.Menu;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Reusable 54-slot (6-row) chest GUI for NPC shops.
 *
 * <p>A shop is described by a {@link List} of {@link NpcShopItem}s, each pairing
 * an {@link ItemStack} with its coin price. Items are laid out left-to-right
 * starting at slot 0 (capped at the 54 available slots), each showing its price
 * as an appended lore line. Clicking an item withdraws its price from the
 * clicking player's purse (via {@link CoinManager}) and grants the item; if they
 * can't afford it, the purchase is rejected with a message.</p>
 */
public class NpcShopMenu extends Menu {

    /**
     * A single purchasable shop item.
     *
     * @param item  the item shown in the menu (also what the player receives)
     * @param price the coin cost to purchase
     */
    public record NpcShopItem(ItemStack item, long price) {
        public NpcShopItem {
            Objects.requireNonNull(item, "item");
        }
    }

    private final CoinManager coinManager;
    private final List<NpcShopItem> items;

    /**
     * Creates a shop using the shared {@link CoinManager} instance.
     *
     * @param title the inventory title (supports colour codes)
     * @param items the items to display
     */
    public NpcShopMenu(String title, List<NpcShopItem> items) {
        this(title, items, CoinManager.getInstance());
    }

    /**
     * Creates a shop backed by the given {@link CoinManager}.
     *
     * @param title       the inventory title (supports colour codes)
     * @param items       the items to display
     * @param coinManager the coin source charged on purchase
     */
    public NpcShopMenu(String title, List<NpcShopItem> items, CoinManager coinManager) {
        super(title, 6);
        this.items = items != null ? new ArrayList<>(items) : new ArrayList<>();
        this.coinManager = Objects.requireNonNull(coinManager, "coinManager");
    }

    @Override
    protected void build() {
        for (int i = 0; i < items.size() && i < 54; i++) {
            NpcShopItem entry = items.get(i);
            ItemStack display = new ItemBuilder(entry.item())
                    .addLore("§7Price: §6" + entry.price() + " coins")
                    .addLore("§eClick to buy!")
                    .build();
            setItem(i, display, event -> purchase((Player) event.getWhoClicked(), entry));
        }
    }

    private void purchase(Player player, NpcShopItem entry) {
        if (coinManager.withdraw(player.getUniqueId(), entry.price())) {
            player.getInventory().addItem(entry.item().clone());
            player.sendMessage("§aPurchased §6" + entry.item().getType() + " §afor §6" + entry.price() + " coins§a!");
        } else {
            player.sendMessage("§cYou don't have enough coins!");
        }
    }
}
