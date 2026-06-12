package com.skyblock.core.magicfind;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * Singleton that tracks per-player Magic Find bonuses and applies them to loot
 * drop-chance calculations.
 *
 * <p>Magic Find (MF) increases rare-item drop probability. An effective MF of
 * {@code n} boosts a base drop chance {@code c} according to:<br>
 * {@code adjustedChance = c * (1 + n / 100)}</p>
 *
 * <p>Not thread-safe; synchronize externally if needed.</p>
 */
public final class MagicFindManager {

    private static final MagicFindManager INSTANCE = new MagicFindManager();

    /** Per-player additive Magic Find bonus (stacks on top of base 0). */
    private final Map<UUID, Double> bonuses = new HashMap<>();

    private MagicFindManager() {
    }

    /**
     * Returns the single shared {@code MagicFindManager} instance.
     *
     * @return the singleton instance
     */
    public static MagicFindManager getInstance() {
        return INSTANCE;
    }

    /**
     * Returns the total Magic Find bonus accumulated for the given player.
     *
     * @param playerId the player's UUID, must not be null
     * @return the total Magic Find bonus, {@code 0} if none
     */
    public double getMagicFind(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        return bonuses.getOrDefault(playerId, 0.0);
    }

    /**
     * Adds (or subtracts, if negative) a Magic Find bonus for the given player.
     *
     * @param playerId the player's UUID, must not be null
     * @param amount   the amount to add
     * @return the new total Magic Find bonus for the player
     */
    public double addBonus(UUID playerId, double amount) {
        Objects.requireNonNull(playerId, "playerId");
        double current = bonuses.getOrDefault(playerId, 0.0);
        double updated = current + amount;
        bonuses.put(playerId, updated);
        return updated;
    }

    /**
     * Removes all Magic Find data for the given player (called on disconnect).
     *
     * @param playerId the player's UUID, must not be null
     * @return {@code true} if the player had stored data, {@code false} otherwise
     */
    public boolean remove(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        return bonuses.remove(playerId) != null;
    }

    /**
     * Applies the given Magic Find value to a base drop chance.
     *
     * <p>The formula mirrors Hypixel SkyBlock: each point of Magic Find raises
     * the effective drop chance by 1 %. The result is clamped to {@code [0, 1]}.</p>
     *
     * @param baseChance the unmodified drop probability in the range {@code [0, 1]}
     * @param magicFind  the player's effective Magic Find value
     * @return the adjusted drop probability, clamped to {@code [0, 1]}
     */
    public double applyToChance(double baseChance, double magicFind) {
        double adjusted = baseChance * (1.0 + magicFind / 100.0);
        return Math.min(1.0, adjusted);
    }
}
