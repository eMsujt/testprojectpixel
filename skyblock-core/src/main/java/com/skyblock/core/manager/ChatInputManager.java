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

    /**
     * Parses a coin amount typed in chat — "10000", "1,000", "10k", "2.5m", "1b" —
     * into a whole number of coins, or {@code -1} if it isn't a valid positive amount.
     */
    public static long parseAmount(String raw) {
        String s = raw.trim().toLowerCase().replace(",", "");
        if (s.isEmpty()) {
            return -1;
        }
        double mult = 1;
        char last = s.charAt(s.length() - 1);
        if (last == 'k') { mult = 1_000D; s = s.substring(0, s.length() - 1); }
        else if (last == 'm') { mult = 1_000_000D; s = s.substring(0, s.length() - 1); }
        else if (last == 'b') { mult = 1_000_000_000D; s = s.substring(0, s.length() - 1); }
        try {
            double value = Double.parseDouble(s) * mult;
            return value <= 0 ? -1 : (long) value;
        } catch (NumberFormatException e) {
            return -1;
        }
    }
}
