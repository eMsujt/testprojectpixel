package com.skyblock.core.menu;

import com.skyblock.core.manager.GardenManager.GardenCrop;
import com.skyblock.core.manager.JacobManager;
import com.skyblock.core.util.SkyblockUtils.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public final class JacobsContestMenu extends Menu {

    static final int SUMMARY_SLOT = 4;

    private final UUID playerId;

    public JacobsContestMenu(UUID playerId) {
        super("§eJacob's Farming Contest", 6);
        this.playerId = playerId;
    }

    @Override
    protected void build() {
        ItemStack pane = new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE).displayName("§r").build();
        for (int slot = 0; slot < 9; slot++) setItem(slot, pane);
        for (int slot = 45; slot < 54; slot++) setItem(slot, pane);

        JacobManager manager = JacobManager.getInstance();

        setItem(SUMMARY_SLOT, new ItemBuilder(Material.WHEAT)
                .displayName("§eJacob's Farming Contest")
                .lore(
                        "§7Contests participated: §e" + manager.getContestsParticipated(playerId),
                        "§7Total medals: §e" + manager.getTotalMedals(playerId))
                .build());

        GardenCrop[] crops = GardenCrop.values();
        for (int i = 0; i < crops.length && i < 36; i++) {
            GardenCrop crop = crops[i];
            setItem(9 + i, new ItemBuilder(materialFor(crop))
                    .displayName("§6" + crop.getDisplayName())
                    .lore("§7Best collection: §e" + manager.getBestCollection(playerId, crop))
                    .build());
        }
    }

    private static Material materialFor(GardenCrop crop) {
        switch (crop) {
            case WHEAT:         return Material.WHEAT;
            case CARROT:        return Material.CARROT;
            case POTATO:        return Material.POTATO;
            case PUMPKIN:       return Material.PUMPKIN;
            case MELON:         return Material.MELON_SLICE;
            case SUGAR_CANE:    return Material.SUGAR_CANE;
            case COCOA_BEANS:   return Material.COCOA_BEANS;
            case CACTUS:        return Material.CACTUS;
            case MUSHROOM:      return Material.RED_MUSHROOM;
            case NETHER_WART:   return Material.NETHER_WART;
            case CABBAGE:       return Material.WHEAT;
            case COARSE_POTATO: return Material.POTATO;
            default:            return Material.WHEAT;
        }
    }

    @Override
    public void handleClick(InventoryClickEvent event) {
        event.setCancelled(true);
    }
}
