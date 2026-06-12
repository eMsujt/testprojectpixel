package com.skyblock.crafting;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * Registry of {@link CustomRecipe} definitions keyed by recipe id, with
 * craftability checks and ingredient-consuming crafting.
 *
 * <p>Recipes are created and registered through
 * {@link #registerRecipe(String, String, int, Map)}. Unregistered recipes
 * cannot be crafted. Not thread-safe; synchronize externally if accessed from
 * multiple threads.</p>
 */
public final class CraftingManager {

    /**
     * A single custom recipe: a set of ingredients (item identifier to
     * required amount) mapped to an output item produced in a given quantity.
     *
     * <p>Instances are immutable and created only through
     * {@link CraftingManager#registerRecipe(String, String, int, Map)}.</p>
     */
    public static final class CustomRecipe {

        private final String recipeId;
        private final String outputItemId;
        private final int outputAmount;
        private final Map<String, Integer> ingredients;

        private CustomRecipe(String recipeId, String outputItemId, int outputAmount,
                             Map<String, Integer> ingredients) {
            this.recipeId = recipeId;
            this.outputItemId = outputItemId;
            this.outputAmount = outputAmount;
            this.ingredients = ingredients;
        }

        /**
         * Returns the unique identifier of the recipe.
         *
         * @return the recipe identifier
         */
        public String getRecipeId() {
            return recipeId;
        }

        /**
         * Returns the identifier of the item produced by the recipe.
         *
         * @return the output item identifier
         */
        public String getOutputItemId() {
            return outputItemId;
        }

        /**
         * Returns the number of output items produced.
         *
         * @return the output amount, always positive
         */
        public int getOutputAmount() {
            return outputAmount;
        }

        /**
         * Returns the required ingredients as item identifier to amount.
         *
         * @return an unmodifiable map of ingredients, never empty
         */
        public Map<String, Integer> getIngredients() {
            return ingredients;
        }

        @Override
        public String toString() {
            return "CustomRecipe{recipeId=" + recipeId + ", outputItemId=" + outputItemId
                    + ", outputAmount=" + outputAmount + ", ingredients=" + ingredients + '}';
        }
    }

    private final Map<String, CustomRecipe> recipeRegistry = new HashMap<>();

    /**
     * Creates and registers a recipe, replacing any existing one with the
     * same id.
     *
     * @param recipeId     the unique identifier of the recipe
     * @param outputItemId the identifier of the item produced by the recipe
     * @param outputAmount the number of output items produced, must be positive
     * @param ingredients  the required ingredients as item identifier to amount,
     *                     must not be empty and all amounts must be positive
     * @return the registered recipe
     * @throws IllegalArgumentException if {@code outputAmount} is not positive,
     *         {@code ingredients} is empty or contains a non-positive amount
     * @throws NullPointerException if {@code recipeId}, {@code outputItemId} or
     *         {@code ingredients} is {@code null}
     */
    public CustomRecipe registerRecipe(String recipeId, String outputItemId, int outputAmount,
                                       Map<String, Integer> ingredients) {
        Objects.requireNonNull(recipeId, "recipeId");
        Objects.requireNonNull(outputItemId, "outputItemId");
        if (outputAmount <= 0) {
            throw new IllegalArgumentException("outputAmount must be positive, got " + outputAmount);
        }
        Objects.requireNonNull(ingredients, "ingredients");
        if (ingredients.isEmpty()) {
            throw new IllegalArgumentException("ingredients must not be empty");
        }
        for (Map.Entry<String, Integer> entry : ingredients.entrySet()) {
            Objects.requireNonNull(entry.getKey(), "ingredient item id");
            Objects.requireNonNull(entry.getValue(), "ingredient amount");
            if (entry.getValue() <= 0) {
                throw new IllegalArgumentException(
                        "ingredient amount must be positive, got " + entry.getValue()
                                + " for " + entry.getKey());
            }
        }
        CustomRecipe recipe = new CustomRecipe(recipeId, outputItemId, outputAmount,
                Map.copyOf(ingredients));
        recipeRegistry.put(recipeId, recipe);
        return recipe;
    }

    /**
     * Removes a recipe from the registry.
     *
     * @param recipeId the recipe identifier
     * @return {@code true} if the recipe was registered and has been removed
     */
    public boolean unregisterRecipe(String recipeId) {
        return recipeRegistry.remove(recipeId) != null;
    }

    /**
     * Returns the recipe registered under the given id.
     *
     * @param recipeId the recipe identifier
     * @return the recipe, or {@code null} if none is registered
     */
    public CustomRecipe getRecipe(String recipeId) {
        return recipeRegistry.get(recipeId);
    }

    /**
     * Returns whether a recipe is registered under the given id.
     *
     * @param recipeId the recipe identifier
     * @return {@code true} if the recipe is registered
     */
    public boolean hasRecipe(String recipeId) {
        return recipeRegistry.containsKey(recipeId);
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
        CustomRecipe recipe = requireRecipe(recipeId);
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
    public CustomRecipe craft(String recipeId, Map<String, Integer> availableItems) {
        if (!canCraft(recipeId, availableItems)) {
            throw new IllegalArgumentException("missing ingredients for recipe: " + recipeId);
        }
        CustomRecipe recipe = requireRecipe(recipeId);
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
        return Collections.unmodifiableSet(recipeRegistry.keySet());
    }

    private CustomRecipe requireRecipe(String recipeId) {
        CustomRecipe recipe = recipeRegistry.get(recipeId);
        if (recipe == null) {
            throw new IllegalArgumentException("recipe is not registered: " + recipeId);
        }
        return recipe;
    }
}
