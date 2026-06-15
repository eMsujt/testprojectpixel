package com.skyblock.plugin.gui.menu;

import com.skyblock.plugin.gui.ItemBuilder;
import com.skyblock.plugin.gui.Menu;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class SacksMenu extends Menu {

    private enum Sack {
        FARMING("Farming Sack", "§a", Material.WHEAT, 10),
        MINING("Mining Sack", "§7", Material.COAL, 11),
        COMBAT("Combat Sack", "§c", Material.ROTTEN_FLESH, 12),
        FORAGING("Foraging Sack", "§2", Material.OAK_LOG, 13),
        FISHING("Fishing Sack", "§9", Material.COD, 14);

        private final String displayName;
        private final String color;
        private final Material icon;
        private final int slot;

        Sack(String displayName, String color, Material icon, int slot) {
            this.displayName = displayName;
            this.color = color;
            this.icon = icon;
            this.slot = slot;
        }
    }

    public SacksMenu() {
        super("§aSacks", 6);
    }

    @Override
    protected void build() {
        fillBorder();

        for (Sack sack : Sack.values()) {
            setItem(sack.slot, new ItemBuilder(sack.icon)
                    .displayName(sack.color + sack.displayName)
                    .lore("§7Click to view your " + sack.displayName.toLowerCase() + ".")
                    .build());
        }
    }

    private void fillBorder() {
        ItemStack pane = new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE)
                .displayName("§r")
                .build();
        for (int slot = 0; slot < 9; slot++)  setItem(slot, pane);
        for (int slot = 45; slot < 54; slot++) setItem(slot, pane);
    }
}
