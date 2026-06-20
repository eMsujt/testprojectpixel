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

    private static final String[]   CATEGORIES     = {"ALL", "FARMING", "MINING", "COMBAT", "FORAGING", "FISHING", "MISC"};
    private static final String[]   CATEGORY_LABELS = {"§fAll Products", "§aFarming", "§9Mining", "§cCombat", "§6Woodcutting", "§bFishing", "§7Misc"};
    private static final Material[] TAB_MATERIALS   = {
            Material.NETHER_STAR,
            Material.WHEAT,
            Material.IRON_ORE,
            Material.IRON_SWORD,
            Material.OAK_LOG,
            Material.COD,
            Material.SLIME_BALL
    };

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
        ItemStack pane = new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE).displayName("§r").build();
        for (int slot = CATEGORIES.length; slot < 9; slot++) setItem(slot, pane);
        for (int slot = 45; slot < 54; slot++) setItem(slot, pane);

        for (int i = 0; i < CATEGORIES.length; i++) {
            boolean selected = i == selectedTab;
            ItemBuilder b = new ItemBuilder(TAB_MATERIALS[i])
                    .displayName(CATEGORY_LABELS[i])
                    .lore(selected ? "§aSelected" : "§7Click to filter");
            if (selected) b.enchant(Enchantment.UNBREAKING, 1).flags(ItemFlag.HIDE_ENCHANTS);
            setItem(i, b.build());
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
            double[] ref = BazaarManager.PRODUCT_DATA.get(product.getItemId());
            double buyPrice  = ref != null ? ref[0] : manager.getLowestAsk(product);
            double sellPrice = ref != null ? ref[1] : manager.getHighestBid(product);

            setItem(ORDER_SLOTS[i], new ItemBuilder(resolveMaterial(product))
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
        if (slot >= 0 && slot < CATEGORIES.length && slot != selectedTab) {
            selectedTab = slot;
            if (event.getWhoClicked() instanceof Player clicker) {
                open(clicker);
            }
        }
    }

    private List<BazaarProduct> getFilteredProducts() {
        List<BazaarProduct> result = new ArrayList<>();
        String cat = CATEGORIES[selectedTab];
        for (BazaarProduct p : BazaarProduct.values()) {
            if ("ALL".equals(cat) || cat.equals(p.getCategory())) {
                result.add(p);
            }
        }
        return result;
    }

    private static Material resolveMaterial(BazaarProduct product) {
        try {
            return Material.valueOf(product.getItemId());
        } catch (IllegalArgumentException e) {
            return Material.PAPER;
        }
    }

    private static String formatCoins(double coins, boolean isBuy) {
        if (isBuy  && coins >= Double.MAX_VALUE / 2) return "§8N/A";
        if (!isBuy && coins <= 0)                    return "§8N/A";
        if (coins >= 1_000_000) return String.format("%.1fM", coins / 1_000_000);
        if (coins >= 1_000)     return String.format("%.1fk", coins / 1_000);
        return String.format("%.1f", coins);
    }
}
