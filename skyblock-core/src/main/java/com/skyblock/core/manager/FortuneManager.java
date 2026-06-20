package com.skyblock.core.manager;

import com.skyblock.core.model.Stat;

import java.util.Objects;
import java.util.UUID;

/**
 * Singleton facade for the three fortune stats: Farming Fortune, Mining Fortune,
 * and Foraging Fortune.
 *
 * <p>Values are backed by {@link StatManager}; this class provides typed accessors
 * so call-sites don't scatter raw {@link Stat} references.</p>
 *
 * <p>Not thread-safe; access from the main server thread only.</p>
 */
public final class FortuneManager {

    public enum FortuneType {
        FARMING(Stat.FARMING_FORTUNE),
        MINING(Stat.MINING_FORTUNE),
        FORAGING(Stat.FORAGING_FORTUNE);

        private final Stat stat;

        FortuneType(Stat stat) {
            this.stat = stat;
        }

        public Stat getStat() {
            return stat;
        }
    }

    private static final FortuneManager INSTANCE = new FortuneManager();

    private FortuneManager() {}

    public static FortuneManager getInstance() {
        return INSTANCE;
    }

    /**
     * Returns the effective fortune value (base + bonuses) for the given type.
     *
     * @param playerId    the player's UUID
     * @param fortuneType which fortune stat to query
     * @return the effective fortune value
     */
    public double getFortune(UUID playerId, FortuneType fortuneType) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(fortuneType, "fortuneType");
        return StatManager.getInstance().getStat(playerId, fortuneType.getStat());
    }

    /** Convenience method for {@link FortuneType#FARMING}. */
    public double getFarmingFortune(UUID playerId) {
        return getFortune(playerId, FortuneType.FARMING);
    }

    /** Convenience method for {@link FortuneType#MINING}. */
    public double getMiningFortune(UUID playerId) {
        return getFortune(playerId, FortuneType.MINING);
    }

    /** Convenience method for {@link FortuneType#FORAGING}. */
    public double getForagingFortune(UUID playerId) {
        return getFortune(playerId, FortuneType.FORAGING);
    }

    /**
     * Adds a bonus to the given fortune stat for a player (e.g. from a pet, tool, or perk).
     *
     * @param playerId    the player's UUID
     * @param fortuneType which fortune stat to update
     * @param amount      the bonus amount to add (may be negative to remove a bonus)
     */
    public void addBonus(UUID playerId, FortuneType fortuneType, double amount) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(fortuneType, "fortuneType");
        StatManager.getInstance().addBonus(playerId, fortuneType.getStat(), amount);
    }

    /**
     * Sets the base fortune value for the given type, overriding the stat default.
     *
     * @param playerId    the player's UUID
     * @param fortuneType which fortune stat to set
     * @param value       the new base value
     */
    public void setBase(UUID playerId, FortuneType fortuneType, double value) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(fortuneType, "fortuneType");
        StatManager.getInstance().setBaseStat(playerId, fortuneType.getStat(), value);
    }

}
