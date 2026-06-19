package com.skyblock.core.menu;

import com.skyblock.core.manager.EconomyManager;
import com.skyblock.core.util.SkyblockUtil.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

/**
 * Canonical NPC shop menu. 54-slot chest with a gray glass-pane border; wares
 * come from a caller-supplied {@link List} of {@link ShopItem}s placed in the 21
 * centred inner slots (rows 2–4).
 *
 * <p>Left-clicking buys an item for its {@code buyPrice}; right-clicking sells
 * one from the player's inventory for its {@code sellPrice} (0 = not sellable).
 * Both operations use {@link EconomyManager}.</p>
 *
 * <p>All other ShopMenu / NpcShopMenu / ShopGui classes in the project are
 * deprecated and delegate here.</p>
 */
public class ShopMenu extends Menu {

    /**
     * A single shop entry: the material, its buy price, and its sell price.
     * A {@code sellPrice} of {@code 0} means the item cannot be sold back.
     */
    public record ShopItem(Material material, int buyPrice, int sellPrice) {
        public ShopItem {
            Objects.requireNonNull(material, "material");
            if (buyPrice < 0) throw new IllegalArgumentException("buyPrice must not be negative");
            if (sellPrice < 0) throw new IllegalArgumentException("sellPrice must not be negative");
        }

        /** Convenience constructor for buy-only items. */
        public ShopItem(Material material, int buyPrice) {
            this(material, buyPrice, 0);
        }
    }

    /** Centred content slots across the middle rows, left-to-right, top-to-bottom. */
    private static final int[] SLOTS = {
            10, 11, 12, 13, 14, 15, 16,
            19, 20, 21, 22, 23, 24, 25,
            28, 29, 30, 31, 32, 33, 34
    };

    private final List<ShopItem> items;

    public ShopMenu(String title, List<ShopItem> items) {
        super(Objects.requireNonNull(title, "title"), 6);
        this.items = List.copyOf(Objects.requireNonNull(items, "items"));
    }

    @Override
    protected void build() {
        fillBorder();

        int count = Math.min(items.size(), SLOTS.length);
        for (int i = 0; i < count; i++) {
            ShopItem item = items.get(i);
            setItem(SLOTS[i], buildIcon(item), event -> {
                event.setCancelled(true);
                HumanEntity who = event.getWhoClicked();
                if (event.getClick() == ClickType.RIGHT) {
                    sell(who, item);
                } else {
                    buy(who, item);
                }
            });
        }
    }

    private static void buy(HumanEntity who, ShopItem item) {
        if (item.buyPrice() <= 0) {
            who.sendMessage("§cThis item is not for sale!");
            return;
        }
        if (EconomyManager.getInstance().withdraw(who.getUniqueId(), (double) item.buyPrice())) {
            who.getInventory().addItem(new ItemStack(item.material()));
            who.sendMessage("§aPurchased §6" + formatName(item.material())
                    + " §afor §6" + item.buyPrice() + " coins§a!");
        } else {
            who.sendMessage("§cYou don't have enough coins!");
        }
    }

    private static void sell(HumanEntity who, ShopItem item) {
        if (item.sellPrice() <= 0) {
            who.sendMessage("§cThis item cannot be sold here!");
            return;
        }
        ItemStack toRemove = new ItemStack(item.material(), 1);
        if (!who.getInventory().containsAtLeast(toRemove, 1)) {
            who.sendMessage("§cYou don't have §6" + formatName(item.material()) + " §cto sell!");
            return;
        }
        who.getInventory().removeItem(toRemove);
        EconomyManager.getInstance().addCoins(who.getUniqueId(), (double) item.sellPrice());
        who.sendMessage("§aSold §6" + formatName(item.material())
                + " §afor §6" + item.sellPrice() + " coins§a!");
    }

    private static ItemStack buildIcon(ShopItem item) {
        List<String> lore = new ArrayList<>();
        if (item.buyPrice() > 0) {
            lore.add("§7Buy: §6" + item.buyPrice() + " coins");
            lore.add("§eLeft-click to buy!");
        }
        if (item.sellPrice() > 0) {
            lore.add("§7Sell: §6" + item.sellPrice() + " coins");
            lore.add("§eRight-click to sell!");
        }
        return new ItemBuilder(item.material())
                .displayName("§f" + formatName(item.material()))
                .lore(lore.toArray(new String[0]))
                .build();
    }

    private void fillBorder() {
        ItemStack pane = new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE)
                .displayName("§r")
                .build();
        for (int slot = 0; slot < 54; slot++) {
            int col = slot % 9;
            if (slot < 9 || slot >= 45 || col == 0 || col == 8) {
                setItem(slot, pane);
            }
        }
    }

    private static String formatName(Material material) {
        StringBuilder sb = new StringBuilder();
        for (String word : material.name().split("_")) {
            if (!word.isEmpty()) {
                sb.append(Character.toUpperCase(word.charAt(0)))
                  .append(word.substring(1).toLowerCase(Locale.ROOT))
                  .append(' ');
            }
        }
        return sb.toString().trim();
    }
}
