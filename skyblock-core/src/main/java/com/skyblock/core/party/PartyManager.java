package com.skyblock.core.party;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * Singleton managing player parties.
 *
 * <p>Not thread-safe; synchronize externally if accessed from multiple threads.</p>
 */
public final class PartyManager {

    private static final PartyManager INSTANCE = new PartyManager();

    public static final int MAX_SIZE = 5;

    /** Maps every party member (including leader) to their Party object. */
    private final Map<UUID, Party> partyByMember = new HashMap<>();

    /** Pending party invites: invitee UUID → leader UUID. */
    private final Map<UUID, UUID> pendingInvites = new HashMap<>();

    private PartyManager() {}

    public static PartyManager getInstance() {
        return INSTANCE;
    }

    // -------------------------------------------------------------------------
    // Invite lifecycle
    // -------------------------------------------------------------------------

    /** Records an invite from {@code leader} to {@code invitee}. */
    public void sendInvite(UUID leader, UUID invitee) {
        pendingInvites.put(invitee, leader);
    }

    /** Returns {@code true} if {@code invitee} has a pending invite from {@code leader}. */
    public boolean hasInvite(UUID leader, UUID invitee) {
        return leader.equals(pendingInvites.get(invitee));
    }

    /** Removes any pending invite held by {@code invitee}. */
    public void clearInvite(UUID invitee) {
        pendingInvites.remove(invitee);
    }

    // -------------------------------------------------------------------------
    // Party lifecycle
    // -------------------------------------------------------------------------

    /**
     * Creates a new party with {@code leader} as the sole member.
     *
     * @throws IllegalStateException if the leader is already in a party
     */
    public Party createParty(UUID leader) {
        if (partyByMember.containsKey(leader)) {
            throw new IllegalStateException("Player is already in a party.");
        }
        Party party = new Party(leader);
        partyByMember.put(leader, party);
        return party;
    }

    /**
     * Adds {@code player} to the party led by {@code leader}.
     *
     * @throws IllegalStateException if the player is already in a party or the leader has no party
     */
    public void joinParty(UUID leader, UUID player) {
        Party party = partyByMember.get(leader);
        if (party == null) {
            throw new IllegalStateException("No party found for leader.");
        }
        if (partyByMember.containsKey(player)) {
            throw new IllegalStateException("Player is already in a party.");
        }
        if (party.getAllMembers().size() >= MAX_SIZE) {
            throw new IllegalStateException("Party is full (max " + MAX_SIZE + " players).");
        }
        party.addMember(player);
        partyByMember.put(player, party);
    }

    /** Returns the {@link Party} for the given player, or {@code null} if not in one. */
    public Party getParty(UUID playerId) {
        return partyByMember.get(playerId);
    }

    /** Returns {@code true} if the player is currently in a party. */
    public boolean inParty(UUID playerId) {
        return partyByMember.containsKey(playerId);
    }

    /**
     * Removes {@code player} from their party. If they are the leader, the entire party is disbanded.
     */
    public void leaveParty(UUID player) {
        Party party = partyByMember.remove(player);
        if (party == null) return;

        if (party.getLeader().equals(player)) {
            // disband: remove all other members too
            for (UUID member : party.getMembers()) {
                partyByMember.remove(member);
            }
            party.clear();
        } else {
            party.removeMember(player);
        }
    }

    /**
     * Kicks {@code target} from the party. No-op if the target is not in the party.
     */
    public void kickFromParty(UUID target) {
        Party party = partyByMember.remove(target);
        if (party != null) {
            party.removeMember(target);
        }
    }

    // -------------------------------------------------------------------------
    // Inner class
    // -------------------------------------------------------------------------

    /** Holds the mutable state of one party. */
    public static final class Party {

        private final UUID leader;
        /** Non-leader members; does not include the leader. */
        private final Set<UUID> members = new HashSet<>();

        Party(UUID leader) {
            this.leader = leader;
        }

        public UUID getLeader() { return leader; }

        /** Returns an unmodifiable view of non-leader members. */
        public Set<UUID> getMembers() {
            return Collections.unmodifiableSet(members);
        }

        /** Returns all participants: leader + members. */
        public Set<UUID> getAllMembers() {
            Set<UUID> all = new HashSet<>(members);
            all.add(leader);
            return Collections.unmodifiableSet(all);
        }

        void addMember(UUID player) {
            members.add(player);
        }

        void removeMember(UUID player) {
            members.remove(player);
        }

        void clear() {
            members.clear();
        }
    }
}
