package com.skyblock.plugin.skill;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Singleton action-bar XP feedback service.
 *
 * <p>When a player gains skill XP a caller invokes {@link #queue(Player, String)},
 * which stores a per-UUID pending {@link ActionBarEntry}. A repeating task started
 * via {@link #start(Plugin)} re-sends each pending message to its player every
 * {@link #REFRESH_TICKS} ticks so the feedback stays visible, then drops the entry
 * once it has been shown for {@link #DISPLAY_TICKS} ticks.</p>
 */
public final class SkillActionBar {

    /** Interval between action bar refreshes, in server ticks. */
    private static final long REFRESH_TICKS = 4L;

    /** How long a queued message stays on screen, in server ticks (3 seconds). */
    private static final long DISPLAY_TICKS = 60L;

    private static final SkillActionBar INSTANCE = new SkillActionBar();

    private final Map<UUID, ActionBarEntry> pending = new ConcurrentHashMap<>();

    private SkillActionBar() {}

    public static SkillActionBar getInstance() {
        return INSTANCE;
    }

    /** A pending action-bar message and the ticks left before it expires. */
    private static final class ActionBarEntry {
        private final String message;
        private long ticksRemaining;

        private ActionBarEntry(String message, long ticksRemaining) {
            this.message = message;
            this.ticksRemaining = ticksRemaining;
        }
    }

    /** Queues {@code message} as {@code player}'s pending action-bar feedback. */
    public void queue(Player player, String message) {
        pending.put(player.getUniqueId(), new ActionBarEntry(message, DISPLAY_TICKS));
    }

    /**
     * Starts the repeating flush task.
     *
     * @param plugin the owning plugin used to schedule the task and resolve players
     */
    public void start(Plugin plugin) {
        new BukkitRunnable() {
            @Override
            public void run() {
                flush(plugin);
            }
        }.runTaskTimer(plugin, 0L, REFRESH_TICKS);
    }

    private void flush(Plugin plugin) {
        pending.entrySet().removeIf(e -> {
            Player player = plugin.getServer().getPlayer(e.getKey());
            if (player == null) {
                return true;
            }
            ActionBarEntry entry = e.getValue();
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(entry.message));
            entry.ticksRemaining -= REFRESH_TICKS;
            return entry.ticksRemaining <= 0;
        });
    }
}
