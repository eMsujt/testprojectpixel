package com.skyblock.core.listener;

import com.skyblock.core.SkyBlockCore;
import com.skyblock.core.combat.calculator.CombatEngine;
import com.skyblock.core.manager.GardenManager;
import com.skyblock.core.manager.StatManager;
import com.skyblock.core.model.Stat;
import com.skyblock.core.stats.CombatStatsManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;

import java.util.Map;

/**
 * Applies the full SkyBlock damage formula to melee combat and records combat
 * statistics.
 *
 * <p>When a player deals melee damage, the event damage is recomputed from the
 * attacker's Strength, Crit Chance, and Crit Damage stats via {@link CombatEngine}.
 * When a player takes damage, it is reduced by the SkyBlock defense formula
 * ({@code damage × 100 / (100 + defense)}).</p>
 *
 * <p>Damage dealt/taken and kills/deaths are also recorded into
 * {@link CombatStatsManager}.</p>
 *
 * <p>This consolidated listener additionally feeds harvested Garden crops into
 * the player's composter as organic matter (formerly {@code CompostListener}),
 * tracked through the canonical composter state on {@link GardenManager}. Fuel
 * and the actual compost-processing step are handled separately (via
 * {@code /compost}); this listener only accumulates organic matter, so it does
 * not duplicate the Farming-XP handling done by the consolidated skill listener.</p>
 */
public final class CombatListener implements Listener {

    private static final CombatListener INSTANCE = new CombatListener();

    /** Organic matter contributed to the composter per harvested crop block. */
    private static final Map<Material, Long> ORGANIC_MATTER = Map.of(
            Material.WHEAT,       2L,
            Material.CARROTS,     2L,
            Material.POTATOES,    2L,
            Material.BEETROOTS,   2L,
            Material.NETHER_WART, 3L,
            Material.MELON,       1L,
            Material.PUMPKIN,     3L,
            Material.COCOA,       2L,
            Material.SUGAR_CANE,  2L
    );

    private CombatListener() {}

    public static CombatListener getInstance() {
        return INSTANCE;
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        StatManager stats = StatManager.getInstance();
        boolean isCrit = false;

        if (event.getDamager() instanceof Player) {
            Player attacker = (Player) event.getDamager();
            double strength = stats.getStat(attacker.getUniqueId(), Stat.STRENGTH);
            double critChance = stats.getStat(attacker.getUniqueId(), Stat.CRIT_CHANCE);
            double critDamage = stats.getStat(attacker.getUniqueId(), Stat.CRIT_DAMAGE);
            isCrit = CombatEngine.rollCrit(critChance);
            double damage = CombatEngine.applyStrength(event.getDamage(), strength);
            if (isCrit) {
                damage = CombatEngine.applyCrit(damage, critDamage);
            }
            event.setDamage(Math.max(0.0, damage));
        }

        if (event.getEntity() instanceof Player) {
            Player defender = (Player) event.getEntity();
            double defense = stats.getStat(defender.getUniqueId(), Stat.DEFENSE);
            double reduced = event.getDamage() * 100.0 / (100.0 + Math.max(0.0, defense));
            event.setDamage(reduced);
        }

        CombatStatsManager combatStats = CombatStatsManager.getInstance();
        double finalDamage = event.getFinalDamage();
        if (event.getDamager() instanceof Player) {
            combatStats.addDamageDealt(((Player) event.getDamager()).getUniqueId(), finalDamage);
        }
        if (event.getEntity() instanceof Player) {
            combatStats.addDamageTaken(((Player) event.getEntity()).getUniqueId(), finalDamage);
        }

        spawnDamageIndicator(event.getEntity().getLocation(), finalDamage, isCrit);
    }

    private void spawnDamageIndicator(Location location, double damage, boolean isCrit) {
        Location display = location.clone().add(0, 1.5, 0);
        ArmorStand stand = (ArmorStand) location.getWorld().spawnEntity(display, EntityType.ARMOR_STAND);
        String label = isCrit
                ? "§c§l✧ " + (long) damage
                : "§f" + (long) damage;
        stand.setCustomName(label);
        stand.setCustomNameVisible(true);
        stand.setGravity(false);
        stand.setVisible(false);
        stand.setInvulnerable(true);
        Bukkit.getScheduler().runTaskLater(SkyBlockCore.getInstance(), stand::remove, 30L);
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        CombatStatsManager combatStats = CombatStatsManager.getInstance();
        Player victim = event.getEntity();
        combatStats.recordDeath(victim.getUniqueId());

        Player killer = victim.getKiller();
        if (killer != null) {
            combatStats.recordKill(killer.getUniqueId());
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Long matter = ORGANIC_MATTER.get(event.getBlock().getType());
        if (matter == null) {
            return;
        }
        Player player = event.getPlayer();
        GardenManager.getInstance().addComposterOrganicMatter(player.getUniqueId(), matter);
    }
}
