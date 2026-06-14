package com.skyblock.plugin.combat;

import com.skyblock.core.stat.StatManager.StatType;
import com.skyblock.core.stats.PlayerStatManager;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.UUID;

public final class CombatListener implements Listener {

    private final PlayerStatManager statManager = PlayerStatManager.getInstance();

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        Entity damager = event.getDamager();
        if (!(damager instanceof Player)) {
            return;
        }

        UUID attackerId = damager.getUniqueId();
        double weaponDamage = event.getDamage();
        double strength     = statManager.getStat(attackerId, StatType.STRENGTH);
        double critChance   = statManager.getStat(attackerId, StatType.CRIT_CHANCE);
        double critDamage   = statManager.getStat(attackerId, StatType.CRIT_DAMAGE);

        double damage = DamageFormula.calculate(weaponDamage, strength, critChance, critDamage);

        Entity victim = event.getEntity();
        if (victim instanceof Player) {
            UUID defenderId = victim.getUniqueId();
            double defense     = statManager.getStat(defenderId, StatType.DEFENSE);
            double trueDefense = statManager.getStat(defenderId, StatType.TRUE_DEFENSE);
            // Hypixel defense formula: damage × (1 - defense / (defense + 100)), then flat true-defense reduction
            damage *= (1.0 - defense / (defense + 100.0));
            damage = Math.max(0.0, damage - trueDefense);
        }

        event.setDamage(damage);
    }
}
