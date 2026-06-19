package com.skyblock.core.manager;

import com.skyblock.core.backpack.BackpackManager;
import com.skyblock.core.backpack.BackpackManager.BackpackTier;
import com.skyblock.core.vault.VaultManager;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
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

    /** Number of item pages every player has. */
    public static final int PAGE_COUNT = 9;

    /** Number of {@link ItemStack} slots per storage page. */
    public static final int PAGE_SIZE = 45;

    private static final StorageManager INSTANCE = new StorageManager();

    private final com.skyblock.core.storage.StorageManager pageStore;
    private final BackpackManager backpackManager;
    private final VaultManager vaultManager;

    /** playerId → {@link #PAGE_COUNT} pages, each a {@link #PAGE_SIZE}-slot array (entries may be null) */
    private final Map<UUID, ItemStack[][]> items = new HashMap<>();

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
    // Item pages
    // -------------------------------------------------------------------------

    private ItemStack[][] itemsFor(UUID playerId) {
        return items.computeIfAbsent(playerId, id -> new ItemStack[PAGE_COUNT][PAGE_SIZE]);
    }

    private static void checkPage(int page) {
        if (page < 0 || page >= PAGE_COUNT) {
            throw new IndexOutOfBoundsException("page must be 0.." + (PAGE_COUNT - 1) + " but was " + page);
        }
    }

    /**
     * Returns a clone of the {@link #PAGE_SIZE}-slot contents of the given page.
     *
     * @param playerId the player's UUID, must not be null
     * @param page     the 0-based page index, must be in {@code [0, PAGE_COUNT)}
     * @return a cloned array of length {@link #PAGE_SIZE}; entries may be null
     */
    public ItemStack[] getPage(UUID playerId, int page) {
        Objects.requireNonNull(playerId, "playerId");
        checkPage(page);
        ItemStack[] src = itemsFor(playerId)[page];
        ItemStack[] copy = new ItemStack[PAGE_SIZE];
        for (int i = 0; i < PAGE_SIZE; i++) {
            copy[i] = src[i] != null ? src[i].clone() : null;
        }
        return copy;
    }

    /**
     * Replaces the contents of the given page with a clone of {@code contents}.
     * Missing or null entries clear the corresponding slot.
     *
     * @param playerId the player's UUID, must not be null
     * @param page     the 0-based page index, must be in {@code [0, PAGE_COUNT)}
     * @param contents the new slot contents, must not be null
     */
    public void setPage(UUID playerId, int page, ItemStack[] contents) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(contents, "contents");
        checkPage(page);
        ItemStack[] dest = itemsFor(playerId)[page];
        for (int i = 0; i < PAGE_SIZE; i++) {
            dest[i] = (i < contents.length && contents[i] != null) ? contents[i].clone() : null;
        }
    }

    /** Clears all stored item pages for the player. */
    public void resetItems(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        items.remove(playerId);
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
        loadItems(dataFolder);
    }

    /** Saves every storage domain that owns its own files to {@code dataFolder}. */
    public void saveAll(File dataFolder) {
        Objects.requireNonNull(dataFolder, "dataFolder");
        backpackManager.save(dataFolder);
        vaultManager.save(dataFolder);
        saveItems(dataFolder);
    }

    private void loadItems(File dataFolder) {
        File file = new File(dataFolder, "storage.yml");
        if (!file.exists()) {
            return;
        }
        YamlConfiguration cfg = YamlConfiguration.loadConfiguration(file);
        items.clear();
        for (String key : cfg.getKeys(false)) {
            try {
                UUID uuid = UUID.fromString(key);
                ItemStack[][] pages = new ItemStack[PAGE_COUNT][PAGE_SIZE];
                for (int page = 0; page < PAGE_COUNT; page++) {
                    for (int slot = 0; slot < PAGE_SIZE; slot++) {
                        pages[page][slot] = cfg.getItemStack(key + ".pages." + page + "." + slot);
                    }
                }
                items.put(uuid, pages);
            } catch (IllegalArgumentException ignored) {
                // skip malformed entries
            }
        }
    }

    private void saveItems(File dataFolder) {
        File file = new File(dataFolder, "storage.yml");
        YamlConfiguration cfg = new YamlConfiguration();
        for (Map.Entry<UUID, ItemStack[][]> entry : items.entrySet()) {
            String key = entry.getKey().toString();
            ItemStack[][] pages = entry.getValue();
            for (int page = 0; page < PAGE_COUNT; page++) {
                for (int slot = 0; slot < PAGE_SIZE; slot++) {
                    ItemStack stack = pages[page][slot];
                    if (stack != null) {
                        cfg.set(key + ".pages." + page + "." + slot, stack);
                    }
                }
            }
        }
        try {
            cfg.save(file);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save storage.yml", e);
        }
    }

    /** A one-line summary of the player's storage holdings. */
    public String getSummary(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        return "Pages: " + getUnlockedPages(playerId)
                + " | Backpack: " + getBackpackTier(playerId).name()
                + " | Vault: " + vaultManager.getBalance(playerId);
    }
}
