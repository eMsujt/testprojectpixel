package com.skyblock.core.crafting;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Handles the {@code /crafting} command.
 *
 * <p>Subcommands:
 * <ul>
 *   <li>{@code /crafting list}          — list all available recipes</li>
 *   <li>{@code /crafting view <id>}     — view details of a specific recipe</li>
 *   <li>{@code /crafting history}       — show your crafting history</li>
 *   <li>{@code /crafting craft <id>}    — record crafting the specified recipe</li>
 * </ul>
 * </p>
 */
public final class CraftingCommand implements TabExecutor {

    private static final List<String> SUBCOMMANDS =
            Arrays.asList("list", "view", "history", "craft", "enchanted");

    private final CraftingManager craftingManager;

    public CraftingCommand(CraftingManager craftingManager) {
        this.craftingManager = craftingManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("This command can only be used by players.");
            return true;
        }

        if (args.length == 0) {
            player.sendMessage("Usage: /crafting <list|view|history|craft|enchanted>");
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "list"      -> handleList(player);
            case "view"      -> handleView(player, args);
            case "history"   -> handleHistory(player);
            case "craft"     -> handleCraft(player, args);
            case "enchanted" -> handleEnchanted(player);
            default          -> player.sendMessage("Unknown subcommand. Usage: /crafting <list|view|history|craft|enchanted>");
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
        if (args.length == 2 && (args[0].equalsIgnoreCase("view") || args[0].equalsIgnoreCase("craft"))) {
            String prefix = args[1].toLowerCase();
            return craftingManager.getAllRecipes().keySet().stream()
                    .filter(id -> id.startsWith(prefix))
                    .sorted()
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    private void handleList(Player player) {
        Map<String, SkyBlockRecipeManager.SkyBlockRecipe> recipes = craftingManager.getAllRecipes();
        if (recipes.isEmpty()) {
            player.sendMessage("No crafting recipes available.");
            return;
        }
        player.sendMessage("=== Crafting Recipes ===");
        recipes.values().stream()
                .sorted((a, b) -> a.id().compareTo(b.id()))
                .forEach(r -> player.sendMessage(String.format("[%s] %dx %s (%s)",
                        r.id(),
                        r.resultAmount(),
                        r.result().name(),
                        r instanceof SkyBlockRecipeManager.ShapedRecipe ? "shaped" : "shapeless")));
    }

    private void handleView(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage("Usage: /crafting view <id>");
            return;
        }
        Optional<SkyBlockRecipeManager.SkyBlockRecipe> opt = craftingManager.getRecipe(args[1].toLowerCase());
        if (opt.isEmpty()) {
            player.sendMessage("Unknown recipe: " + args[1] + ". Use /crafting list to see available recipes.");
            return;
        }
        SkyBlockRecipeManager.SkyBlockRecipe recipe = opt.get();
        player.sendMessage("=== Recipe: " + recipe.id() + " ===");
        player.sendMessage("Result: " + recipe.resultAmount() + "x " + recipe.result().name());
        if (recipe instanceof SkyBlockRecipeManager.ShapedRecipe shaped) {
            player.sendMessage("Type: Shaped");
            player.sendMessage("Shape:");
            for (String row : shaped.shape()) {
                player.sendMessage("  " + row);
            }
            player.sendMessage("Ingredients:");
            shaped.ingredientMap().forEach((ch, mat) ->
                    player.sendMessage("  '" + ch + "' = " + mat.name()));
        } else if (recipe instanceof SkyBlockRecipeManager.ShapelessRecipe shapeless) {
            player.sendMessage("Type: Shapeless");
            player.sendMessage("Ingredients: " + shapeless.ingredients().stream()
                    .map(Enum::name)
                    .collect(Collectors.joining(", ")));
        }
        int count = craftingManager.getCraftCount(player.getUniqueId(), recipe.id());
        player.sendMessage("Times crafted: " + count);
    }

    private void handleHistory(Player player) {
        Map<String, Integer> history = craftingManager.getCraftHistory(player.getUniqueId());
        if (history.isEmpty()) {
            player.sendMessage("You have not crafted any SkyBlock recipes yet.");
            return;
        }
        player.sendMessage("=== Your Crafting History ===");
        history.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .forEach(e -> player.sendMessage(e.getKey() + ": " + e.getValue() + "x"));
    }

    private void handleEnchanted(Player player) {
        player.sendMessage("=== SkyBlock Enchanted Recipes ===");
        for (CraftingManager.SkyblockRecipe recipe : CraftingManager.SkyblockRecipe.values()) {
            player.sendMessage(String.format("[%s] %s (requires %d)",
                    recipe.name(), recipe.getDisplayName(), recipe.getRequiredAmount()));
        }
    }

    private void handleCraft(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage("Usage: /crafting craft <id>");
            return;
        }
        String recipeId = args[1].toLowerCase();
        if (craftingManager.getRecipe(recipeId).isEmpty()) {
            player.sendMessage("Unknown recipe: " + recipeId + ". Use /crafting list to see available recipes.");
            return;
        }
        craftingManager.recordCraft(player.getUniqueId(), recipeId);
        SkyBlockRecipeManager.SkyBlockRecipe recipe = craftingManager.getRecipe(recipeId).get();
        player.sendMessage("Crafted " + recipe.resultAmount() + "x " + recipe.result().name()
                + "! (Total crafted: " + craftingManager.getCraftCount(player.getUniqueId(), recipeId) + ")");
    }
}
