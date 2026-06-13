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
 * Singleton managing per-player mailbox inboxes with YAML persistence.
 *
 * <p>Not thread-safe; synchronize externally if accessed from multiple threads.</p>
 */
public final class MailboxManager {

    /** Immutable mail message record. */
    public static final class MailMessage {
        public final String sender;
        public final String content;
        public final long timestamp;

        public MailMessage(String sender, String content, long timestamp) {
            this.sender = Objects.requireNonNull(sender, "sender");
            this.content = Objects.requireNonNull(content, "content");
            this.timestamp = timestamp;
        }
    }

    private static final MailboxManager INSTANCE = new MailboxManager();

    /** Per-player inbox, keyed by UUID. */
    private final Map<UUID, List<MailMessage>> inbox = new HashMap<>();

    private MailboxManager() {}

    public static MailboxManager getInstance() {
        return INSTANCE;
    }

    /**
     * Delivers a message to the given player's inbox.
     *
     * @param recipient the recipient's UUID
     * @param message   the message to deliver
     */
    public void sendMail(UUID recipient, MailMessage message) {
        Objects.requireNonNull(recipient, "recipient");
        Objects.requireNonNull(message, "message");
        inbox.computeIfAbsent(recipient, id -> new ArrayList<>()).add(message);
    }

    /**
     * Returns an unmodifiable view of the player's inbox.
     *
     * @param playerId the player's UUID
     * @return list of messages, empty if none
     */
    public List<MailMessage> getInbox(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        List<MailMessage> messages = inbox.get(playerId);
        return messages == null ? Collections.emptyList() : Collections.unmodifiableList(messages);
    }

    /**
     * Clears all messages from the player's inbox.
     *
     * @param playerId the player's UUID
     * @return {@code true} if the inbox had any messages
     */
    public boolean clearInbox(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        List<MailMessage> messages = inbox.remove(playerId);
        return messages != null && !messages.isEmpty();
    }

    public void load(File dataFolder) {
        File file = new File(dataFolder, "mailbox.yml");
        if (!file.exists()) {
            return;
        }
        YamlConfiguration cfg = YamlConfiguration.loadConfiguration(file);
        inbox.clear();
        for (String key : cfg.getKeys(false)) {
            try {
                UUID uuid = UUID.fromString(key);
                List<Map<?, ?>> entries = cfg.getMapList(key);
                if (!entries.isEmpty()) {
                    List<MailMessage> messages = new ArrayList<>();
                    for (Map<?, ?> entry : entries) {
                        String sender = (String) entry.get("sender");
                        String content = (String) entry.get("content");
                        long timestamp = entry.get("timestamp") instanceof Number n ? n.longValue() : 0L;
                        if (sender != null && content != null) {
                            messages.add(new MailMessage(sender, content, timestamp));
                        }
                    }
                    if (!messages.isEmpty()) {
                        inbox.put(uuid, messages);
                    }
                }
            } catch (IllegalArgumentException ignored) {
                // skip malformed entries
            }
        }
    }

    public void save(File dataFolder) {
        File file = new File(dataFolder, "mailbox.yml");
        YamlConfiguration cfg = new YamlConfiguration();
        for (Map.Entry<UUID, List<MailMessage>> entry : inbox.entrySet()) {
            if (entry.getValue().isEmpty()) {
                continue;
            }
            List<Map<String, Object>> entries = new ArrayList<>();
            for (MailMessage msg : entry.getValue()) {
                Map<String, Object> map = new HashMap<>();
                map.put("sender", msg.sender);
                map.put("content", msg.content);
                map.put("timestamp", msg.timestamp);
                entries.add(map);
            }
            cfg.set(entry.getKey().toString(), entries);
        }
        try {
            cfg.save(file);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save mailbox.yml", e);
        }
    }
}
