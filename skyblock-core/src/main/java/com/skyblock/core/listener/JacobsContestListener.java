package com.skyblock.core.listener;

import com.skyblock.core.manager.JacobsContestManager;
import com.skyblock.core.manager.JacobsContestManager.CropType;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import java.util.EnumMap;
import java.util.Map;
import java.util.UUID;

public final class JacobsContestListener implements Listener {

    private static final JacobsContestListener INSTANCE = new JacobsContestListener();

    private static final Map<Material, CropType> CROP_MAP = new EnumMap<>(Material.class);

    static {
        CROP_MAP.put(Material.WHEAT,          CropType.WHEAT);
        CROP_MAP.put(Material.CARROTS,        CropType.CARROT);
        CROP_MAP.put(Material.POTATOES,       CropType.POTATO);
        CROP_MAP.put(Material.MELON,          CropType.MELON);
        CROP_MAP.put(Material.PUMPKIN,        CropType.PUMPKIN);
        CROP_MAP.put(Material.SUGAR_CANE,     CropType.SUGAR_CANE);
        CROP_MAP.put(Material.COCOA,          CropType.COCOA_BEANS);
        CROP_MAP.put(Material.CACTUS,         CropType.CACTUS);
        CROP_MAP.put(Material.RED_MUSHROOM,   CropType.MUSHROOM);
        CROP_MAP.put(Material.BROWN_MUSHROOM, CropType.MUSHROOM);
        CROP_MAP.put(Material.NETHER_WART,    CropType.NETHER_WART);
    }

    private final JacobsContestManager contestManager = JacobsContestManager.getInstance();

    private JacobsContestListener() {}

    public static JacobsContestListener getInstance() {
        return INSTANCE;
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (event.isCancelled()) return;
        if (!contestManager.isContestActive()) return;

        CropType crop = CROP_MAP.get(event.getBlock().getType());
        if (crop == null || !contestManager.getActiveCrops().contains(crop)) return;

        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();
        contestManager.addScore(uuid, crop, 1L);

        long score = contestManager.getActiveScore(uuid, crop);
        player.sendMessage("§6[Jacob's Contest] §f" + crop.getDisplayName()
                + ": §e" + score + " §f(" + contestManager.getActiveMedal(uuid, crop).getDisplayName() + ")");
    }
}
