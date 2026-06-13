package com.skyblock.core.pet;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * Singleton tracking each player's active pet and per-type experience.
 */
public final class PetManager {

    public static final int MAX_LEVEL = 100;

    private static final long[] XP_THRESHOLD;

    static {
        XP_THRESHOLD = new long[MAX_LEVEL];
        long cumulative = 0;
        for (int i = 0; i < MAX_LEVEL; i++) {
            cumulative += 100L * (i + 1);
            XP_THRESHOLD[i] = cumulative;
        }
    }

    public enum PetRarity {
        COMMON("Common"), UNCOMMON("Uncommon"), RARE("Rare"), EPIC("Epic"), LEGENDARY("Legendary");

        private final String displayName;

        PetRarity(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    public enum PetType {
        // Common
        CHICKEN(PetRarity.COMMON,                "Chicken"),
        WORM(PetRarity.COMMON,                   "Worm"),
        SHEEP(PetRarity.COMMON,                  "Sheep"),
        PIG(PetRarity.COMMON,                    "Pig"),
        COW(PetRarity.COMMON,                    "Cow"),
        BAT(PetRarity.COMMON,                    "Bat"),
        SNAIL(PetRarity.COMMON,                  "Snail"),
        ROCK(PetRarity.COMMON,                   "Rock"),
        // Uncommon
        RABBIT(PetRarity.UNCOMMON,               "Rabbit"),
        PENGUIN(PetRarity.UNCOMMON,              "Penguin"),
        HORSE(PetRarity.UNCOMMON,                "Horse"),
        MULE(PetRarity.UNCOMMON,                 "Mule"),
        DONKEY(PetRarity.UNCOMMON,               "Donkey"),
        GOAT(PetRarity.UNCOMMON,                 "Goat"),
        SILVERFISH(PetRarity.UNCOMMON,           "Silverfish"),
        CAVE_SPIDER(PetRarity.UNCOMMON,          "Cave Spider"),
        MUSHROOM_COW(PetRarity.UNCOMMON,         "Mushroom Cow"),
        // Rare
        BEE(PetRarity.RARE,                      "Bee"),
        DOLPHIN(PetRarity.RARE,                  "Dolphin"),
        SQUID(PetRarity.RARE,                    "Squid"),
        FLYING_FISH(PetRarity.RARE,              "Flying Fish"),
        CAT(PetRarity.RARE,                      "Cat"),
        PARROT(PetRarity.RARE,                   "Parrot"),
        MONKEY(PetRarity.RARE,                   "Monkey"),
        GUARDIAN(PetRarity.RARE,                 "Guardian"),
        OCELOT(PetRarity.RARE,                   "Ocelot"),
        TURTLE(PetRarity.RARE,                   "Turtle"),
        ELEPHANT(PetRarity.RARE,                 "Elephant"),
        GIRAFFE(PetRarity.RARE,                  "Giraffe"),
        LION(PetRarity.RARE,                     "Lion"),
        CREEPER(PetRarity.RARE,                  "Creeper"),
        ZOMBIE(PetRarity.RARE,                   "Zombie"),
        SKELETON(PetRarity.RARE,                 "Skeleton"),
        SPIDER(PetRarity.RARE,                   "Spider"),
        // Epic
        WOLF(PetRarity.EPIC,                     "Wolf"),
        BLAZE(PetRarity.EPIC,                    "Blaze"),
        MAGMA_CUBE(PetRarity.EPIC,               "Magma Cube"),
        ENDERMAN(PetRarity.EPIC,                 "Enderman"),
        GHAST(PetRarity.EPIC,                    "Ghast"),
        GOLEM(PetRarity.EPIC,                    "Golem"),
        WITHER_SKELETON(PetRarity.EPIC,          "Wither Skeleton"),
        TARANTULA(PetRarity.EPIC,                "Tarantula"),
        BABY_YETI(PetRarity.EPIC,                "Baby Yeti"),
        BLUE_WHALE(PetRarity.EPIC,               "Blue Whale"),
        TIGER(PetRarity.EPIC,                    "Tiger"),
        WISP(PetRarity.EPIC,                     "Wisp"),
        SNOWMAN(PetRarity.EPIC,                  "Snowman"),
        ARMADILLO(PetRarity.EPIC,                "Armadillo"),
        AMMONITE(PetRarity.EPIC,                 "Ammonite"),
        SPINOCLAW(PetRarity.EPIC,                "Spinoclaw"),
        // Legendary
        GRIFFIN(PetRarity.LEGENDARY,             "Griffin"),
        GOLDEN_DRAGON(PetRarity.LEGENDARY,       "Golden Dragon"),
        BLUE_SHARK(PetRarity.LEGENDARY,          "Blue Shark"),
        JERRY(PetRarity.LEGENDARY,               "Jerry"),
        BLACK_CAT(PetRarity.LEGENDARY,           "Black Cat"),
        GRANDMA_WOLF(PetRarity.LEGENDARY,        "Grandma Wolf"),
        ENDER_DRAGON(PetRarity.LEGENDARY,        "Ender Dragon");

        public final PetRarity rarity;
        private final String displayName;

        PetType(PetRarity rarity, String displayName) {
            this.rarity = rarity;
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    public static final class PetData {
        public final PetType type;
        public final PetRarity rarity;
        public final long experience;

        public PetData(PetType type, PetRarity rarity, long experience) {
            this.type = Objects.requireNonNull(type, "type");
            this.rarity = Objects.requireNonNull(rarity, "rarity");
            this.experience = experience;
        }

        public int getLevel() {
            int level = 1;
            while (level < MAX_LEVEL && experience >= XP_THRESHOLD[level - 1]) {
                level++;
            }
            return level;
        }
    }

    private static final PetManager INSTANCE = new PetManager();

    private final Map<UUID, Map<PetType, Long>> petExperience = new HashMap<>();
    private final Map<UUID, PetData> activePets = new HashMap<>();

    private PetManager() {
    }

    public static PetManager getInstance() {
        return INSTANCE;
    }

    public long addExperience(UUID playerId, PetType type, long amount) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(type, "type");
        if (amount < 0) {
            throw new IllegalArgumentException("amount must not be negative");
        }
        Map<PetType, Long> xpMap = petExperience.computeIfAbsent(
                playerId, id -> new EnumMap<>(PetType.class));
        long total = xpMap.getOrDefault(type, 0L) + amount;
        xpMap.put(type, total);
        return total;
    }

    public long getExperience(UUID playerId, PetType type) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(type, "type");
        Map<PetType, Long> xpMap = petExperience.get(playerId);
        return xpMap == null ? 0L : xpMap.getOrDefault(type, 0L);
    }

    public int getLevel(UUID playerId, PetType type) {
        long xp = getExperience(playerId, type);
        int level = 1;
        while (level < MAX_LEVEL && xp >= XP_THRESHOLD[level - 1]) {
            level++;
        }
        return level;
    }

    public void setActivePet(UUID playerId, PetType type) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(type, "type");
        long xp = getExperience(playerId, type);
        activePets.put(playerId, new PetData(type, type.rarity, xp));
    }

    public PetData getActivePet(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        return activePets.get(playerId);
    }

    public boolean removeActivePet(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        return activePets.remove(playerId) != null;
    }

    public boolean reset(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        boolean hadData = petExperience.remove(playerId) != null;
        hadData |= activePets.remove(playerId) != null;
        return hadData;
    }
}
