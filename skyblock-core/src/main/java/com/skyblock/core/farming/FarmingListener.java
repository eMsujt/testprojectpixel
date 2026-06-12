package com.skyblock.core.farming;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import java.util.EnumMap;
import java.util.Map;

/**
 * Bukkit listener that intercepts {@link BlockBreakEvent} for farming crops
 * and routes harvests to {@link FarmingManager}.
 */
public final class FarmingListener implements Listener {

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

    public FarmingListener(FarmingManager farmingManager) {
        this.farmingManager = farmingManager;
    }

    /**
     * Intercepts crop block-break events and records a harvest in
     * {@link FarmingManager}, awarding skill XP to the breaking player.
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
        farmingManager.recordHarvest(player.getUniqueId(), crop, 1);
    }
}
