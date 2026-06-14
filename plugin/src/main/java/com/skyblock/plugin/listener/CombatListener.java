package com.skyblock.plugin.listener;

import com.skyblock.core.stat.StatManager.StatType;
import com.skyblock.core.stats.PlayerStatManager;
import com.skyblock.plugin.combat.DamageFormula;
import com.skyblock.plugin.economy.PlayerEconomy;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;

import java.util.UUID;

/**
 * Bukkit listener that intercepts {@link EntityDamageByEntityEvent} and, when the
 * damager is a {@link Player}, replaces Minecraft's raw damage with the SkyBlock
 * value from {@link DamageFormula} using the attacker's combat stats, then applies
 * the defender's defense and true-defense reductions.
 *
 * <p>Hypixel defense formula: {@code effective = damage × (1 - defense / (defense + 100))},
 * followed by a flat reduction of {@code trueDefense}.</p>
 */
public final class CombatListener implements Listener {

    private final PlayerStatManager statManager = PlayerStatManager.getInstance();
    private final PlayerEconomy economy = PlayerEconomy.getInstance();

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

    /**
     * Rewards the killer with combat coins when they slay a mob. The drop scales
     * with the victim's max health so tougher mobs pay out more.
     */
    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        LivingEntity victim = event.getEntity();
        Player killer = victim.getKiller();
        if (killer == null) {
            return;
        }
        long coins = Math.max(1L, Math.round(victim.getMaxHealth()));
        economy.addPurse(killer.getUniqueId(), coins);
    }
}
