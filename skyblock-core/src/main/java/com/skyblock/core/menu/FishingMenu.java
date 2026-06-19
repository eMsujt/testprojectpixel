package com.skyblock.core.menu;

import com.skyblock.core.manager.FishingManager.FishingTrophy;
import com.skyblock.core.util.SkyblockUtils.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public final class FishingMenu extends Menu {

    /** Fish-themed icons cycled across the trophy slots. */
    private static final Material[] FISH_ICONS = {
            Material.COD, Material.SALMON, Material.TROPICAL_FISH, Material.PUFFERFISH
    };

    public FishingMenu() {
        super("§bFishing", 6);
    }

    @Override
    protected void build() {
        FishingTrophy[] trophies = FishingTrophy.values();
        for (int i = 0; i < trophies.length && i < 54; i++) {
            FishingTrophy trophy = trophies[i];
            ItemStack item = new ItemBuilder(FISH_ICONS[i % FISH_ICONS.length])
                    .displayName("§b" + trophy.getDisplayName())
                    .lore("§7Min Level: §e" + trophy.minLevel)
                    .build();
            setItem(i, item);
        }
    }

    @Override
    public void handleClick(InventoryClickEvent event) {
        event.setCancelled(true);
    }
}
