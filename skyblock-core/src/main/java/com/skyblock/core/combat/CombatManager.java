package com.skyblock.core.combat;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.UUID;

/**
 * Singleton manager that computes SkyBlock damage using the formula:
 * <ol>
 *   <li>Strength bonus  — {@code base * (1 + strength / 100)}</li>
 *   <li>Critical hit    — roll {@code [0, 100)} against {@code critChance};
 *       if hit, multiply by {@code (1 + critDamage / 100)}</li>
 *   <li>Defense reduction — {@code effective * 100 / (defense + 100)}</li>
 * </ol>
 *
 * <p>Event handling is delegated to {@link CombatListener}.</p>
 * <p>Not thread-safe; synchronize externally if needed.</p>
 */
public final class CombatManager {

    private static final CombatManager INSTANCE = new CombatManager();

    private final StatManager stats = StatManager.getInstance();
    final Random random = new Random();

    private final Map<UUID, Integer> playerKills  = new HashMap<>();
    private final Map<UUID, Integer> playerDeaths = new HashMap<>();
    private final Map<UUID, Integer> mobKills     = new HashMap<>();

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
     * Returns the effective STRENGTH, CRIT_CHANCE, and CRIT_DAMAGE stats
     * for the given player via the shared {@link StatManager}.
     *
     * @param playerId the attacker's UUID
     * @return array: [strength, critChance, critDamage]
     */
    double[] getAttackStats(UUID playerId) {
        double strength   = stats.getStat(playerId, StatManager.CombatStat.STRENGTH);
        double critChance = stats.getStat(playerId, StatManager.CombatStat.CRIT_CHANCE);
        double critDamage = stats.getStat(playerId, StatManager.CombatStat.CRIT_DAMAGE);
        return new double[]{strength, critChance, critDamage};
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

    // ---------------------------------------------------------------------------
    // Per-player kill / death tracking
    // ---------------------------------------------------------------------------

    public int getKills(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        return playerKills.getOrDefault(playerId, 0);
    }

    public int addKill(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        int total = playerKills.getOrDefault(playerId, 0) + 1;
        playerKills.put(playerId, total);
        return total;
    }

    public int getDeaths(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        return playerDeaths.getOrDefault(playerId, 0);
    }

    public int addDeath(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        int total = playerDeaths.getOrDefault(playerId, 0) + 1;
        playerDeaths.put(playerId, total);
        return total;
    }

    public int getMobKills(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        return mobKills.getOrDefault(playerId, 0);
    }

    public int addMobKill(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        int total = mobKills.getOrDefault(playerId, 0) + 1;
        mobKills.put(playerId, total);
        return total;
    }

    public boolean reset(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        boolean had = playerKills.remove(playerId) != null;
        had |= playerDeaths.remove(playerId) != null;
        had |= mobKills.remove(playerId) != null;
        return had;
    }
}
