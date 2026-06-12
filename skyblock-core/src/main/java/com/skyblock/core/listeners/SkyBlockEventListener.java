package com.skyblock.core.listeners;

import com.skyblock.core.collections.CollectionManager;
import com.skyblock.core.collections.CollectionManager.Collection;
import com.skyblock.farming.CropType;
import com.skyblock.farming.FarmingManager;
import com.skyblock.foraging.ForagingManager;
import com.skyblock.mining.MiningManager;
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
 * progression managers (mining, farming, foraging) and grants collection
 * progress via {@link CollectionManager}.
 *
 * <p>Each broken block is checked against the three skill XP maps in order:
 * ores → {@link MiningManager}, crops → {@link FarmingManager}, logs →
 * {@link ForagingManager}. Blocks not in any map are silently ignored.</p>
 */
public final class SkyBlockEventListener implements Listener {

    private static final Map<Material, CropType> MATERIAL_TO_CROP;
    private static final Map<Material, Collection> MATERIAL_TO_COLLECTION;

    static {
        Map<Material, CropType> cropMap = new EnumMap<>(Material.class);
        cropMap.put(Material.WHEAT,           CropType.WHEAT);
        cropMap.put(Material.CARROTS,         CropType.CARROT);
        cropMap.put(Material.POTATOES,        CropType.POTATO);
        cropMap.put(Material.PUMPKIN,         CropType.PUMPKIN);
        cropMap.put(Material.MELON,           CropType.MELON);
        cropMap.put(Material.SUGAR_CANE,      CropType.SUGAR_CANE);
        cropMap.put(Material.COCOA_BEANS,     CropType.COCOA_BEANS);
        cropMap.put(Material.CACTUS,          CropType.CACTUS);
        cropMap.put(Material.BROWN_MUSHROOM,  CropType.MUSHROOM);
        cropMap.put(Material.RED_MUSHROOM,    CropType.MUSHROOM);
        cropMap.put(Material.NETHER_WART,     CropType.NETHER_WART);
        MATERIAL_TO_CROP = Map.copyOf(cropMap);

        Map<Material, Collection> colMap = new EnumMap<>(Material.class);
        // Ores → collection (mapped to their drop, matching the Collection enum)
        colMap.put(Material.COAL_ORE,                 Collection.COAL);
        colMap.put(Material.DEEPSLATE_COAL_ORE,       Collection.COAL);
        colMap.put(Material.IRON_ORE,                 Collection.IRON_INGOT);
        colMap.put(Material.DEEPSLATE_IRON_ORE,       Collection.IRON_INGOT);
        colMap.put(Material.GOLD_ORE,                 Collection.GOLD_INGOT);
        colMap.put(Material.DEEPSLATE_GOLD_ORE,       Collection.GOLD_INGOT);
        colMap.put(Material.NETHER_GOLD_ORE,          Collection.GOLD_INGOT);
        colMap.put(Material.DIAMOND_ORE,              Collection.DIAMOND);
        colMap.put(Material.DEEPSLATE_DIAMOND_ORE,    Collection.DIAMOND);
        colMap.put(Material.EMERALD_ORE,              Collection.EMERALD);
        colMap.put(Material.DEEPSLATE_EMERALD_ORE,    Collection.EMERALD);
        colMap.put(Material.REDSTONE_ORE,             Collection.REDSTONE);
        colMap.put(Material.DEEPSLATE_REDSTONE_ORE,   Collection.REDSTONE);
        colMap.put(Material.LAPIS_ORE,                Collection.LAPIS_LAZULI);
        colMap.put(Material.DEEPSLATE_LAPIS_ORE,      Collection.LAPIS_LAZULI);
        colMap.put(Material.NETHER_QUARTZ_ORE,        Collection.QUARTZ);
        // Crops → collection
        colMap.put(Material.WHEAT,                    Collection.WHEAT);
        colMap.put(Material.CARROTS,                  Collection.CARROT);
        colMap.put(Material.POTATOES,                 Collection.POTATO);
        colMap.put(Material.PUMPKIN,                  Collection.PUMPKIN);
        colMap.put(Material.MELON,                    Collection.MELON);
        colMap.put(Material.SUGAR_CANE,               Collection.SUGAR_CANE);
        colMap.put(Material.COCOA_BEANS,              Collection.COCOA_BEANS);
        colMap.put(Material.CACTUS,                   Collection.CACTUS);
        colMap.put(Material.BROWN_MUSHROOM,           Collection.MUSHROOM);
        colMap.put(Material.RED_MUSHROOM,             Collection.MUSHROOM);
        colMap.put(Material.NETHER_WART,              Collection.NETHER_WART);
        // Logs → collection (stripped variants count toward the same collection)
        colMap.put(Material.OAK_LOG,                  Collection.OAK_LOG);
        colMap.put(Material.STRIPPED_OAK_LOG,         Collection.OAK_LOG);
        colMap.put(Material.BIRCH_LOG,                Collection.BIRCH_LOG);
        colMap.put(Material.STRIPPED_BIRCH_LOG,       Collection.BIRCH_LOG);
        colMap.put(Material.SPRUCE_LOG,               Collection.SPRUCE_LOG);
        colMap.put(Material.STRIPPED_SPRUCE_LOG,      Collection.SPRUCE_LOG);
        colMap.put(Material.JUNGLE_LOG,               Collection.JUNGLE_LOG);
        colMap.put(Material.STRIPPED_JUNGLE_LOG,      Collection.JUNGLE_LOG);
        colMap.put(Material.ACACIA_LOG,               Collection.ACACIA_LOG);
        colMap.put(Material.STRIPPED_ACACIA_LOG,      Collection.ACACIA_LOG);
        colMap.put(Material.DARK_OAK_LOG,             Collection.DARK_OAK_LOG);
        colMap.put(Material.STRIPPED_DARK_OAK_LOG,    Collection.DARK_OAK_LOG);
        MATERIAL_TO_COLLECTION = Map.copyOf(colMap);
    }

