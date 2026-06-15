package com.skyblock.plugin.shop;

import com.skyblock.economy.CoinManager;
import com.skyblock.core.util.ItemBuilder;
import com.skyblock.plugin.gui.Menu;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * @deprecated Use {@link com.skyblock.core.menu.ShopMenu} instead.
 *
 * Reusable 54-slot (6-row) menu base for NPC shops.
 *
 * <p>A shop is described by a {@link List} of {@link ShopEntry}s, each pairing an
 * item with its coin cost. Entries are laid out left-to-right starting at slot 0
 * (capped at the 54 available slots). Clicking an entry withdraws its price from
 * the viewing player's purse (via {@link CoinManager}) and adds the item to their
 * inventory; if they can't afford it, the purchase is rejected with a message.</p>
 */
@Deprecated
public class ShopMenu extends Menu {

    private final UUID playerId;
    private final CoinManager coinManager;
    private final List<ShopEntry> entries;

    public ShopMenu(String title, UUID playerId, CoinManager coinManager, List<ShopEntry> entries) {
        super(title, 6);
        this.playerId = Objects.requireNonNull(playerId, "playerId");
        this.coinManager = Objects.requireNonNull(coinManager, "coinManager");
        this.entries = Objects.requireNonNull(entries, "entries");
    }

    @Override
    protected void build() {
        int slots = 6 * 9;
        for (int i = 0; i < entries.size() && i < slots; i++) {
            ShopEntry entry = entries.get(i);
            ItemStack icon = new ItemBuilder(entry.item())
                    .addLore("§7Cost: §6" + entry.price() + " coins")
                    .addLore("§eClick to buy!")
                    .build();
            setItem(i, icon, event -> purchase(entry));
        }
    }

    private void purchase(ShopEntry entry) {
        Player player = Bukkit.getPlayer(playerId);
        if (player == null) {
            return;
        }
        if (coinManager.withdraw(playerId, entry.price())) {
            player.getInventory().addItem(entry.item().clone());
            player.sendMessage("§aPurchased §6" + entry.item().getType() + " §afor §6" + entry.price() + " coins§a!");
        } else {
            player.sendMessage("§cYou don't have enough coins!");
        }
    }

    /**
     * A single purchasable item in a shop: the item handed to the buyer and the
     * coin price charged for it.
     */
    public record ShopEntry(ItemStack item, long price) {
        public ShopEntry {
            Objects.requireNonNull(item, "item");
        }
    }
}
