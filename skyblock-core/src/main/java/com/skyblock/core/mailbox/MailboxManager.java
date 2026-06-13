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

    public record MailboxItem(String sender, String message) {
        public MailboxItem {
            Objects.requireNonNull(sender, "sender");
            Objects.requireNonNull(message, "message");
        }
    }

    private static final MailboxManager INSTANCE = new MailboxManager();

    /** Pending deliveries: recipient UUID → ordered list of mail items. */
    private final Map<UUID, List<MailboxItem>> deliveries = new HashMap<>();

    private MailboxManager() {}

    public static MailboxManager getInstance() {
        return INSTANCE;
    }

    public void addDelivery(UUID recipient, String sender, String message) {
        Objects.requireNonNull(recipient, "recipient");
        deliveries.computeIfAbsent(recipient, k -> new ArrayList<>()).add(new MailboxItem(sender, message));
    }

    public List<MailboxItem> getDeliveries(UUID player) {
        Objects.requireNonNull(player, "player");
        return Collections.unmodifiableList(deliveries.getOrDefault(player, Collections.emptyList()));
    }

    public boolean claimAll(UUID player) {
        Objects.requireNonNull(player, "player");
        List<MailboxItem> items = deliveries.remove(player);
        return items != null && !items.isEmpty();
    }

    public boolean hasDeliveries(UUID player) {
        Objects.requireNonNull(player, "player");
        List<MailboxItem> items = deliveries.get(player);
        return items != null && !items.isEmpty();
    }

    // -------------------------------------------------------------------------
    // Persistence
    // -------------------------------------------------------------------------

    /**
     * Loads deliveries from per-player files in {@code data/mailbox/<uuid>.yml}.
     *
     * @param dataFolder the plugin data folder, must not be null
     */
    public void load(File dataFolder) {
        File mailboxDir = new File(dataFolder, "data/mailbox");
        deliveries.clear();
        if (!mailboxDir.isDirectory()) {
            return;
        }
        File[] files = mailboxDir.listFiles((d, name) -> name.endsWith(".yml"));
        if (files == null) {
            return;
        }
        for (File file : files) {
            String name = file.getName();
            String uuidStr = name.substring(0, name.length() - 4);
            try {
                UUID uuid = UUID.fromString(uuidStr);
                YamlConfiguration cfg = YamlConfiguration.loadConfiguration(file);
                List<Map<?, ?>> rawList = cfg.getMapList("messages");
                List<MailboxItem> items = new ArrayList<>();
                for (Map<?, ?> entry : rawList) {
                    String sender = entry.containsKey("sender") ? String.valueOf(entry.get("sender")) : "";
                    String message = entry.containsKey("message") ? String.valueOf(entry.get("message")) : "";
                    items.add(new MailboxItem(sender, message));
                }
                if (!items.isEmpty()) {
                    deliveries.put(uuid, items);
                }
            } catch (IllegalArgumentException ignored) {}
        }
    }

    /**
     * Saves each player's deliveries to {@code data/mailbox/<uuid>.yml}.
     *
     * @param dataFolder the plugin data folder, must not be null
     * @throws RuntimeException if a file cannot be written
     */
    public void save(File dataFolder) {
        File mailboxDir = new File(dataFolder, "data/mailbox");
        mailboxDir.mkdirs();
        for (Map.Entry<UUID, List<MailboxItem>> entry : deliveries.entrySet()) {
            File file = new File(mailboxDir, entry.getKey().toString() + ".yml");
            YamlConfiguration cfg = new YamlConfiguration();
            List<Map<String, String>> rawList = new ArrayList<>();
            for (MailboxItem item : entry.getValue()) {
                Map<String, String> map = new HashMap<>();
                map.put("sender", item.sender());
                map.put("message", item.message());
                rawList.add(map);
            }
            cfg.set("messages", rawList);
            try {
                cfg.save(file);
            } catch (IOException e) {
                throw new RuntimeException("Failed to save mailbox for " + entry.getKey(), e);
            }
        }
    }
}
