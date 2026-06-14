package com.skyblock.plugin.skill;

import com.skyblock.plugin.managers.SkillsManager;
import com.skyblock.plugin.profile.ProfileManager;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDeathEvent;

import java.util.Map;

/**
 * Singleton Bukkit listener awarding Mining XP when a player breaks an ore or
 * mineable stone block. Complements {@link SkillXPListener}, which handles the
 * Farming, Foraging and Fishing skills.
 */
public final class SkillManager implements Listener {

    private static final Map<Material, Long> MINING_XP = Map.ofEntries(
            Map.entry(Material.COAL_ORE,            5L),
            Map.entry(Material.DEEPSLATE_COAL_ORE,  5L),
            Map.entry(Material.IRON_ORE,            6L),
            Map.entry(Material.DEEPSLATE_IRON_ORE,  6L),
            Map.entry(Material.GOLD_ORE,            8L),
            Map.entry(Material.DEEPSLATE_GOLD_ORE,  8L),
            Map.entry(Material.REDSTONE_ORE,        7L),
            Map.entry(Material.DEEPSLATE_REDSTONE_ORE, 7L),
            Map.entry(Material.LAPIS_ORE,           8L),
            Map.entry(Material.DEEPSLATE_LAPIS_ORE, 8L),
            Map.entry(Material.DIAMOND_ORE,         16L),
            Map.entry(Material.DEEPSLATE_DIAMOND_ORE, 16L),
            Map.entry(Material.EMERALD_ORE,         20L),
            Map.entry(Material.DEEPSLATE_EMERALD_ORE, 20L),
            Map.entry(Material.NETHER_QUARTZ_ORE,   4L),
            Map.entry(Material.NETHER_GOLD_ORE,     8L),
            Map.entry(Material.STONE,               2L),
            Map.entry(Material.COBBLESTONE,         2L),
            Map.entry(Material.NETHERRACK,          2L),
            Map.entry(Material.END_STONE,           3L)
    );

    private static final Map<EntityType, Long> COMBAT_XP = Map.ofEntries(
            Map.entry(EntityType.ZOMBIE,        4L),
            Map.entry(EntityType.SKELETON,      4L),
            Map.entry(EntityType.SPIDER,        4L),
            Map.entry(EntityType.CAVE_SPIDER,   4L),
            Map.entry(EntityType.CREEPER,       6L),
            Map.entry(EntityType.ENDERMAN,      14L),
            Map.entry(EntityType.WITCH,         14L),
            Map.entry(EntityType.BLAZE,         10L),
            Map.entry(EntityType.SLIME,         3L),
            Map.entry(EntityType.MAGMA_CUBE,    3L)
    );

    private static final SkillManager INSTANCE = new SkillManager();

    private final SkillsManager skillsManager = SkillsManager.getInstance();

    private SkillManager() {}

    public static SkillManager getInstance() {
        return INSTANCE;
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Block block = event.getBlock();
        Long miningXp = MINING_XP.get(block.getType());
        if (miningXp == null) {
            return;
        }
        Player player = event.getPlayer();
        skillsManager.addSkillXP(player.getUniqueId(), "mining", miningXp);
        ProfileManager.getInstance().getOrCreate(player.getUniqueId()).addSkillXp("mining", miningXp);
        sendXpBar(player, "mining", miningXp);
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        LivingEntity entity = event.getEntity();
        Player killer = entity.getKiller();
        if (killer == null) {
            return;
        }
        Long combatXp = COMBAT_XP.get(entity.getType());
        if (combatXp == null) {
            return;
        }
        skillsManager.addSkillXP(killer.getUniqueId(), "combat", combatXp);
        ProfileManager.getInstance().getOrCreate(killer.getUniqueId()).addSkillXp("combat", combatXp);
        sendXpBar(killer, "combat", combatXp);
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
            int pct = forNext <= 0 ? 100 : (int) Math.min(100, Math.floor((double) inLevel / forNext * 100));
            msg = "§a+" + xpGained + " " + displayName + " XP §7(§e" + inLevel + "§7/§e" + forNext + " §6" + pct + "%§7)";
        }
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(msg));
    }
}
