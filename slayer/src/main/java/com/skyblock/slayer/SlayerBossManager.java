package com.skyblock.slayer;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.entity.Player;

/**
 * Singleton that spawns and tracks active slayer bosses for each player.
 *
 * <p>Each player may have at most one active boss at a time. Bosses are
 * identified by a randomly generated {@link UUID}. Access the shared
 * instance via {@link #getInstance()}. Not thread-safe; synchronize
 * externally if accessed from multiple threads.</p>
 */
public final class SlayerBossManager {

    /**
     * The four boss types that can be spawned through the slayer system.
     * Each entry declares its display name, the corresponding quest line,
     * and the base maximum health for tier-1 through tier-4 scaling.
     */
    public enum SlayerBossType {

        REVENANT("Revenant Horror", SlayerType.ZOMBIE, new long[]{500L, 20_000L, 400_000L, 10_000_000L}),
        TARANTULA("Tarantula Broodfather", SlayerType.SPIDER, new long[]{750L, 30_000L, 600_000L, 15_000_000L}),
        SVEN("Sven Packmaster", SlayerType.WOLF, new long[]{2_000L, 40_000L, 750_000L, 2_000_000L}),
        VOIDGLOOM("Voidgloom Seraph", SlayerType.ENDERMAN, new long[]{500L, 40_000L, 1_500_000L, 8_000_000L});

        private final String displayName;
        private final SlayerType questLine;
        private final long[] healthByTier;

        SlayerBossType(String displayName, SlayerType questLine, long[] healthByTier) {
            this.displayName = displayName;
            this.questLine = questLine;
            this.healthByTier = healthByTier;
        }

        public String getDisplayName() {
            return displayName;
        }

        public SlayerType getQuestLine() {
            return questLine;
        }

        /**
         * Returns the maximum health for the given tier (1-based).
         *
         * @param tier the boss tier, 1 to 4
         * @return max health for that tier
         * @throws IllegalArgumentException if tier is out of range
         */
        public long getMaxHealth(int tier) {
            if (tier < 1 || tier > healthByTier.length) {
                throw new IllegalArgumentException(
                        "tier must be between 1 and " + healthByTier.length + ": " + tier);
            }
            return healthByTier[tier - 1];
        }
    }

    /**
     * A live boss instance tied to one player for the duration of a fight.
     *
     * <p>Instances are created only through
     * {@link SlayerBossManager#spawnBoss(Player, SlayerBossType, int)}.</p>
     */
    public static final class ActiveBoss {

        private final UUID bossId;
        private final SlayerBossType type;
        private final int tier;
        private final long maxHealth;
        private long health;

        private ActiveBoss(SlayerBossType type, int tier) {
            this.bossId = UUID.randomUUID();
            this.type = type;
            this.tier = tier;
            this.maxHealth = type.getMaxHealth(tier);
            this.health = this.maxHealth;
        }

        public UUID getBossId() {
            return bossId;
        }

        public SlayerBossType getType() {
            return type;
        }

        public int getTier() {
            return tier;
        }

        public long getMaxHealth() {
            return maxHealth;
        }

        public long getHealth() {
            return health;
        }

        public boolean isDefeated() {
            return health <= 0;
        }
    }

    private static final SlayerBossManager INSTANCE = new SlayerBossManager();

    private SlayerBossManager() {
    }

    /**
     * Returns the shared manager instance.
     *
     * @return the singleton {@code SlayerBossManager}
     */
    public static SlayerBossManager getInstance() {
        return INSTANCE;
    }

    /** Active boss keyed by the owning player's UUID. */
    private final Map<UUID, ActiveBoss> activeBosses = new HashMap<>();
    /** Lookup from boss UUID back to owner UUID for defeat resolution. */
    private final Map<UUID, UUID> bossOwner = new HashMap<>();

    /**
     * Spawns a slayer boss for the player.
     *
     * @param player the player spawning the boss, must not be null
     * @param type   the boss type to spawn, must not be null
     * @param tier   the tier of the boss, 1 to 4
     * @return the newly spawned {@link ActiveBoss}
     * @throws IllegalArgumentException if {@code player} or {@code type} is
     *                                  null, or {@code tier} is out of range
     * @throws IllegalStateException    if the player already has an active boss
     */
    public ActiveBoss spawnBoss(Player player, SlayerBossType type, int tier) {
        if (player == null || type == null) {
            throw new IllegalArgumentException("player and type must not be null");
        }
        UUID playerId = player.getUniqueId();
        if (activeBosses.containsKey(playerId)) {
            throw new IllegalStateException("player already has an active slayer boss: " + playerId);
        }
        ActiveBoss boss = new ActiveBoss(type, tier);
        activeBosses.put(playerId, boss);
        bossOwner.put(boss.getBossId(), playerId);
        return boss;
    }

    /**
     * Returns the player's active boss, or {@code null} if none is in progress.
     *
     * @param playerId the player's UUID
     * @return the active boss, or {@code null}
     */
    public ActiveBoss getActiveBoss(UUID playerId) {
        return activeBosses.get(playerId);
    }

    /**
     * Returns whether the player currently has an active slayer boss.
     *
     * @param playerId the player's UUID
     * @return {@code true} if a boss fight is in progress
     */
    public boolean hasActiveBoss(UUID playerId) {
        return activeBosses.containsKey(playerId);
    }

    /**
     * Applies damage to the player's active boss.
     *
     * @param player the player whose boss to damage, must not be null
     * @param amount the damage amount, must be non-negative
     * @return the boss after applying damage
     * @throws IllegalArgumentException if {@code player} is null or
     *                                  {@code amount} is negative
     * @throws IllegalStateException    if the player has no active boss
     */
    public ActiveBoss damageBoss(Player player, long amount) {
        if (player == null) {
            throw new IllegalArgumentException("player must not be null");
        }
        if (amount < 0) {
            throw new IllegalArgumentException("amount must be non-negative: " + amount);
        }
        ActiveBoss boss = activeBosses.get(player.getUniqueId());
        if (boss == null) {
            throw new IllegalStateException("player has no active slayer boss: " + player.getUniqueId());
        }
        boss.health = Math.max(0L, boss.health - amount);
        return boss;
    }

    /**
     * Removes the player's active boss, whether defeated or despawned.
     *
     * @param player the player whose boss to remove, must not be null
     * @return the boss that was removed, or {@code null} if none was active
     * @throws IllegalArgumentException if {@code player} is null
     */
    public ActiveBoss defeatBoss(Player player) {
        if (player == null) {
            throw new IllegalArgumentException("player must not be null");
        }
        ActiveBoss boss = activeBosses.remove(player.getUniqueId());
        if (boss != null) {
            bossOwner.remove(boss.getBossId());
        }
        return boss;
    }
}
