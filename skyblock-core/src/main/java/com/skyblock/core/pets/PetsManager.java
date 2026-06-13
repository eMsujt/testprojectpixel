package com.skyblock.core.pets;

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
 * Singleton managing the pet collection (inventory) for each player.
 *
 * <p>Tracks which {@link Pet} instances a player owns and which one is
 * currently equipped. XP and leveling are delegated to {@link PetManager}.
 * Not thread-safe; synchronize externally if accessed from multiple threads.</p>
 */
public final class PetsManager {

    /** Every pet type available in SkyBlock. */
    public enum PetType {
        BEE, WOLF, ENDERMAN, BLAZE, TIGER, DOLPHIN,
        RABBIT, LION, ELEPHANT, HORSE, CAT, PARROT,
        PENGUIN, TURTLE, SHEEP, PIG, CHICKEN,
        SKELETON, SPIDER, ZOMBIE, JELLYFISH
    }

    /** Rarity tiers for pets. */
    public enum PetRarity {
        COMMON, UNCOMMON, RARE, EPIC, LEGENDARY
    }

    /** The highest level a pet can reach. */
    public static final int MAX_LEVEL = 100;

    /** Cumulative XP required to reach each level, indexed by level - 1. */
    private static final double[] XP_PER_LEVEL;

    static {
        XP_PER_LEVEL = new double[MAX_LEVEL];
        double cumulative = 0;
        for (int i = 0; i < MAX_LEVEL; i++) {
            cumulative += 100.0 * (i + 1);
            XP_PER_LEVEL[i] = cumulative;
        }
    }

    /** Immutable snapshot of a pet's XP and level. */
    public static final class PetData {
        public final PetType type;
        public final double xp;
        public final int level;

        public PetData(PetType type, double xp, int level) {
            this.type = Objects.requireNonNull(type, "type");
            this.xp = xp;
            this.level = level;
        }
    }

    /** A single owned pet instance. */
    public static final class Pet {
        public final UUID id;
        public final PetType type;
        public final PetRarity rarity;

        public Pet(UUID id, PetType type, PetRarity rarity) {
            this.id = Objects.requireNonNull(id, "id");
            this.type = Objects.requireNonNull(type, "type");
            this.rarity = Objects.requireNonNull(rarity, "rarity");
        }
    }

    private static final PetsManager INSTANCE = new PetsManager();

    /** All pets owned by each player, keyed by pet UUID. */
    private final Map<UUID, Map<UUID, Pet>> playerPets = new HashMap<>();

    /** Currently equipped pet per player (pet UUID). */
    private final Map<UUID, UUID> equippedPets = new HashMap<>();

    /** Per-player XP data keyed by pet type. */
    private final Map<UUID, Map<PetType, PetData>> petXpData = new HashMap<>();

    private PetsManager() {
    }

    /**
     * Returns the single shared {@code PetsManager} instance.
     *
     * @return the singleton instance
     */
    public static PetsManager getInstance() {
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
    public Pet addPet(UUID playerId, PetType type, PetRarity rarity) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(type, "type");
        Objects.requireNonNull(rarity, "rarity");
        Pet pet = new Pet(UUID.randomUUID(), type, rarity);
        playerPets.computeIfAbsent(playerId, k -> new HashMap<>()).put(pet.id, pet);
        return pet;
    }

