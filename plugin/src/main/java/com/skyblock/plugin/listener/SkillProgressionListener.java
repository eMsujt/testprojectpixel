package com.skyblock.plugin.listener;

import com.skyblock.plugin.skills.SkillManager;
import com.skyblock.plugin.skills.SkillManager.SkillType;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.Ageable;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import java.util.Map;
import java.util.UUID;

/**
 * Awards skill XP through {@link SkillManager} whenever a player breaks a block,
 * mapping the broken {@link Material} to its governing {@link SkillType}
 * (Farming, Mining or Foraging) and firing level-up rewards when the player's
 * level increases.
 *
 * <p>Farming crops only count once mature: {@link Ageable} crops must have reached
 * their maximum age, while non-ageable produce (pumpkins, melons, sugar cane, …)
 * always counts.</p>
 */
public final class SkillProgressionListener implements Listener {

    /** Farming XP per crop block, keyed by {@link Material}. */
    private static final Map<Material, Long> FARMING_XP = Map.ofEntries(
            Map.entry(Material.WHEAT,                3L),
            Map.entry(Material.POTATOES,             3L),
            Map.entry(Material.CARROTS,              3L),
            Map.entry(Material.BEETROOTS,            3L),
            Map.entry(Material.NETHER_WART,          5L),
            Map.entry(Material.PUMPKIN,              8L),
            Map.entry(Material.MELON,                2L),
            Map.entry(Material.SUGAR_CANE,           2L),
            Map.entry(Material.CACTUS,               2L),
            Map.entry(Material.COCOA,                3L),
            Map.entry(Material.MUSHROOM_STEM,        3L),
            Map.entry(Material.RED_MUSHROOM_BLOCK,   3L),
            Map.entry(Material.BROWN_MUSHROOM_BLOCK, 3L)
    );

    /** Mining XP per ore/stone block, keyed by {@link Material}. */
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

    /** Foraging XP per log broken, keyed by {@link Material}. */
    private static final Map<Material, Long> FORAGING_XP = Map.ofEntries(
            Map.entry(Material.OAK_LOG,      6L),
            Map.entry(Material.BIRCH_LOG,    6L),
            Map.entry(Material.SPRUCE_LOG,   6L),
            Map.entry(Material.JUNGLE_LOG,   6L),
            Map.entry(Material.ACACIA_LOG,   6L),
            Map.entry(Material.DARK_OAK_LOG, 6L),
            Map.entry(Material.MANGROVE_LOG, 6L),
            Map.entry(Material.CHERRY_LOG,   6L)
    );

    private final SkillManager skillManager = SkillManager.getInstance();

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Block block = event.getBlock();
        Material type = block.getType();

        Long farming = FARMING_XP.get(type);
        if (farming != null) {
            if (isMature(block)) {
                grantXP(event.getPlayer(), SkillType.FARMING, farming);
            }
            return;
        }

        Long mining = MINING_XP.get(type);
        if (mining != null) {
            grantXP(event.getPlayer(), SkillType.MINING, mining);
            return;
        }

        Long foraging = FORAGING_XP.get(type);
        if (foraging != null) {
            grantXP(event.getPlayer(), SkillType.FORAGING, foraging);
        }
    }

    /**
     * Returns whether the crop block has finished growing. {@link Ageable} crops
     * are mature only at their maximum age; all other produce is always mature.
     */
    private static boolean isMature(Block block) {
        if (block.getBlockData() instanceof Ageable ageable) {
            return ageable.getAge() >= ageable.getMaximumAge();
        }
        return true;
    }

    private void grantXP(Player player, SkillType skill, long amount) {
        UUID id = player.getUniqueId();
        int before = skillManager.getLevel(id, skill);
        skillManager.addXP(id, skill, amount);
        int after = skillManager.getLevel(id, skill);
        if (after > before) {
            skillManager.grantLevelUpRewards(id, skill, before, after);
            String name = skill.name().charAt(0) + skill.name().substring(1).toLowerCase();
            player.sendTitle("§aSkill Level Up!", "§e" + name + " §a→ §eLVL " + after, 10, 60, 20);
        }
    }
}
