package com.skyblock.core.listener;

import com.skyblock.core.SkyBlockCore;
import com.skyblock.core.manager.ChatInputManager;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.UUID;

/**
 * Intercepts the chat message of a player who has a pending {@link ChatInputManager}
 * prompt: cancels the message (so it isn't broadcast) and runs the callback on the
 * main thread (chat fires async, but the callback opens menus / touches econ).
 */
public final class ChatInputListener implements Listener {

    private static final ChatInputListener INSTANCE = new ChatInputListener();

    public static ChatInputListener getInstance() {
        return INSTANCE;
    }

    private ChatInputListener() {}

    @EventHandler(priority = EventPriority.LOWEST)
    public void onChat(AsyncPlayerChatEvent event) {
        UUID playerId = event.getPlayer().getUniqueId();
        if (!ChatInputManager.getInstance().hasPending(playerId)) {
            return;
        }
        event.setCancelled(true);
        String message = event.getMessage();
        Bukkit.getScheduler().runTask(SkyBlockCore.getInstance(),
                () -> ChatInputManager.getInstance().consume(playerId, message));
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        ChatInputManager.getInstance().cancel(event.getPlayer().getUniqueId());
    }
}
