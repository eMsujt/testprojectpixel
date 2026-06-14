package com.skyblock.plugin.skills;

import com.skyblock.plugin.managers.SkillsManager;
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
        long xp = Math.max(1L, Math.round(event.getEntity().getMaxHealth()));
        skillsManager.addSkillXP(killer.getUniqueId(), "combat", xp);
    }

    @EventHandler
    public void onPlayerFish(PlayerFishEvent event) {
        if (event.getState() != PlayerFishEvent.State.CAUGHT_FISH) {
            return;
        }
        skillsManager.addSkillXP(event.getPlayer().getUniqueId(), "fishing", 5L);
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
        skillsManager.addSkillXP(event.getEnchanter().getUniqueId(), "enchanting", xp);
    }
}
