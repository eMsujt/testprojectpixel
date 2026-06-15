package com.skyblock.core.pets;

import com.skyblock.core.manager.PetManager;

import java.io.File;
import java.util.Collections;
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
        CAT, DOG, BEE, RABBIT, ENDERMAN, WOLF, BLAZE, SPIDER;

        public PetManager.PetType toCanonical() {
            return PetManager.PetType.valueOf(this.name());
        }
    }

    public static final int MAX_LEVEL = PetManager.MAX_LEVEL;
    public static final Map<String, int[]> PET_DATA = PetManager.PET_DATA;

    public static final class Pet {
        public final UUID id;
        public final PetType type;
        public int level;

        public Pet(PetType type) {
            this(UUID.randomUUID(), type, 1);
        }

        public Pet(UUID id, PetType type, int level) {
            this.id = id;
            this.type = type;
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
