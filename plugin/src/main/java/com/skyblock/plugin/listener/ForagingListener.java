package com.skyblock.plugin.listener;

import com.skyblock.plugin.skills.SkillManager;
import com.skyblock.plugin.skills.SkillManager.SkillType;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import java.util.Map;
import java.util.UUID;

/**
 * Awards Foraging XP through {@link SkillManager} whenever a player chops any
 * log type and fires level-up rewards when the player's level increases.
 */
public final class ForagingListener implements Listener {

    /** Foraging XP granted per log broken, keyed by log {@link Material}. */
    private static final Map<Material, Long> LOG_XP = Map.ofEntries(
            Map.entry(Material.OAK_LOG,       6L),
            Map.entry(Material.BIRCH_LOG,     6L),
            Map.entry(Material.SPRUCE_LOG,    6L),
            Map.entry(Material.JUNGLE_LOG,    6L),
            Map.entry(Material.ACACIA_LOG,    6L),
            Map.entry(Material.DARK_OAK_LOG,  6L),
            Map.entry(Material.MANGROVE_LOG,  6L),
            Map.entry(Material.CHERRY_LOG,    6L)
    );

    private final SkillManager skillManager = SkillManager.getInstance();

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Long xp = LOG_XP.get(event.getBlock().getType());
        if (xp == null) {
            return;
        }
        grantXP(event.getPlayer(), xp);
    }

    private void grantXP(Player player, long amount) {
        UUID id = player.getUniqueId();
        int before = skillManager.getLevel(id, SkillType.FORAGING);
        skillManager.addXP(id, SkillType.FORAGING, amount);
        int after = skillManager.getLevel(id, SkillType.FORAGING);
        if (after > before) {
            skillManager.grantLevelUpRewards(id, SkillType.FORAGING, before, after);
            player.sendTitle("§aSkill Level Up!", "§eForaging §a→ §eLVL " + after, 10, 60, 20);
        }
    }
}
