package com.skyblock.core.combat.manager;

import com.skyblock.core.model.Stat;
import com.skyblock.core.manager.StatManager;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.EnumMap;
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
 * <p>Event handling is delegated to
 * {@link com.skyblock.core.listener.SkillXpListener}.</p>
 * <p>Not thread-safe; synchronize externally if needed.</p>
 */
public final class CombatManager {

    public static final int MAX_LEVEL = 60;

    private static final double[] XP_PER_LEVEL = {
            50, 125, 200, 300, 500, 800, 1200, 1700, 2500, 3500,
            5000, 7500, 10000, 15000, 20000, 30000, 50000, 75000, 100000, 150000,
            200000, 300000, 400000, 500000, 600000, 700000, 800000, 900000, 1000000, 1200000,
            1500000, 2000000, 2500000, 3000000, 3500000, 4000000, 4500000, 5000000, 5500000, 6000000,
            7000000, 8000000, 9000000, 10000000, 12000000, 14000000, 16000000, 18000000, 20000000, 22000000,
            24000000, 26000000, 28000000, 30000000, 32000000, 34000000, 36000000, 38000000, 40000000, 42000000
    };

    private static final CombatManager INSTANCE = new CombatManager();

    private final StatManager stats = StatManager.getInstance();
    final Random random = new Random();

    /** Returns {@code true} if a crit triggers for the given crit-chance percentage. */
    public boolean isCriticalHit(double critChance) {
        return random.nextDouble() * 100.0 < critChance;
    }

    public enum MobType {
        ZOMBIE, SKELETON, SPIDER, ENDERMAN, BLAZE, GHAST,
        SLIME, CREEPER, WITHER_SKELETON, MAGMA_CUBE, CAVE_SPIDER,
        WITCH, ENDERMITE, GUARDIAN, ELDER_GUARDIAN
    }

    private final Map<UUID, Integer> killCounts   = new HashMap<>();
    private final Map<UUID, Integer> playerDeaths = new HashMap<>();
    private final Map<UUID, Integer> mobKills     = new HashMap<>();
    private final Map<UUID, Map<MobType, Integer>> mobTypeKills = new HashMap<>();
    private final Map<UUID, Double>  combatXp     = new HashMap<>();

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
        return Math.max(0.0, stats.getStat(entity.getUniqueId(), Stat.DEFENSE));
    }

    /**
     * Returns the effective STRENGTH, CRIT_CHANCE, and CRIT_DAMAGE stats
     * for the given player via the shared {@link StatManager}.
     *
     * @param playerId the attacker's UUID
     * @return array: [strength, critChance, critDamage]
     */
    public double[] getAttackStats(UUID playerId) {
        double strength   = stats.getStat(playerId, Stat.STRENGTH);
        double critChance = stats.getStat(playerId, Stat.CRIT_CHANCE);
        double critDamage = stats.getStat(playerId, Stat.CRIT_DAMAGE);
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
        double strength = stats.getStat(attackerId, Stat.STRENGTH);
        double effective = baseDamage * (1.0 + strength / 100.0);

        if (defenderId != null) {
            double defense = Math.max(0.0, stats.getStat(defenderId, Stat.DEFENSE));
            effective = effective * 100.0 / (defense + 100.0);
        }

        return Math.max(0.0, effective);
    }

    // ---------------------------------------------------------------------------
    // Per-player kill / death tracking
    // ---------------------------------------------------------------------------

    public int getKills(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        return killCounts.getOrDefault(playerId, 0);
    }

    public int addKill(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        int total = killCounts.getOrDefault(playerId, 0) + 1;
        killCounts.put(playerId, total);
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

    public int addMobKill(UUID playerId, MobType type) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(type, "type");
        addMobKill(playerId);
        return mobTypeKills.computeIfAbsent(playerId, id -> new EnumMap<>(MobType.class))
                .merge(type, 1, Integer::sum);
    }

    public int getMobKillCount(UUID playerId, MobType type) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(type, "type");
        Map<MobType, Integer> counts = mobTypeKills.get(playerId);
        return counts != null ? counts.getOrDefault(type, 0) : 0;
    }

    public Map<MobType, Integer> getMobKillCounts(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        Map<MobType, Integer> counts = mobTypeKills.get(playerId);
        return counts != null ? Map.copyOf(counts) : Map.of();
    }

    // ---------------------------------------------------------------------------
    // Combat XP / skill level
    // ---------------------------------------------------------------------------

    public double addCombatXp(UUID playerId, double amount) {
        Objects.requireNonNull(playerId, "playerId");
        if (amount < 0) {
            throw new IllegalArgumentException("amount must not be negative, got " + amount);
        }
        double total = combatXp.getOrDefault(playerId, 0.0) + amount;
        combatXp.put(playerId, total);
        return total;
    }

    public double getCombatXp(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        return combatXp.getOrDefault(playerId, 0.0);
    }

    public int getLevel(UUID playerId) {
        double xp = getCombatXp(playerId);
        int level = 0;
        while (level < MAX_LEVEL && xp >= XP_PER_LEVEL[level]) {
            level++;
        }
        return level;
    }

    public boolean reset(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        boolean had = killCounts.remove(playerId) != null;
        had |= playerDeaths.remove(playerId) != null;
        had |= mobKills.remove(playerId) != null;
        had |= mobTypeKills.remove(playerId) != null;
        had |= combatXp.remove(playerId) != null;
        return had;
    }
}
