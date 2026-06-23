package com.skyblock.core.menu;

import com.skyblock.core.manager.EconomyManager;
import com.skyblock.core.manager.ShopManager;
import com.skyblock.core.manager.ShopManager.ShopEntry;
import com.skyblock.core.util.HeadTextures;
import com.skyblock.core.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * 54-slot NPC shop GUI. Left-click buys; right-click sells (when sellPrice &gt; 0).
 * Gray glass-pane border; wares in the 28 inner slots.
 */
public final class NPCShopMenu extends Menu {

    private static final int[] ITEM_SLOTS = {
        10, 11, 12, 13, 14, 15, 16,
        19, 20, 21, 22, 23, 24, 25,
        28, 29, 30, 31, 32, 33, 34,
        37, 38, 39, 40, 41, 42, 43
    };

    private final List<ShopEntry> entries;

    public NPCShopMenu(String shopId) {
        super(resolveTitle(shopId), 6);
        this.entries = ShopManager.getInstance()
                .getShop(shopId)
                .map(ShopManager.Shop::entries)
                .orElse(List.of());
    }

    private static String resolveTitle(String shopId) {
        return ShopManager.getInstance()
                .getShop(shopId)
                .map(ShopManager.Shop::title)
                .orElse("§6NPC Shop");
    }

    @Override
    protected void build() {
        ItemStack pane = new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE).displayName("§r").build();
        for (int slot = 0; slot < 54; slot++) {
            int col = slot % 9;
            if (slot < 9 || slot >= 45 || col == 0 || col == 8) {
                setItem(slot, pane);
            }
        }

        int count = Math.min(entries.size(), ITEM_SLOTS.length);
        for (int i = 0; i < count; i++) {
            ShopEntry entry = entries.get(i);
            Material mat = HeadTextures.itemMaterial(entry.itemId());
            List<String> lore = new ArrayList<>();
            lore.add("§7Buy: §6" + entry.buyPrice() + " coins");
            if (entry.sellPrice() > 0) {
                lore.add("§7Sell: §6" + entry.sellPrice() + " coins");
                lore.add("§eLeft-click to buy  §7|  §eRight-click to sell");
            } else {
                lore.add("§eLeft-click to buy!");
            }
            ItemStack icon = ItemBuilder.forItem(entry.itemId())
                    .displayName("§f" + formatName(entry.itemId()))
                    .lore(lore.toArray(new String[0]))
                    .build();
            setItem(ITEM_SLOTS[i], icon, event -> handleClick(event, entry, mat));
        }
    }

    private static void handleClick(InventoryClickEvent event, ShopEntry entry, Material mat) {
        event.setCancelled(true);
        HumanEntity who = event.getWhoClicked();
        boolean rightClick = switch (event.getClick()) {
            case RIGHT, SHIFT_RIGHT -> true;
            default -> false;
        };
        if (rightClick) {
            if (entry.sellPrice() <= 0) {
                who.sendMessage("§cThis item cannot be sold here!");
                return;
            }
            ItemStack toRemove = new ItemStack(mat, 1);
            if (!who.getInventory().containsAtLeast(toRemove, 1)) {
                who.sendMessage("§cYou don't have §6" + formatName(entry.itemId()) + " §cto sell!");
                return;
            }
            who.getInventory().removeItem(toRemove);
            EconomyManager.getInstance().addCoins(who.getUniqueId(), (double) entry.sellPrice());
            who.sendMessage("§aSold §6" + formatName(entry.itemId()) + " §afor §6" + entry.sellPrice() + " coins§a!");
        } else {
            if (!EconomyManager.getInstance().withdraw(who.getUniqueId(), (double) entry.buyPrice())) {
                who.sendMessage("§cYou don't have enough coins! (§6" + entry.buyPrice() + " §crequired)");
                return;
            }
            who.getInventory().addItem(new ItemStack(mat));
            who.sendMessage("§aPurchased §6" + formatName(entry.itemId()) + " §afor §6" + entry.buyPrice() + " coins§a!");
        }
    }

    private static String formatName(String itemId) {
        StringBuilder sb = new StringBuilder();
        for (String word : itemId.split("_")) {
            if (!word.isEmpty()) {
                sb.append(Character.toUpperCase(word.charAt(0)))
                  .append(word.substring(1).toLowerCase(Locale.ROOT))
                  .append(' ');
            }
        }
        return sb.toString().trim();
    }
}
