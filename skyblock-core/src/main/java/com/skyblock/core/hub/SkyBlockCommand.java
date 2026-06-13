package com.skyblock.core.hub;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

/**
 * Handles the {@code /skyblock} (alias {@code /sb}) command.
 *
 * <p>Prints a formatted help menu listing all major SkyBlock features and commands.</p>
 */
public final class SkyBlockCommand implements TabExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("This command can only be used by players.");
            return true;
        }
        sendHelp(player);
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return Collections.emptyList();
    }

    private void sendHelp(Player player) {
        player.sendMessage("=== SkyBlock Help ===");
        player.sendMessage("/skill                   — view and manage your skills");
        player.sendMessage("/collection              — view your item collections");
        player.sendMessage("/bank                    — manage your personal bank balance");
        player.sendMessage("/bazaar                  — browse and trade on the Bazaar");
        player.sendMessage("/ah                      — browse and list on the Auction House");
        player.sendMessage("/pet                     — view and equip your pets");
        player.sendMessage("/accessorybag            — manage your accessory bag");
        player.sendMessage("/wardrobe                — manage your armor wardrobe");
        player.sendMessage("/hotm                    — view Heart of the Mountain perks");
        player.sendMessage("/forge                   — view item forge recipes and slots");
        player.sendMessage("/dungeon                 — view dungeon stats and classes");
        player.sendMessage("/crimsonisle             — view Crimson Isle faction and kuudra info");
        player.sendMessage("/quest                   — view and track your quests");
        player.sendMessage("/trade                   — trade with other players");
        player.sendMessage("/gemstone                — view your gemstone collection");
        player.sendMessage("/island                  — view and manage your private island");
        player.sendMessage("=== Use /sb for this menu ===");
    }
}
