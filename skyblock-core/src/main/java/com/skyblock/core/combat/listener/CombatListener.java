package com.skyblock.core.combat.listener;

import com.skyblock.core.combat.CombatManager;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

/**
 * Bukkit listener that intercepts {@link EntityDamageByEntityEvent} and
 * replaces Minecraft's raw damage with the SkyBlock formula from
 * {@link CombatManager}.
 */
public final class CombatListener implements Listener {

    private final CombatManager combatManager;

    /**
     * Creates a listener backed by the given {@link CombatManager}.
     *
     * @param combatManager the combat manager, must not be null
     * @throws IllegalArgumentException if {@code combatManager} is null
     */
    public CombatListener(CombatManager combatManager) {
        if (combatManager == null) {
            throw new IllegalArgumentException("combatManager must not be null");
        }
        this.combatManager = combatManager;
    }

    /**
     * Intercepts entity-damage events where the attacker is a player and
     * overwrites the damage with the SkyBlock formula result.
     *
     * @param event the damage event
     */
    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        Entity attacker = event.getDamager();
        if (!(attacker instanceof Player)) {
            return;
        }

        double[] attackStats = combatManager.getAttackStats(attacker.getUniqueId());
        double strength   = attackStats[0];
        double critChance = attackStats[1];
        double critDamage = attackStats[2];

        double baseDamage = event.getDamage();

        // 1. Strength bonus
        double effective = baseDamage * (1.0 + strength / 100.0);

        // 2. Critical hit
        if (combatManager.isCriticalHit(critChance)) {
            effective *= (1.0 + critDamage / 100.0);
        }

        // 3. Defense reduction (defender must be a LivingEntity for stat lookup)
        Entity defender = event.getEntity();
        if (defender instanceof LivingEntity) {
            double defense = combatManager.getDefense(defender);
            effective = effective * 100.0 / (defense + 100.0);
        }

        event.setDamage(Math.max(0.0, effective));
    }
}
