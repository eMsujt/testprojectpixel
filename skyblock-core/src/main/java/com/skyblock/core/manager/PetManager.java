package com.skyblock.core.manager;

import com.skyblock.core.model.Rarity;
import com.skyblock.core.model.Stat;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * Canonical singleton managing per-player pet collections, active pets, and XP.
 *
 * <p>All other PetManager/PetsManager classes in this project are deprecated
 * stubs that delegate here. Not thread-safe; access from the server main
 * thread or guard externally.</p>
 */
public final class PetManager {

    /** The skill category a pet is associated with, used to group pets in menus. */
    public enum PetCategory {
        COMBAT("Combat"),
        MINING("Mining"),
        FARMING("Farming"),
        FISHING("Fishing"),
        FORAGING("Foraging"),
        ALCHEMY("Alchemy");

        private final String displayName;

        PetCategory(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    /** Every pet type available in SkyBlock with its default rarity, display name, and skill category. */
    public enum PetType {
        // Common
        CHICKEN(Rarity.COMMON, "Chicken", PetCategory.FARMING),
        WORM(Rarity.COMMON, "Worm", PetCategory.FISHING),
        SHEEP(Rarity.COMMON, "Sheep", PetCategory.ALCHEMY),
        PIG(Rarity.COMMON, "Pig", PetCategory.FARMING),
        COW(Rarity.COMMON, "Cow", PetCategory.FARMING),
        BAT(Rarity.COMMON, "Bat", PetCategory.MINING),
        SNAIL(Rarity.COMMON, "Snail", PetCategory.MINING),
        ROCK(Rarity.COMMON, "Rock", PetCategory.MINING),
        MOSQUITO(Rarity.COMMON, "Mosquito", PetCategory.FARMING),
        SLUG(Rarity.COMMON, "Slug", PetCategory.FARMING),
        // Uncommon
        RABBIT(Rarity.UNCOMMON, "Rabbit", PetCategory.FARMING),
        PENGUIN(Rarity.UNCOMMON, "Penguin", PetCategory.FISHING),
        HORSE(Rarity.UNCOMMON, "Horse", PetCategory.FORAGING),
        MULE(Rarity.UNCOMMON, "Mule", PetCategory.FORAGING),
        DONKEY(Rarity.UNCOMMON, "Donkey", PetCategory.FORAGING),
        GOAT(Rarity.UNCOMMON, "Goat", PetCategory.MINING),
        SILVERFISH(Rarity.UNCOMMON, "Silverfish", PetCategory.MINING),
        CAVE_SPIDER(Rarity.UNCOMMON, "Cave Spider", PetCategory.COMBAT),
        MOOSHROOM_COW(Rarity.UNCOMMON, "Mooshroom Cow", PetCategory.FARMING),
        HOUND(Rarity.UNCOMMON, "Hound", PetCategory.COMBAT),
        // Rare
        BEE(Rarity.RARE, "Bee", PetCategory.FARMING),
        DOG(Rarity.RARE, "Dog", PetCategory.MINING),
        DOLPHIN(Rarity.RARE, "Dolphin", PetCategory.FISHING),
        SQUID(Rarity.RARE, "Squid", PetCategory.FISHING),
        FLYING_FISH(Rarity.RARE, "Flying Fish", PetCategory.FISHING),
        CAT(Rarity.RARE, "Cat", PetCategory.COMBAT),
        PARROT(Rarity.RARE, "Parrot", PetCategory.ALCHEMY),
        MONKEY(Rarity.RARE, "Monkey", PetCategory.FORAGING),
        GUARDIAN(Rarity.RARE, "Guardian", PetCategory.FISHING),
        OCELOT(Rarity.RARE, "Ocelot", PetCategory.FORAGING),
        TURTLE(Rarity.RARE, "Turtle", PetCategory.FISHING),
        ELEPHANT(Rarity.RARE, "Elephant", PetCategory.FARMING),
        GIRAFFE(Rarity.RARE, "Giraffe", PetCategory.FORAGING),
        LION(Rarity.RARE, "Lion", PetCategory.FORAGING),
        CREEPER(Rarity.RARE, "Creeper", PetCategory.COMBAT),
        ZOMBIE(Rarity.RARE, "Zombie", PetCategory.COMBAT),
        SKELETON(Rarity.RARE, "Skeleton", PetCategory.COMBAT),
        SPIDER(Rarity.RARE, "Spider", PetCategory.COMBAT),
        ENDERMITE(Rarity.RARE, "Endermite", PetCategory.COMBAT),
        PIGMAN(Rarity.RARE, "Pigman", PetCategory.COMBAT),
        JELLYFISH(Rarity.RARE, "Jellyfish", PetCategory.FISHING),
        SCARECROW(Rarity.RARE, "Scarecrow", PetCategory.FARMING),
        MITHRIL_GOLEM(Rarity.RARE, "Mithril Golem", PetCategory.MINING),
        // Epic
        WOLF(Rarity.EPIC, "Wolf", PetCategory.COMBAT),
        BLAZE(Rarity.EPIC, "Blaze", PetCategory.COMBAT),
        MAGMA_CUBE(Rarity.EPIC, "Magma Cube", PetCategory.COMBAT),
        ENDERMAN(Rarity.EPIC, "Enderman", PetCategory.COMBAT),
        GHAST(Rarity.EPIC, "Ghast", PetCategory.COMBAT),
        GOLEM(Rarity.EPIC, "Golem", PetCategory.COMBAT),
        WITHER_SKELETON(Rarity.EPIC, "Wither Skeleton", PetCategory.COMBAT),
        TARANTULA(Rarity.EPIC, "Tarantula", PetCategory.COMBAT),
        BABY_YETI(Rarity.EPIC, "Baby Yeti", PetCategory.COMBAT),
        BLUE_WHALE(Rarity.EPIC, "Blue Whale", PetCategory.FISHING),
        TIGER(Rarity.EPIC, "Tiger", PetCategory.COMBAT),
        WISP(Rarity.EPIC, "Wisp", PetCategory.COMBAT),
        SNOWMAN(Rarity.EPIC, "Snowman", PetCategory.COMBAT),
        ARMADILLO(Rarity.EPIC, "Armadillo", PetCategory.MINING),
        AMMONITE(Rarity.EPIC, "Ammonite", PetCategory.FISHING),
        SPINOCLAW(Rarity.EPIC, "Spinoclaw", PetCategory.FORAGING),
        GHOUL(Rarity.EPIC, "Ghoul", PetCategory.COMBAT),
        DROPLET_WISP(Rarity.EPIC, "Droplet Wisp", PetCategory.COMBAT),
        HEDGEHOG(Rarity.EPIC, "Hedgehog", PetCategory.FARMING),
        SUMO(Rarity.EPIC, "Sumo", PetCategory.COMBAT),
        // Legendary
        GRIFFIN(Rarity.LEGENDARY, "Griffin", PetCategory.COMBAT),
        GOLDEN_DRAGON(Rarity.LEGENDARY, "Golden Dragon", PetCategory.COMBAT),
        BLUE_SHARK(Rarity.LEGENDARY, "Blue Shark", PetCategory.FISHING),
        JERRY(Rarity.LEGENDARY, "Jerry", PetCategory.COMBAT),
        BLACK_CAT(Rarity.LEGENDARY, "Black Cat", PetCategory.COMBAT),
        GRANDMA_WOLF(Rarity.LEGENDARY, "Grandma Wolf", PetCategory.COMBAT),
        ENDER_DRAGON(Rarity.LEGENDARY, "Ender Dragon", PetCategory.COMBAT),
        PHOENIX(Rarity.LEGENDARY, "Phoenix", PetCategory.COMBAT);

        /** Default rarity of this pet type. Individual instances may override it. */
        public final Rarity defaultRarity;
        private final String displayName;
        /** The skill category this pet belongs to. */
        public final PetCategory category;
        /** The primary stat this pet grants, scaling with its level. */
        public final PetStatBonus statBonus;

        PetType(Rarity defaultRarity, String displayName, PetCategory category) {
            this.defaultRarity = defaultRarity;
            this.displayName = displayName;
            this.category = category;
            this.statBonus = statBonusFor(category);
        }

        public String getDisplayName() {
            return displayName;
        }

        public PetCategory getCategory() {
            return category;
        }

        /** Returns this pet's primary stat bonus, granted in proportion to its level. */
        public PetStatBonus getStatBonus() {
            return statBonus;
        }
    }

    /** A pet's primary stat bonus: a {@link Stat} granted at {@code bonusPerLevel} per pet level. */
    public record PetStatBonus(Stat stat, double bonusPerLevel) {
    }

    /** Maps a pet's skill category to the primary stat its level grants. */
    private static PetStatBonus statBonusFor(PetCategory category) {
        return switch (category) {
            case COMBAT -> new PetStatBonus(Stat.STRENGTH, 0.5);
            case MINING -> new PetStatBonus(Stat.MINING_SPEED, 0.5);
            case FARMING -> new PetStatBonus(Stat.FARMING_FORTUNE, 0.5);
            case FISHING -> new PetStatBonus(Stat.SEA_CREATURE_CHANCE, 0.25);
            case FORAGING -> new PetStatBonus(Stat.FORAGING_FORTUNE, 0.5);
            case ALCHEMY -> new PetStatBonus(Stat.INTELLIGENCE, 1.0);
        };
    }

    /** The highest level a pet can reach. */
    public static final int MAX_LEVEL = 100;

    /**
     * Cumulative XP required to reach each level (index 0 = level 1 → 2) per rarity.
     * Values sourced from the SkyBlock wiki pet XP tables.
     */
    public static final Map<String, long[]> PET_XP_TABLE;

    static {
        Map<String, long[]> m = new HashMap<>();
        m.put("COMMON", new long[]{
            100, 210, 330, 460, 600, 750, 910, 1080, 1260, 1450,
            1650, 1860, 2080, 2310, 2550, 2800, 3060, 3330, 3610, 3900,
            4200, 4510, 4830, 5160, 5500, 5850, 6210, 6580, 6960, 7350,
            7750, 8160, 8580, 9010, 9450, 9900, 10360, 10830, 11310, 11800,
            12300, 12810, 13330, 13860, 14400, 14950, 15510, 16080, 16660, 17250,
            17850, 18460, 19080, 19710, 20350, 21000, 21660, 22330, 23010, 23700,
            24400, 25110, 25830, 26560, 27300, 28050, 28810, 29580, 30360, 31150,
            31950, 32760, 33580, 34410, 35250, 36100, 36960, 37830, 38710, 39600,
            40500, 41410, 42330, 43260, 44200, 45150, 46110, 47080, 48060, 49050,
            50050, 51060, 52080, 53110, 54150, 55200, 56260, 57330, 58410, 59500
        });
        m.put("UNCOMMON", new long[]{
            175, 368, 578, 808, 1058, 1330, 1624, 1940, 2278, 2640,
            3025, 3433, 3864, 4318, 4796, 5298, 5824, 6375, 6950, 7550,
            8175, 8825, 9500, 10200, 10925, 11675, 12450, 13250, 14075, 14925,
            15800, 16700, 17625, 18575, 19550, 20550, 21575, 22625, 23700, 24800,
            25925, 27075, 28250, 29450, 30675, 31925, 33200, 34500, 35825, 37175,
            38550, 39950, 41375, 42825, 44300, 45800, 47325, 48875, 50450, 52050,
            53675, 55325, 57000, 58700, 60425, 62175, 63950, 65750, 67575, 69425,
            71300, 73200, 75125, 77075, 79050, 81050, 83075, 85125, 87200, 89300,
            91425, 93575, 95750, 97950, 100175, 102425, 104700, 107000, 109325, 111675,
            114050, 116450, 118875, 121325, 123800, 126300, 128825, 131375, 133950, 136550
        });
        m.put("RARE", new long[]{
            275, 578, 908, 1268, 1658, 2083, 2543, 3040, 3575, 4150,
            4765, 5421, 6119, 6860, 7645, 8475, 9350, 10271, 11239, 12255,
            13320, 14435, 15601, 16819, 18090, 19415, 20795, 22231, 23724, 25275,
            26885, 28555, 30286, 32079, 33935, 35855, 37840, 39891, 42009, 44195,
            46450, 48775, 51171, 53639, 56180, 58795, 61485, 64251, 67094, 70015,
            73015, 76096, 79259, 82505, 85835, 89251, 92754, 96346, 100029, 103803,
            107670, 111632, 115691, 119849, 124108, 128470, 132936, 137509, 142191, 146984,
            151891, 156914, 162056, 167319, 172705, 178216, 183856, 189627, 195531, 201571,
            207750, 214069, 220531, 227139, 233896, 240804, 247866, 255085, 262464, 270006,
            277714, 285591, 293641, 301867, 310273, 318862, 327637, 336601, 345758, 355112
        });
        m.put("EPIC", new long[]{
            440, 925, 1455, 2035, 2665, 3350, 4095, 4903, 5775, 6715,
            7726, 8811, 9973, 11215, 12540, 13951, 15451, 17043, 18731, 20519,
            22410, 24408, 26517, 28741, 31085, 33553, 36150, 38880, 41748, 44759,
            47917, 51228, 54697, 58330, 62132, 66109, 70267, 74612, 79150, 83888,
            88831, 93986, 99360, 104960, 110793, 116866, 123186, 129761, 136599, 143708,
            151096, 158770, 166739, 175011, 183594, 192496, 201726, 211292, 221203, 231468,
            242096, 253096, 264478, 276250, 288422, 301003, 314003, 327430, 341295, 355607,
            370375, 385610, 401322, 417521, 434218, 451423, 469146, 487398, 506190, 525533,
            545438, 565916, 586979, 608639, 630907, 653795, 677315, 701479, 726299, 751788,
            777958, 804821, 832391, 860681, 889705, 919477, 950011, 981321, 1013421, 1046325
        });
        m.put("LEGENDARY", new long[]{
            660, 1388, 2183, 3053, 4003, 5038, 6163, 7383, 8703, 10128,
            11663, 13313, 15083, 16978, 19003, 21163, 23463, 25908, 28503, 31253,
            34163, 37238, 40483, 43903, 47503, 51288, 55263, 59433, 63803, 68378,
            73163, 78163, 83383, 88828, 94503, 100413, 106563, 112958, 119603, 126503,
            133663, 141088, 148783, 156753, 165003, 173538, 182363, 191483, 200903, 210628,
            220663, 231013, 241683, 252678, 264003, 275663, 287663, 300008, 312703, 325753,
            339163, 352938, 367083, 381603, 396503, 411788, 427463, 443533, 460003, 476878,
            494163, 511863, 529983, 548528, 567503, 586913, 606763, 627058, 647803, 669003,
            690663, 712788, 735383, 758453, 782003, 806038, 830563, 855583, 881103, 907128,
            933663, 960713, 988283, 1016378, 1045003, 1074163, 1103863, 1134108, 1164903, 1196253
        });
        PET_XP_TABLE = Collections.unmodifiableMap(m);
    }

    /**
     * Static base-stat metadata for each SkyBlock pet.
     * int[] layout: {rarity_ordinal, speed_bonus, strength_bonus, health_bonus}
     */
    public static final Map<String, int[]> PET_DATA;

    static {
        Map<String, int[]> m = new HashMap<>();
        // COMMON
        m.put("CHICKEN",      new int[]{0,  0,  0,  50});
        m.put("PIG",          new int[]{0,  5,  0,  30});
        m.put("RABBIT",       new int[]{0,  0,  0,  40});
        // UNCOMMON
        m.put("CAT",          new int[]{1,  0, 10,  60});
        m.put("DOG",          new int[]{1, 10, 15,  70});
        m.put("SHEEP",        new int[]{1,  0,  0,  80});
        m.put("SKELETON",     new int[]{1,  0, 15,  50});
        m.put("SPIDER",       new int[]{1,  0, 20,  50});
        m.put("ZOMBIE",       new int[]{1,  0, 25,  70});
        // RARE
        m.put("ARMADILLO",    new int[]{2,  0,  5, 120});
        m.put("ELEPHANT",     new int[]{2,  0,  0, 100});
        m.put("HORSE",        new int[]{2, 35,  0,  80});
        m.put("PARROT",       new int[]{2,  0,  0,  60});
        // EPIC
        m.put("BEE",          new int[]{3,  0,  0,  80});
        m.put("DOLPHIN",      new int[]{3, 10, 30,  90});
        m.put("JELLYFISH",    new int[]{3,  0,  0, 150});
        m.put("PENGUIN",      new int[]{3,  0,  0, 120});
        m.put("TURTLE",       new int[]{3,  0,  0, 200});
        // LEGENDARY
        m.put("BLAZE",        new int[]{4,  0, 40, 100});
        m.put("BLUE_WHALE",   new int[]{4,  0,  0, 300});
        m.put("ENDER_DRAGON", new int[]{4,  0, 50, 200});
        m.put("ENDERMAN",     new int[]{4,  0, 30, 150});
        m.put("GRIFFIN",      new int[]{4,  0, 35, 120});
        m.put("LION",         new int[]{4, 25, 50, 120});
        m.put("ROCK",         new int[]{4,  0,  0, 500});
        m.put("TIGER",        new int[]{4,  0, 60, 100});
        m.put("WOLF",         new int[]{4,  0, 45, 150});
        PET_DATA = Collections.unmodifiableMap(m);
    }

    /** Whether an ability applies while the pet is summoned ({@code ACTIVE}) or merely held in the menu ({@code HELD}). */
    public enum AbilityKind { ACTIVE, HELD }

    /** A single pet ability, unlocked once the pet reaches {@link #unlockLevel}. */
    public static final class PetAbility {
        public final String name;
        public final String description;
        public final AbilityKind kind;
        public final int unlockLevel;

        public PetAbility(String name, String description, AbilityKind kind, int unlockLevel) {
            this.name = Objects.requireNonNull(name, "name");
            this.description = Objects.requireNonNull(description, "description");
            this.kind = Objects.requireNonNull(kind, "kind");
            this.unlockLevel = unlockLevel;
        }
    }

    /** Abilities granted by each pet type, ordered by unlock level. Pets absent here have no abilities. */
    public static final Map<PetType, List<PetAbility>> PET_ABILITIES;

    static {
        Map<PetType, List<PetAbility>> m = new EnumMap<>(PetType.class);
        m.put(PetType.CHICKEN, List.of(
            new PetAbility("Light Feet", "Reduces fall damage taken", AbilityKind.ACTIVE, 1),
            new PetAbility("Eggstra", "Chance to drop an extra egg when killing chickens", AbilityKind.HELD, 10)));
        m.put(PetType.PIG, List.of(
            new PetAbility("Ridable", "Right-click to ride your pig", AbilityKind.ACTIVE, 1)));
        m.put(PetType.RABBIT, List.of(
            new PetAbility("Happy Feet", "Jump potions also grant farming fortune", AbilityKind.ACTIVE, 1)));
        m.put(PetType.CAT, List.of(
            new PetAbility("Hunter", "Increases speed against animals", AbilityKind.ACTIVE, 1),
            new PetAbility("Nine Lives", "Reduces fall and combat damage taken", AbilityKind.HELD, 10)));
        m.put(PetType.DOG, List.of(
            new PetAbility("Finder", "Increases speed and magic find", AbilityKind.ACTIVE, 1)));
        m.put(PetType.SHEEP, List.of(
            new PetAbility("Mana Saver", "Reduces ability mana cost", AbilityKind.ACTIVE, 1)));
        m.put(PetType.SKELETON, List.of(
            new PetAbility("Bone Zone", "Increases bow damage", AbilityKind.ACTIVE, 1)));
        m.put(PetType.SPIDER, List.of(
            new PetAbility("One With The Spider", "Gain strength per nearby spider", AbilityKind.ACTIVE, 1)));
        m.put(PetType.ZOMBIE, List.of(
            new PetAbility("Chomp", "Heal on melee hits", AbilityKind.ACTIVE, 1),
            new PetAbility("Rotten Blade", "Increases damage against zombies", AbilityKind.HELD, 10)));
        m.put(PetType.ARMADILLO, List.of(
            new PetAbility("Mobile Tank", "Gain defense while moving on the mining island", AbilityKind.ACTIVE, 1)));
        m.put(PetType.ELEPHANT, List.of(
            new PetAbility("Stomp", "Gain farming fortune per defense", AbilityKind.ACTIVE, 1)));
        m.put(PetType.HORSE, List.of(
            new PetAbility("Run", "Increases movement speed", AbilityKind.ACTIVE, 1),
            new PetAbility("Ride Into Battle", "Speed boost after riding", AbilityKind.HELD, 10)));
        m.put(PetType.PARROT, List.of(
            new PetAbility("Flamboyant", "Adds levels to active potion effects", AbilityKind.ACTIVE, 1)));
        m.put(PetType.BEE, List.of(
            new PetAbility("Busy Buzz Buzz", "Chance to spawn a beehive when farming", AbilityKind.ACTIVE, 1),
            new PetAbility("Hive", "Gain intelligence and strength per nearby bee", AbilityKind.HELD, 10)));
        m.put(PetType.DOLPHIN, List.of(
            new PetAbility("Pod Tactics", "Increases sea creature chance per nearby player", AbilityKind.ACTIVE, 1)));
        m.put(PetType.JELLYFISH, List.of(
            new PetAbility("Radiant Scarf", "Increases healing potency", AbilityKind.ACTIVE, 1)));
        m.put(PetType.PENGUIN, List.of(
            new PetAbility("Frozen Wave", "Increases fishing speed in cold water", AbilityKind.ACTIVE, 1)));
        m.put(PetType.TURTLE, List.of(
            new PetAbility("Turtle Tactics", "Increases defense", AbilityKind.ACTIVE, 1),
            new PetAbility("Genius Amniote", "Reflects a portion of melee damage", AbilityKind.HELD, 10)));
        m.put(PetType.BLAZE, List.of(
            new PetAbility("Nether Embodiment", "Boosts stats while on the Blazing Fortress", AbilityKind.ACTIVE, 1)));
        m.put(PetType.BLUE_WHALE, List.of(
            new PetAbility("Ingestion", "Increases health from healing potions", AbilityKind.ACTIVE, 1),
            new PetAbility("Bulk", "Gain max health based on missing health", AbilityKind.HELD, 10)));
        m.put(PetType.ENDER_DRAGON, List.of(
            new PetAbility("End Strike", "Increases damage dealt to End mobs", AbilityKind.ACTIVE, 1),
            new PetAbility("Superior", "Boosts all stats by a percentage", AbilityKind.HELD, 10)));
        m.put(PetType.ENDERMAN, List.of(
            new PetAbility("Enderian", "Take reduced damage from End mobs", AbilityKind.ACTIVE, 1),
            new PetAbility("Zealot Madness", "Increases combat XP against Zealots", AbilityKind.HELD, 10)));
        m.put(PetType.GRIFFIN, List.of(
            new PetAbility("Odyssey", "Gain stats per magic find", AbilityKind.ACTIVE, 1),
            new PetAbility("King of Bloodhounds", "Increases damage on mythological kills", AbilityKind.HELD, 10)));
        m.put(PetType.LION, List.of(
            new PetAbility("Primal Force", "Increases strength near low-health mobs", AbilityKind.ACTIVE, 1)));
        m.put(PetType.ROCK, List.of(
            new PetAbility("Fortify", "Increases defense while sitting still", AbilityKind.ACTIVE, 1),
            new PetAbility("Sailing Stone", "Move faster while sneaking", AbilityKind.HELD, 10)));
        m.put(PetType.TIGER, List.of(
            new PetAbility("Merciless Swipe", "Increases ferocity", AbilityKind.ACTIVE, 1)));
        m.put(PetType.WOLF, List.of(
            new PetAbility("Alpha Dog", "Reduces damage taken from wolves", AbilityKind.ACTIVE, 1),
            new PetAbility("Pack Leader", "Gain crit damage per nearby wolf", AbilityKind.HELD, 10)));
        PET_ABILITIES = Collections.unmodifiableMap(m);
    }

    /**
     * Items a pet can hold for a flat stat bonus.
     * int layout mirrors {@link #PET_DATA}: speed, strength, health.
     */
    public enum PetItem {
        NONE("None", 0, 0, 0),
        IRON_CLAWS("Iron Claws", 0, 10, 0),
        SHARPENED_CLAWS("Sharpened Claws", 0, 15, 0),
        QUICK_CLAW("Quick Claw", 25, 0, 0),
        SADDLE("Saddle", 20, 0, 0),
        YETI_FOOT("Yeti Foot", 10, 0, 0),
        HARDENED_SCALES("Hardened Scales", 0, 0, 25),
        BIG_TEDDY("Big Teddy", 0, 0, 75),
        BUBBLEGUM("Bubblegum", 0, 0, 50),
        DWARF_TURTLE_SHELMET("Dwarf Turtle Shelmet", 0, 0, 100);

        private final String displayName;
        public final int speedBonus;
        public final int strengthBonus;
        public final int healthBonus;

        PetItem(String displayName, int speedBonus, int strengthBonus, int healthBonus) {
            this.displayName = displayName;
            this.speedBonus = speedBonus;
            this.strengthBonus = strengthBonus;
            this.healthBonus = healthBonus;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    /** A single owned pet instance. */
    public static final class Pet {
        public final UUID id;
        public final PetType type;
        public final Rarity rarity;
        /** Number of pet candies consumed by this pet (capped at 10 in SkyBlock). */
        public int candyUsed;

        public Pet(UUID id, PetType type, Rarity rarity) {
            this.id = Objects.requireNonNull(id, "id");
            this.type = Objects.requireNonNull(type, "type");
            this.rarity = Objects.requireNonNull(rarity, "rarity");
        }

        public Pet(PetType type, Rarity rarity) {
            this(UUID.randomUUID(), type, rarity);
        }
    }

    /** Immutable snapshot of a pet's experience and computed level. */
    public static final class PetData {
        public final PetType type;
        public final Rarity rarity;
        public final long experience;

        public PetData(PetType type, Rarity rarity, long experience) {
            this.type = Objects.requireNonNull(type, "type");
            this.rarity = Objects.requireNonNull(rarity, "rarity");
            this.experience = experience;
        }

        /** Returns the pet's current level, between 1 and {@link #MAX_LEVEL}, scaled by rarity. */
        public int getLevel() {
            return computeLevel(experience, rarity);
        }
    }

    private static final PetManager INSTANCE = new PetManager();

    /** All pets owned by each player, keyed by pet UUID. */
    private final Map<UUID, Map<UUID, Pet>> playerPets = new HashMap<>();

    /** Currently equipped pet UUID per player. */
    private final Map<UUID, UUID> equippedPets = new HashMap<>();

    /** Per-player XP data keyed by pet type. */
    private final Map<UUID, Map<PetType, Long>> petExperience = new HashMap<>();

    /** Held item per owned pet, keyed by player then pet UUID. */
    private final Map<UUID, Map<UUID, PetItem>> petHeldItems = new HashMap<>();

    /** Total XP of the player's currently active pet, keyed by player UUID. */
    private final Map<UUID, Long> petXp = new HashMap<>();

    /** Cached level of the player's currently active pet, keyed by player UUID. */
    private final Map<UUID, Integer> petLevel = new HashMap<>();

    /** Event history per player. */
    private final Map<UUID, List<String>> petHistory = new HashMap<>();

    private PetManager() {
    }

    /** Returns the single shared {@code PetManager} instance. */
    public static PetManager getInstance() {
        return INSTANCE;
    }

    /**
     * Adds a new pet to the player's collection.
     *
     * @param playerId the player receiving the pet
     * @param type     the type of pet
     * @param rarity   the pet's rarity
     * @return the newly created {@link Pet}
     */
    public Pet addPet(UUID playerId, PetType type, Rarity rarity) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(type, "type");
        Objects.requireNonNull(rarity, "rarity");
        Pet pet = new Pet(type, rarity);
        playerPets.computeIfAbsent(playerId, k -> new HashMap<>()).put(pet.id, pet);
        return pet;
    }

    /**
     * Removes a pet from the player's collection, unequipping it first if active.
     *
     * @return {@code true} if the pet existed and was removed
     */
    public boolean removePet(UUID playerId, UUID petId) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(petId, "petId");
        Map<UUID, Pet> collection = playerPets.get(playerId);
        if (collection == null || collection.remove(petId) == null) {
            return false;
        }
        if (petId.equals(equippedPets.get(playerId))) {
            equippedPets.remove(playerId);
        }
        Map<UUID, PetItem> held = petHeldItems.get(playerId);
        if (held != null) {
            held.remove(petId);
        }
        return true;
    }

    /**
     * Equips the given pet for the player, replacing any previously equipped pet.
     *
     * @return {@code true} if the pet was found in the player's collection and equipped
     */
    public boolean equipPet(UUID playerId, UUID petId) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(petId, "petId");
        Map<UUID, Pet> collection = playerPets.get(playerId);
        if (collection == null || !collection.containsKey(petId)) {
            return false;
        }
        equippedPets.put(playerId, petId);
        Pet pet = collection.get(petId);
        recordPetEvent(playerId, "Equipped pet " + pet.type.name() + " (" + pet.rarity.name() + ")");
        refreshPetBonus(playerId);
        return true;
    }

