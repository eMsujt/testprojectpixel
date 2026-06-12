package com.skyblock.core.forge;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Singleton registry for SkyBlock forge recipes.
 *
 * <p>Decouples recipe catalogue management from active-job tracking, which lives in
 * {@link ForgeManager}. Recipes can be registered and unregistered at runtime,
 * allowing plugins to contribute recipes without modifying {@link ForgeManager}.</p>
 *
 * <p>Not thread-safe; synchronize externally if needed.</p>
 */
public final class ForgeRecipeManager {

    /** A recipe that can be forged at the Forge. */
    public static final class ForgeRecipe {
        private final String id;
        private final String displayName;
        /** Ingredient name → required quantity. */
        private final Map<String, Integer> ingredients;
        private final String outputItem;
        private final int outputAmount;
        /** Forge duration in seconds. */
        private final int durationSeconds;

        public ForgeRecipe(String id, String displayName,
                           Map<String, Integer> ingredients,
                           String outputItem, int outputAmount,
                           int durationSeconds) {
            this.id = Objects.requireNonNull(id, "id");
            this.displayName = Objects.requireNonNull(displayName, "displayName");
            this.ingredients = Collections.unmodifiableMap(new HashMap<>(ingredients));
            this.outputItem = Objects.requireNonNull(outputItem, "outputItem");
            this.outputAmount = outputAmount;
            this.durationSeconds = durationSeconds;
        }

        public String getId() { return id; }
        public String getDisplayName() { return displayName; }
        public Map<String, Integer> getIngredients() { return ingredients; }
        public String getOutputItem() { return outputItem; }
        public int getOutputAmount() { return outputAmount; }
        public int getDurationSeconds() { return durationSeconds; }
    }

    // ---------------------------------------------------------------------------
    // Singleton
    // ---------------------------------------------------------------------------

    private static final ForgeRecipeManager INSTANCE = new ForgeRecipeManager();

    /** Mutable recipe catalogue keyed by recipe ID. */
    private final Map<String, ForgeRecipe> recipes = new HashMap<>();

    private ForgeRecipeManager() {
        seedDefaultRecipes();
    }

    /**
     * Returns the single shared {@code ForgeRecipeManager} instance.
     *
     * @return the singleton instance
     */
    public static ForgeRecipeManager getInstance() {
        return INSTANCE;
    }

    // ---------------------------------------------------------------------------
    // Recipe registration
    // ---------------------------------------------------------------------------

    /**
     * Registers a recipe, replacing any existing recipe with the same ID.
     *
     * @param recipe the recipe to register (must not be {@code null})
     */
    public void registerRecipe(ForgeRecipe recipe) {
        Objects.requireNonNull(recipe, "recipe");
        recipes.put(recipe.getId(), recipe);
    }

    /**
     * Unregisters the recipe with the given ID.
     *
     * @param id recipe identifier (case-sensitive)
     * @return {@code true} if a recipe was removed, {@code false} if it did not exist
     */
    public boolean unregisterRecipe(String id) {
        return recipes.remove(id) != null;
    }

    /**
     * Returns whether a recipe with the given ID is registered.
     *
     * @param id recipe identifier (case-sensitive)
     * @return {@code true} if a matching recipe exists
     */
    public boolean hasRecipe(String id) {
        return recipes.containsKey(id);
    }

    // ---------------------------------------------------------------------------
    // Recipe access
    // ---------------------------------------------------------------------------

    /**
     * Returns the recipe with the given ID, or {@code null} if unknown.
     *
     * @param id recipe identifier (case-sensitive)
     * @return matching {@link ForgeRecipe}, or {@code null}
     */
    public ForgeRecipe getRecipe(String id) {
        return recipes.get(id);
    }

    /**
     * Returns an unmodifiable view of all registered recipes, keyed by ID.
     *
     * @return the recipe catalogue
     */
    public Map<String, ForgeRecipe> getRecipes() {
        return Collections.unmodifiableMap(recipes);
    }

    /**
     * Returns an unmodifiable collection of all registered recipes.
     *
     * @return all registered {@link ForgeRecipe} values
     */
    public Collection<ForgeRecipe> getAllRecipes() {
        return Collections.unmodifiableCollection(recipes.values());
    }

    // ---------------------------------------------------------------------------
    // Default recipe catalogue
    // ---------------------------------------------------------------------------

    private void seedDefaultRecipes() {
        register("mithril_pickaxe", "Mithril Pickaxe",
                Map.of("MITHRIL_ORE", 20, "IRON_INGOT", 8),
                "MITHRIL_PICKAXE", 1, 3600);

        register("titanium_drill", "Titanium Drill",
                Map.of("TITANIUM", 15, "MITHRIL_PICKAXE", 1, "COAL", 64),
                "TITANIUM_DRILL", 1, 14400);

        register("refined_mithril", "Refined Mithril",
                Map.of("MITHRIL_ORE", 10),
                "REFINED_MITHRIL", 1, 900);

        register("refined_titanium", "Refined Titanium",
                Map.of("TITANIUM", 5),
                "REFINED_TITANIUM", 1, 1800);

        register("fuel_tank", "Fuel Tank",
                Map.of("REFINED_MITHRIL", 5, "IRON_INGOT", 16),
                "FUEL_TANK", 1, 7200);

        register("mithril_plate", "Mithril Plate",
                Map.of("REFINED_MITHRIL", 8),
                "MITHRIL_PLATE", 1, 3600);

        register("tungsten_key", "Tungsten Key",
                Map.of("TUNGSTEN", 3, "IRON_INGOT", 4),
                "TUNGSTEN_KEY", 1, 600);

        register("gemstone_gauntlet", "Gemstone Gauntlet",
                Map.of("RUBY_GEMSTONE", 5, "SAPPHIRE_GEMSTONE", 5, "REFINED_MITHRIL", 4),
                "GEMSTONE_GAUNTLET", 1, 28800);

        register("dragon_scale", "Dragon Scale",
                Map.of("DRAGON_FRAGMENT", 12, "REFINED_TITANIUM", 3),
                "DRAGON_SCALE", 1, 86400);

        register("mithril_blade", "Mithril Blade",
                Map.of("REFINED_MITHRIL", 6, "IRON_INGOT", 5),
                "MITHRIL_BLADE", 1, 7200);
    }

    private void register(String id, String displayName, Map<String, Integer> ingredients,
                          String outputItem, int outputAmount, int durationSeconds) {
        recipes.put(id, new ForgeRecipe(id, displayName, ingredients, outputItem, outputAmount, durationSeconds));
    }
}
