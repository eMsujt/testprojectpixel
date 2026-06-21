package com.skyblock.core.manager;

import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * Canonical singleton for per-player SkyBlock pet management.
 *
 * <p>Tracks every {@link PetData} instance by owner {@link UUID} and manages
 * which pet is currently active. Not thread-safe; synchronize externally if
 * accessed from multiple threads.</p>
 */
public final class PetsManager {

    public static final int MAX_LEVEL = 100;

    /** Cumulative XP required to reach each level (index = level - 1, length = MAX_LEVEL). */
    private static final long[] XP_TABLE;

    static {
        XP_TABLE = new long[MAX_LEVEL];
        // Vanilla SkyBlock pet XP curve approximation
        XP_TABLE[0] = 0;
        long cumulative = 0;
        for (int lvl = 1; lvl < MAX_LEVEL; lvl++) {
            long needed = 100L + (long) (lvl * lvl * 2.5);
            cumulative += needed;
            XP_TABLE[lvl] = cumulative;
        }
    }

    public enum PetRarity {
        COMMON("§f"),
        UNCOMMON("§a"),
        RARE("§9"),
        EPIC("§5"),
        LEGENDARY("§6"),
        MYTHIC("§d");

        private final String colorCode;

        PetRarity(String colorCode) {
            this.colorCode = colorCode;
        }

        public String getColorCode() {
            return colorCode;
        }

        public String getDisplayName() {
            String name = name().charAt(0) + name().substring(1).toLowerCase();
            return colorCode + name;
        }
    }

    public enum PetType {
        BEE("Bee"),
        BLAZE("Blaze"),
        CAT("Cat"),
        CHICKEN("Chicken"),
        ENDERMAN("Enderman"),
        ENDER_DRAGON("Ender Dragon"),
        FLYING_FISH("Flying Fish"),
        GIRAFFE("Giraffe"),
        GOLDEN_DRAGON("Golden Dragon"),
        GOLEM("Golem"),
        GRANDMA_WOLF("Grandma Wolf"),
        GRIFFIN("Griffin"),
        GUARDIAN("Guardian"),
        HORSE("Horse"),
        HOUND("Hound"),
        JERRY("Jerry"),
        JELLYFISH("Jellyfish"),
        LION("Lion"),
        MAGMA_CUBE("Magma Cube"),
        MITHRIL_GOLEM("Mithril Golem"),
        MONKEY("Monkey"),
        OCELOT("Ocelot"),
        PIG("Pig"),
        PHOENIX("Phoenix"),
        RABBIT("Rabbit"),
        RAT("Rat"),
        ROCK("Rock"),
        SHEEP("Sheep"),
        SILVERFISH("Silverfish"),
        SKELETON("Skeleton"),
        SKELETON_HORSE("Skeleton Horse"),
        SLUG("Slug"),
        SNOWMAN("Snowman"),
        SPIDER("Spider"),
        SQUID("Squid"),
        TIGER("Tiger"),
        TURTLE("Turtle"),
        WITHER_SKELETON("Wither Skeleton"),
        WOLF("Wolf"),
        ZOMBIE("Zombie");

        private final String displayName;

