package com.skyblock.core.menu;

import com.skyblock.core.item.SkyblockItems;
import com.skyblock.core.manager.BazaarManager;
import com.skyblock.core.manager.BazaarManager.BazaarProduct;
import com.skyblock.core.manager.EconomyManager;
import com.skyblock.core.manager.SackManager;
import com.skyblock.core.util.Coins;
import com.skyblock.core.util.ItemBuilder;
import com.skyblock.core.util.SignInput;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public final class BazaarMenu extends AbstractSkyBlockMenu {

    // The Bazaar's 5 wiki categories; "Woods & Fishes" merges the FORAGING + FISHING products.
    private static final String[][] TAB_CATEGORIES = {
            {"FARMING"}, {"MINING"}, {"COMBAT"}, {"FORAGING", "FISHING"}, {"MISC"}
    };
    private static final String[]   CATEGORY_LABELS = {"§eFarming", "§bMining", "§cCombat", "§6Woods & Fishes", "§dOddities"};
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

    /** Category-themed spacer pane that frames the product grid (wiki colours). */
    private static final Material[] PANE_MATERIALS = {
            Material.YELLOW_STAINED_GLASS_PANE,  // Farming
            Material.CYAN_STAINED_GLASS_PANE,    // Mining
            Material.RED_STAINED_GLASS_PANE,     // Combat
            Material.ORANGE_STAINED_GLASS_PANE,  // Woods & Fishes
            Material.PINK_STAINED_GLASS_PANE     // Oddities
    };

    /** Spacer slots: the top row and the columns either side of the product grid. */
    private static final int[] SPACER_SLOTS = {
            1, 2, 3, 4, 5, 6, 7, 8,
            10, 17, 19, 26, 28, 35, 37, 44, 46, 53
    };

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
        // Category-themed spacer panes frame the grid (wiki layout).
        ItemStack spacer = new ItemBuilder(PANE_MATERIALS[selectedTab]).displayName(" ").build();
        for (int slot : SPACER_SLOTS) {
            setItem(slot, spacer);
        }

        for (int i = 0; i < CATEGORY_SLOTS.length; i++) {
            boolean selected = i == selectedTab;
            ItemBuilder b = new ItemBuilder(TAB_MATERIALS[i])
                    .displayName(CATEGORY_LABELS[i])
                    .lore("§8Category", "", selected ? "§aCurrently viewing!" : "§eClick to view!");
            if (selected) b.enchant(Enchantment.UNBREAKING, 1).flags(ItemFlag.HIDE_ENCHANTS);
            setItem(CATEGORY_SLOTS[i], b.build());
        }

        // Bottom control bar (wiki layout). Close is functional; the rest are display for now.
        setItem(45, new ItemBuilder(Material.OAK_SIGN).displayName("§aSearch")
                .lore("§7Find products by name!", "", "§eClick to search!").build());
        setItem(47, new ItemBuilder(Material.CHEST).displayName("§aSell Inventory Now")
                .lore("§7Instantly sell sellable items", "§7straight from your inventory.").build());
        setItem(48, new ItemBuilder(Material.CAULDRON).displayName("§bSell Sacks Now")
                .lore("§7Instantly sell everything in", "§7your sacks to the Bazaar.").build());
        setItem(49, new ItemBuilder(Material.BARRIER).displayName("§cClose").build());
        setItem(50, new ItemBuilder(Material.BOOK).displayName("§aManage Orders")
                .lore("§7View and manage your", "§7buy and sell orders.").build());
        setItem(51, new ItemBuilder(Material.MAP).displayName("§aBazaar History")
                .lore("§7View your recent trades.").build());
        setItem(52, new ItemBuilder(Material.REDSTONE_TORCH).displayName("§aBazaar Settings")
                .lore("§7View and edit your", "§7Bazaar settings.").build());

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
        if (!(event.getWhoClicked() instanceof Player clicker)) {
            return;
        }
        // Control bar.
        switch (slot) {
            case 45 -> { openSearch(clicker); return; }
            case 47 -> { sellInventoryNow(clicker); return; }
            case 48 -> { sellSacksNow(clicker); return; }
            case 49 -> { clicker.closeInventory(); return; }
            case 50 -> { new BazaarOrdersMenu(clicker).open(clicker); return; }
            case 51 -> { showHistory(clicker); return; }
            case 52 -> { showSettings(clicker); return; }
        }
        // Clicking a product opens its instant buy/sell view.
        List<BazaarProduct> products = getFilteredProducts();
        for (int i = 0; i < ORDER_SLOTS.length && i < products.size(); i++) {
            if (ORDER_SLOTS[i] == slot) {
                new BazaarProductMenu(clicker, products.get(i)).open(clicker);
                return;
            }
        }
    }

    /** Search: type a product name on a sign; opens the first matching product's page. */
    private void openSearch(Player clicker) {
        SignInput.request(clicker, "§8Search product", query -> {
            if (query.isBlank()) {
                new BazaarMenu(clicker, selectedTab).open(clicker);
                return;
            }
            String q = query.toLowerCase().trim();
            BazaarProduct match = null;
            for (BazaarProduct p : BazaarProduct.values()) {
                if (p.getDisplayName().toLowerCase().contains(q)) {
                    match = p;
                    break;
                }
            }
            if (match != null) {
                new BazaarProductMenu(clicker, match).open(clicker);
            } else {
                clicker.sendMessage("§cNo Bazaar product matches \"" + query + "\".");
                new BazaarMenu(clicker, selectedTab).open(clicker);
            }
        });
    }

    /** Instantly sells every raw Bazaar material in the player's inventory at the sell price. */
    private void sellInventoryNow(Player clicker) {
        BazaarManager mgr = BazaarManager.getInstance();
        double totalCoins = 0;
        int totalItems = 0;
        for (int i = 0; i < clicker.getInventory().getSize(); i++) {
            ItemStack item = clicker.getInventory().getItem(i);
            BazaarProduct product = productOf(item);
            if (product == null) {
                continue;
            }
            double price = mgr.getDisplaySellPrice(product);
            if (price <= 0) {
                continue;
            }
            int qty = item.getAmount();
            totalCoins += price * qty;
            totalItems += qty;
            clicker.getInventory().setItem(i, null);
        }
        if (totalItems > 0) {
            EconomyManager.getInstance().addCoins(clicker.getUniqueId(), totalCoins);
            clicker.sendMessage("§aSold §e" + totalItems + " §aitem(s) for §6" + formatCoinsFull(totalCoins) + " coins§a.");
        } else {
            clicker.sendMessage("§cNo sellable Bazaar items in your inventory.");
        }
        new BazaarMenu(clicker, selectedTab).open(clicker);
    }

    /** Instantly sells every sellable item stored in the player's sacks. */
    private void sellSacksNow(Player clicker) {
        BazaarManager mgr = BazaarManager.getInstance();
        SackManager sacks = SackManager.getInstance();
        double totalCoins = 0;
        int totalItems = 0;
        for (SackManager.SackType type : SackManager.SackType.values()) {
            Map<String, Integer> contents = sacks.getSackContents(clicker.getUniqueId(), type);
            for (Map.Entry<String, Integer> entry : new ArrayList<>(contents.entrySet())) {
                BazaarProduct product = BazaarManager.PRODUCT_DATA.get(entry.getKey());
                if (product == null || entry.getValue() <= 0) {
                    continue;
                }
                double price = mgr.getDisplaySellPrice(product);
                if (price <= 0) {
                    continue;
                }
                int removed = sacks.removeItem(clicker.getUniqueId(), type, entry.getKey(), entry.getValue());
                if (removed > 0) {
                    totalCoins += price * removed;
                    totalItems += removed;
                }
            }
        }
        if (totalItems > 0) {
            EconomyManager.getInstance().addCoins(clicker.getUniqueId(), totalCoins);
            clicker.sendMessage("§aSold §e" + totalItems + " §asack item(s) for §6" + formatCoinsFull(totalCoins) + " coins§a.");
        } else {
            clicker.sendMessage("§cNo sellable items in your sacks.");
        }
        new BazaarMenu(clicker, selectedTab).open(clicker);
    }

    /** Bazaar History: shows the player's claimable proceeds (recent fills aren't logged). */
    private void showHistory(Player clicker) {
        BazaarManager mgr = BazaarManager.getInstance();
        double coins = mgr.getClaimableCoins(clicker.getUniqueId());
        clicker.sendMessage("§6Bazaar ➜ History");
        clicker.sendMessage("§7Coins waiting in your claims: §6" + formatCoinsFull(coins) + " coins");
        clicker.sendMessage("§7Open §eManage Orders §7to collect filled orders.");
    }

    /** Bazaar Settings: shows the player's current sale-fee tier and rate. */
    private void showSettings(Player clicker) {
        BazaarManager mgr = BazaarManager.getInstance();
        BazaarManager.FeeTier tier = mgr.getFeeTier(clicker.getUniqueId());
        clicker.sendMessage("§6Bazaar ➜ Settings");
        clicker.sendMessage("§7Your sale fee tier: §e" + tier.name()
                + " §7(" + String.format("%.2f", tier.getRate() * 100) + "% fee)");
    }

    /** Resolves an inventory item to its Bazaar product (by SkyBlock id, else material), or null. */
    private static BazaarProduct productOf(ItemStack item) {
        if (item == null || item.getType() == Material.AIR) {
            return null;
        }
        String id = SkyblockItems.idOf(item);
        if (id != null) {
            BazaarProduct byId = BazaarManager.PRODUCT_DATA.get(id);
            if (byId != null) {
                return byId;
            }
        }
        return BazaarManager.PRODUCT_DATA.get(item.getType().name());
    }

    private static String formatCoinsFull(double coins) {
        return Coins.format(coins);
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
