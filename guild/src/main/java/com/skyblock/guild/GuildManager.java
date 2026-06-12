package com.skyblock.guild;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * Manages the members of a single guild and their ranks.
 *
 * <p>The player who founds the guild holds the {@link GuildRank#OWNER} rank;
 * every guild has exactly one owner. Other members hold one of the lower
 * ranks and can be promoted or demoted one step at a time. Not thread-safe;
 * synchronize externally if accessed from multiple threads.</p>
 */
public final class GuildManager {

    /**
     * The rank a member holds within the guild, ordered from lowest to highest.
     */
    public enum GuildRank {
        MEMBER,
        OFFICER,
        OWNER
    }

    private final Map<UUID, GuildRank> members = new LinkedHashMap<>();

    /**
     * Creates a guild manager with the given player as owner and first member.
     *
     * @param owner the unique id of the founding player, must not be null
     */
    public GuildManager(UUID owner) {
        Objects.requireNonNull(owner, "owner");
        members.put(owner, GuildRank.OWNER);
    }

    /**
     * Adds a player to the guild with the {@link GuildRank#MEMBER} rank.
     *
     * @param playerId the unique id of the player, must not be null
     * @throws IllegalArgumentException if the player is already a member
     */
    public void addMember(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        if (members.containsKey(playerId)) {
            throw new IllegalArgumentException("player is already a member: " + playerId);
        }
        members.put(playerId, GuildRank.MEMBER);
    }

    /**
     * Removes a player from the guild. The owner cannot be removed.
     *
     * @param playerId the unique id of the player
     * @return {@code true} if the player was a member and has been removed
     * @throws IllegalStateException if the player is the guild owner
     */
    public boolean removeMember(UUID playerId) {
        if (members.get(playerId) == GuildRank.OWNER) {
            throw new IllegalStateException("the guild owner cannot be removed");
        }
        return members.remove(playerId) != null;
    }

    /**
     * Promotes a member one rank, from {@link GuildRank#MEMBER} to
     * {@link GuildRank#OFFICER}. The {@link GuildRank#OWNER} rank cannot be
     * reached by promotion.
     *
     * @param playerId the unique id of the member
     * @return the member's new rank
     * @throws IllegalArgumentException if the player is not a member
     * @throws IllegalStateException    if the member cannot be promoted further
     */
    public GuildRank promote(UUID playerId) {
        GuildRank rank = getRank(playerId);
        if (rank != GuildRank.MEMBER) {
            throw new IllegalStateException("cannot promote beyond " + rank);
        }
        members.put(playerId, GuildRank.OFFICER);
        return GuildRank.OFFICER;
    }

    /**
     * Demotes a member one rank, from {@link GuildRank#OFFICER} to
     * {@link GuildRank#MEMBER}. The owner cannot be demoted.
     *
     * @param playerId the unique id of the member
     * @return the member's new rank
     * @throws IllegalArgumentException if the player is not a member
     * @throws IllegalStateException    if the member cannot be demoted further
     */
    public GuildRank demote(UUID playerId) {
        GuildRank rank = getRank(playerId);
        if (rank != GuildRank.OFFICER) {
            throw new IllegalStateException("cannot demote " + rank);
        }
        members.put(playerId, GuildRank.MEMBER);
        return GuildRank.MEMBER;
    }

    /**
     * Returns whether the given player is a member of the guild.
     *
     * @param playerId the unique id of the player
     * @return {@code true} if the player is a member
     */
    public boolean isMember(UUID playerId) {
        return members.containsKey(playerId);
    }

    /**
     * Returns the rank held by the given member.
     *
     * @param playerId the unique id of the member
     * @return the member's rank
     * @throws IllegalArgumentException if the player is not a member
     */
    public GuildRank getRank(UUID playerId) {
        GuildRank rank = members.get(playerId);
        if (rank == null) {
            throw new IllegalArgumentException("player is not a member: " + playerId);
        }
        return rank;
    }

    /**
     * Returns the members and their ranks in join order.
     *
     * @return an unmodifiable view of the members' unique ids mapped to ranks
     */
    public Map<UUID, GuildRank> getMembers() {
        return Collections.unmodifiableMap(members);
    }
}
