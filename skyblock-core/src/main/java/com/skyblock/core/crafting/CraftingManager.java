package com.skyblock.core.crafting;

import org.bukkit.Material;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

/**
 * Singleton facade over {@link SkyBlockRecipeManager} that also tracks each
 * player's crafting history (which recipe IDs they have crafted and how many
 * times).
 *
 * <p>Not thread-safe; synchronize externally if accessed from multiple threads.</p>
 */
public final class CraftingManager {

    private static final CraftingManager INSTANCE = new CraftingManager();

    private final SkyBlockRecipeManager recipeManager = SkyBlockRecipeManager.getInstance();

    /** Per-player map of recipe-id → craft count. */
    private final Map<UUID, Map<String, Integer>> craftHistory = new HashMap<>();

    private CraftingManager() {
        loadDefaultRecipes();
    }

    /**
     * Returns the single shared {@code CraftingManager} instance.
     *
     * @return the singleton instance
     */
    public static CraftingManager getInstance() {
        return INSTANCE;
    }

    // ---------------------------------------------------------------------------
    // Recipe access
    // ---------------------------------------------------------------------------

    /**
     * Returns an unmodifiable view of all registered recipes, keyed by id.
     *
     * @return all recipes
     */
    public Map<String, SkyBlockRecipeManager.SkyBlockRecipe> getAllRecipes() {
        return recipeManager.getAllRecipes();
    }

    /**
     * Returns the recipe registered under the given id, if any.
     *
     * @param id the recipe's unique id
     * @return the registered recipe, or empty
     */
    public Optional<SkyBlockRecipeManager.SkyBlockRecipe> getRecipe(String id) {
        Objects.requireNonNull(id, "id");
        return recipeManager.getRecipe(id);
    }

    // ---------------------------------------------------------------------------
    // Crafting history
    // ---------------------------------------------------------------------------

    /**
     * Records that the player crafted the given recipe once.
     *
     * @param playerId the player who crafted
     * @param recipeId the recipe that was crafted
     * @throws IllegalArgumentException if the recipe id is not registered
     */
    public void recordCraft(UUID playerId, String recipeId) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(recipeId, "recipeId");
        if (recipeManager.getRecipe(recipeId).isEmpty()) {
            throw new IllegalArgumentException("Unknown recipe: " + recipeId);
        }
        craftHistory.computeIfAbsent(playerId, id -> new HashMap<>())
                .merge(recipeId, 1, Integer::sum);
    }

    /**
     * Returns how many times the player has crafted the given recipe.
     *
     * @param playerId the player to look up
     * @param recipeId the recipe id to check
     * @return craft count, 0 if never crafted
     */
    public int getCraftCount(UUID playerId, String recipeId) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(recipeId, "recipeId");
        Map<String, Integer> history = craftHistory.get(playerId);
        return history == null ? 0 : history.getOrDefault(recipeId, 0);
    }

    /**
     * Returns an unmodifiable view of the player's crafting history
     * (recipe-id → craft count).
     *
     * @param playerId the player to look up
     * @return crafting history, empty map if none
     */
    public Map<String, Integer> getCraftHistory(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        Map<String, Integer> history = craftHistory.get(playerId);
        return history == null ? Collections.emptyMap() : Collections.unmodifiableMap(history);
    }

    /**
     * Removes all crafting history for the given player.
     *
     * @param playerId the player to reset
     * @return {@code true} if the player had any history, {@code false} otherwise
     */
    public boolean resetHistory(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        return craftHistory.remove(playerId) != null;
    }

    // ---------------------------------------------------------------------------
    // Default recipe registration
    // ---------------------------------------------------------------------------

    private void loadDefaultRecipes() {
        // Shaped recipes
        recipeManager.registerShaped("enchanted_iron_sword", Material.IRON_SWORD, 1,
                new String[]{"I", "I", "S"},
                Map.of('I', Material.IRON_INGOT, 'S', Material.STICK));

        recipeManager.registerShaped("iron_helmet", Material.IRON_HELMET, 1,
                new String[]{"III", "I I"},
                Map.of('I', Material.IRON_INGOT));

        recipeManager.registerShaped("iron_chestplate", Material.IRON_CHESTPLATE, 1,
                new String[]{"I I", "III", "III"},
                Map.of('I', Material.IRON_INGOT));

        recipeManager.registerShaped("iron_leggings", Material.IRON_LEGGINGS, 1,
                new String[]{"III", "I I", "I I"},
                Map.of('I', Material.IRON_INGOT));

        recipeManager.registerShaped("iron_boots", Material.IRON_BOOTS, 1,
                new String[]{"I I", "I I"},
                Map.of('I', Material.IRON_INGOT));

        recipeManager.registerShaped("iron_pickaxe", Material.IRON_PICKAXE, 1,
                new String[]{"III", " S ", " S "},
                Map.of('I', Material.IRON_INGOT, 'S', Material.STICK));

        recipeManager.registerShaped("iron_axe", Material.IRON_AXE, 1,
                new String[]{"II", "IS", " S"},
                Map.of('I', Material.IRON_INGOT, 'S', Material.STICK));

        recipeManager.registerShaped("iron_shovel", Material.IRON_SHOVEL, 1,
                new String[]{"I", "S", "S"},
                Map.of('I', Material.IRON_INGOT, 'S', Material.STICK));

        recipeManager.registerShaped("iron_hoe", Material.IRON_HOE, 1,
                new String[]{"II", " S", " S"},
                Map.of('I', Material.IRON_INGOT, 'S', Material.STICK));

        recipeManager.registerShaped("chest", Material.CHEST, 1,
                new String[]{"WWW", "W W", "WWW"},
                Map.of('W', Material.OAK_PLANKS));

        // Shapeless recipes
        recipeManager.registerShapeless("torch_x4", Material.TORCH, 4,
                List.of(Material.COAL, Material.STICK));

        recipeManager.registerShapeless("wooden_planks", Material.OAK_PLANKS, 4,
                List.of(Material.OAK_LOG));

        recipeManager.registerShapeless("paper_x3", Material.PAPER, 3,
                new ArrayList<>(List.of(Material.SUGAR_CANE, Material.SUGAR_CANE, Material.SUGAR_CANE)));

        recipeManager.registerShapeless("book", Material.BOOK, 1,
                List.of(Material.PAPER, Material.PAPER, Material.PAPER, Material.LEATHER));

        recipeManager.registerShapeless("glass_bottle", Material.GLASS_BOTTLE, 3,
                new ArrayList<>(List.of(Material.GLASS, Material.GLASS, Material.GLASS)));
    }
}
