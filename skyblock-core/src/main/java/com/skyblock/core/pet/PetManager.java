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
        LEGENDARY_GRIFFIN(PetRarity.LEGENDARY,   "Griffin"),
        LEGENDARY_GOLDEN_DRAGON(PetRarity.LEGENDARY, "Golden Dragon"),
        EPIC_ENDERMAN(PetRarity.EPIC,            "Enderman"),
        EPIC_BLAZE(PetRarity.EPIC,               "Blaze"),
        EPIC_WOLF(PetRarity.EPIC,                "Wolf"),
        RARE_RABBIT(PetRarity.RARE,              "Rabbit"),
        RARE_BEE(PetRarity.RARE,                 "Bee"),
        UNCOMMON_PENGUIN(PetRarity.UNCOMMON,     "Penguin"),
        COMMON_CHICKEN(PetRarity.COMMON,         "Chicken");

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
