package com.skyblock.core.listener;

import com.skyblock.core.farming.manager.FarmingManager;
import com.skyblock.core.farming.manager.FarmingManager.CropType;
import com.skyblock.core.manager.CollectionManager;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import java.util.Map;
import java.util.UUID;

public final class FarmingListener implements Listener {

    private static final FarmingListener INSTANCE = new FarmingListener();

    private static final Map<Material, CropType> MATERIAL_TO_CROP = Map.ofEntries(
            Map.entry(Material.WHEAT,        CropType.WHEAT),
            Map.entry(Material.CARROTS,      CropType.CARROT),
            Map.entry(Material.POTATOES,     CropType.POTATO),
            Map.entry(Material.PUMPKIN,      CropType.PUMPKIN),
            Map.entry(Material.MELON,        CropType.MELON),
            Map.entry(Material.SUGAR_CANE,   CropType.SUGAR_CANE),
            Map.entry(Material.COCOA,        CropType.COCOA_BEANS),
            Map.entry(Material.CACTUS,       CropType.CACTUS),
            Map.entry(Material.BROWN_MUSHROOM, CropType.MUSHROOM),
            Map.entry(Material.RED_MUSHROOM,   CropType.MUSHROOM),
            Map.entry(Material.NETHER_WART,    CropType.NETHER_WART)
    );

    private final FarmingManager farmingManager      = FarmingManager.getInstance();
    private final CollectionManager collectionManager = CollectionManager.getInstance();

    private FarmingListener() {}

    public static FarmingListener getInstance() {
        return INSTANCE;
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (event.isCancelled()) return;
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();
        Material type = event.getBlock().getType();

        CropType crop = MATERIAL_TO_CROP.get(type);
        if (crop == null) return;

        int before = farmingManager.getLevel(uuid);
        farmingManager.recordHarvest(uuid, crop, 1);
        int after = farmingManager.getLevel(uuid);
        if (after > before) {
            player.sendTitle("§aSkill Level Up!", "§eFarming §a→ §eLVL " + after, 10, 60, 20);
        }
        collectionManager.addCollection(uuid, type, 1);
    }
}
