package com.skyblock.plugin.commands;

import com.skyblock.core.bazaar.BazaarManager;
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

        UUID id = player.getUniqueId();
        BazaarManager manager = BazaarManager.getInstance();
        List<BazaarManager.BazaarOrder> orders = manager.getOrdersForPlayer(id);
        player.sendMessage("=== Bazaar ===");
        player.sendMessage("Active Orders: " + manager.getOrderCount(id));
        if (orders.isEmpty()) {
            player.sendMessage("You have no active orders.");
        } else {
            for (BazaarManager.BazaarOrder order : orders) {
                player.sendMessage(order.type().getDisplayName() + " — " + order.itemId()
                        + " x" + order.quantity() + " @ " + order.priceEach() + " coins each");
            }
        }
        return true;
    }
}