    /**
     * Unequips the player's active pet without removing it from the collection.
     *
     * @return {@code true} if the player had an active pet
     */
    public boolean unequipPet(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        boolean had = equippedPets.remove(playerId) != null;
        refreshPetBonus(playerId);
        return had;
    }

    /** Tracks the stat bonus currently applied for the player's active pet, for exact removal. */
    private final Map<UUID, AppliedPetBonus> appliedPetBonuses = new HashMap<>();

    private record AppliedPetBonus(Stat stat, double amount) {}

    /** Re-applies the active pet's level-scaled stat bonus, removing any previously applied one. */
    private void refreshPetBonus(UUID playerId) {
        AppliedPetBonus previous = appliedPetBonuses.remove(playerId);
        if (previous != null) {
            StatManager.getInstance().addBonus(playerId, previous.stat(), -previous.amount());
        }
        Pet pet = getActivePet(playerId);
        if (pet == null) {
            return;
        }
        PetStatBonus bonus = pet.type.getStatBonus();
        double amount = bonus.bonusPerLevel() * getLevel(playerId, pet.type);
        if (amount == 0.0) {
            return;
        }
        StatManager.getInstance().addBonus(playerId, bonus.stat(), amount);
        appliedPetBonuses.put(playerId, new AppliedPetBonus(bonus.stat(), amount));
    }

