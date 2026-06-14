package com.skyblock.plugin.commands;

import com.skyblock.core.alchemy.AlchemyManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;

public final class AlchemyCommand implements CommandExecutor {

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
            case "level"   -> handleLevel(player);
            case "brew"    -> handleBrew(player, args);
            case "status"  -> handleStatus(player);
            case "cancel"  -> handleCancel(player);
            case "recipes" -> handleRecipes(player);
            default        -> sendHelp(player);
        }
        return true;
    }

    private void handleLevel(Player player) {
        UUID id = player.getUniqueId();
        AlchemyManager manager = AlchemyManager.getInstance();
        int level = manager.getLevel(id);
        double xp = manager.getXp(id);
        player.sendMessage("=== Alchemy ===");
        player.sendMessage("Level: " + level + "  XP: " + String.format("%.1f", xp));
    }

    private void handleBrew(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage("Usage: /skyblock alchemy brew <recipe_id>");
            return;
        }
        String recipeId = args[1].toLowerCase();
        AlchemyManager manager = AlchemyManager.getInstance();
        if (manager.getRecipe(recipeId) == null) {
            player.sendMessage("Unknown recipe '" + recipeId + "'. Use /skyblock alchemy recipes to see available recipes.");
            return;
        }
        try {
            manager.startBrew(player.getUniqueId(), recipeId, System.currentTimeMillis());
            AlchemyManager.PotionRecipe recipe = manager.getRecipe(recipeId);
            player.sendMessage("Brewing " + recipe.getDisplayName() + " — completes in " + recipe.getDurationSeconds() + "s.");
        } catch (IllegalStateException e) {
            player.sendMessage("You already have an active brew. Use /skyblock alchemy cancel to stop it.");
        }
    }

    private void handleStatus(Player player) {
        AlchemyManager manager = AlchemyManager.getInstance();
        AlchemyManager.BrewJob job = manager.getActiveJob(player.getUniqueId());
        if (job == null) {
            player.sendMessage("You have no active brew.");
            return;
        }
        long elapsed = System.currentTimeMillis() - job.getStartTimeMillis();
        long total = (long) job.getRecipe().getDurationSeconds() * 1000L;
        long remaining = Math.max(0L, total - elapsed);
        if (remaining == 0) {
            player.sendMessage("Brewing " + job.getRecipe().getDisplayName() + " — ready to collect!");
        } else {
            player.sendMessage("Brewing " + job.getRecipe().getDisplayName() + " — " + (remaining / 1000L) + "s remaining.");
        }
    }

    private void handleCancel(Player player) {
        boolean cancelled = AlchemyManager.getInstance().cancelBrew(player.getUniqueId());
        if (cancelled) {
            player.sendMessage("Brew cancelled.");
        } else {
            player.sendMessage("You have no active brew to cancel.");
        }
    }

    private void handleRecipes(Player player) {
        Map<String, AlchemyManager.PotionRecipe> recipes = AlchemyManager.getInstance().getRecipes();
        player.sendMessage("=== Alchemy Recipes ===");
        for (AlchemyManager.PotionRecipe recipe : recipes.values()) {
            player.sendMessage("  " + recipe.getId() + " — " + recipe.getDisplayName()
                    + " (" + recipe.getDurationSeconds() + "s, " + recipe.getXpReward() + " XP)");
        }
    }

    private void sendHelp(Player player) {
        player.sendMessage("=== Alchemy Commands ===");
        player.sendMessage("/skyblock alchemy level            — show your alchemy level and XP");
        player.sendMessage("/skyblock alchemy brew <recipe_id> — start brewing a potion");
        player.sendMessage("/skyblock alchemy status           — check your active brew");
        player.sendMessage("/skyblock alchemy cancel           — cancel your active brew");
        player.sendMessage("/skyblock alchemy recipes          — list all available recipes");
    }
}
