package com.skyblock.core.listener;

import com.skyblock.core.manager.SkillManager;
import com.skyblock.core.model.Skill;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import java.util.Map;

public final class FarmingListener implements Listener {

    private static final FarmingListener INSTANCE = new FarmingListener();

    private static final Map<Material, Long> CROP_XP = Map.of(
            Material.WHEAT,       4L,
            Material.CARROTS,     4L,
            Material.POTATOES,    4L,
            Material.BEETROOTS,   4L,
            Material.NETHER_WART, 4L,
            Material.MELON,       4L,
            Material.PUMPKIN,     4L,
            Material.COCOA,       4L,
            Material.SUGAR_CANE,  4L
    );

    private final SkillManager skillManager = SkillManager.getInstance();

    private FarmingListener() {}

    public static FarmingListener getInstance() {
        return INSTANCE;
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Long xp = CROP_XP.get(event.getBlock().getType());
        if (xp == null) {
            return;
        }
        skillManager.addXP(event.getPlayer().getUniqueId(), Skill.FARMING, xp);
    }
}
