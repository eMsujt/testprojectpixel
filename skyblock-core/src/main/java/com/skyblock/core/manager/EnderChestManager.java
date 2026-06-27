package com.skyblock.core.manager;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

/**
 * Singleton tracking each player's SkyBlock Ender Chest storage.
 *
 * <p>Each player owns a single 54-slot {@link Inventory} — a real container the
 * player places items into and takes items out of directly. Chests are created
 * lazily, kept in memory for the session, and persisted to
 * {@code enderchests.yml} (per-slot {@link ItemStack} serialization).</p>
 *
 * <p>Not thread-safe; synchronize externally if accessed from multiple threads.</p>
 */
public final class EnderChestManager {

    /** Number of slots in a player's Ender Chest (a double chest). */
    public static final int CHEST_SIZE = 54;

    /** Title shown on the Ender Chest inventory. */
    public static final String CHEST_TITLE = "Ender Chest";

    private static final String FILE_NAME = "enderchests.yml";

    private static final EnderChestManager INSTANCE = new EnderChestManager();

    private final Map<UUID, Inventory> chests = new HashMap<>();

    private EnderChestManager() {
    }

    /** Returns the single shared {@code EnderChestManager} instance. */
    public static EnderChestManager getInstance() {
        return INSTANCE;
    }

    /**
     * Returns the Ender Chest inventory for a player, creating it lazily on first
     * access.
     */
    public Inventory getChest(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        return chests.computeIfAbsent(playerId,
                id -> Bukkit.createInventory(null, CHEST_SIZE, CHEST_TITLE));
    }

    /** Opens the player's real Ender Chest container (place/take items directly). */
    public void open(Player player) {
        Objects.requireNonNull(player, "player");
        player.openInventory(getChest(player.getUniqueId()));
    }

    /** Replaces the stored inventory for a player. */
    public void putChest(UUID playerId, Inventory inv) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(inv, "inv");
        chests.put(playerId, inv);
    }

    /** Returns whether an Ender Chest has been created for the given player. */
    public boolean hasChest(UUID playerId) {
        return chests.containsKey(playerId);
    }

    /** Discards a player's cached Ender Chest. Save first if the data must survive. */
    public void unload(UUID playerId) {
        chests.remove(playerId);
    }

    /** Loads all persisted Ender Chests from {@code dataFolder/enderchests.yml}. */
    public void load(File dataFolder) {
        File file = new File(dataFolder, FILE_NAME);
        if (!file.exists()) {
            return;
        }
        YamlConfiguration cfg = YamlConfiguration.loadConfiguration(file);
        for (String uuidStr : cfg.getKeys(false)) {
            UUID uuid;
            try {
                uuid = UUID.fromString(uuidStr);
            } catch (IllegalArgumentException e) {
                continue;
            }
            ConfigurationSection section = cfg.getConfigurationSection(uuidStr);
            if (section == null) {
                continue;
            }
            Inventory inv = getChest(uuid);
            for (String slotStr : section.getKeys(false)) {
                int slot;
                try {
                    slot = Integer.parseInt(slotStr);
                } catch (NumberFormatException e) {
                    continue;
                }
                ItemStack item = section.getItemStack(slotStr);
                if (item != null && slot >= 0 && slot < CHEST_SIZE) {
                    inv.setItem(slot, item);
                }
            }
        }
    }

    /** Writes all in-memory Ender Chests to {@code dataFolder/enderchests.yml}. */
    public void save(File dataFolder) {
        YamlConfiguration cfg = new YamlConfiguration();
        for (Map.Entry<UUID, Inventory> entry : chests.entrySet()) {
            ItemStack[] contents = entry.getValue().getContents();
            for (int i = 0; i < contents.length; i++) {
                if (contents[i] != null) {
                    cfg.set(entry.getKey() + "." + i, contents[i]);
                }
            }
        }
        if (!dataFolder.exists()) {
            dataFolder.mkdirs();
        }
        try {
            cfg.save(new File(dataFolder, FILE_NAME));
        } catch (IOException e) {
            Bukkit.getLogger().warning("[SkyBlock] Failed to save Ender Chests: " + e.getMessage());
        }
    }
}
