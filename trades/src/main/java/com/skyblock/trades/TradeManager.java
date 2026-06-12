package com.skyblock.trades;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Manages player-to-player trades: each {@link TradeSession} pairs two
 * players, tracks the items and coins each side offers, and completes only
 * once both sides have confirmed.
 *
 * <p>Sessions are stored in a {@link ConcurrentHashMap} keyed by session id,
 * with per-session state guarded by the session's own monitor, so all
 * operations are thread-safe. Coins are tracked as doubles.</p>
 */
public final class TradeManager {

    /** The lifecycle state of a trade session. */
    public enum TradeState {
        /** Both sides may still change their offers. */
        OPEN,
        /** Both sides confirmed and the trade completed. */
        COMPLETED,
        /** One side cancelled before completion. */
        CANCELLED
    }

    /** A trade between two players. */
    public static final class TradeSession {

        private final UUID sessionId;
        private final UUID initiator;
        private final UUID partner;
        private final Map<String, Integer> initiatorItems = new LinkedHashMap<>();
        private final Map<String, Integer> partnerItems = new LinkedHashMap<>();
        private double initiatorCoins;
        private double partnerCoins;
        private boolean initiatorConfirmed;
        private boolean partnerConfirmed;
        private TradeState state = TradeState.OPEN;

        private TradeSession(UUID sessionId, UUID initiator, UUID partner) {
            this.sessionId = sessionId;
            this.initiator = initiator;
            this.partner = partner;
        }

        /** Returns the id of this session. */
        public UUID getSessionId() {
            return sessionId;
        }

        /** Returns the id of the player who started the trade. */
        public UUID getInitiator() {
            return initiator;
        }

        /** Returns the id of the player the trade was started with. */
        public UUID getPartner() {
            return partner;
        }

        /** Returns this session's current state. */
        public synchronized TradeState getState() {
            return state;
        }

        /** Returns the coins the given participant has offered. */
        public synchronized double getOfferedCoins(UUID player) {
            return isInitiator(player) ? initiatorCoins : partnerCoins;
        }

        /** Returns an unmodifiable copy of the items the given participant has offered, keyed by item id. */
        public synchronized Map<String, Integer> getOfferedItems(UUID player) {
            return Collections.unmodifiableMap(new LinkedHashMap<>(isInitiator(player) ? initiatorItems : partnerItems));
        }

        /** Returns whether the given participant has confirmed the trade. */
        public synchronized boolean hasConfirmed(UUID player) {
            return isInitiator(player) ? initiatorConfirmed : partnerConfirmed;
        }

        private synchronized void offerItem(UUID player, String itemId, int amount) {
            requireOpen();
            Map<String, Integer> items = isInitiator(player) ? initiatorItems : partnerItems;
            items.merge(itemId, amount, Integer::sum);
            resetConfirmations();
        }

        private synchronized void offerCoins(UUID player, double coins) {
            requireOpen();
            if (isInitiator(player)) {
                initiatorCoins = coins;
            } else {
                partnerCoins = coins;
            }
            resetConfirmations();
        }

        private synchronized boolean confirm(UUID player) {
            requireOpen();
            if (isInitiator(player)) {
                initiatorConfirmed = true;
            } else {
                partnerConfirmed = true;
            }
            if (initiatorConfirmed && partnerConfirmed) {
                state = TradeState.COMPLETED;
                return true;
            }
            return false;
        }

        private synchronized void cancel() {
            requireOpen();
            state = TradeState.CANCELLED;
        }

        private boolean isInitiator(UUID player) {
            if (initiator.equals(player)) {
                return true;
            }
            if (partner.equals(player)) {
                return false;
            }
            throw new IllegalArgumentException("player is not part of this trade");
        }

        private void requireOpen() {
            if (state != TradeState.OPEN) {
                throw new IllegalStateException("trade is " + state);
            }
        }

        private void resetConfirmations() {
            initiatorConfirmed = false;
            partnerConfirmed = false;
        }
    }

    private final ConcurrentHashMap<UUID, TradeSession> sessions = new ConcurrentHashMap<>();

    /**
     * Opens a new trade session between two players.
     *
     * @param initiator the player starting the trade
     * @param partner   the player being traded with, distinct from the initiator
     * @return the created session
     */
    public TradeSession openTrade(UUID initiator, UUID partner) {
        requirePlayer(initiator, "initiator");
        requirePlayer(partner, "partner");
        if (initiator.equals(partner)) {
            throw new IllegalArgumentException("a player cannot trade with themselves");
        }
        UUID sessionId = UUID.randomUUID();
        TradeSession session = new TradeSession(sessionId, initiator, partner);
        sessions.put(sessionId, session);
        return session;
    }

    /**
     * Returns the session with the given id, if any.
     *
     * @param sessionId the session's id
     * @return the session, or empty if no such session exists
     */
    public Optional<TradeSession> getSession(UUID sessionId) {
        if (sessionId == null) {
            throw new IllegalArgumentException("sessionId must be non-null");
        }
        return Optional.ofNullable(sessions.get(sessionId));
    }

    /**
     * Adds items to a participant's offer, resetting both confirmations.
     *
     * @param sessionId the session to update
     * @param player    the participant whose offer to add to
     * @param itemId    the item's id, non-blank
     * @param amount    the number of items to add, must be positive
     * @throws IllegalStateException if the trade is no longer open
     */
    public void offerItem(UUID sessionId, UUID player, String itemId, int amount) {
        if (itemId == null || itemId.isBlank()) {
            throw new IllegalArgumentException("itemId must be non-blank");
        }
        if (amount <= 0) {
            throw new IllegalArgumentException("amount must be positive");
        }
        requireSession(sessionId).offerItem(requirePlayer(player, "player"), itemId.trim(), amount);
    }

    /**
     * Sets the coins a participant offers, resetting both confirmations.
     *
     * @param sessionId the session to update
     * @param player    the participant whose coin offer to set
     * @param coins     the coin amount, must be non-negative
     * @throws IllegalStateException if the trade is no longer open
     */
    public void offerCoins(UUID sessionId, UUID player, double coins) {
        if (coins < 0 || !Double.isFinite(coins)) {
            throw new IllegalArgumentException("coins must be non-negative");
        }
        requireSession(sessionId).offerCoins(requirePlayer(player, "player"), coins);
    }

    /**
     * Records a participant's confirmation; the trade completes once both
     * participants have confirmed.
     *
     * @param sessionId the session to confirm
     * @param player    the confirming participant
     * @return {@code true} if this confirmation completed the trade
     * @throws IllegalStateException if the trade is no longer open
     */
    public boolean confirm(UUID sessionId, UUID player) {
        return requireSession(sessionId).confirm(requirePlayer(player, "player"));
    }

    /**
     * Cancels an open trade.
     *
     * @param sessionId the session to cancel
     * @throws IllegalStateException if the trade is no longer open
     */
    public void cancelTrade(UUID sessionId) {
        requireSession(sessionId).cancel();
    }

    private TradeSession requireSession(UUID sessionId) {
        return getSession(sessionId)
                .orElseThrow(() -> new IllegalArgumentException("no trade session with id " + sessionId));
    }

    private static UUID requirePlayer(UUID player, String name) {
        if (player == null) {
            throw new IllegalArgumentException(name + " must be non-null");
        }
        return player;
    }
}