    private final MiningManager miningManager;
    private final FarmingManager farmingManager;
    private final ForagingManager foragingManager;

    /**
     * Creates a listener that dispatches block-break events to the given managers.
     *
     * @param miningManager   the mining manager, must not be null
     * @param farmingManager  the farming manager, must not be null
     * @param foragingManager the foraging manager, must not be null
     * @throws IllegalArgumentException if any argument is null
     */
    public SkyBlockEventListener(MiningManager miningManager,
                                  FarmingManager farmingManager,
                                  ForagingManager foragingManager) {
        if (miningManager == null) {
            throw new IllegalArgumentException("miningManager must not be null");
        }
        if (farmingManager == null) {
            throw new IllegalArgumentException("farmingManager must not be null");
        }
        if (foragingManager == null) {
            throw new IllegalArgumentException("foragingManager must not be null");
        }
        this.miningManager = miningManager;
        this.farmingManager = farmingManager;
        this.foragingManager = foragingManager;
    }

    /**
     * Dispatches a block-break to the appropriate skill manager and grants
     * collection progress for the broken block.
     *
     * @param event the block-break event fired by the server
     */
    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Material material = event.getBlock().getType();
        UUID playerId = player.getUniqueId();

        Integer miningXp = MiningManager.ORE_XP_MAP.get(material);
        if (miningXp != null) {
            miningManager.recordBlockMined(playerId, material.name(), miningXp);
            grantCollection(playerId, material);
            return;
        }

        CropType crop = MATERIAL_TO_CROP.get(material);
        if (crop != null) {
            farmingManager.recordHarvest(playerId, crop, 1);
            grantCollection(playerId, material);
            return;
        }

        Integer foragingXp = ForagingManager.WOOD_XP_MAP.get(material);
        if (foragingXp != null) {
            foragingManager.recordChop(playerId, foragingXp);
            grantCollection(playerId, material);
        }
    }

    private void grantCollection(UUID playerId, Material material) {
        Collection collection = MATERIAL_TO_COLLECTION.get(material);
        if (collection != null) {
            CollectionManager.getInstance().addItems(playerId, collection, 1);
        }
    }
}
