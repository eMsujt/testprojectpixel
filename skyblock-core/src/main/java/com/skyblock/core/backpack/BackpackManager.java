package com.skyblock.core.backpack;

import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * Singleton managing per-player backpacks (named item storage bags).
 *
 * <p>Each player has a {@link BackpackTier} that determines how many item-name
 * slots are available.  Tier and items are persisted to
 * {@code plugins/SkyblockCore/backpack.yml}.
 * Not thread-safe; synchronize externally if accessed from multiple threads.</p>
 */
public final class BackpackManager {

    /** Backpack sizes available to players. */
    public enum BackpackTier {
        SMALL(9),
        MEDIUM(18),
        LARGE(27),
        JUMBO(36);

        /** Number of item slots provided by this tier. */
        public final int slots;

        BackpackTier(int slots) {
            this.slots = slots;
        }

        public int getSlots() {
            return slots;
        }
    }

    private static final BackpackManager INSTANCE = new BackpackManager();

    /** Per-player backpack tier; absent entries default to {@link BackpackTier#SMALL}. */
    private final Map<UUID, BackpackTier> playerTiers = new HashMap<>();

    /** Per-player list of item names stored in the backpack. */
    private final Map<UUID, List<String>> playerItems = new HashMap<>();

    private BackpackManager() {
    }

    /**
     * Returns the single shared {@code BackpackManager} instance.
     *
     * @return the singleton instance
     */
    public static BackpackManager getInstance() {
        return INSTANCE;
    }

    // -------------------------------------------------------------------------
    // Tier
    // -------------------------------------------------------------------------

    /**
     * Returns the player's current backpack tier, defaulting to {@link BackpackTier#SMALL}.
     *
     * @param playerId the player's UUID, must not be null
     * @return the player's tier
     */
    public BackpackTier getTier(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        return playerTiers.getOrDefault(playerId, BackpackTier.SMALL);
    }

    /**
     * Sets the player's backpack tier.
     *
     * @param playerId the player's UUID, must not be null
     * @param tier     the tier to set, must not be null
     */
    public void setTier(UUID playerId, BackpackTier tier) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(tier, "tier");
        playerTiers.put(playerId, tier);
    }

    // -------------------------------------------------------------------------
    // Items
    // -------------------------------------------------------------------------

    /**
     * Returns an unmodifiable view of the item names stored in the player's backpack.
     *
     * @param playerId the player's UUID, must not be null
     * @return unmodifiable list of item names, never {@code null}
     */
    public List<String> getItems(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        List<String> items = playerItems.get(playerId);
        return items == null ? Collections.emptyList() : Collections.unmodifiableList(items);
    }

    /**
     * Adds an item name to the player's backpack if capacity allows.
     *
     * @param playerId the player's UUID, must not be null
     * @param itemName the item name to add, must not be null
     * @return {@code true} if added, {@code false} if the backpack is full
     */
    public boolean addItem(UUID playerId, String itemName) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(itemName, "itemName");
        List<String> items = playerItems.computeIfAbsent(playerId, id -> new ArrayList<>());
        int capacity = getTier(playerId).slots;
        if (items.size() >= capacity) {
            return false;
        }
        items.add(itemName);
        return true;
    }

    /**
     * Removes the first occurrence of the given item name from the player's backpack.
     *
     * @param playerId the player's UUID, must not be null
     * @param itemName the item name to remove, must not be null
     * @return {@code true} if the item was present and removed
     */
    public boolean removeItem(UUID playerId, String itemName) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(itemName, "itemName");
        List<String> items = playerItems.get(playerId);
        return items != null && items.remove(itemName);
    }

    /**
     * Removes all item data for the given player (e.g. on quit).
     *
     * @param playerId the player to remove
     */
    public void remove(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        playerTiers.remove(playerId);
        playerItems.remove(playerId);
    }

    // -------------------------------------------------------------------------
    // Persistence
    // -------------------------------------------------------------------------

    public void load(File dataFolder) {
        File file = new File(dataFolder, "backpack.yml");
        if (!file.exists()) {
            return;
        }
        YamlConfiguration cfg = YamlConfiguration.loadConfiguration(file);
        playerTiers.clear();
        playerItems.clear();
        if (cfg.isConfigurationSection("players")) {
            for (String key : cfg.getConfigurationSection("players").getKeys(false)) {
                try {
                    UUID uuid = UUID.fromString(key);
                    String tierName = cfg.getString("players." + key + ".tier");
                    if (tierName != null) {
                        try {
                            playerTiers.put(uuid, BackpackTier.valueOf(tierName));
                        } catch (IllegalArgumentException ignored) {
                            // skip unknown tier name
                        }
                    }
                    List<String> items = cfg.getStringList("players." + key + ".items");
                    if (!items.isEmpty()) {
                        playerItems.put(uuid, new ArrayList<>(items));
                    }
                } catch (IllegalArgumentException ignored) {
                    // skip malformed UUID
                }
            }
        }
    }

    public void save(File dataFolder) {
        File file = new File(dataFolder, "backpack.yml");
        YamlConfiguration cfg = new YamlConfiguration();
        for (Map.Entry<UUID, BackpackTier> entry : playerTiers.entrySet()) {
            cfg.set("players." + entry.getKey().toString() + ".tier", entry.getValue().name());
        }
        for (Map.Entry<UUID, List<String>> entry : playerItems.entrySet()) {
            cfg.set("players." + entry.getKey().toString() + ".items", entry.getValue());
        }
        try {
            cfg.save(file);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save backpack.yml", e);
        }
    }
}
