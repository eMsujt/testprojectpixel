package com.skyblock.core.command;

import com.skyblock.core.bazaar.BazaarManager;
import com.skyblock.core.bazaar.BazaarManager.BuyOrder;
import com.skyblock.core.bazaar.BazaarManager.SellOrder;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * Handles the {@code /bazaar} command.
 *
 * <p>Subcommands:
 * <ul>
 *   <li>{@code /bazaar buy <item> <quantity> <priceEach>}  — place a buy order</li>
 *   <li>{@code /bazaar sell <item> <quantity> <priceEach>} — place a sell order</li>
 *   <li>{@code /bazaar view <item>}                        — list current orders for an item</li>
 *   <li>{@code /bazaar cancel buy|sell <orderId>}          — cancel a standing order</li>
 * </ul>
 * </p>
 */
public final class BazaarCommand implements TabExecutor {

    private final BazaarManager bazaarManager;

    public BazaarCommand(BazaarManager bazaarManager) {
        this.bazaarManager = bazaarManager;
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
            case "buy" -> handleBuy(player, args);
            case "sell" -> handleSell(player, args);
            case "view" -> handleView(player, args);
            case "cancel" -> handleCancel(player, args);
            default -> sendHelp(player);
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            String lower = args[0].toLowerCase();
            return Arrays.asList("buy", "sell", "view", "cancel").stream()
                    .filter(s -> s.startsWith(lower))
                    .toList();
        }
        if (args.length == 2 && args[0].equalsIgnoreCase("cancel")) {
            String lower = args[1].toLowerCase();
            return Arrays.asList("buy", "sell").stream()
                    .filter(s -> s.startsWith(lower))
                    .toList();
        }
        return Collections.emptyList();
    }

    private void handleBuy(Player player, String[] args) {
        if (args.length < 4) {
            player.sendMessage("Usage: /bazaar buy <item> <quantity> <priceEach>");
            return;
        }
        String item = args[1];
        int qty;
        double price;
        try {
            qty = Integer.parseInt(args[2]);
            price = Double.parseDouble(args[3]);
        } catch (NumberFormatException e) {
            player.sendMessage("Invalid quantity or price.");
            return;
        }
        if (qty <= 0 || price <= 0) {
            player.sendMessage("Quantity and price must be positive.");
            return;
        }
        UUID orderId = bazaarManager.addBuyOrder(player.getUniqueId(), item, qty, price);
        player.sendMessage("Buy order placed for " + qty + "x " + item + " at " + price + " each. Order ID: " + orderId);
    }

    private void handleSell(Player player, String[] args) {
        if (args.length < 4) {
            player.sendMessage("Usage: /bazaar sell <item> <quantity> <priceEach>");
            return;
        }
        String item = args[1];
        int qty;
        double price;
        try {
            qty = Integer.parseInt(args[2]);
            price = Double.parseDouble(args[3]);
        } catch (NumberFormatException e) {
            player.sendMessage("Invalid quantity or price.");
            return;
        }
        if (qty <= 0 || price <= 0) {
            player.sendMessage("Quantity and price must be positive.");
            return;
        }
        UUID orderId = bazaarManager.addSellOrder(player.getUniqueId(), item, qty, price);
        player.sendMessage("Sell order placed for " + qty + "x " + item + " at " + price + " each. Order ID: " + orderId);
    }

    private void handleView(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage("Usage: /bazaar view <item>");
            return;
        }
        String item = args[1];
        List<BuyOrder> buys = bazaarManager.getBuyOrders(item);
        List<SellOrder> sells = bazaarManager.getSellOrders(item);

        player.sendMessage("=== Bazaar: " + item + " ===");
        player.sendMessage("Lowest ask: " + formatPrice(bazaarManager.getLowestAsk(item)));
        player.sendMessage("Highest bid: " + bazaarManager.getHighestBid(item));
        player.sendMessage("Buy orders (" + buys.size() + "):");
        for (BuyOrder o : buys) {
            player.sendMessage("  " + o.quantity() + "x @ " + o.priceEach() + " [" + o.id() + "]");
        }
        player.sendMessage("Sell orders (" + sells.size() + "):");
        for (SellOrder o : sells) {
            player.sendMessage("  " + o.quantity() + "x @ " + o.priceEach() + " [" + o.id() + "]");
        }
    }

    private void handleCancel(Player player, String[] args) {
        if (args.length < 3) {
            player.sendMessage("Usage: /bazaar cancel buy|sell <orderId>");
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
                bazaarManager.cancelBuyOrder(orderId);
                player.sendMessage("Buy order " + orderId + " cancelled.");
            } else if (type.equals("sell")) {
                bazaarManager.cancelSellOrder(orderId);
                player.sendMessage("Sell order " + orderId + " cancelled.");
            } else {
                player.sendMessage("Usage: /bazaar cancel buy|sell <orderId>");
            }
        } catch (IllegalArgumentException e) {
            player.sendMessage("Order not found: " + orderId);
        }
    }

    private void sendHelp(Player player) {
        player.sendMessage("=== Bazaar Commands ===");
        player.sendMessage("/bazaar buy <item> <quantity> <priceEach> — place a buy order");
        player.sendMessage("/bazaar sell <item> <quantity> <priceEach> — place a sell order");
        player.sendMessage("/bazaar view <item> — view current orders");
        player.sendMessage("/bazaar cancel buy|sell <orderId> — cancel a standing order");
    }

    private static String formatPrice(double price) {
        return price == Double.MAX_VALUE ? "none" : String.valueOf(price);
    }
}
