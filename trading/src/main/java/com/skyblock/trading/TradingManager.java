package com.skyblock.trading;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Manages trade requests between players: a player sends a request to a
 * target, who may accept or decline it. Each player can have at most one
 * outgoing request at a time.
 *
 * <p>Requests are stored in a {@link ConcurrentHashMap} keyed by the sender's
 * id, so all operations are thread-safe.</p>
 */
public final class TradingManager {

    /** A pending trade request from one player to another. */
    public static final class TradeRequest {

        private final UUID sender;
        private final UUID target;

        private TradeRequest(UUID sender, UUID target) {
            this.sender = sender;
            this.target = target;
        }

        /** Returns the id of the player who sent the request. */
        public UUID getSender() {
            return sender;
        }

        /** Returns the id of the player the request was sent to. */
        public UUID getTarget() {
            return target;
        }
    }

    private final ConcurrentHashMap<UUID, TradeRequest> requests = new ConcurrentHashMap<>();

    /**
     * Sends a trade request from one player to another, replacing any
     * existing outgoing request from the sender.
     *
     * @param sender the player sending the request
     * @param target the player being asked to trade, distinct from the sender
     * @return the created request
     */
    public TradeRequest sendRequest(UUID sender, UUID target) {
        requirePlayer(sender, "sender");
        requirePlayer(target, "target");
        if (sender.equals(target)) {
            throw new IllegalArgumentException("a player cannot request a trade with themselves");
        }
        TradeRequest request = new TradeRequest(sender, target);
        requests.put(sender, request);
        return request;
    }

    /**
     * Returns the sender's outgoing request, if any.
     *
     * @param sender the player whose request to look up
     * @return the request, or empty if the sender has none pending
     */
    public Optional<TradeRequest> getRequest(UUID sender) {
        return Optional.ofNullable(requests.get(requirePlayer(sender, "sender")));
    }

    /**
     * Accepts the sender's pending request, removing it.
     *
     * @param sender the player who sent the request
     * @param target the player accepting it, must match the request's target
     * @return the accepted request
     * @throws IllegalStateException if the sender has no pending request
     */
    public TradeRequest acceptRequest(UUID sender, UUID target) {
        requirePlayer(sender, "sender");
        requirePlayer(target, "target");
        TradeRequest request = requests.get(sender);
        if (request == null) {
            throw new IllegalStateException("no pending request from " + sender);
        }
        if (!request.target.equals(target)) {
            throw new IllegalArgumentException("request was not sent to " + target);
        }
        requests.remove(sender, request);
        return request;
    }

    /**
     * Declines and removes the sender's pending request, if any.
     *
     * @param sender the player whose request to decline
     * @return {@code true} if a request was removed
     */
    public boolean declineRequest(UUID sender) {
        return requests.remove(requirePlayer(sender, "sender")) != null;
    }

    private static UUID requirePlayer(UUID player, String name) {
        if (player == null) {
            throw new IllegalArgumentException(name + " must be non-null");
        }
        return player;
    }
}
