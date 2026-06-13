package com.skyblock.core.friend;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

/**
 * Singleton managing per-player friend lists and pending friend requests.
 *
 * <p>Not thread-safe; synchronize externally if accessed from multiple threads.</p>
 */
public final class FriendManager {

    private static final FriendManager INSTANCE = new FriendManager();

    /** Bidirectional friend relationships: player UUID → set of friend UUIDs. */
    private final Map<UUID, Set<UUID>> friends = new HashMap<>();

    /** Pending friend requests: recipient UUID → set of sender UUIDs. */
    private final Map<UUID, Set<UUID>> pendingRequests = new HashMap<>();

    private FriendManager() {}

    public static FriendManager getInstance() {
        return INSTANCE;
    }

    // -------------------------------------------------------------------------
    // Core operations
    // -------------------------------------------------------------------------

    /**
     * Adds a bidirectional friend relationship between {@code a} and {@code b}.
     *
     * @throws IllegalStateException if they are already friends
     */
    public void addFriend(UUID a, UUID b) {
        Objects.requireNonNull(a, "a");
        Objects.requireNonNull(b, "b");
        if (areFriends(a, b)) {
            throw new IllegalStateException("You are already friends with that player.");
        }
        friends.computeIfAbsent(a, k -> new HashSet<>()).add(b);
        friends.computeIfAbsent(b, k -> new HashSet<>()).add(a);
    }

    /**
     * Removes the bidirectional friend relationship between {@code a} and {@code b}.
     *
     * @throws IllegalStateException if they are not friends
     */
    public void removeFriend(UUID a, UUID b) {
        Objects.requireNonNull(a, "a");
        Objects.requireNonNull(b, "b");
        if (!areFriends(a, b)) {
            throw new IllegalStateException("That player is not in your friend list.");
        }
        friends.getOrDefault(a, Collections.emptySet()).remove(b);
        friends.getOrDefault(b, Collections.emptySet()).remove(a);
    }

    /**
     * Returns an unmodifiable view of {@code player}'s friend set.
     */
    public Set<UUID> getFriends(UUID player) {
        Objects.requireNonNull(player, "player");
        return Collections.unmodifiableSet(friends.getOrDefault(player, Collections.emptySet()));
    }

    /**
     * Returns {@code true} if {@code a} and {@code b} are friends.
     */
    public boolean areFriends(UUID a, UUID b) {
        Objects.requireNonNull(a, "a");
        Objects.requireNonNull(b, "b");
        return friends.getOrDefault(a, Collections.emptySet()).contains(b);
    }

    // -------------------------------------------------------------------------
    // Request lifecycle
    // -------------------------------------------------------------------------

    /**
     * Records a friend request from {@code sender} to {@code recipient}.
     *
     * @throws IllegalStateException if they are already friends or a request is already pending
     */
    public void sendRequest(UUID sender, UUID recipient) {
        Objects.requireNonNull(sender, "sender");
        Objects.requireNonNull(recipient, "recipient");
        if (areFriends(sender, recipient)) {
            throw new IllegalStateException("You are already friends with that player.");
        }
        Set<UUID> pending = pendingRequests.computeIfAbsent(recipient, k -> new HashSet<>());
        if (!pending.add(sender)) {
            throw new IllegalStateException("You have already sent a friend request to that player.");
        }
    }

    /**
     * Accepts the pending request from {@code sender} to {@code recipient}, making them friends.
     *
     * @throws IllegalStateException if no such pending request exists
     */
    public void acceptRequest(UUID recipient, UUID sender) {
        Objects.requireNonNull(recipient, "recipient");
        Objects.requireNonNull(sender, "sender");
        Set<UUID> pending = pendingRequests.getOrDefault(recipient, Collections.emptySet());
        if (!pending.remove(sender)) {
            throw new IllegalStateException("No pending friend request from that player.");
        }
        addFriend(recipient, sender);
    }

    /**
     * Declines and removes the pending request from {@code sender} to {@code recipient}.
     *
     * @return {@code true} if a request was removed, {@code false} if none existed
     */
    public boolean declineRequest(UUID recipient, UUID sender) {
        Objects.requireNonNull(recipient, "recipient");
        Objects.requireNonNull(sender, "sender");
        Set<UUID> pending = pendingRequests.getOrDefault(recipient, Collections.emptySet());
        return pending.remove(sender);
    }

    /**
     * Returns {@code true} if {@code sender} has a pending request to {@code recipient}.
     */
    public boolean hasPendingRequest(UUID recipient, UUID sender) {
        Objects.requireNonNull(recipient, "recipient");
        Objects.requireNonNull(sender, "sender");
        return pendingRequests.getOrDefault(recipient, Collections.emptySet()).contains(sender);
    }
}
