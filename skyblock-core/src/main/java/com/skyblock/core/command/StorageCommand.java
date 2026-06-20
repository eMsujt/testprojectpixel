package com.skyblock.core.command;

import com.skyblock.core.manager.StorageManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Handles the {@code /storage} command.
 *
 * <p>Subcommands:
 * <ul>
 *   <li>{@code /storage info}    — show storage summary</li>
 *   <li>{@code /storage upgrade} — unlock an additional storage page</li>
 *   <li>{@code /storage reset}   — reset storage data to default</li>
 * </ul>
 * </p>
 */
public final class StorageCommand implements TabExecutor {

    private static final List<String> SUBCOMMANDS = Arrays.asList("info", "upgrade", "reset");

    private final StorageManager storageManager;

    public StorageCommand(StorageManager storageManager) {
        this.storageManager = storageManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("This command can only be used by players.");
            return true;
        }

        if (args.length == 0) {
            sendHelp(player);
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "info"    -> handleInfo(player);
            case "upgrade" -> handleUpgrade(player);
            case "reset"   -> handleReset(player);
            default        -> sendHelp(player);
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
        return Collections.emptyList();
    }

    private void handleInfo(Player player) {
        player.sendMessage("=== Your Storage ===");
        player.sendMessage(storageManager.getSummary(player.getUniqueId()));
    }

    private void handleUpgrade(Player player) {
        boolean upgraded = storageManager.unlockPage(player.getUniqueId());
        if (upgraded) {
            int pages = storageManager.getUnlockedPages(player.getUniqueId());
            player.sendMessage("Storage upgraded! You now have " + pages + " page(s).");
        } else {
            player.sendMessage("Your storage is already at the maximum of "
                    + com.skyblock.core.storage.StorageManager.MAX_PAGES + " pages.");
        }
    }

    private void handleReset(Player player) {
        java.util.UUID playerId = player.getUniqueId();
        storageManager.pages().resetStorage(playerId);
        storageManager.resetItems(playerId);
        player.sendMessage("Your storage has been reset to default.");
    }

    private void sendHelp(Player player) {
        player.sendMessage("=== Storage Commands ===");
        player.sendMessage("/storage info    — view your storage summary");
        player.sendMessage("/storage upgrade — unlock an additional page");
        player.sendMessage("/storage reset   — reset storage to default");
    }
}
