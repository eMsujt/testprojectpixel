package com.skyblock.core.command;

import com.skyblock.core.manager.WardrobeManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public final class WardrobeCommand extends PlayerCommand {

    private static final List<String> SUBCOMMANDS = Arrays.asList("save", "load", "list", "delete", "slots");

    private final WardrobeManager wardrobeManager;

    public WardrobeCommand(WardrobeManager wardrobeManager) {
        this.wardrobeManager = wardrobeManager;
    }

    @Override
    protected boolean execute(Player player, Command command, String label, String[] args) {
        if (args.length == 0) {
            openMenu(player);
            return true;
        }
        UUID id = player.getUniqueId();
        switch (args[0].toLowerCase()) {
            case "save":
                if (args.length < 2) {
                    player.sendMessage("Usage: /wardrobe save <name>");
                    return true;
                }
                ItemStack[] armor = player.getInventory().getArmorContents();
                wardrobeManager.saveOutfit(id, args[1], armor);
                player.sendMessage("Outfit '" + args[1] + "' saved.");
                return true;
            case "load":
                if (args.length < 2) {
                    player.sendMessage("Usage: /wardrobe load <name>");
                    return true;
                }
                ItemStack[] outfit = wardrobeManager.getOutfit(id, args[1]);
                if (outfit == null) {
                    player.sendMessage("No outfit named '" + args[1] + "' found.");
                    return true;
                }
                player.getInventory().setArmorContents(outfit);
                player.sendMessage("Outfit '" + args[1] + "' loaded.");
                return true;
            case "delete":
                if (args.length < 2) {
                    player.sendMessage("Usage: /wardrobe delete <name>");
                    return true;
                }
                boolean deleted = wardrobeManager.deleteOutfit(id, args[1]);
                if (!deleted) {
                    player.sendMessage("No outfit named '" + args[1] + "' found.");
                    return true;
                }
                player.sendMessage("Outfit '" + args[1] + "' deleted.");
                return true;
            case "list":
                Set<String> names = wardrobeManager.getOutfitNames(id);
                if (names.isEmpty()) {
                    player.sendMessage("You have no saved outfits.");
                    return true;
                }
                player.sendMessage("Saved outfits: " + String.join(", ", names));
                return true;
            case "slots":
                player.sendMessage("You have " + WardrobeManager.DEFAULT_UNLOCKED_SLOTS + " unlocked wardrobe slots.");
                return true;
            default:
                return true;
        }
    }

    @Override
    protected void openMenu(Player player) {
        // no-op: menu open is handled by the caller in a server context
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            return SUBCOMMANDS;
        }
        return Collections.emptyList();
    }
}
