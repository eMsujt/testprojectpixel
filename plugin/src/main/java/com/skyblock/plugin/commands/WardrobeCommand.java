package com.skyblock.plugin.commands;

import com.skyblock.core.wardrobe.WardrobeManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Set;
import java.util.UUID;

public final class WardrobeCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("This command can only be used by players.");
            return true;
        }

        if (args.length == 0) {
            sendHelp(player);
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "save"   -> handleSave(player, args);
            case "load"   -> handleLoad(player, args);
            case "delete" -> handleDelete(player, args);
            case "list"   -> handleList(player);
            case "slots"  -> handleSlots(player);
            case "slot"   -> handleSlot(player, args);
            default       -> sendHelp(player);
        }
        return true;
    }

    private void handleSave(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage("Usage: /skyblock wardrobe save <name>");
            return;
        }
        String name = args[1];
        WardrobeManager manager = WardrobeManager.getInstance();
        ItemStack[] armor = player.getInventory().getArmorContents();
        boolean saved = manager.saveOutfit(player.getUniqueId(), name, armor);
        if (saved) {
            player.sendMessage("Outfit '" + name + "' saved.");
        } else {
            player.sendMessage("You have reached the maximum of " + WardrobeManager.MAX_OUTFITS + " outfits.");
        }
    }

    private void handleLoad(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage("Usage: /skyblock wardrobe load <name>");
            return;
        }
        String name = args[1];
        WardrobeManager manager = WardrobeManager.getInstance();
        ItemStack[] armor = manager.getOutfit(player.getUniqueId(), name);
        if (armor == null) {
            player.sendMessage("No outfit named '" + name + "' found.");
            return;
        }
        player.getInventory().setArmorContents(armor);
        player.sendMessage("Outfit '" + name + "' equipped.");
    }

    private void handleDelete(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage("Usage: /skyblock wardrobe delete <name>");
            return;
        }
        String name = args[1];
        WardrobeManager manager = WardrobeManager.getInstance();
        boolean removed = manager.deleteOutfit(player.getUniqueId(), name);
        if (removed) {
            player.sendMessage("Outfit '" + name + "' deleted.");
        } else {
            player.sendMessage("No outfit named '" + name + "' found.");
        }
    }

    private void handleList(Player player) {
        UUID id = player.getUniqueId();
        WardrobeManager manager = WardrobeManager.getInstance();
        Set<String> names = manager.getOutfitNames(id);
        player.sendMessage("=== Wardrobe (" + names.size() + "/" + WardrobeManager.MAX_OUTFITS + ") ===");
        if (names.isEmpty()) {
            player.sendMessage("You have no saved outfits.");
            return;
        }
        for (String name : names) {
            player.sendMessage("  - " + name);
        }
    }

    private void handleSlots(Player player) {
        WardrobeManager manager = WardrobeManager.getInstance();
        player.sendMessage("=== Wardrobe Slots ===");
        for (WardrobeManager.WardrobeSlot slot : WardrobeManager.WardrobeSlot.values()) {
            ItemStack[] armor = manager.getOutfit(player.getUniqueId(), slot);
            String status = (armor != null) ? "occupied" : "empty";
            player.sendMessage("  " + slot.getDisplayName() + " (" + slot.name() + "): " + status);
        }
    }

    private void handleSlot(Player player, String[] args) {
        if (args.length < 3) {
            player.sendMessage("Usage: /skyblock wardrobe slot <save|load|clear> <SLOT_1..SLOT_18>");
            return;
        }
        WardrobeManager.WardrobeSlot slot;
        try {
            slot = WardrobeManager.WardrobeSlot.valueOf(args[2].toUpperCase());
        } catch (IllegalArgumentException e) {
            player.sendMessage("Unknown slot '" + args[2] + "'. Use SLOT_1 through SLOT_18.");
            return;
        }
        WardrobeManager manager = WardrobeManager.getInstance();
        switch (args[1].toLowerCase()) {
            case "save" -> {
                ItemStack[] armor = player.getInventory().getArmorContents();
                boolean saved = manager.saveOutfit(player.getUniqueId(), slot, armor);
                if (saved) {
                    player.sendMessage(slot.getDisplayName() + " saved.");
                } else {
                    player.sendMessage("You have reached the maximum of " + WardrobeManager.MAX_OUTFITS + " outfits.");
                }
            }
            case "load" -> {
                ItemStack[] armor = manager.getOutfit(player.getUniqueId(), slot);
                if (armor == null) {
                    player.sendMessage(slot.getDisplayName() + " is empty.");
                    return;
                }
                player.getInventory().setArmorContents(armor);
                player.sendMessage(slot.getDisplayName() + " equipped.");
            }
            case "clear" -> {
                boolean removed = manager.deleteOutfit(player.getUniqueId(), slot);
                if (removed) {
                    player.sendMessage(slot.getDisplayName() + " cleared.");
                } else {
                    player.sendMessage(slot.getDisplayName() + " is already empty.");
                }
            }
            default -> player.sendMessage("Usage: /skyblock wardrobe slot <save|load|clear> <SLOT_1..SLOT_18>");
        }
    }

    private void sendHelp(Player player) {
        player.sendMessage("=== Wardrobe Commands ===");
        player.sendMessage("/skyblock wardrobe save <name>              — save current armor as an outfit");
        player.sendMessage("/skyblock wardrobe load <name>              — equip a saved outfit");
        player.sendMessage("/skyblock wardrobe delete <name>            — remove a saved outfit");
        player.sendMessage("/skyblock wardrobe list                     — list all saved outfits");
        player.sendMessage("/skyblock wardrobe slots                    — list wardrobe slots");
        player.sendMessage("/skyblock wardrobe slot save <SLOT_1..18>   — save current armor into a slot");
        player.sendMessage("/skyblock wardrobe slot load <SLOT_1..18>   — equip armor from a slot");
        player.sendMessage("/skyblock wardrobe slot clear <SLOT_1..18>  — clear a slot");
    }
}
