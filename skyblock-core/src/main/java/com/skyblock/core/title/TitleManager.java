package com.skyblock.core.title;

import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

/**
 * Singleton tracking per-player active titles and unlocked title sets with YAML persistence.
 *
 * <p>Not thread-safe; synchronize externally if accessed from multiple threads.</p>
 */
public final class TitleManager {

    /** Every title a player can earn in SkyBlock. */
    public enum TitleType {
        NOVICE("Novice", "The beginning of your journey."),
        ADVENTURER("Adventurer", "You've ventured beyond the starting island."),
        VETERAN("Veteran", "A seasoned SkyBlock player."),
        CHAMPION("Champion", "You've proven yourself in combat."),
        LEGEND("Legend", "Your name is known across the island."),
        MASTER("Master", "You have mastered the arts of SkyBlock.");

        private final String displayName;
        private final String description;

        TitleType(String displayName, String description) {
            this.displayName = displayName;
            this.description = description;
        }

        public String getDisplayName() {
            return displayName;
        }

        public String getDescription() {
            return description;
        }
    }

    private static final TitleManager INSTANCE = new TitleManager();

    /** Per-player active title. Absent means no title is equipped. */
    private final Map<UUID, TitleType> activeTitles = new HashMap<>();

    /** Per-player set of unlocked titles. */
    private final Map<UUID, Set<TitleType>> unlockedTitles = new HashMap<>();

    private TitleManager() {}

    public static TitleManager getInstance() {
        return INSTANCE;
    }

    /**
     * Returns the active title for the given player, or {@code null} if none is set.
     *
     * @param playerId the player's UUID
     * @return the active {@link TitleType}, or {@code null}
     */
    public TitleType getActiveTitle(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        return activeTitles.get(playerId);
    }

    /**
     * Sets the player's active title. The title must already be unlocked.
     *
     * @param playerId the player's UUID
     * @param title    the title to equip
     * @return {@code true} if the title was set; {@code false} if the title is not unlocked
     */
    public boolean setActiveTitle(UUID playerId, TitleType title) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(title, "title");
        if (!isUnlocked(playerId, title)) {
            return false;
        }
        activeTitles.put(playerId, title);
        return true;
    }

    /**
     * Clears the player's active title.
     *
     * @param playerId the player's UUID
     * @return {@code true} if the player had an active title
     */
    public boolean clearActiveTitle(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        return activeTitles.remove(playerId) != null;
    }

    /**
     * Unlocks a title for the given player.
     *
     * @param playerId the player's UUID
     * @param title    the title to unlock
     * @return {@code true} if the title was newly unlocked; {@code false} if already unlocked
     */
    public boolean unlockTitle(UUID playerId, TitleType title) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(title, "title");
        return unlockedTitles.computeIfAbsent(playerId, id -> new HashSet<>()).add(title);
    }

    /**
     * Returns whether the player has unlocked the given title.
     *
     * @param playerId the player's UUID
     * @param title    the title to check
     * @return {@code true} if unlocked
     */
    public boolean isUnlocked(UUID playerId, TitleType title) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(title, "title");
        Set<TitleType> unlocked = unlockedTitles.get(playerId);
        return unlocked != null && unlocked.contains(title);
    }

    /**
     * Returns an unmodifiable view of the player's unlocked titles.
     *
     * @param playerId the player's UUID
     * @return set of unlocked titles, empty if none
     */
    public Set<TitleType> getUnlockedTitles(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        Set<TitleType> unlocked = unlockedTitles.get(playerId);
        return unlocked == null ? Collections.emptySet() : Collections.unmodifiableSet(unlocked);
    }

    public void load(File dataFolder) {
        File file = new File(dataFolder, "title.yml");
        if (!file.exists()) {
            return;
        }
        YamlConfiguration cfg = YamlConfiguration.loadConfiguration(file);
        activeTitles.clear();
        unlockedTitles.clear();
        for (String key : cfg.getKeys(false)) {
            try {
                UUID uuid = UUID.fromString(key);
                String activeRaw = cfg.getString(key + ".active");
                if (activeRaw != null && !activeRaw.isEmpty()) {
                    try {
                        activeTitles.put(uuid, TitleType.valueOf(activeRaw));
                    } catch (IllegalArgumentException ignored) {}
                }
                for (String titleName : cfg.getStringList(key + ".unlocked")) {
                    try {
                        unlockedTitles.computeIfAbsent(uuid, id -> new HashSet<>())
                                .add(TitleType.valueOf(titleName));
                    } catch (IllegalArgumentException ignored) {}
                }
            } catch (IllegalArgumentException ignored) {}
        }
    }

    public void save(File dataFolder) {
        File file = new File(dataFolder, "title.yml");
        YamlConfiguration cfg = new YamlConfiguration();
        Set<UUID> allPlayers = new HashSet<>();
        allPlayers.addAll(activeTitles.keySet());
        allPlayers.addAll(unlockedTitles.keySet());
        for (UUID uuid : allPlayers) {
            String path = uuid.toString();
            TitleType active = activeTitles.get(uuid);
            cfg.set(path + ".active", active != null ? active.name() : "");
            Set<TitleType> unlocked = unlockedTitles.get(uuid);
            if (unlocked != null && !unlocked.isEmpty()) {
                cfg.set(path + ".unlocked", unlocked.stream().map(TitleType::name).toList());
            } else {
                cfg.set(path + ".unlocked", Collections.emptyList());
            }
        }
        try {
            cfg.save(file);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save title.yml", e);
        }
    }
}
