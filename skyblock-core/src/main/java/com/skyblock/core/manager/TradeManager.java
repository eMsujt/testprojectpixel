package com.skyblock.core.manager;

import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * Singleton managing active player trade sessions.
 *
 * <p>Not thread-safe; synchronize externally if accessed from multiple threads.</p>
 */
public final class TradeManager {

    /** Immutable snapshot of a pending trade request. */
    public static final class TradeRequest {
        public final UUID initiator;
        public final UUID target;
        public final List<String> offeredItemNames;
        public final double offeredCoins;

        public TradeRequest(UUID initiator, UUID target, List<String> offeredItemNames, double offeredCoins) {
            this.initiator = Objects.requireNonNull(initiator, "initiator");
            this.target = Objects.requireNonNull(target, "target");
            this.offeredItemNames = Collections.unmodifiableList(
                    new ArrayList<>(Objects.requireNonNull(offeredItemNames, "offeredItemNames")));
            this.offeredCoins = offeredCoins;
        }
    }

    private static final TradeManager INSTANCE = new TradeManager();

    /** Pending trade requests: initiator UUID → TradeRequest. */
    private final Map<UUID, TradeRequest> pendingRequests = new HashMap<>();

    /** Active trade sessions keyed by one of the two participant UUIDs. */
    private final Map<UUID, TradeSession> sessions = new HashMap<>();

    private TradeManager() {}

    public static TradeManager getInstance() {
        return INSTANCE;
    }

    // -------------------------------------------------------------------------
    // Request lifecycle
    // -------------------------------------------------------------------------

    /**
     * Records a trade request. Overwrites any previous pending request from the same initiator.
     */
    public void sendRequest(TradeRequest request) {
        pendingRequests.put(request.initiator, request);
    }

    /** Returns {@code true} if there is a pending request from {@code from} to {@code to}. */
    public boolean hasPendingRequest(UUID from, UUID to) {
        TradeRequest req = pendingRequests.get(from);
        return req != null && to.equals(req.target);
    }

    /** Returns the pending {@link TradeRequest} sent by {@code from}, or {@code null} if none. */
    public TradeRequest getPendingRequest(UUID from) {
        return pendingRequests.get(from);
    }

    /** Removes any pending request sent by {@code from}. */
    public void clearRequest(UUID from) {
        pendingRequests.remove(from);
    }

    // -------------------------------------------------------------------------
    // Session lifecycle
    // -------------------------------------------------------------------------

    /**
     * Opens a new {@link TradeSession} between {@code playerA} and {@code playerB}.
     * Both UUIDs are registered as keys so either can look up the session.
     *
     * @throws IllegalStateException if either player already has an active session
     */
    public TradeSession openSession(UUID playerA, UUID playerB) {
        if (sessions.containsKey(playerA) || sessions.containsKey(playerB)) {
            throw new IllegalStateException("One or both players already have an active trade session.");
        }
        TradeSession session = new TradeSession(playerA, playerB);
        sessions.put(playerA, session);
        sessions.put(playerB, session);
        return session;
    }

    /** Returns the active {@link TradeSession} for the given player, or {@code null} if none. */
    public TradeSession getSession(UUID playerId) {
        return sessions.get(playerId);
    }

    /** Returns {@code true} if the player has an active trade session. */
    public boolean hasSession(UUID playerId) {
        return sessions.containsKey(playerId);
    }

    /**
     * Closes and removes the trade session for the given player.
     * Both participant entries are removed from the map.
     */
    public void closeSession(UUID playerId) {
        TradeSession session = sessions.remove(playerId);
        if (session != null) {
            sessions.remove(session.getOther(playerId));
        }
    }

    // -------------------------------------------------------------------------
    // Inner class
    // -------------------------------------------------------------------------

    /** Holds the mutable state of one player-to-player trade. */
    public static final class TradeSession {

        private final UUID playerA;
        private final UUID playerB;

        private final List<ItemStack> offeredByA = new ArrayList<>();
        private final List<ItemStack> offeredByB = new ArrayList<>();

        private double coinsByA = 0.0;
        private double coinsByB = 0.0;

        private boolean confirmedA = false;
        private boolean confirmedB = false;

        TradeSession(UUID playerA, UUID playerB) {
            this.playerA = playerA;
            this.playerB = playerB;
        }

        public UUID getPlayerA() { return playerA; }
        public UUID getPlayerB() { return playerB; }

        /** Returns the partner of the given player in this session. */
        public UUID getOther(UUID playerId) {
            return playerId.equals(playerA) ? playerB : playerA;
        }

        // Offered items

        public void addItem(UUID playerId, ItemStack item) {
            getOffered(playerId).add(item);
        }

        public boolean removeItem(UUID playerId, int index) {
            List<ItemStack> offered = getOffered(playerId);
            if (index < 0 || index >= offered.size()) return false;
            offered.remove(index);
            return true;
        }

        public List<ItemStack> getOfferedItems(UUID playerId) {
            return Collections.unmodifiableList(getOffered(playerId));
        }

        // Offered coins (escrow)

        /** Sets the coins {@code playerId} is offering. Negative amounts are clamped to zero. */
        public void setOfferedCoins(UUID playerId, double coins) {
            double amount = coins < 0 ? 0.0 : coins;
            if (playerId.equals(playerA)) coinsByA = amount;
            else coinsByB = amount;
        }

        public double getOfferedCoins(UUID playerId) {
            return playerId.equals(playerA) ? coinsByA : coinsByB;
        }

        // Confirmation

        public void confirm(UUID playerId) {
            if (playerId.equals(playerA)) confirmedA = true;
            else confirmedB = true;
        }

        public void unconfirm(UUID playerId) {
            if (playerId.equals(playerA)) confirmedA = false;
            else confirmedB = false;
        }

        public boolean isConfirmed(UUID playerId) {
            return playerId.equals(playerA) ? confirmedA : confirmedB;
        }

        public boolean bothConfirmed() {
            return confirmedA && confirmedB;
        }

        private List<ItemStack> getOffered(UUID playerId) {
            return playerId.equals(playerA) ? offeredByA : offeredByB;
        }
    }
}
