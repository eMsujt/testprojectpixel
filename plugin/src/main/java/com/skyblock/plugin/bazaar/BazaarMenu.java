package com.skyblock.plugin.bazaar;

import com.skyblock.plugin.bazaar.BazaarManager.Product;
import com.skyblock.plugin.gui.ItemBuilder;
import com.skyblock.plugin.gui.Menu;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

/**
 * The Bazaar menu.
 *
 * <p>A 54-slot (6-row) chest GUI titled {@code §6Bazaar} framed by a
 * {@code GRAY_STAINED_GLASS_PANE} border, listing the {@link BazaarManager}'s
 * loaded products across the inner slots in definition order. Each product shows
 * its instant buy and sell prices as lore.</p>
 */
public class BazaarMenu extends Menu {

    /** Inner slots (excluding the border) in left-to-right, top-to-bottom order. */
    private static final int[] CONTENT_SLOTS = buildContentSlots();

    private final List<Product> products;

    public BazaarMenu(List<Product> products) {
        super("§6Bazaar", 6);
        this.products = products != null ? new ArrayList<>(products) : new ArrayList<>();
    }

    @Override
    protected void build() {
        fillBorder();

        for (int i = 0; i < products.size() && i < CONTENT_SLOTS.length; i++) {
            Product product = products.get(i);
            setItem(CONTENT_SLOTS[i], new ItemBuilder(product.material())
                    .displayName(product.displayName())
                    .lore("§7Buy: §6" + product.buyPrice() + " coins")
                    .addLore("§7Sell: §6" + product.sellPrice() + " coins")
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

    /** Computes the inner content slots (every non-border slot of a 6-row menu). */
    private static int[] buildContentSlots() {
        List<Integer> slots = new ArrayList<>();
        for (int slot = 0; slot < 54; slot++) {
            int column = slot % 9;
            if (slot >= 9 && slot < 45 && column != 0 && column != 8) {
                slots.add(slot);
            }
        }
        int[] result = new int[slots.size()];
        for (int i = 0; i < result.length; i++) {
            result[i] = slots.get(i);
        }
        return result;
    }
}
