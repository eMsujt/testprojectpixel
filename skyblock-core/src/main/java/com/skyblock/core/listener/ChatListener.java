package com.skyblock.core.listener;

import com.skyblock.core.manager.ChatManager;
import com.skyblock.core.manager.SkyblockLevelManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.UUID;

public final class ChatListener implements Listener {

    private static final ChatListener INSTANCE = new ChatListener();

    private final SkyblockLevelManager levelManager = SkyblockLevelManager.getInstance();
    private final ChatManager chatManager = ChatManager.getInstance();

    private ChatListener() {}

    public static ChatListener getInstance() {
        return INSTANCE;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        UUID uuid = event.getPlayer().getUniqueId();
        int level = levelManager.getLevel(uuid);
        ChatManager.RankType rank = chatManager.getRank(uuid);
        event.setFormat("§7[§6" + level + "§7] " + rank.getColorCode() + rank.getPrefix() + "%s§7: §f%s");
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        chatManager.removePlayer(event.getPlayer().getUniqueId());
    }
}
