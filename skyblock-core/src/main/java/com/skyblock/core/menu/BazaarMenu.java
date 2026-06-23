package com.skyblock.core.menu;

import com.skyblock.core.manager.BazaarManager;
import com.skyblock.core.manager.BazaarManager.BazaarProduct;
import com.skyblock.core.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public final class BazaarMenu extends AbstractSkyBlockMenu {

    private static final String[]   CATEGORIES      = {"FARMING", "MINING", "COMBAT", "FORAGING", "FISHING", "MISC"};
    private static final String[]   CATEGORY_LABELS = {"§aFarming", "§9Mining", "§cCombat", "§6Foraging", "§bFishing", "§7Oddities"};
    private static final Material[] TAB_MATERIALS    = {
            Material.WHEAT,
            Material.STONE_PICKAXE,
            Material.IRON_SWORD,
            Material.OAK_LOG,
            Material.FISHING_ROD,
            Material.ENCHANTING_TABLE
    };

    /** Category tabs run down the left column (Hypixel layout). */
    private static final int[] CATEGORY_SLOTS = {0, 9, 18, 27, 36, 45};

    static final int[] ORDER_SLOTS = {
            10, 11, 12, 13, 14, 15, 16,
            19, 20, 21, 22, 23, 24, 25,
            28, 29, 30, 31, 32, 33, 34,
            37, 38, 39, 40, 41, 42, 43
    };

    private int selectedTab;

    public BazaarMenu(Player player) {
        super(player, "§6Bazaar", 6);
        this.selectedTab = 0;
    }

    @Override
    protected void populate() {
        for (int i = 0; i < CATEGORIES.length; i++) {
            boolean selected = i == selectedTab;
            ItemBuilder b = new ItemBuilder(TAB_MATERIALS[i])
                    .displayName(CATEGORY_LABELS[i])
                    .lore(selected ? "§aSelected" : "§7Click to filter");
            if (selected) b.enchant(Enchantment.UNBREAKING, 1).flags(ItemFlag.HIDE_ENCHANTS);
            setItem(CATEGORY_SLOTS[i], b.build());
        }

        List<BazaarProduct> products = getFilteredProducts();
        BazaarManager manager = BazaarManager.getInstance();

        if (products.isEmpty()) {
            setItem(31, new ItemBuilder(Material.BARRIER)
                    .displayName("§cNo Products")
                    .lore("§7No items in this category.")
                    .build());
            return;
        }

        for (int i = 0; i < ORDER_SLOTS.length && i < products.size(); i++) {
            BazaarProduct product = products.get(i);
            double buyPrice  = manager.getDisplayBuyPrice(product);
            double sellPrice = manager.getDisplaySellPrice(product);

            setItem(ORDER_SLOTS[i], ItemBuilder.forItem(product.getItemId())
                    .displayName("§f" + product.getDisplayName())
                    .lore(
                        "§7Buy:  §6" + formatCoins(buyPrice, true),
                        "§7Sell: §6" + formatCoins(sellPrice, false),
                        "",
                        "§eClick to trade!")
                    .build());
        }
    }

    @Override
    public void handleClick(InventoryClickEvent event) {
        event.setCancelled(true);
        int slot = event.getRawSlot();
        for (int i = 0; i < CATEGORY_SLOTS.length; i++) {
            if (CATEGORY_SLOTS[i] == slot) {
                if (i != selectedTab && event.getWhoClicked() instanceof Player clicker) {
                    selectedTab = i;
                    open(clicker);
                }
                return;
            }
        }
    }

    private List<BazaarProduct> getFilteredProducts() {
        List<BazaarProduct> result = new ArrayList<>();
        String cat = CATEGORIES[selectedTab];
        for (BazaarProduct p : BazaarProduct.values()) {
            if (cat.equals(p.getCategory().name())) {
                result.add(p);
            }
        }
        return result;
    }

    private static String formatCoins(double coins, boolean isBuy) {
        if (isBuy  && coins >= Double.MAX_VALUE / 2) return "§8N/A";
        if (!isBuy && coins <= 0)                    return "§8N/A";
        if (coins >= 1_000_000) return String.format("%.1fM", coins / 1_000_000);
        if (coins >= 1_000)     return String.format("%.1fk", coins / 1_000);
        return String.format("%.1f", coins);
    }
}
