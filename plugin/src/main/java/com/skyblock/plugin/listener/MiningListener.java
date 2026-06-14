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
 * Awards Mining XP through {@link SkillManager} whenever a player mines any ore
 * or stone type and fires level-up rewards when the player's level increases.
 */
public final class MiningListener implements Listener {

    /** Mining XP granted per block broken, keyed by ore/stone {@link Material}. */
    private static final Map<Material, Long> MINING_XP = Map.ofEntries(
            Map.entry(Material.STONE,             1L),
            Map.entry(Material.COBBLESTONE,       1L),
            Map.entry(Material.COAL_ORE,          5L),
            Map.entry(Material.IRON_ORE,          5L),
            Map.entry(Material.GOLD_ORE,         10L),
            Map.entry(Material.DIAMOND_ORE,      30L),
            Map.entry(Material.EMERALD_ORE,      30L),
            Map.entry(Material.LAPIS_ORE,        25L),
            Map.entry(Material.REDSTONE_ORE,      7L),
            Map.entry(Material.NETHER_QUARTZ_ORE, 10L)
    );

    private final SkillManager skillManager = SkillManager.getInstance();

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Long xp = MINING_XP.get(event.getBlock().getType());
        if (xp == null) {
            return;
        }
        grantXP(event.getPlayer(), xp);
    }

    private void grantXP(Player player, long amount) {
        UUID id = player.getUniqueId();
        int before = skillManager.getLevel(id, SkillType.MINING);
        skillManager.addXP(id, SkillType.MINING, amount);
        int after = skillManager.getLevel(id, SkillType.MINING);
        if (after > before) {
            skillManager.grantLevelUpRewards(id, SkillType.MINING, before, after);
            player.sendTitle("§aSkill Level Up!", "§eMining §a→ §eLVL " + after, 10, 60, 20);
        }
    }
}
