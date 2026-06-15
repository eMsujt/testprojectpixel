package com.skyblock.plugin.gui.menu;

import com.skyblock.plugin.gui.ItemBuilder;
import com.skyblock.plugin.gui.Menu;
import com.skyblock.plugin.manager.EconomyManager;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.ItemStack;

public class NpcShopMenu extends Menu {

    private record ShopItem(Material material, String name, int price) {}

    private static final ShopItem[] ITEMS = {
            new ShopItem(Material.IRON_INGOT,    "§fIron Ingot",    10),
            new ShopItem(Material.GOLD_INGOT,    "§fGold Ingot",    15),
            new ShopItem(Material.DIAMOND,       "§fDiamond",       50),
            new ShopItem(Material.COAL,          "§fCoal",           5),
            new ShopItem(Material.IRON_SWORD,    "§fIron Sword",    25),
            new ShopItem(Material.IRON_CHESTPLATE,"§fIron Chestplate",75),
            new ShopItem(Material.IRON_HELMET,   "§fIron Helmet",   50),
    };

    private static final int[] SLOTS = {10, 11, 12, 13, 14, 15, 16};

    public NpcShopMenu() {
        super("§aMagnus the Blacksmith", 6);
    }

    @Override
    protected void build() {
        fillBorder();

        for (int i = 0; i < ITEMS.length; i++) {
            ShopItem item = ITEMS[i];
            setItem(SLOTS[i], new ItemBuilder(item.material())
                            .displayName(item.name())
                            .lore("§7Price: §6" + item.price() + " coins", "§eClick to buy!")
                            .build(),
                    event -> {
                        event.setCancelled(true);
                        HumanEntity who = event.getWhoClicked();
                        if (EconomyManager.getInstance().removeCoins(who.getUniqueId(), item.price())) {
                            who.getInventory().addItem(new ItemStack(item.material()));
                            who.sendMessage("§aPurchased " + item.name()
                                    + " §afor §6" + item.price() + " coins§a!");
                        } else {
                            who.sendMessage("§cYou don't have enough coins!");
                        }
                    });
        }
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
}
