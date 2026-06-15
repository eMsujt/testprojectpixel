package com.skyblock.core.pets;

import com.skyblock.core.manager.PetManager;

import java.util.UUID;

/**
 * @deprecated Use {@link PetManager} directly.
 *
 * <p>Retained for backward compatibility only. All methods delegate to
 * {@link PetManager#getInstance()}.</p>
 */
@Deprecated
public final class PetManager {

    public static final int MAX_LEVEL = com.skyblock.core.manager.PetManager.MAX_LEVEL;

    public enum PetRarity {
        COMMON, UNCOMMON, RARE, EPIC, LEGENDARY;

        public com.skyblock.core.manager.PetManager.PetRarity toCanonical() {
            return com.skyblock.core.manager.PetManager.PetRarity.valueOf(this.name());
        }
    }

    public enum PetType {
        BEE, RABBIT, WOLF, LION, TIGER, DOLPHIN, DOG, ELEPHANT, HORSE, CAT,
        PARROT, PENGUIN, TURTLE, SHEEP, PIG, CHICKEN, BLAZE, ENDERMAN,
        SKELETON, SPIDER, ZOMBIE, GOLDEN_DRAGON, ROCK, SILVERFISH, FLYING_FISH,
        BAT, SQUID, JELLYFISH, ENDER_DRAGON, BLACK_CAT, BABY_YETI, PHOENIX,
        GHOUL, JERRY, SLUG, ARMADILLO, DROPLET_WISP, PIGMAN, HOUND,
        WITHER_SKELETON, GOLEM, OCELOT, MONKEY, GIRAFFE, HEDGEHOG,
        GUARDIAN, SNOWMAN, SCARECROW, MOOSHROOM_COW, MITHRIL_GOLEM,
        SUMO, ENDERMITE;

        public com.skyblock.core.manager.PetManager.PetType toCanonical() {
            return com.skyblock.core.manager.PetManager.PetType.valueOf(this.name());
        }
    }

    public static final class PetData {
        public final PetType type;
        public final PetRarity rarity;
        public final long experience;

        public PetData(PetType type, PetRarity rarity, long experience) {
            this.type = type;
            this.rarity = rarity;
            this.experience = experience;
        }

        public int getLevel() {
            return com.skyblock.core.manager.PetManager.getInstance()
                    .getLevel(null, type.toCanonical());
        }
    }

    private static final PetManager INSTANCE = new PetManager();
    private final com.skyblock.core.manager.PetManager delegate =
            com.skyblock.core.manager.PetManager.getInstance();

    private PetManager() {
    }

    public static PetManager getInstance() {
        return INSTANCE;
    }

    public long addExperience(UUID playerId, PetType type, long amount) {
        return delegate.addExperience(playerId, type.toCanonical(), amount);
    }

    public long getExperience(UUID playerId, PetType type) {
        return delegate.getExperience(playerId, type.toCanonical());
    }

    public int getLevel(UUID playerId, PetType type) {
        return delegate.getLevel(playerId, type.toCanonical());
    }

    public boolean removeActivePet(UUID playerId) {
        return delegate.unequipPet(playerId);
    }

    public boolean reset(UUID playerId) {
        return delegate.reset(playerId);
    }
}
