package com.skyblock.core.friend;

import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
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

    // -------------------------------------------------------------------------
    // Persistence
    // -------------------------------------------------------------------------

    /**
     * Loads friend lists and pending requests from {@code friends.yml} inside the given data folder.
     *
     * @param dataFolder the plugin data folder, must not be null
     */
    public void load(File dataFolder) {
        File file = new File(dataFolder, "friends.yml");
        if (!file.exists()) {
            return;
        }
        YamlConfiguration cfg = YamlConfiguration.loadConfiguration(file);
        friends.clear();
        pendingRequests.clear();
        if (cfg.isConfigurationSection("friends")) {
            for (String key : cfg.getConfigurationSection("friends").getKeys(false)) {
                try {
                    UUID uuid = UUID.fromString(key);
                    List<String> list = cfg.getStringList("friends." + key);
                    Set<UUID> set = new HashSet<>();
                    for (String s : list) {
                        try { set.add(UUID.fromString(s)); } catch (IllegalArgumentException ignored) {}
                    }
                    if (!set.isEmpty()) {
                        friends.put(uuid, set);
                    }
                } catch (IllegalArgumentException ignored) {}
            }
        }
        if (cfg.isConfigurationSection("pending")) {
            for (String key : cfg.getConfigurationSection("pending").getKeys(false)) {
                try {
                    UUID uuid = UUID.fromString(key);
                    List<String> list = cfg.getStringList("pending." + key);
                    Set<UUID> set = new HashSet<>();
                    for (String s : list) {
                        try { set.add(UUID.fromString(s)); } catch (IllegalArgumentException ignored) {}
                    }
                    if (!set.isEmpty()) {
                        pendingRequests.put(uuid, set);
                    }
                } catch (IllegalArgumentException ignored) {}
            }
        }
    }

    /**
     * Saves all friend lists and pending requests to {@code friends.yml} inside the given data folder.
     *
     * @param dataFolder the plugin data folder, must not be null
     * @throws RuntimeException if the file cannot be written
     */
    public void save(File dataFolder) {
        File file = new File(dataFolder, "friends.yml");
        YamlConfiguration cfg = new YamlConfiguration();
        for (Map.Entry<UUID, Set<UUID>> entry : friends.entrySet()) {
            List<String> list = new java.util.ArrayList<>();
            for (UUID id : entry.getValue()) {
                list.add(id.toString());
            }
            cfg.set("friends." + entry.getKey().toString(), list);
        }
        for (Map.Entry<UUID, Set<UUID>> entry : pendingRequests.entrySet()) {
            List<String> list = new java.util.ArrayList<>();
            for (UUID id : entry.getValue()) {
                list.add(id.toString());
            }
            cfg.set("pending." + entry.getKey().toString(), list);
        }
        try {
            cfg.save(file);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save friends.yml", e);
        }
    }
}
