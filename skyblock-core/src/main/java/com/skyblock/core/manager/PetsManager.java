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

    /** Every pet type available, with its display name and default rarity. */
    public enum PetType {
        RABBIT("Rabbit", PetRarity.UNCOMMON),
        BEE("Bee", PetRarity.RARE),
        ELEPHANT("Elephant", PetRarity.RARE),
        WOLF("Wolf", PetRarity.EPIC),
        ENDERMAN("Enderman", PetRarity.EPIC),
        TIGER("Tiger", PetRarity.EPIC);

        private final String displayName;
        private final PetRarity defaultRarity;

        PetType(String displayName, PetRarity defaultRarity) {
            this.displayName = displayName;
            this.defaultRarity = defaultRarity;
        }

        public String getDisplayName() {
            return displayName;
        }

        public PetRarity getDefaultRarity() {
            return defaultRarity;
        }

        /** Creates a {@link Pet} of this type at its default rarity. */
        public Pet asPet() {
            return new Pet(displayName, defaultRarity);
        }
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
