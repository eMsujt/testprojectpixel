package com.skyblock.guilds;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.entity.Player;

/**
 * Manages guilds and their memberships.
 *
 * <p>Guilds are identified by their unique name. A player can be a member of
 * at most one guild at a time; the player who creates a guild becomes its
 * owner and first member. Not thread-safe; synchronize externally if accessed
 * from multiple threads.</p>
 */
public final class GuildManager {

    /** Each member's unique id mapped to the guild they belong to. */
    private final HashMap<UUID, Guild> guilds = new HashMap<>();

    /**
     * Creates a new guild with the given player as its owner and first member.
     *
     * @param owner     the founding player, must not be null
     * @param guildName the unique guild name, must not be null or blank
     * @return the newly created guild
     * @throws IllegalArgumentException if the owner is null, the name is null
     *                                  or blank, a guild with that name already
     *                                  exists, or the owner is already in a guild
     */
    public Guild createGuild(Player owner, String guildName) {
        if (owner == null) {
            throw new IllegalArgumentException("owner must not be null");
        }
        if (guildName == null || guildName.isBlank()) {
            throw new IllegalArgumentException("guildName must not be null or blank");
        }
        UUID ownerId = owner.getUniqueId();
        Guild current = guilds.get(ownerId);
        if (current != null) {
            throw new IllegalArgumentException(
                    "player is already in a guild: " + current.getName());
        }
        if (guildExists(guildName)) {
            throw new IllegalArgumentException("guild already exists: " + guildName);
        }
        Guild guild = new Guild(guildName, ownerId);
        guilds.put(ownerId, guild);
        return guild;
    }

    /**
     * Disbands a guild, removing all of its members.
     *
     * @param guildName the guild name
     * @return {@code true} if the guild existed and has been disbanded
     */
    public boolean disbandGuild(String guildName) {
        Guild guild = findGuild(guildName);
        if (guild == null) {
            return false;
        }
        for (UUID member : guild.getMembers()) {
            guilds.remove(member);
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
        return findGuild(guildName) != null;
    }

    /**
     * Adds a player to an existing guild.
     *
     * @param player    the joining player, must not be null
     * @param guildName the guild name
     * @throws IllegalArgumentException if the player is null, the guild does
     *                                  not exist, or the player is already in a guild
     */
    public void addMember(Player player, String guildName) {
        if (player == null) {
            throw new IllegalArgumentException("player must not be null");
        }
        Guild guild = findGuild(guildName);
        if (guild == null) {
            throw new IllegalArgumentException("guild does not exist: " + guildName);
        }
        UUID playerId = player.getUniqueId();
        Guild current = guilds.get(playerId);
        if (current != null) {
            throw new IllegalArgumentException(
                    "player is already in a guild: " + current.getName());
        }
        guild.addMember(playerId);
        guilds.put(playerId, guild);
    }

    /**
     * Removes a player from their guild. If the player is the guild owner,
     * the guild is disbanded.
     *
     * @param playerId the unique id of the player
     * @return {@code true} if the player was in a guild and has been removed
     */
    public boolean removeMember(UUID playerId) {
        Guild guild = guilds.get(playerId);
        if (guild == null) {
            return false;
        }
        if (playerId.equals(guild.getOwner())) {
            return disbandGuild(guild.getName());
        }
        guild.removeMember(playerId);
        guilds.remove(playerId);
        return true;
    }

    /**
     * Returns the guild the player belongs to.
     *
     * @param playerId the unique id of the player
     * @return the player's guild, or {@code null} if the player is not in a guild
     */
    public Guild getGuild(UUID playerId) {
        return guilds.get(playerId);
    }

    private Guild findGuild(String guildName) {
        for (Guild guild : guilds.values()) {
            if (guild.getName().equals(guildName)) {
                return guild;
            }
        }
        return null;
    }
}
