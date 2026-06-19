package com.skyblock.core.listener;

import com.skyblock.core.manager.SkillManager;
import com.skyblock.core.model.Skill;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import java.util.Map;

public final class FarmingListener implements Listener {

    private static final FarmingListener INSTANCE = new FarmingListener();

    private static final Map<Material, Long> CROP_XP = Map.of(
            Material.WHEAT,       3L,
            Material.CARROTS,     3L,
            Material.POTATOES,    3L,
            Material.BEETROOTS,   3L,
            Material.NETHER_WART, 5L,
            Material.MELON,       2L,
            Material.PUMPKIN,     5L,
            Material.COCOA,       3L,
            Material.SUGAR_CANE,  3L
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
        Player player = event.getPlayer();
        int before = skillManager.getLevel(player.getUniqueId(), Skill.FARMING);
        skillManager.addXP(player.getUniqueId(), Skill.FARMING, xp);
        int after = skillManager.getLevel(player.getUniqueId(), Skill.FARMING);
        if (after > before) {
            player.sendTitle("§aSkill Level Up!", "§eFarming §a→ §eLVL " + after, 10, 60, 20);
        }
    }
}
