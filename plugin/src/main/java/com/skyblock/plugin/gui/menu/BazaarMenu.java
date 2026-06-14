package com.skyblock.plugin.gui.menu;

import com.skyblock.plugin.bazaar.BazaarManager;
import com.skyblock.plugin.bazaar.BazaarManager.Product;
import com.skyblock.plugin.gui.ItemBuilder;
import com.skyblock.plugin.gui.Menu;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

/**
 * The Bazaar menu.
 *
 * <p>A 54-slot (6-row) menu titled {@code §6Bazaar}, framed by a gray glass
 * border, that lists every product loaded from {@code bazaar.yml} via
 * {@link BazaarManager}. Each product occupies one of the 28 inner slots and
 * is shown using the product's configured {@link Material} icon.</p>
 */
public class BazaarMenu extends Menu {

    /** Inner slots across the four centre rows, left-to-right, top-to-bottom. */
    private static final int[] INNER_SLOTS = {
            10, 11, 12, 13, 14, 15, 16,
            19, 20, 21, 22, 23, 24, 25,
            28, 29, 30, 31, 32, 33, 34,
            37, 38, 39, 40, 41, 42, 43
    };

    private final Player player;

    public BazaarMenu(Player player) {
        super("§6Bazaar", 6);
        this.player = player;
    }

    @Override
    protected void build() {
        fillBorder();

        List<Product> products = BazaarManager.getInstance().getProducts();

        for (int i = 0; i < products.size() && i < INNER_SLOTS.length; i++) {
            Product product = products.get(i);
            setItem(INNER_SLOTS[i], new ItemBuilder(product.material())
                    .displayName(product.displayName())
                    .lore(
                            "§7Category: §e" + product.category().displayName(),
                            "§7Buy: §6" + product.buyPrice() + " coins",
                            "§7Sell: §6" + product.sellPrice() + " coins",
                            "",
                            "§eClick to view!")
                    .build());
        }

        if (products.isEmpty()) {
            setItem(22, new ItemBuilder(Material.BARRIER)
                    .displayName("§cNo Products Available")
                    .lore("§7No bazaar products have been loaded.")
                    .build());
        }
    }

    /** Fills the menu's outer edge with gray glass panes, matching Hypixel. */
    private void fillBorder() {
        ItemStack pane = new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE)
                .displayName("§r")
                .build();
        for (int slot = 0; slot < 54; slot++) {
            int column = slot % 9;
            if (slot < 9 || slot >= 45 || column == 0 || column == 8) {
                setItem(slot, pane);
            }
        }
    }
}
