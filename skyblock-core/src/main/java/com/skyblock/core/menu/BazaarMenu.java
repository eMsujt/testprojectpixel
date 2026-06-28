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

    // The Bazaar's 5 wiki categories; "Woods & Fishes" merges the FORAGING + FISHING products.
    private static final String[][] TAB_CATEGORIES = {
            {"FARMING"}, {"MINING"}, {"COMBAT"}, {"FORAGING", "FISHING"}, {"MISC"}
    };
    private static final String[]   CATEGORY_LABELS = {"§aFarming", "§9Mining", "§cCombat", "§3Woods & Fishes", "§7Oddities"};
    /** Plain category names for the window title (Hypixel: "Bazaar ➜ Farming"). */
    private static final String[]   TITLE_LABELS    = {"Farming", "Mining", "Combat", "Woods & Fishes", "Oddities"};
    // Wiki tab icons: Farming=Golden Hoe, Mining=Diamond Pickaxe, Combat=Iron Sword,
    // Woods & Fishes=Fishing Rod, Oddities=Enchantment Table.
    private static final Material[] TAB_MATERIALS    = {
            Material.GOLDEN_HOE,
            Material.DIAMOND_PICKAXE,
            Material.IRON_SWORD,
            Material.FISHING_ROD,
            Material.ENCHANTING_TABLE
    };

    /** The 5 category tabs run down the left column (Hypixel layout). */
    private static final int[] CATEGORY_SLOTS = {0, 9, 18, 27, 36};

    // Products fill cols 3-8 (rows 2-5); col 2 (10/19/28/37) is a filler pane on Hypixel.
    static final int[] ORDER_SLOTS = {
            11, 12, 13, 14, 15, 16,
            20, 21, 22, 23, 24, 25,
            29, 30, 31, 32, 33, 34,
            38, 39, 40, 41, 42, 43
    };

    private final int selectedTab;

    public BazaarMenu(Player player) {
        this(player, 0);
    }

    private BazaarMenu(Player player, int tab) {
        super(player, "§6Bazaar ➜ §f" + TITLE_LABELS[tab], 6);
        this.selectedTab = tab;
    }

    @Override
    protected void populate() {
        for (int i = 0; i < CATEGORY_SLOTS.length; i++) {
            boolean selected = i == selectedTab;
            ItemBuilder b = new ItemBuilder(TAB_MATERIALS[i])
                    .displayName(CATEGORY_LABELS[i])
                    .lore(selected ? "§aSelected" : "§7Click to filter");
            if (selected) b.enchant(Enchantment.UNBREAKING, 1).flags(ItemFlag.HIDE_ENCHANTS);
            setItem(CATEGORY_SLOTS[i], b.build());
        }

        // Bottom control bar (wiki layout). Close is functional; the rest are display for now.
        setItem(45, new ItemBuilder(Material.NAME_TAG).displayName("§aSearch")
                .lore("§7Search for a specific product.").build());
        setItem(47, new ItemBuilder(Material.CHEST).displayName("§aSell Inventory")
                .lore("§7Sell sellable items straight", "§7from your inventory.").build());
        setItem(48, new ItemBuilder(Material.BUNDLE).displayName("§aSell Sacks")
                .lore("§7Sell the contents of your sacks.").build());
        setItem(49, new ItemBuilder(Material.BARRIER).displayName("§cClose").build());
        setItem(50, new ItemBuilder(Material.BOOK).displayName("§aManage Orders")
                .lore("§7View and manage your", "§7buy and sell orders.").build());
        setItem(51, new ItemBuilder(Material.MAP).displayName("§aBazaar History")
                .lore("§7View your recent trades.").build());
        setItem(52, new ItemBuilder(Material.REDSTONE_TORCH).displayName("§aSettings")
                .lore("§7Adjust your Bazaar settings.").build());

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
                    new BazaarMenu(clicker, i).open(clicker);
                }
                return;
            }
        }
        if (slot == 49 && event.getWhoClicked() instanceof Player clicker) {
            clicker.closeInventory();
            return;
        }
        // Clicking a product opens its instant buy/sell view.
        List<BazaarProduct> products = getFilteredProducts();
        for (int i = 0; i < ORDER_SLOTS.length && i < products.size(); i++) {
            if (ORDER_SLOTS[i] == slot && event.getWhoClicked() instanceof Player clicker) {
                new BazaarProductMenu(clicker, products.get(i)).open(clicker);
                return;
            }
        }
    }

    private List<BazaarProduct> getFilteredProducts() {
        List<BazaarProduct> result = new ArrayList<>();
        String[] cats = TAB_CATEGORIES[selectedTab];
        for (BazaarProduct p : BazaarProduct.values()) {
            String pc = p.getCategory().name();
            for (String cat : cats) {
                if (cat.equals(pc)) {
                    result.add(p);
                    break;
                }
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
