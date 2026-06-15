package com.skyblock.plugin.listener;

import com.skyblock.core.manager.SkillManager;
import com.skyblock.core.skills.SkillManager.SkillType;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerFishEvent;

import java.util.Map;
import java.util.UUID;

/**
 * Awards skill XP through {@link SkillManager} on real in-game actions and
 * fires level-up rewards when the player's level increases.
 */
public final class SkillListener implements Listener {

    private static final Map<Material, SkillType> BLOCK_SKILL = Map.ofEntries(
            Map.entry(Material.WHEAT,              SkillType.FARMING),
            Map.entry(Material.CARROTS,            SkillType.FARMING),
            Map.entry(Material.POTATOES,           SkillType.FARMING),
            Map.entry(Material.BEETROOTS,          SkillType.FARMING),
            Map.entry(Material.PUMPKIN,            SkillType.FARMING),
            Map.entry(Material.MELON,              SkillType.FARMING),
            Map.entry(Material.SUGAR_CANE,         SkillType.FARMING),
            Map.entry(Material.NETHER_WART,        SkillType.FARMING),
            Map.entry(Material.COCOA_BEANS,        SkillType.FARMING),
            Map.entry(Material.CACTUS,             SkillType.FARMING),
            Map.entry(Material.BROWN_MUSHROOM,     SkillType.FARMING),
            Map.entry(Material.RED_MUSHROOM,       SkillType.FARMING),
            Map.entry(Material.COAL_ORE,           SkillType.MINING),
            Map.entry(Material.IRON_ORE,           SkillType.MINING),
            Map.entry(Material.GOLD_ORE,           SkillType.MINING),
            Map.entry(Material.DIAMOND_ORE,        SkillType.MINING),
            Map.entry(Material.EMERALD_ORE,        SkillType.MINING),
            Map.entry(Material.LAPIS_ORE,          SkillType.MINING),
            Map.entry(Material.REDSTONE_ORE,       SkillType.MINING),
            Map.entry(Material.NETHER_QUARTZ_ORE,  SkillType.MINING),
            Map.entry(Material.OBSIDIAN,           SkillType.MINING),
            Map.entry(Material.STONE,              SkillType.MINING),
            Map.entry(Material.COBBLESTONE,        SkillType.MINING),
            Map.entry(Material.OAK_LOG,            SkillType.FORAGING),
            Map.entry(Material.BIRCH_LOG,          SkillType.FORAGING),
            Map.entry(Material.SPRUCE_LOG,         SkillType.FORAGING),
            Map.entry(Material.JUNGLE_LOG,         SkillType.FORAGING),
            Map.entry(Material.ACACIA_LOG,         SkillType.FORAGING),
            Map.entry(Material.DARK_OAK_LOG,       SkillType.FORAGING)
    );

    private static final Map<Material, Long> BLOCK_XP = Map.ofEntries(
            Map.entry(Material.WHEAT,              4L),
            Map.entry(Material.CARROTS,            4L),
            Map.entry(Material.POTATOES,           4L),
            Map.entry(Material.BEETROOTS,          4L),
            Map.entry(Material.PUMPKIN,            6L),
            Map.entry(Material.MELON,              6L),
            Map.entry(Material.SUGAR_CANE,         2L),
            Map.entry(Material.NETHER_WART,        3L),
            Map.entry(Material.COCOA_BEANS,        3L),
            Map.entry(Material.CACTUS,             2L),
            Map.entry(Material.BROWN_MUSHROOM,     6L),
            Map.entry(Material.RED_MUSHROOM,       6L),
            Map.entry(Material.COAL_ORE,           3L),
            Map.entry(Material.IRON_ORE,           5L),
            Map.entry(Material.GOLD_ORE,           6L),
            Map.entry(Material.DIAMOND_ORE,        8L),
            Map.entry(Material.EMERALD_ORE,       10L),
            Map.entry(Material.LAPIS_ORE,          7L),
            Map.entry(Material.REDSTONE_ORE,       6L),
            Map.entry(Material.NETHER_QUARTZ_ORE,  5L),
            Map.entry(Material.OBSIDIAN,          12L),
            Map.entry(Material.STONE,              1L),
            Map.entry(Material.COBBLESTONE,        1L),
            Map.entry(Material.OAK_LOG,            6L),
            Map.entry(Material.BIRCH_LOG,          6L),
            Map.entry(Material.SPRUCE_LOG,         6L),
            Map.entry(Material.JUNGLE_LOG,         6L),
            Map.entry(Material.ACACIA_LOG,         6L),
            Map.entry(Material.DARK_OAK_LOG,       6L)
    );

    private final SkillManager skillManager = SkillManager.getInstance();

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Material type = event.getBlock().getType();
        SkillType skill = BLOCK_SKILL.get(type);
        if (skill == null) {
            return;
        }
        long xp = BLOCK_XP.getOrDefault(type, 1L);
        grantXP(event.getPlayer(), skill, xp);
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        Player killer = event.getEntity().getKiller();
        if (killer == null) {
            return;
        }
        long xp = Math.max(1L, Math.round(event.getEntity().getMaxHealth()));
        grantXP(killer, SkillType.COMBAT, xp);
    }

    @EventHandler
    public void onPlayerFish(PlayerFishEvent event) {
        if (event.getState() != PlayerFishEvent.State.CAUGHT_FISH) {
            return;
        }
        grantXP(event.getPlayer(), SkillType.FISHING, 5L);
    }

    @EventHandler
    public void onEnchantItem(EnchantItemEvent event) {
        int xp = 0;
        for (int level : event.getEnchantsToAdd().values()) {
            xp += level;
        }
        if (xp <= 0) {
            return;
        }
        grantXP(event.getEnchanter(), SkillType.ENCHANTING, xp);
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
