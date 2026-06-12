package com.skyblock.core.listeners;

import com.skyblock.farming.CropType;
import com.skyblock.farming.FarmingManager;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import java.util.EnumMap;
import java.util.Map;
import java.util.UUID;

/**
 * Bukkit listener that routes {@link BlockBreakEvent} into the per-skill
 * progression managers (farming).
 *
 * <p>Each broken block is checked against the crop XP map.
 * Mining is handled by {@link com.skyblock.core.mining.MiningListener}.
 * Foraging is handled by {@link com.skyblock.core.foraging.ForagingListener}.
 * Blocks not in any map are silently ignored.</p>
 */
public final class SkyBlockEventListener implements Listener {

    private static final Map<Material, CropType> MATERIAL_TO_CROP;

    static {
        Map<Material, CropType> map = new EnumMap<>(Material.class);
        map.put(Material.WHEAT,           CropType.WHEAT);
        map.put(Material.CARROTS,         CropType.CARROT);
        map.put(Material.POTATOES,        CropType.POTATO);
        map.put(Material.PUMPKIN,         CropType.PUMPKIN);
        map.put(Material.MELON,           CropType.MELON);
        map.put(Material.SUGAR_CANE,      CropType.SUGAR_CANE);
        map.put(Material.COCOA_BEANS,     CropType.COCOA_BEANS);
        map.put(Material.CACTUS,          CropType.CACTUS);
        map.put(Material.BROWN_MUSHROOM,  CropType.MUSHROOM);
        map.put(Material.RED_MUSHROOM,    CropType.MUSHROOM);
        map.put(Material.NETHER_WART,     CropType.NETHER_WART);
        MATERIAL_TO_CROP = Map.copyOf(map);
    }

    private final FarmingManager farmingManager;

    /**
     * Creates a listener that dispatches block-break events to the farming manager.
     *
     * @param farmingManager the farming manager, must not be null
     * @throws IllegalArgumentException if farmingManager is null
     */
    public SkyBlockEventListener(FarmingManager farmingManager) {
        if (farmingManager == null) {
            throw new IllegalArgumentException("farmingManager must not be null");
        }
        this.farmingManager = farmingManager;
    }

    /**
     * Dispatches a block-break to the appropriate skill manager.
     *
     * @param event the block-break event fired by the server
     */
    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Material material = event.getBlock().getType();
        UUID playerId = player.getUniqueId();

        CropType crop = MATERIAL_TO_CROP.get(material);
        if (crop != null) {
            farmingManager.recordHarvest(playerId, crop, 1);
        }
    }
}
