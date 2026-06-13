package com.skyblock.core.event;

import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * Singleton manager for SkyBlock boost events (Double XP, Mining Fiesta, etc.).
 *
 * <p>Tracks per-player active-event participation and score.
 * Not thread-safe; synchronize externally if needed.</p>
 */
public final class SkyblockEventManager {

    /** Boost events that players can participate in. */
    public enum SkyblockEvent {
        DOUBLE_XP("Double XP", 2.0),
        MINING_FIESTA("Mining Fiesta", 1.5),
        FISHING_FESTIVAL("Fishing Festival", 2.0),
        SPOOKY_FESTIVAL("Spooky Festival", 1.75),
        TRAVELLING_ZOO("Travelling Zoo", 1.25);

        private final String displayName;
        private final double multiplier;

        SkyblockEvent(String displayName, double multiplier) {
            this.displayName = displayName;
            this.multiplier = multiplier;
        }

        public String getDisplayName() { return displayName; }
        public double getMultiplier()  { return multiplier; }
    }

    private static final SkyblockEventManager INSTANCE = new SkyblockEventManager();

    /** Per-player active event, null if not participating. */
    private final Map<UUID, SkyblockEvent> activeEvents = new HashMap<>();
    /** Per-player score per event. */
    private final Map<UUID, Map<SkyblockEvent, Long>> scores = new HashMap<>();

    private SkyblockEventManager() {}

    /**
     * Returns the single shared {@code SkyblockEventManager} instance.
     *
     * @return the singleton instance
     */
    public static SkyblockEventManager getInstance() {
        return INSTANCE;
    }

    /**
     * Joins the given event for the player. If the player is already in a
     * different event they are moved to the new one.
     *
     * @param playerId the player's UUID
     * @param event    the event to join
     */
    public void joinEvent(UUID playerId, SkyblockEvent event) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(event, "event");
        activeEvents.put(playerId, event);
        scores.computeIfAbsent(playerId, id -> new HashMap<>()).putIfAbsent(event, 0L);
    }

    /**
     * Removes the player from their current event.
     *
     * @param playerId the player's UUID
     */
    public void leaveEvent(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        activeEvents.remove(playerId);
    }

    /**
     * Returns the event the player is currently participating in, or {@code null}.
     *
     * @param playerId the player's UUID
     * @return active event, or {@code null}
     */
    public SkyblockEvent getActiveEvent(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        return activeEvents.get(playerId);
    }

    /**
     * Adds score to the player's tally for the given event.
     *
     * @param playerId the player's UUID
     * @param event    the event to add score to
     * @param amount   non-negative amount to add
     * @return the player's total score for the event after the addition
     * @throws IllegalArgumentException if {@code amount} is negative
     */
    public long addScore(UUID playerId, SkyblockEvent event, long amount) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(event, "event");
        if (amount < 0) {
            throw new IllegalArgumentException("amount must not be negative: " + amount);
        }
        Map<SkyblockEvent, Long> scoreMap = scores.computeIfAbsent(playerId, id -> new HashMap<>());
        long total = scoreMap.getOrDefault(event, 0L) + amount;
        scoreMap.put(event, total);
        return total;
    }

    /**
     * Returns the player's score for the given event.
     *
     * @param playerId the player's UUID
     * @param event    the event to look up
     * @return score, or {@code 0} if none recorded
     */
    public long getScore(UUID playerId, SkyblockEvent event) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(event, "event");
        Map<SkyblockEvent, Long> scoreMap = scores.get(playerId);
        return scoreMap == null ? 0L : scoreMap.getOrDefault(event, 0L);
    }

    /**
     * Resets all event data for the given player.
     *
     * @param playerId the player's UUID
     */
    public void reset(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        activeEvents.remove(playerId);
        scores.remove(playerId);
    }

    // ---------------------------------------------------------------------------
    // Persistence
    // ---------------------------------------------------------------------------

    public void load(File dataFolder) {
        File file = new File(dataFolder, "skyblock_events.yml");
        if (!file.exists()) {
            return;
        }
        YamlConfiguration cfg = YamlConfiguration.loadConfiguration(file);
        activeEvents.clear();
        scores.clear();
        for (String key : cfg.getKeys(false)) {
            try {
                UUID id = UUID.fromString(key);
                String activeName = cfg.getString(key + ".active");
                if (activeName != null) {
                    try {
                        activeEvents.put(id, SkyblockEvent.valueOf(activeName));
                    } catch (IllegalArgumentException ignored) {
                        // skip unknown event names
                    }
                }
                if (cfg.isConfigurationSection(key + ".scores")) {
                    Map<SkyblockEvent, Long> scoreMap = new HashMap<>();
                    for (String eventName : cfg.getConfigurationSection(key + ".scores").getKeys(false)) {
                        try {
                            SkyblockEvent event = SkyblockEvent.valueOf(eventName);
                            scoreMap.put(event, cfg.getLong(key + ".scores." + eventName, 0L));
                        } catch (IllegalArgumentException ignored) {
                            // skip unknown event names
                        }
                    }
                    if (!scoreMap.isEmpty()) {
                        scores.put(id, scoreMap);
                    }
                }
            } catch (IllegalArgumentException ignored) {
                // skip malformed UUIDs
            }
        }
    }

    public void save(File dataFolder) {
        File file = new File(dataFolder, "skyblock_events.yml");
        YamlConfiguration cfg = new YamlConfiguration();
        for (Map.Entry<UUID, SkyblockEvent> entry : activeEvents.entrySet()) {
            cfg.set(entry.getKey().toString() + ".active", entry.getValue().name());
        }
        for (Map.Entry<UUID, Map<SkyblockEvent, Long>> entry : scores.entrySet()) {
            String key = entry.getKey().toString();
            for (Map.Entry<SkyblockEvent, Long> score : entry.getValue().entrySet()) {
                cfg.set(key + ".scores." + score.getKey().name(), score.getValue());
            }
        }
        try {
            cfg.save(file);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save skyblock_events.yml", e);
        }
    }
}
