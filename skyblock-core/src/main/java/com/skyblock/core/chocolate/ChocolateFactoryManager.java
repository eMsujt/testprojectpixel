package com.skyblock.core.chocolate;

import com.skyblock.core.model.Rarity;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * Singleton tracking each player's Chocolate Factory state.
 *
 * <p>Manages the player's chocolate balance and rabbit collection.
 * Not thread-safe; synchronize externally if accessed from multiple threads.</p>
 */
public final class ChocolateFactoryManager {

    /** Chocolate-per-second production contributed by one rabbit of each rarity. */
    public static final EnumMap<Rarity, Integer> CHOCOLATE_PER_SECOND = new EnumMap<>(Rarity.class);
    static {
        CHOCOLATE_PER_SECOND.put(Rarity.COMMON,      1);
        CHOCOLATE_PER_SECOND.put(Rarity.UNCOMMON,    2);
        CHOCOLATE_PER_SECOND.put(Rarity.RARE,        4);
        CHOCOLATE_PER_SECOND.put(Rarity.EPIC,        8);
        CHOCOLATE_PER_SECOND.put(Rarity.LEGENDARY,  20);
        CHOCOLATE_PER_SECOND.put(Rarity.MYTHIC,     50);
        CHOCOLATE_PER_SECOND.put(Rarity.DIVINE,    200);
    }

    private static final ChocolateFactoryManager INSTANCE = new ChocolateFactoryManager();

    /** Per-player chocolate balance; absent entries mean no chocolate earned yet. */
    private final Map<UUID, Long> chocolateBalances = new HashMap<>();

    /** Per-player rabbit counts by rarity. */
    private final Map<UUID, Map<Rarity, Integer>> rabbitCounts = new HashMap<>();

    private ChocolateFactoryManager() {
    }

    /**
     * Returns the single shared {@code ChocolateFactoryManager} instance.
     *
     * @return the singleton instance
     */
    public static ChocolateFactoryManager getInstance() {
        return INSTANCE;
    }

    /**
     * Returns the current chocolate balance for the given player.
     *
     * @param playerId the player to look up
     * @return the chocolate balance, {@code 0} if none
     */
    public long getChocolate(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        return chocolateBalances.getOrDefault(playerId, 0L);
    }

    /**
     * Adds chocolate to the given player's balance.
     *
     * @param playerId the player
     * @param amount   the amount to add (must be positive)
     */
    public void addChocolate(UUID playerId, long amount) {
        Objects.requireNonNull(playerId, "playerId");
        if (amount <= 0) throw new IllegalArgumentException("amount must be positive");
        chocolateBalances.merge(playerId, amount, Long::sum);
    }

    /**
     * Returns the number of rabbits of the given rarity the player owns.
     *
     * @param playerId the player
     * @param rarity   the rabbit rarity
     * @return the rabbit count, {@code 0} if none
     */
    public int getRabbitCount(UUID playerId, Rarity rarity) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(rarity, "rarity");
        Map<Rarity, Integer> counts = rabbitCounts.get(playerId);
        return counts == null ? 0 : counts.getOrDefault(rarity, 0);
    }

    /**
     * Adds one rabbit of the given rarity to the player's collection.
     *
     * @param playerId the player
     * @param rarity   the rarity of the rabbit to add
     */
    public void addRabbit(UUID playerId, Rarity rarity) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(rarity, "rarity");
        rabbitCounts.computeIfAbsent(playerId, id -> new HashMap<>())
                .merge(rarity, 1, Integer::sum);
    }

    /**
     * Calculates the total chocolate-per-second rate for the given player based on their rabbits.
     *
     * @param playerId the player
     * @return the total chocolate-per-second production rate
     */
    public int getProductionRate(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        Map<Rarity, Integer> counts = rabbitCounts.get(playerId);
        if (counts == null) return 0;
        int rate = 0;
        for (Map.Entry<Rarity, Integer> entry : counts.entrySet()) {
            rate += CHOCOLATE_PER_SECOND.getOrDefault(entry.getKey(), 0) * entry.getValue();
        }
        return rate;
    }

    /**
     * Removes all Chocolate Factory data for the given player (e.g. on quit).
     *
     * @param playerId the player to remove
     * @return {@code true} if the player had data, {@code false} otherwise
     */
    public boolean remove(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        boolean had = chocolateBalances.remove(playerId) != null;
        had |= rabbitCounts.remove(playerId) != null;
        return had;
    }
}
