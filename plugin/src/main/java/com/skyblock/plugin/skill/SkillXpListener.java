package com.skyblock.plugin.skill;

import com.skyblock.plugin.managers.SkillsManager;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDeathEvent;

import java.util.Map;

/**
 * Awards skill XP from block breaking: Farming crops, Mining ores and Foraging logs.
 */
public final class SkillXpListener implements Listener {

    private static final Map<Material, Long> FARMING_XP = Map.ofEntries(
            Map.entry(Material.WHEAT,          4L),
            Map.entry(Material.CARROTS,        4L),
            Map.entry(Material.POTATOES,       4L),
            Map.entry(Material.BEETROOTS,      4L),
            Map.entry(Material.NETHER_WART,    3L),
            Map.entry(Material.PUMPKIN,        6L),
            Map.entry(Material.MELON,          6L),
            Map.entry(Material.SUGAR_CANE,     2L),
            Map.entry(Material.COCOA_BEANS,    3L),
            Map.entry(Material.CACTUS,         2L),
            Map.entry(Material.BROWN_MUSHROOM, 6L),
            Map.entry(Material.RED_MUSHROOM,   6L)
    );

    private static final Map<Material, Long> MINING_XP = Map.ofEntries(
            Map.entry(Material.COBBLESTONE,        1L),
            Map.entry(Material.STONE,              1L),
            Map.entry(Material.COAL_ORE,           3L),
            Map.entry(Material.IRON_ORE,           5L),
            Map.entry(Material.GOLD_ORE,           6L),
            Map.entry(Material.DIAMOND_ORE,        8L),
            Map.entry(Material.LAPIS_ORE,          7L),
            Map.entry(Material.EMERALD_ORE,       10L),
            Map.entry(Material.REDSTONE_ORE,       6L),
            Map.entry(Material.NETHER_QUARTZ_ORE,  5L),
            Map.entry(Material.OBSIDIAN,          12L)
    );

    private static final Map<Material, Long> FORAGING_XP = Map.ofEntries(
            Map.entry(Material.OAK_LOG,      6L),
            Map.entry(Material.BIRCH_LOG,    6L),
            Map.entry(Material.SPRUCE_LOG,   6L),
            Map.entry(Material.JUNGLE_LOG,   6L),
            Map.entry(Material.ACACIA_LOG,   6L),
            Map.entry(Material.DARK_OAK_LOG, 6L)
    );

    private static final Map<EntityType, Long> COMBAT_XP = Map.of(
            EntityType.ZOMBIE,           5L,
            EntityType.SKELETON,         5L,
            EntityType.SPIDER,           5L,
            EntityType.CREEPER,          6L,
            EntityType.ENDERMAN,         8L,
            EntityType.BLAZE,           10L,
            EntityType.WITHER_SKELETON, 15L
    );

    private final SkillsManager skillsManager = SkillsManager.getInstance();

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        Player killer = event.getEntity().getKiller();
        if (killer == null) return;
        Long xp = COMBAT_XP.get(event.getEntity().getType());
        if (xp == null) return;
        skillsManager.addSkillXP(killer.getUniqueId(), "combat", xp);
        sendXpBar(killer, "combat", xp);
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Material type = event.getBlock().getType();
        Player player = event.getPlayer();

        Long farmingXp = FARMING_XP.get(type);
        if (farmingXp != null) {
            skillsManager.addSkillXP(player.getUniqueId(), "farming", farmingXp);
            sendXpBar(player, "farming", farmingXp);
            return;
        }
        Long miningXp = MINING_XP.get(type);
        if (miningXp != null) {
            skillsManager.addSkillXP(player.getUniqueId(), "mining", miningXp);
            sendXpBar(player, "mining", miningXp);
            return;
        }
        Long foragingXp = FORAGING_XP.get(type);
        if (foragingXp != null) {
            skillsManager.addSkillXP(player.getUniqueId(), "foraging", foragingXp);
            sendXpBar(player, "foraging", foragingXp);
        }
    }

    private void sendXpBar(Player player, String skill, long xpGained) {
        long total = skillsManager.getSkillXP(player.getUniqueId(), skill);
        int level = skillsManager.getSkillLevel(player.getUniqueId(), skill);
        long[] table = SkillsManager.SKILL_XP_TABLE.get(skill);
        String displayName = Character.toUpperCase(skill.charAt(0)) + skill.substring(1);
        String msg;
        if (table == null || level >= table.length) {
            msg = "§a+" + xpGained + " " + displayName + " XP §7(§eMAXED§7)";
        } else {
            long cumulative = 0;
            for (int i = 0; i < level; i++) cumulative += table[i];
            long inLevel = total - cumulative;
            long forNext = table[level];
            msg = "§a+" + xpGained + " " + displayName + " XP §7(§e" + inLevel + "§7/§e" + forNext + "§7)";
        }
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(msg));
    }
}
