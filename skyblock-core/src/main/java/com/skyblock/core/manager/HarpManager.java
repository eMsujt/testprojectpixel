package com.skyblock.core.manager;

import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

/**
 * Singleton tracking each player's progress on Melody's Harp, the rhythm
 * minigame in The Catacombs entrance.
 *
 * <p>A player plays {@link Song songs} of increasing difficulty; the harp
 * records the best completion percentage achieved per song. Reaching
 * {@link #COMPLETION_THRESHOLD 100%} on a song completes it for the first time
 * and grants its permanent Intelligence reward. The total Intelligence bonus is
 * the sum of the rewards of every completed song.</p>
 *
 * <p>Not thread-safe; synchronize externally if accessed from multiple threads.</p>
 */
public final class HarpManager {

    /** Best completion percentage that counts a song as fully completed. */
    public static final int COMPLETION_THRESHOLD = 100;

    /**
     * The songs playable on Melody's Harp, ordered by increasing difficulty.
     * Each song unlocks the next and grants a permanent Intelligence reward the
     * first time it is completed.
     */
    public enum Song {
        // Easy tier (+1 Intelligence each)
        HYMN_TO_THE_JOY("Hymn to the Joy", 1, 1),
        FRERE_JACQUES("Frère Jacques", 2, 1),
        AMAZING_GRACE("Amazing Grace", 3, 1),
        // Hard tier (+2)
        BRAHMS_LULLABY("Brahm's Lullaby", 4, 2),
        HAPPY_BIRTHDAY("Happy Birthday to You", 5, 2),
        GREENSLEEVES("Greensleeves", 6, 2),
        // Expert tier (+3)
        GEOTHERMY("Geothermy?", 7, 3),
        MINUET("Minuet", 8, 3),
        JOY_TO_THE_WORLD("Joy to the World", 9, 3),
        // Virtuoso tier (+4)
        GODLY_IMAGINATION("Godly Imagination", 10, 4),
        LA_VIE_EN_ROSE("La Vie en Rose", 11, 4);

        private final String displayName;
        private final int difficulty;
        private final int intelligenceReward;

        Song(String displayName, int difficulty, int intelligenceReward) {
            this.displayName = displayName;
            this.difficulty = difficulty;
            this.intelligenceReward = intelligenceReward;
        }

        public String getDisplayName() {
            return displayName;
        }

        /** Relative difficulty rank, {@code 1} (easiest) upward. */
        public int getDifficulty() {
            return difficulty;
        }

        /** Permanent Intelligence granted the first time this song is completed. */
        public int getIntelligenceReward() {
            return intelligenceReward;
        }

        /** Returns the next, harder song, or {@code null} if this is the last. */
        public Song next() {
            int next = ordinal() + 1;
            Song[] values = values();
            return next < values.length ? values[next] : null;
        }
    }

    private static final HarpManager INSTANCE = new HarpManager();

    /** Per-player best completion percentage (0–100) achieved per song. */
    private final Map<UUID, Map<Song, Integer>> bestCompletion = new HashMap<>();

    /** Per-player set of fully completed songs (best completion at the threshold). */
    private final Map<UUID, Set<Song>> completedSongs = new HashMap<>();

    private HarpManager() {
    }

    /**
     * Returns the single shared {@code HarpManager} instance.
     *
     * @return the singleton instance
     */
    public static HarpManager getInstance() {
        return INSTANCE;
    }

    // -------------------------------------------------------------------------
    // Song unlocking
    // -------------------------------------------------------------------------

