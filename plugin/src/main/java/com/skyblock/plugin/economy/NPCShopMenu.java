package com.skyblock.plugin.economy;

import com.skyblock.economy.CoinManager;
import com.skyblock.plugin.economy.ShopManager.Shop;
import com.skyblock.plugin.economy.ShopMenu.ShopEntry;
import com.skyblock.plugin.gui.ItemBuilder;
import com.skyblock.plugin.gui.Menu;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Objects;

/**
 * Configurable 54-slot (6-row) {@code §6Shop} chest GUI for a single NPC
 * {@link Shop}.
 *
 * <p>The shop's {@link ShopEntry}s are laid out left-to-right starting at slot 0
 * (capped at the 54 available slots), each showing its price as an appended lore
 * line. Clicking an entry withdraws its price from the clicking player's purse
 * (via {@link CoinManager}) and grants the item; if they can't afford it, the
 * purchase is rejected with a message.</p>
 */
public class NPCShopMenu extends Menu {

    private final Shop shop;
    private final CoinManager coinManager;

    /**
     * Creates a shop menu using the shared {@link CoinManager} instance.
     *
     * @param shop the shop to display
     */
    public NPCShopMenu(Shop shop) {
        this(shop, CoinManager.getInstance());
    }

    /**
     * Creates a shop menu backed by the given {@link CoinManager}.
     *
     * @param shop        the shop to display
     * @param coinManager the coin source charged on purchase
     */
    public NPCShopMenu(Shop shop, CoinManager coinManager) {
        super("§6Shop", 6);
        this.shop = Objects.requireNonNull(shop, "shop");
        this.coinManager = Objects.requireNonNull(coinManager, "coinManager");
    }

    @Override
    protected void build() {
        for (int i = 0; i < shop.entries().size() && i < 54; i++) {
            ShopEntry entry = shop.entries().get(i);
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
