package com.skyblock.core.listener;

import com.skyblock.core.farming.manager.FarmingManager;
import com.skyblock.core.manager.CollectionManager;
import com.skyblock.core.manager.SkillManager;
import com.skyblock.core.model.Skill;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import java.util.Map;
import java.util.UUID;

public final class FarmingListener implements Listener {

    private static final FarmingListener INSTANCE = new FarmingListener();

    private static final Map<Material, FarmingManager.CropType> CROP_MAP = Map.ofEntries(
            Map.entry(Material.WHEAT,          FarmingManager.CropType.WHEAT),
            Map.entry(Material.CARROTS,        FarmingManager.CropType.CARROT),
            Map.entry(Material.POTATOES,       FarmingManager.CropType.POTATO),
            Map.entry(Material.PUMPKIN,        FarmingManager.CropType.PUMPKIN),
            Map.entry(Material.MELON,          FarmingManager.CropType.MELON),
            Map.entry(Material.SUGAR_CANE,     FarmingManager.CropType.SUGAR_CANE),
            Map.entry(Material.COCOA,          FarmingManager.CropType.COCOA_BEANS),
            Map.entry(Material.CACTUS,         FarmingManager.CropType.CACTUS),
            Map.entry(Material.RED_MUSHROOM,   FarmingManager.CropType.MUSHROOM),
            Map.entry(Material.BROWN_MUSHROOM, FarmingManager.CropType.MUSHROOM),
            Map.entry(Material.NETHER_WART,    FarmingManager.CropType.NETHER_WART),
            Map.entry(Material.BEETROOTS,      FarmingManager.CropType.CARROT)
    );

    private final FarmingManager farmingManager = FarmingManager.getInstance();
    private final SkillManager skillManager = SkillManager.getInstance();
    private final CollectionManager collectionManager = CollectionManager.getInstance();

    private FarmingListener() {}

    public static FarmingListener getInstance() {
        return INSTANCE;
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        FarmingManager.CropType crop = CROP_MAP.get(event.getBlock().getType());
        if (crop == null) {
            return;
        }
        UUID uuid = event.getPlayer().getUniqueId();
        farmingManager.recordHarvest(uuid, crop, 1);
        skillManager.addXP(uuid, Skill.FARMING, (long) crop.getBaseXp());
        collectionManager.addCollection(uuid, event.getBlock().getType(), 1);
    }
}