    /** Returns the UUID of the player's currently equipped pet, or {@code null} if none. */
    public UUID getActivePetId(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        return equippedPets.get(playerId);
    }

    /** Returns the player's currently equipped {@link Pet}, or {@code null} if none. */
    public Pet getActivePet(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        UUID petId = equippedPets.get(playerId);
        if (petId == null) {
            return null;
        }
        Map<UUID, Pet> collection = playerPets.get(playerId);
        return collection == null ? null : collection.get(petId);
    }

    /**
     * Adds XP to the player's currently active pet. If no pet is equipped the call is a no-op.
     * Updates {@code petXp} and recomputes {@code petLevel} from the pet's rarity XP table.
     *
     * @param playerId the player gaining experience
     * @param amount   XP to add, must not be negative
     * @return the active pet's new cumulative XP, or -1 if no pet is equipped
     */
    public long addPetXp(UUID playerId, long amount) {
        Objects.requireNonNull(playerId, "playerId");
        if (amount < 0) {
            throw new IllegalArgumentException("amount must not be negative, got " + amount);
        }
        Pet active = getActivePet(playerId);
        if (active == null) {
            return -1L;
        }
        long prev = petXp.getOrDefault(playerId, 0L);
        long total = prev + amount;
        petXp.put(playerId, total);
        int lvlBefore = petLevel.getOrDefault(playerId, 1);
        int lvlAfter = computeLevel(total, active.rarity);
        petLevel.put(playerId, lvlAfter);
        if (lvlAfter > lvlBefore) {
            recordPetEvent(playerId, "Active pet " + active.type.name() + " leveled up to " + lvlAfter);
        }
        return total;
    }

