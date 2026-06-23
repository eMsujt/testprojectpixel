package com.skyblock.core.manager;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * Singleton tracking each player's Chocolate Factory state.
 *
 * <p>Models Hypixel's factory: a chocolate balance plus seven named rabbit
 * {@link Employee}s, each with a level you raise by spending chocolate. Total
 * production is the sum of every employee's level &times; its per-level rate.
 * Not thread-safe; synchronize externally if accessed from multiple threads.</p>
 */
public final class ChocolateFactoryManager {

    /** The seven named rabbit employees, in escalating production order. */
    public enum Employee {
        RABBIT_BRO("Rabbit Bro", 1),
        RABBIT_COUSIN("Rabbit Cousin", 2),
        RABBIT_SIS("Rabbit Sis", 4),
        RABBIT_DADDY("Rabbit Daddy", 8),
        RABBIT_GRANNY("Rabbit Granny", 20),
        RABBIT_UNCLE("Rabbit Uncle", 50),
        RABBIT_DOG("Rabbit Dog", 200);

        public final String displayName;
        /** Chocolate-per-second this employee adds for each level. */
        public final int perLevel;

        Employee(String displayName, int perLevel) {
            this.displayName = displayName;
            this.perLevel = perLevel;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    private static final ChocolateFactoryManager INSTANCE = new ChocolateFactoryManager();

    /** Per-player chocolate balance; absent entries mean no chocolate earned yet. */
    private final Map<UUID, Long> chocolateBalances = new HashMap<>();

    /** Per-player employee levels. */
    private final Map<UUID, Map<Employee, Integer>> employeeLevels = new HashMap<>();

    private ChocolateFactoryManager() {
    }

    public static ChocolateFactoryManager getInstance() {
        return INSTANCE;
    }

    public long getChocolate(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        return chocolateBalances.getOrDefault(playerId, 0L);
    }

    public void addChocolate(UUID playerId, long amount) {
        Objects.requireNonNull(playerId, "playerId");
        if (amount <= 0) throw new IllegalArgumentException("amount must be positive");
        chocolateBalances.merge(playerId, amount, Long::sum);
    }

    /** Spends chocolate if the player can afford it; returns {@code true} on success. */
    public boolean spendChocolate(UUID playerId, long amount) {
        Objects.requireNonNull(playerId, "playerId");
        if (amount <= 0) return false;
        long balance = getChocolate(playerId);
        if (balance < amount) return false;
        chocolateBalances.put(playerId, balance - amount);
        return true;
    }

    public int getEmployeeLevel(UUID playerId, Employee employee) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(employee, "employee");
        Map<Employee, Integer> levels = employeeLevels.get(playerId);
        return levels == null ? 0 : levels.getOrDefault(employee, 0);
    }

    /** Chocolate-per-second this employee currently contributes. */
    public int getEmployeeProduction(UUID playerId, Employee employee) {
        return getEmployeeLevel(playerId, employee) * employee.perLevel;
    }

    /** Chocolate cost to raise the employee from its current level to the next. */
    public long getUpgradeCost(UUID playerId, Employee employee) {
        int next = getEmployeeLevel(playerId, employee) + 1;
        return (long) employee.perLevel * next * 10L;
    }

    /**
     * Spends chocolate to raise an employee one level. Returns {@code true} if the
     * player could afford it, {@code false} otherwise.
     */
    public boolean upgradeEmployee(UUID playerId, Employee employee) {
        long cost = getUpgradeCost(playerId, employee);
        if (!spendChocolate(playerId, cost)) return false;
        employeeLevels.computeIfAbsent(playerId, id -> new EnumMap<>(Employee.class))
                .merge(employee, 1, Integer::sum);
        return true;
    }

    /** Total chocolate-per-second across all of the player's employees. */
    public int getProductionRate(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        Map<Employee, Integer> levels = employeeLevels.get(playerId);
        if (levels == null) return 0;
        int rate = 0;
        for (Map.Entry<Employee, Integer> entry : levels.entrySet()) {
            rate += entry.getKey().perLevel * entry.getValue();
        }
        return rate;
    }

    public boolean remove(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        boolean had = chocolateBalances.remove(playerId) != null;
        had |= employeeLevels.remove(playerId) != null;
        return had;
    }
}
