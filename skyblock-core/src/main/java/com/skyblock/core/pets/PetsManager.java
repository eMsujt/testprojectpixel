package com.skyblock.core.pets;

import com.skyblock.core.manager.PetManager;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * @deprecated Use {@link PetManager} directly.
 *
 * <p>Retained for backward compatibility only. All methods delegate to
 * {@link PetManager#getInstance()}.</p>
 */
@Deprecated
public final class PetsManager {

    public enum PetType {
        BEE, WOLF, ENDERMAN, BLAZE, TIGER, DOLPHIN,
        RABBIT, LION, ELEPHANT, HORSE, CAT, DOG, PARROT,
        PENGUIN, TURTLE, SHEEP, PIG, CHICKEN,
        SKELETON, SPIDER, ZOMBIE, JELLYFISH,
        BLUE_WHALE, ARMADILLO, ROCK;

        public PetManager.PetType toCanonical() {
            return PetManager.PetType.valueOf(this.name());
        }
    }

    public enum PetRarity {
        COMMON, UNCOMMON, RARE, EPIC, LEGENDARY;

        public PetManager.PetRarity toCanonical() {
            return PetManager.PetRarity.valueOf(this.name());
        }
    }

    public static final int MAX_LEVEL = PetManager.MAX_LEVEL;
    public static final Map<String, long[]> PET_XP_TABLE = PetManager.PET_XP_TABLE;
    public static final Map<String, int[]> PET_DATA = PetManager.PET_DATA;

    public static final class Pet {
        public final UUID id;
        public final PetType type;
        public final PetRarity rarity;

        public Pet(UUID id, PetType type, PetRarity rarity) {
            this.id = id;
            this.type = type;
            this.rarity = rarity;
        }
    }

    public static final class PetData {
        public final PetType type;
        public final double xp;
        public final int level;

        public PetData(PetType type, double xp, int level) {
            this.type = type;
            this.xp = xp;
            this.level = level;
        }
    }

    private static final PetsManager INSTANCE = new PetsManager();
    private final PetManager delegate = PetManager.getInstance();

    private PetsManager() {
    }

    public static PetsManager getInstance() {
        return INSTANCE;
    }

    public Pet addPet(UUID playerId, PetType type, PetRarity rarity) {
        PetManager.Pet canonical = delegate.addPet(playerId, type.toCanonical(), rarity.toCanonical());
        return new Pet(canonical.id, type, rarity);
    }

    public boolean removePet(UUID playerId, UUID petId) {
        return delegate.removePet(playerId, petId);
    }

    public boolean equipPet(UUID playerId, UUID petId) {
        return delegate.equipPet(playerId, petId);
    }

    public boolean unequipPet(UUID playerId) {
        return delegate.unequipPet(playerId);
    }

    public UUID getActivePetId(UUID playerId) {
        return delegate.getActivePetId(playerId);
    }

    public List<Pet> getPets(UUID playerId) {
        return delegate.getPets(playerId).stream()
                .map(p -> new Pet(p.id, PetType.valueOf(p.type.name()), PetRarity.valueOf(p.rarity.name())))
                .collect(java.util.stream.Collectors.toUnmodifiableList());
    }

    public PetData gainXP(UUID playerId, PetType pet, double amount) {
        long total = delegate.addExperience(playerId, pet.toCanonical(), (long) amount);
        int level = delegate.getLevel(playerId, pet.toCanonical());
        return new PetData(pet, total, level);
    }

    public PetData getPetData(UUID playerId, PetType pet) {
        long xp = delegate.getExperience(playerId, pet.toCanonical());
        int level = delegate.getLevel(playerId, pet.toCanonical());
        return new PetData(pet, xp, level);
    }

    public void recordPetEvent(UUID playerId, String summary) {
        delegate.recordPetEvent(playerId, summary);
    }

    public List<String> getPetHistory(UUID playerId) {
        return delegate.getPetHistory(playerId);
    }

    public Map<UUID, List<String>> getAllPetHistory() {
        return delegate.getAllPetHistory();
    }

    public void load(File dataFolder) {
        delegate.load(dataFolder);
    }

    public void save(File dataFolder) {
        delegate.save(dataFolder);
    }

    public boolean reset(UUID playerId) {
        return delegate.reset(playerId);
    }
}
