package com.skyblock.core.manager;

import com.skyblock.core.backpack.BackpackManager;
import com.skyblock.core.backpack.BackpackManager.BackpackTier;
import com.skyblock.core.vault.VaultManager;

import java.io.File;
import java.util.Objects;
import java.util.UUID;

/**
 * Canonical personal-storage coordinator.
 *
 * <p>A player's stored items live across three domain managers that this class
 * ties together as a single entry point:</p>
 * <ul>
 *   <li>{@link com.skyblock.core.storage.StorageManager} — ender chest
 *       <em>pages</em> the player has unlocked.</li>
 *   <li>{@link BackpackManager} — backpack {@link BackpackTier tiers} and the
 *       item slots each tier provides.</li>
 *   <li>{@link VaultManager} — the player's personal vault.</li>
 * </ul>
 *
 * <p>It does not duplicate their state; it composes them and owns the
 * cross-cutting <em>item persistence</em> wiring, loading and saving every
 * storage domain that owns its own files in a single call.</p>
 *
 * <p>Not thread-safe; synchronize externally if accessed from multiple threads.</p>
 */
public final class StorageManager {

    private static final StorageManager INSTANCE = new StorageManager();

    private final com.skyblock.core.storage.StorageManager pageStore;
    private final BackpackManager backpackManager;
    private final VaultManager vaultManager;

    private StorageManager() {
        this(com.skyblock.core.storage.StorageManager.getInstance(),
                BackpackManager.getInstance(),
                VaultManager.getInstance());
    }

    // Package-private for tests that need isolated sub-managers.
    StorageManager(com.skyblock.core.storage.StorageManager pageStore,
                   BackpackManager backpackManager,
                   VaultManager vaultManager) {
        this.pageStore = Objects.requireNonNull(pageStore, "pageStore");
        this.backpackManager = Objects.requireNonNull(backpackManager, "backpackManager");
        this.vaultManager = Objects.requireNonNull(vaultManager, "vaultManager");
    }

    public static StorageManager getInstance() {
        return INSTANCE;
    }

    /** The ender-chest page store. */
    public com.skyblock.core.storage.StorageManager pages() {
        return pageStore;
    }

    /** The backpack tier/slot manager. */
    public BackpackManager backpacks() {
        return backpackManager;
    }

    /** The personal vault. */
    public VaultManager vault() {
        return vaultManager;
    }

    // -------------------------------------------------------------------------
    // Convenience pass-throughs
    // -------------------------------------------------------------------------

    /** The number of ender-chest pages the player has unlocked. */
    public int getUnlockedPages(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        return pageStore.getStorage(playerId).getUnlockedPages();
    }

    /**
     * Unlocks one additional ender-chest page for the player.
     *
     * @return {@code true} if a page was added, {@code false} if already at max
     */
    public boolean unlockPage(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        return pageStore.unlockPage(playerId);
    }

    /** The player's backpack tier. */
    public BackpackTier getBackpackTier(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        return backpackManager.getTier(playerId);
    }

    // -------------------------------------------------------------------------
    // Item persistence
    // -------------------------------------------------------------------------

    /**
     * Loads every storage domain that owns its own files from {@code dataFolder}.
     * The ender-chest page store is persisted per-player via the data layer and
     * therefore has nothing to load here.
     */
    public void loadAll(File dataFolder) {
        Objects.requireNonNull(dataFolder, "dataFolder");
        backpackManager.load(dataFolder);
        vaultManager.load(dataFolder);
    }

    /** Saves every storage domain that owns its own files to {@code dataFolder}. */
    public void saveAll(File dataFolder) {
        Objects.requireNonNull(dataFolder, "dataFolder");
        backpackManager.save(dataFolder);
        vaultManager.save(dataFolder);
    }

    /** A one-line summary of the player's storage holdings. */
    public String getSummary(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        return "Pages: " + getUnlockedPages(playerId)
                + " | Backpack: " + getBackpackTier(playerId).name()
                + " | Vault: " + vaultManager.getBalance(playerId);
    }
}
