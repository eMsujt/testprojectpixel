package com.skyblock.core.armor;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * Singleton manager for SkyBlock named armor sets.
 *
 * <p>Each {@link ArmorSet} entry describes a well-known SkyBlock armor set and its
 * full-set {@link ArmorSetBonus}. Call {@link #getActiveSet(UUID)} to query which
 * set is currently active for a player and {@link #setActiveSet(UUID, ArmorSet)} to
 * update it whenever a player's armor changes.</p>
 *
 * <p>Not thread-safe; synchronize externally if needed.</p>
 */
public final class ArmorSetManager {

    /** A named SkyBlock armor set and its full-set bonus. */
    public enum ArmorSet {
        HARDENED_DIAMOND(
                "Hardened Diamond",
                new ArmorSetBonus("All pieces grant extra Defense", 100, 0, 0, 0)),
        PERFECT(
                "Perfect",
                new ArmorSetBonus("+1 Defense per 1 Defense on each piece", 200, 100, 0, 0)),
        SUPERIOR_DRAGON(
                "Superior Dragon",
                new ArmorSetBonus("+5% bonus to all stats", 100, 200, 100, 5)),
        STRONG_DRAGON(
                "Strong Dragon",
                new ArmorSetBonus("+75 Strength and +75 Crit Damage", 0, 0, 75, 0)),
        UNSTABLE_DRAGON(
                "Unstable Dragon",
                new ArmorSetBonus("+100 Crit Damage", 0, 0, 0, 0)),
        OLD_DRAGON(
                "Old Dragon",
                new ArmorSetBonus("+5 HP per armor piece worn", 0, 50, 0, 0)),
        WISE_DRAGON(
                "Wise Dragon",
                new ArmorSetBonus("+100 Magic Find", 0, 0, 0, 0)),
        YOUNG_DRAGON(
                "Young Dragon",
                new ArmorSetBonus("+70 Speed", 0, 0, 0, 70)),
        PROTECTOR_DRAGON(
                "Protector Dragon",
                new ArmorSetBonus("+1% Defense per 2,000 max HP", 150, 0, 0, 0)),
        HOLY_DRAGON(
                "Holy Dragon",
                new ArmorSetBonus("+5% HP and +1% Magic Find per piece", 0, 100, 0, 0)),
        FAIRY(
                "Fairy",
                new ArmorSetBonus("+5 HP per Fairy Soul collected", 0, 50, 0, 5)),
        NECRON(
                "Necron",
                new ArmorSetBonus("+3% damage dealt to all mobs", 100, 50, 50, 0)),
        MAXOR(
                "Maxor",
                new ArmorSetBonus("+25% Speed to Strength conversion", 0, 0, 50, 0)),
        STORM(
                "Storm",
                new ArmorSetBonus("+30% Strength while your HP is above 50%", 0, 0, 30, 0)),
        GOLDOR(
                "Goldor",
                new ArmorSetBonus("+100 Defense while in the Dungeons", 200, 0, 0, 0)),
        TARANTULA(
                "Tarantula",
                new ArmorSetBonus("+25% Crit Damage", 0, 0, 0, 0)),
        MASTIFF(
                "Mastiff",
                new ArmorSetBonus("+50 HP per Dungeon Star on each piece", 0, 200, 0, 0)),
        ZOMBIE_SOLDIER(
                "Zombie Soldier",
                new ArmorSetBonus("+2 Defense per Revenant Horror kill", 50, 0, 0, 0)),
        REVENANT(
                "Revenant",
                new ArmorSetBonus("+5% damage against Undead mobs", 75, 25, 0, 0)),
        MINERAL(
                "Mineral",
                new ArmorSetBonus("+1 Defense per 1,000 ore mined", 100, 0, 0, 0));

        private final String displayName;
        private final ArmorSetBonus bonus;

        ArmorSet(String displayName, ArmorSetBonus bonus) {
            this.displayName = displayName;
            this.bonus = bonus;
        }

        public String getDisplayName() { return displayName; }
        public ArmorSetBonus getBonus() { return bonus; }

        public static ArmorSet fromName(String name) {
            for (ArmorSet s : values()) {
                if (s.displayName.equalsIgnoreCase(name) || s.name().equalsIgnoreCase(name)) {
                    return s;
                }
            }
            return null;
        }
    }

    private static final ArmorSetManager INSTANCE = new ArmorSetManager();

    /** Per-player currently active set (null when no named set is worn). */
    private final Map<UUID, ArmorSet> activeSets = new HashMap<>();

    private ArmorSetManager() {}

    public static ArmorSetManager getInstance() {
        return INSTANCE;
    }

    /**
     * Returns the active {@link ArmorSet} for the given player, or {@code null} if none.
     *
     * @param playerId the player's UUID
     * @return active set or {@code null}
     */
    public ArmorSet getActiveSet(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        return activeSets.get(playerId);
    }

    /**
     * Sets the active armor set for the given player.
     *
     * @param playerId the player to update
     * @param set      the armor set now active
     */
    public void setActiveSet(UUID playerId, ArmorSet set) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(set, "set");
        activeSets.put(playerId, set);
    }

    /**
     * Clears the active armor set for the given player.
     *
     * @param playerId the player to reset
     */
    public void clearActiveSet(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        activeSets.remove(playerId);
    }

    /** Returns an unmodifiable view of all active sets, keyed by player UUID. */
    public Map<UUID, ArmorSet> getActiveSets() {
        return Collections.unmodifiableMap(activeSets);
    }
}
