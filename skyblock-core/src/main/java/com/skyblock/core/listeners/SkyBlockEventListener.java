package com.skyblock.core.listeners;

import com.skyblock.farming.CropType;
import com.skyblock.farming.FarmingManager;
import com.skyblock.foraging.ForagingManager;
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
 * progression managers (farming, foraging).
 *
 * <p>Each broken block is checked against the skill XP maps in order:
 * crops → {@link FarmingManager}, logs → {@link ForagingManager}.
 * Mining is handled by {@link com.skyblock.core.mining.MiningListener}.
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
    private final ForagingManager foragingManager;

    /**
     * Creates a listener that dispatches block-break events to the given managers.
     *
     * @param farmingManager  the farming manager, must not be null
     * @param foragingManager the foraging manager, must not be null
     * @throws IllegalArgumentException if any argument is null
     */
    public SkyBlockEventListener(FarmingManager farmingManager,
                                  ForagingManager foragingManager) {
        if (farmingManager == null) {
            throw new IllegalArgumentException("farmingManager must not be null");
        }
        if (foragingManager == null) {
            throw new IllegalArgumentException("foragingManager must not be null");
        }
        this.farmingManager = farmingManager;
        this.foragingManager = foragingManager;
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
            return;
        }

        Integer foragingXp = ForagingManager.WOOD_XP_MAP.get(material);
        if (foragingXp != null) {
            foragingManager.recordChop(playerId, foragingXp);
        }
    }
}
