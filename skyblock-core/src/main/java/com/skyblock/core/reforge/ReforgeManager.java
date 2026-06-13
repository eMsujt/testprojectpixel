package com.skyblock.core.reforge;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * Singleton manager for SkyBlock item reforges.
 *
 * <p>Tracks the active reforge applied to each player's held item slot
 * and exposes the full {@link Reforge} catalogue with stat bonuses.</p>
 *
 * <p>Not thread-safe; synchronize externally if needed.</p>
 */
public final class ReforgeManager {

    /** A reforge type with display name and primary stat bonus. */
    public enum ReforgeType {
        NONE("None", 0, 0, 0),
        SHARP("Sharp", 10, 0, 0),
        FIERCE("Fierce", 20, 0, 5),
        GENTLE("Gentle", 0, 10, 0),
        STRONG("Strong", 15, 5, 0),
        SUPERIOR("Superior", 35, 20, 20),
        LEGENDARY("Legendary", 25, 10, 10),
        ANCIENT("Ancient", 30, 15, 15),
        FORCEFUL("Forceful", 5, 0, 20),
        UNPLEASANT("Unpleasant", 0, 0, 5),
        CLEAN("Clean", 0, 5, 0),
        WEIRD("Weird", 5, 5, 5),
        BIZARRE("Bizarre", 25, 0, 10),
        SPIKED("Spiked", 0, 25, 5),
        MOIL("Moil", 15, 0, 0),
        TOIL("Toil", 20, 0, 0),
        FRUITFUL("Fruitful", 0, 10, 20),
        HONORED("Honored", 10, 15, 0),
        BEJEWELED("Bejeweled", 15, 15, 0),
        RENOWNED("Renowned", 30, 10, 10),
        FESTIVE("Festive", 10, 10, 10);

        private final String displayName;
        private final int strengthBonus;
        private final int defenseBonus;
        private final int speedBonus;

        ReforgeType(String displayName, int strengthBonus, int defenseBonus, int speedBonus) {
            this.displayName = displayName;
            this.strengthBonus = strengthBonus;
            this.defenseBonus = defenseBonus;
            this.speedBonus = speedBonus;
        }

        public String getDisplayName() { return displayName; }
        public int getStrengthBonus() { return strengthBonus; }
        public int getDefenseBonus() { return defenseBonus; }
        public int getSpeedBonus() { return speedBonus; }

        public static ReforgeType fromName(String name) {
            for (ReforgeType r : values()) {
                if (r.displayName.equalsIgnoreCase(name) || r.name().equalsIgnoreCase(name)) {
                    return r;
                }
            }
            return null;
        }
    }

    /** Item categories that can be reforged at the Blacksmith. */
    public enum ReforgeItemType {
        SWORD("Sword"),
        BOW("Bow"),
        ARMOR("Armor"),
        AXE("Axe"),
        PICKAXE("Pickaxe"),
        ROD("Fishing Rod"),
        WAND("Wand");

        /** Human-readable display name shown to players. */
        public final String displayName;

        ReforgeItemType(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() { return displayName; }

        public static ReforgeItemType fromName(String name) {
            for (ReforgeItemType t : values()) {
                if (t.displayName.equalsIgnoreCase(name) || t.name().equalsIgnoreCase(name)) {
                    return t;
                }
            }
            return null;
        }
    }

    /** A reforge stone item that applies a specific reforge when used. */
    public enum ReforgeStone {
        ROUGH_RUBY("Rough Ruby", "Odd"),
        PERFECT_AMETHYST("Perfect Amethyst", "Renowned"),
        PERFECT_RUBY("Perfect Ruby", "Bejeweled"),
        PERFECT_TOPAZ("Perfect Topaz", "Toil"),
        PERFECT_SAPPHIRE("Perfect Sapphire", "Festive"),
        PERFECT_AMBER("Perfect Amber", "Fruitful"),
        PERFECT_JADE("Perfect Jade", "Bizarre"),
        ITCHY("Itchy Reforge Stone", "Itchy"),
        STORMY("Stormy Reforge Stone", "Stormy"),
        FORCEFUL("Forceful Reforge Stone", "Forceful"),
        STRONG("Strong Reforge Stone", "Strong"),
        LEGENDARY("Legendary Reforge Stone", "Legendary"),
        ANCIENT("Ancient Reforge Stone", "Ancient"),
        SUPERIOR("Superior Reforge Stone", "Superior"),
        CLEAN("Clean Reforge Stone", "Clean"),
        GENTLE("Gentle Reforge Stone", "Gentle"),
        FIERCE("Fierce Reforge Stone", "Fierce"),
        SHARP("Sharp Reforge Stone", "Sharp"),
        WEIRD("Weird Reforge Stone", "Weird"),
        UNPLEASANT("Unpleasant Reforge Stone", "Unpleasant"),
        JADERALD("Jaderald", "Bizarre"),
        LAPIS_CRYSTAL("Lapis Crystal", "Spiked"),
        JASPER_CRYSTAL("Jasper Crystal", "Moil"),
        TOPAZ_CRYSTAL("Topaz Crystal", "Toil"),
        AMBER_CRYSTAL("Amber Crystal", "Fruitful"),
        AMETHYST_CRYSTAL("Amethyst Crystal", "Honored"),
        RUBY_CRYSTAL("Ruby Crystal", "Bejeweled"),
        ONYX_CRYSTAL("Onyx Crystal", "Renowned"),
        SAPPHIRE_CRYSTAL("Sapphire Crystal", "Festive");

        private final String displayName;
        private final String reforge;

        ReforgeStone(String displayName, String reforge) {
            this.displayName = displayName;
            this.reforge = reforge;
        }

        public String getDisplayName() { return displayName; }
        public String getReforge() { return reforge; }

        public static ReforgeStone fromName(String name) {
            for (ReforgeStone s : values()) {
                if (s.displayName.equalsIgnoreCase(name) || s.name().equalsIgnoreCase(name)) {
                    return s;
                }
            }
            return null;
        }
    }

    private static final ReforgeManager INSTANCE = new ReforgeManager();

    /** Per-player active reforge. */
    private final Map<UUID, ReforgeType> playerReforges = new HashMap<>();

    private ReforgeManager() {}

    public static ReforgeManager getInstance() {
        return INSTANCE;
    }

    /**
     * Returns the active reforge for the given player, or {@link Reforge#NONE} if unset.
     *
     * @param playerId the player to look up
     * @return the player's current reforge
     */
    public ReforgeType getReforge(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        return playerReforges.getOrDefault(playerId, ReforgeType.NONE);
    }

    /**
     * Sets the active reforge for the given player.
     *
     * @param playerId the player to update
     * @param reforge  the reforge to apply; use {@link ReforgeType#NONE} to clear
     */
    public void setReforge(UUID playerId, ReforgeType reforge) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(reforge, "reforge");
        playerReforges.put(playerId, reforge);
    }

    /**
     * Clears the active reforge for the given player.
     *
     * @param playerId the player to reset
     */
    public void clearReforge(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        playerReforges.remove(playerId);
    }

    /**
     * Returns an unmodifiable view of all player reforges.
     *
     * @return map of player UUID to active {@link ReforgeType}
     */
    public Map<UUID, ReforgeType> getAllReforges() {
        return Collections.unmodifiableMap(playerReforges);
    }
}
