package com.skyblock.core.listener;

import com.skyblock.core.farming.manager.FarmingManager;
import com.skyblock.core.farming.manager.FarmingManager.CropType;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import java.util.EnumMap;
import java.util.Map;
import java.util.UUID;

public final class FarmingListener implements Listener {

    private static final FarmingListener INSTANCE = new FarmingListener();

    private static final Map<Material, CropType> CROP_MAP = new EnumMap<>(Material.class);

    static {
        CROP_MAP.put(Material.WHEAT,       CropType.WHEAT);
        CROP_MAP.put(Material.CARROTS,     CropType.CARROT);
        CROP_MAP.put(Material.POTATOES,    CropType.POTATO);
        CROP_MAP.put(Material.PUMPKIN,     CropType.PUMPKIN);
        CROP_MAP.put(Material.MELON,       CropType.MELON);
        CROP_MAP.put(Material.SUGAR_CANE,  CropType.SUGAR_CANE);
        CROP_MAP.put(Material.COCOA,       CropType.COCOA_BEANS);
        CROP_MAP.put(Material.CACTUS,      CropType.CACTUS);
        CROP_MAP.put(Material.RED_MUSHROOM,   CropType.MUSHROOM);
        CROP_MAP.put(Material.BROWN_MUSHROOM, CropType.MUSHROOM);
        CROP_MAP.put(Material.NETHER_WART,    CropType.NETHER_WART);
    }

    private final FarmingManager farmingManager = FarmingManager.getInstance();

    private FarmingListener() {}

    public static FarmingListener getInstance() {
        return INSTANCE;
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Block block = event.getBlock();
        CropType crop = CROP_MAP.get(block.getType());
        if (crop == null) {
            return;
        }

        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();
        farmingManager.recordHarvest(uuid, crop, 1);
    }
}
