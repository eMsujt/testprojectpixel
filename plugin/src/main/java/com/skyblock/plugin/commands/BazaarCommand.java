package com.skyblock.plugin.commands;

import com.skyblock.plugin.managers.BazaarManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public final class BazaarCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("This command can only be used by players.");
            return true;
        }

        if (args.length == 0) {
            handleList(player);
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "list"   -> handleList(player);
            case "price"  -> handlePrice(player, args);
            case "buy"    -> handleBuy(player, args);
            case "sell"   -> handleSell(player, args);
            case "orders"  -> handleOrders(player, args);
            case "history" -> handleHistory(player);
            default        -> sendHelp(player);
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
        buyPrices.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .forEach(e -> player.sendMessage(
                        e.getKey() + " — Buy: " + e.getValue()
                        + " | Sell: " + sellPrices.getOrDefault(e.getKey(), 0.0) + " coins"));
        sellPrices.keySet().stream()
                .filter(item -> !buyPrices.containsKey(item))
                .sorted()
                .forEach(item -> player.sendMessage(item + " — Sell: " + manager.getSellPrice(item) + " coins"));
    }

    private void handlePrice(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage("Usage: /bazaar price <item>");
            return;
        }
        String item = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
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

    private void handleBuy(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage("Usage: /bazaar buy <item>");
            return;
        }
        String item = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
        List<BazaarManager.BuyOrder> orders = BazaarManager.getInstance().getBuyOrders(item);
        if (orders.isEmpty()) {
            player.sendMessage("No buy orders for '" + item + "'.");
            return;
        }
        player.sendMessage("=== Buy Orders: " + item + " (" + orders.size() + ") ===");
        orders.stream()
                .sorted((a, b) -> Double.compare(b.pricePerUnit(), a.pricePerUnit()))
                .forEach(o -> player.sendMessage(
                        "[" + o.id().toString().substring(0, 8) + "] x" + o.quantity()
                        + " @ " + o.pricePerUnit() + " coins each"));
    }

    private void handleSell(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage("Usage: /bazaar sell <item>");
            return;
        }
        String item = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
        List<BazaarManager.SellOrder> orders = BazaarManager.getInstance().getSellOrders(item);
        if (orders.isEmpty()) {
            player.sendMessage("No sell orders for '" + item + "'.");
            return;
        }
        player.sendMessage("=== Sell Orders: " + item + " (" + orders.size() + ") ===");
        orders.stream()
                .sorted((a, b) -> Double.compare(a.pricePerUnit(), b.pricePerUnit()))
                .forEach(o -> player.sendMessage(
                        "[" + o.id().toString().substring(0, 8) + "] x" + o.quantity()
                        + " @ " + o.pricePerUnit() + " coins each"));
    }

    private void handleOrders(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage("Usage: /bazaar orders <item>");
            return;
        }
        String item = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
        BazaarManager manager = BazaarManager.getInstance();
        List<BazaarManager.BuyOrder> buyOrders = manager.getBuyOrders(item);
        List<BazaarManager.SellOrder> sellOrders = manager.getSellOrders(item);
        player.sendMessage("=== Orders: " + item + " ===");
        player.sendMessage("Buy orders: " + buyOrders.size() + " | Sell orders: " + sellOrders.size());
        double bestBuy = buyOrders.stream().mapToDouble(BazaarManager.BuyOrder::pricePerUnit).max().orElse(0.0);
        double bestSell = sellOrders.stream().mapToDouble(BazaarManager.SellOrder::pricePerUnit).min().orElse(0.0);
        player.sendMessage("Best buy offer: " + bestBuy + " coins | Best sell offer: " + bestSell + " coins");
    }

    private void handleHistory(Player player) {
        UUID uuid = player.getUniqueId();
        List<String> history = BazaarManager.getInstance().getBazaarHistory(uuid);
        if (history.isEmpty()) {
            player.sendMessage("You have no bazaar history.");
            return;
        }
        player.sendMessage("=== Bazaar History ===");
        for (int i = 0; i < history.size(); i++) {
            player.sendMessage((i + 1) + ". " + history.get(i));
        }
    }

    private void sendHelp(Player player) {
        player.sendMessage("=== Bazaar Commands ===");
        player.sendMessage("/bazaar list           — list all items with prices");
        player.sendMessage("/bazaar price <item>   — show buy/sell price for an item");
        player.sendMessage("/bazaar buy <item>     — show buy orders for an item");
        player.sendMessage("/bazaar sell <item>    — show sell orders for an item");
        player.sendMessage("/bazaar orders <item>  — show order summary for an item");
        player.sendMessage("/bazaar history        — show your bazaar order history");
    }
}
