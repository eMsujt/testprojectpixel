package com.skyblock.core.chocolate;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Handles the {@code /chocolatefactory} command.
 *
 * <p>Subcommands:
 * <ul>
 *   <li>{@code /chocolatefactory balance}              — show your current chocolate balance</li>
 *   <li>{@code /chocolatefactory production}           — show your chocolate-per-second rate</li>
 *   <li>{@code /chocolatefactory rabbits}              — list your rabbit counts by rarity</li>
 *   <li>{@code /chocolatefactory rarities}             — list all rabbit rarities and their CPS bonuses</li>
 *   <li>{@code /chocolatefactory addrabbit <rarity>}   — (op) add a rabbit of the given rarity</li>
 * </ul>
 * </p>
 */
public final class ChocolateFactoryCommand implements TabExecutor {

    private static final List<String> SUBCOMMANDS = Arrays.asList(
            "balance", "production", "rabbits", "rarities", "addrabbit");

    private final ChocolateFactoryManager chocolateFactoryManager;

    public ChocolateFactoryCommand(ChocolateFactoryManager chocolateFactoryManager) {
        this.chocolateFactoryManager = chocolateFactoryManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("This command can only be used by players.");
            return true;
        }

        if (args.length == 0) {
            player.sendMessage("Usage: /chocolatefactory <balance|production|rabbits|rarities|addrabbit>");
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "balance"   -> handleBalance(player);
            case "production"-> handleProduction(player);
            case "rabbits"   -> handleRabbits(player);
            case "rarities"  -> handleRarities(player);
            case "addrabbit" -> handleAddRabbit(player, args);
            default          -> player.sendMessage(
                    "Unknown subcommand. Usage: /chocolatefactory <balance|production|rabbits|rarities|addrabbit>");
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
        if (args.length == 2 && args[0].equalsIgnoreCase("addrabbit")) {
            String prefix = args[1].toUpperCase();
            return Arrays.stream(ChocolateFactoryManager.RabbitRarity.values())
                    .map(Enum::name)
                    .filter(s -> s.startsWith(prefix))
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    private void handleBalance(Player player) {
        long chocolate = chocolateFactoryManager.getChocolate(player.getUniqueId());
        player.sendMessage("Chocolate balance: " + chocolate);
    }

    private void handleProduction(Player player) {
        int rate = chocolateFactoryManager.getProductionRate(player.getUniqueId());
        player.sendMessage("Chocolate production: " + rate + " per second");
    }

    private void handleRabbits(Player player) {
        player.sendMessage("=== Your Rabbits ===");
        boolean any = false;
        for (ChocolateFactoryManager.RabbitRarity rarity : ChocolateFactoryManager.RabbitRarity.values()) {
            int count = chocolateFactoryManager.getRabbitCount(player.getUniqueId(), rarity);
            if (count > 0) {
                player.sendMessage(rarity.getDisplayName() + ": " + count);
                any = true;
            }
        }
        if (!any) {
            player.sendMessage("You have no rabbits yet.");
        }
    }

    private void handleRarities(Player player) {
        player.sendMessage("=== Rabbit Rarities ===");
        for (ChocolateFactoryManager.RabbitRarity rarity : ChocolateFactoryManager.RabbitRarity.values()) {
            player.sendMessage(rarity.getDisplayName() + ": " + rarity.chocolatePerSecond + " chocolate/sec");
        }
    }

    private void handleAddRabbit(Player player, String[] args) {
        if (!player.isOp()) {
            player.sendMessage("You do not have permission to use this subcommand.");
            return;
        }
        if (args.length < 2) {
            player.sendMessage("Usage: /chocolatefactory addrabbit <rarity>");
            return;
        }
        ChocolateFactoryManager.RabbitRarity rarity;
        try {
            rarity = ChocolateFactoryManager.RabbitRarity.valueOf(args[1].toUpperCase());
        } catch (IllegalArgumentException e) {
            player.sendMessage("Unknown rarity: " + args[1]);
            return;
        }
        chocolateFactoryManager.addRabbit(player.getUniqueId(), rarity);
        player.sendMessage("Added a " + rarity.getDisplayName() + " rabbit! Production rate: "
                + chocolateFactoryManager.getProductionRate(player.getUniqueId()) + " chocolate/sec");
    }
}
