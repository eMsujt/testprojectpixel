package com.skyblock.core.listener;

import com.skyblock.core.chat.ChatChannel;
import com.skyblock.core.chat.ChatManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.UUID;

/**
 * Prefixes outgoing chat with the sender's active {@link ChatChannel} tag.
 *
 * <p>Players keep using the vanilla chat box; this listener simply rewrites the
 * format so messages are labelled with the channel the player has selected via
 * {@code /chat}. GLOBAL messages are left unlabelled.</p>
 */
public final class SkyBlockChatListener implements Listener {

    private static final SkyBlockChatListener INSTANCE = new SkyBlockChatListener();

    private final ChatManager chatManager = ChatManager.getInstance();

    private SkyBlockChatListener() {}

    public static SkyBlockChatListener getInstance() {
        return INSTANCE;
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        if (event.isCancelled() || event.getPlayer() == null) {
            return;
        }
        UUID uuid = event.getPlayer().getUniqueId();
        ChatChannel channel = chatManager.getChannel(uuid);
        if (channel == ChatChannel.GLOBAL) {
            return;
        }
        event.setFormat("[" + channel.displayName() + "] " + event.getFormat());
    }
}
