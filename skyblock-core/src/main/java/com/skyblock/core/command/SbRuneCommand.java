package com.skyblock.core.command;

import com.skyblock.core.item.RuneItem;
import com.skyblock.core.manager.RuneManager;
import com.skyblock.core.manager.RuneManager.RuneType;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

/**
 * {@code /sbrune <type> [level]} — gives the sender a rune item (the consumable
 * dropped into the Runic Pedestal's sacrifice slot). Admin/testing entry point
 * until runes have in-game drop sources.
 */
public final class SbRuneCommand implements TabExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("This command can only be used by players.");
            return true;
        }
        if (args.length == 0) {
            player.sendMessage("§6/sbrune <type> [level] §7— get a rune item.");
            player.sendMessage("§7Types: §f" + ids());
            return true;
        }
        RuneType type = RuneManager.getInstance().getRune(args[0]);
        if (type == null) {
            player.sendMessage("§cUnknown rune '" + args[0] + "'. Types: §f" + ids());
            return true;
        }
        int level = 1;
        if (args.length >= 2) {
            try {
                level = Integer.parseInt(args[1]);
            } catch (NumberFormatException ignored) {
                // keep level 1
            }
        }
        ItemStack rune = RuneItem.createRuneItem(type, level);
        for (ItemStack overflow : player.getInventory().addItem(rune).values()) {
            player.getWorld().dropItemNaturally(player.getLocation(), overflow);
        }
        player.sendMessage("§aGave you §r" + rune.getItemMeta().getDisplayName() + "§a.");
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> out = new ArrayList<>();
        if (args.length == 1) {
            String prefix = args[0].toLowerCase();
            for (String id : RuneManager.getInstance().getRegistry().keySet()) {
                if (id.startsWith(prefix)) {
                    out.add(id);
                }
            }
        }
        return out;
    }

    private static String ids() {
        return String.join(", ", RuneManager.getInstance().getRegistry().keySet());
    }
}
