package com.skyblock.core.forge;

import com.skyblock.core.manager.ForgeManager;
import com.skyblock.core.menu.ForgeMenu;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Handles the {@code /forge} command.
 *
 * <p>Subcommands:
 * <ul>
 *   <li>{@code /forge list}            — list all available forge recipes</li>
 *   <li>{@code /forge start <recipe>}  — begin forging a recipe</li>
 *   <li>{@code /forge status}          — check progress of the active forge job</li>
 *   <li>{@code /forge collect}         — collect a completed forge job</li>
 *   <li>{@code /forge cancel}          — cancel the active forge job</li>
 * </ul>
 * </p>
 */
public final class ForgeCommand implements TabExecutor {

    private static final List<String> SUBCOMMANDS =
            Arrays.asList("list", "start", "status", "collect", "cancel");

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
            case "list"    -> handleList(player);
            case "start"   -> handleStart(player, args);
            case "status"  -> handleStatus(player);
            case "collect" -> handleCollect(player);
            case "cancel"  -> handleCancel(player);
            default        -> player.sendMessage("Unknown subcommand. Usage: /forge <list|start|status|collect|cancel>");
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
            return forgeManager.getRecipes().keySet().stream()
                    .filter(id -> id.startsWith(prefix))
                    .sorted()
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    private void handleList(Player player) {
        if (forgeManager.getRecipes().isEmpty()) {
            player.sendMessage("No forge recipes available.");
            return;
        }
        player.sendMessage("=== Forge Recipes ===");
        forgeManager.getRecipes().values().stream()
                .sorted((a, b) -> a.name().compareTo(b.name()))
                .forEach(recipe -> {
                    String ingredients = recipe.getIngredients().entrySet().stream()
                            .map(e -> e.getValue() + "x " + e.getKey())
                            .collect(Collectors.joining(", "));
                    player.sendMessage(String.format("[%s] %s -> %dx %s (%ds) | Needs: %s",
                            recipe.name().toLowerCase(),
                            recipe.getDisplayName(),
                            recipe.getOutputAmount(),
                            recipe.getOutputItem(),
                            recipe.getDurationSeconds(),
                            ingredients));
                });
    }

    private void handleStart(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage("Usage: /forge start <recipe>");
            return;
        }
        String recipeId = args[1].toLowerCase();
        if (forgeManager.getRecipe(recipeId) == null) {
            player.sendMessage("Unknown recipe: " + recipeId + ". Use /forge list to see available recipes.");
            return;
        }
        try {
            forgeManager.startForge(player.getUniqueId(), recipeId, System.currentTimeMillis());
            ForgeManager.ForgeRecipe recipe = forgeManager.getRecipe(recipeId);
            player.sendMessage("Started forging: " + recipe.getDisplayName()
                    + ". Time remaining: " + formatDuration(recipe.getDurationSeconds()));
        } catch (IllegalStateException e) {
            player.sendMessage("You already have an active forge job. Use /forge status to check it.");
        }
    }

    private void handleStatus(Player player) {
        ForgeManager.ForgeJob job = forgeManager.getActiveJob(player.getUniqueId());
        if (job == null) {
            player.sendMessage("You have no active forge job. Use /forge start <recipe> to begin one.");
            return;
        }
        long nowMillis = System.currentTimeMillis();
        if (job.isComplete(nowMillis)) {
            player.sendMessage("Forge complete: " + job.getRecipe().getDisplayName()
                    + " is ready! Use /forge collect to claim it.");
        } else {
            long elapsedSeconds = (nowMillis - job.getStartTimeMillis()) / 1000L;
            long remaining = job.getRecipe().getDurationSeconds() - elapsedSeconds;
            player.sendMessage("Forging: " + job.getRecipe().getDisplayName()
                    + " — " + formatDuration((int) Math.max(0, remaining)) + " remaining.");
        }
    }

    private void handleCollect(Player player) {
        try {
            ForgeManager.ForgeJob job = forgeManager.collectForge(player.getUniqueId(), System.currentTimeMillis());
            player.sendMessage("Collected " + job.getRecipe().getOutputAmount() + "x "
                    + job.getRecipe().getOutputItem() + " from forging "
                    + job.getRecipe().getDisplayName() + "!");
        } catch (IllegalStateException e) {
            player.sendMessage(e.getMessage().contains("not yet complete")
                    ? "Your forge job is not complete yet. Use /forge status to check progress."
                    : "You have no active forge job.");
        }
    }

    private void handleCancel(Player player) {
        boolean cancelled = forgeManager.cancelForge(player.getUniqueId());
        if (cancelled) {
            player.sendMessage("Your forge job has been cancelled. Ingredients are not refunded.");
        } else {
            player.sendMessage("You have no active forge job to cancel.");
        }
    }

    /** Formats a duration in seconds as a human-readable string (e.g. "2h 30m 15s"). */
    private static String formatDuration(int seconds) {
        if (seconds <= 0) {
            return "0s";
        }
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
