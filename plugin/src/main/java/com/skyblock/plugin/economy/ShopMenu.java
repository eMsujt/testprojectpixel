package com.skyblock.plugin.economy;

import com.skyblock.economy.CoinManager;
import com.skyblock.core.util.ItemBuilder;
import com.skyblock.plugin.gui.Menu;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Reusable 54-slot (6-row) menu for NPC shops.
 *
 * <p>A shop is described by a {@link List} of {@link ShopEntry}s, each pairing a
 * {@link Material} with its coin price. Entries are laid out left-to-right
 * starting at slot 0 (capped at the 54 available slots), each showing its price
 * as an appended lore line. Clicking an entry withdraws its price from the
 * clicking player's purse (via {@link CoinManager}) and grants the item; if they
 * can't afford it, the purchase is rejected with a message.</p>
 */
public class ShopMenu extends Menu {

    /**
     * A single purchasable shop entry.
     *
     * @param material the item shown in the menu (also what the player receives)
     * @param price    the coin cost to purchase
     */
    public record ShopEntry(Material material, int price) {
        public ShopEntry {
            Objects.requireNonNull(material, "material");
        }
    }

    private final CoinManager coinManager;
    private final List<ShopEntry> entries;

    /**
     * Creates a shop using the shared {@link CoinManager} instance.
     *
     * @param title   the inventory title (supports colour codes)
     * @param entries the entries to display
     */
    public ShopMenu(String title, List<ShopEntry> entries) {
        this(title, entries, CoinManager.getInstance());
    }

    /**
     * Creates a shop backed by the given {@link CoinManager}.
     *
     * @param title       the inventory title (supports colour codes)
     * @param entries     the entries to display
     * @param coinManager the coin source charged on purchase
     */
    public ShopMenu(String title, List<ShopEntry> entries, CoinManager coinManager) {
        super(title, 6);
        this.entries = entries != null ? new ArrayList<>(entries) : new ArrayList<>();
        this.coinManager = Objects.requireNonNull(coinManager, "coinManager");
    }

    @Override
    protected void build() {
        for (int i = 0; i < entries.size() && i < 54; i++) {
            ShopEntry entry = entries.get(i);
            ItemStack display = new ItemBuilder(entry.material())
                    .addLore("§7Price: §6" + entry.price() + " coins")
                    .addLore("§eClick to buy!")
                    .build();
            setItem(i, display, event -> purchase((Player) event.getWhoClicked(), entry));
        }
    }

    private void purchase(Player player, ShopEntry entry) {
        if (coinManager.withdraw(player.getUniqueId(), entry.price())) {
            player.getInventory().addItem(new ItemStack(entry.material()));
            player.sendMessage("§aPurchased §6" + entry.material() + " §afor §6" + entry.price() + " coins§a!");
        } else {
            player.sendMessage("§cYou don't have enough coins!");
        }
    }
}