    /** Returns the cumulative XP of the player's currently active pet (0 if none or no XP yet). */
    public long getPetXp(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        return petXp.getOrDefault(playerId, 0L);
    }

    /** Returns the cached level of the player's currently active pet (1 if none or no XP yet). */
    public int getPetLevel(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        return petLevel.getOrDefault(playerId, 1);
    }

    /** Returns an unmodifiable list of all pets owned by the player. */
    public List<Pet> getPets(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        Map<UUID, Pet> collection = playerPets.get(playerId);
        if (collection == null || collection.isEmpty()) {
            return Collections.emptyList();
        }
        return Collections.unmodifiableList(new ArrayList<>(collection.values()));
    }

    /**
     * Adds experience to the given pet type for a player, capped at MAX_LEVEL.
     *
     * @param playerId the player gaining experience
     * @param type     the pet type receiving XP
     * @param amount   the amount to add, must not be negative
     * @return the player's new total XP for that pet type
     * @throws IllegalArgumentException if {@code amount} is negative
     */
    public long addExperience(UUID playerId, PetType type, long amount) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(type, "type");
        if (amount < 0) {
            throw new IllegalArgumentException("amount must not be negative, got " + amount);
        }
        Map<PetType, Long> xpMap = petExperience.computeIfAbsent(
                playerId, id -> new EnumMap<>(PetType.class));
        long prev = xpMap.getOrDefault(type, 0L);
        long total = prev + amount;
        xpMap.put(type, total);
        int levelBefore = computeLevel(prev, type.defaultRarity);
        int levelAfter = computeLevel(total, type.defaultRarity);
        if (levelAfter > levelBefore) {
            recordPetEvent(playerId, "Pet " + type.name() + " leveled up to " + levelAfter);
        }
        return total;
    }