    /**
     * Removes a pet from the player's collection, unequipping it first if active.
     *
     * @param playerId the player losing the pet
     * @param petId    the UUID of the pet to remove
     * @return {@code true} if the pet existed and was removed
     */
    public boolean removePet(UUID playerId, UUID petId) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(petId, "petId");
        Map<UUID, Pet> collection = playerPets.get(playerId);
        if (collection == null || collection.remove(petId) == null) {
            return false;
        }
        UUID equipped = equippedPets.get(playerId);
        if (petId.equals(equipped)) {
            equippedPets.remove(playerId);
        }
        return true;
    }

    /**
     * Equips the given pet for the player, replacing any previously equipped pet.
     *
     * @param playerId the player equipping the pet
     * @param petId    the UUID of the pet to equip
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
        return true;
    }

    /**
     * Unequips the player's active pet without removing it from the collection.
     *
     * @param playerId the player to unequip
     * @return {@code true} if the player had an active pet, {@code false} otherwise
     */
    public boolean unequipPet(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        return equippedPets.remove(playerId) != null;
    }

    /**
     * Returns the player's currently equipped {@link Pet}, or {@code null} if none.
     *
     * @param playerId the player to look up
     * @return the active pet, or {@code null}
     */
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
     * Returns an unmodifiable list of all pets owned by the player.
     *
     * @param playerId the player to look up
     * @return an unmodifiable list of pets, empty if the player has none
     */
    public List<Pet> getPets(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        Map<UUID, Pet> collection = playerPets.get(playerId);
        if (collection == null || collection.isEmpty()) {
            return Collections.emptyList();
        }
        return Collections.unmodifiableList(new ArrayList<>(collection.values()));
    }

    /**
     * Adds XP to the given pet type for a player, leveling up when thresholds are met.
     *
     * @param player the player gaining XP
     * @param pet    the pet type receiving XP
     * @param amount the amount of XP to add, must not be negative
     * @return the updated {@link PetData} after the addition
     * @throws IllegalArgumentException if {@code amount} is negative
     */
    public PetData gainXP(UUID player, PetType pet, double amount) {
        Objects.requireNonNull(player, "player");
        Objects.requireNonNull(pet, "pet");
        if (amount < 0) {
            throw new IllegalArgumentException("amount must not be negative, got " + amount);
        }
        Map<PetType, PetData> xpMap = petXpData.computeIfAbsent(
                player, id -> new EnumMap<>(PetType.class));
        PetData current = xpMap.getOrDefault(pet, new PetData(pet, 0.0, 1));
        double newXp = current.xp + amount;
        int level = 1;
        while (level < MAX_LEVEL && newXp >= XP_PER_LEVEL[level - 1]) {
            level++;
        }
        PetData updated = new PetData(pet, newXp, level);
        xpMap.put(pet, updated);
        return updated;
    }

    /**
     * Returns the {@link PetData} for the given pet type, or a default level-1 entry if none.
     *
     * @param player the player to look up
     * @param pet    the pet type to look up
     * @return the current {@link PetData}
     */
    public PetData getPetData(UUID player, PetType pet) {
        Objects.requireNonNull(player, "player");
        Objects.requireNonNull(pet, "pet");
        Map<PetType, PetData> xpMap = petXpData.get(player);
        if (xpMap == null) {
            return new PetData(pet, 0.0, 1);
        }
        return xpMap.getOrDefault(pet, new PetData(pet, 0.0, 1));
    }

    public void load(File dataFolder) {
        File file = new File(dataFolder, "pets.yml");
        if (!file.exists()) {
            return;
        }
        YamlConfiguration cfg = YamlConfiguration.loadConfiguration(file);
        petXpData.clear();
        for (String key : cfg.getKeys(false)) {
            try {
                UUID uuid = UUID.fromString(key);
                if (!cfg.isConfigurationSection(key)) {
                    continue;
                }
                Map<PetType, PetData> xpMap = new EnumMap<>(PetType.class);
                for (String typeName : cfg.getConfigurationSection(key).getKeys(false)) {
                    try {
                        PetType type = PetType.valueOf(typeName);
                        double xp = cfg.getDouble(key + "." + typeName + ".xp", 0.0);
                        int level = cfg.getInt(key + "." + typeName + ".level", 1);
                        xpMap.put(type, new PetData(type, xp, level));
                    } catch (IllegalArgumentException ignored) {
                        // skip unknown pet types
                    }
                }
                if (!xpMap.isEmpty()) {
                    petXpData.put(uuid, xpMap);
                }
            } catch (IllegalArgumentException ignored) {
                // skip malformed entries
            }
        }
    }

    public void save(File dataFolder) {
        File file = new File(dataFolder, "pets.yml");
        YamlConfiguration cfg = new YamlConfiguration();
        for (Map.Entry<UUID, Map<PetType, PetData>> entry : petXpData.entrySet()) {
            String key = entry.getKey().toString();
            for (Map.Entry<PetType, PetData> pd : entry.getValue().entrySet()) {
                String typeName = pd.getKey().name();
                cfg.set(key + "." + typeName + ".xp", pd.getValue().xp);
                cfg.set(key + "." + typeName + ".level", pd.getValue().level);
            }
        }
        try {
            cfg.save(file);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save pets.yml", e);
        }
    }

    /**
     * Removes all pet data for the given player, including their collection and equipped pet.
     *
     * @param playerId the player to reset
     * @return {@code true} if the player had any data
     */
    public boolean reset(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        boolean hadData = playerPets.remove(playerId) != null;
        hadData |= equippedPets.remove(playerId) != null;
        hadData |= petXpData.remove(playerId) != null;
        return hadData;
    }
}
