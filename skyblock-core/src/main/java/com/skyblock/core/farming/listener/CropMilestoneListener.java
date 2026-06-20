package com.skyblock.core.farming.listener;

import com.skyblock.core.farming.manager.FarmingManager;
import com.skyblock.core.util.ChatUtil;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import java.util.EnumMap;
import java.util.Map;

/**
 * Bukkit listener that fires crop-milestone notifications when a player's
 * per-crop harvest count crosses a milestone threshold tracked by
 * {@link FarmingManager}.
 */
public final class CropMilestoneListener implements Listener {

    /** Harvest-count thresholds that trigger a milestone message. */
    private static final int[] MILESTONES = {
        100, 250, 500, 1_000, 2_500, 5_000, 10_000, 25_000, 50_000, 100_000
    };

    private static final Map<Material, FarmingManager.CropType> MATERIAL_TO_CROP;

    static {
        Map<Material, FarmingManager.CropType> map = new EnumMap<>(Material.class);
        map.put(Material.WHEAT,          FarmingManager.CropType.WHEAT);
        map.put(Material.CARROTS,        FarmingManager.CropType.CARROT);
        map.put(Material.POTATOES,       FarmingManager.CropType.POTATO);
        map.put(Material.PUMPKIN,        FarmingManager.CropType.PUMPKIN);
        map.put(Material.MELON,          FarmingManager.CropType.MELON);
        map.put(Material.SUGAR_CANE,     FarmingManager.CropType.SUGAR_CANE);
        map.put(Material.COCOA_BEANS,    FarmingManager.CropType.COCOA_BEANS);
        map.put(Material.CACTUS,         FarmingManager.CropType.CACTUS);
        map.put(Material.BROWN_MUSHROOM, FarmingManager.CropType.MUSHROOM);
        map.put(Material.RED_MUSHROOM,   FarmingManager.CropType.MUSHROOM);
        map.put(Material.NETHER_WART,    FarmingManager.CropType.NETHER_WART);
        MATERIAL_TO_CROP = Map.copyOf(map);
    }

    private final FarmingManager farmingManager;

    public CropMilestoneListener(FarmingManager farmingManager) {
        this.farmingManager = farmingManager;
    }

    /**
     * Checks whether the player has just crossed a harvest milestone for the
     * broken crop and sends a chat notification if so.
     *
     * @param event the block-break event
     */
    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        FarmingManager.CropType crop = MATERIAL_TO_CROP.get(event.getBlock().getType());
        if (crop == null) {
            return;
        }
        Player player = event.getPlayer();
        int total = farmingManager.getHarvests(player.getUniqueId(), crop);
        for (int milestone : MILESTONES) {
            if (total == milestone) {
                ChatUtil.send(player, "[Farming] Milestone reached: " + milestone
                        + " " + crop.getDisplayName() + " harvested!");
                break;
            }
        }
    }
}
