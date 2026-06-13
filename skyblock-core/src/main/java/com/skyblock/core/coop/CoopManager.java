package com.skyblock.core.coop;

import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

/**
 * Singleton managing co-op island groups with YAML persistence.
 *
 * <p>Each island owner may have up to {@value #MAX_MEMBERS} co-op members (including themselves).
 * Not thread-safe; synchronize externally if accessed from multiple threads.</p>
 */
public final class CoopManager {

    private static final CoopManager INSTANCE = new CoopManager();

    public static final int MAX_MEMBERS = 4;

    /** Maps island owner UUID → set of co-op member UUIDs (includes the owner). */
    private final Map<UUID, Set<UUID>> coopMembers = new HashMap<>();

    /** Maps each member UUID → their island owner UUID (for reverse lookups). */
    private final Map<UUID, UUID> memberToOwner = new HashMap<>();

    /** Pending co-op invites: invitee UUID → island owner UUID. */
    private final Map<UUID, UUID> pendingInvites = new HashMap<>();

    private CoopManager() {}

    public static CoopManager getInstance() {
        return INSTANCE;
    }

    // -------------------------------------------------------------------------
    // Invite lifecycle
    // -------------------------------------------------------------------------

    /** Records a co-op invite from {@code owner} to {@code invitee}. */
    public void sendInvite(UUID owner, UUID invitee) {
        pendingInvites.put(invitee, owner);
    }

    /** Returns {@code true} if {@code invitee} has a pending invite from {@code owner}. */
    public boolean hasInvite(UUID owner, UUID invitee) {
        return owner.equals(pendingInvites.get(invitee));
    }

    /** Removes any pending invite held by {@code invitee}. */
    public void clearInvite(UUID invitee) {
        pendingInvites.remove(invitee);
    }

    // -------------------------------------------------------------------------
    // Co-op lifecycle
    // -------------------------------------------------------------------------

    /**
     * Returns {@code true} if the player is already in any co-op group (as owner or member).
     */
    public boolean inCoop(UUID playerId) {
        return memberToOwner.containsKey(playerId);
    }

    /**
     * Returns the island owner for the given player's co-op group, or {@code null} if not in one.
     */
    public UUID getOwner(UUID playerId) {
        return memberToOwner.get(playerId);
    }

    /**
     * Returns an unmodifiable view of the co-op members for the given island owner,
     * or an empty set if no group exists.
     */
    public Set<UUID> getMembers(UUID owner) {
        Objects.requireNonNull(owner, "owner");
        Set<UUID> members = coopMembers.get(owner);
        return members == null ? Collections.emptySet() : Collections.unmodifiableSet(members);
    }

    /**
     * Creates a co-op group for {@code owner}, adding them as the first member.
     *
     * @throws IllegalStateException if the owner is already in a co-op group
     */
    public void createCoop(UUID owner) {
        Objects.requireNonNull(owner, "owner");
        if (memberToOwner.containsKey(owner)) {
            throw new IllegalStateException("Player is already in a co-op group.");
        }
        Set<UUID> members = new HashSet<>();
        members.add(owner);
        coopMembers.put(owner, members);
        memberToOwner.put(owner, owner);
    }

    /**
     * Adds {@code player} to the co-op group owned by {@code owner}.
     *
     * @throws IllegalStateException if the player is already in a group, the owner has no group,
     *                               or the group is full
     */
    public void joinCoop(UUID owner, UUID player) {
        Objects.requireNonNull(owner, "owner");
        Objects.requireNonNull(player, "player");
        Set<UUID> members = coopMembers.get(owner);
        if (members == null) {
            throw new IllegalStateException("No co-op group found for owner.");
        }
        if (memberToOwner.containsKey(player)) {
            throw new IllegalStateException("Player is already in a co-op group.");
        }
        if (members.size() >= MAX_MEMBERS) {
            throw new IllegalStateException("Co-op group is full (max " + MAX_MEMBERS + " players).");
        }
        members.add(player);
        memberToOwner.put(player, owner);
    }

    /**
     * Removes {@code player} from their co-op group.
     * If they are the owner, the entire group is disbanded.
     */
    public void leaveCoop(UUID player) {
        Objects.requireNonNull(player, "player");
        UUID owner = memberToOwner.remove(player);
        if (owner == null) return;

        if (owner.equals(player)) {
            // disband: remove all members
            Set<UUID> members = coopMembers.remove(player);
            if (members != null) {
                for (UUID memberId : members) {
                    if (!memberId.equals(player)) {
                        memberToOwner.remove(memberId);
                    }
                }
            }
        } else {
            Set<UUID> members = coopMembers.get(owner);
            if (members != null) {
                members.remove(player);
            }
        }
    }

    /**
     * Kicks {@code target} from the co-op group. No-op if the target is not in the group.
     *
     * @throws IllegalStateException if the target is the owner (owners must disband instead)
     */
    public void kickFromCoop(UUID target) {
        Objects.requireNonNull(target, "target");
        UUID owner = memberToOwner.get(target);
        if (owner == null) return;
        if (owner.equals(target)) {
            throw new IllegalStateException("Owner must disband the co-op, not kick themselves.");
        }
        memberToOwner.remove(target);
        Set<UUID> members = coopMembers.get(owner);
        if (members != null) {
            members.remove(target);
        }
    }

    // -------------------------------------------------------------------------
    // Persistence
    // -------------------------------------------------------------------------

    public void load(File dataFolder) {
        File file = new File(dataFolder, "coop.yml");
        if (!file.exists()) return;
        YamlConfiguration cfg = YamlConfiguration.loadConfiguration(file);
        coopMembers.clear();
        memberToOwner.clear();
        for (String ownerKey : cfg.getKeys(false)) {
            try {
                UUID owner = UUID.fromString(ownerKey);
                Set<UUID> members = new HashSet<>();
                for (String memberStr : cfg.getStringList(ownerKey)) {
                    try {
                        UUID memberId = UUID.fromString(memberStr);
                        members.add(memberId);
                        memberToOwner.put(memberId, owner);
                    } catch (IllegalArgumentException ignored) {
                        // skip malformed member entry
                    }
                }
                if (!members.isEmpty()) {
                    members.add(owner);
                    memberToOwner.put(owner, owner);
                    coopMembers.put(owner, members);
                }
            } catch (IllegalArgumentException ignored) {
                // skip malformed owner key
            }
        }
    }

    public void save(File dataFolder) {
        File file = new File(dataFolder, "coop.yml");
        YamlConfiguration cfg = new YamlConfiguration();
        for (Map.Entry<UUID, Set<UUID>> entry : coopMembers.entrySet()) {
            if (!entry.getValue().isEmpty()) {
                cfg.set(entry.getKey().toString(),
                        entry.getValue().stream()
                                .map(UUID::toString)
                                .collect(java.util.stream.Collectors.toList()));
            }
        }
        try {
            cfg.save(file);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save coop.yml", e);
        }
    }
}
