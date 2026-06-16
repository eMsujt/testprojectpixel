package com.skyblock.plugin.manager;

import com.skyblock.core.model.Stat;
import com.skyblock.core.stat.StatManager;
import com.skyblock.plugin.combat.calculator.DamageFormula;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.UUID;

/**
 * Singleton manager that intercepts {@link EntityDamageByEntityEvent} and replaces
 * Minecraft's raw damage with the Hypixel SkyBlock value from {@link DamageFormula}.
 *
 * <p>When the attacker is a {@link Player} the damage is computed from their
 * weapon-damage, strength, crit-chance, and crit-damage stats. When the victim is
 * also a {@link Player} the Hypixel defense formula is applied:
 * {@code effective = damage × (1 − defense / (defense + 100))}, followed by a flat
 * {@code trueDefense} reduction.</p>
 */
public final class DamageManager implements Listener {

    private static final DamageManager INSTANCE = new DamageManager();

    private final StatManager statManager = StatManager.getInstance();

    private DamageManager() {
    }

    public static DamageManager getInstance() {
        return INSTANCE;
    }

    /**
     * Registers this manager as a Bukkit event listener.
     *
     * @param plugin the owning plugin
     */
    public void register(JavaPlugin plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        Entity damager = event.getDamager();
        if (!(damager instanceof Player)) {
            return;
        }

        UUID attackerId = damager.getUniqueId();
        double weaponDamage = event.getDamage();
        double strength   = statManager.getStat(attackerId, Stat.STRENGTH);
        double critChance = statManager.getStat(attackerId, Stat.CRIT_CHANCE);
        double critDamage = statManager.getStat(attackerId, Stat.CRIT_DAMAGE);

        double damage = DamageFormula.calculate(weaponDamage, strength, critChance, critDamage);

        Entity victim = event.getEntity();
        if (victim instanceof Player) {
            UUID defenderId = victim.getUniqueId();
            double defense     = statManager.getStat(defenderId, Stat.DEFENSE);
            double trueDefense = statManager.getStat(defenderId, Stat.TRUE_DEFENSE);
            damage *= (1.0 - defense / (defense + 100.0));
            damage = Math.max(0.0, damage - trueDefense);
        }

        event.setDamage(damage);
    }
}
