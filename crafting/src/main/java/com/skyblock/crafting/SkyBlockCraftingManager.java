package com.skyblock.crafting;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

/**
 * Registry of {@link CraftingRecipe} definitions with craftability checks.
 *
 * <p>Recipes are identified by their recipe id. Unregistered recipes cannot be
 * crafted. Not thread-safe; synchronize externally if accessed from multiple
 * threads.</p>
 */
public final class SkyBlockCraftingManager {

    private final Map<String, CraftingRecipe> recipes = new HashMap<>();

    /**
     * Registers a recipe, or replaces an existing one with the same id.
     *
     * @param recipe the recipe to register
     * @throws NullPointerException if {@code recipe} is {@code null}
     */
    public void registerRecipe(CraftingRecipe recipe) {
        Objects.requireNonNull(recipe, "recipe");
        recipes.put(recipe.getRecipeId(), recipe);
    }

    /**
     * Removes a recipe from the registry.
     *
     * @param recipeId the recipe identifier
     * @return {@code true} if the recipe was registered and has been removed
     */
    public boolean unregisterRecipe(String recipeId) {
        return recipes.remove(recipeId) != null;
    }

    /**
     * Returns the recipe registered under the given id, if any.
     *
     * @param recipeId the recipe identifier
     * @return the recipe, or an empty optional if none is registered
     */
    public Optional<CraftingRecipe> getRecipe(String recipeId) {
        return Optional.ofNullable(recipes.get(recipeId));
    }

    /**
     * Returns whether a recipe is registered under the given id.
     *
     * @param recipeId the recipe identifier
     * @return {@code true} if the recipe is registered
     */
    public boolean isRegistered(String recipeId) {
        return recipes.containsKey(recipeId);
    }

    /**
     * Returns whether the given items satisfy all ingredients of a recipe.
     *
     * @param recipeId       the recipe identifier
     * @param availableItems the available items as item identifier to amount
     * @return {@code true} if every ingredient is present in sufficient amount
     * @throws IllegalArgumentException if the recipe is not registered
     * @throws NullPointerException if {@code availableItems} is {@code null}
     */
    public boolean canCraft(String recipeId, Map<String, Integer> availableItems) {
        Objects.requireNonNull(availableItems, "availableItems");
        CraftingRecipe recipe = requireRecipe(recipeId);
        for (Map.Entry<String, Integer> ingredient : recipe.getIngredients().entrySet()) {
            int available = availableItems.getOrDefault(ingredient.getKey(), 0);
            if (available < ingredient.getValue()) {
                return false;
            }
        }
        return true;
    }

    /**
     * Crafts a recipe by consuming its ingredients from the given items.
     *
     * <p>The {@code availableItems} map is mutated in place: ingredient amounts
     * are subtracted and entries that reach zero are removed.</p>
     *
     * @param recipeId       the recipe identifier
     * @param availableItems the available items as item identifier to amount,
     *                       mutated on success
     * @return the crafted recipe
     * @throws IllegalArgumentException if the recipe is not registered or the
     *         items do not satisfy its ingredients
     * @throws NullPointerException if {@code availableItems} is {@code null}
     */
    public CraftingRecipe craft(String recipeId, Map<String, Integer> availableItems) {
        if (!canCraft(recipeId, availableItems)) {
            throw new IllegalArgumentException("missing ingredients for recipe: " + recipeId);
        }
        CraftingRecipe recipe = requireRecipe(recipeId);
        for (Map.Entry<String, Integer> ingredient : recipe.getIngredients().entrySet()) {
            int remaining = availableItems.get(ingredient.getKey()) - ingredient.getValue();
            if (remaining == 0) {
                availableItems.remove(ingredient.getKey());
            } else {
                availableItems.put(ingredient.getKey(), remaining);
            }
        }
        return recipe;
    }

    /**
     * Returns the ids of all registered recipes.
     *
     * @return an unmodifiable view of the registered recipe ids
     */
    public Set<String> getRecipeIds() {
        return Collections.unmodifiableSet(recipes.keySet());
    }

    private CraftingRecipe requireRecipe(String recipeId) {
        CraftingRecipe recipe = recipes.get(recipeId);
        if (recipe == null) {
            throw new IllegalArgumentException("recipe is not registered: " + recipeId);
        }
        return recipe;
    }
}
