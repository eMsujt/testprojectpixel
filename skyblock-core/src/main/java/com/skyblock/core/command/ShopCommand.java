package com.skyblock.core.command;

import com.skyblock.core.economy.EconomyManager;
import com.skyblock.core.shop.ShopManager;
import com.skyblock.core.shop.ShopManager.ShopCategory;
import com.skyblock.core.shop.ShopManager.ShopItem;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Handles the {@code /shop} command.
 *
 * <p>Usage:
 * <ul>
 *   <li>{@code /shop}                            — list categories</li>
 *   <li>{@code /shop <category>}                 — list items in that category</li>
 *   <li>{@code /shop buy <category> <item>}      — purchase one item</li>
 * </ul>
 * </p>
 */
public final class ShopCommand implements TabExecutor {

    private final ShopManager shopManager;
    private final EconomyManager economyManager;

    public ShopCommand(ShopManager shopManager, EconomyManager economyManager) {
        this.shopManager = shopManager;
        this.economyManager = economyManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("This command can only be used by players.");
            return true;
        }

        if (args.length == 0) {
            sendCategoryList(player);
            return true;
        }

        if (args[0].equalsIgnoreCase("buy")) {
            handleBuy(player, args);
            return true;
        }

        ShopCategory category = parseCategory(args[0]);
        if (category == null) {
            player.sendMessage("Unknown category: " + args[0] + ". Use /shop to see categories.");
            return true;
        }
        sendItemList(player, category);
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            String lower = args[0].toLowerCase();
            List<String> options = Arrays.stream(ShopCategory.values())
                    .map(c -> c.name().toLowerCase())
                    .collect(Collectors.toList());
            options.add("buy");
            return options.stream().filter(s -> s.startsWith(lower)).collect(Collectors.toList());
        }
        if (args.length == 2 && args[0].equalsIgnoreCase("buy")) {
            String lower = args[1].toLowerCase();
            return Arrays.stream(ShopCategory.values())
                    .map(c -> c.name().toLowerCase())
                    .filter(s -> s.startsWith(lower))
                    .collect(Collectors.toList());
        }
        if (args.length == 3 && args[0].equalsIgnoreCase("buy")) {
            ShopCategory category = parseCategory(args[1]);
            if (category == null) return Collections.emptyList();
            String lower = args[2].toLowerCase();
            return shopManager.getItems(category).stream()
                    .map(ShopItem::name)
                    .filter(n -> n.toLowerCase().startsWith(lower))
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    private void handleBuy(Player player, String[] args) {
        if (args.length < 3) {
            player.sendMessage("Usage: /shop buy <category> <item>");
            return;
        }
        ShopCategory category = parseCategory(args[1]);
        if (category == null) {
            player.sendMessage("Unknown category: " + args[1] + ". Use /shop to see categories.");
            return;
        }
        String itemName = String.join(" ", Arrays.copyOfRange(args, 2, args.length));
        ShopItem item = shopManager.findByName(category, itemName);
        if (item == null) {
            player.sendMessage("Item '" + itemName + "' not found in " + category.name().toLowerCase() + ".");
            return;
        }
        if (!economyManager.withdraw(player.getUniqueId(), item.price())) {
            player.sendMessage("You don't have enough coins. " + item.name() + " costs " + item.price() + " coins.");
            return;
        }
        player.sendMessage("You purchased " + item.name() + " for " + item.price() + " coins.");
    }

    private void sendCategoryList(Player player) {
        player.sendMessage("=== Shop Categories ===");
        for (ShopCategory category : ShopCategory.values()) {
            int count = shopManager.getItems(category).size();
            player.sendMessage("- " + category.name().toLowerCase() + " (" + count + " items)");
        }
        player.sendMessage("Use /shop <category> to browse, or /shop buy <category> <item> to purchase.");
    }

    private void sendItemList(Player player, ShopCategory category) {
        List<ShopItem> items = shopManager.getItems(category);
        player.sendMessage("=== Shop: " + category.name().toLowerCase() + " ===");
        if (items.isEmpty()) {
            player.sendMessage("No items available in this category.");
            return;
        }
        for (ShopItem item : items) {
            player.sendMessage("- " + item.name() + " — " + item.price() + " coins");
        }
    }

    private static ShopCategory parseCategory(String input) {
        for (ShopCategory category : ShopCategory.values()) {
            if (category.name().equalsIgnoreCase(input)) {
                return category;
            }
        }
        return null;
    }
}