    /** Returns the total experience the player has for the given pet type. */
    public long getExperience(UUID playerId, PetType type) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(type, "type");
        Map<PetType, Long> xpMap = petExperience.get(playerId);
        return xpMap == null ? 0L : xpMap.getOrDefault(type, 0L);
    }

    /** Returns the current level for the player's given pet type (1–{@link #MAX_LEVEL}), scaled by the pet's default rarity. */
    public int getLevel(UUID playerId, PetType type) {
        Objects.requireNonNull(type, "type");
        return computeLevel(getExperience(playerId, type), type.defaultRarity);
    }

    /** Returns all pet types belonging to the given category, in enum declaration order. */
    public static List<PetType> getTypesByCategory(PetCategory category) {
        Objects.requireNonNull(category, "category");
        List<PetType> types = new ArrayList<>();
        for (PetType type : PetType.values()) {
            if (type.category == category) {
                types.add(type);
            }
        }
        return Collections.unmodifiableList(types);
    }

    /** Returns all abilities defined for the given pet type, in unlock order (empty if the pet has none). */
    public static List<PetAbility> getAbilities(PetType type) {
        Objects.requireNonNull(type, "type");
        return PET_ABILITIES.getOrDefault(type, Collections.emptyList());
    }

    /**
     * Returns the abilities currently unlocked for the player's pet of the given type, based on its level.
     *
     * @param kind {@code ACTIVE} for abilities that apply while summoned, {@code HELD} for menu-held abilities,
     *             or {@code null} to include both kinds
     */
    public List<PetAbility> getUnlockedAbilities(UUID playerId, PetType type, AbilityKind kind) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(type, "type");
        List<PetAbility> all = PET_ABILITIES.get(type);
        if (all == null || all.isEmpty()) {
            return Collections.emptyList();
        }
        int level = getLevel(playerId, type);
        List<PetAbility> unlocked = new ArrayList<>();
        for (PetAbility ability : all) {
            if (level >= ability.unlockLevel && (kind == null || ability.kind == kind)) {
                unlocked.add(ability);
            }
        }
        return Collections.unmodifiableList(unlocked);
    }

    /** Returns a {@link PetData} snapshot for the given pet type, or a level-1 default if none. */
    public PetData getPetData(UUID playerId, PetType type) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(type, "type");
        long xp = getExperience(playerId, type);
        return new PetData(type, type.defaultRarity, xp);
    }

    /**
     * Sets the item held by the given pet. Passing {@link PetItem#NONE} clears it.
     *
     * @return {@code true} if the pet exists in the player's collection
     */
    public boolean setHeldItem(UUID playerId, UUID petId, PetItem item) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(petId, "petId");
        Objects.requireNonNull(item, "item");
        Map<UUID, Pet> collection = playerPets.get(playerId);
        if (collection == null || !collection.containsKey(petId)) {
            return false;
        }
        if (item == PetItem.NONE) {
            Map<UUID, PetItem> held = petHeldItems.get(playerId);
            if (held != null) {
                held.remove(petId);
            }
        } else {
            petHeldItems.computeIfAbsent(playerId, k -> new HashMap<>()).put(petId, item);
        }
        return true;
    }

    /** Returns the item held by the given pet, or {@link PetItem#NONE} if none. */
    public PetItem getHeldItem(UUID playerId, UUID petId) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(petId, "petId");
        Map<UUID, PetItem> held = petHeldItems.get(playerId);
        return held == null ? PetItem.NONE : held.getOrDefault(petId, PetItem.NONE);
    }

    /**
     * Returns the held-item stat bonus for the given pet as {@code {speed, strength, health}},
     * all zero if the pet holds no item.
     */
    public int[] getHeldItemBonus(UUID playerId, UUID petId) {
        PetItem item = getHeldItem(playerId, petId);
        return new int[]{item.speedBonus, item.strengthBonus, item.healthBonus};
    }

    /**
     * Returns the total stat bonus the player's active pet contributes,
     * as {@code {speed, strength, health}}: the pet type's base stats (from
     * {@link #PET_DATA}) scaled linearly by its current level plus the bonus
     * from any held item. All zero if the player has no active pet.
     */
    public int[] getActivePetStats(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        UUID petId = equippedPets.get(playerId);
        if (petId == null) {
            return new int[]{0, 0, 0};
        }
        Pet pet = getActivePet(playerId);
        if (pet == null) {
            return new int[]{0, 0, 0};
        }
        int speed = 0, strength = 0, health = 0;
        int[] base = PET_DATA.get(pet.type.name());
        if (base != null) {
            double scale = (double) computeLevel(getExperience(playerId, pet.type), pet.rarity) / MAX_LEVEL;
            speed = (int) Math.round(base[1] * scale);
            strength = (int) Math.round(base[2] * scale);
            health = (int) Math.round(base[3] * scale);
        }
        int[] held = getHeldItemBonus(playerId, petId);
        return new int[]{speed + held[0], strength + held[1], health + held[2]};
    }

    /**
     * Returns the primary stat bonus the player's active pet grants at its current level,
     * or {@code 0} if the player has no active pet. The bonus is
     * {@code statBonus.bonusPerLevel() * level} of {@link PetType#statBonus}.
     */
    public double getActivePetStatBonus(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        Pet pet = getActivePet(playerId);
        if (pet == null) {
            return 0.0;
        }
        int level = computeLevel(getExperience(playerId, pet.type), pet.rarity);
        return pet.type.statBonus.bonusPerLevel() * level;
    }

    public void recordPetEvent(UUID playerId, String summary) {
        petHistory.computeIfAbsent(playerId, k -> new ArrayList<>()).add(summary);
    }

    public List<String> getPetHistory(UUID playerId) {
        return Collections.unmodifiableList(petHistory.getOrDefault(playerId, Collections.emptyList()));
    }

    public Map<UUID, List<String>> getAllPetHistory() {
        return Collections.unmodifiableMap(petHistory);
    }

    /**
     * Loads pet data from {@code pets.yml} in the given folder, replacing all in-memory state.
     *
     * @param dataFolder the plugin data folder
     */
    public void load(File dataFolder) {
        File file = new File(dataFolder, "pets.yml");
        if (!file.exists()) {
            return;
        }
        YamlConfiguration cfg = YamlConfiguration.loadConfiguration(file);
        playerPets.clear();
        equippedPets.clear();
        petExperience.clear();
        petHeldItems.clear();
        petHistory.clear();
        petXp.clear();
        petLevel.clear();
        for (String key : cfg.getKeys(false)) {
            if ("petHistory".equals(key)) {
                continue;
            }
            try {
                UUID playerId = UUID.fromString(key);
                String equippedStr = cfg.getString(key + ".equipped");
                if (equippedStr != null) {
                    try {
                        equippedPets.put(playerId, UUID.fromString(equippedStr));
                    } catch (IllegalArgumentException ignored) {
                        // skip malformed UUID
                    }
                }
                if (cfg.isConfigurationSection(key + ".pets")) {
                    Map<UUID, Pet> collection = new HashMap<>();
                    Map<UUID, PetItem> held = new HashMap<>();
                    for (String petIdStr : cfg.getConfigurationSection(key + ".pets").getKeys(false)) {
                        try {
                            UUID petId = UUID.fromString(petIdStr);
                            String typeName = cfg.getString(key + ".pets." + petIdStr + ".type");
                            String rarityName = cfg.getString(key + ".pets." + petIdStr + ".rarity");
                            if (typeName == null || rarityName == null) {
                                continue;
                            }
                            PetType type = PetType.valueOf(typeName);
                            Rarity rarity = Rarity.valueOf(rarityName);
                            collection.put(petId, new Pet(petId, type, rarity));
                            String itemName = cfg.getString(key + ".pets." + petIdStr + ".item");
                            if (itemName != null) {
                                try {
                                    PetItem item = PetItem.valueOf(itemName);
                                    if (item != PetItem.NONE) {
                                        held.put(petId, item);
                                    }
                                } catch (IllegalArgumentException ignored) {
                                    // skip unknown held item
                                }
                            }
                        } catch (IllegalArgumentException ignored) {
                            // skip malformed or unknown entries
                        }
                    }
                    if (!collection.isEmpty()) {
                        playerPets.put(playerId, collection);
                    }
                    if (!held.isEmpty()) {
                        petHeldItems.put(playerId, held);
                    }
                }
                if (cfg.isConfigurationSection(key + ".xp")) {
                    Map<PetType, Long> xpMap = new EnumMap<>(PetType.class);
                    for (String typeName : cfg.getConfigurationSection(key + ".xp").getKeys(false)) {
                        try {
                            PetType type = PetType.valueOf(typeName);
                            long xp = cfg.getLong(key + ".xp." + typeName, 0L);
                            xpMap.put(type, xp);
                        } catch (IllegalArgumentException ignored) {
                            // skip unknown pet types
                        }
                    }
                    if (!xpMap.isEmpty()) {
                        petExperience.put(playerId, xpMap);
                    }
                }
            } catch (IllegalArgumentException ignored) {
                // skip malformed player UUID keys
            }
        }
        if (cfg.isConfigurationSection("petHistory")) {
            for (String key : cfg.getConfigurationSection("petHistory").getKeys(false)) {
                try {
                    UUID playerId = UUID.fromString(key);
                    List<String> entries = cfg.getStringList("petHistory." + key);
                    if (!entries.isEmpty()) {
                        petHistory.put(playerId, new ArrayList<>(entries));
                    }
                } catch (IllegalArgumentException ignored) {
                    // skip malformed entries
                }
            }
        }
    }

    /**
     * Saves all pet data to {@code pets.yml} in the given folder.
     *
     * @param dataFolder the plugin data folder
     */
    public void save(File dataFolder) {
        File file = new File(dataFolder, "pets.yml");
        YamlConfiguration cfg = new YamlConfiguration();
        for (Map.Entry<UUID, Map<UUID, Pet>> entry : playerPets.entrySet()) {
            String playerKey = entry.getKey().toString();
            UUID equipped = equippedPets.get(entry.getKey());
            if (equipped != null) {
                cfg.set(playerKey + ".equipped", equipped.toString());
            }
            Map<UUID, PetItem> held = petHeldItems.get(entry.getKey());
            for (Map.Entry<UUID, Pet> petEntry : entry.getValue().entrySet()) {
                String petPath = playerKey + ".pets." + petEntry.getKey().toString();
                cfg.set(petPath + ".type", petEntry.getValue().type.name());
                cfg.set(petPath + ".rarity", petEntry.getValue().rarity.name());
                PetItem item = held == null ? null : held.get(petEntry.getKey());
                if (item != null && item != PetItem.NONE) {
                    cfg.set(petPath + ".item", item.name());
                }
            }
        }
        for (Map.Entry<UUID, Map<PetType, Long>> entry : petExperience.entrySet()) {
            String playerKey = entry.getKey().toString();
            for (Map.Entry<PetType, Long> xpEntry : entry.getValue().entrySet()) {
                cfg.set(playerKey + ".xp." + xpEntry.getKey().name(), xpEntry.getValue());
            }
        }
        for (Map.Entry<UUID, List<String>> entry : petHistory.entrySet()) {
            if (!entry.getValue().isEmpty()) {
                cfg.set("petHistory." + entry.getKey().toString(), entry.getValue());
            }
        }
        try {
            cfg.save(file);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save pets.yml", e);
        }
    }

    /**
     * Removes all data for the given player.
     *
     * @return {@code true} if the player had any data
     */
    public boolean reset(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        boolean hadData = playerPets.remove(playerId) != null;
        hadData |= equippedPets.remove(playerId) != null;
        hadData |= petExperience.remove(playerId) != null;
        hadData |= petHeldItems.remove(playerId) != null;
        petHistory.remove(playerId);
        petXp.remove(playerId);
        petLevel.remove(playerId);
        return hadData;
    }

    /** Returns the cumulative-XP threshold table for the given rarity, falling back sensibly for unmapped tiers. */
    private static long[] xpTableFor(Rarity rarity) {
        long[] table = PET_XP_TABLE.get(rarity.name());
        if (table != null) {
            return table;
        }
        // Rarities above LEGENDARY (MYTHIC/DIVINE/SPECIAL) reuse the LEGENDARY curve; anything else uses COMMON.
        return rarity.compareTo(Rarity.LEGENDARY) >= 0
                ? PET_XP_TABLE.get("LEGENDARY")
                : PET_XP_TABLE.get("COMMON");
    }

    private static int computeLevel(long xp, Rarity rarity) {
        long[] table = xpTableFor(rarity);
        int level = 1;
        while (level < MAX_LEVEL && xp >= table[level - 1]) {
            level++;
        }
        return level;
    }
}
