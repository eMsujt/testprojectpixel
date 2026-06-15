package com.skyblock.plugin.combat;

import com.skyblock.core.stat.Stat;
import com.skyblock.core.stats.PlayerStatManager;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.UUID;

/**
 * Bukkit listener that intercepts {@link EntityDamageByEntityEvent} and, when the
 * damager is a {@link Player}, replaces Minecraft's raw damage with the SkyBlock
 * value from {@link DamageFormula} using the attacker's combat stats, then applies
 * the defender's defense and true-defense reductions when the victim is a player.
 *
 * <p>Hypixel defense formula: {@code effective = damage × (1 - defense / (defense + 100))},
 * followed by a flat reduction of {@code trueDefense}.</p>
 *
 * @deprecated Use {@link com.skyblock.plugin.listener.CombatDamageListener} instead.
 */
@Deprecated
public final class HypixelDamageHandler implements Listener {

    private final PlayerStatManager statManager = PlayerStatManager.getInstance();

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        Entity damager = event.getDamager();
        if (!(damager instanceof Player)) {
            return;
        }

        UUID attackerId = damager.getUniqueId();
        double weaponDamage = event.getDamage();
        double strength     = statManager.getStat(attackerId, Stat.STRENGTH);
        double critChance   = statManager.getStat(attackerId, Stat.CRIT_CHANCE);
        double critDamage   = statManager.getStat(attackerId, Stat.CRIT_DAMAGE);

        double damage = DamageFormula.calculate(weaponDamage, strength, critChance, critDamage);

        Entity victim = event.getEntity();
        if (victim instanceof Player) {
            UUID defenderId = victim.getUniqueId();
            double defense     = statManager.getStat(defenderId, Stat.DEFENSE);
            double trueDefense = statManager.getStat(defenderId, Stat.TRUE_DEFENSE);
            // Hypixel defense formula: damage × (1 - defense / (defense + 100)), then flat true-defense reduction
            damage *= (1.0 - defense / (defense + 100.0));
            damage = Math.max(0.0, damage - trueDefense);
        }

        event.setDamage(damage);
    }
}
