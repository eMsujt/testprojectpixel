package com.skyblock.core.storage;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * Singleton managing per-player personal storage metadata.
 *
 * <p>Tracks how many storage pages each player has unlocked.
 * Not thread-safe; synchronize externally if accessed from multiple threads.</p>
 */
public final class StorageManager {

    /** Maximum pages a player can unlock. */
    public static final int MAX_PAGES = 15;

    /** Mutable storage state for a single player. */
    public static final class StorageData {
        private final UUID owner;
        private int unlockedPages;

        public StorageData(UUID owner, int unlockedPages) {
            this.owner = Objects.requireNonNull(owner, "owner");
            this.unlockedPages = Math.max(1, Math.min(unlockedPages, MAX_PAGES));
        }

        public UUID getOwner() {
            return owner;
        }

        public int getUnlockedPages() {
            return unlockedPages;
        }

        public void setUnlockedPages(int pages) {
            this.unlockedPages = Math.max(1, Math.min(pages, MAX_PAGES));
        }
    }

    private static final StorageManager INSTANCE = new StorageManager();

    private final Map<UUID, StorageData> storageMap = new HashMap<>();

    private StorageManager() {}

    /**
     * Returns the single shared {@code StorageManager} instance.
     *
     * @return the singleton instance
     */
    public static StorageManager getInstance() {
        return INSTANCE;
    }

    /**
     * Returns the storage data for the given player, creating a default entry
     * (1 page) if none exists.
     *
     * @param playerId the player's UUID, must not be null
     * @return the player's {@link StorageData}
     */
    public StorageData getStorage(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        return storageMap.computeIfAbsent(playerId, id -> new StorageData(id, 1));
    }

    /**
     * Unlocks one additional storage page for the player, up to {@link #MAX_PAGES}.
     *
     * @param playerId the player's UUID, must not be null
     * @return {@code true} if a page was added, {@code false} if already at max
     */
    public boolean unlockPage(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        StorageData data = getStorage(playerId);
        if (data.getUnlockedPages() >= MAX_PAGES) {
            return false;
        }
        data.setUnlockedPages(data.getUnlockedPages() + 1);
        return true;
    }

    /**
     * Resets the given player's storage data to the default (1 page).
     *
     * @param playerId the player's UUID, must not be null
     */
    public void resetStorage(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        storageMap.remove(playerId);
    }

    /** Removes all stored data. */
    public void clear() {
        storageMap.clear();
    }
}
