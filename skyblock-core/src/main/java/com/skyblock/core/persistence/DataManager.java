package com.skyblock.core.persistence;

import com.skyblock.core.manager.PlayerDataManager;
import com.skyblock.core.manager.PlayerDataManager.PlayerData;
import com.skyblock.core.storage.StorageManager;
import com.skyblock.core.storage.StorageManager.StorageData;
import com.skyblock.core.storage.YamlPlayerStorage;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

/**
 * Canonical façade for all player persistence: load, save, and in-memory query.
 *
 * <p>Delegates to the existing singletons so callers need only interact with
 * one class instead of three.  Call {@link #load(UUID)} on player join and
 * {@link #save(UUID)} on player quit/server stop.</p>
 */
public final class DataManager {

    private static final DataManager INSTANCE = new DataManager();

    private final PlayerDataManager playerDataManager = PlayerDataManager.getInstance();
    private final StorageManager storageManager = StorageManager.getInstance();
    private final YamlPlayerStorage yamlStorage = YamlPlayerStorage.getInstance();

    private DataManager() {}

    /**
     * Returns the single shared {@code DataManager} instance.
     *
     * @return the singleton instance
     */
    public static DataManager getInstance() {
        return INSTANCE;
    }

    // -------------------------------------------------------------------------
    // Load / Save
    // -------------------------------------------------------------------------

    /**
     * Loads persisted data for the given player into the in-memory cache.
     *
     * <p>Creates a fresh {@link PlayerData} entry if none exists and populates
     * it from the player's YAML file on disk.  Safe to call multiple times —
     * subsequent calls reload disk data into the existing cache entry.</p>
     *
     * @param uuid the player's unique identifier
     */
    public void load(UUID uuid) {
        Objects.requireNonNull(uuid, "uuid");
        PlayerData data = playerDataManager.getOrCreate(uuid);
        yamlStorage.load(data);
    }

    /**
     * Saves the in-memory data for the given player to disk.
     *
     * <p>No-op if the player has no cached entry.</p>
     *
     * @param uuid the player's unique identifier
     */
    public void save(UUID uuid) {
        Objects.requireNonNull(uuid, "uuid");
        playerDataManager.get(uuid).ifPresent(yamlStorage::save);
    }

    /**
     * Saves and evicts the in-memory data for the given player.
     *
     * <p>Intended for use on player quit so the cache stays lean.</p>
     *
     * @param uuid the player's unique identifier
     */
    public void saveAndEvict(UUID uuid) {
        Objects.requireNonNull(uuid, "uuid");
        save(uuid);
        playerDataManager.remove(uuid);
    }

    // -------------------------------------------------------------------------
    // Query
    // -------------------------------------------------------------------------

    /**
     * Returns the cached {@link PlayerData} for the given player.
     *
     * @param uuid the player's unique identifier
     * @return the cached data, or empty if no entry exists
     */
    public Optional<PlayerData> getPlayerData(UUID uuid) {
        Objects.requireNonNull(uuid, "uuid");
        return playerDataManager.get(uuid);
    }

    /**
     * Returns the cached {@link PlayerData} for the given player, creating an
     * entry (not loaded from disk) if none exists.
     *
     * @param uuid the player's unique identifier
     * @return the existing or new {@link PlayerData}
     */
    public PlayerData getOrCreatePlayerData(UUID uuid) {
        Objects.requireNonNull(uuid, "uuid");
        return playerDataManager.getOrCreate(uuid);
    }

    /**
     * Returns the {@link StorageData} for the given player, creating a default
     * entry (1 page) if none exists.
     *
     * @param uuid the player's unique identifier
     * @return the player's storage state
     */
    public StorageData getStorageData(UUID uuid) {
        Objects.requireNonNull(uuid, "uuid");
        return storageManager.getStorage(uuid);
    }
}
