package com.skyblock.core.bazaar.gui;

import com.skyblock.core.manager.BazaarManager;
import com.skyblock.core.manager.BazaarManager.BazaarProduct;
import com.skyblock.core.menu.Menu;
import com.skyblock.core.util.SkyblockUtils.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Canonical Bazaar hub menu. A 54-slot (6-row) chest titled {@code §6Bazaar},
 * framed by a {@code GRAY_STAINED_GLASS_PANE} border, listing products from
 * {@link BazaarManager}. Category filter buttons in the bottom border row let
 * players narrow the view to one of the six Hypixel Bazaar categories.
 *
 * <p>All other BazaarMenu/BazaarGui/ShopMenu-as-Bazaar classes in the project
 * are deprecated stubs that delegate here.</p>
 */
public final class BazaarMenu extends Menu {

    private enum Category {
        FARMING("Farming Supplies",   Material.GOLDEN_HOE),
        MINING("Mining Supplies",     Material.STONE_PICKAXE),
        FORAGING("Foraging Supplies", Material.OAK_LOG),
        COMBAT("Combat Supplies",     Material.IRON_SWORD),
        FISHING("Fishing Supplies",   Material.FISHING_ROD),
        MISC("Miscellaneous",         Material.NETHER_STAR);

        final String displayName;
        final Material icon;

        Category(String displayName, Material icon) {
            this.displayName = displayName;
            this.icon = icon;
        }
    }

    /** Inner slots across rows 1–4, excluding columns 0 and 8. */
    private static final int[] INNER_SLOTS = {
        10, 11, 12, 13, 14, 15, 16,
        19, 20, 21, 22, 23, 24, 25,
        28, 29, 30, 31, 32, 33, 34,
        37, 38, 39, 40, 41, 42, 43
    };

    /** Slots used for category filter buttons in the bottom border row. */
    private static final int[] CATEGORY_SLOTS = {46, 47, 48, 49, 50, 51};

    private final Player player;
    /** {@code null} means show all categories. */
    private final Category selectedCategory;

    public BazaarMenu(Player player) {
        this(player, null);
    }

    private BazaarMenu(Player player, Category selectedCategory) {
        super("§6Bazaar", 6);
        this.player = player;
        this.selectedCategory = selectedCategory;
    }

    @Override
    protected void build() {
        fillBorder();
        buildProducts();
        buildCategoryButtons();
    }

    private void buildProducts() {
        List<BazaarProduct> visible = Arrays.stream(BazaarProduct.values())
                .filter(p -> selectedCategory == null
                        || p.getCategory().equalsIgnoreCase(selectedCategory.name()))
                .collect(Collectors.toList());

        if (visible.isEmpty()) {
            setItem(22, new ItemBuilder(Material.BARRIER)
                    .displayName("§cNo Products Available")
                    .lore("§7No products in this category.")
                    .build());
            return;
        }

        BazaarManager manager = BazaarManager.getInstance();
        for (int i = 0; i < visible.size() && i < INNER_SLOTS.length; i++) {
            BazaarProduct product = visible.get(i);
            Material icon = Material.matchMaterial(product.getItemId());
            if (icon == null) icon = Material.PAPER;

            double lowestAsk = manager.getLowestAsk(product.getItemId());
            double highestBid = manager.getHighestBid(product.getItemId());
            String askStr = lowestAsk == Double.MAX_VALUE ? "§7None" : "§6" + (long) lowestAsk + " coins";
            String bidStr = highestBid <= 0 ? "§7None" : "§6" + (long) highestBid + " coins";

            setItem(INNER_SLOTS[i], new ItemBuilder(icon)
                    .displayName("§a" + product.getDisplayName())
                    .lore(
                            "§7Category: §e" + product.getCategory(),
                            "§7Lowest Ask: " + askStr,
                            "§7Highest Offer: " + bidStr,
                            "",
                            "§eClick to view!")
                    .build());
        }
    }

    private void buildCategoryButtons() {
        Category[] categories = Category.values();
        for (int i = 0; i < categories.length && i < CATEGORY_SLOTS.length; i++) {
            Category cat = categories[i];
            boolean active = cat == selectedCategory;
            final Category next = active ? null : cat;
            setItem(CATEGORY_SLOTS[i],
                    new ItemBuilder(cat.icon)
                            .displayName((active ? "§a" : "§e") + cat.displayName)
                            .lore(active
                                    ? "§7Click to show §aAll Products"
                                    : "§7Click to filter by " + cat.displayName.toLowerCase())
                            .build(),
                    event -> new BazaarMenu(player, next).open(player));
        }
    }

    private void fillBorder() {
        ItemStack pane = new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE).displayName("§r").build();
        for (int slot = 0; slot < 54; slot++) {
            int col = slot % 9;
            if (slot < 9 || slot >= 45 || col == 0 || col == 8) {
                setItem(slot, pane);
            }
        }
    }
}
