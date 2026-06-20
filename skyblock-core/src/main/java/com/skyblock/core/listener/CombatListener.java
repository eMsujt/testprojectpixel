package com.skyblock.core.listener;

import com.skyblock.core.combat.calculator.CombatEngine;
import com.skyblock.core.manager.StatManager;
import com.skyblock.core.model.Stat;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

/**
 * Applies the full SkyBlock damage formula to melee combat.
 *
 * <p>When a player deals melee damage, the event damage is recomputed from the
 * attacker's Strength, Crit Chance, and Crit Damage stats via {@link CombatEngine}.
 * When a player takes damage, it is reduced by the SkyBlock defense formula
 * ({@code damage × 100 / (100 + defense)}).</p>
 */
public final class CombatListener implements Listener {

    private static final CombatListener INSTANCE = new CombatListener();

    private CombatListener() {}

    public static CombatListener getInstance() {
        return INSTANCE;
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        StatManager stats = StatManager.getInstance();

        if (event.getDamager() instanceof Player) {
            Player attacker = (Player) event.getDamager();
            double strength = stats.getStat(attacker.getUniqueId(), Stat.STRENGTH);
            double critChance = stats.getStat(attacker.getUniqueId(), Stat.CRIT_CHANCE);
            double critDamage = stats.getStat(attacker.getUniqueId(), Stat.CRIT_DAMAGE);
            double damage = CombatEngine.calculateDamage(event.getDamage(), strength, critChance, critDamage);
            event.setDamage(damage);
        }

        if (event.getEntity() instanceof Player) {
            Player defender = (Player) event.getEntity();
            double defense = stats.getStat(defender.getUniqueId(), Stat.DEFENSE);
            double reduced = event.getDamage() * 100.0 / (100.0 + Math.max(0.0, defense));
            event.setDamage(reduced);
        }
    }
}
