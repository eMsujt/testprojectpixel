package com.skyblock.core.menu;

import com.skyblock.core.manager.GardenManager.GardenCrop;
import com.skyblock.core.manager.JacobFarmingManager;
import com.skyblock.core.util.SkyblockUtils.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * 54-slot Jacob's Farming skill menu.
 *
 * <p>Slot 4 holds a golden-hoe summary item showing the player's farming level,
 * XP, and total crops harvested (from {@link JacobFarmingManager}). The middle
 * rows render each {@link GardenCrop} with its individual harvest count. Top and
 * bottom rows are gray-pane borders.</p>
 */
public final class JacobFarmingMenu extends Menu {

    static final int SUMMARY_SLOT = 4;
    private static final int[] CROP_SLOTS = {19, 20, 21, 22, 23, 24, 25, 28, 29, 30, 31, 32};

    private final UUID playerId;

    public JacobFarmingMenu(UUID playerId) {
        super("§6Jacob's Farming", 6);
        this.playerId = playerId;
    }

    @Override
    protected void build() {
        ItemStack pane = new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE).displayName("§r").build();
        for (int slot = 0; slot < 9; slot++) setItem(slot, pane);
        for (int slot = 45; slot < 54; slot++) setItem(slot, pane);

        JacobFarmingManager manager = JacobFarmingManager.getInstance();
        int level = manager.getLevel(playerId);
        double xp = manager.getXp(playerId);
        long totalHarvested = manager.getTotalHarvested(playerId);

        List<String> summaryLore = new ArrayList<>();
        summaryLore.add("§7Level: §e" + level);
        summaryLore.add("§7XP: §e" + String.format("%.1f", xp));
        summaryLore.add("§7Total Harvested: §e" + totalHarvested);
        setItem(SUMMARY_SLOT, new ItemBuilder(Material.GOLDEN_HOE)
                .displayName("§eJacob's Farming")
                .lore(summaryLore)
                .build());

        GardenCrop[] crops = GardenCrop.values();
        for (int i = 0; i < CROP_SLOTS.length && i < crops.length; i++) {
            GardenCrop crop = crops[i];
            long harvested = manager.getCropHarvested(playerId, crop);
            setItem(CROP_SLOTS[i], new ItemBuilder(materialFor(crop))
                    .displayName("§a" + crop.getDisplayName())
                    .lore("§7Harvested: §e" + harvested)
                    .build());
        }
    }

    private static Material materialFor(GardenCrop crop) {
        switch (crop) {
            case WHEAT:        return Material.WHEAT;
            case CARROT:       return Material.CARROT;
            case POTATO:       return Material.POTATO;
            case MELON:        return Material.MELON_SLICE;
            case PUMPKIN:      return Material.PUMPKIN;
            case SUGAR_CANE:   return Material.SUGAR_CANE;
            case COCOA_BEANS:  return Material.COCOA_BEANS;
            case CACTUS:       return Material.CACTUS;
            case MUSHROOM:     return Material.RED_MUSHROOM;
            case NETHER_WART:  return Material.NETHER_WART;
            case CABBAGE:      return Material.WHEAT;
            case COARSE_POTATO: return Material.POTATO;
            default:           return Material.WHEAT;
        }
    }

    @Override
    public void handleClick(InventoryClickEvent event) {
        event.setCancelled(true);
    }
}
