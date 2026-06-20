package com.skyblock.core.command;

import com.skyblock.core.collections.gui.CollectionCategoryMenu;
import com.skyblock.core.menu.CollectionsMenu;
import com.skyblock.core.model.CollectionCategory;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public final class CollectionCommand extends PlayerCommand {

    @Override
    protected void openMenu(Player p) {
        new CollectionsMenu(p.getUniqueId()).open(p);
    }

    @Override
    protected boolean execute(Player player, Command command, String label, String[] args) {
        if (args.length == 0) {
            openMenu(player);
            return true;
        }
        CollectionCategory category = parseCategory(args[0]);
        if (category == null) {
            player.sendMessage("Unknown category: " + args[0] + ". Valid categories: " +
                    Arrays.stream(CollectionCategory.values())
                          .map(c -> c.name().toLowerCase())
                          .collect(Collectors.joining(", ")));
            return true;
        }
        new CollectionCategoryMenu(player.getUniqueId(), category).open(player);
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            String prefix = args[0].toLowerCase();
            return Arrays.stream(CollectionCategory.values())
                    .map(c -> c.name().toLowerCase())
                    .filter(s -> s.startsWith(prefix))
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    private static CollectionCategory parseCategory(String input) {
        for (CollectionCategory c : CollectionCategory.values()) {
            if (c.name().equalsIgnoreCase(input)) return c;
        }
        return null;
    }
}
