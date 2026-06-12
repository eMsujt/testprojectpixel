package com.skyblock.core.guild;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

/**
 * Singleton managing player guilds.
 *
 * <p>Not thread-safe; synchronize externally if accessed from multiple threads.</p>
 */
public final class GuildManager {

    private static final GuildManager INSTANCE = new GuildManager();

    /** All guilds, keyed by lower-cased name for case-insensitive lookup. */
    private final Map<String, Guild> guildByName = new HashMap<>();

    /** Maps every guild member (including leader) to their Guild. */
    private final Map<UUID, Guild> guildByMember = new HashMap<>();

    /** Pending guild invites: invitee UUID → guild name. */
    private final Map<UUID, String> pendingInvites = new HashMap<>();

    private GuildManager() {}

    public static GuildManager getInstance() {
        return INSTANCE;
    }

    // -------------------------------------------------------------------------
    // Guild lifecycle
    // -------------------------------------------------------------------------

    /**
     * Creates a new guild with the given name and leader.
     *
     * @param name   the guild name; must be unique (case-insensitive)
     * @param leader the UUID of the founding leader
     * @return the newly created {@link Guild}
     * @throws IllegalStateException    if {@code leader} is already in a guild
     * @throws IllegalArgumentException if a guild with that name already exists
     */
    public Guild createGuild(String name, UUID leader) {
        Objects.requireNonNull(name, "name");
        Objects.requireNonNull(leader, "leader");
        String key = name.toLowerCase();
        if (guildByName.containsKey(key)) {
            throw new IllegalArgumentException("A guild named '" + name + "' already exists.");
        }
        if (guildByMember.containsKey(leader)) {
            throw new IllegalStateException("Player is already in a guild.");
        }
        Guild guild = new Guild(name, leader, new HashSet<>());
        guildByName.put(key, guild);
        guildByMember.put(leader, guild);
        return guild;
    }

    /**
     * Disbands the guild that {@code leader} leads, removing all members.
     *
     * @param leader the UUID of the guild leader
     * @throws IllegalStateException if the player is not in a guild or is not the leader
     */
    public void disbandGuild(UUID leader) {
        Objects.requireNonNull(leader, "leader");
        Guild guild = guildByMember.get(leader);
        if (guild == null) {
            throw new IllegalStateException("Player is not in a guild.");
        }
        if (!guild.leader().equals(leader)) {
            throw new IllegalStateException("Only the guild leader can disband the guild.");
        }
        guildByName.remove(guild.name().toLowerCase());
        guildByMember.remove(leader);
        for (UUID member : guild.members()) {
            guildByMember.remove(member);
        }
        guild.members().clear();
    }

    // -------------------------------------------------------------------------
    // Invite lifecycle
    // -------------------------------------------------------------------------

    /**
     * Records a guild invite for {@code invitee} from the guild led by {@code leader}.
     *
     * @param leader  the UUID of the inviting leader
     * @param invitee the UUID of the player being invited
     * @throws IllegalStateException    if {@code leader} has no guild or is not its leader,
     *                                   or if {@code invitee} is already in a guild
     */
    public void sendInvite(UUID leader, UUID invitee) {
        Objects.requireNonNull(leader, "leader");
        Objects.requireNonNull(invitee, "invitee");
        Guild guild = guildByMember.get(leader);
        if (guild == null) {
            throw new IllegalStateException("Player is not in a guild.");
        }
        if (!guild.leader().equals(leader)) {
            throw new IllegalStateException("Only the guild leader can send invites.");
        }
        if (guildByMember.containsKey(invitee)) {
            throw new IllegalStateException("That player is already in a guild.");
        }
        pendingInvites.put(invitee, guild.name().toLowerCase());
    }

    /**
     * Returns {@code true} if {@code invitee} has a pending invite to the guild led by {@code leader}.
     */
    public boolean hasInvite(UUID leader, UUID invitee) {
        Objects.requireNonNull(leader, "leader");
        Objects.requireNonNull(invitee, "invitee");
        Guild guild = guildByMember.get(leader);
        if (guild == null) return false;
        return guild.name().toLowerCase().equals(pendingInvites.get(invitee));
    }

