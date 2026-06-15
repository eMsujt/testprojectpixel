package com.skyblock.plugin.gui.menu;

import com.skyblock.plugin.bazaar.BazaarManager;
import com.skyblock.plugin.bazaar.BazaarManager.Category;
import com.skyblock.plugin.bazaar.BazaarManager.Product;
import com.skyblock.plugin.gui.ItemBuilder;
import com.skyblock.plugin.gui.Menu;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.stream.Collectors;

/**
 * The Bazaar menu.
 *
 * <p>A 54-slot (6-row) menu titled {@code §6Bazaar}, framed by a gray glass
 * border, that lists every product loaded from {@code bazaar.yml} via
 * {@link BazaarManager}. Each product occupies one of the 28 inner slots and
 * is shown using the product's configured {@link Material} icon. Category
 * filter buttons sit in the bottom border row (slots 46–51) and re-open the menu
 * filtered to that category; clicking a highlighted category shows all
 * products again.</p>
 */
public class BazaarMenu extends Menu {

    /** Inner slots across the four centre rows, left-to-right, top-to-bottom. */
    private static final int[] INNER_SLOTS = {
             9, 10, 11, 12, 13, 14, 15, 16, 17,
            18, 19, 20, 21, 22, 23, 24, 25, 26,
            27, 28, 29, 30, 31, 32, 33, 34, 35,
            36, 37, 38, 39, 40, 41, 42, 43, 44
    };

    /** Slots used for category filter buttons (within the bottom border row). */
    private static final int[] CATEGORY_SLOTS = {46, 47, 48, 49, 50, 51};

    private final Player player;
    private final List<Product> allProducts;
    /** {@code null} means "show all categories". */
    private final Category selectedCategory;

    public BazaarMenu(Player player, List<Product> products) {
        this(player, products, null);
    }

    private BazaarMenu(Player player, List<Product> products, Category selectedCategory) {
        super("§6Bazaar", 6);
        this.player = player;
        this.allProducts = products;
        this.selectedCategory = selectedCategory;
    }

    @Override
    protected void build() {
        fillBorder();

        List<Product> visible = selectedCategory == null
                ? allProducts
                : allProducts.stream()
                        .filter(p -> p.category() == selectedCategory)
                        .collect(Collectors.toList());

        for (int i = 0; i < visible.size() && i < INNER_SLOTS.length; i++) {
            Product product = visible.get(i);
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

        if (visible.isEmpty()) {
            setItem(22, new ItemBuilder(Material.BARRIER)
                    .displayName("§cNo Products Available")
                    .lore("§7No bazaar products have been loaded.")
                    .build());
        }

        addCategoryButtons();
    }

    private void addCategoryButtons() {
        Category[] categories = Category.values();
        for (int i = 0; i < categories.length && i < CATEGORY_SLOTS.length; i++) {
            Category cat = categories[i];
            boolean active = cat == selectedCategory;
            ItemStack icon = new ItemBuilder(cat.icon())
                    .displayName((active ? "§a" : "§e") + cat.displayName().replaceFirst("^§.", ""))
                    .lore(active ? "§7Click to show §aAll Products" : "§7Click to filter by this category")
                    .build();
            final Category next = active ? null : cat;
            setItem(CATEGORY_SLOTS[i], icon,
                    event -> new BazaarMenu(player, allProducts, next).open(player));
        }
    }

    /** Fills rows 0 and 5 with gray glass panes. */
    private void fillBorder() {
        ItemStack pane = new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE)
                .displayName("§r")
                .build();
        for (int slot = 0; slot < 54; slot++) {
            int column = slot % 9;
            if (slot < 9 || slot >= 45) {
                setItem(slot, pane);
            }
        }
    }
}
