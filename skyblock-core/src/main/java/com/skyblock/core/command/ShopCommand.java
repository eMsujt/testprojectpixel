package com.skyblock.core.command;

import com.skyblock.core.manager.ShopManager;
import com.skyblock.core.manager.ShopManager.Shop;
import com.skyblock.core.manager.ShopManager.ShopEntry;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Lists registered NPC shops and their buy/sell pricing.
 *
 * <p>{@code /shop} lists all shops; {@code /shop <id>} shows the prices of the
 * items in that shop.</p>
 */
public final class ShopCommand extends PlayerCommand {

    private final ShopManager shopManager;

    public ShopCommand(ShopManager shopManager) {
        this.shopManager = shopManager;
    }

    @Override
    protected void openMenu(Player p) {}

    @Override
    protected boolean execute(Player player, Command command, String label, String[] args) {
        if (args.length == 0) {
            var shops = shopManager.getShops();
            if (shops.isEmpty()) {
                player.sendMessage(ChatColor.GRAY + "There are no shops available.");
                return true;
            }
            player.sendMessage(ChatColor.GOLD + "Shops:");
            for (Shop shop : shops.values()) {
                player.sendMessage(ChatColor.YELLOW + "  " + shop.id() + ChatColor.GRAY + " - " + shop.title());
            }
            player.sendMessage(ChatColor.GRAY + "Use " + ChatColor.YELLOW + "/" + label + " <id>"
                    + ChatColor.GRAY + " to view a shop's prices.");
            return true;
        }

        Optional<Shop> found = shopManager.getShop(args[0]);
        if (found.isEmpty()) {
            player.sendMessage(ChatColor.RED + "No shop with id '" + args[0] + "'.");
            return true;
        }
        Shop shop = found.get();
        player.sendMessage(ChatColor.GOLD + shop.title() + ChatColor.GRAY + ":");
        if (shop.entries().isEmpty()) {
            player.sendMessage(ChatColor.GRAY + "  This shop is empty.");
            return true;
        }
        for (ShopEntry entry : shop.entries()) {
            player.sendMessage(ChatColor.YELLOW + "  " + entry.itemId()
                    + ChatColor.GRAY + " - buy " + ChatColor.GOLD + entry.buyPrice()
                    + ChatColor.GRAY + ", sell " + ChatColor.GOLD + entry.sellPrice());
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            String prefix = args[0].toLowerCase();
            List<String> ids = new ArrayList<>();
            for (String id : shopManager.getShops().keySet()) {
                if (id.toLowerCase().startsWith(prefix)) {
                    ids.add(id);
                }
            }
            return ids;
        }
        return super.onTabComplete(sender, command, alias, args);
    }
}
