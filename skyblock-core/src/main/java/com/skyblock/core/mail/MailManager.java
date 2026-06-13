package com.skyblock.core.mail;

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
 * Singleton managing per-player mail inboxes with YAML persistence.
 *
 * <p>Not thread-safe; synchronize externally if accessed from multiple threads.</p>
 */
public final class MailManager {

    private static final MailManager INSTANCE = new MailManager();

    /** Per-player inbox, keyed by UUID. */
    private final Map<UUID, List<String>> inbox = new HashMap<>();

    private MailManager() {}

    public static MailManager getInstance() {
        return INSTANCE;
    }

    /**
     * Sends a mail message to the given player's inbox.
     *
     * @param recipient the recipient's UUID
     * @param message   the message to deliver
     */
    public void sendMail(UUID recipient, String message) {
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
    public List<String> getInbox(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        List<String> messages = inbox.get(playerId);
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
        List<String> messages = inbox.remove(playerId);
        return messages != null && !messages.isEmpty();
    }

    public void load(File dataFolder) {
        File file = new File(dataFolder, "mail.yml");
        if (!file.exists()) {
            return;
        }
        YamlConfiguration cfg = YamlConfiguration.loadConfiguration(file);
        inbox.clear();
        for (String key : cfg.getKeys(false)) {
            try {
                UUID uuid = UUID.fromString(key);
                List<String> messages = cfg.getStringList(key);
                if (!messages.isEmpty()) {
                    inbox.put(uuid, new ArrayList<>(messages));
                }
            } catch (IllegalArgumentException ignored) {
                // skip malformed entries
            }
        }
    }

    public void save(File dataFolder) {
        File file = new File(dataFolder, "mail.yml");
        YamlConfiguration cfg = new YamlConfiguration();
        for (Map.Entry<UUID, List<String>> entry : inbox.entrySet()) {
            if (!entry.getValue().isEmpty()) {
                cfg.set(entry.getKey().toString(), entry.getValue());
            }
        }
        try {
            cfg.save(file);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save mail.yml", e);
        }
    }
}