    /**
     * Returns whether the player has unlocked the given song. The first song is
     * always unlocked; every later song unlocks once its predecessor is
     * completed.
     *
     * @param playerId the player to look up
     * @param song     the song to check
     * @return {@code true} if the song can be played
     */
    public boolean isUnlocked(UUID playerId, Song song) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(song, "song");
        if (song.ordinal() == 0) {
            return true;
        }
        return isCompleted(playerId, Song.values()[song.ordinal() - 1]);
    }

    // -------------------------------------------------------------------------
    // Completion progress
    // -------------------------------------------------------------------------

    /**
     * Records a play of the given song. The stored best completion only ever
     * increases. Reaching {@link #COMPLETION_THRESHOLD} for the first time marks
     * the song completed and awards its Intelligence reward.
     *
     * @param playerId the player who played the song
     * @param song     the song played
     * @param percent  the completion percentage achieved this play (clamped to [0, 100])
     * @return {@code true} if this play completed the song for the first time
     */
    public boolean recordCompletion(UUID playerId, Song song, int percent) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(song, "song");
        int clamped = Math.max(0, Math.min(COMPLETION_THRESHOLD, percent));
        Map<Song, Integer> best = bestCompletion.computeIfAbsent(playerId, k -> new EnumMap<>(Song.class));
        int previous = best.getOrDefault(song, 0);
        if (clamped > previous) {
            best.put(song, clamped);
        }
        if (clamped >= COMPLETION_THRESHOLD) {
            Set<Song> completed = completedSongs.computeIfAbsent(playerId, k -> EnumSet.noneOf(Song.class));
            return completed.add(song);
        }
        return false;
    }

    /**
     * Returns the best completion percentage the player has achieved on the song.
     *
     * @param playerId the player to look up
     * @param song     the song to check
     * @return the best completion percentage, {@code 0} if never played
     */
    public int getBestCompletion(UUID playerId, Song song) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(song, "song");
        Map<Song, Integer> best = bestCompletion.get(playerId);
        return best == null ? 0 : best.getOrDefault(song, 0);
    }

    /**
     * Returns whether the player has fully completed the given song.
     *
     * @param playerId the player to look up
     * @param song     the song to check
     * @return {@code true} if the song is completed
     */
    public boolean isCompleted(UUID playerId, Song song) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(song, "song");
        Set<Song> completed = completedSongs.get(playerId);
        return completed != null && completed.contains(song);
    }

    /**
     * Returns an unmodifiable view of the songs the player has completed.
     *
     * @param playerId the player to look up
     * @return the completed songs (empty if none)
     */
    public Set<Song> getCompletedSongs(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        Set<Song> completed = completedSongs.get(playerId);
        if (completed == null || completed.isEmpty()) {
            return Collections.emptySet();
        }
        return Collections.unmodifiableSet(EnumSet.copyOf(completed));
    }

    /**
     * Returns the number of songs the player has completed.
     *
     * @param playerId the player to look up
     * @return the completed-song count
     */
    public int getCompletedCount(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        Set<Song> completed = completedSongs.get(playerId);
        return completed == null ? 0 : completed.size();
    }

    /**
     * Returns the total permanent Intelligence the player has earned from the
     * harp, the sum of the rewards of every completed song.
     *
     * @param playerId the player to look up
     * @return the Intelligence bonus
     */
    public int getIntelligenceBonus(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        Set<Song> completed = completedSongs.get(playerId);
        if (completed == null) {
            return 0;
        }
        int total = 0;
        for (Song song : completed) {
            total += song.getIntelligenceReward();
        }
        return total;
    }

    /**
     * Clears all harp progress for the given player.
     *
     * @param playerId the player to reset
     */
    public void reset(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        bestCompletion.remove(playerId);
        completedSongs.remove(playerId);
    }

    // -------------------------------------------------------------------------
    // Persistence
    // -------------------------------------------------------------------------

    public void load(File dataFolder) {
        File file = new File(dataFolder, "harp.yml");
        if (!file.exists()) {
            return;
        }
        YamlConfiguration cfg = YamlConfiguration.loadConfiguration(file);
        bestCompletion.clear();
        completedSongs.clear();
        for (String key : cfg.getKeys(false)) {
            try {
                UUID uuid = UUID.fromString(key);
                if (cfg.isConfigurationSection(key + ".best")) {
                    Map<Song, Integer> best = new EnumMap<>(Song.class);
                    for (String songName : cfg.getConfigurationSection(key + ".best").getKeys(false)) {
                        try {
                            Song song = Song.valueOf(songName);
                            best.put(song, cfg.getInt(key + ".best." + songName, 0));
                        } catch (IllegalArgumentException ignored) {}
                    }
                    if (!best.isEmpty()) {
                        bestCompletion.put(uuid, best);
                    }
                }
                if (cfg.isList(key + ".completed")) {
                    Set<Song> completed = EnumSet.noneOf(Song.class);
                    for (String songName : cfg.getStringList(key + ".completed")) {
                        try {
                            completed.add(Song.valueOf(songName));
                        } catch (IllegalArgumentException ignored) {}
                    }
                    if (!completed.isEmpty()) {
                        completedSongs.put(uuid, completed);
                    }
                }
            } catch (IllegalArgumentException ignored) {}
        }
    }

    public void save(File dataFolder) throws IOException {
        YamlConfiguration cfg = new YamlConfiguration();
        Set<UUID> players = new java.util.HashSet<>();
        players.addAll(bestCompletion.keySet());
        players.addAll(completedSongs.keySet());
        for (UUID uuid : players) {
            String key = uuid.toString();
            Map<Song, Integer> best = bestCompletion.get(uuid);
            if (best != null) {
                for (Map.Entry<Song, Integer> entry : best.entrySet()) {
                    cfg.set(key + ".best." + entry.getKey().name(), entry.getValue());
                }
            }
            Set<Song> completed = completedSongs.get(uuid);
            if (completed != null && !completed.isEmpty()) {
                java.util.List<String> names = new java.util.ArrayList<>();
                for (Song song : completed) {
                    names.add(song.name());
                }
                cfg.set(key + ".completed", names);
            }
        }
        cfg.save(new File(dataFolder, "harp.yml"));
    }
}
