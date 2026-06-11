package com.skyblock.guilds;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * Manages guilds and their memberships.
 *
 * <p>Guilds are identified by their unique name. A player can be a member of
 * at most one guild at a time; the player who creates a guild becomes its
 * owner and first member. Not thread-safe; synchronize externally if accessed
 * from multiple threads.</p>
 */
public final class GuildManager {

    private final Map<String, Set<UUID>> guildMembers = new HashMap<>();
    private final Map<String, UUID> guildOwners = new HashMap<>();
    private final Map<UUID, String> playerGuilds = new HashMap<>();

    /**
     * Creates a new guild with the given owner as its first member.
     *
     * @param guildName the unique guild name, must not be null or blank
     * @param owner     the unique id of the founding player, must not be null
     * @throws IllegalArgumentException if the name is null or blank, the owner
     *                                  is null, a guild with that name already
     *                                  exists, or the owner is already in a guild
     */
    public void createGuild(String guildName, UUID owner) {
        if (guildName == null || guildName.isBlank()) {
            throw new IllegalArgumentException("guildName must not be null or blank");
        }
        if (owner == null) {
            throw new IllegalArgumentException("owner must not be null");
        }
        if (guildMembers.containsKey(guildName)) {
            throw new IllegalArgumentException("guild already exists: " + guildName);
        }
        if (playerGuilds.containsKey(owner)) {
            throw new IllegalArgumentException(
                    "player is already in a guild: " + playerGuilds.get(owner));
        }
        Set<UUID> members = new LinkedHashSet<>();
        members.add(owner);
        guildMembers.put(guildName, members);
        guildOwners.put(guildName, owner);
        playerGuilds.put(owner, guildName);
    }

    /**
     * Disbands a guild, removing all of its members.
     *
     * @param guildName the guild name
     * @return {@code true} if the guild existed and has been disbanded
     */
    public boolean disbandGuild(String guildName) {
        Set<UUID> members = guildMembers.remove(guildName);
        if (members == null) {
            return false;
        }
        guildOwners.remove(guildName);
        for (UUID member : members) {
            playerGuilds.remove(member);
        }
        return true;
    }

    /**
     * Returns whether a guild with the given name exists.
     *
     * @param guildName the guild name
     * @return {@code true} if the guild exists
     */
    public boolean guildExists(String guildName) {
        return guildMembers.containsKey(guildName);
    }

    /**
     * Adds a player to an existing guild.
     *
     * @param guildName the guild name
     * @param playerId  the unique id of the player, must not be null
     * @throws IllegalArgumentException if the guild does not exist, the player
     *                                  is null, or the player is already in a guild
     */
    public void addMember(String guildName, UUID playerId) {
        if (playerId == null) {
            throw new IllegalArgumentException("playerId must not be null");
        }
        Set<UUID> members = guildMembers.get(guildName);
        if (members == null) {
            throw new IllegalArgumentException("guild does not exist: " + guildName);
        }
        if (playerGuilds.containsKey(playerId)) {
            throw new IllegalArgumentException(
                    "player is already in a guild: " + playerGuilds.get(playerId));
        }
        members.add(playerId);
        playerGuilds.put(playerId, guildName);
    }

    /**
     * Removes a player from their guild. If the player is the guild owner,
     * the guild is disbanded.
     *
     * @param playerId the unique id of the player
     * @return {@code true} if the player was in a guild and has been removed
     */
    public boolean removeMember(UUID playerId) {
        String guildName = playerGuilds.get(playerId);
        if (guildName == null) {
            return false;
        }
        if (playerId.equals(guildOwners.get(guildName))) {
            return disbandGuild(guildName);
        }
        guildMembers.get(guildName).remove(playerId);
        playerGuilds.remove(playerId);
        return true;
    }

    /**
     * Returns the name of the guild the player belongs to.
     *
     * @param playerId the unique id of the player
     * @return the guild name, or {@code null} if the player is not in a guild
     */
    public String getGuild(UUID playerId) {
        return playerGuilds.get(playerId);
    }

    /**
     * Returns the unique id of a guild's owner.
     *
     * @param guildName the guild name
     * @return the owner's unique id
     * @throws IllegalArgumentException if the guild does not exist
     */
    public UUID getOwner(String guildName) {
        UUID owner = guildOwners.get(guildName);
        if (owner == null) {
            throw new IllegalArgumentException("guild does not exist: " + guildName);
        }
        return owner;
    }

    /**
     * Returns the members of a guild in join order.
     *
     * @param guildName the guild name
     * @return an unmodifiable view of the members' unique ids
     * @throws IllegalArgumentException if the guild does not exist
     */
    public Set<UUID> getMembers(String guildName) {
        Set<UUID> members = guildMembers.get(guildName);
        if (members == null) {
            throw new IllegalArgumentException("guild does not exist: " + guildName);
        }
        return Collections.unmodifiableSet(members);
    }

    /**
     * Returns the names of all existing guilds.
     *
     * @return an unmodifiable view of the guild names
     */
    public Set<String> getGuildNames() {
        return Collections.unmodifiableSet(guildMembers.keySet());
    }
}
