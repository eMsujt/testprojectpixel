package com.skyblock.party;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * Manages parties and their memberships.
 *
 * <p>Parties are identified by their leader's unique id. A player can be a
 * member of at most one party at a time; the player who creates a party
 * becomes its leader. Not thread-safe; synchronize externally if accessed
 * from multiple threads.</p>
 */
public final class PartyManager {

    /** Role of a player within a party. */
    public enum PartyRole {
        /** The party leader; disbanding the leader's membership disbands the party. */
        LEADER,
        /** A moderator who may manage regular members. */
        MODERATOR,
        /** A regular party member. */
        MEMBER
    }

    private final Map<UUID, Map<UUID, PartyRole>> partyMembers = new HashMap<>();
    private final Map<UUID, UUID> playerParties = new HashMap<>();

    /**
     * Creates a new party led by the given player.
     *
     * @param leader the unique id of the founding player, must not be null
     * @throws IllegalArgumentException if the leader is null or is already in a party
     */
    public void createParty(UUID leader) {
        if (leader == null) {
            throw new IllegalArgumentException("leader must not be null");
        }
        if (playerParties.containsKey(leader)) {
            throw new IllegalArgumentException(
                    "player is already in a party: " + playerParties.get(leader));
        }
        Map<UUID, PartyRole> members = new LinkedHashMap<>();
        members.put(leader, PartyRole.LEADER);
        partyMembers.put(leader, members);
        playerParties.put(leader, leader);
    }

    /**
     * Disbands a party, removing all of its members.
     *
     * @param leader the unique id of the party leader
     * @return {@code true} if the party existed and has been disbanded
     */
    public boolean disbandParty(UUID leader) {
        Map<UUID, PartyRole> members = partyMembers.remove(leader);
        if (members == null) {
            return false;
        }
        for (UUID member : members.keySet()) {
            playerParties.remove(member);
        }
        return true;
    }

    /**
     * Returns whether a party led by the given player exists.
     *
     * @param leader the unique id of the party leader
     * @return {@code true} if the party exists
     */
    public boolean partyExists(UUID leader) {
        return partyMembers.containsKey(leader);
    }

    /**
     * Adds a player to an existing party as a regular member.
     *
     * @param leader   the unique id of the party leader
     * @param playerId the unique id of the player, must not be null
     * @throws IllegalArgumentException if the party does not exist, the player
     *                                  is null, or the player is already in a party
     */
    public void addMember(UUID leader, UUID playerId) {
        if (playerId == null) {
            throw new IllegalArgumentException("playerId must not be null");
        }
        Map<UUID, PartyRole> members = partyMembers.get(leader);
        if (members == null) {
            throw new IllegalArgumentException("party does not exist: " + leader);
        }
        if (playerParties.containsKey(playerId)) {
            throw new IllegalArgumentException(
                    "player is already in a party: " + playerParties.get(playerId));
        }
        members.put(playerId, PartyRole.MEMBER);
        playerParties.put(playerId, leader);
    }

    /**
     * Removes a player from their party. If the player is the party leader,
     * the party is disbanded.
     *
     * @param playerId the unique id of the player
     * @return {@code true} if the player was in a party and has been removed
     */
    public boolean removeMember(UUID playerId) {
        UUID leader = playerParties.get(playerId);
        if (leader == null) {
            return false;
        }
        if (playerId.equals(leader)) {
            return disbandParty(leader);
        }
        partyMembers.get(leader).remove(playerId);
        playerParties.remove(playerId);
        return true;
    }

    /**
     * Promotes or demotes a party member to the given role. The leader's role
     * cannot be changed, and a member cannot be promoted to leader.
     *
     * @param playerId the unique id of the player
     * @param role     the new role, must not be null or {@link PartyRole#LEADER}
     * @throws IllegalArgumentException if the role is null or LEADER, the player
     *                                  is not in a party, or the player is the leader
     */
    public void setRole(UUID playerId, PartyRole role) {
        if (role == null || role == PartyRole.LEADER) {
            throw new IllegalArgumentException("role must be MODERATOR or MEMBER");
        }
        UUID leader = playerParties.get(playerId);
        if (leader == null) {
            throw new IllegalArgumentException("player is not in a party: " + playerId);
        }
        if (playerId.equals(leader)) {
            throw new IllegalArgumentException("cannot change the leader's role");
        }
        partyMembers.get(leader).put(playerId, role);
    }

    /**
     * Returns the role of a player within their party.
     *
     * @param playerId the unique id of the player
     * @return the player's role, or {@code null} if the player is not in a party
     */
    public PartyRole getRole(UUID playerId) {
        UUID leader = playerParties.get(playerId);
        if (leader == null) {
            return null;
        }
        return partyMembers.get(leader).get(playerId);
    }

    /**
     * Returns the unique id of the leader of the party the player belongs to.
     *
     * @param playerId the unique id of the player
     * @return the leader's unique id, or {@code null} if the player is not in a party
     */
    public UUID getParty(UUID playerId) {
        return playerParties.get(playerId);
    }

    /**
     * Returns the members of a party in join order.
     *
     * @param leader the unique id of the party leader
     * @return an unmodifiable view of the members' unique ids
     * @throws IllegalArgumentException if the party does not exist
     */
    public Set<UUID> getMembers(UUID leader) {
        Map<UUID, PartyRole> members = partyMembers.get(leader);
        if (members == null) {
            throw new IllegalArgumentException("party does not exist: " + leader);
        }
        return Collections.unmodifiableSet(members.keySet());
    }
}