        PetType(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    /** Mutable state for a single pet owned by a player. */
    public static final class PetData {
        public final UUID id;
        public final UUID owner;
        public final PetType type;
        public final PetRarity rarity;
        private long experience;

        public PetData(UUID id, UUID owner, PetType type, PetRarity rarity) {
            this.id = Objects.requireNonNull(id, "id");
            this.owner = Objects.requireNonNull(owner, "owner");
            this.type = Objects.requireNonNull(type, "type");
            this.rarity = Objects.requireNonNull(rarity, "rarity");
        }

        public long getExperience() {
            return experience;
        }

        /** Returns the pet's current level (1–{@link PetsManager#MAX_LEVEL}). */
        public int getLevel() {
            int level = 1;
            while (level < MAX_LEVEL && experience >= XP_TABLE[level]) {
                level++;
            }
            return level;
        }

        public String getDisplayName() {
            return rarity.getColorCode() + "[Lvl " + getLevel() + "] " + type.getDisplayName();
        }
    }

    /**
     * Immutable snapshot of a single pet: its display name, type, rarity,
     * current level, and total experience.
     */
    public record Pet(String name, PetType type, PetRarity rarity, int level, long xp) {
    }

    private static final PetsManager INSTANCE = new PetsManager();

    /** All pets owned by each player. */
    private final Map<UUID, List<PetData>> petsByOwner = new HashMap<>();

    /** The UUID of the active pet per player, or absent if none is active. */
    private final Map<UUID, UUID> activePet = new HashMap<>();

    private PetsManager() {}

    public static PetsManager getInstance() {
        return INSTANCE;
    }

    /**
     * Adds a new pet for the given player and returns its data.
     * The pet is not automatically set as the active pet.
     */
    public PetData addPet(UUID playerId, PetType type, PetRarity rarity) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(type, "type");
        Objects.requireNonNull(rarity, "rarity");
        PetData pet = new PetData(UUID.randomUUID(), playerId, type, rarity);
        petsByOwner.computeIfAbsent(playerId, id -> new ArrayList<>()).add(pet);
        return pet;
    }

    /**
     * Removes the pet with the given ID from the player's collection.
     * If it was the active pet, the active slot is cleared.
     *
     * @return {@code true} if the pet was found and removed
     */
    public boolean removePet(UUID playerId, UUID petId) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(petId, "petId");
        List<PetData> pets = petsByOwner.get(playerId);
        if (pets == null) {
            return false;
        }
        boolean removed = pets.removeIf(p -> p.id.equals(petId));
        if (removed && petId.equals(activePet.get(playerId))) {
            activePet.remove(playerId);
        }
        return removed;
    }

    /** Returns an unmodifiable view of the player's pets, or an empty list if they have none. */
    public List<PetData> getPets(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        List<PetData> pets = petsByOwner.get(playerId);
        return pets == null ? Collections.emptyList() : Collections.unmodifiableList(pets);
    }

    /** Returns the player's currently active pet, or {@code null} if none is active. */
    public PetData getActivePet(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        UUID petId = activePet.get(playerId);
        if (petId == null) {
            return null;
        }
        List<PetData> pets = petsByOwner.get(playerId);
        if (pets == null) {
            return null;
        }
        for (PetData pet : pets) {
            if (pet.id.equals(petId)) {
                return pet;
            }
        }
        return null;
    }

    /**
     * Sets the active pet for the player. Pass {@code null} for petId to deactivate.
     *
     * @throws IllegalArgumentException if petId is non-null but not found in the player's collection
     */
    public void setActivePet(UUID playerId, UUID petId) {
        Objects.requireNonNull(playerId, "playerId");
        if (petId == null) {
            activePet.remove(playerId);
            return;
        }
        List<PetData> pets = petsByOwner.get(playerId);
        if (pets == null || pets.stream().noneMatch(p -> p.id.equals(petId))) {
            throw new IllegalArgumentException("Pet " + petId + " does not belong to player " + playerId);
        }
        activePet.put(playerId, petId);
    }

