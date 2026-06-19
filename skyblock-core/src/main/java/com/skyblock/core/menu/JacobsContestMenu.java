package com.skyblock.core.menu;

import com.skyblock.core.manager.GardenManager.GardenCrop;
import com.skyblock.core.manager.JacobManager;
import com.skyblock.core.util.SkyblockUtils.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

/**
 * Jacob's Farming Contest hub menu opened by {@code /jacobscontest}. Displays one
 * tile per {@link GardenCrop} showing the viewing player's best contest collection
 * for that crop, with an overall medal summary.
 */
public class JacobsContestMenu extends Menu {

    static final int SUMMARY_SLOT = 4;

    private final UUID playerId;

    public JacobsContestMenu(UUID playerId) {
        super("§aJacob's Farming Contest", 4);
        this.playerId = playerId;
    }

    @Override
    protected void build() {
        fillBorder();
        JacobManager manager = JacobManager.getInstance();

        setItem(SUMMARY_SLOT, new ItemBuilder(Material.WHEAT)
                .displayName("§aJacob's Farming Contest")
                .lore(
                        "§7Contests participated: §e" + manager.getContestsParticipated(playerId),
                        "§7Total medals: §e" + manager.getTotalMedals(playerId))
                .build(),
                e -> e.setCancelled(true));

        GardenCrop[] crops = GardenCrop.values();
        for (int i = 0; i < crops.length; i++) {
            GardenCrop crop = crops[i];
            setItem(9 + i, new ItemBuilder(materialFor(crop))
                    .displayName("§a" + crop.getDisplayName())
                    .lore("§7Best collection: §e" + manager.getBestCollection(playerId, crop))
                    .build(),
                    e -> e.setCancelled(true));
        }
    }

    private static Material materialFor(GardenCrop crop) {
        switch (crop) {
            case WHEAT: return Material.WHEAT;
            case CARROT: return Material.CARROT;
            case POTATO: return Material.POTATO;
            case MELON: return Material.MELON_SLICE;
            case PUMPKIN: return Material.PUMPKIN;
            case SUGAR_CANE: return Material.SUGAR_CANE;
            case COCOA_BEANS: return Material.COCOA_BEANS;
            case CACTUS: return Material.CACTUS;
            case MUSHROOM: return Material.RED_MUSHROOM;
            case NETHER_WART: return Material.NETHER_WART;
            case CABBAGE: return Material.GREEN_DYE;
            case COARSE_POTATO: return Material.POISONOUS_POTATO;
            default: return Material.WHEAT;
        }
    }

    private void fillBorder() {
        ItemStack pane = new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE)
                .displayName("§r")
                .build();
        for (int slot = 0; slot < 9; slot++) setItem(slot, pane);
    }
}
