package com.skyblock.core.command;

import com.skyblock.core.crafting.RecipeData;
import com.skyblock.core.menu.ItemsMenu;
import com.skyblock.core.menu.RecipeMenu;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

/**
 * {@code /recipe <id>} — shows the real crafting recipe for any SkyBlock item. With no args, opens
 * the item browser. Tab-completes over every item that has a recipe.
 */
public final class RecipeCommand extends PlayerCommand {

    private static final int MAX_SUGGESTIONS = 50;

    @Override
    protected void openMenu(Player p) {
        new ItemsMenu(p).open(p);
    }

    @Override
    protected boolean execute(Player player, Command command, String label, String[] args) {
        if (args.length == 0) {
            player.sendMessage("§eUsage: §7/recipe <item id>§e — tab-complete to find ids.");
            return true;
        }
        String id = args[0].toUpperCase(Locale.ROOT);
        if (!RecipeData.has(id)) {
            player.sendMessage("§cNo recipe for §e" + args[0] + "§c.");
            return true;
        }
        new RecipeMenu(id).open(player);
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length != 1) {
            return Collections.emptyList();
        }
        String prefix = args[0].toUpperCase(Locale.ROOT);
        List<String> out = new ArrayList<>();
        for (String id : RecipeData.ids()) {
            if (id.startsWith(prefix)) {
                out.add(id);
                if (out.size() >= MAX_SUGGESTIONS) break;
            }
        }
        return out;
    }
}
