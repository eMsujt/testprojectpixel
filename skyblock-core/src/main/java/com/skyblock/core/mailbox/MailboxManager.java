package com.skyblock.core.mailbox;

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
 * Singleton managing per-player mailbox item deliveries.
 *
 * <p>Not thread-safe; synchronize externally if accessed from multiple threads.</p>
 */
public final class MailboxManager {

    private static final MailboxManager INSTANCE = new MailboxManager();

    /** Pending deliveries: recipient UUID → list of item descriptions. */
    private final Map<UUID, List<String>> deliveries = new HashMap<>();

    private MailboxManager() {}

    public static MailboxManager getInstance() {
        return INSTANCE;
    }

    public void addDelivery(UUID recipient, String itemDescription) {
        Objects.requireNonNull(recipient, "recipient");
        Objects.requireNonNull(itemDescription, "itemDescription");
        deliveries.computeIfAbsent(recipient, k -> new ArrayList<>()).add(itemDescription);
    }

    public List<String> getDeliveries(UUID player) {
        Objects.requireNonNull(player, "player");
        return Collections.unmodifiableList(deliveries.getOrDefault(player, Collections.emptyList()));
    }

    public boolean claimAll(UUID player) {
        Objects.requireNonNull(player, "player");
        List<String> items = deliveries.remove(player);
        return items != null && !items.isEmpty();
    }

    public boolean hasDeliveries(UUID player) {
        Objects.requireNonNull(player, "player");
        List<String> items = deliveries.get(player);
        return items != null && !items.isEmpty();
    }

    // -------------------------------------------------------------------------
    // Persistence
    // -------------------------------------------------------------------------

    /**
     * Loads deliveries from {@code mailbox.yml} inside the given data folder.
     *
     * @param dataFolder the plugin data folder, must not be null
     */
    public void load(File dataFolder) {
        File file = new File(dataFolder, "mailbox.yml");
        if (!file.exists()) {
            return;
        }
        YamlConfiguration cfg = YamlConfiguration.loadConfiguration(file);
        deliveries.clear();
        if (cfg.isConfigurationSection("deliveries")) {
            for (String key : cfg.getConfigurationSection("deliveries").getKeys(false)) {
                try {
                    UUID uuid = UUID.fromString(key);
                    List<String> list = cfg.getStringList("deliveries." + key);
                    if (!list.isEmpty()) {
                        deliveries.put(uuid, new ArrayList<>(list));
                    }
                } catch (IllegalArgumentException ignored) {}
            }
        }
    }

    /**
     * Saves all deliveries to {@code mailbox.yml} inside the given data folder.
     *
     * @param dataFolder the plugin data folder, must not be null
     * @throws RuntimeException if the file cannot be written
     */
    public void save(File dataFolder) {
        File file = new File(dataFolder, "mailbox.yml");
        YamlConfiguration cfg = new YamlConfiguration();
        for (Map.Entry<UUID, List<String>> entry : deliveries.entrySet()) {
            cfg.set("deliveries." + entry.getKey().toString(), new ArrayList<>(entry.getValue()));
        }
        try {
            cfg.save(file);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save mailbox.yml", e);
        }
    }
}