    /**
     * Adds experience to the given pet and returns the new total XP.
     *
     * @throws IllegalArgumentException if amount is negative
     * @throws IllegalArgumentException if the pet is not found in the player's collection
     */
    public long addExperience(UUID playerId, UUID petId, long amount) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(petId, "petId");
        if (amount < 0) {
            throw new IllegalArgumentException("amount must not be negative, got " + amount);
        }
        PetData pet = findPet(playerId, petId);
        if (pet == null) {
            throw new IllegalArgumentException("Pet " + petId + " does not belong to player " + playerId);
        }
        long cap = XP_TABLE[MAX_LEVEL - 1];
        pet.experience = Math.min(cap, pet.experience + amount);
        return pet.experience;
    }

    /**
     * Adds experience to the given pet and returns an updated {@link Pet} snapshot.
     *
     * <p>Equivalent to {@link #addExperience(UUID, UUID, long)} but returning the
     * pet's resulting state (name, type, rarity, level, total XP) rather than only
     * its new total.</p>
     *
     * @throws IllegalArgumentException if amount is negative or the pet is not found
     */
    public Pet addXp(UUID playerId, UUID petId, long amount) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(petId, "petId");
        if (amount < 0) {
            throw new IllegalArgumentException("amount must not be negative, got " + amount);
        }
        PetData pet = findPet(playerId, petId);
        if (pet == null) {
            throw new IllegalArgumentException("Pet " + petId + " does not belong to player " + playerId);
        }
        long cap = XP_TABLE[MAX_LEVEL - 1];
        pet.experience = Math.min(cap, pet.experience + amount);
        return new Pet(pet.type.getDisplayName(), pet.type, pet.rarity, pet.getLevel(), pet.experience);
    }

    /** Clears all pets and the active slot for the given player. */
    public boolean reset(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        boolean hadData = petsByOwner.remove(playerId) != null;
        hadData |= activePet.remove(playerId) != null;
        return hadData;
    }

    private PetData findPet(UUID playerId, UUID petId) {
        List<PetData> pets = petsByOwner.get(playerId);
        if (pets == null) {
            return null;
        }
        for (PetData pet : pets) {
            if (pet.id.equals(petId)) {
                return pet;
            }
        }
        return null;
    }

    public void load(File dataFolder) {
        File file = new File(dataFolder, "pets.yml");
        if (!file.exists()) {
            return;
        }
        YamlConfiguration cfg = YamlConfiguration.loadConfiguration(file);
        petsByOwner.clear();
        activePet.clear();
        for (String ownerKey : cfg.getKeys(false)) {
            try {
                UUID ownerId = UUID.fromString(ownerKey);
                String activeStr = cfg.getString(ownerKey + ".active");
                if (activeStr != null) {
                    try {
                        activePet.put(ownerId, UUID.fromString(activeStr));
                    } catch (IllegalArgumentException ignored) {}
                }
                if (!cfg.isConfigurationSection(ownerKey + ".pets")) {
                    continue;
                }
                List<PetData> pets = new ArrayList<>();
                for (String petKey : cfg.getConfigurationSection(ownerKey + ".pets").getKeys(false)) {
                    try {
                        UUID petId = UUID.fromString(petKey);
                        String typeName   = cfg.getString(ownerKey + ".pets." + petKey + ".type");
                        String rarityName = cfg.getString(ownerKey + ".pets." + petKey + ".rarity");
                        long xp = cfg.getLong(ownerKey + ".pets." + petKey + ".xp", 0L);
                        PetType type     = PetType.valueOf(typeName);
                        PetRarity rarity = PetRarity.valueOf(rarityName);
                        PetData pet = new PetData(petId, ownerId, type, rarity);
                        pet.experience = xp;
                        pets.add(pet);
                    } catch (IllegalArgumentException ignored) {
                        // skip malformed or unknown pet entries
                    }
                }
                if (!pets.isEmpty()) {
                    petsByOwner.put(ownerId, pets);
                }
            } catch (IllegalArgumentException ignored) {
                // skip malformed owner UUID
            }
        }
    }

    public void save(File dataFolder) {
        File file = new File(dataFolder, "pets.yml");
        YamlConfiguration cfg = new YamlConfiguration();
        for (Map.Entry<UUID, List<PetData>> entry : petsByOwner.entrySet()) {
            String ownerKey = entry.getKey().toString();
            UUID active = activePet.get(entry.getKey());
            if (active != null) {
                cfg.set(ownerKey + ".active", active.toString());
            }
            for (PetData pet : entry.getValue()) {
                String petKey = ownerKey + ".pets." + pet.id.toString();
                cfg.set(petKey + ".type",   pet.type.name());
                cfg.set(petKey + ".rarity", pet.rarity.name());
                cfg.set(petKey + ".xp",     pet.experience);
            }
        }
        try {
            cfg.save(file);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save pets.yml", e);
        }
    }
}
