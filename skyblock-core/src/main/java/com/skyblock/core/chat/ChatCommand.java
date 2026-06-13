package com.skyblock.core.chat;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Handles the {@code /chat} command.
 *
 * <p>Usage:
 * <ul>
 *   <li>{@code /chat}              — show your current active channel</li>
 *   <li>{@code /chat <channel>}    — switch to GLOBAL, PARTY, GUILD, or TRADE</li>
 * </ul>
 * </p>
 */
public final class ChatCommand implements TabExecutor {

    private static final List<String> CHANNEL_NAMES = Arrays.stream(ChatChannel.values())
            .map(c -> c.name().toLowerCase())
            .collect(Collectors.toList());

    private final ChatManager chatManager;

    public ChatCommand(ChatManager chatManager) {
        this.chatManager = chatManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("This command can only be used by players.");
            return true;
        }

        if (args.length == 0) {
            ChatChannel current = chatManager.getChannel(player.getUniqueId());
            player.sendMessage("Current chat channel: " + current.displayName());
            return true;
        }

        String input = args[0].toUpperCase();
        ChatChannel channel;
        try {
            channel = ChatChannel.valueOf(input);
        } catch (IllegalArgumentException e) {
            player.sendMessage("Unknown channel '" + args[0] + "'. Options: " + CHANNEL_NAMES);
            return true;
        }

        chatManager.setChannel(player.getUniqueId(), channel);
        player.sendMessage("Chat channel set to: " + channel.displayName());
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            String prefix = args[0].toLowerCase();
            return CHANNEL_NAMES.stream()
                    .filter(c -> c.startsWith(prefix))
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }
}
