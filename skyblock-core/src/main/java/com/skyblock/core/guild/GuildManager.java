package com.skyblock.core.guild;

import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
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

    /** Per-guild XP, keyed by lower-cased guild name. */
    private final Map<String, Long> guildXp = new HashMap<>();

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
        Map<UUID, GuildRank> ranks = new HashMap<>();
        ranks.put(leader, GuildRank.GUILD_MASTER);
        Guild guild = new Guild(name, leader, new HashSet<>(), ranks);
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
        String key = guild.name().toLowerCase();
        guildByName.remove(key);
        guildXp.remove(key);
        guildByMember.remove(leader);
        for (UUID member : guild.members()) {
            guildByMember.remove(member);
        }
        guild.members().clear();
        guild.memberRanks().clear();
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
        guild.memberRanks().put(invitee, GuildRank.RECRUIT);
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
            guild.memberRanks().remove(player);
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
        guild.memberRanks().remove(target);
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
    // Rank operations
    // -------------------------------------------------------------------------

    /**
     * Returns the {@link GuildRank} of {@code player}, or {@code null} if they are not in a guild.
     *
     * @param player the player to query
     * @return their rank, or {@code null}
     */
    public GuildRank getRank(UUID player) {
        Objects.requireNonNull(player, "player");
        Guild guild = guildByMember.get(player);
        if (guild == null) return null;
        return guild.memberRanks().get(player);
    }

    /**
     * Sets the rank of {@code target} within the guild led by {@code leader}.
     *
     * <p>The leader's own rank ({@code GUILD_MASTER}) cannot be changed via this method.</p>
     *
     * @param leader the UUID of the guild leader (must be GUILD_MASTER)
     * @param target the UUID of the member whose rank is being set
     * @param rank   the new rank; must not be {@code GUILD_MASTER}
     * @throws IllegalStateException    if {@code leader} is not a guild leader or {@code target} is not in the same guild
     * @throws IllegalArgumentException if {@code rank} is {@code GUILD_MASTER}
     */
    public void setRank(UUID leader, UUID target, GuildRank rank) {
        Objects.requireNonNull(leader, "leader");
        Objects.requireNonNull(target, "target");
        Objects.requireNonNull(rank, "rank");
        Guild guild = guildByMember.get(leader);
        if (guild == null || !guild.leader().equals(leader)) {
            throw new IllegalStateException("Only the guild leader can change ranks.");
        }
        if (guild.leader().equals(target)) {
            throw new IllegalStateException("Cannot change the guild leader's rank.");
        }
        if (!guild.members().contains(target)) {
            throw new IllegalStateException("That player is not in your guild.");
        }
        if (rank == GuildRank.GUILD_MASTER) {
            throw new IllegalArgumentException("Cannot assign GUILD_MASTER rank; use a leadership-transfer operation.");
        }
        guild.memberRanks().put(target, rank);
    }

    // -------------------------------------------------------------------------
    // XP operations
    // -------------------------------------------------------------------------

    /**
     * Returns the XP of the guild with the given name, or 0 if not found.
     *
     * @param guildName the guild name (case-insensitive)
     * @return the guild's XP total
     */
    public long getXp(String guildName) {
        Objects.requireNonNull(guildName, "guildName");
        return guildXp.getOrDefault(guildName.toLowerCase(), 0L);
    }

    /**
     * Adds {@code amount} XP to the named guild.
     *
     * @param guildName the guild name (case-insensitive)
     * @param amount    the amount of XP to add (must be >= 0)
     * @throws IllegalArgumentException if {@code amount} is negative or the guild does not exist
     */
    public void addXp(String guildName, long amount) {
        Objects.requireNonNull(guildName, "guildName");
        if (amount < 0) throw new IllegalArgumentException("amount must be >= 0, got " + amount);
        String key = guildName.toLowerCase();
        if (!guildByName.containsKey(key)) {
            throw new IllegalArgumentException("No guild named '" + guildName + "'.");
        }
        guildXp.merge(key, amount, Long::sum);
    }

    // -------------------------------------------------------------------------
    // Persistence
    // -------------------------------------------------------------------------

    public void load(File dataFolder) {
        File file = new File(dataFolder, "guild.yml");
        if (!file.exists()) {
            return;
        }
        YamlConfiguration cfg = YamlConfiguration.loadConfiguration(file);
        guildByName.clear();
        guildByMember.clear();
        guildXp.clear();
        for (String key : cfg.getKeys(false)) {
            try {
                String name = cfg.getString(key + ".name", key);
                UUID leader = UUID.fromString(cfg.getString(key + ".leader", ""));
                long xp = cfg.getLong(key + ".xp", 0L);
                Set<UUID> members = new HashSet<>();
                Map<UUID, GuildRank> ranks = new HashMap<>();
                ranks.put(leader, GuildRank.GUILD_MASTER);
                List<?> rawMembers = cfg.getList(key + ".members", Collections.emptyList());
                for (Object o : rawMembers) {
                    try {
                        UUID memberId = UUID.fromString(String.valueOf(o));
                        members.add(memberId);
                        String rankStr = cfg.getString(key + ".ranks." + memberId, GuildRank.RECRUIT.name());
                        GuildRank rank;
                        try {
                            rank = GuildRank.valueOf(rankStr);
                        } catch (IllegalArgumentException ignored) {
                            rank = GuildRank.RECRUIT;
                        }
                        ranks.put(memberId, rank);
                    } catch (IllegalArgumentException ignored) {
                        // skip malformed member UUIDs
                    }
                }
                Guild guild = new Guild(name, leader, members, ranks);
                guildByName.put(key, guild);
                guildByMember.put(leader, guild);
                for (UUID memberId : members) {
                    guildByMember.put(memberId, guild);
                }
                guildXp.put(key, xp);
            } catch (IllegalArgumentException ignored) {
                // skip malformed guild entries
            }
        }
    }

    public void save(File dataFolder) {
        File file = new File(dataFolder, "guild.yml");
        YamlConfiguration cfg = new YamlConfiguration();
        for (Map.Entry<String, Guild> entry : guildByName.entrySet()) {
            String key = entry.getKey();
            Guild guild = entry.getValue();
            cfg.set(key + ".name", guild.name());
            cfg.set(key + ".leader", guild.leader().toString());
            cfg.set(key + ".xp", guildXp.getOrDefault(key, 0L));
            List<String> memberStrings = new ArrayList<>();
            for (UUID memberId : guild.members()) {
                memberStrings.add(memberId.toString());
                GuildRank rank = guild.memberRanks().getOrDefault(memberId, GuildRank.RECRUIT);
                cfg.set(key + ".ranks." + memberId, rank.name());
            }
            cfg.set(key + ".members", memberStrings);
        }
        try {
            cfg.save(file);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save guild.yml", e);
        }
    }

    // -------------------------------------------------------------------------
    // Inner types
    // -------------------------------------------------------------------------

    /** Rank hierarchy for guild members. */
    public enum GuildRank {
        GUILD_MASTER("Guild Master"),
        OFFICER("Officer"),
        MEMBER("Member"),
        RECRUIT("Recruit");

        private final String displayName;

        GuildRank(String displayName) {
            this.displayName = displayName;
        }

        public String displayName() {
            return displayName;
        }
    }

    // -------------------------------------------------------------------------
    // Inner record
    // -------------------------------------------------------------------------

    /**
     * Holds the mutable state of one guild.
     *
     * <p>The {@code members} set contains non-leader members only; the leader is stored separately.
     * {@code memberRanks} maps every member (including the leader) to their {@link GuildRank}.</p>
     */
    public record Guild(String name, UUID leader, Set<UUID> members, Map<UUID, GuildRank> memberRanks) {

        /** Returns an unmodifiable view of the full member set (leader + non-leader members). */
        public Set<UUID> getAllMembers() {
            Set<UUID> all = new HashSet<>(members);
            all.add(leader);
            return Collections.unmodifiableSet(all);
        }
    }
}
