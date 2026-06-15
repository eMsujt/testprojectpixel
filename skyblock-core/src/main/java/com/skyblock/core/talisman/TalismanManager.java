package com.skyblock.core.talisman;

import com.skyblock.core.model.AccessoryRarity;
import com.skyblock.core.model.Stat;

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

    /** Every talisman available in SkyBlock with its stat bonuses and rarity. */
    public enum TalismanType {
        // Speed line
        SPEED_TALISMAN(Stat.SPEED, 1.0, AccessoryRarity.COMMON),
        SPEED_RING(Stat.SPEED, 3.0, AccessoryRarity.UNCOMMON),
        SPEED_ARTIFACT(Stat.SPEED, 5.0, AccessoryRarity.RARE),
        // Strength line
        STRENGTH_TALISMAN(Stat.STRENGTH, 5.0, AccessoryRarity.COMMON),
        STRENGTH_RING(Stat.STRENGTH, 10.0, AccessoryRarity.UNCOMMON),
        STRENGTH_ARTIFACT(Stat.STRENGTH, 15.0, AccessoryRarity.RARE),
        // Crit Chance line
        CRIT_TALISMAN(Stat.CRIT_CHANCE, 3.0, AccessoryRarity.COMMON),
        CRIT_RING(Stat.CRIT_CHANCE, 5.0, AccessoryRarity.UNCOMMON),
        CRIT_ARTIFACT(Stat.CRIT_CHANCE, 8.0, AccessoryRarity.RARE),
        // Crit Damage line
        CRIT_DAMAGE_TALISMAN(Stat.CRIT_DAMAGE, 5.0, AccessoryRarity.COMMON),
        CRIT_DAMAGE_RING(Stat.CRIT_DAMAGE, 8.0, AccessoryRarity.UNCOMMON),
        CRIT_DAMAGE_ARTIFACT(Stat.CRIT_DAMAGE, 12.0, AccessoryRarity.RARE),
        // Defense line
        DEFENSE_TALISMAN(Stat.DEFENSE, 5.0, AccessoryRarity.COMMON),
        DEFENSE_RING(Stat.DEFENSE, 10.0, AccessoryRarity.UNCOMMON),
        DEFENSE_ARTIFACT(Stat.DEFENSE, 15.0, AccessoryRarity.RARE),
        // Health line
        HEALTH_TALISMAN(Stat.HEALTH, 10.0, AccessoryRarity.COMMON),
        HEALTH_RING(Stat.HEALTH, 20.0, AccessoryRarity.UNCOMMON),
        HEALTH_ARTIFACT(Stat.HEALTH, 30.0, AccessoryRarity.RARE),
        // Intelligence line
        INTELLIGENCE_TALISMAN(Stat.INTELLIGENCE, 10.0, AccessoryRarity.COMMON),
        INTELLIGENCE_RING(Stat.INTELLIGENCE, 20.0, AccessoryRarity.UNCOMMON),
        INTELLIGENCE_ARTIFACT(Stat.INTELLIGENCE, 30.0, AccessoryRarity.RARE),
        // Ferocity line
        FEROCITY_TALISMAN(Stat.FEROCITY, 1.0, AccessoryRarity.UNCOMMON),
        FEROCITY_RING(Stat.FEROCITY, 2.0, AccessoryRarity.RARE),
        FEROCITY_ARTIFACT(Stat.FEROCITY, 3.0, AccessoryRarity.EPIC),
        // Attack Speed line
        ATTACK_SPEED_TALISMAN(Stat.ATTACK_SPEED, 3.0, AccessoryRarity.UNCOMMON),
        ATTACK_SPEED_RING(Stat.ATTACK_SPEED, 5.0, AccessoryRarity.RARE),
        ATTACK_SPEED_ARTIFACT(Stat.ATTACK_SPEED, 8.0, AccessoryRarity.EPIC),
        // Magic Find line
        MAGIC_FIND_TALISMAN(Stat.MAGIC_FIND, 3.0, AccessoryRarity.UNCOMMON),
        MAGIC_FIND_RING(Stat.MAGIC_FIND, 5.0, AccessoryRarity.RARE),
        MAGIC_FIND_ARTIFACT(Stat.MAGIC_FIND, 8.0, AccessoryRarity.EPIC),
        // True Defense line
        TRUE_DEFENSE_TALISMAN(Stat.TRUE_DEFENSE, 3.0, AccessoryRarity.UNCOMMON),
        TRUE_DEFENSE_RING(Stat.TRUE_DEFENSE, 6.0, AccessoryRarity.RARE),
        TRUE_DEFENSE_ARTIFACT(Stat.TRUE_DEFENSE, 10.0, AccessoryRarity.EPIC),
        // Vitality line
        VITALITY_TALISMAN(Stat.VITALITY, 3.0, AccessoryRarity.UNCOMMON),
        VITALITY_RING(Stat.VITALITY, 6.0, AccessoryRarity.RARE),
        VITALITY_ARTIFACT(Stat.VITALITY, 10.0, AccessoryRarity.EPIC),
        // Named SkyBlock accessories
        JUNGLE_TALISMAN(Stat.SPEED, 2.0, AccessoryRarity.COMMON),
        FARMER_ORB(Stat.HEALTH, 5.0, AccessoryRarity.COMMON),
        HASTE_RING(Stat.ATTACK_SPEED, 2.0, AccessoryRarity.COMMON),
        ZOMBIE_TALISMAN(Stat.DEFENSE, 3.0, AccessoryRarity.COMMON),
        ZOMBIE_RING(Stat.DEFENSE, 6.0, AccessoryRarity.UNCOMMON),
        ZOMBIE_ARTIFACT(Stat.DEFENSE, 10.0, AccessoryRarity.RARE),
        SPIDER_TALISMAN(Stat.CRIT_DAMAGE, 3.0, AccessoryRarity.UNCOMMON),
        CANDY_TALISMAN(Stat.SPEED, 1.0, AccessoryRarity.COMMON),
        WOLF_TALISMAN(Stat.SPEED, 2.0, AccessoryRarity.UNCOMMON),
        WOLF_RING(Stat.SPEED, 4.0, AccessoryRarity.RARE),
        MELODY_HAIR(Stat.INTELLIGENCE, 15.0, AccessoryRarity.EPIC),
        ENDER_ARTIFACT(Stat.STRENGTH, 10.0, AccessoryRarity.EPIC),
        DOMINO_FRAGMENT(Stat.INTELLIGENCE, 5.0, AccessoryRarity.RARE),
        HEGEMONY_ARTIFACT(Stat.CRIT_DAMAGE, 20.0, AccessoryRarity.LEGENDARY),
        BEACON_TALISMAN(Stat.MAGIC_FIND, 5.0, AccessoryRarity.RARE);

        /** The stat this talisman boosts. */
        public final Stat stat;

        /** The flat bonus applied to that stat. */
        public final double bonus;

        /** The rarity tier of this talisman. */
        public final AccessoryRarity rarity;

        TalismanType(Stat stat, double bonus, AccessoryRarity rarity) {
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
     * @return a map of {@link Stat} to total bonus value; empty if none equipped
     */
    public Map<Stat, Double> getTotalBonuses(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        Set<TalismanType> set = equipped.get(playerId);
        if (set == null || set.isEmpty()) {
            return Collections.emptyMap();
        }
        Map<Stat, Double> totals = new EnumMap<>(Stat.class);
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
