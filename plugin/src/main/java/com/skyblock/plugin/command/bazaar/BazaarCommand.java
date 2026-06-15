package com.skyblock.plugin.command.bazaar;

import com.skyblock.core.manager.BazaarManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

public final class BazaarCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("This command can only be used by players.");
            return true;
        }

        BazaarManager manager = BazaarManager.getInstance();

        if (args.length == 0) {
            sendHelp(player);
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "info" -> handleInfo(player, args, manager);
            case "buy" -> handleBuy(player, args, manager);
            case "sell" -> handleSell(player, args, manager);
            case "cancel" -> handleCancel(player, args, manager);
            case "orders"  -> handleOrders(player, manager);
            case "history" -> handleHistory(player, manager);
            default        -> sendHelp(player);
        }
        return true;
    }

    private void handleInfo(Player player, String[] args, BazaarManager manager) {
        if (args.length < 2) {
            player.sendMessage("Usage: /bazaar info <item>");
            return;
        }
        String itemId = args[1];
        double lowestAsk = manager.getLowestAsk(itemId);
        double highestBid = manager.getHighestBid(itemId);
        player.sendMessage("=== Bazaar: " + itemId + " ===");
        player.sendMessage("Lowest Ask: " + (lowestAsk == Double.MAX_VALUE ? "none" : lowestAsk + " coins"));
        player.sendMessage("Highest Bid: " + (highestBid == 0 ? "none" : highestBid + " coins"));
        player.sendMessage("Buy Orders: " + manager.getBuyOrders(itemId).size()
                + "  |  Sell Orders: " + manager.getSellOrders(itemId).size());
    }

    private void handleBuy(Player player, String[] args, BazaarManager manager) {
        if (args.length < 4) {
            player.sendMessage("Usage: /bazaar buy <item> <quantity> <priceEach>");
            return;
        }
        String itemId = args[1];
        int quantity;
        double priceEach;
        try {
            quantity = Integer.parseInt(args[2]);
            priceEach = Double.parseDouble(args[3]);
        } catch (NumberFormatException e) {
            player.sendMessage("Invalid quantity or price.");
            return;
        }
        try {
            UUID orderId = manager.addBuyOrder(player.getUniqueId(), itemId, quantity, priceEach);
            player.sendMessage("Buy order placed for " + quantity + "x " + itemId + " at " + priceEach + " coins each.");
            player.sendMessage("Order ID: " + orderId);
        } catch (IllegalArgumentException e) {
            player.sendMessage(e.getMessage());
        }
    }

    private void handleSell(Player player, String[] args, BazaarManager manager) {
        if (args.length < 4) {
            player.sendMessage("Usage: /bazaar sell <item> <quantity> <priceEach>");
            return;
        }
        String itemId = args[1];
        int quantity;
        double priceEach;
        try {
            quantity = Integer.parseInt(args[2]);
            priceEach = Double.parseDouble(args[3]);
        } catch (NumberFormatException e) {
            player.sendMessage("Invalid quantity or price.");
            return;
        }
        try {
            UUID orderId = manager.addSellOrder(player.getUniqueId(), itemId, quantity, priceEach);
            player.sendMessage("Sell order placed for " + quantity + "x " + itemId + " at " + priceEach + " coins each.");
            player.sendMessage("Order ID: " + orderId);
        } catch (IllegalArgumentException e) {
            player.sendMessage(e.getMessage());
        }
    }

    private void handleCancel(Player player, String[] args, BazaarManager manager) {
        if (args.length < 3) {
            player.sendMessage("Usage: /bazaar cancel <buy|sell> <orderId>");
            return;
        }
        String type = args[1].toLowerCase();
        UUID orderId;
        try {
            orderId = UUID.fromString(args[2]);
        } catch (IllegalArgumentException e) {
            player.sendMessage("Invalid order ID.");
            return;
        }
        try {
            if (type.equals("buy")) {
                manager.cancelBuyOrder(orderId);
                player.sendMessage("Buy order " + orderId + " cancelled.");
            } else if (type.equals("sell")) {
                manager.cancelSellOrder(orderId);
                player.sendMessage("Sell order " + orderId + " cancelled.");
            } else {
                player.sendMessage("Usage: /bazaar cancel <buy|sell> <orderId>");
            }
        } catch (IllegalArgumentException e) {
            player.sendMessage(e.getMessage());
        }
    }

    private void handleOrders(Player player, BazaarManager manager) {
        List<BazaarManager.BazaarOrder> orders = manager.getOrdersForPlayer(player.getUniqueId());
        if (orders.isEmpty()) {
            player.sendMessage("You have no active bazaar orders.");
            return;
        }
        player.sendMessage("=== Your Bazaar Orders ===");
        for (BazaarManager.BazaarOrder order : orders) {
            player.sendMessage(order.type().getDisplayName() + " | " + order.itemId()
                    + " x" + order.quantity() + " @ " + order.priceEach() + " coins | ID: " + order.id());
        }
    }

    private void handleHistory(Player player, BazaarManager manager) {
        List<String> history = manager.getBazaarHistory(player.getUniqueId());
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
        player.sendMessage("/bazaar info <item> — show best bid/ask");
        player.sendMessage("/bazaar buy <item> <qty> <price> — place a buy order");
        player.sendMessage("/bazaar sell <item> <qty> <price> — place a sell order");
        player.sendMessage("/bazaar cancel buy <orderId> — cancel a buy order");
        player.sendMessage("/bazaar cancel sell <orderId> — cancel a sell order");
        player.sendMessage("/bazaar orders — list your active orders");
        player.sendMessage("/bazaar history — show your bazaar history");
    }
}
