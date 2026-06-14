package com.skyblock.plugin.commands;

import com.skyblock.plugin.managers.BazaarManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Map;

public final class BazaarCommand implements CommandExecutor {

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
            case "list"  -> handleList(player);
            case "price" -> handlePrice(player, args);
            default      -> sendHelp(player);
        }
        return true;
    }

    private void handleList(Player player) {
        BazaarManager manager = BazaarManager.getInstance();
        Map<String, Double> buyPrices = manager.getBuyPrices();
        Map<String, Double> sellPrices = manager.getSellPrices();
        if (buyPrices.isEmpty() && sellPrices.isEmpty()) {
            player.sendMessage("The bazaar has no items listed.");
            return;
        }
        player.sendMessage("=== Bazaar Items ===");
        for (String item : buyPrices.keySet()) {
            double buy = manager.getBuyPrice(item);
            double sell = manager.getSellPrice(item);
            player.sendMessage(item + " — Buy: " + buy + " | Sell: " + sell + " coins");
        }
        for (String item : sellPrices.keySet()) {
            if (!buyPrices.containsKey(item)) {
                player.sendMessage(item + " — Sell: " + manager.getSellPrice(item) + " coins");
            }
        }
    }

    private void handlePrice(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage("Usage: /bazaar price <item>");
            return;
        }
        String item = String.join(" ", java.util.Arrays.copyOfRange(args, 1, args.length));
        BazaarManager manager = BazaarManager.getInstance();
        double buy = manager.getBuyPrice(item);
        double sell = manager.getSellPrice(item);
        if (buy == 0.0 && sell == 0.0) {
            player.sendMessage("No bazaar prices found for '" + item + "'.");
            return;
        }
        player.sendMessage("=== Bazaar: " + item + " ===");
        if (buy > 0) player.sendMessage("Buy price:  " + buy + " coins");
        if (sell > 0) player.sendMessage("Sell price: " + sell + " coins");
    }

    private void sendHelp(Player player) {
        player.sendMessage("=== Bazaar Commands ===");
        player.sendMessage("/bazaar list          — list all items with prices");
        player.sendMessage("/bazaar price <item>  — show buy/sell price for an item");
    }
}
