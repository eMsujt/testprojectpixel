package com.skyblock.core.chat;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Singleton tracking the active {@link ChatChannel} for each online player.
 * Players default to {@link ChatChannel#GLOBAL} when no channel has been set.
 */
public final class ChatManager {

    private static final ChatManager INSTANCE = new ChatManager();

    /** Per-player active channel. Absent entries imply GLOBAL. */
    private final Map<UUID, ChatChannel> activeChannels = new HashMap<>();

    private ChatManager() {
    }

    public static ChatManager getInstance() {
        return INSTANCE;
    }

    /**
     * Sets the active chat channel for the given player.
     *
     * @param playerId the player's UUID
     * @param channel  the channel to switch to
     */
    public void setChannel(UUID playerId, ChatChannel channel) {
        if (channel == null) throw new IllegalArgumentException("channel must not be null");
        activeChannels.put(playerId, channel);
    }

    /**
     * Returns the active channel for the player, defaulting to {@link ChatChannel#GLOBAL}.
     *
     * @param playerId the player's UUID
     * @return the player's active channel
     */
    public ChatChannel getChannel(UUID playerId) {
        return activeChannels.getOrDefault(playerId, ChatChannel.GLOBAL);
    }

    /**
     * Removes the stored channel for the player (e.g. on disconnect).
     *
     * @param playerId the player's UUID
     */
    public void removePlayer(UUID playerId) {
        activeChannels.remove(playerId);
    }
}
