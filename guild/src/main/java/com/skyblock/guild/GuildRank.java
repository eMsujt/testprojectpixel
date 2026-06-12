package com.skyblock.guild;

/**
 * The ranks a player can hold within a guild, ordered from lowest to
 * highest authority. Ordinal order is meaningful: {@link #compareTo(Enum)}
 * ranks members against each other.
 */
public enum GuildRank {

    MEMBER("Member", "A regular guild member"),
    OFFICER("Officer", "Can invite, kick and promote members"),
    GUILD_MASTER("Guild Master", "Owns the guild and has full control");

    private final String displayName;
    private final String description;

    GuildRank(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    /** Returns the display name of this rank. */
    public String getDisplayName() {
        return displayName;
    }

    /** Returns a short description of this rank's permissions. */
    public String getDescription() {
        return description;
    }

    /**
     * Returns whether this rank has at least the authority of the given
     * rank.
     *
     * @param other the rank to compare against
     * @return {@code true} if this rank is equal to or higher than {@code other}
     */
    public boolean isAtLeast(GuildRank other) {
        return compareTo(other) >= 0;
    }
}
