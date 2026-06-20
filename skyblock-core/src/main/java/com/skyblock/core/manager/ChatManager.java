package com.skyblock.core.manager;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

/**
 * Singleton managing player chat ranks, message formatting, and chat channels.
 *
 * <p>Players default to {@link RankType#DEFAULT} when no rank has been assigned
 * and to {@link Channel#GLOBAL} when no channel is set.</p>
 */
public final class ChatManager {

    /** Available chat channels. */
    public enum Channel {
        GLOBAL, PARTY, ISLAND
    }

    public enum RankType {
        DEFAULT("§7", ""),
        VIP("§a", "[VIP] "),
        VIP_PLUS("§a", "[VIP+] "),
        MVP("§b", "[MVP] "),
        MVP_PLUS("§b", "[MVP+] "),
        YOUTUBER("§c", "[YOUTUBE] "),
        HELPER("§5", "[HELPER] "),
        MOD("§2", "[MOD] "),
        ADMIN("§c", "[ADMIN] ");

        private final String colorCode;
        private final String prefix;

        RankType(String colorCode, String prefix) {
            this.colorCode = colorCode;
            this.prefix = prefix;
        }

        public String getColorCode() {
            return colorCode;
        }

        public String getPrefix() {
            return prefix;
        }
    }

    private static final ChatManager INSTANCE = new ChatManager();

    /** Per-player rank. Absent entries imply DEFAULT. */
    private final Map<UUID, RankType> ranks = new HashMap<>();

    /** Per-player active channel. Absent entries imply GLOBAL. */
    private final Map<UUID, Channel> channels = new HashMap<>();

    private ChatManager() {}

    public static ChatManager getInstance() {
        return INSTANCE;
    }

    /**
     * Sets the rank for the given player.
     *
     * @param playerId the player's UUID
     * @param rank     the rank to assign
     */
    public void setRank(UUID playerId, RankType rank) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(rank, "rank");
        ranks.put(playerId, rank);
    }

    /**
     * Returns the rank for the given player, defaulting to {@link RankType#DEFAULT}.
     *
     * @param playerId the player's UUID
     * @return the player's rank
     */
    public RankType getRank(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        return ranks.getOrDefault(playerId, RankType.DEFAULT);
    }

    /**
     * Formats a chat message with the player's rank prefix and color.
     *
     * @param playerId   the player's UUID
     * @param playerName the player's display name
     * @param message    the raw message text
     * @return the formatted chat string
     */
    public String formatMessage(UUID playerId, String playerName, String message) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(playerName, "playerName");
        Objects.requireNonNull(message, "message");
        RankType rank = getRank(playerId);
        return rank.getColorCode() + rank.getPrefix() + playerName + "§7: §f" + message;
    }

    /**
     * Removes the stored rank for the player (e.g. on disconnect).
     *
     * @param playerId the player's UUID
     */
    public void removePlayer(UUID playerId) {
        ranks.remove(playerId);
        channels.remove(playerId);
    }

    /** Removes all stored ranks and channel assignments. */
    public void clear() {
        ranks.clear();
        channels.clear();
    }

    // -------------------------------------------------------------------------
    // Channel management
    // -------------------------------------------------------------------------

    /**
     * Sets the active chat channel for the given player.
     *
     * @param playerId the player's UUID
     * @param channel  the channel to activate
     */
    public void setChannel(UUID playerId, Channel channel) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(channel, "channel");
        if (channel == Channel.GLOBAL) {
            channels.remove(playerId);
        } else {
            channels.put(playerId, channel);
        }
    }

    /**
     * Returns the active channel for the given player, defaulting to {@link Channel#GLOBAL}.
     *
     * @param playerId the player's UUID
     * @return the player's current channel
     */
    public Channel getChannel(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        return channels.getOrDefault(playerId, Channel.GLOBAL);
    }

    // -------------------------------------------------------------------------
    // Channel message dispatch
    // -------------------------------------------------------------------------

    /**
     * Sends a party-chat message from {@code sender} to every online party member.
     * Returns {@code false} if the sender is not in a party.
     *
     * @param sender        the sending player
     * @param message       the raw message text
     * @param partyManager  the PartyManager instance
     * @return {@code true} if the message was delivered, {@code false} if no party was found
     */
    public boolean sendPartyMessage(Player sender, String message, PartyManager partyManager) {
        Objects.requireNonNull(sender, "sender");
        Objects.requireNonNull(message, "message");
        Objects.requireNonNull(partyManager, "partyManager");

        PartyManager.Party party = partyManager.getParty(sender.getUniqueId());
        if (party == null) {
            return false;
        }
        String formatted = "§9Party §8> " + formatMessage(sender.getUniqueId(), sender.getName(), message);
        for (UUID memberId : party.getAllMembers()) {
            Player member = Bukkit.getPlayer(memberId);
            if (member != null) {
                member.sendMessage(formatted);
            }
        }
        return true;
    }

    /**
     * Sends an island-chat message from {@code sender} to every online island member (owner + coop).
     * Returns {@code false} if the sender has no island membership.
     *
     * @param sender         the sending player
     * @param message        the raw message text
     * @param islandManager  the IslandManager instance
     * @return {@code true} if the message was delivered, {@code false} if no island was found
     */
    public boolean sendIslandMessage(Player sender, String message, IslandManager islandManager) {
        Objects.requireNonNull(sender, "sender");
        Objects.requireNonNull(message, "message");
        Objects.requireNonNull(islandManager, "islandManager");

        Optional<IslandManager.SkyBlockIsland> islandOpt =
                islandManager.getIslandByMember(sender.getUniqueId());
        if (!islandOpt.isPresent()) {
            islandOpt = islandManager.getIsland(sender.getUniqueId());
        }
        if (!islandOpt.isPresent()) {
            return false;
        }
        IslandManager.SkyBlockIsland island = islandOpt.get();
        String formatted = "§aIsland §8> " + formatMessage(sender.getUniqueId(), sender.getName(), message);

        Player owner = Bukkit.getPlayer(island.getOwner());
        if (owner != null) {
            owner.sendMessage(formatted);
        }
        for (UUID memberId : island.getMembers()) {
            if (memberId.equals(island.getOwner())) continue;
            Player member = Bukkit.getPlayer(memberId);
            if (member != null) {
                member.sendMessage(formatted);
            }
        }
        return true;
    }
}
