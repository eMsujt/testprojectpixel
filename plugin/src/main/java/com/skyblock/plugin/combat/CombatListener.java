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
 * value from {@link DamageFormula} using the attacker's combat stats.
 */
public final class CombatListener implements Listener {

    private final StatManager statManager = StatManager.getInstance();

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        Entity attacker = event.getDamager();
        if (!(attacker instanceof Player)) {
            return;
        }

        UUID uuid = attacker.getUniqueId();
        double strength   = statManager.getStat(uuid, StatManager.CombatStat.STRENGTH);
        double critChance = statManager.getStat(uuid, StatManager.CombatStat.CRIT_CHANCE);
        double critDamage = statManager.getStat(uuid, StatManager.CombatStat.CRIT_DAMAGE);

        // The vanilla damage of the swung weapon feeds the formula's weapon-damage term.
        double weaponDamage = event.getDamage();
        double damage = DamageFormula.calculate(weaponDamage, strength, critChance, critDamage);

        event.setDamage(damage);
    }
}
