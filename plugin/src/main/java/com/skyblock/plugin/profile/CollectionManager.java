package com.skyblock.plugin.profile;

import java.util.Objects;
import java.util.UUID;

/**
 * Thin facade over {@link ProfileManager} for accumulating collection progress.
 *
 * <p>All progress is stored in the player's {@link PlayerProfile} collection map
 * keyed by material name, so it is persisted alongside the rest of the profile by
 * {@link ProfileManager#saveAsync(UUID)}. Like the underlying profile, this must
 * be used from the server main thread.</p>
 */
public final class CollectionManager {

    private static final CollectionManager INSTANCE = new CollectionManager();

    private CollectionManager() {}

    public static CollectionManager getInstance() {
        return INSTANCE;
    }

    /**
     * Adds the given amount to the player's collection for the given material and
     * persists the updated profile.
     *
     * @param playerId unique identifier of the player
     * @param material the collection material name
     * @param amount the amount to add, must not be negative
     * @return the player's new total for that collection
     * @throws IllegalArgumentException if {@code amount} is negative
     */
    public long incrementCollection(UUID playerId, String material, int amount) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(material, "material");
        if (amount < 0) {
            throw new IllegalArgumentException("amount must not be negative, got " + amount);
        }

        ProfileManager profiles = ProfileManager.getInstance();
        PlayerProfile profile = profiles.getOrCreate(playerId);
        profile.addCollectionXp(material, amount);
        profiles.saveAsync(playerId);
        return profile.getCollectionXp(material);
    }

    /**
     * Returns the player's accumulated progress in the given collection, or 0 if
     * none has been recorded.
     *
     * @param playerId unique identifier of the player
     * @param material the collection material name
     * @return the current total
     */
    public long getCollection(UUID playerId, String material) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(material, "material");
        PlayerProfile profile = ProfileManager.getInstance().getProfile(playerId);
        return profile == null ? 0L : profile.getCollectionXp(material);
    }
}
