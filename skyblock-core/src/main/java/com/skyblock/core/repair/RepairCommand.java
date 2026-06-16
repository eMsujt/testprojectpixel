package com.skyblock.core.repair;

import com.skyblock.core.manager.RepairManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Handles the {@code /repair} command.
 *
 * <p>Subcommands:
 * <ul>
 *   <li>{@code /repair hand} — repair the item in the player's main hand</li>
 *   <li>{@code /repair all}  — repair every repairable item in the player's inventory</li>
 * </ul>
 * </p>
 */
public final class RepairCommand implements TabExecutor {

    private final RepairManager repairManager;

    public RepairCommand(RepairManager repairManager) {
        if (repairManager == null) {
            throw new IllegalArgumentException("repairManager must not be null");
        }
        this.repairManager = repairManager;
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
            case "hand" -> handleHand(player);
            case "all"  -> handleAll(player);
            default     -> sendHelp(player);
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            String lower = args[0].toLowerCase();
            return Arrays.asList("hand", "all").stream()
                    .filter(s -> s.startsWith(lower))
                    .toList();
        }
        return Collections.emptyList();
    }

    private void handleHand(Player player) {
        ItemStack item = player.getInventory().getItemInMainHand();
        int cost = repairManager.getRepairCost(item);
        if (cost < 0) {
            player.sendMessage("That item cannot be repaired.");
            return;
        }
        if (cost == 0) {
            player.sendMessage("That item is already fully repaired.");
            return;
        }
        if (!repairManager.hasCoins(player, cost)) {
            player.sendMessage("You need " + cost + " coins to repair that item.");
            return;
        }
        repairManager.deductCoins(player, cost);
        repairManager.repair(item);
        player.sendMessage("Item repaired for " + cost + " coins.");
    }

    private void handleAll(Player player) {
        ItemStack[] contents = player.getInventory().getContents();
        int totalCost = 0;
        for (ItemStack item : contents) {
            int cost = repairManager.getRepairCost(item);
            if (cost > 0) {
                totalCost += cost;
            }
        }
        if (totalCost == 0) {
            player.sendMessage("All your items are already fully repaired or cannot be repaired.");
            return;
        }
        if (!repairManager.hasCoins(player, totalCost)) {
            player.sendMessage("You need " + totalCost + " coins to repair all your items.");
            return;
        }
        repairManager.deductCoins(player, totalCost);
        int repaired = 0;
        for (ItemStack item : contents) {
            if (repairManager.repair(item)) {
                repaired++;
            }
        }
        player.sendMessage("Repaired " + repaired + " item(s) for " + totalCost + " coins.");
    }

    private void sendHelp(Player player) {
        player.sendMessage("=== Repair Commands ===");
        player.sendMessage("/repair hand — repair the item in your hand");
        player.sendMessage("/repair all  — repair all items in your inventory");
    }
}
