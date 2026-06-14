package com.skyblock.plugin.skills;

import com.skyblock.plugin.managers.SkillsManager;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerFishEvent;

import java.util.Map;
import java.util.UUID;

public final class SkillXpListener implements Listener {

    private static final Map<Material, Long> FARMING_XP = Map.ofEntries(
            Map.entry(Material.WHEAT,          3L),
            Map.entry(Material.CARROTS,        3L),
            Map.entry(Material.POTATOES,       3L),
            Map.entry(Material.PUMPKIN,        12L),
            Map.entry(Material.MELON,          3L),
            Map.entry(Material.SUGAR_CANE,     2L),
            Map.entry(Material.COCOA_BEANS,    3L),
            Map.entry(Material.CACTUS,         2L),
            Map.entry(Material.BROWN_MUSHROOM, 6L),
            Map.entry(Material.RED_MUSHROOM,   6L),
            Map.entry(Material.NETHER_WART,    3L)
    );

    private static final Map<Material, Long> MINING_XP = Map.ofEntries(
            Map.entry(Material.STONE,              1L),
            Map.entry(Material.COBBLESTONE,        1L),
            Map.entry(Material.COAL_ORE,           5L),
            Map.entry(Material.IRON_ORE,           7L),
            Map.entry(Material.GOLD_ORE,           9L),
            Map.entry(Material.REDSTONE_ORE,       5L),
            Map.entry(Material.LAPIS_ORE,         12L),
            Map.entry(Material.DIAMOND_ORE,       22L),
            Map.entry(Material.EMERALD_ORE,       20L),
            Map.entry(Material.NETHER_QUARTZ_ORE,  4L),
            Map.entry(Material.OBSIDIAN,          12L)
    );

    private static final Map<Material, Long> FORAGING_XP = Map.ofEntries(
            Map.entry(Material.OAK_LOG,      6L),
            Map.entry(Material.SPRUCE_LOG,   6L),
            Map.entry(Material.BIRCH_LOG,    6L),
            Map.entry(Material.JUNGLE_LOG,   6L),
            Map.entry(Material.ACACIA_LOG,   6L),
            Map.entry(Material.DARK_OAK_LOG, 6L)
    );

    private static final Map<EntityType, Long> MOB_XP = Map.ofEntries(
            Map.entry(EntityType.ZOMBIE,    4L),
            Map.entry(EntityType.SKELETON,  4L),
            Map.entry(EntityType.SPIDER,    5L),
            Map.entry(EntityType.CREEPER,   6L),
            Map.entry(EntityType.ENDERMAN,  8L),
            Map.entry(EntityType.BLAZE,     8L)
    );

    private final SkillsManager skillsManager = SkillsManager.getInstance();

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Material type = event.getBlock().getType();
        UUID uuid = event.getPlayer().getUniqueId();

        Long farmingXp = FARMING_XP.get(type);
        if (farmingXp != null) {
            skillsManager.addSkillXP(uuid, "farming", farmingXp);
            return;
        }
        Long miningXp = MINING_XP.get(type);
        if (miningXp != null) {
            skillsManager.addSkillXP(uuid, "mining", miningXp);
            return;
        }
        Long foragingXp = FORAGING_XP.get(type);
        if (foragingXp != null) {
            skillsManager.addSkillXP(uuid, "foraging", foragingXp);
        }
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        Player killer = event.getEntity().getKiller();
        if (killer == null) {
            return;
        }
        Long mobXp = MOB_XP.get(event.getEntityType());
        if (mobXp != null) {
            skillsManager.addSkillXP(killer.getUniqueId(), "combat", mobXp);
        }
    }

    @EventHandler
    public void onPlayerFish(PlayerFishEvent event) {
        if (event.getState() != PlayerFishEvent.State.CAUGHT_FISH) {
            return;
        }
        skillsManager.addSkillXP(event.getPlayer().getUniqueId(), "fishing", 1L);
    }

    @EventHandler
    public void onEnchantItem(EnchantItemEvent event) {
        int levelSum = 0;
        for (int level : event.getEnchantsToAdd().values()) {
            levelSum += level;
        }
        long xp = Math.min(levelSum * 3L, 30L);
        if (xp > 0) {
            skillsManager.addSkillXP(event.getEnchanter().getUniqueId(), "enchanting", xp);
        }
    }
}
