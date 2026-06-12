package com.skyblock.core.pets;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * Singleton tracking each player's active pet and pet experience per {@link PetType}.
 *
 * <p>Experience is stored per player as an {@link EnumMap} of pet type to
 * total XP. Not thread-safe; synchronize externally if accessed
 * from multiple threads.</p>
 */
public final class PetManager {

    /** The highest level a pet can reach. */
    public static final int MAX_LEVEL = 100;

    /** Cumulative XP required to reach each level, indexed by level - 1. */
    private static final long[] XP_PER_LEVEL;

    static {
        XP_PER_LEVEL = new long[MAX_LEVEL];
        long cumulative = 0;
        for (int i = 0; i < MAX_LEVEL; i++) {
            cumulative += 100L * (i + 1);
            XP_PER_LEVEL[i] = cumulative;
        }
    }

    /** Every pet type available in SkyBlock. */
    public enum PetType {
        CHICKEN, WOLF, RABBIT, BEE, LION, TIGER, ELEPHANT, HORSE,
        CAT, PARROT, PENGUIN, TURTLE, SHEEP, PIG, DOLPHIN,
        BLAZE, ENDERMAN, SKELETON, SPIDER, ZOMBIE
    }

    /** Rarity tiers for pets. */
    public enum Rarity {
        COMMON, UNCOMMON, RARE, EPIC, LEGENDARY
    }

    /** Immutable snapshot of a pet instance. */
    public static final class PetData {
        public final PetType type;
        public final Rarity rarity;
        public final long experience;

        public PetData(PetType type, Rarity rarity, long experience) {
            this.type = Objects.requireNonNull(type, "type");
            this.rarity = Objects.requireNonNull(rarity, "rarity");
            this.experience = experience;
        }

        /** Returns the pet's current level, between 1 and {@link #MAX_LEVEL}. */
        public int getLevel() {
            int level = 1;
            while (level < MAX_LEVEL && experience >= XP_PER_LEVEL[level - 1]) {
                level++;
            }
            return level;
        }
    }

    private static final PetManager INSTANCE = new PetManager();

    /** Per-player XP storage keyed by pet type. */
    private final Map<UUID, Map<PetType, Long>> petExperience = new HashMap<>();

    /** Currently active pet per player. */
    private final Map<UUID, PetData> activePets = new HashMap<>();

    private PetManager() {
    }

    /**
     * Returns the single shared {@code PetManager} instance.
     *
     * @return the singleton instance
     */
    public static PetManager getInstance() {
        return INSTANCE;
    }

    /**
     * Adds experience to the given pet type for a player.
     *
     * @param playerId the player gaining experience
     * @param type     the pet type receiving XP
     * @param amount   the amount of XP to add, must not be negative
     * @return the player's total XP for the pet after the addition
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
        long total = xpMap.getOrDefault(type, 0L) + amount;
        xpMap.put(type, total);
        return total;
    }

    /**
     * Returns the total experience the player has for the given pet type.
     *
     * @param playerId the player to look up
     * @param type     the pet type to look up
     * @return the total XP, {@code 0} if the player has none
     */
    public long getExperience(UUID playerId, PetType type) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(type, "type");
        Map<PetType, Long> xpMap = petExperience.get(playerId);
        return xpMap == null ? 0L : xpMap.getOrDefault(type, 0L);
    }

    /**
     * Returns the current level for the player's given pet type.
     *
     * @param playerId the player to look up
     * @param type     the pet type to look up
     * @return the level between {@code 1} and {@link #MAX_LEVEL}
     */
    public int getLevel(UUID playerId, PetType type) {
        long xp = getExperience(playerId, type);
        int level = 1;
        while (level < MAX_LEVEL && xp >= XP_PER_LEVEL[level - 1]) {
            level++;
        }
        return level;
    }

    /**
     * Sets the player's active pet.
     *
     * @param playerId the player equipping the pet
     * @param type     the pet type to equip
     * @param rarity   the rarity of the pet being equipped
     */
    public void setActivePet(UUID playerId, PetType type, Rarity rarity) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(type, "type");
        Objects.requireNonNull(rarity, "rarity");
        long xp = getExperience(playerId, type);
        activePets.put(playerId, new PetData(type, rarity, xp));
    }

    /**
     * Returns the player's currently active pet, or {@code null} if none is equipped.
     *
     * @param playerId the player to look up
     * @return the active {@link PetData}, or {@code null}
     */
    public PetData getActivePet(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        return activePets.get(playerId);
    }

    /**
     * Removes the player's active pet without deleting their XP.
     *
     * @param playerId the player to unequip
     * @return {@code true} if the player had an active pet, {@code false} otherwise
     */
    public boolean removeActivePet(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        return activePets.remove(playerId) != null;
    }

    /**
     * Removes all data for the given player, including XP and active pet.
     *
     * @param playerId the player to reset
     * @return {@code true} if the player had any data, {@code false} otherwise
     */
    public boolean reset(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        boolean hadData = petExperience.remove(playerId) != null;
        hadData |= activePets.remove(playerId) != null;
        return hadData;
    }
}
