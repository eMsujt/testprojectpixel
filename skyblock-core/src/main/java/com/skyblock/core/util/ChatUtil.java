package com.skyblock.core.util;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public final class ChatUtil {

    public static final String PREFIX = "§6§lSkyBlock §r§7» ";

    private ChatUtil() {}

    public static String colorize(String message) {
        if (message == null) {
            return "";
        }
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    public static void sendPrefixed(Player player, String... messages) {
        if (player == null || messages == null) {
            return;
        }
        for (String message : messages) {
            if (message == null) {
                continue;
            }
            player.sendMessage(PREFIX + colorize(message));
        }
    }
}
