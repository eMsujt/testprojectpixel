package com.skyblock.core.manager;

import com.skyblock.core.SkyBlockCore;
import com.skyblock.core.model.Rarity;
import com.skyblock.core.model.Stat;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.EnumMap;
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

    /**
     * A reforge type. Each carries a per-rarity stat table (columns Common..Mythic)
     * taken from the wiki Reforging pages; {@link #getStats(Rarity)} returns the bonuses
     * for an item of a given rarity. Legacy reforges that Hypixel removed (folded into
     * Accessory Powers) keep their prior flat Strength/Defense/Speed values.
     */
    public enum ReforgeType {
        NONE("None"), SHARP("Sharp"), FIERCE("Fierce"), GENTLE("Gentle"), STRONG("Strong"),
        SUPERIOR("Superior"), LEGENDARY("Legendary"), ANCIENT("Ancient"), FORCEFUL("Forceful"),
        UNPLEASANT("Unpleasant"), PERFECT("Perfect"), LUCKY("Lucky"), CLEAN("Clean"), WEIRD("Weird"),
        BIZARRE("Bizarre"), SPIKED("Spiked"), MOIL("Moil"), TOIL("Toil"), FRUITFUL("Fruitful"),
        HONORED("Honored"), BEJEWELED("Bejeweled"), RENOWNED("Renowned"), FESTIVE("Festive"),
        HEROIC("Heroic"), PURE("Pure"), ODD("Odd"), FAST("Fast"), FAIR("Fair"), EPIC("Epic"),
        STORMY("Stormy"), SPICY("Spicy"), GODLY("Godly"), ITCHY("Itchy"), BLOODY("Bloody"),
        WARPED("Warped"), WITHERED("Withered"), NECROTIC("Necrotic"), SPIRITUAL("Spiritual"),
        SILKY("Silky"), TREACHEROUS("Treacherous"), TITANIC("Titanic");

        private final String displayName;

        ReforgeType(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() { return displayName; }

        /** Per-reforge stat table: Stat -&gt; [Common, Uncommon, Rare, Epic, Legendary, Mythic]. */
        private static final Map<ReforgeType, Map<Stat, double[]>> STATS = new EnumMap<>(ReforgeType.class);

        static {
            // --- Reforges with current wiki per-rarity tables (verbatim values) ---
            s(SHARP,     e(Stat.CRIT_CHANCE, 10,12,14,17,20,25), e(Stat.CRIT_DAMAGE, 20,30,40,55,75,90));
            s(FIERCE,    e(Stat.STRENGTH, 2,4,6,8,10,12), e(Stat.CRIT_CHANCE, 2,3,4,5,6,8), e(Stat.CRIT_DAMAGE, 4,7,10,14,18,24));
            s(GENTLE,    e(Stat.STRENGTH, 3,5,7,10,15,20), e(Stat.ATTACK_SPEED, 8,10,15,20,25,30));
            s(ODD,       e(Stat.CRIT_CHANCE, 12,15,15,20,25,30), e(Stat.CRIT_DAMAGE, 10,15,15,22,30,40), e(Stat.INTELLIGENCE, -5,-10,-18,-32,-50,-75));
            s(FAST,      e(Stat.ATTACK_SPEED, 10,20,30,40,50,60));
            s(FAIR,      e(Stat.STRENGTH, 2,3,4,7,10,12), e(Stat.CRIT_CHANCE, 2,3,4,7,10,12), e(Stat.CRIT_DAMAGE, 2,3,4,7,10,12), e(Stat.INTELLIGENCE, 2,3,4,7,10,12), e(Stat.ATTACK_SPEED, 2,3,4,7,10,12));
            s(EPIC,      e(Stat.STRENGTH, 15,20,25,32,40,50), e(Stat.CRIT_DAMAGE, 10,15,20,27,35,45), e(Stat.ATTACK_SPEED, 1,2,4,7,10,15));
            s(HEROIC,    e(Stat.STRENGTH, 15,20,25,32,40,50), e(Stat.INTELLIGENCE, 40,50,65,80,100,125), e(Stat.ATTACK_SPEED, 1,2,2,3,5,7));
            s(SPICY,     e(Stat.STRENGTH, 2,3,4,7,10,12), e(Stat.CRIT_CHANCE, 1,1,1,1,1,1), e(Stat.CRIT_DAMAGE, 25,35,45,60,80,100), e(Stat.ATTACK_SPEED, 1,2,4,7,10,15));
            s(LEGENDARY, e(Stat.STRENGTH, 3,7,12,18,25,32), e(Stat.CRIT_CHANCE, 5,7,9,12,15,18), e(Stat.CRIT_DAMAGE, 5,10,15,22,28,36), e(Stat.INTELLIGENCE, 5,8,12,18,25,35), e(Stat.ATTACK_SPEED, 2,3,5,7,10,15));
            s(WITHERED,  e(Stat.STRENGTH, 60,75,90,110,135,170));
            s(WARPED,    e(Stat.STRENGTH, 0,0,0,165,165,165), e(Stat.INTELLIGENCE, 0,0,0,65,100,150));
            s(SPIRITUAL, e(Stat.STRENGTH, 4,8,14,20,28,38), e(Stat.CRIT_CHANCE, 7,8,9,10,12,14), e(Stat.CRIT_DAMAGE, 10,15,23,37,55,75));
            s(CLEAN,     e(Stat.HEALTH, 5,7,10,15,20,25), e(Stat.DEFENSE, 5,7,10,15,20,25), e(Stat.CRIT_CHANCE, 2,4,6,8,10,12));
            s(PURE,      e(Stat.HEALTH, 2,3,4,6,8,10), e(Stat.DEFENSE, 2,3,4,6,8,10), e(Stat.STRENGTH, 2,3,4,6,8,10), e(Stat.SPEED, 1,1,1,1,1,1), e(Stat.CRIT_CHANCE, 2,4,6,8,10,12), e(Stat.CRIT_DAMAGE, 2,3,4,6,8,8), e(Stat.ATTACK_SPEED, 1,1,2,3,4,5), e(Stat.INTELLIGENCE, 2,3,4,6,8,10));
            s(TITANIC,   e(Stat.HEALTH, 10,15,20,25,35,50), e(Stat.DEFENSE, 10,15,20,25,35,50));
            s(PERFECT,   e(Stat.DEFENSE, 25,35,50,65,80,110));
            s(NECROTIC,  e(Stat.INTELLIGENCE, 30,60,90,120,150,200));
            s(ANCIENT,   e(Stat.HEALTH, 7,7,7,7,7,7), e(Stat.DEFENSE, 7,7,7,7,7,7), e(Stat.STRENGTH, 4,8,12,18,25,35), e(Stat.CRIT_CHANCE, 3,5,7,9,12,15), e(Stat.INTELLIGENCE, 6,9,12,16,20,25));
            s(SPIKED,    e(Stat.HEALTH, 2,3,4,6,8,10), e(Stat.DEFENSE, 2,3,4,6,8,10), e(Stat.STRENGTH, 3,4,6,8,10,12), e(Stat.SPEED, 1,1,1,1,1,1), e(Stat.CRIT_CHANCE, 2,4,6,8,10,12), e(Stat.CRIT_DAMAGE, 3,4,6,8,10,12), e(Stat.ATTACK_SPEED, 1,1,2,3,4,5), e(Stat.INTELLIGENCE, 3,4,6,8,10,12));
            s(RENOWNED,  e(Stat.HEALTH, 2,3,4,6,8,10), e(Stat.DEFENSE, 2,3,4,6,8,10), e(Stat.STRENGTH, 3,4,6,8,10,12), e(Stat.SPEED, 1,1,1,1,1,1), e(Stat.CRIT_CHANCE, 2,4,6,8,10,12), e(Stat.CRIT_DAMAGE, 3,4,6,8,10,12), e(Stat.ATTACK_SPEED, 1,1,2,3,4,5), e(Stat.INTELLIGENCE, 3,4,6,8,10,12));
            s(FESTIVE,   e(Stat.SEA_CREATURE_CHANCE, 0.05,0.05,0.1,0.15,0.2,0.25), e(Stat.INTELLIGENCE, 5,10,15,20,25,30), e(Stat.FISHING_SPEED, 2,3,4,6,8,10));

            // --- Legacy reforges Hypixel removed (no current table): keep prior flat Str/Def/Speed ---
            flat(STRONG, 15, 5, 0);     flat(SUPERIOR, 35, 20, 20); flat(FORCEFUL, 5, 0, 20);
            flat(UNPLEASANT, 0, 0, 5);  flat(WEIRD, 5, 5, 5);       flat(BIZARRE, 25, 0, 10);
            flat(STORMY, 0, 0, 0);      flat(GODLY, 15, 0, 10);     flat(ITCHY, 5, 0, 0);
            flat(BLOODY, 10, 0, 5);     flat(MOIL, 15, 0, 0);       flat(TOIL, 20, 0, 0);
            flat(FRUITFUL, 0, 10, 20);  flat(HONORED, 10, 15, 0);   flat(BEJEWELED, 15, 15, 0);
            flat(SILKY, 5, 5, 15);      flat(LUCKY, 0, 0, 10);      flat(TREACHEROUS, 15, 10, 5);
        }

        private static Map.Entry<Stat, double[]> e(Stat stat, double... vals) {
            return Map.entry(stat, vals);
        }

        @SafeVarargs
        private static void s(ReforgeType type, Map.Entry<Stat, double[]>... entries) {
            Map<Stat, double[]> table = new EnumMap<>(Stat.class);
            for (Map.Entry<Stat, double[]> en : entries) table.put(en.getKey(), en.getValue());
            STATS.put(type, table);
        }

        private static void flat(ReforgeType type, double str, double def, double spd) {
            s(type, e(Stat.STRENGTH, str, str, str, str, str, str),
                    e(Stat.DEFENSE, def, def, def, def, def, def),
                    e(Stat.SPEED, spd, spd, spd, spd, spd, spd));
        }

        /** The stat bonuses this reforge grants on an item of the given rarity (zeros omitted). */
        public Map<Stat, Double> getStats(Rarity rarity) {
            Map<Stat, double[]> table = STATS.get(this);
            if (table == null) return Collections.emptyMap();
            int idx = Math.min(rarity.ordinal(), 5); // clamp Divine/Special to the Mythic column
            Map<Stat, Double> out = new EnumMap<>(Stat.class);
            for (Map.Entry<Stat, double[]> en : table.entrySet()) {
                double v = en.getValue()[idx];
                if (v != 0) out.put(en.getKey(), v);
            }
            return out;
        }

        // Backward-compatible accessors (Legendary-tier value) for the reforge menu + admin commands.
        public int getStrengthBonus() { return legendary(Stat.STRENGTH); }
        public int getDefenseBonus()  { return legendary(Stat.DEFENSE); }
        public int getSpeedBonus()    { return legendary(Stat.SPEED); }

        private int legendary(Stat stat) {
            Map<Stat, double[]> table = STATS.get(this);
            if (table == null || !table.containsKey(stat)) return 0;
            return (int) Math.round(table.get(stat)[4]);
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

    // --- Item-bound reforges (the source of truth that reaches combat) -------------------

    private static NamespacedKey reforgeKey() {
        return new NamespacedKey(SkyBlockCore.getInstance(), "reforge");
    }

    /**
     * Stamps a reforge onto the item itself (persistent data) and prefixes its display name with
     * the reforge word (e.g. {@code §dHyperion} → {@code §dFierce Hyperion}), replacing any previous
     * reforge. The stamped reforge is what {@code EquipmentListener.recompute} reads to grant stats.
     *
     * @param item the item to reforge; ignored if null or has no meta
     * @param type the reforge to apply; {@link ReforgeType#NONE} clears the reforge
     */
    public void applyReforge(ItemStack item, ReforgeType type) {
        if (item == null || type == null) {
            return;
        }
        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            return;
        }
        ReforgeType old = readReforge(meta);
        PersistentDataContainer pdc = meta.getPersistentDataContainer();
        if (type == ReforgeType.NONE) {
            pdc.remove(reforgeKey());
        } else {
            pdc.set(reforgeKey(), PersistentDataType.STRING, type.name());
        }
        if (meta.hasDisplayName()) {
            String name = meta.getDisplayName();
            if (old != ReforgeType.NONE) {
                name = removeWordAfterCodes(name, old.getDisplayName());
            }
            if (type != ReforgeType.NONE) {
                name = insertWordAfterCodes(name, type.getDisplayName());
            }
            meta.setDisplayName(name);
        }
        item.setItemMeta(meta);
    }

    /**
     * Returns the reforge stamped on the item, or {@link ReforgeType#NONE} if none.
     *
     * @param item the item to inspect; null yields {@link ReforgeType#NONE}
     * @return the item's reforge
     */
    public ReforgeType getItemReforge(ItemStack item) {
        if (item == null) {
            return ReforgeType.NONE;
        }
        ItemMeta meta = item.getItemMeta();
        return meta == null ? ReforgeType.NONE : readReforge(meta);
    }

    private static ReforgeType readReforge(ItemMeta meta) {
        String name = meta.getPersistentDataContainer().get(reforgeKey(), PersistentDataType.STRING);
        if (name == null) {
            return ReforgeType.NONE;
        }
        try {
            return ReforgeType.valueOf(name);
        } catch (IllegalArgumentException e) {
            return ReforgeType.NONE;
        }
    }

    /** Index just past any leading legacy colour/format codes (§x) in a display name. */
    private static int leadingCodesEnd(String s) {
        int i = 0;
        while (i + 1 < s.length() && s.charAt(i) == '§') {
            i += 2;
        }
        return i;
    }

    /** Inserts {@code word} (plus a space) right after the leading colour codes. */
    private static String insertWordAfterCodes(String name, String word) {
        int i = leadingCodesEnd(name);
        return name.substring(0, i) + word + " " + name.substring(i);
    }

    /** Removes a leading reforge {@code word} (plus a space) that sits right after the colour codes. */
    private static String removeWordAfterCodes(String name, String word) {
        int i = leadingCodesEnd(name);
        String rest = name.substring(i);
        if (rest.regionMatches(true, 0, word + " ", 0, word.length() + 1)) {
            rest = rest.substring(word.length() + 1);
        }
        return name.substring(0, i) + rest;
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
