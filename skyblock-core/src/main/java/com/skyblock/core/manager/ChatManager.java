package com.skyblock.core.manager;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * Singleton managing player chat ranks and message formatting.
 *
 * <p>Players default to {@link RankType#DEFAULT} when no rank has been assigned.</p>
 */
public final class ChatManager {

    public enum RankType {
        DEFAULT("§7", ""),
        VIP("§a", "[VIP] "),
        VIP_PLUS("§a", "[VIP+] "),
        MVP("§b", "[MVP] "),
        MVP_PLUS("§b", "[MVP+] "),
        YOUTUBER("§c", "[YOUTUBE] "),
        HELPER("§5", "[HELPER] "),
        MOD("§2", "[MOD] "),
        ADMIN("§c", "[ADMIN] ");

        private final String colorCode;
        private final String prefix;

        RankType(String colorCode, String prefix) {
            this.colorCode = colorCode;
            this.prefix = prefix;
        }

        public String getColorCode() {
            return colorCode;
        }

        public String getPrefix() {
            return prefix;
        }
    }

    private static final ChatManager INSTANCE = new ChatManager();

    /** Per-player rank. Absent entries imply DEFAULT. */
    private final Map<UUID, RankType> ranks = new HashMap<>();

    private ChatManager() {}

    public static ChatManager getInstance() {
        return INSTANCE;
    }

    /**
     * Sets the rank for the given player.
     *
     * @param playerId the player's UUID
     * @param rank     the rank to assign
     */
    public void setRank(UUID playerId, RankType rank) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(rank, "rank");
        ranks.put(playerId, rank);
    }

    /**
     * Returns the rank for the given player, defaulting to {@link RankType#DEFAULT}.
     *
     * @param playerId the player's UUID
     * @return the player's rank
     */
    public RankType getRank(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        return ranks.getOrDefault(playerId, RankType.DEFAULT);
    }

    /**
     * Formats a chat message with the player's rank prefix and color.
     *
     * @param playerId   the player's UUID
     * @param playerName the player's display name
     * @param message    the raw message text
     * @return the formatted chat string
     */
    public String formatMessage(UUID playerId, String playerName, String message) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(playerName, "playerName");
        Objects.requireNonNull(message, "message");
        RankType rank = getRank(playerId);
        return rank.getColorCode() + rank.getPrefix() + playerName + "§7: §f" + message;
    }

    /**
     * Removes the stored rank for the player (e.g. on disconnect).
     *
     * @param playerId the player's UUID
     */
    public void removePlayer(UUID playerId) {
        ranks.remove(playerId);
    }

    /** Removes all stored ranks. */
    public void clear() {
        ranks.clear();
    }
}
