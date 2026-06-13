package com.skyblock.core.talisman;

import com.skyblock.core.combat.StatManager.CombatStat;

import java.util.Collections;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

/**
 * Singleton tracking each player's equipped talismans and the stat bonuses
 * they provide.
 *
 * <p>Not thread-safe; synchronize externally if accessed from multiple threads.</p>
 */
public final class TalismanManager {

    /** Rarity tier for accessories, determining the stat multiplier applied to base bonuses. */
    public enum AccessoryRarity {
        COMMON("Common", 1.0),
        UNCOMMON("Uncommon", 1.5),
        RARE("Rare", 2.0),
        EPIC("Epic", 3.0),
        LEGENDARY("Legendary", 5.0);

        private final String displayName;
        /** Multiplier applied to the talisman's base stat bonus. */
        public final double statMultiplier;

        AccessoryRarity(String displayName, double statMultiplier) {
            this.displayName = displayName;
            this.statMultiplier = statMultiplier;
        }

        public String getDisplayName() { return displayName; }
    }

    /** Every talisman available in SkyBlock with its stat bonuses and rarity. */
    public enum TalismanType {
        SPEED_TALISMAN(CombatStat.SPEED, 1.0, AccessoryRarity.COMMON),
        STRENGTH_TALISMAN(CombatStat.STRENGTH, 5.0, AccessoryRarity.COMMON),
        CRIT_TALISMAN(CombatStat.CRIT_CHANCE, 3.0, AccessoryRarity.COMMON),
        CRIT_RING(CombatStat.CRIT_CHANCE, 5.0, AccessoryRarity.UNCOMMON),
        CRIT_ARTIFACT(CombatStat.CRIT_CHANCE, 8.0, AccessoryRarity.RARE),
        DEFENSE_TALISMAN(CombatStat.DEFENSE, 5.0, AccessoryRarity.COMMON),
        DEFENSE_RING(CombatStat.DEFENSE, 10.0, AccessoryRarity.UNCOMMON),
        DEFENSE_ARTIFACT(CombatStat.DEFENSE, 15.0, AccessoryRarity.RARE),
        HEALTH_TALISMAN(CombatStat.HEALTH, 10.0, AccessoryRarity.COMMON),
        FEROCITY_TALISMAN(CombatStat.FEROCITY, 1.0, AccessoryRarity.UNCOMMON),
        SPEED_RING(CombatStat.SPEED, 3.0, AccessoryRarity.UNCOMMON),
        SPEED_ARTIFACT(CombatStat.SPEED, 5.0, AccessoryRarity.RARE),
        INTELLIGENCE_TALISMAN(CombatStat.INTELLIGENCE, 10.0, AccessoryRarity.COMMON),
        MAGIC_FIND_TALISMAN(CombatStat.MAGIC_FIND, 3.0, AccessoryRarity.UNCOMMON),
        ATTACK_SPEED_TALISMAN(CombatStat.ATTACK_SPEED, 3.0, AccessoryRarity.UNCOMMON);

        /** The stat this talisman boosts. */
        public final CombatStat stat;

        /** The flat bonus applied to that stat. */
        public final double bonus;

        /** The rarity tier of this talisman. */
        public final AccessoryRarity rarity;

        TalismanType(CombatStat stat, double bonus, AccessoryRarity rarity) {
            this.stat = stat;
            this.bonus = bonus;
            this.rarity = rarity;
        }
    }

    private static final TalismanManager INSTANCE = new TalismanManager();

    /** Per-player set of currently equipped talismans. */
    private final Map<UUID, Set<TalismanType>> equipped = new HashMap<>();

    private TalismanManager() {
    }

    /**
     * Returns the single shared {@code TalismanManager} instance.
     *
     * @return the singleton instance
     */
    public static TalismanManager getInstance() {
        return INSTANCE;
    }

    /**
     * Equips a talisman for the given player.
     *
     * @param playerId the player equipping the talisman
     * @param type     the talisman to equip
     * @return {@code true} if the talisman was newly equipped, {@code false} if already equipped
     */
    public boolean equip(UUID playerId, TalismanType type) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(type, "type");
        return equipped.computeIfAbsent(playerId, id -> EnumSet.noneOf(TalismanType.class))
                .add(type);
    }

    /**
     * Unequips a talisman for the given player.
     *
     * @param playerId the player unequipping the talisman
     * @param type     the talisman to unequip
     * @return {@code true} if the talisman was removed, {@code false} if it wasn't equipped
     */
    public boolean unequip(UUID playerId, TalismanType type) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(type, "type");
        Set<TalismanType> set = equipped.get(playerId);
        return set != null && set.remove(type);
    }

    /**
     * Returns whether the given player has the specified talisman equipped.
     *
     * @param playerId the player to check
     * @param type     the talisman to look for
     * @return {@code true} if equipped
     */
    public boolean hasEquipped(UUID playerId, TalismanType type) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(type, "type");
        Set<TalismanType> set = equipped.get(playerId);
        return set != null && set.contains(type);
    }

    /**
     * Returns an unmodifiable view of the talismans the player currently has equipped.
     *
     * @param playerId the player to look up
     * @return an unmodifiable set of equipped talismans, empty if none
     */
    public Set<TalismanType> getEquipped(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        Set<TalismanType> set = equipped.get(playerId);
        return set == null ? Collections.emptySet() : Collections.unmodifiableSet(set);
    }

    /**
     * Computes the total stat bonuses provided by all of the player's equipped talismans.
     *
     * @param playerId the player to look up
     * @return a map of {@link CombatStat} to total bonus value; empty if none equipped
     */
    public Map<CombatStat, Double> getTotalBonuses(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        Set<TalismanType> set = equipped.get(playerId);
        if (set == null || set.isEmpty()) {
            return Collections.emptyMap();
        }
        Map<CombatStat, Double> totals = new EnumMap<>(CombatStat.class);
        for (TalismanType t : set) {
            totals.merge(t.stat, t.bonus, Double::sum);
        }
        return totals;
    }

    /**
     * Removes all talisman data for the given player.
     *
     * @param playerId the player to reset
     * @return {@code true} if the player had any equipped talismans, {@code false} otherwise
     */
    public boolean reset(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        return equipped.remove(playerId) != null;
    }
}
