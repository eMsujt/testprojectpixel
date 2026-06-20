package com.skyblock.core.alchemy;

import com.skyblock.core.manager.AlchemyManager;
import com.skyblock.core.menu.AlchemyMenu;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Handles the {@code /alchemy} command.
 *
 * <p>Subcommands:
 * <ul>
 *   <li>{@code /alchemy list}             — list all available potion recipes</li>
 *   <li>{@code /alchemy brew <recipe>}    — begin brewing a recipe</li>
 *   <li>{@code /alchemy status}           — check progress of the active brew</li>
 *   <li>{@code /alchemy collect}          — collect a completed brew</li>
 *   <li>{@code /alchemy cancel}           — cancel the active brew</li>
 * </ul>
 * </p>
 */
public final class AlchemyCommand implements TabExecutor {

    private static final List<String> SUBCOMMANDS =
            Arrays.asList("list", "brew", "status", "collect", "cancel");

    private final AlchemyManager alchemyManager;

    public AlchemyCommand(AlchemyManager alchemyManager) {
        this.alchemyManager = alchemyManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("This command can only be used by players.");
            return true;
        }

        if (args.length == 0) {
            new AlchemyMenu(player).open(player);
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "list"    -> handleList(player);
            case "brew"    -> handleBrew(player, args);
            case "status"  -> handleStatus(player);
            case "collect" -> handleCollect(player);
            case "cancel"  -> handleCancel(player);
            default        -> player.sendMessage("Unknown subcommand. Usage: /alchemy <list|brew|status|collect|cancel>");
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
        if (args.length == 2 && args[0].equalsIgnoreCase("brew")) {
            String prefix = args[1].toLowerCase();
            return alchemyManager.getRecipes().keySet().stream()
                    .filter(id -> id.startsWith(prefix))
                    .sorted()
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    private void handleList(Player player) {
        if (alchemyManager.getRecipes().isEmpty()) {
            player.sendMessage("No alchemy recipes available.");
            return;
        }
        player.sendMessage("=== Alchemy Recipes ===");
        alchemyManager.getRecipes().values().stream()
                .sorted((a, b) -> a.getId().compareTo(b.getId()))
                .forEach(recipe -> {
                    String ingredients = recipe.getIngredients().entrySet().stream()
                            .map(e -> e.getValue() + "x " + e.getKey())
                            .collect(Collectors.joining(", "));
                    player.sendMessage(String.format("[%s] %s -> %dx %s (%ds, %.0f XP) | Needs: %s",
                            recipe.getId(),
                            recipe.getDisplayName(),
                            recipe.getOutputAmount(),
                            recipe.getOutputPotion(),
                            recipe.getDurationSeconds(),
                            recipe.getXpReward(),
                            ingredients));
                });
    }

    private void handleBrew(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage("Usage: /alchemy brew <recipe>");
            return;
        }
        String recipeId = args[1].toLowerCase();
        if (alchemyManager.getRecipe(recipeId) == null) {
            player.sendMessage("Unknown recipe: " + recipeId + ". Use /alchemy list to see available recipes.");
            return;
        }
        try {
            alchemyManager.startBrew(player.getUniqueId(), recipeId, System.currentTimeMillis());
            AlchemyManager.PotionRecipe recipe = alchemyManager.getRecipe(recipeId);
            player.sendMessage("Started brewing: " + recipe.getDisplayName()
                    + ". Time remaining: " + formatDuration(recipe.getDurationSeconds()));
        } catch (IllegalStateException e) {
            player.sendMessage("You already have an active brew. Use /alchemy status to check it.");
        }
    }

    private void handleStatus(Player player) {
        AlchemyManager.BrewJob job = alchemyManager.getActiveJob(player.getUniqueId());
        if (job == null) {
            player.sendMessage("You have no active brew. Use /alchemy brew <recipe> to begin one.");
            return;
        }
        long nowMillis = System.currentTimeMillis();
        if (job.isComplete(nowMillis)) {
            player.sendMessage("Brew complete: " + job.getRecipe().getDisplayName()
                    + " is ready! Use /alchemy collect to claim it.");
        } else {
            long elapsedSeconds = (nowMillis - job.getStartTimeMillis()) / 1000L;
            long remaining = job.getRecipe().getDurationSeconds() - elapsedSeconds;
            player.sendMessage("Brewing: " + job.getRecipe().getDisplayName()
                    + " — " + formatDuration((int) Math.max(0, remaining)) + " remaining.");
        }
    }

    private void handleCollect(Player player) {
        try {
            AlchemyManager.BrewJob job = alchemyManager.collectBrew(player.getUniqueId(), System.currentTimeMillis());
            player.sendMessage("Collected " + job.getRecipe().getOutputAmount() + "x "
                    + job.getRecipe().getOutputPotion() + " from brewing "
                    + job.getRecipe().getDisplayName() + "!");
        } catch (IllegalStateException e) {
            player.sendMessage(e.getMessage().contains("not yet complete")
                    ? "Your brew is not complete yet. Use /alchemy status to check progress."
                    : "You have no active brew.");
        }
    }

    private void handleCancel(Player player) {
        boolean cancelled = alchemyManager.cancelBrew(player.getUniqueId());
        if (cancelled) {
            player.sendMessage("Your brew has been cancelled. Ingredients are not refunded.");
        } else {
            player.sendMessage("You have no active brew to cancel.");
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
