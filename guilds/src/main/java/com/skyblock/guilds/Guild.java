package com.skyblock.guilds;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;

/**
 * A guild: a named group of players founded and owned by a single player.
 *
 * <p>Instances are created and mutated exclusively through
 * {@link GuildManager}; the owner is always a member.</p>
 */
public final class Guild {

    private final String name;
    private final UUID owner;
    private final Set<UUID> members = new LinkedHashSet<>();

    Guild(String name, UUID owner) {
        this.name = name;
        this.owner = owner;
        members.add(owner);
    }

    /**
     * Returns the unique guild name.
     *
     * @return the guild name
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the unique id of the player who owns the guild.
     *
     * @return the owner's unique id
     */
    public UUID getOwner() {
        return owner;
    }

    /**
     * Returns the members of the guild in join order, the owner first.
     *
     * @return an unmodifiable view of the members' unique ids
     */
    public Set<UUID> getMembers() {
        return Collections.unmodifiableSet(members);
    }

    /**
     * Returns whether the player is a member of the guild.
     *
     * @param playerId the player's UUID
     * @return {@code true} if the player is a member
     */
    public boolean isMember(UUID playerId) {
        return members.contains(playerId);
    }

    void addMember(UUID playerId) {
        members.add(playerId);
    }

    void removeMember(UUID playerId) {
        members.remove(playerId);
    }
}
