package com.skyblock.core.listener;

import com.skyblock.core.manager.SkyblockLevelManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public final class ChatListener implements Listener {

    private static final ChatListener INSTANCE = new ChatListener();

    private final SkyblockLevelManager levelManager = SkyblockLevelManager.getInstance();

    private ChatListener() {}

    public static ChatListener getInstance() {
        return INSTANCE;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        int level = levelManager.getLevel(event.getPlayer().getUniqueId());
        event.setFormat("§7[§6" + level + "§7] §f%s§7: §f%s");
    }
}
