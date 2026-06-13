package com.skyblock.core.skill;

import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityTameEvent;
import org.bukkit.event.inventory.BrewingStandFuelEvent;
import org.bukkit.event.player.PlayerFishEvent;

import java.util.EnumSet;
import java.util.Set;

/**
 * Bukkit listener that awards {@link SkillManager} XP for in-game actions
 * across all skill types.
 */
public final class SkillListener implements Listener {

    private static final Set<Material> CROP_MATERIALS = EnumSet.of(
            Material.WHEAT, Material.CARROTS, Material.POTATOES,
            Material.PUMPKIN, Material.MELON, Material.SUGAR_CANE,
            Material.COCOA_BEANS, Material.CACTUS,
            Material.BROWN_MUSHROOM, Material.RED_MUSHROOM, Material.NETHER_WART
    );

    private static final Set<Material> LOG_MATERIALS = EnumSet.of(
            Material.OAK_LOG, Material.SPRUCE_LOG, Material.BIRCH_LOG,
            Material.JUNGLE_LOG, Material.ACACIA_LOG, Material.DARK_OAK_LOG,
            Material.CRIMSON_STEM, Material.WARPED_STEM
    );

    private static final Set<Material> ORE_MATERIALS = EnumSet.of(
            Material.COAL_ORE, Material.IRON_ORE, Material.GOLD_ORE,
            Material.LAPIS_ORE, Material.REDSTONE_ORE, Material.DIAMOND_ORE,
            Material.EMERALD_ORE, Material.NETHER_QUARTZ_ORE,
            Material.ANCIENT_DEBRIS, Material.DEEPSLATE_COAL_ORE,
            Material.DEEPSLATE_IRON_ORE, Material.DEEPSLATE_GOLD_ORE,
            Material.DEEPSLATE_LAPIS_ORE, Material.DEEPSLATE_REDSTONE_ORE,
            Material.DEEPSLATE_DIAMOND_ORE, Material.DEEPSLATE_EMERALD_ORE,
            Material.NETHER_GOLD_ORE, Material.GILDED_BLACKSTONE
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
        Player player = event.getPlayer();
        Material type = event.getBlock().getType();
        if (ORE_MATERIALS.contains(type)) {
            skillManager.addXp(player.getUniqueId(), SkillManager.SkillType.MINING, 5L);
        } else if (CROP_MATERIALS.contains(type)) {
            skillManager.addXp(player.getUniqueId(), SkillManager.SkillType.FARMING, 4L);
        } else if (LOG_MATERIALS.contains(type)) {
            skillManager.addXp(player.getUniqueId(), SkillManager.SkillType.FORAGING, 4L);
        }
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        LivingEntity entity = event.getEntity();
        Player killer = entity.getKiller();
        if (killer == null) {
            return;
        }
        skillManager.addXp(killer.getUniqueId(), SkillManager.SkillType.COMBAT, 8L);
    }

    @EventHandler
    public void onPlayerFish(PlayerFishEvent event) {
        if (event.getState() != PlayerFishEvent.State.CAUGHT_FISH) {
            return;
        }
        skillManager.addXp(event.getPlayer().getUniqueId(), SkillManager.SkillType.FISHING, 6L);
    }

    @EventHandler
    public void onEnchantItem(EnchantItemEvent event) {
        skillManager.addXp(event.getEnchanter().getUniqueId(), SkillManager.SkillType.ENCHANTING, 10L);
    }

    @EventHandler
    public void onEntityTame(EntityTameEvent event) {
        Entity owner = event.getOwner();
        if (!(owner instanceof Player)) {
            return;
        }
        skillManager.addXp(owner.getUniqueId(), SkillManager.SkillType.TAMING, 10L);
    }
}
