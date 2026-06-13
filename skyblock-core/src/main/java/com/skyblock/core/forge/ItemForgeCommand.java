package com.skyblock.core.forge;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public final class ItemForgeCommand implements TabExecutor {

    private static final List<String> SUBCOMMANDS = Arrays.asList("list", "start", "check", "claim");

    private final ItemForgeManager forgeManager;

    public ItemForgeCommand(ItemForgeManager forgeManager) {
        this.forgeManager = forgeManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("This command can only be used by players.");
            return true;
        }

        if (args.length == 0) {
            handleCheck(player);
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "list"  -> handleList(player);
            case "start" -> handleStart(player, args);
            case "check" -> handleCheck(player);
            case "claim" -> handleClaim(player, args);
            default      -> sendHelp(player);
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
        if (args.length == 2 && args[0].equalsIgnoreCase("start")) {
            String prefix = args[1].toLowerCase();
            return Arrays.stream(ItemForgeManager.ForgeRecipe.values())
                    .map(r -> r.name().toLowerCase())
                    .filter(n -> n.startsWith(prefix))
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    private void handleList(Player player) {
        player.sendMessage("=== Forge Recipes ===");
        for (ItemForgeManager.ForgeRecipe recipe : ItemForgeManager.ForgeRecipe.values()) {
            player.sendMessage("  " + recipe.name().toLowerCase() + " — "
                    + recipe.getDisplayName() + " (" + formatSeconds(recipe.getDurationSeconds()) + ")");
        }
    }

    private void handleStart(Player player, String[] args) {
        if (args.length < 3) {
            player.sendMessage("Usage: /forge start <recipe> <slot>");
            return;
        }
        ItemForgeManager.ForgeRecipe recipe;
        try {
            recipe = ItemForgeManager.ForgeRecipe.valueOf(args[1].toUpperCase());
        } catch (IllegalArgumentException e) {
            player.sendMessage("Unknown recipe: " + args[1]);
            return;
        }
        int slot;
        try {
            slot = Integer.parseInt(args[2]);
        } catch (NumberFormatException e) {
            player.sendMessage("Slot must be a number 0–" + (ItemForgeManager.MAX_SLOTS - 1) + ".");
            return;
        }
        try {
            forgeManager.startForge(player.getUniqueId(), slot, recipe, System.currentTimeMillis());
            player.sendMessage("Started forging " + recipe.getDisplayName()
                    + " in slot " + slot + " (" + formatSeconds(recipe.getDurationSeconds()) + ").");
        } catch (IllegalArgumentException | IllegalStateException e) {
            player.sendMessage(e.getMessage());
        }
    }

    private void handleCheck(Player player) {
        Map<Integer, ItemForgeManager.ForgeSlot> playerSlots =
                forgeManager.getSlots(player.getUniqueId());
        if (playerSlots.isEmpty()) {
            player.sendMessage("You have no active forge slots.");
            return;
        }
        long now = System.currentTimeMillis();
        player.sendMessage("=== Your Forge Slots ===");
        for (Map.Entry<Integer, ItemForgeManager.ForgeSlot> entry : playerSlots.entrySet()) {
            ItemForgeManager.ForgeSlot fs = entry.getValue();
            String status = fs.isComplete(now)
                    ? "READY"
                    : formatSeconds((int) fs.remainingSeconds(now)) + " remaining";
            player.sendMessage("  Slot " + entry.getKey() + ": "
                    + fs.recipe.getDisplayName() + " — " + status);
        }
    }

    private void handleClaim(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage("Usage: /forge claim <slot>");
            return;
        }
        int slot;
        try {
            slot = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            player.sendMessage("Slot must be a number 0–" + (ItemForgeManager.MAX_SLOTS - 1) + ".");
            return;
        }
        ItemForgeManager.ForgeSlot fs =
                forgeManager.claimSlot(player.getUniqueId(), slot, System.currentTimeMillis());
        if (fs == null) {
            ItemForgeManager.ForgeSlot active = forgeManager.getSlot(player.getUniqueId(), slot);
            if (active == null) {
                player.sendMessage("Slot " + slot + " is empty.");
            } else {
                long rem = active.remainingSeconds(System.currentTimeMillis());
                player.sendMessage(active.recipe.getDisplayName()
                        + " is not ready yet — " + formatSeconds((int) rem) + " remaining.");
            }
            return;
        }
        player.sendMessage("Claimed " + fs.recipe.getDisplayName() + " from slot " + slot + "!");
    }

    private void sendHelp(Player player) {
        player.sendMessage("=== Forge Commands ===");
        player.sendMessage("/forge list                    — list all recipes");
        player.sendMessage("/forge start <recipe> <slot>   — begin forging");
        player.sendMessage("/forge check                   — check your slots");
        player.sendMessage("/forge claim <slot>            — claim a finished item");
    }

    private static String formatSeconds(int seconds) {
        if (seconds < 60)  return seconds + "s";
        if (seconds < 3600) return (seconds / 60) + "m " + (seconds % 60) + "s";
        int h = seconds / 3600;
        int m = (seconds % 3600) / 60;
        return h + "h " + m + "m";
    }
}
