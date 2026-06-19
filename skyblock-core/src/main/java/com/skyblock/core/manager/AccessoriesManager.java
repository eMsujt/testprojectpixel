package com.skyblock.core.manager;

import com.skyblock.core.model.AccessoryRarity;
import com.skyblock.core.model.Stat;
import com.skyblock.core.talisman.manager.TalismanManager;

import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * Singleton façade that aggregates stat bonuses from all three accessory
 * subsystems: equipped talismans (scaled by their assigned rarity multiplier),
 * power-stone tuning points, and the power-stone flat bonus.
 *
 * <p>Not thread-safe; synchronize externally if accessed from multiple threads.</p>
 */
public final class AccessoriesManager {

    private static final AccessoriesManager INSTANCE = new AccessoriesManager();

    private AccessoriesManager() {}

    public static AccessoriesManager getInstance() {
        return INSTANCE;
    }

    /**
     * Returns the net stat bonuses for the given player, combining:
     * <ol>
     *   <li>Each equipped talisman's base bonus multiplied by its assigned rarity multiplier.</li>
     *   <li>Per-stat tuning bonuses: tuning points × 2 per point (each point = +2 to that stat).</li>
     *   <li>Power-stone flat bonuses from {@link AccessoryBagManager#getPowerStoneBonuses}.</li>
     * </ol>
     *
     * @param playerId the player's UUID, must not be null
     * @return unmodifiable map of stat to total bonus; empty if the player has no accessories
     */
    public Map<Stat, Double> getTotalBonuses(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");

        TalismanManager talismanManager = TalismanManager.getInstance();
        AccessoryManager accessoryManager = AccessoryManager.getInstance();
        AccessoryBagManager bagManager = AccessoryBagManager.getInstance();

        Map<Stat, Double> totals = new EnumMap<>(Stat.class);

        // Talisman bonuses scaled by rarity multiplier
        for (TalismanManager.TalismanType type : talismanManager.getEquipped(playerId)) {
            AccessoryRarity rarity = accessoryManager.getRarity(playerId, type);
            double scaled = type.bonus * rarity.statMultiplier;
            totals.merge(type.stat, scaled, Double::sum);
        }

        // Tuning-point bonuses (2 stats per point allocated to each stat)
        for (Map.Entry<Stat, Integer> entry : accessoryManager.getTuning(playerId).entrySet()) {
            totals.merge(entry.getKey(), entry.getValue() * 2.0, Double::sum);
        }

        // Power-stone flat bonuses
        for (Map.Entry<Stat, Double> entry : bagManager.getPowerStoneBonuses(playerId).entrySet()) {
            totals.merge(entry.getKey(), entry.getValue(), Double::sum);
        }

        return Collections.unmodifiableMap(totals);
    }

    /**
     * Returns the total magical power for the given player, delegating to
     * {@link AccessoryManager#getTotalMagicalPower}.
     *
     * @param playerId the player's UUID, must not be null
     * @return the total magical power
     */
    public int getTotalMagicalPower(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        return AccessoryManager.getInstance().getTotalMagicalPower(playerId);
    }

    /**
     * Returns the number of tuning points available to the given player.
     *
     * @param playerId the player's UUID, must not be null
     * @return available tuning points
     */
    public int getAvailableTuningPoints(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        return AccessoryManager.getInstance().getAvailableTuningPoints(playerId);
    }
}
