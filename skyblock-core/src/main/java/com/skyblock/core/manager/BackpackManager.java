package com.skyblock.core.manager;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * Singleton managing per-player backpacks (real item-storage containers).
 *
 * <p>Each player has a {@link BackpackTier} that sizes their backpack
 * {@link Inventory}; items are placed/taken directly. Tier and contents persist
 * to {@code backpack.yml} (per-slot {@link ItemStack} serialization). One
 * backpack per player. Not thread-safe.</p>
 */
public final class BackpackManager {

    /** Backpack sizes available to players (wiki: up to Jumbo = 45). */
    public enum BackpackTier {
        SMALL(9),
        MEDIUM(18),
        LARGE(27),
        GREATER(36),
        JUMBO(45);

        /** Number of item slots provided by this tier. */
        public final int slots;

        BackpackTier(int slots) {
            this.slots = slots;
        }

        public int getSlots() {
            return slots;
        }
    }

    private static final String FILE_NAME = "backpack.yml";

    private static final BackpackManager INSTANCE = new BackpackManager();

    /** Per-player backpack tier; absent entries default to {@link BackpackTier#SMALL}. */
    private final Map<UUID, BackpackTier> playerTiers = new HashMap<>();
    /** Per-player backpack container, sized to the player's tier. */
    private final Map<UUID, Inventory> backpacks = new HashMap<>();

    private BackpackManager() {
    }

    /** Returns the single shared {@code BackpackManager} instance. */
    public static BackpackManager getInstance() {
        return INSTANCE;
    }

    /** Returns the player's tier, defaulting to {@link BackpackTier#SMALL}. */
    public BackpackTier getTier(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        return playerTiers.getOrDefault(playerId, BackpackTier.SMALL);
    }

    /** Sets the player's tier, growing the backpack container (preserving items) if it changed. */
    public void setTier(UUID playerId, BackpackTier tier) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(tier, "tier");
        playerTiers.put(playerId, tier);
        Inventory existing = backpacks.get(playerId);
        if (existing != null && existing.getSize() != tier.slots) {
            Inventory resized = Bukkit.createInventory(null, tier.slots, title(tier));
            int n = Math.min(existing.getSize(), resized.getSize());
            for (int i = 0; i < n; i++) {
                resized.setItem(i, existing.getItem(i));
            }
            backpacks.put(playerId, resized);
        }
    }

    /** Returns the player's backpack container, creating it at their tier size on first access. */
    public Inventory getBackpack(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        return backpacks.computeIfAbsent(playerId,
                id -> Bukkit.createInventory(null, getTier(id).slots, title(getTier(id))));
    }

    /** Opens the player's real backpack container (place/take items directly). */
    public void open(Player player) {
        Objects.requireNonNull(player, "player");
        player.openInventory(getBackpack(player.getUniqueId()));
    }

    /** Removes all backpack data for the given player. Save first if it must survive. */
    public void remove(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        playerTiers.remove(playerId);
        backpacks.remove(playerId);
    }

    private static String title(BackpackTier tier) {
        String name = tier.name().charAt(0) + tier.name().substring(1).toLowerCase(Locale.ROOT);
        return name + " Backpack";
    }

    // -------------------------------------------------------------------------
    // Persistence
    // -------------------------------------------------------------------------

    public void load(File dataFolder) {
        File file = new File(dataFolder, FILE_NAME);
        if (!file.exists()) {
            return;
        }
        YamlConfiguration cfg = YamlConfiguration.loadConfiguration(file);
        playerTiers.clear();
        backpacks.clear();
        ConfigurationSection players = cfg.getConfigurationSection("players");
        if (players == null) {
            return;
        }
        for (String key : players.getKeys(false)) {
            UUID uuid;
            try {
                uuid = UUID.fromString(key);
            } catch (IllegalArgumentException e) {
                continue;
            }
            String tierName = players.getString(key + ".tier", "SMALL");
            BackpackTier tier;
            try {
                tier = BackpackTier.valueOf(tierName);
            } catch (IllegalArgumentException e) {
                tier = BackpackTier.SMALL;
            }
            playerTiers.put(uuid, tier);
            Inventory inv = getBackpack(uuid);
            ConfigurationSection items = players.getConfigurationSection(key + ".items");
            if (items != null) {
                for (String slotStr : items.getKeys(false)) {
                    int slot;
                    try {
                        slot = Integer.parseInt(slotStr);
                    } catch (NumberFormatException e) {
                        continue;
                    }
                    ItemStack item = items.getItemStack(slotStr);
                    if (item != null && slot >= 0 && slot < inv.getSize()) {
                        inv.setItem(slot, item);
                    }
                }
            }
        }
    }

    public void save(File dataFolder) {
        YamlConfiguration cfg = new YamlConfiguration();
        for (Map.Entry<UUID, BackpackTier> entry : playerTiers.entrySet()) {
            cfg.set("players." + entry.getKey() + ".tier", entry.getValue().name());
        }
        for (Map.Entry<UUID, Inventory> entry : backpacks.entrySet()) {
            ItemStack[] contents = entry.getValue().getContents();
            for (int i = 0; i < contents.length; i++) {
                if (contents[i] != null) {
                    cfg.set("players." + entry.getKey() + ".items." + i, contents[i]);
                }
            }
        }
        if (!dataFolder.exists()) {
            dataFolder.mkdirs();
        }
        try {
            cfg.save(new File(dataFolder, FILE_NAME));
        } catch (IOException e) {
            Bukkit.getLogger().warning("[SkyBlock] Failed to save backpacks: " + e.getMessage());
        }
    }
}
