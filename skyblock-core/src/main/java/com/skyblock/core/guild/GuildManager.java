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
 * <p>Tracks guilds by name and maps each player UUID to their current guild.
 * Not thread-safe; synchronize externally if needed.</p>
 */
public final class GuildManager {

    /** Maximum number of members a guild may have (including leader). */
    static final int MAX_MEMBERS = 50;

    private static final GuildManager INSTANCE = new GuildManager();

    /** guild name (lower-case) → Guild */
    private final Map<String, Guild> guilds = new HashMap<>();
    /** member UUID → guild name (lower-case) */
    private final Map<UUID, String> playerGuild = new HashMap<>();

    private GuildManager() {
    }

    public static GuildManager getInstance() {
        return INSTANCE;
    }

    // -----------------------------------------------------------------------
    // Guild lifecycle
    // -----------------------------------------------------------------------

    /**
     * Creates a new guild with the given player as leader.
     *
     * @param leader    the creating player, must not be null
     * @param guildName the desired guild name, must not be null or blank
     * @throws IllegalArgumentException if the name is already taken or invalid
     * @throws IllegalStateException    if the player is already in a guild
     */
    public Guild createGuild(UUID leader, String guildName) {
        Objects.requireNonNull(leader, "leader");
        Objects.requireNonNull(guildName, "guildName");
        String name = guildName.trim();
        if (name.isEmpty()) {
            throw new IllegalArgumentException("Guild name must not be blank");
        }
        if (playerGuild.containsKey(leader)) {
            throw new IllegalStateException("Player is already in a guild");
        }
        String key = name.toLowerCase();
        if (guilds.containsKey(key)) {
            throw new IllegalArgumentException("A guild named '" + name + "' already exists");
        }
        Guild guild = new Guild(name, leader);
        guilds.put(key, guild);
        playerGuild.put(leader, key);
        return guild;
    }

    /**
     * Disbands the guild that the given player leads.
     *
     * @param leader the leader requesting disbandment, must not be null
     * @throws IllegalStateException if the player is not in a guild or is not the leader
     */
    public void disbandGuild(UUID leader) {
        Objects.requireNonNull(leader, "leader");
        Guild guild = getGuildOfPlayer(leader);
        if (guild == null) {
            throw new IllegalStateException("Player is not in a guild");
        }
        if (!guild.getLeader().equals(leader)) {
            throw new IllegalStateException("Only the guild leader can disband the guild");
        }
        for (UUID member : guild.getMembers()) {
            playerGuild.remove(member);
        }
        guilds.remove(guild.getName().toLowerCase());
    }

    // -----------------------------------------------------------------------
    // Membership
    // -----------------------------------------------------------------------

    /**
     * Adds a player to a guild as a regular member.
     *
     * @param guildName the target guild name, case-insensitive
     * @param player    the player to add, must not be null
     * @throws IllegalArgumentException if the guild does not exist or the guild is full
     * @throws IllegalStateException    if the player is already in a guild
     */
    public void addMember(String guildName, UUID player) {
        Objects.requireNonNull(guildName, "guildName");
        Objects.requireNonNull(player, "player");
        if (playerGuild.containsKey(player)) {
            throw new IllegalStateException("Player is already in a guild");
        }
        Guild guild = guilds.get(guildName.toLowerCase());
        if (guild == null) {
            throw new IllegalArgumentException("Guild '" + guildName + "' does not exist");
        }
        if (guild.getMembers().size() >= MAX_MEMBERS) {
            throw new IllegalArgumentException("Guild '" + guildName + "' is full");
        }
        guild.members.add(player);
        playerGuild.put(player, guildName.toLowerCase());
    }

    /**
     * Removes a player from their guild.
     *
     * @param player the player to remove, must not be null
     * @throws IllegalStateException if the player is not in a guild or is the leader
     */
    public void removeMember(UUID player) {
        Objects.requireNonNull(player, "player");
        Guild guild = getGuildOfPlayer(player);
        if (guild == null) {
            throw new IllegalStateException("Player is not in a guild");
        }
        if (guild.getLeader().equals(player)) {
            throw new IllegalStateException("Leader must disband the guild, not leave it");
        }
        guild.members.remove(player);
        playerGuild.remove(player);
    }

    // -----------------------------------------------------------------------
    // Queries
    // -----------------------------------------------------------------------

    /**
     * Returns the guild the player belongs to, or {@code null} if none.
     */
    public Guild getGuildOfPlayer(UUID player) {
        Objects.requireNonNull(player, "player");
        String key = playerGuild.get(player);
        return key == null ? null : guilds.get(key);
    }

    /**
     * Returns the guild with the given name (case-insensitive), or {@code null}.
     */
    public Guild getGuild(String name) {
        Objects.requireNonNull(name, "name");
        return guilds.get(name.toLowerCase());
    }

    /** Returns an unmodifiable view of all guilds keyed by lower-case name. */
    public Map<String, Guild> getAllGuilds() {
        return Collections.unmodifiableMap(guilds);
    }

    // -----------------------------------------------------------------------
    // Inner class
    // -----------------------------------------------------------------------

    /** Represents a single player guild. */
    public static final class Guild {

        private final String name;
        private UUID leader;
        private final Set<UUID> members = new HashSet<>();

        private Guild(String name, UUID leader) {
            this.name = name;
            this.leader = leader;
            this.members.add(leader);
        }

        public String getName() {
            return name;
        }

        public UUID getLeader() {
            return leader;
        }

        /** Returns an unmodifiable view of all members (includes the leader). */
        public Set<UUID> getMembers() {
            return Collections.unmodifiableSet(members);
        }

        public int getMemberCount() {
            return members.size();
        }
    }
}
