package com.skyblock.core.pet;

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
        CHICKEN, WORM, SHEEP, PIG, COW, BAT, SNAIL, ROCK, MOSQUITO, SLUG,
        RABBIT, PENGUIN, HORSE, MULE, DONKEY, GOAT, SILVERFISH, CAVE_SPIDER, MUSHROOM_COW, HOUND,
        BEE, DOLPHIN, SQUID, FLYING_FISH, CAT, PARROT, MONKEY, GUARDIAN, OCELOT, TURTLE,
        ELEPHANT, GIRAFFE, LION, CREEPER, ZOMBIE, SKELETON, SPIDER, ENDERMITE, PIGMAN,
        WOLF, BLAZE, MAGMA_CUBE, ENDERMAN, GHAST, GOLEM, WITHER_SKELETON, TARANTULA,
        BABY_YETI, BLUE_WHALE, TIGER, WISP, SNOWMAN, ARMADILLO, AMMONITE, SPINOCLAW,
        GRIFFIN, GOLDEN_DRAGON, BLUE_SHARK, JERRY, BLACK_CAT, GRANDMA_WOLF, ENDER_DRAGON, PHOENIX;

        public com.skyblock.core.manager.PetManager.PetType toCanonical() {
            // MUSHROOM_COW was renamed MOOSHROOM_COW in the canonical enum
            if (this == MUSHROOM_COW) {
                return com.skyblock.core.manager.PetManager.PetType.MOOSHROOM_COW;
            }
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

    public void setActivePet(UUID playerId, PetType type) {
        com.skyblock.core.manager.PetManager.PetType canonical = type.toCanonical();
        com.skyblock.core.manager.PetManager.Pet pet =
                delegate.addPet(playerId, canonical, canonical.defaultRarity);
        delegate.equipPet(playerId, pet.id);
    }

    public PetData getActivePet(UUID playerId) {
        com.skyblock.core.manager.PetManager.Pet pet = delegate.getActivePet(playerId);
        if (pet == null) {
            return null;
        }
        long xp = delegate.getExperience(playerId, pet.type);
        return new PetData(PetType.valueOf(pet.type.name()), PetRarity.valueOf(pet.rarity.name()), xp);
    }

    public boolean removeActivePet(UUID playerId) {
        return delegate.unequipPet(playerId);
    }

    public boolean reset(UUID playerId) {
        return delegate.reset(playerId);
    }
}
