package com.skyblock.core.command;

import com.skyblock.core.manager.ChocolateFactoryManager;
import com.skyblock.core.manager.ChocolateFactoryManager.Employee;
import com.skyblock.core.menu.ChocolateFactoryMenu;

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
 *   <li>{@code /chocolatefactory}            — open the Chocolate Factory menu</li>
 *   <li>{@code /chocolatefactory balance}    — show your current chocolate balance</li>
 *   <li>{@code /chocolatefactory production} — show your chocolate-per-second rate</li>
 *   <li>{@code /chocolatefactory employees}  — list your rabbit employees and their levels</li>
 *   <li>{@code /chocolatefactory give <n>}   — (op) add {@code n} chocolate to your balance</li>
 * </ul>
 * </p>
 */
public final class ChocolateFactoryCommand implements TabExecutor {

    private static final List<String> SUBCOMMANDS = Arrays.asList(
            "balance", "production", "employees", "give");

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
            new ChocolateFactoryMenu(player).open(player);
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "balance"    -> player.sendMessage("Chocolate balance: "
                    + chocolateFactoryManager.getChocolate(player.getUniqueId()));
            case "production" -> player.sendMessage("Chocolate production: "
                    + chocolateFactoryManager.getProductionRate(player.getUniqueId()) + " per second");
            case "employees"  -> handleEmployees(player);
            case "give"       -> handleGive(player, args);
            default           -> player.sendMessage(
                    "Unknown subcommand. Usage: /chocolatefactory <balance|production|employees|give>");
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

    private void handleEmployees(Player player) {
        player.sendMessage("=== Your Rabbit Employees ===");
        for (Employee emp : Employee.values()) {
            int level = chocolateFactoryManager.getEmployeeLevel(player.getUniqueId(), emp);
            int production = chocolateFactoryManager.getEmployeeProduction(player.getUniqueId(), emp);
            player.sendMessage(emp.getDisplayName() + ": level " + level + " (" + production + "/s)");
        }
    }

    private void handleGive(Player player, String[] args) {
        if (!player.isOp()) {
            player.sendMessage("You do not have permission to use this subcommand.");
            return;
        }
        if (args.length < 2) {
            player.sendMessage("Usage: /chocolatefactory give <amount>");
            return;
        }
        long amount;
        try {
            amount = Long.parseLong(args[1]);
        } catch (NumberFormatException e) {
            player.sendMessage("Invalid amount: " + args[1]);
            return;
        }
        if (amount <= 0) {
            player.sendMessage("Amount must be positive.");
            return;
        }
        chocolateFactoryManager.addChocolate(player.getUniqueId(), amount);
        player.sendMessage("Added " + amount + " chocolate. Balance: "
                + chocolateFactoryManager.getChocolate(player.getUniqueId()));
    }
}
