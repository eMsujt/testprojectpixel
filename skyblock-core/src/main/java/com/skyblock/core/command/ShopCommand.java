package com.skyblock.core.command;

import com.skyblock.core.economy.manager.EconomyManager;
import com.skyblock.core.manager.ShopManager;
import com.skyblock.core.manager.ShopManager.Shop;
import com.skyblock.core.manager.ShopManager.ShopEntry;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Handles the {@code /shop} command.
 *
 * <p>Usage:
 * <ul>
 *   <li>{@code /shop}                         — list shops</li>
 *   <li>{@code /shop <shopId>}                — list items in that shop</li>
 *   <li>{@code /shop buy <shopId> <itemId>}   — purchase one item</li>
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
            sendShopList(player);
            return true;
        }

        if (args[0].equalsIgnoreCase("buy")) {
            handleBuy(player, args);
            return true;
        }

        Optional<Shop> shop = shopManager.getShop(args[0].toUpperCase());
        if (shop.isEmpty()) {
            player.sendMessage("Unknown shop: " + args[0] + ". Use /shop to see shops.");
            return true;
        }
        sendItemList(player, shop.get());
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            String lower = args[0].toLowerCase();
            List<String> options = new ArrayList<>(shopManager.getShops().keySet().stream()
                    .map(String::toLowerCase).collect(Collectors.toList()));
            options.add("buy");
            return options.stream().filter(s -> s.startsWith(lower)).collect(Collectors.toList());
        }
        if (args.length == 2 && args[0].equalsIgnoreCase("buy")) {
            String lower = args[1].toLowerCase();
            return shopManager.getShops().keySet().stream()
                    .map(String::toLowerCase)
                    .filter(s -> s.startsWith(lower))
                    .collect(Collectors.toList());
        }
        if (args.length == 3 && args[0].equalsIgnoreCase("buy")) {
            Optional<Shop> shop = shopManager.getShop(args[1].toUpperCase());
            if (shop.isEmpty()) return Collections.emptyList();
            String lower = args[2].toLowerCase();
            return shop.get().entries().stream()
                    .map(e -> e.itemId().toLowerCase())
                    .filter(s -> s.startsWith(lower))
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    private void handleBuy(Player player, String[] args) {
        if (args.length < 3) {
            player.sendMessage("Usage: /shop buy <shop> <item>");
            return;
        }
        String shopId = args[1].toUpperCase();
        Optional<Shop> shopOpt = shopManager.getShop(shopId);
        if (shopOpt.isEmpty()) {
            player.sendMessage("Unknown shop: " + args[1] + ". Use /shop to see shops.");
            return;
        }
        String itemId = args[2].toUpperCase();
        Optional<ShopEntry> entryOpt = shopManager.getEntry(shopId, itemId);
        if (entryOpt.isEmpty()) {
            player.sendMessage("Item '" + itemId + "' not found in " + shopOpt.get().title() + ".");
            return;
        }
        ShopEntry entry = entryOpt.get();
        if (!economyManager.withdraw(player.getUniqueId(), entry.buyPrice())) {
            player.sendMessage("You don't have enough coins. " + entry.itemId() + " costs " + entry.buyPrice() + " coins.");
            return;
        }
        player.sendMessage("You purchased " + entry.itemId() + " for " + entry.buyPrice() + " coins.");
    }

    private void sendShopList(Player player) {
        player.sendMessage("=== Shops ===");
        for (Shop shop : shopManager.getShops().values()) {
            player.sendMessage("- " + shop.id().toLowerCase() + " (" + shop.entries().size() + " items)");
        }
        player.sendMessage("Use /shop <shop> to browse, or /shop buy <shop> <item> to purchase.");
    }

    private void sendItemList(Player player, Shop shop) {
        player.sendMessage("=== Shop: " + shop.title() + " ===");
        if (shop.entries().isEmpty()) {
            player.sendMessage("No items available in this shop.");
            return;
        }
        for (ShopEntry entry : shop.entries()) {
            player.sendMessage("- " + entry.itemId() + " — " + entry.buyPrice() + " coins");
        }
    }
}
