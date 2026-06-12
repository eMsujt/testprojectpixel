package com.skyblock.core.crafting;

import org.bukkit.Material;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * Singleton registry of custom SkyBlock crafting recipes.
 *
 * <p>Recipes are keyed by their unique string id. Not thread-safe;
 * register recipes on server start from the main thread only.</p>
 */
public final class SkyBlockRecipeManager {

    /**
     * Immutable descriptor for a single SkyBlock crafting recipe.
     *
     * @param id           unique string identifier for this recipe
     * @param result       the {@link Material} produced
     * @param resultAmount how many items the craft produces, must be &ge; 1
     * @param ingredients  map of ingredient {@link Material} to required count
     */
    public record SkyBlockRecipe(
            String id,
            Material result,
            int resultAmount,
            Map<Material, Integer> ingredients) {

        /** Validates fields and copies the ingredients map defensively. */
        public SkyBlockRecipe {
            Objects.requireNonNull(id, "id");
            if (id.isBlank()) {
                throw new IllegalArgumentException("id must not be blank");
            }
            Objects.requireNonNull(result, "result");
            if (resultAmount < 1) {
                throw new IllegalArgumentException(
                        "resultAmount must be >= 1, got " + resultAmount);
            }
            Objects.requireNonNull(ingredients, "ingredients");
            ingredients = Map.copyOf(ingredients);
        }
    }

    private static final SkyBlockRecipeManager INSTANCE = new SkyBlockRecipeManager();

    private final Map<String, SkyBlockRecipe> recipes = new HashMap<>();

    private SkyBlockRecipeManager() {
    }

    /**
     * Returns the single shared {@code SkyBlockRecipeManager} instance.
     *
     * @return the singleton instance
     */
    public static SkyBlockRecipeManager getInstance() {
        return INSTANCE;
    }

    /**
     * Registers a recipe.
     *
     * @param recipe the recipe to register
     * @throws IllegalStateException if a recipe with the same id is already registered
     */
    public void registerRecipe(SkyBlockRecipe recipe) {
        Objects.requireNonNull(recipe, "recipe");
        if (recipes.containsKey(recipe.id())) {
            throw new IllegalStateException(
                    "Recipe already registered for id: " + recipe.id());
        }
        recipes.put(recipe.id(), recipe);
    }

    /**
     * Returns the recipe registered under the given id, if any.
     *
     * @param id the recipe's unique id
     * @return the registered recipe, or empty
     */
    public Optional<SkyBlockRecipe> getRecipe(String id) {
        Objects.requireNonNull(id, "id");
        return Optional.ofNullable(recipes.get(id));
    }

    /**
     * Returns an unmodifiable view of all registered recipes, keyed by id.
     *
     * @return all registered recipes
     */
    public Map<String, SkyBlockRecipe> getAllRecipes() {
        return Collections.unmodifiableMap(recipes);
    }

    /**
     * Removes a registered recipe.
     *
     * @param id the id of the recipe to remove
     * @return {@code true} if a recipe was removed, {@code false} if none was found
     */
    public boolean removeRecipe(String id) {
        Objects.requireNonNull(id, "id");
        return recipes.remove(id) != null;
    }
}
