package com.skyblock.core.menu;

import com.skyblock.core.manager.EnchantingManager;
import com.skyblock.core.manager.EnchantingManager.SkyBlockEnchantment;
import com.skyblock.core.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public final class EnchantingMenu extends AbstractSkyBlockMenu {

    public static final int TABLE_SLOT = 22;

    private static final int[] SLOTS = {
            10, 11, 12, 13, 14, 15, 16,
            19, 20, 21, 22, 23, 24, 25,
            28, 29, 30, 31, 32, 33, 34,
            37, 38, 39, 40, 41, 42, 43
    };

    public EnchantingMenu(Player player) {
        super(player, "§5§lEnchanting Table", 6);
    }

    @Override
    protected void populate() {
        ItemStack pane = new ItemBuilder(Material.PURPLE_STAINED_GLASS_PANE).displayName("§r").build();
        for (int slot = 0; slot < 9; slot++) setItem(slot, pane);
        for (int slot = 45; slot < 54; slot++) setItem(slot, pane);

        EnchantingManager mgr = EnchantingManager.getInstance();
        SkyBlockEnchantment[] enchants = SkyBlockEnchantment.values();
        for (int i = 0; i < enchants.length && i < SLOTS.length; i++) {
            SkyBlockEnchantment enchant = enchants[i];
            int maxLevel = enchant.getMaxLevel();
            int playerLevel = mgr.getLevel(player.getUniqueId(), enchant);
            int cost = mgr.getEnchantCost(enchant, 1);
            boolean ultimate = mgr.isUltimate(enchant);
            String prefix = ultimate ? "§d§l" : "§9";
            Material mat = ultimate ? Material.KNOWLEDGE_BOOK : Material.ENCHANTED_BOOK;
            setItem(SLOTS[i], new ItemBuilder(mat)
                    .displayName(prefix + formatName(enchant.name()))
                    .lore(
                            "§7Level: §e" + playerLevel + " §8/ §e" + maxLevel,
                            "§7Cost (Lv 1): §e" + cost + " exp",
                            ultimate ? "§d✦ Ultimate Enchantment" : "")
                    .build(),
                    e -> e.setCancelled(true));
        }
    }

    @Override
    public void handleClick(InventoryClickEvent event) {
        event.setCancelled(true);
    }

    private static String formatName(String name) {
        String[] parts = name.toLowerCase().split("_");
        StringBuilder sb = new StringBuilder();
        for (String part : parts) {
            if (!part.isEmpty()) {
                if (!sb.isEmpty()) sb.append(' ');
                sb.append(Character.toUpperCase(part.charAt(0)));
                sb.append(part.substring(1));
            }
        }
        return sb.toString();
    }
}
