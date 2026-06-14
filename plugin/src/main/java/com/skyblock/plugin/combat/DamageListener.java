package com.skyblock.plugin.combat;

import com.skyblock.core.combat.StatManager;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.UUID;

/**
 * Bukkit listener that intercepts {@link EntityDamageByEntityEvent} and, when the
 * damager is a {@link Player}, replaces Minecraft's raw damage with the SkyBlock
 * value from {@link DamageCalculator} using the attacker's combat stats, then applies
 * the defender's defense and true-defense reductions.
 *
 * <p>Hypixel defense formula: {@code effective = damage × (1 - defense / (defense + 100))},
 * followed by a flat reduction of {@code trueDefense}.</p>
 */
public final class DamageListener implements Listener {

    private final StatManager statManager = StatManager.getInstance();

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        Entity damager = event.getDamager();
        if (!(damager instanceof Player)) {
            return;
        }

        UUID attackerId = damager.getUniqueId();
        double strength   = statManager.getStat(attackerId, StatManager.CombatStat.STRENGTH);
        double critChance = statManager.getStat(attackerId, StatManager.CombatStat.CRIT_CHANCE);
        double critDamage = statManager.getStat(attackerId, StatManager.CombatStat.CRIT_DAMAGE);

        int weaponDamage = (int) event.getDamage();
        double damage = DamageCalculator.computeDamage(weaponDamage, strength, critChance, critDamage);

        // Apply defense reduction when the victim is a player.
        Entity victim = event.getEntity();
        if (victim instanceof Player) {
            UUID defenderId = victim.getUniqueId();
            double defense     = statManager.getStat(defenderId, StatManager.CombatStat.DEFENSE);
            double trueDefense = statManager.getStat(defenderId, StatManager.CombatStat.TRUE_DEFENSE);
            // Percentage reduction: damage × (1 - defense / (defense + 100))
            damage *= (1.0 - defense / (defense + 100.0));
            // Flat reduction from true defense, never below zero.
            damage = Math.max(0.0, damage - trueDefense);
        }

        event.setDamage(damage);
    }
}
