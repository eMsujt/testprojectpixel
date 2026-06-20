package com.skyblock.core.util;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public final class ChatUtil {

    public static final String PREFIX = "§6§lSkyBlock §r§7» ";

    private ChatUtil() {}

    public static String colorize(String message) {
        if (message == null) return "";
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    public static void send(CommandSender sender, String message) {
        if (sender == null || message == null) return;
        sender.sendMessage(PREFIX + colorize(message));
    }

    public static void send(CommandSender sender, String... messages) {
        if (sender == null || messages == null) return;
        for (String message : messages) {
            if (message != null) sender.sendMessage(PREFIX + colorize(message));
        }
    }

    public static void sendError(CommandSender sender, String message) {
        if (sender == null || message == null) return;
        sender.sendMessage(PREFIX + "§c" + colorize(message));
    }

    public static void sendSuccess(CommandSender sender, String message) {
        if (sender == null || message == null) return;
        sender.sendMessage(PREFIX + "§a" + colorize(message));
    }

    public static void playerOnly(CommandSender sender) {
        if (sender == null) return;
        sender.sendMessage("§cThis command can only be used by players.");
    }
}
