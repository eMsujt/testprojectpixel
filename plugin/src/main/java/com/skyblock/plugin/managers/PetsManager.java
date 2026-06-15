package com.skyblock.plugin.managers;

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

    public static final class Pet {
        private final UUID id;
        private final String name;
        private final String rarity;
        private final int level;

        public Pet(UUID id, String name, String rarity, int level) {
            this.id = id;
            this.name = name;
            this.rarity = rarity;
            this.level = level;
        }

        public Pet(String name, String rarity, int level) {
            this(UUID.randomUUID(), name, rarity, level);
        }

        public UUID getId() { return id; }
        public String getName() { return name; }
        public String getRarity() { return rarity; }
        public int getLevel() { return level; }
    }

    private static final PetsManager INSTANCE = new PetsManager();
    private final PetManager delegate = PetManager.getInstance();

    private PetsManager() {
    }

    public static PetsManager getInstance() {
        return INSTANCE;
    }

    public Pet getActivePet(UUID playerId) {
        PetManager.Pet pet = delegate.getActivePet(playerId);
        if (pet == null) {
            return null;
        }
        int level = delegate.getLevel(playerId, pet.type);
        return new Pet(pet.id, pet.type.getDisplayName(), pet.rarity.name(), level);
    }

    public void setActivePet(UUID playerId, Pet pet) {
        if (pet == null) {
            delegate.unequipPet(playerId);
            return;
        }
        try {
            PetManager.PetType type = PetManager.PetType.valueOf(pet.getName().toUpperCase().replace(' ', '_'));
            PetManager.PetRarity rarity = PetManager.PetRarity.valueOf(pet.getRarity());
            PetManager.Pet canonical = delegate.addPet(playerId, type, rarity);
            delegate.equipPet(playerId, canonical.id);
        } catch (IllegalArgumentException ignored) {
            // unknown pet type or rarity — skip silently
        }
    }

    public boolean clearActivePet(UUID playerId) {
        return delegate.unequipPet(playerId);
    }

    public List<String> getPetsHistory(UUID playerId) {
        return delegate.getPetHistory(playerId);
    }

    public Map<UUID, List<String>> getAllPetsHistory() {
        return delegate.getAllPetHistory();
    }

    public void recordPetEvent(UUID playerId, String summary) {
        delegate.recordPetEvent(playerId, summary);
    }

    public void load(File dataFolder) {
        delegate.load(dataFolder);
    }

    public void save(File dataFolder) {
        delegate.save(dataFolder);
    }
}
