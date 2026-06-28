package com.skyblock.core.manager;

import com.skyblock.core.model.Stat;
import com.skyblock.core.scoreboard.SkyBlockScoreboard;
import com.skyblock.core.stats.StatsManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public final class ScoreboardManager {

    private static final ScoreboardManager INSTANCE = new ScoreboardManager();

    private static final long UPDATE_INTERVAL_TICKS = 20L;
    private static final String TITLE = ChatColor.YELLOW + "" + ChatColor.BOLD + "SKYBLOCK";

    private final Map<UUID, SkyBlockScoreboard> boards = new HashMap<>();
    private final Map<UUID, BukkitTask> tasks = new HashMap<>();
    private final java.util.Set<UUID> sidebarDisabled = java.util.concurrent.ConcurrentHashMap.newKeySet();
    private Plugin plugin;

    private ScoreboardManager() {}

    public static ScoreboardManager getInstance() {
        return INSTANCE;
    }

    public void start(Plugin plugin) {
        this.plugin = plugin;
        for (Player player : Bukkit.getOnlinePlayers()) {
            initPlayer(player);
        }
    }

    public void stop() {
        for (BukkitTask task : tasks.values()) {
            task.cancel();
        }
        tasks.clear();
        for (SkyBlockScoreboard board : boards.values()) {
            board.delete();
        }
        boards.clear();
    }

    public void initPlayer(Player player) {
        stopForPlayer(player);
        if (sidebarDisabled.contains(player.getUniqueId())) {
            return; // player has hidden their sidebar in Settings
        }
        SkyBlockScoreboard board = new SkyBlockScoreboard(player, TITLE);
        boards.put(player.getUniqueId(), board);
        BukkitTask task = new BukkitRunnable() {
            @Override
            public void run() {
                if (!player.isOnline()) {
                    cancel();
                    tasks.remove(player.getUniqueId());
                    return;
                }
                updateScoreboard(player);
            }
        }.runTaskTimer(plugin, 0L, UPDATE_INTERVAL_TICKS);
        tasks.put(player.getUniqueId(), task);
    }

    public void stopForPlayer(Player player) {
        BukkitTask existing = tasks.remove(player.getUniqueId());
        if (existing != null) {
            existing.cancel();
        }
        SkyBlockScoreboard board = boards.remove(player.getUniqueId());
        if (board != null) {
            board.delete();
        }
    }

    /** Whether the player currently has the SkyBlock sidebar shown (default true). */
    public boolean isSidebarVisible(UUID playerId) {
        return !sidebarDisabled.contains(playerId);
    }

    /** Shows or hides the sidebar for a player, taking effect immediately. */
    public void setSidebarVisible(Player player, boolean visible) {
        if (visible) {
            sidebarDisabled.remove(player.getUniqueId());
            initPlayer(player);
        } else {
            sidebarDisabled.add(player.getUniqueId());
            stopForPlayer(player);
        }
    }

    public void updateScoreboard(Player player) {
        SkyBlockScoreboard board = boards.get(player.getUniqueId());
        if (board == null) {
            return;
        }

        CalendarManager cal = CalendarManager.getInstance();
        int dom = cal.getCurrentDayOfMonth();
        org.bukkit.World world = player.getWorld();
        double coins = EconomyManager.getInstance().getBalance(player.getUniqueId());

        // 1:1 with the Hypixel SkyBlock sidebar: IRL date + server id, SkyBlock date, time,
        // location, then purse/bits. Health & Defense live on the action bar, not here.
        List<String> lines = Arrays.asList(
            " ",
            ChatColor.GRAY + irlDate() + " " + ChatColor.DARK_GRAY + serverId(world),
            ChatColor.WHITE + cal.getCurrentMonth().getDisplayName() + " " + dom + ordinal(dom),
            ChatColor.GRAY + " " + skyblockTime(world.getTime())
                    + (world.isDayTime() ? " " + ChatColor.YELLOW + "☀" : " " + ChatColor.AQUA + "☽"),
            ChatColor.GRAY + " ⏣ " + ChatColor.GREEN + locationOf(world),
            "  ",
            ChatColor.WHITE + "Purse: " + ChatColor.GOLD + formatCoins(coins),
            ChatColor.WHITE + "Bits: " + ChatColor.AQUA
                    + String.format("%,d", EconomyManager.getInstance().getBits(player.getUniqueId())),
            "   ",
            ChatColor.YELLOW + "www.hypixel.net"
        );
        board.updateLines(lines);
    }

    private static String irlDate() {
        return java.time.format.DateTimeFormatter.ofPattern("MM/dd/yy").format(java.time.LocalDate.now());
    }

    private static String serverId(org.bukkit.World world) {
        return "m" + Integer.toString(Math.abs(world.getName().hashCode()) % 1000, 36).toUpperCase();
    }

    /** Converts the world's tick time to a 12-hour SkyBlock clock string (e.g. {@code 5:40am}). */
    private static String skyblockTime(long ticks) {
        int totalMin = (int) (((ticks / 1000.0 + 6) % 24) * 60);
        int hour = totalMin / 60;
        int min = (totalMin % 60) / 10 * 10;
        String ampm = hour < 12 ? "am" : "pm";
        int h12 = hour % 12;
        if (h12 == 0) h12 = 12;
        return String.format("%d:%02d%s", h12, min, ampm);
    }

    private static String locationOf(org.bukkit.World world) {
        String name = world.getName();
        if (name.startsWith("island_")) {
            return "Your Island";
        }
        // Derive the area from the world name directly (so "Hub" -> Hub, "dwarven_mines" ->
        // Dwarven Mines, etc.) rather than mislabelling the default overworld as the Hub.
        return prettify(name);
    }

    /** Turns a world name into a display label: underscores to spaces, Title Case. */
    private static String prettify(String name) {
        StringBuilder sb = new StringBuilder();
        for (String part : name.replace('_', ' ').split(" ")) {
            if (part.isEmpty()) {
                continue;
            }
            if (sb.length() > 0) {
                sb.append(' ');
            }
            sb.append(Character.toUpperCase(part.charAt(0))).append(part.substring(1).toLowerCase());
        }
        return sb.length() == 0 ? name : sb.toString();
    }

    private static String formatCoins(double coins) {
        if (coins >= 1_000_000) {
            return String.format("%.1fM", coins / 1_000_000);
        }
        if (coins >= 1_000) {
            return String.format("%.1fK", coins / 1_000);
        }
        return String.format("%.0f", coins);
    }

    private static String ordinal(int n) {
        if (n >= 11 && n <= 13) return "th";
        switch (n % 10) {
            case 1: return "st";
            case 2: return "nd";
            case 3: return "rd";
            default: return "th";
        }
    }
}
