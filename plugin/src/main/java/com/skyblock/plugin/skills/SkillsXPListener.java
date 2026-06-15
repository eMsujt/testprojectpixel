package com.skyblock.plugin.skills;

import com.skyblock.core.manager.SkillManager;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerFishEvent;

import java.util.Map;

/**
 * Single Bukkit listener covering every skill XP source: farming and foraging
 * and mining via {@link BlockBreakEvent}, combat via {@link EntityDeathEvent},
 * fishing via {@link PlayerFishEvent}, and enchanting via {@link EnchantItemEvent}.
 */
public final class SkillsXPListener implements Listener {

    private static final Map<Material, Long> FARMING_XP = Map.ofEntries(
            Map.entry(Material.WHEAT,          6L),
            Map.entry(Material.CARROTS,        3L),
            Map.entry(Material.POTATOES,       3L),
            Map.entry(Material.PUMPKIN,        4L),
            Map.entry(Material.MELON,          4L),
            Map.entry(Material.SUGAR_CANE,     2L),
            Map.entry(Material.COCOA_BEANS,    3L),
            Map.entry(Material.CACTUS,         2L),
            Map.entry(Material.BROWN_MUSHROOM, 6L),
            Map.entry(Material.RED_MUSHROOM,   6L),
            Map.entry(Material.NETHER_WART,    3L)
    );

    private static final Map<Material, Long> MINING_XP = Map.ofEntries(
            Map.entry(Material.COBBLESTONE,   1L),
            Map.entry(Material.STONE,         1L),
            Map.entry(Material.COAL_ORE,      3L),
            Map.entry(Material.IRON_ORE,      5L),
            Map.entry(Material.GOLD_ORE,      6L),
            Map.entry(Material.DIAMOND_ORE,   8L),
            Map.entry(Material.LAPIS_ORE,     7L),
            Map.entry(Material.EMERALD_ORE,  10L),
            Map.entry(Material.REDSTONE_ORE,  6L),
            Map.entry(Material.NETHER_QUARTZ_ORE, 5L),
            Map.entry(Material.OBSIDIAN,     12L)
    );

    private static final Map<Material, Long> FORAGING_XP = Map.ofEntries(
            Map.entry(Material.OAK_LOG,      6L),
            Map.entry(Material.BIRCH_LOG,    6L),
            Map.entry(Material.SPRUCE_LOG,   6L),
            Map.entry(Material.JUNGLE_LOG,   6L),
            Map.entry(Material.ACACIA_LOG,   6L),
            Map.entry(Material.DARK_OAK_LOG, 6L)
    );

    private final SkillManager skillsManager = SkillManager.getInstance();

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Material type = event.getBlock().getType();
        Player player = event.getPlayer();

        Long farmingXp = FARMING_XP.get(type);
        if (farmingXp != null) {
            grantXp(player, "farming", farmingXp);
            return;
        }
        Long miningXp = MINING_XP.get(type);
        if (miningXp != null) {
            grantXp(player, "mining", miningXp);
            return;
        }
        Long foragingXp = FORAGING_XP.get(type);
        if (foragingXp != null) {
            grantXp(player, "foraging", foragingXp);
        }
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        Player killer = event.getEntity().getKiller();
        if (killer == null) {
            return;
        }
        long xp = Math.max(1L, Math.round(event.getEntity().getMaxHealth()));
        grantXp(killer, "combat", xp);
    }

    @EventHandler
    public void onPlayerFish(PlayerFishEvent event) {
        if (event.getState() != PlayerFishEvent.State.CAUGHT_FISH) {
            return;
        }
        grantXp(event.getPlayer(), "fishing", 5L);
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
        grantXp(event.getEnchanter(), "enchanting", xp);
    }

    private void grantXp(Player player, String skill, long amount) {
        int before = skillsManager.getSkillLevel(player.getUniqueId(), skill);
        skillsManager.addSkillXP(player.getUniqueId(), skill, amount);
        int after = skillsManager.getSkillLevel(player.getUniqueId(), skill);
        for (int lvl = before + 1; lvl <= after; lvl++) {
            String name = Character.toUpperCase(skill.charAt(0)) + skill.substring(1);
            player.sendTitle("§aSkill Level Up!", "§e" + name + " §a→ §eLVL " + lvl, 10, 60, 20);
            applyLevelBonus(player, skill);
        }
    }

    private static void applyLevelBonus(Player player, String skill) {
        switch (skill) {
            case "farming" -> adjustAttribute(player, Attribute.GENERIC_MAX_HEALTH, 2.0);
            case "combat"  -> adjustAttribute(player, Attribute.GENERIC_MAX_HEALTH, 4.0);
            case "fishing" -> adjustAttribute(player, Attribute.GENERIC_MAX_HEALTH, 4.0);
            case "foraging" -> adjustAttribute(player, Attribute.GENERIC_ATTACK_DAMAGE, 2.0);
        }
    }

    private static void adjustAttribute(Player player, Attribute attribute, double amount) {
        AttributeInstance inst = player.getAttribute(attribute);
        if (inst != null) {
            inst.setBaseValue(inst.getBaseValue() + amount);
        }
    }
}
