package com.skyblock.core.manager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
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

    /** Party finder queue: activity (e.g. dungeon floor) → waiting players, in join order. */
    private final Map<String, LinkedHashSet<UUID>> finderQueue = new HashMap<>();

    /** Reverse lookup for the finder queue: player UUID → activity they are queued for. */
    private final Map<UUID, String> finderActivity = new HashMap<>();

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

    /**
     * Accepts {@code invitee}'s pending invite, joining the inviting leader's party.
     *
     * @return the party the invitee joined
     * @throws IllegalStateException if there is no pending invite, the invitee is already in a
     *                               party, or the inviting leader no longer has a party
     */
    public Party acceptInvite(UUID invitee) {
        UUID leader = pendingInvites.get(invitee);
        if (leader == null) {
            throw new IllegalStateException("No pending invite to accept.");
        }
        pendingInvites.remove(invitee);
        joinParty(leader, invitee);
        return partyByMember.get(invitee);
    }

    /**
     * Declines {@code invitee}'s pending invite.
     *
     * @return {@code true} if an invite was pending and removed, {@code false} otherwise
     */
    public boolean declineInvite(UUID invitee) {
        return pendingInvites.remove(invitee) != null;
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

    /**
     * Transfers leadership of {@code currentLeader}'s party to {@code newLeader}. The former leader
     * remains in the party as a regular member.
     *
     * @throws IllegalStateException    if {@code currentLeader} does not lead a party
     * @throws IllegalArgumentException if {@code newLeader} is not a member of that party
     */
    public void transferLeadership(UUID currentLeader, UUID newLeader) {
        Party party = partyByMember.get(currentLeader);
        if (party == null || !party.getLeader().equals(currentLeader)) {
            throw new IllegalStateException("Only the current leader can transfer leadership.");
        }
        if (!party.getAllMembers().contains(newLeader)) {
            throw new IllegalArgumentException("New leader must be a member of the party.");
        }
        party.setLeader(newLeader);
    }

    /**
     * Disbands {@code leader}'s party, removing every member. No-op if {@code leader} is not in a party.
     *
     * @throws IllegalStateException if {@code leader} is in a party but is not its leader
     */
    public void disband(UUID leader) {
        Party party = partyByMember.get(leader);
        if (party == null) return;
        if (!party.getLeader().equals(leader)) {
            throw new IllegalStateException("Only the leader can disband the party.");
        }
        for (UUID member : party.getAllMembers()) {
            partyByMember.remove(member);
        }
        party.clear();
    }

    // -------------------------------------------------------------------------
    // Party finder queue
    // -------------------------------------------------------------------------

    /**
     * Queues {@code player} in the party finder for {@code activity} (e.g. a dungeon floor).
     *
     * @throws IllegalStateException if the player is already in a party or already queued
     */
    public void queueForFinder(UUID player, String activity) {
        if (partyByMember.containsKey(player)) {
            throw new IllegalStateException("Player is already in a party.");
        }
        if (finderActivity.containsKey(player)) {
            throw new IllegalStateException("Player is already in the party finder queue.");
        }
        finderQueue.computeIfAbsent(activity, k -> new LinkedHashSet<>()).add(player);
        finderActivity.put(player, activity);
    }

    /**
     * Removes {@code player} from the party finder queue.
     *
     * @return {@code true} if the player was queued and removed, {@code false} otherwise
     */
    public boolean leaveFinderQueue(UUID player) {
        String activity = finderActivity.remove(player);
        if (activity == null) return false;
        LinkedHashSet<UUID> queue = finderQueue.get(activity);
        if (queue != null) {
            queue.remove(player);
            if (queue.isEmpty()) finderQueue.remove(activity);
        }
        return true;
    }

    /** Returns {@code true} if the player is currently in the party finder queue. */
    public boolean isQueued(UUID player) {
        return finderActivity.containsKey(player);
    }

    /** Returns the players queued for {@code activity}, in join order. */
    public List<UUID> getFinderQueue(String activity) {
        LinkedHashSet<UUID> queue = finderQueue.get(activity);
        return queue == null ? Collections.emptyList()
                : Collections.unmodifiableList(new ArrayList<>(queue));
    }

    /**
     * Forms a dungeon party from the front of the party finder queue for {@code activity}. The
     * first queued player becomes leader and up to {@link #MAX_SIZE} players are pulled in total;
     * all selected players are removed from the queue and the new party is tagged with the
     * activity as its dungeon floor.
     *
     * @return the newly formed dungeon party, or {@code null} if no players are queued
     */
    public Party formDungeonParty(String activity) {
        LinkedHashSet<UUID> queue = finderQueue.get(activity);
        if (queue == null || queue.isEmpty()) return null;

        List<UUID> selected = new ArrayList<>();
        for (UUID id : queue) {
            selected.add(id);
            if (selected.size() >= MAX_SIZE) break;
        }
        for (UUID id : selected) {
            queue.remove(id);
            finderActivity.remove(id);
        }
        if (queue.isEmpty()) finderQueue.remove(activity);

        UUID leader = selected.get(0);
        Party party = createParty(leader);
        party.setDungeonFloor(activity);
        for (int i = 1; i < selected.size(); i++) {
            joinParty(leader, selected.get(i));
        }
        return party;
    }

    /**
     * Marks {@code leader}'s existing party as running the given dungeon {@code floor}.
     *
     * @throws IllegalStateException if {@code leader} does not lead a party
     */
    public void startDungeon(UUID leader, String floor) {
        Party party = partyByMember.get(leader);
        if (party == null || !party.getLeader().equals(leader)) {
            throw new IllegalStateException("Only the party leader can start a dungeon.");
        }
        party.setDungeonFloor(floor);
    }

    // -------------------------------------------------------------------------
    // Inner class
    // -------------------------------------------------------------------------

    /** Holds the mutable state of one party. */
    public static final class Party {

        private UUID leader;
        /** Non-leader members; does not include the leader. */
        private final Set<UUID> members = new HashSet<>();
        /** Dungeon floor this party is running, or {@code null} if not a dungeon party. */
        private String dungeonFloor;

        Party(UUID leader) {
            this.leader = leader;
        }

        public UUID getLeader() { return leader; }

        /** Returns {@code true} if this party is running a dungeon. */
        public boolean isDungeonParty() { return dungeonFloor != null; }

        /** Returns the dungeon floor this party is running, or {@code null} if none. */
        public String getDungeonFloor() { return dungeonFloor; }

        void setDungeonFloor(String floor) { this.dungeonFloor = floor; }

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

        /** Promotes {@code newLeader} (a current member) to leader; the old leader becomes a member. */
        void setLeader(UUID newLeader) {
            if (newLeader.equals(leader)) return;
            members.remove(newLeader);
            members.add(leader);
            leader = newLeader;
        }

        void removeMember(UUID player) {
            members.remove(player);
        }

        void clear() {
            members.clear();
        }
    }
}
