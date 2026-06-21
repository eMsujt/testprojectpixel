package com.skyblock.core.manager;

import com.skyblock.core.model.Rarity;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
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
        PERFECT("Perfect", 40, 25, 25),
        LUCKY("Lucky", 0, 0, 10),
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
        FESTIVE("Festive", 10, 10, 10),
        HEROIC("Heroic", 20, 15, 5),
        PURE("Pure", 15, 15, 15),
        ODD("Odd", 5, 0, 10),
        FAST("Fast", 0, 0, 25),
        FAIR("Fair", 10, 10, 10),
        EPIC("Epic", 30, 20, 15),
        STORMY("Stormy", 0, 0, 0),
        SPICY("Spicy", 10, 0, 0),
        GODLY("Godly", 15, 0, 10),
        ITCHY("Itchy", 5, 0, 0),
        BLOODY("Bloody", 10, 0, 5),
        WARPED("Warped", 20, 5, 0),
        WITHERED("Withered", 25, 15, 0),
        NECROTIC("Necrotic", 0, 20, 0),
        SPIRITUAL("Spiritual", 0, 0, 15),
        SILKY("Silky", 5, 5, 15),
        TREACHEROUS("Treacherous", 15, 10, 5),
        TITANIC("Titanic", 0, 30, 0);

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

        /** Returns this reforge's strength bonus scaled for the given item rarity. */
        public int getStrengthBonus(Rarity rarity) { return scaled(strengthBonus, rarity); }
        /** Returns this reforge's defense bonus scaled for the given item rarity. */
        public int getDefenseBonus(Rarity rarity) { return scaled(defenseBonus, rarity); }
        /** Returns this reforge's speed bonus scaled for the given item rarity. */
        public int getSpeedBonus(Rarity rarity) { return scaled(speedBonus, rarity); }

        /**
         * Per-rarity stat multiplier table, indexed by {@link Rarity#ordinal()}.
         * Higher-rarity items gain a proportionally larger bonus from the same reforge.
         */
        private static final double[] RARITY_MULTIPLIER = {
            0.5,  // COMMON
            0.7,  // UNCOMMON
            1.0,  // RARE
            1.3,  // EPIC
            1.6,  // LEGENDARY
            2.0,  // MYTHIC
            2.4,  // DIVINE
            2.4   // SPECIAL
        };

        private static int scaled(int base, Rarity rarity) {
            Objects.requireNonNull(rarity, "rarity");
            int i = rarity.ordinal();
            double mult = i < RARITY_MULTIPLIER.length
                    ? RARITY_MULTIPLIER[i]
                    : RARITY_MULTIPLIER[RARITY_MULTIPLIER.length - 1];
            return (int) Math.round(base * mult);
        }

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
        ACCESSORY("Accessory"),
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
        // entries: (displayName, reforge, strengthBase, critDamageBase, critChanceBase)
        PERFECT("Perfect Gemstone",            "Perfect",     40, 25, 25),
        FIERCE("Fierce Reforge Stone",          "Fierce",      20,  0,  5),
        LUCKY("Lucky Clover",                   "Lucky",        0,  0, 10),
        LEGENDARY("Legendary Reforge Stone",    "Legendary",   25, 10, 10),
        ANCIENT("Ancient Reforge Stone",        "Ancient",     30, 15, 15),
        SUPERIOR("Superior Reforge Stone",      "Superior",    35, 20, 20),
        STRONG("Strong Reforge Stone",          "Strong",      15,  5,  0),
        CLEAN("Clean Reforge Stone",            "Clean",        0,  5,  0),
        GENTLE("Gentle Reforge Stone",          "Gentle",       0, 10,  0),
        SHARP("Sharp Reforge Stone",            "Sharp",       10,  0,  0),
        HEROIC("Heroic Reforge Stone",          "Heroic",      20, 15,  5),
        PURE("Pure Reforge Stone",              "Pure",        15, 15, 15),
        ODD("Odd Reforge Stone",                "Odd",          5,  0, 10),
        FAST("Fast Reforge Stone",              "Fast",         0,  0, 25),
        FAIR("Fair Reforge Stone",              "Fair",        10, 10, 10),
        EPIC("Epic Reforge Stone",              "Epic",        30, 20, 15),
        STORMY("Stormy Reforge Stone",          "Stormy",       0, 20, 10),
        ITCHY("Itchy Reforge Stone",            "Itchy",        5,  0,  0),
        UNPLEASANT("Unpleasant Reforge Stone",  "Unpleasant",   0,  0,  5),
        FORCEFUL("Forceful Reforge Stone",      "Forceful",     5,  0, 20),
        WEIRD("Weird Reforge Stone",            "Weird",        5,  5,  5),
        SPIKED("Lapis Crystal",                 "Spiked",       0, 25,  5),
        MOIL("Jasper Crystal",                  "Moil",        15,  0,  0),
        TOIL("Topaz Crystal",                   "Toil",        20,  0,  0),
        FRUITFUL("Amber Crystal",               "Fruitful",     0, 10, 20),
        HONORED("Amethyst Crystal",             "Honored",     10, 15,  0),
        BEJEWELED("Ruby Crystal",               "Bejeweled",   15, 15,  0),
        RENOWNED("Onyx Crystal",                "Renowned",    30, 10, 10),
        FESTIVE("Sapphire Crystal",             "Festive",     10, 10, 10),
        BIZARRE("Jaderald",                     "Bizarre",     25,  0, 10),
        BLOODY("Bloody Reforge Stone",          "Bloody",      10,  0,  5),
        WARPED("Warped Reforge Stone",          "Warped",      20,  5,  0),
        WITHERED("Withered Reforge Stone",      "Withered",    25, 15,  0),
        NECROTIC("Necromancer's Brooch",        "Necrotic",     0, 20,  0),
        SPIRITUAL("Spiritual Artifact",         "Spiritual",    0,  0, 15),
        SILKY("Silky Reforge Stone",            "Silky",        5,  5, 15),
        TREACHEROUS("Treacherous Reforge Stone","Treacherous", 15, 10,  5),
        TITANIC("Titanic Reforge Stone",        "Titanic",      0, 30,  0);

        private final String displayName;
        private final String reforge;
        private final int strengthBase;
        private final int critDamageBase;
        private final int critChanceBase;

        private static final double[] RARITY_MULT = {
            0.5,  // COMMON
            0.7,  // UNCOMMON
            1.0,  // RARE
            1.3,  // EPIC
            1.6,  // LEGENDARY
            2.0,  // MYTHIC
            2.4,  // DIVINE
            2.4   // SPECIAL
        };

        ReforgeStone(String displayName, String reforge,
                     int strengthBase, int critDamageBase, int critChanceBase) {
            this.displayName = displayName;
            this.reforge = reforge;
            this.strengthBase = strengthBase;
            this.critDamageBase = critDamageBase;
            this.critChanceBase = critChanceBase;
        }

        public String getDisplayName() { return displayName; }
        public String getReforge() { return reforge; }

        public int getStrength(Rarity rarity) { return stoneScale(strengthBase, rarity); }
        public int getCritDamage(Rarity rarity) { return stoneScale(critDamageBase, rarity); }
        public int getCritChance(Rarity rarity) { return stoneScale(critChanceBase, rarity); }

        private static int stoneScale(int base, Rarity rarity) {
            Objects.requireNonNull(rarity, "rarity");
            int i = rarity.ordinal();
            double m = i < RARITY_MULT.length ? RARITY_MULT[i] : RARITY_MULT[RARITY_MULT.length - 1];
            return (int) Math.round(base * m);
        }

        public static ReforgeStone fromName(String name) {
            for (ReforgeStone s : values()) {
                if (s.displayName.equalsIgnoreCase(name) || s.name().equalsIgnoreCase(name)) {
                    return s;
                }
            }
            return null;
        }
    }

    /**
     * Coin cost of reforging an item at the Blacksmith anvil, indexed by
     * {@link Rarity#ordinal()}. Higher-rarity items cost more to reforge.
     */
    private static final int[] REFORGE_COST = {
        250,   // COMMON
        500,   // UNCOMMON
        1000,  // RARE
        2500,  // EPIC
        5000,  // LEGENDARY
        10000, // MYTHIC
        25000, // DIVINE
        25000  // SPECIAL
    };

    /**
     * Returns the coin cost to reforge an item of the given rarity at the anvil.
     *
     * @param rarity the item's rarity
     * @return the reforge cost in coins
     */
    public static int getReforgeCost(Rarity rarity) {
        Objects.requireNonNull(rarity, "rarity");
        int i = rarity.ordinal();
        return i < REFORGE_COST.length
                ? REFORGE_COST[i]
                : REFORGE_COST[REFORGE_COST.length - 1];
    }

    private static final ReforgeManager INSTANCE = new ReforgeManager();

    /** Per-player active reforge. */
    private final Map<UUID, ReforgeType> playerReforges = new HashMap<>();

    /** Per-player, per-slot reforge selections (slot key → ReforgeType). */
    private final Map<UUID, Map<String, ReforgeType>> slotReforges = new HashMap<>();

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

    /**
     * Returns the reforge applied to the given item slot for the player,
     * or {@link ReforgeType#NONE} if no reforge is set for that slot.
     */
    public ReforgeType getSlotReforge(UUID playerId, String slot) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(slot, "slot");
        Map<String, ReforgeType> slots = slotReforges.get(playerId);
        return slots == null ? ReforgeType.NONE : slots.getOrDefault(slot, ReforgeType.NONE);
    }

    /**
     * Sets the reforge for the given item slot. Use {@link ReforgeType#NONE} to clear.
     */
    public void setSlotReforge(UUID playerId, String slot, ReforgeType reforge) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(slot, "slot");
        Objects.requireNonNull(reforge, "reforge");
        if (reforge == ReforgeType.NONE) {
            Map<String, ReforgeType> slots = slotReforges.get(playerId);
            if (slots != null) slots.remove(slot);
        } else {
            slotReforges.computeIfAbsent(playerId, k -> new HashMap<>()).put(slot, reforge);
        }
    }

    /**
     * Applies a reforge stone to the player's active reforge, resolving the
     * stone's reforge to a {@link ReforgeType} and setting it.
     *
     * @param playerId the player to update
     * @param stone    the reforge stone being used
     * @return the applied reforge type, or {@code null} if the stone's reforge is unknown
     */
    public ReforgeType applyStone(UUID playerId, ReforgeStone stone) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(stone, "stone");
        ReforgeType type = ReforgeType.fromName(stone.getReforge());
        if (type == null) {
            return null;
        }
        setReforge(playerId, type);
        return type;
    }

    /**
     * Applies a reforge stone to the given item slot, resolving the stone's
     * reforge to a {@link ReforgeType} and setting it for that slot.
     *
     * @param playerId the player to update
     * @param slot     the item slot key
     * @param stone    the reforge stone being used
     * @return the applied reforge type, or {@code null} if the stone's reforge is unknown
     */
    public ReforgeType applyStone(UUID playerId, String slot, ReforgeStone stone) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(slot, "slot");
        Objects.requireNonNull(stone, "stone");
        ReforgeType type = ReforgeType.fromName(stone.getReforge());
        if (type == null) {
            return null;
        }
        setSlotReforge(playerId, slot, type);
        return type;
    }

    /**
     * Removes all slot reforges for the given player.
     */
    public void clearSlotReforges(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        slotReforges.remove(playerId);
    }

    public void load(File dataFolder) {
        File file = new File(dataFolder, "reforge.yml");
        if (!file.exists()) {
            return;
        }
        YamlConfiguration cfg = YamlConfiguration.loadConfiguration(file);
        playerReforges.clear();
        slotReforges.clear();
        for (String key : cfg.getKeys(false)) {
            if (key.equals("slots")) continue;
            try {
                UUID uuid = UUID.fromString(key);
                String name = cfg.getString(key);
                if (name == null) continue;
                ReforgeType type = ReforgeType.fromName(name);
                if (type != null && type != ReforgeType.NONE) {
                    playerReforges.put(uuid, type);
                }
            } catch (IllegalArgumentException ignored) {
                // skip malformed entries
            }
        }
        ConfigurationSection slotsSection = cfg.getConfigurationSection("slots");
        if (slotsSection != null) {
            for (String uuidKey : slotsSection.getKeys(false)) {
                try {
                    UUID uuid = UUID.fromString(uuidKey);
                    ConfigurationSection playerSection = slotsSection.getConfigurationSection(uuidKey);
                    if (playerSection == null) continue;
                    for (String slotKey : playerSection.getKeys(false)) {
                        String name = playerSection.getString(slotKey);
                        if (name == null) continue;
                        ReforgeType type = ReforgeType.fromName(name);
                        if (type != null && type != ReforgeType.NONE) {
                            slotReforges.computeIfAbsent(uuid, k -> new HashMap<>()).put(slotKey, type);
                        }
                    }
                } catch (IllegalArgumentException ignored) {
                    // skip malformed entries
                }
            }
        }
    }

    public void save(File dataFolder) {
        File file = new File(dataFolder, "reforge.yml");
        YamlConfiguration cfg = new YamlConfiguration();
        for (Map.Entry<UUID, ReforgeType> entry : playerReforges.entrySet()) {
            cfg.set(entry.getKey().toString(), entry.getValue().name());
        }
        for (Map.Entry<UUID, Map<String, ReforgeType>> playerEntry : slotReforges.entrySet()) {
            String uuidKey = "slots." + playerEntry.getKey().toString();
            for (Map.Entry<String, ReforgeType> slotEntry : playerEntry.getValue().entrySet()) {
                cfg.set(uuidKey + "." + slotEntry.getKey(), slotEntry.getValue().name());
            }
        }
        try {
            cfg.save(file);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save reforge.yml", e);
        }
    }
}
