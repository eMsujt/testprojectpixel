package com.skyblock.core.manager;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

/**
 * Tracks one-shot "type your answer in chat" prompts. A menu calls
 * {@link #request(UUID, Consumer)} (typically after closing its inventory), the
 * {@link com.skyblock.core.listener.ChatInputListener} intercepts the player's
 * next chat message, and {@link #consume(UUID, String)} runs the callback on the
 * main thread. Used e.g. by the Bank's custom deposit/withdraw amount.
 */
public final class ChatInputManager {

    private static final ChatInputManager INSTANCE = new ChatInputManager();

    public static ChatInputManager getInstance() {
        return INSTANCE;
    }

    private ChatInputManager() {}

    private final Map<UUID, Consumer<String>> pending = new ConcurrentHashMap<>();

    /** Registers a callback to run with the player's next chat message. */
    public void request(UUID playerId, Consumer<String> callback) {
        pending.put(playerId, callback);
    }

    public boolean hasPending(UUID playerId) {
        return pending.containsKey(playerId);
    }

    /** Drops a pending prompt without running it (e.g. on quit). */
    public void cancel(UUID playerId) {
        pending.remove(playerId);
    }

    /**
     * Runs and clears the pending callback for this player. Must be called on the
     * main thread (the callback opens menus / touches econ). Returns whether one ran.
     */
    public boolean consume(UUID playerId, String input) {
        Consumer<String> callback = pending.remove(playerId);
        if (callback == null) {
            return false;
        }
        callback.accept(input);
        return true;
    }
}
