package com.skyblock.core.crafting;

import org.bukkit.Material;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * Singleton registry of custom SkyBlock crafting recipes.
 *
 * <p>Supports both shaped (grid-positioned) and shapeless (order-independent)
 * recipes. Register on server start from the main thread only.</p>
 */
public final class SkyBlockRecipeManager {

    /**
     * Common contract for all SkyBlock crafting recipes.
     */
    public sealed interface SkyBlockRecipe permits ShapedRecipe, ShapelessRecipe {
        /** Unique identifier for this recipe. */
        String id();
        /** The {@link Material} produced when the recipe is crafted. */
        Material result();
        /** Number of result items produced; always &ge; 1. */
        int resultAmount();
    }

    /**
     * A grid-positioned recipe requiring ingredients to be placed in a specific
     * shape inside the crafting table.
     *
     * <p>{@code shape} must contain 1–3 strings, each 1–3 characters long.
     * Each character either maps to a {@link Material} in {@code ingredientMap}
     * or is a space (empty slot).</p>
     *
     * @param id            unique recipe id
     * @param result        the material produced
     * @param resultAmount  result item count, &ge; 1
     * @param shape         1–3 row strings of 1–3 characters each
     * @param ingredientMap mapping of each shape character to its {@link Material}
     */
    public record ShapedRecipe(
            String id,
            Material result,
            int resultAmount,
            String[] shape,
            Map<Character, Material> ingredientMap) implements SkyBlockRecipe {

        /** Validates and defensively copies mutable inputs. */
        public ShapedRecipe {
            Objects.requireNonNull(id, "id");
            if (id.isBlank()) {
                throw new IllegalArgumentException("id must not be blank");
            }
            Objects.requireNonNull(result, "result");
            if (resultAmount < 1) {
                throw new IllegalArgumentException(
                        "resultAmount must be >= 1, got " + resultAmount);
            }
            Objects.requireNonNull(shape, "shape");
            if (shape.length == 0 || shape.length > 3) {
                throw new IllegalArgumentException(
                        "shape must have 1–3 rows, got " + shape.length);
            }
            for (String row : shape) {
                Objects.requireNonNull(row, "shape row must not be null");
                if (row.isEmpty() || row.length() > 3) {
                    throw new IllegalArgumentException(
                            "each shape row must be 1–3 characters, got \"" + row + "\"");
                }
            }
            shape = shape.clone();
            Objects.requireNonNull(ingredientMap, "ingredientMap");
            ingredientMap = Map.copyOf(ingredientMap);
        }
    }

    /**
     * An order-independent recipe requiring a multiset of ingredients anywhere
     * in the crafting grid.
     *
     * @param id           unique recipe id
     * @param result       the material produced
     * @param resultAmount result item count, &ge; 1
     * @param ingredients  the required ingredients; may contain duplicates
     */
    public record ShapelessRecipe(
            String id,
            Material result,
            int resultAmount,
            List<Material> ingredients) implements SkyBlockRecipe {

        /** Validates and defensively copies the ingredients list. */
        public ShapelessRecipe {
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
            if (ingredients.isEmpty()) {
                throw new IllegalArgumentException("ingredients must not be empty");
            }
            ingredients = List.copyOf(ingredients);
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
     * Registers a shaped recipe.
     *
     * @param id            unique id for this recipe
     * @param result        the material produced
     * @param resultAmount  number of result items, &ge; 1
     * @param shape         1–3 row strings defining the crafting grid pattern
     * @param ingredientMap mapping of each shape character to its {@link Material}
     * @throws IllegalStateException if a recipe with the same id is already registered
     */
    public void registerShaped(String id, Material result, int resultAmount,
                                String[] shape, Map<Character, Material> ingredientMap) {
        register(new ShapedRecipe(id, result, resultAmount, shape, ingredientMap));
    }

    /**
     * Registers a shapeless recipe.
     *
     * @param id           unique id for this recipe
     * @param result       the material produced
     * @param resultAmount number of result items, &ge; 1
     * @param ingredients  the required ingredients (order-independent)
     * @throws IllegalStateException if a recipe with the same id is already registered
     */
    public void registerShapeless(String id, Material result, int resultAmount,
                                   List<Material> ingredients) {
        register(new ShapelessRecipe(id, result, resultAmount, new ArrayList<>(ingredients)));
    }

    /**
     * Registers a pre-built recipe.
     *
     * @param recipe the recipe to register
     * @throws IllegalStateException if a recipe with the same id is already registered
     */
    public void registerRecipe(SkyBlockRecipe recipe) {
        Objects.requireNonNull(recipe, "recipe");
        register(recipe);
    }

    private void register(SkyBlockRecipe recipe) {
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
