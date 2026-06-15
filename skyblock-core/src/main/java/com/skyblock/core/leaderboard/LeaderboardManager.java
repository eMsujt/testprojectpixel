package com.skyblock.core.leaderboard;

import com.skyblock.core.level.SkyblockLevelManager;
import com.skyblock.core.stat.Stat;
import com.skyblock.core.stat.StatManager;
import com.skyblock.core.vault.VaultManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * Singleton managing per-type leaderboard scores for players.
 *
 * <p>Not thread-safe; synchronize externally if accessed from multiple threads.</p>
 */
public final class LeaderboardManager {

    /** Categories tracked by the leaderboard. */
    public enum LeaderboardType {
        SKILL_LEVEL("Skill Level"),
        SLAYER_XP("Slayer XP"),
        DUNGEON_COMPLETIONS("Dungeon Completions"),
        COIN_BALANCE("Coin Balance"),
        FISHING_XP("Fishing XP"),
        SKYBLOCK_LEVEL("SkyBlock Level"),
        VAULT_BALANCE("Vault Balance"),
        STAT_STRENGTH("Strength");

        private final String displayName;

        LeaderboardType(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    /** One ranked entry returned by {@link #getTopEntries}. */
    public static final class LeaderboardEntry {
        private final UUID playerId;
        private final String playerName;
        private final double score;

        public LeaderboardEntry(UUID playerId, String playerName, double score) {
            this.playerId = Objects.requireNonNull(playerId, "playerId");
            this.playerName = Objects.requireNonNull(playerName, "playerName");
            this.score = score;
        }

        public UUID getPlayerId() { return playerId; }
        public String getPlayerName() { return playerName; }
        public double getScore() { return score; }
    }

    private static final LeaderboardManager INSTANCE = new LeaderboardManager();

    /** type → (playerId → score) */
    private final Map<LeaderboardType, Map<UUID, Double>> scores = new EnumMap<>(LeaderboardType.class);
    /** playerId → display name */
    private final Map<UUID, String> playerNames = new HashMap<>();

    private LeaderboardManager() {}

    /**
     * Returns the single shared {@code LeaderboardManager} instance.
     *
     * @return the singleton instance
     */
    public static LeaderboardManager getInstance() {
        return INSTANCE;
    }

    /**
     * Sets the score for the given player in the given type.
     *
     * @param type       the leaderboard type
     * @param playerId   the player's UUID
     * @param playerName the player's display name (used in ranking output)
     * @param score      the score to record; replaces any previous value
     */
    public void setScore(LeaderboardType type, UUID playerId, String playerName, double score) {
        Objects.requireNonNull(type, "type");
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(playerName, "playerName");
        scores.computeIfAbsent(type, t -> new HashMap<>()).put(playerId, score);
        playerNames.put(playerId, playerName);
    }

    /**
     * Adds {@code delta} to the player's existing score in the given type.
     * If no score exists yet it is treated as {@code 0}.
     *
     * @param type       the leaderboard type
     * @param playerId   the player's UUID
     * @param playerName the player's display name
     * @param delta      the amount to add (may be negative)
     */
    public void addScore(LeaderboardType type, UUID playerId, String playerName, double delta) {
        Objects.requireNonNull(type, "type");
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(playerName, "playerName");
        Map<UUID, Double> typeScores = scores.computeIfAbsent(type, t -> new HashMap<>());
        typeScores.merge(playerId, delta, Double::sum);
        playerNames.put(playerId, playerName);
    }

    /**
     * Returns the player's score in the given type, or {@code 0} if unset.
     *
     * @param type     the leaderboard type
     * @param playerId the player's UUID
     * @return the recorded score
     */
    public double getScore(LeaderboardType type, UUID playerId) {
        Objects.requireNonNull(type, "type");
        Objects.requireNonNull(playerId, "playerId");
        Map<UUID, Double> typeScores = scores.get(type);
        return typeScores == null ? 0.0 : typeScores.getOrDefault(playerId, 0.0);
    }

    /**
     * Returns the top-{@code limit} entries for the given type, sorted
     * descending by score.
     *
     * @param type  the leaderboard type
     * @param limit the maximum number of entries to return; clamped to [1, 100]
     * @return an unmodifiable list of at most {@code limit} entries
     */
    public List<LeaderboardEntry> getTopEntries(LeaderboardType type, int limit) {
        Objects.requireNonNull(type, "type");
        int cap = Math.max(1, Math.min(limit, 100));
        Map<UUID, Double> typeScores = scores.get(type);
        if (typeScores == null || typeScores.isEmpty()) {
            return Collections.emptyList();
        }
        List<LeaderboardEntry> entries = new ArrayList<>(typeScores.size());
        for (Map.Entry<UUID, Double> e : typeScores.entrySet()) {
            String name = playerNames.getOrDefault(e.getKey(), e.getKey().toString());
            entries.add(new LeaderboardEntry(e.getKey(), name, e.getValue()));
        }
        entries.sort(Comparator.comparingDouble(LeaderboardEntry::getScore).reversed());
        return Collections.unmodifiableList(entries.subList(0, Math.min(cap, entries.size())));
    }

    /**
     * Refreshes {@link LeaderboardType#SKYBLOCK_LEVEL}, {@link LeaderboardType#VAULT_BALANCE},
     * and {@link LeaderboardType#STAT_STRENGTH} scores by reading the current state of the
     * three source managers.
     *
     * @param knownNames a map from UUID to display name used to label entries;
     *                   UUIDs not present in the map fall back to the UUID string
     */
    public void syncFromManagers(Map<UUID, String> knownNames) {
        SkyblockLevelManager levelMgr = SkyblockLevelManager.getInstance();
        VaultManager vaultMgr = VaultManager.getInstance();
        StatManager statMgr = StatManager.getInstance();

        Map<UUID, Double> levelScores = scores.computeIfAbsent(LeaderboardType.SKYBLOCK_LEVEL, t -> new HashMap<>());
        levelScores.clear();
        for (UUID id : levelMgr.getTrackedPlayers()) {
            String name = knownNames.getOrDefault(id, id.toString());
            levelScores.put(id, (double) levelMgr.getLevel(id));
            playerNames.put(id, name);
        }

        Map<UUID, Double> vaultScores = scores.computeIfAbsent(LeaderboardType.VAULT_BALANCE, t -> new HashMap<>());
        vaultScores.clear();
        for (UUID id : vaultMgr.getTrackedPlayers()) {
            String name = knownNames.getOrDefault(id, id.toString());
            vaultScores.put(id, (double) vaultMgr.getBalance(id));
            playerNames.put(id, name);
        }

        Map<UUID, Double> strengthScores = scores.computeIfAbsent(LeaderboardType.STAT_STRENGTH, t -> new HashMap<>());
        strengthScores.clear();
        for (UUID id : statMgr.getTrackedPlayers()) {
            String name = knownNames.getOrDefault(id, id.toString());
            strengthScores.put(id, statMgr.getStat(id, Stat.STRENGTH));
            playerNames.put(id, name);
        }
    }

    /** Removes all stored scores. */
    public void clear() {
        scores.clear();
        playerNames.clear();
    }
}
