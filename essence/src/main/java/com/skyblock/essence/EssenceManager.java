package com.skyblock.essence;

import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Manages per-player essence balances.
 *
 * <p>Each player holds a separate balance for every {@link EssenceType}.
 * Balances are never negative. Not thread-safe; synchronize externally if
 * accessed from multiple threads.</p>
 */
public final class EssenceManager {

    /**
     * The kinds of essence a player can collect.
     */
    public enum EssenceType {
        WITHER,
        SPIDER,
        UNDEAD,
        DRAGON,
        GOLD,
        DIAMOND,
        ICE,
        CRIMSON
    }

    private final Map<UUID, Map<EssenceType, Long>> balances = new HashMap<>();

    /**
     * Adds essence to a player's balance.
     *
     * @param playerId the unique id of the player, must not be null
     * @param type     the essence type, must not be null
     * @param amount   the amount to add, must be positive
     * @throws IllegalArgumentException if the player or type is null, or the
     *                                  amount is not positive
     */
    public void addEssence(UUID playerId, EssenceType type, long amount) {
        if (playerId == null) {
            throw new IllegalArgumentException("playerId must not be null");
        }
        if (type == null) {
            throw new IllegalArgumentException("type must not be null");
        }
        if (amount <= 0) {
            throw new IllegalArgumentException("amount must be positive: " + amount);
        }
        balances.computeIfAbsent(playerId, id -> new EnumMap<>(EssenceType.class))
                .merge(type, amount, Long::sum);
    }

    /**
     * Withdraws essence from a player's balance if it is sufficient.
     *
     * @param playerId the unique id of the player
     * @param type     the essence type
     * @param amount   the amount to withdraw, must be positive
     * @return {@code true} if the player had at least the requested amount and
     *         it has been withdrawn
     * @throws IllegalArgumentException if the amount is not positive
     */
    public boolean withdrawEssence(UUID playerId, EssenceType type, long amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("amount must be positive: " + amount);
        }
        long current = getEssence(playerId, type);
        if (current < amount) {
            return false;
        }
        Map<EssenceType, Long> playerBalances = balances.get(playerId);
        long remaining = current - amount;
        if (remaining == 0) {
            playerBalances.remove(type);
            if (playerBalances.isEmpty()) {
                balances.remove(playerId);
            }
        } else {
            playerBalances.put(type, remaining);
        }
        return true;
    }

    /**
     * Returns a player's balance for one essence type.
     *
     * @param playerId the unique id of the player
     * @param type     the essence type
     * @return the balance, or {@code 0} if the player holds none
     */
    public long getEssence(UUID playerId, EssenceType type) {
        Map<EssenceType, Long> playerBalances = balances.get(playerId);
        if (playerBalances == null) {
            return 0L;
        }
        return playerBalances.getOrDefault(type, 0L);
    }

    /**
     * Returns all non-zero essence balances of a player.
     *
     * @param playerId the unique id of the player
     * @return an unmodifiable view of the player's balances by type; empty if
     *         the player holds no essence
     */
    public Map<EssenceType, Long> getEssences(UUID playerId) {
        Map<EssenceType, Long> playerBalances = balances.get(playerId);
        if (playerBalances == null) {
            return Collections.emptyMap();
        }
        return Collections.unmodifiableMap(playerBalances);
    }
}
