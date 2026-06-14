package com.skyblock.plugin.combat;

import com.skyblock.core.combat.StatManager;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.UUID;

public final class CombatListener implements Listener {

    private final StatManager statManager = StatManager.getInstance();

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        Entity damager = event.getDamager();
        if (!(damager instanceof Player)) {
            return;
        }

        UUID attackerId = damager.getUniqueId();
        int weaponDamage  = (int) event.getDamage();
        double strength   = statManager.getStat(attackerId, StatManager.CombatStat.STRENGTH);
        double critChance = statManager.getStat(attackerId, StatManager.CombatStat.CRIT_CHANCE);
        double critDamage = statManager.getStat(attackerId, StatManager.CombatStat.CRIT_DAMAGE);

        double damage = DamageCalculator.calculate(weaponDamage, strength, critChance, critDamage);

        Entity victim = event.getEntity();
        if (victim instanceof Player) {
            UUID defenderId = victim.getUniqueId();
            double defense     = statManager.getStat(defenderId, StatManager.CombatStat.DEFENSE);
            double trueDefense = statManager.getStat(defenderId, StatManager.CombatStat.TRUE_DEFENSE);
            damage *= (1.0 - defense / (defense + 100.0));
            damage = Math.max(0.0, damage - trueDefense);
        }

        event.setDamage(damage);
    }
}
