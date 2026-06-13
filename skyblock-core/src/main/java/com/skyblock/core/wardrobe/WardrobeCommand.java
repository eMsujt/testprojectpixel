package com.skyblock.core.wardrobe;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Handles the {@code /wardrobe} command.
 *
 * <p>Subcommands:
 * <ul>
 *   <li>{@code /wardrobe save <name>}         — snapshot current armor as a named outfit</li>
 *   <li>{@code /wardrobe load <name>}         — equip a saved outfit</li>
 *   <li>{@code /wardrobe delete <name>}       — remove a saved outfit</li>
 *   <li>{@code /wardrobe list}                — list all saved outfits</li>
 *   <li>{@code /wardrobe slots}               — list available wardrobe slots</li>
 *   <li>{@code /wardrobe slot save <slot>}    — snapshot current armor into a slot</li>
 *   <li>{@code /wardrobe slot load <slot>}    — equip armor from a slot</li>
 *   <li>{@code /wardrobe slot clear <slot>}   — clear a slot</li>
 * </ul>
 * </p>
 */
public final class WardrobeCommand implements TabExecutor {

    private static final List<String> SUBCOMMANDS = Arrays.asList("save", "load", "delete", "list", "slots", "slot");

    private final WardrobeManager wardrobeManager;

    public WardrobeCommand(WardrobeManager wardrobeManager) {
        this.wardrobeManager = wardrobeManager;
    }

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

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            String prefix = args[0].toLowerCase();
            return SUBCOMMANDS.stream()
                    .filter(s -> s.startsWith(prefix))
                    .collect(Collectors.toList());
        }
        if (args.length == 2 && sender instanceof Player player) {
            String sub = args[0].toLowerCase();
            if (sub.equals("load") || sub.equals("delete")) {
                String prefix = args[1].toLowerCase();
                return wardrobeManager.getOutfitNames(player.getUniqueId()).stream()
                        .filter(n -> n.toLowerCase().startsWith(prefix))
                        .collect(Collectors.toList());
            }
            if (sub.equals("slot")) {
                String prefix = args[1].toLowerCase();
                return Arrays.asList("save", "load", "clear").stream()
                        .filter(s -> s.startsWith(prefix))
                        .collect(Collectors.toList());
            }
        }
        if (args.length == 3 && args[0].equalsIgnoreCase("slot")) {
            String action = args[1].toLowerCase();
            if (action.equals("save") || action.equals("load") || action.equals("clear")) {
                String prefix = args[2].toUpperCase();
                return Arrays.stream(WardrobeManager.WardrobeSlot.values())
                        .map(Enum::name)
                        .filter(n -> n.startsWith(prefix))
                        .collect(Collectors.toList());
            }
        }
        return Collections.emptyList();
    }

    private void handleSave(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage("Usage: /wardrobe save <name>");
            return;
        }
        String name = args[1];
        PlayerInventory inv = player.getInventory();
        ItemStack[] armor = inv.getArmorContents();
        boolean saved = wardrobeManager.saveOutfit(player.getUniqueId(), name, armor);
        if (saved) {
            player.sendMessage("Outfit '" + name + "' saved.");
        } else {
            player.sendMessage("You have reached the maximum of " + WardrobeManager.MAX_OUTFITS + " outfits.");
        }
    }

    private void handleLoad(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage("Usage: /wardrobe load <name>");
            return;
        }
        String name = args[1];
        ItemStack[] armor = wardrobeManager.getOutfit(player.getUniqueId(), name);
        if (armor == null) {
            player.sendMessage("No outfit named '" + name + "' found.");
            return;
        }
        player.getInventory().setArmorContents(armor);
        player.sendMessage("Outfit '" + name + "' equipped.");
    }

    private void handleDelete(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage("Usage: /wardrobe delete <name>");
            return;
        }
        String name = args[1];
        boolean removed = wardrobeManager.deleteOutfit(player.getUniqueId(), name);
        if (removed) {
            player.sendMessage("Outfit '" + name + "' deleted.");
        } else {
            player.sendMessage("No outfit named '" + name + "' found.");
        }
    }

    private void handleList(Player player) {
        Set<String> names = wardrobeManager.getOutfitNames(player.getUniqueId());
        if (names.isEmpty()) {
            player.sendMessage("You have no saved outfits.");
            return;
        }
        player.sendMessage("=== Your Wardrobe (" + names.size() + "/" + WardrobeManager.MAX_OUTFITS + ") ===");
        for (String name : names) {
            player.sendMessage("  - " + name);
        }
    }

    private void handleSlots(Player player) {
        player.sendMessage("=== Wardrobe Slots ===");
        for (WardrobeManager.WardrobeSlot slot : WardrobeManager.WardrobeSlot.values()) {
            ItemStack[] armor = wardrobeManager.getOutfit(player.getUniqueId(), slot);
            String status = (armor != null) ? "occupied" : "empty";
            player.sendMessage("  " + slot.getDisplayName() + " (" + slot.name() + "): " + status);
        }
    }

    private void handleSlot(Player player, String[] args) {
        if (args.length < 3) {
            player.sendMessage("Usage: /wardrobe slot <save|load|clear> <SLOT_1..SLOT_18>");
            return;
        }
        WardrobeManager.WardrobeSlot slot;
        try {
            slot = WardrobeManager.WardrobeSlot.valueOf(args[2].toUpperCase());
        } catch (IllegalArgumentException e) {
            player.sendMessage("Unknown slot '" + args[2] + "'. Use SLOT_1 through SLOT_9.");
            return;
        }
        switch (args[1].toLowerCase()) {
            case "save" -> {
                ItemStack[] armor = player.getInventory().getArmorContents();
                boolean saved = wardrobeManager.saveOutfit(player.getUniqueId(), slot, armor);
                if (saved) {
                    player.sendMessage(slot.getDisplayName() + " saved.");
                } else {
                    player.sendMessage("You have reached the maximum of " + WardrobeManager.MAX_OUTFITS + " outfits.");
                }
            }
            case "load" -> {
                ItemStack[] armor = wardrobeManager.getOutfit(player.getUniqueId(), slot);
                if (armor == null) {
                    player.sendMessage(slot.getDisplayName() + " is empty.");
                    return;
                }
                player.getInventory().setArmorContents(armor);
                player.sendMessage(slot.getDisplayName() + " equipped.");
            }
            case "clear" -> {
                boolean removed = wardrobeManager.deleteOutfit(player.getUniqueId(), slot);
                if (removed) {
                    player.sendMessage(slot.getDisplayName() + " cleared.");
                } else {
                    player.sendMessage(slot.getDisplayName() + " is already empty.");
                }
            }
            default -> player.sendMessage("Usage: /wardrobe slot <save|load|clear> <SLOT_1..SLOT_18>");
        }
    }

    private void sendHelp(Player player) {
        player.sendMessage("=== Wardrobe Commands ===");
        player.sendMessage("/wardrobe save <name>              — save current armor as an outfit");
        player.sendMessage("/wardrobe load <name>              — equip a saved outfit");
        player.sendMessage("/wardrobe delete <name>            — remove a saved outfit");
        player.sendMessage("/wardrobe list                     — list all saved outfits");
        player.sendMessage("/wardrobe slots                    — list wardrobe slots");
        player.sendMessage("/wardrobe slot save <SLOT_1..9>    — save current armor into a slot");
        player.sendMessage("/wardrobe slot load <SLOT_1..9>    — equip armor from a slot");
        player.sendMessage("/wardrobe slot clear <SLOT_1..9>   — clear a slot");
    }
}
