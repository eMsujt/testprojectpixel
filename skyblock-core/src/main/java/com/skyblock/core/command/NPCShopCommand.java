package com.skyblock.core.command;

import com.skyblock.core.manager.NPCShopManager;
import com.skyblock.core.manager.ShopManager;
import com.skyblock.core.menu.NPCShopMenu;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * Opens the NPC shop GUI for a named shop.
 *
 * <p>{@code /npcshop} lists available shops; {@code /npcshop <id>} opens the GUI.</p>
 */
public final class NPCShopCommand extends PlayerCommand {

    private final NPCShopManager npcShopManager;

    public NPCShopCommand(NPCShopManager npcShopManager) {
        this.npcShopManager = npcShopManager;
    }

    @Override
    protected void openMenu(Player player) {
        listShops(player);
    }

    @Override
    protected boolean execute(Player player, Command command, String label, String[] args) {
        if (args.length == 0) {
            listShops(player);
            return true;
        }
        String shopId = args[0].toLowerCase();
        if (ShopManager.getInstance().getShop(shopId).isEmpty()) {
            player.sendMessage(ChatColor.RED + "No shop found with id '" + shopId + "'.");
            player.sendMessage(ChatColor.GRAY + "Use /" + label + " to see available shops.");
            return true;
        }
        new NPCShopMenu(shopId).open(player);
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            String prefix = args[0].toLowerCase();
            List<String> results = new ArrayList<>();
            for (String id : npcShopManager.getShopIds()) {
                if (id.startsWith(prefix)) results.add(id);
            }
            return results;
        }
        return super.onTabComplete(sender, command, alias, args);
    }

    private void listShops(Player player) {
        player.sendMessage(ChatColor.GOLD + "NPC Shops:");
        for (String id : npcShopManager.getShopIds()) {
            ShopManager.getInstance().getShop(id).ifPresent(shop ->
                player.sendMessage(ChatColor.YELLOW + "  " + id + ChatColor.GRAY + " - " + shop.title())
            );
        }
        player.sendMessage(ChatColor.GRAY + "Use /npcshop <id> to browse a shop.");
    }
}