    /**
     * Accepts a pending invite and adds {@code invitee} to the guild.
     *
     * @param invitee the UUID of the accepting player
     * @return the {@link Guild} that the player joined
     * @throws IllegalStateException if the player has no pending invite or the guild no longer exists
     */
    public Guild acceptInvite(UUID invitee) {
        Objects.requireNonNull(invitee, "invitee");
        String guildKey = pendingInvites.remove(invitee);
        if (guildKey == null) {
            throw new IllegalStateException("No pending guild invite.");
        }
        Guild guild = guildByName.get(guildKey);
        if (guild == null) {
            throw new IllegalStateException("The guild no longer exists.");
        }
        if (guildByMember.containsKey(invitee)) {
            throw new IllegalStateException("Player is already in a guild.");
        }
        guild.members().add(invitee);
        guildByMember.put(invitee, guild);
        return guild;
    }

    /**
     * Declines and removes a pending invite for {@code invitee}.
     *
     * @param invitee the UUID of the declining player
     * @return {@code true} if the player had a pending invite, {@code false} otherwise
     */
    public boolean declineInvite(UUID invitee) {
        Objects.requireNonNull(invitee, "invitee");
        return pendingInvites.remove(invitee) != null;
    }

    // -------------------------------------------------------------------------
    // Member operations
    // -------------------------------------------------------------------------

    /**
     * Removes {@code player} from their guild. If they are the leader, the guild is disbanded.
     *
     * @param player the UUID of the leaving player
     * @throws IllegalStateException if the player is not in a guild
     */
    public void leaveGuild(UUID player) {
        Objects.requireNonNull(player, "player");
        Guild guild = guildByMember.get(player);
        if (guild == null) {
            throw new IllegalStateException("Player is not in a guild.");
        }
        if (guild.leader().equals(player)) {
            disbandGuild(player);
        } else {
            guild.members().remove(player);
            guildByMember.remove(player);
        }
    }

    /**
     * Kicks {@code target} from the guild led by {@code leader}.
     *
     * @param leader the UUID of the kicking leader
     * @param target the UUID of the player to kick
     * @throws IllegalStateException if {@code leader} is not a guild leader, {@code target} is not
     *                                in the same guild, or {@code target} is the leader themselves
     */
    public void kickMember(UUID leader, UUID target) {
        Objects.requireNonNull(leader, "leader");
        Objects.requireNonNull(target, "target");
        Guild guild = guildByMember.get(leader);
        if (guild == null || !guild.leader().equals(leader)) {
            throw new IllegalStateException("Only the guild leader can kick members.");
        }
        if (guild.leader().equals(target)) {
            throw new IllegalStateException("Cannot kick the guild leader.");
        }
        if (!guild.members().contains(target)) {
            throw new IllegalStateException("That player is not in your guild.");
        }
        guild.members().remove(target);
        guildByMember.remove(target);
    }

    // -------------------------------------------------------------------------
    // Lookup
    // -------------------------------------------------------------------------

    /**
     * Returns the guild the player belongs to, or {@code null} if they are not in one.
     *
     * @param playerId the player to look up
     * @return the player's {@link Guild}, or {@code null}
     */
    public Guild getGuild(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        return guildByMember.get(playerId);
    }

    /**
     * Returns the guild with the given name (case-insensitive), or {@code null} if none.
     *
     * @param name the guild name to look up
     * @return the matching {@link Guild}, or {@code null}
     */
    public Guild getGuildByName(String name) {
        Objects.requireNonNull(name, "name");
        return guildByName.get(name.toLowerCase());
    }

    /**
     * Returns {@code true} if the player is currently in a guild.
     *
     * @param playerId the player to check
     * @return {@code true} if the player belongs to a guild
     */
    public boolean inGuild(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        return guildByMember.containsKey(playerId);
    }

    // -------------------------------------------------------------------------
    // Inner record
    // -------------------------------------------------------------------------

    /**
     * Holds the mutable state of one guild.
     *
     * <p>The {@code members} set contains non-leader members only; the leader is stored separately.</p>
     */
    public record Guild(String name, UUID leader, Set<UUID> members) {

        /** Returns an unmodifiable view of the full member set (leader + non-leader members). */
        public Set<UUID> getAllMembers() {
            Set<UUID> all = new HashSet<>(members);
            all.add(leader);
            return Collections.unmodifiableSet(all);
        }
    }
}
