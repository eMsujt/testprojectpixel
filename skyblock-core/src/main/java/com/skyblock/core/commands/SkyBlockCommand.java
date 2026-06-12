package com.skyblock.core.commands;

import com.skyblock.core.PlayerDataManager;
import com.skyblock.core.PlayerDataManager.PlayerData;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.Objects;

/**
 * Handles the {@code /skyblock} command and dispatches subcommands.
 *
 * <p>Supported subcommands:
 * <ul>
 *   <li>{@code /skyblock help} — lists available subcommands</li>
 *   <li>{@code /skyblock balance} — shows the sender's coin balance</li>
 *   <li>{@code /skyblock skills} — shows the sender's skill levels</li>
 * </ul>
 * </p>
 */
public final class SkyBlockCommand implements CommandExecutor {

    private final PlayerDataManager playerDataManager;

    /**
     * Creates a new {@code SkyBlockCommand} backed by the given player data manager.
     *
     * @param playerDataManager the manager used to look up player data, must not be null
     */
    public SkyBlockCommand(PlayerDataManager playerDataManager) {
        this.playerDataManager = Objects.requireNonNull(playerDataManager, "playerDataManager");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("This command can only be used by players.");
            return true;
        }

        String sub = args.length > 0 ? args[0].toLowerCase() : "help";

        switch (sub) {
            case "help" -> sendHelp(player, label);
            case "balance", "coins" -> sendBalance(player);
            case "skills" -> sendSkills(player);
            default -> {
                player.sendMessage("Unknown subcommand. Use /" + label + " help for a list of commands.");
            }
        }

        return true;
    }

    private void sendHelp(Player player, String label) {
        player.sendMessage("=== SkyBlock Commands ===");
        player.sendMessage("/" + label + " help    - Show this help message");
        player.sendMessage("/" + label + " balance - Show your coin balance");
        player.sendMessage("/" + label + " skills  - Show your skill levels");
    }

    private void sendBalance(Player player) {
        PlayerData data = playerDataManager.getOrCreate(player.getUniqueId());
        player.sendMessage("Coins: " + data.getCoins());
    }

    private void sendSkills(Player player) {
        PlayerData data = playerDataManager.getOrCreate(player.getUniqueId());
        Map<String, Integer> levels = data.getSkillLevels();
        if (levels.isEmpty()) {
            player.sendMessage("You have no skill levels yet.");
            return;
        }
        player.sendMessage("=== Skill Levels ===");
        levels.forEach((skill, level) -> player.sendMessage(skill + ": " + level));
    }
}
