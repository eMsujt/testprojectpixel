package com.skyblock.core.skill;

import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerFishEvent;

import java.util.EnumSet;
import java.util.Set;

/**
 * Bukkit listener that awards {@link SkillManager} XP for in-game actions.
 *
 * <p>Skill XP mapping:
 * <ul>
 *   <li>MINING  — ore/stone block breaks</li>
 *   <li>FARMING — crop block breaks</li>
 *   <li>FORAGING — log block breaks</li>
 *   <li>COMBAT  — player deals damage to a non-player entity</li>
 *   <li>FISHING — successful fish catch</li>
 * </ul>
 * </p>
 */
public final class SkillListener implements Listener {

    private static final long XP_MINING   = 5L;
    private static final long XP_FARMING  = 3L;
    private static final long XP_FORAGING = 4L;
    private static final long XP_COMBAT   = 6L;
    private static final long XP_FISHING  = 8L;

    private static final Set<Material> ORE_BLOCKS = EnumSet.of(
            Material.COAL_ORE, Material.DEEPSLATE_COAL_ORE,
            Material.IRON_ORE, Material.DEEPSLATE_IRON_ORE,
            Material.GOLD_ORE, Material.DEEPSLATE_GOLD_ORE,
            Material.DIAMOND_ORE, Material.DEEPSLATE_DIAMOND_ORE,
            Material.EMERALD_ORE, Material.DEEPSLATE_EMERALD_ORE,
            Material.LAPIS_ORE, Material.DEEPSLATE_LAPIS_ORE,
            Material.REDSTONE_ORE, Material.DEEPSLATE_REDSTONE_ORE,
            Material.NETHER_QUARTZ_ORE, Material.NETHER_GOLD_ORE,
            Material.ANCIENT_DEBRIS, Material.COBBLESTONE, Material.STONE
    );

    private static final Set<Material> CROP_BLOCKS = EnumSet.of(
            Material.WHEAT, Material.CARROTS, Material.POTATOES,
            Material.PUMPKIN, Material.MELON, Material.SUGAR_CANE,
            Material.COCOA_BEANS, Material.CACTUS,
            Material.BROWN_MUSHROOM, Material.RED_MUSHROOM, Material.NETHER_WART
    );

    private static final Set<Material> LOG_BLOCKS = EnumSet.of(
            Material.OAK_LOG, Material.SPRUCE_LOG, Material.BIRCH_LOG,
            Material.JUNGLE_LOG, Material.ACACIA_LOG, Material.DARK_OAK_LOG,
            Material.MANGROVE_LOG, Material.CHERRY_LOG
    );

    private final SkillManager skillManager;

    public SkillListener(SkillManager skillManager) {
        if (skillManager == null) {
            throw new IllegalArgumentException("skillManager must not be null");
        }
        this.skillManager = skillManager;
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Material mat = event.getBlock().getType();
        Player player = event.getPlayer();
        if (ORE_BLOCKS.contains(mat)) {
            skillManager.addXp(player.getUniqueId(), SkillManager.SkillType.MINING, XP_MINING);
        } else if (CROP_BLOCKS.contains(mat)) {
            skillManager.addXp(player.getUniqueId(), SkillManager.SkillType.FARMING, XP_FARMING);
        } else if (LOG_BLOCKS.contains(mat)) {
            skillManager.addXp(player.getUniqueId(), SkillManager.SkillType.FORAGING, XP_FORAGING);
        }
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        Entity attacker = event.getDamager();
        Entity target = event.getEntity();
        if (attacker instanceof Player && !(target instanceof Player)) {
            skillManager.addXp(attacker.getUniqueId(), SkillManager.SkillType.COMBAT, XP_COMBAT);
        }
    }

    @EventHandler
    public void onPlayerFish(PlayerFishEvent event) {
        if (event.getState() != PlayerFishEvent.State.CAUGHT_FISH) {
            return;
        }
        if (!(event.getCaught() instanceof Item)) {
            return;
        }
        skillManager.addXp(event.getPlayer().getUniqueId(), SkillManager.SkillType.FISHING, XP_FISHING);
    }
}
