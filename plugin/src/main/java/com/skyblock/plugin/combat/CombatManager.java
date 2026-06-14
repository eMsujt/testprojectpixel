package com.skyblock.plugin.combat;

import com.skyblock.core.combat.StatManager;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.UUID;

/**
 * Singleton listener that resolves SkyBlock combat damage.
 *
 * <p>On {@link EntityDamageByEntityEvent}, when the damager is a {@link Player},
 * the vanilla damage is replaced with the value from {@link DamageFormula} using
 * the attacker's combat stats from {@link StatManager}.</p>
 *
 * <p>This type is registered as an event listener in
 * {@link com.skyblock.plugin.SkyBlockPlugin#onEnable()}.</p>
 */
public final class CombatManager implements Listener {

    private static final CombatManager INSTANCE = new CombatManager();

    private final StatManager statManager = StatManager.getInstance();

    private CombatManager() {}

    public static CombatManager getInstance() {
        return INSTANCE;
    }

    /**
     * Replaces a player's vanilla hit damage with the SkyBlock-computed value.
     *
     * @param event the damage event
     */
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
        event.setDamage(DamageFormula.calculate(weaponDamage, strength, critChance, critDamage));
    }

    /**
     * Calculates the melee damage of a hit from the given combat stats.
     *
     * @param weaponDamage      base weapon damage stat, clamped to &ge; 0
     * @param strength          attacker's strength stat, clamped to &ge; 0
     * @param critChancePercent chance to land a critical hit as a percentage, e.g. {@code 30.0} for 30 %
     * @param critDamagePercent crit damage bonus as a percentage, e.g. {@code 50.0} for +50 %
     * @return the final damage dealt, never negative
     */
    public static double calculateDamage(double weaponDamage, double strength, double critChancePercent, double critDamagePercent) {
        return DamageFormula.calculate(weaponDamage, strength, critChancePercent, critDamagePercent);
    }
}
