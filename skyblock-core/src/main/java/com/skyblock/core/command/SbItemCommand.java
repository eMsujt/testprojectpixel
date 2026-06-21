package com.skyblock.core.command;

import com.skyblock.core.item.SkyblockItems;
import com.skyblock.core.menu.ItemsMenu;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

/**
 * {@code /sbitem [<id> [amount]]} — with no args opens the item browser; with an internal id
 * gives a fully 1:1 copy of any registered SkyBlock item (texture + name + lore). Tab-completes
 * over every known item id.
 */
public final class SbItemCommand extends PlayerCommand {

    private static final int MAX_SUGGESTIONS = 50;

    @Override
    protected void openMenu(Player p) {
        new ItemsMenu(p).open(p);
    }

    @Override
    protected boolean execute(Player player, Command command, String label, String[] args) {
        if (args.length == 0) {
            openMenu(player);
            return true;
        }
        String id = args[0].toUpperCase(Locale.ROOT);
        if (!SkyblockItems.exists(id)) {
            player.sendMessage("§cUnknown item: §e" + args[0]
                    + "§c. Use tab-completion to browse item ids.");
            return true;
        }
        int amount = 1;
        if (args.length >= 2) {
            try {
                amount = Integer.parseInt(args[1]);
            } catch (NumberFormatException ignored) {
                amount = 1;
            }
        }
        ItemStack item = SkyblockItems.build(id, amount);
        if (item == null) {
            player.sendMessage("§cCould not build item: §e" + id);
            return true;
        }
        player.getInventory().addItem(item);
        ItemMeta meta = item.getItemMeta();
        String shown = meta != null && meta.hasDisplayName() ? meta.getDisplayName() : id;
        player.sendMessage("§aGave §e" + item.getAmount() + "x §r" + shown + "§a.");
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length != 1) {
            return Collections.emptyList();
        }
        String prefix = args[0].toUpperCase(Locale.ROOT);
        List<String> out = new ArrayList<>();
        for (String id : SkyblockItems.ids()) {
            if (id.startsWith(prefix)) {
                out.add(id);
                if (out.size() >= MAX_SUGGESTIONS) break;
            }
        }
        return out;
    }
}
