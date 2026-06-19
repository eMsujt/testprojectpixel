package com.skyblock.core.menu;

import com.skyblock.core.manager.GardenManager.GardenCrop;
import com.skyblock.core.manager.JacobManager;
import com.skyblock.core.util.SkyblockUtils.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public final class JacobsContestMenu extends Menu {

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
        for (int i = 0; i < crops.length && i < 27; i++) {
            GardenCrop crop = crops[i];
            setItem(9 + i, new ItemBuilder(Material.WHEAT)
                    .displayName("§6" + crop.getDisplayName())
                    .lore("§7Best collection: §e" + manager.getBestCollection(playerId, crop))
                    .build(),
                    e -> e.setCancelled(true));
        }
    }

    private void fillBorder() {
        ItemStack pane = new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE)
                .displayName("§r")
                .build();
        for (int slot = 0; slot < 9; slot++) setItem(slot, pane);
    }
}
