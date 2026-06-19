package com.skyblock.core.manager;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * Manages per-player pet collections.
 *
 * <p>Not thread-safe; access from the server main thread or guard externally.</p>
 */
public final class PetsManager {

    public enum PetRarity {
        COMMON, UNCOMMON, RARE, EPIC, LEGENDARY
    }

    public record Pet(String name, PetRarity rarity) {}

    private static final PetsManager INSTANCE = new PetsManager();

    private PetsManager() {}

    public static PetsManager getInstance() {
        return INSTANCE;
    }

    /** Returns the pets owned by the given player, in display order. */
    public List<Pet> getPets(UUID playerId) {
        return Collections.emptyList();
    }
}
