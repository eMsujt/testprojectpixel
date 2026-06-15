package com.skyblock.plugin.pet;

import com.skyblock.core.manager.PetManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * @deprecated Use {@link PetManager} directly.
 *
 * <p>Retained for backward compatibility only. All methods delegate to
 * {@link PetManager#getInstance()}.</p>
 */
@Deprecated
public final class PetManager {

    /** A single equipped pet. */
    public static final class ActivePet {
        private final UUID id;
        private final String name;
        private final String rarity;
        private final int level;

        public ActivePet(UUID id, String name, String rarity, int level) {
            this.id = id;
            this.name = name;
            this.rarity = rarity;
            this.level = level;
        }

        public ActivePet(String name, String rarity, int level) {
            this(UUID.randomUUID(), name, rarity, level);
        }

        public UUID getId() { return id; }
        public String getName() { return name; }
        public String getRarity() { return rarity; }
        public int getLevel() { return level; }
    }

    private static final PetManager INSTANCE = new PetManager();
    private final com.skyblock.core.manager.PetManager delegate =
            com.skyblock.core.manager.PetManager.getInstance();

    private PetManager() {
    }

    public static PetManager getInstance() {
        return INSTANCE;
    }

    public void addPet(UUID playerId, ActivePet pet) {
        try {
            com.skyblock.core.manager.PetManager.PetType type =
                    com.skyblock.core.manager.PetManager.PetType.valueOf(
                            pet.getName().toUpperCase().replace(' ', '_'));
            com.skyblock.core.manager.PetManager.PetRarity rarity =
                    com.skyblock.core.manager.PetManager.PetRarity.valueOf(pet.getRarity());
            delegate.addPet(playerId, type, rarity);
        } catch (IllegalArgumentException ignored) {
            // unknown type or rarity — skip silently
        }
    }

    public ActivePet removePet(UUID playerId, UUID petId) {
        com.skyblock.core.manager.PetManager.Pet removed =
                delegate.getPets(playerId).stream()
                        .filter(p -> p.id.equals(petId))
                        .findFirst()
                        .orElse(null);
        if (removed == null) {
            return null;
        }
        delegate.removePet(playerId, petId);
        int level = delegate.getLevel(playerId, removed.type);
        return new ActivePet(removed.id, removed.type.getDisplayName(), removed.rarity.name(), level);
    }

    public List<ActivePet> getPets(UUID playerId) {
        List<ActivePet> result = new ArrayList<>();
        for (com.skyblock.core.manager.PetManager.Pet pet : delegate.getPets(playerId)) {
            int level = delegate.getLevel(playerId, pet.type);
            result.add(new ActivePet(pet.id, pet.type.getDisplayName(), pet.rarity.name(), level));
        }
        return Collections.unmodifiableList(result);
    }

    public boolean equip(UUID playerId, UUID petId) {
        return delegate.equipPet(playerId, petId);
    }

    public UUID unequip(UUID playerId) {
        UUID active = delegate.getActivePetId(playerId);
        delegate.unequipPet(playerId);
        return active;
    }

    public UUID getActivePetId(UUID playerId) {
        return delegate.getActivePetId(playerId);
    }

    public ActivePet getActivePet(UUID playerId) {
        com.skyblock.core.manager.PetManager.Pet pet = delegate.getActivePet(playerId);
        if (pet == null) {
            return null;
        }
        int level = delegate.getLevel(playerId, pet.type);
        return new ActivePet(pet.id, pet.type.getDisplayName(), pet.rarity.name(), level);
    }

    public boolean hasActivePet(UUID playerId) {
        return delegate.getActivePetId(playerId) != null;
    }
}
