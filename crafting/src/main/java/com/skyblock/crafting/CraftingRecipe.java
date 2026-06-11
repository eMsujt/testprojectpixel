package com.skyblock.crafting;

import java.util.Map;
import java.util.Objects;

/**
 * Data holder for a single crafting recipe.
 *
 * <p>Maps a set of ingredients (item identifier to required amount) to an
 * output item produced in a given quantity. Instances are immutable and
 * therefore safe to share between threads.</p>
 */
public final class CraftingRecipe {

    private final String recipeId;
    private final String outputItemId;
    private final int outputAmount;
    private final Map<String, Integer> ingredients;

    /**
     * Creates a new crafting recipe.
     *
     * @param recipeId     the unique identifier of the recipe
     * @param outputItemId the identifier of the item produced by the recipe
     * @param outputAmount the number of output items produced, must be positive
     * @param ingredients  the required ingredients as item identifier to amount,
     *                     must not be empty and all amounts must be positive
     * @throws IllegalArgumentException if {@code outputAmount} is not positive,
     *         {@code ingredients} is empty or contains a non-positive amount
     * @throws NullPointerException if {@code recipeId}, {@code outputItemId} or
     *         {@code ingredients} is {@code null}
     */
    public CraftingRecipe(String recipeId, String outputItemId, int outputAmount,
                          Map<String, Integer> ingredients) {
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
        this.recipeId = Objects.requireNonNull(recipeId, "recipeId");
        this.outputItemId = Objects.requireNonNull(outputItemId, "outputItemId");
        this.outputAmount = outputAmount;
        this.ingredients = Map.copyOf(ingredients);
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

    /**
     * Returns the required amount of the given ingredient.
     *
     * @param itemId the identifier of the ingredient item
     * @return the required amount, or {@code 0} if the recipe does not use it
     */
    public int getRequiredAmount(String itemId) {
        return ingredients.getOrDefault(itemId, 0);
    }

    @Override
    public String toString() {
        return "CraftingRecipe{recipeId=" + recipeId + ", outputItemId=" + outputItemId
                + ", outputAmount=" + outputAmount + ", ingredients=" + ingredients + '}';
    }
}
