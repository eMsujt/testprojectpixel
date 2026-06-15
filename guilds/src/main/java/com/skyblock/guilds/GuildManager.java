package com.skyblock.guilds;

import com.skyblock.core.guild.GuildManager.Guild;
import com.skyblock.core.guild.GuildManager.GuildRank;

import java.util.UUID;

/**
 * Delegation stub — all operations are forwarded to
 * {@link com.skyblock.core.guild.GuildManager}, which is the canonical
 * singleton implementation.
 *
 * @deprecated Use {@link com.skyblock.core.guild.GuildManager} directly.
 */
@Deprecated
public final class GuildManager {

    private static final GuildManager INSTANCE = new GuildManager();

    private final com.skyblock.core.guild.GuildManager delegate =
            com.skyblock.core.guild.GuildManager.getInstance();

    private GuildManager() {}

    public static GuildManager getInstance() {
        return INSTANCE;
    }

    public Guild createGuild(String name, UUID leader) {
        return delegate.createGuild(name, leader);
    }

    public void disbandGuild(UUID leader) {
        delegate.disbandGuild(leader);
    }

    public boolean guildExists(String guildName) {
        return delegate.getGuildByName(guildName) != null;
    }

    public boolean inGuild(UUID playerId) {
        return delegate.inGuild(playerId);
    }

    public Guild getGuild(UUID playerId) {
        return delegate.getGuild(playerId);
    }

    public void sendInvite(UUID leader, UUID invitee) {
        delegate.sendInvite(leader, invitee);
    }

    public Guild acceptInvite(UUID invitee) {
        return delegate.acceptInvite(invitee);
    }

    public boolean declineInvite(UUID invitee) {
        return delegate.declineInvite(invitee);
    }

    public void leaveGuild(UUID player) {
        delegate.leaveGuild(player);
    }

    public void kickMember(UUID leader, UUID target) {
        delegate.kickMember(leader, target);
    }

    public GuildRank getRank(UUID player) {
        return delegate.getRank(player);
    }

    public void setRank(UUID leader, UUID target, GuildRank rank) {
        delegate.setRank(leader, target, rank);
    }

    public long getXp(String guildName) {
        return delegate.getXp(guildName);
    }

    public void addXp(String guildName, long amount) {
        delegate.addXp(guildName, amount);
    }
}
