package com.skyblock.core.manager;

import com.skyblock.core.menu.BestiaryMenu;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Handles the {@code /bestiary} command.
 *
 * <p>Subcommands:
 * <ul>
 *   <li>{@code /bestiary}            — show all recorded kill counts</li>
 *   <li>{@code /bestiary view <mob>} — show kill count for a specific mob</li>
 *   <li>{@code /bestiary reset}      — reset all kill counts</li>
 * </ul>
 * </p>
 */
public final class BestiaryCommand implements TabExecutor {

    private static final List<String> SUBCOMMANDS = Arrays.asList("list", "view", "reset", "families", "categories");

    private final BestiaryManager bestiaryManager;

    public BestiaryCommand(BestiaryManager bestiaryManager) {
        this.bestiaryManager = bestiaryManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("This command can only be used by players.");
            return true;
        }

        if (args.length == 0) {
            new BestiaryMenu(player).open(player);
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "list"       -> handleList(player);
            case "view"       -> handleView(player, args);
            case "reset"      -> handleReset(player);
            case "families"   -> handleFamilies(player);
            case "categories" -> handleCategories(player);
            default           -> sendHelp(player);
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            String prefix = args[0].toLowerCase();
            return SUBCOMMANDS.stream()
                    .filter(s -> s.startsWith(prefix))
                    .collect(Collectors.toList());
        }
        if (args.length == 2 && sender instanceof Player player && "view".equals(args[0].toLowerCase())) {
            String prefix = args[1].toLowerCase();
            List<String> mobKeys = Arrays.stream(BestiaryManager.BestiaryMob.values())
                    .map(m -> m.mobKey)
                    .collect(Collectors.toList());
            bestiaryManager.getAllKills(player.getUniqueId()).keySet().stream()
                    .filter(k -> !mobKeys.contains(k))
                    .forEach(mobKeys::add);
            return mobKeys.stream()
                    .filter(m -> m.startsWith(prefix))
                    .sorted()
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    private void handleList(Player player) {
        UUID id = player.getUniqueId();
        Map<String, Integer> all = bestiaryManager.getAllKills(id);
        if (all.isEmpty()) {
            player.sendMessage("Your bestiary is empty. Go kill some mobs!");
            return;
        }
        player.sendMessage("=== Your Bestiary (" + all.size() + " mob types) ===");
        all.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .forEach(e -> player.sendMessage("  " + e.getKey() + ": " + e.getValue()));
    }

    private void handleView(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage("Usage: /bestiary view <mob>");
            return;
        }
        String mob = args[1];
        int count = bestiaryManager.getKills(player.getUniqueId(), mob);
        player.sendMessage(mob + " kills: " + count);
    }

    private void handleReset(Player player) {
        bestiaryManager.resetKills(player.getUniqueId());
        player.sendMessage("Your bestiary has been reset.");
    }

    private void handleFamilies(Player player) {
        UUID id = player.getUniqueId();
        player.sendMessage("=== Bestiary Families ===");
        for (BestiaryManager.BestiaryFamily family : BestiaryManager.BestiaryFamily.values()) {
            int total = bestiaryManager.getKillsForFamily(id, family);
            player.sendMessage("  " + family.getDisplayName() + ": " + total + " kills");
        }
    }

    private void handleCategories(Player player) {
        UUID id = player.getUniqueId();
        player.sendMessage("=== Bestiary Categories ===");
        for (BestiaryManager.BestiaryCategory category : BestiaryManager.BestiaryCategory.values()) {
            int total = bestiaryManager.getKillsForCategory(id, category);
            player.sendMessage("  " + category.displayName + ": " + total + " kills");
        }
    }

    private void sendHelp(Player player) {
        player.sendMessage("=== Bestiary Commands ===");
        player.sendMessage("/bestiary              — open the bestiary menu");
        player.sendMessage("/bestiary list         — show all kill counts in chat");
        player.sendMessage("/bestiary view <mob>   — show kills for a specific mob");
        player.sendMessage("/bestiary families     — show kills grouped by family");
        player.sendMessage("/bestiary categories   — show kills grouped by category");
        player.sendMessage("/bestiary reset        — reset all kill counts");
    }
}
