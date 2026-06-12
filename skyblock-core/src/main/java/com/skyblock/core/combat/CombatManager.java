package com.skyblock.core.combat;

import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.Objects;
import java.util.Random;
import java.util.UUID;

/**
 * Singleton Bukkit listener that intercepts {@link EntityDamageByEntityEvent}
 * and replaces Minecraft's raw damage with a SkyBlock damage calculation.
 *
 * <p>Formula (attacker must be a {@link Player}):</p>
 * <ol>
 *   <li>Strength bonus  — {@code base * (1 + strength / 100)}</li>
 *   <li>Critical hit    — roll {@code [0, 100)} against {@code critChance};
 *       if hit, multiply by {@code (1 + critDamage / 100)}</li>
 *   <li>Defense reduction — {@code effective * 100 / (defense + 100)}</li>
 * </ol>
 *
 * <p>Not thread-safe; synchronize externally if needed.</p>
 */
public final class CombatManager implements Listener {

    private static final CombatManager INSTANCE = new CombatManager();

    private final StatManager stats = StatManager.getInstance();
    private final Random random = new Random();

    private CombatManager() {
    }

    /**
     * Returns the single shared {@code CombatManager} instance.
     *
     * @return the singleton instance
     */
    public static CombatManager getInstance() {
        return INSTANCE;
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

        UUID attackerId = attacker.getUniqueId();
        double strength  = stats.getStat(attackerId, StatManager.CombatStat.STRENGTH);
        double critChance = stats.getStat(attackerId, StatManager.CombatStat.CRIT_CHANCE);
        double critDamage = stats.getStat(attackerId, StatManager.CombatStat.CRIT_DAMAGE);

        double baseDamage = event.getDamage();

        // 1. Strength bonus
        double effective = baseDamage * (1.0 + strength / 100.0);

        // 2. Critical hit
        if (random.nextDouble() * 100.0 < critChance) {
            effective *= (1.0 + critDamage / 100.0);
        }

        // 3. Defense reduction (defender must be a LivingEntity for stat lookup)
        Entity defender = event.getEntity();
        if (defender instanceof LivingEntity) {
            double defense = getDefense(defender);
            effective = effective * 100.0 / (defense + 100.0);
        }

        event.setDamage(Math.max(0.0, effective));
    }

    // ---------------------------------------------------------------------------
    // Helpers
    // ---------------------------------------------------------------------------

    /**
     * Returns the effective DEFENSE stat for the given entity.
     * Players use {@link StatManager}; non-players default to {@code 0}.
     *
     * @param entity the defending entity
     * @return defense value, {@code >= 0}
     */
    public double getDefense(Entity entity) {
        Objects.requireNonNull(entity, "entity");
        if (!(entity instanceof Player)) {
            return 0.0;
        }
        return Math.max(0.0, stats.getStat(entity.getUniqueId(), StatManager.CombatStat.DEFENSE));
    }

    /**
     * Computes the final SkyBlock damage without a crit roll (deterministic,
     * useful for tooltips or simulations).
     *
     * @param attackerId the attacker's UUID
     * @param defenderId the defender's UUID ({@code null} for no defender)
     * @param baseDamage the raw weapon damage
     * @return the final damage after strength and defense modifiers (no crit)
     */
    public double computeDamage(UUID attackerId, UUID defenderId, double baseDamage) {
        Objects.requireNonNull(attackerId, "attackerId");
        double strength = stats.getStat(attackerId, StatManager.CombatStat.STRENGTH);
        double effective = baseDamage * (1.0 + strength / 100.0);

        if (defenderId != null) {
            double defense = Math.max(0.0, stats.getStat(defenderId, StatManager.CombatStat.DEFENSE));
            effective = effective * 100.0 / (defense + 100.0);
        }

        return Math.max(0.0, effective);
    }
}
