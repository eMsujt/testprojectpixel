package com.skyblock.core.command;

import com.skyblock.core.manager.ForgeManager;
import com.skyblock.core.manager.ForgeManager.ForgeJob;
import com.skyblock.core.menu.ForgeMenu;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public final class ForgeCommand implements TabExecutor {

    private final ForgeManager forgeManager;

    public ForgeCommand(ForgeManager forgeManager) {
        this.forgeManager = forgeManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("This command can only be used by players.");
            return true;
        }

        if (args.length == 0) {
            new ForgeMenu(player).open(player);
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "start"   -> handleStart(player, args);
            case "collect" -> handleCollect(player, args);
            case "cancel"  -> handleCancel(player, args);
            case "list"    -> handleList(player);
            default        -> sendHelp(player);
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            String lower = args[0].toLowerCase();
            return Arrays.asList("start", "collect", "cancel", "list").stream()
                    .filter(s -> s.startsWith(lower))
                    .toList();
        }
        if (args.length == 2 && args[0].equalsIgnoreCase("start")) {
            String lower = args[1].toLowerCase();
            return forgeManager.getRecipes().keySet().stream()
                    .filter(s -> s.startsWith(lower))
                    .sorted()
                    .toList();
        }
        return Collections.emptyList();
    }

    private void handleStart(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage("Usage: /forge start <recipe>");
            return;
        }
        String recipeId = args[1].toLowerCase();
        try {
            ForgeJob job = forgeManager.startForge(player.getUniqueId(), recipeId, System.currentTimeMillis());
            player.sendMessage("Started forging " + job.getRecipe().getDisplayName()
                    + " in slot " + (job.getSlot() + 1)
                    + " (" + formatDuration(job.getDurationSeconds()) + ").");
        } catch (IllegalArgumentException e) {
            player.sendMessage("Unknown recipe: " + recipeId + ". Use /forge list to see available recipes.");
        } catch (IllegalStateException e) {
            player.sendMessage("All forge slots are busy. Collect a completed item first.");
        }
    }

    private void handleCollect(Player player, String[] args) {
        try {
            ForgeJob job;
            if (args.length >= 2) {
                int slot = Integer.parseInt(args[1]) - 1;
                job = forgeManager.collectForge(player.getUniqueId(), slot, System.currentTimeMillis());
            } else {
                job = forgeManager.collectForge(player.getUniqueId(), System.currentTimeMillis());
            }
            ForgeManager.giveOutput(player, job);
            player.sendMessage("Collected " + job.getRecipe().getOutputAmount() + "x "
                    + job.getRecipe().getOutputItem() + " from forging "
                    + job.getRecipe().getDisplayName() + "!");
        } catch (NumberFormatException e) {
            player.sendMessage("Usage: /forge collect [slot]");
        } catch (IllegalStateException e) {
            player.sendMessage(e.getMessage());
        }
    }

    private void handleCancel(Player player, String[] args) {
        boolean cancelled;
        if (args.length >= 2) {
            try {
                int slot = Integer.parseInt(args[1]) - 1;
                cancelled = forgeManager.cancelForge(player.getUniqueId(), slot);
            } catch (NumberFormatException e) {
                player.sendMessage("Usage: /forge cancel [slot]");
                return;
            }
        } else {
            cancelled = forgeManager.cancelForge(player.getUniqueId());
        }
        if (cancelled) {
            player.sendMessage("Forge job cancelled.");
        } else {
            player.sendMessage("No active forge job to cancel.");
        }
    }

    private void handleList(Player player) {
        Map<Integer, ForgeJob> jobs = forgeManager.getActiveJobs(player.getUniqueId());
        if (jobs.isEmpty()) {
            player.sendMessage("You have no active forge jobs.");
            return;
        }
        long now = System.currentTimeMillis();
        player.sendMessage("=== Active Forge Jobs ===");
        for (Map.Entry<Integer, ForgeJob> entry : jobs.entrySet()) {
            ForgeJob job = entry.getValue();
            if (job.isComplete(now)) {
                player.sendMessage("Slot " + (entry.getKey() + 1) + ": " + job.getRecipe().getDisplayName() + " — Ready!");
            } else {
                long elapsed = (now - job.getStartTimeMillis()) / 1000L;
                long remaining = Math.max(0, job.getDurationSeconds() - elapsed);
                player.sendMessage("Slot " + (entry.getKey() + 1) + ": " + job.getRecipe().getDisplayName()
                        + " — " + formatDuration((int) remaining) + " remaining.");
            }
        }
    }

    private void sendHelp(Player player) {
        player.sendMessage("=== Forge Commands ===");
        player.sendMessage("/forge — open the forge menu");
        player.sendMessage("/forge start <recipe> — begin forging a recipe");
        player.sendMessage("/forge collect [slot] — collect a finished forge job");
        player.sendMessage("/forge cancel [slot] — cancel an active forge job");
        player.sendMessage("/forge list — list your active forge jobs");
    }

    private static String formatDuration(int seconds) {
        if (seconds <= 0) return "0s";
        int h = seconds / 3600;
        int m = (seconds % 3600) / 60;
        int s = seconds % 60;
        StringBuilder sb = new StringBuilder();
        if (h > 0) sb.append(h).append("h ");
        if (m > 0) sb.append(m).append("m ");
        if (s > 0 || sb.length() == 0) sb.append(s).append("s");
        return sb.toString().trim();
    }
}
