package com.skyblock.core.forge;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * Singleton manager for SkyBlock item forging.
 *
 * <p>Holds the static {@link ForgeRecipe} catalogue and tracks each player's
 * active forge slot (one active forge per player at a time).</p>
 *
 * <p>Not thread-safe; synchronize externally if needed.</p>
 */
public final class ForgeManager {

    /**
     * A recipe that can be forged at the Forge.
     *
     * @param id              unique recipe identifier
     * @param displayName     human-readable name
     * @param ingredients     ingredient name → required quantity
     * @param outputItem      item produced by this recipe
     * @param outputAmount    quantity produced
     * @param durationSeconds forge duration in seconds
     */
    public record ForgeRecipe(String id, String displayName,
                              Map<String, Integer> ingredients,
                              String outputItem, int outputAmount,
                              int durationSeconds) {
        public ForgeRecipe {
            Objects.requireNonNull(id, "id");
            Objects.requireNonNull(displayName, "displayName");
            ingredients = Collections.unmodifiableMap(new HashMap<>(ingredients));
            Objects.requireNonNull(outputItem, "outputItem");
        }
    }

    /** An active forge job for a player. */
    public static final class ForgeJob {
        private final ForgeRecipe recipe;
        private final long startTimeMillis;

        ForgeJob(ForgeRecipe recipe, long startTimeMillis) {
            this.recipe = Objects.requireNonNull(recipe, "recipe");
            this.startTimeMillis = startTimeMillis;
        }

        public ForgeRecipe getRecipe() { return recipe; }
        public long getStartTimeMillis() { return startTimeMillis; }

        /**
         * Returns whether the forge job is complete at the given wall-clock time.
         *
         * @param nowMillis current time in milliseconds
         * @return {@code true} if the required duration has elapsed
         */
        public boolean isComplete(long nowMillis) {
            return (nowMillis - startTimeMillis) >= (long) recipe.durationSeconds() * 1000L;
        }
    }

    // ---------------------------------------------------------------------------
    // Recipe catalogue
    // ---------------------------------------------------------------------------

    private static final Map<String, ForgeRecipe> RECIPES;

    static {
        Map<String, ForgeRecipe> r = new HashMap<>();

        r.put("mithril_pickaxe", new ForgeRecipe(
                "mithril_pickaxe", "Mithril Pickaxe",
                Map.of("MITHRIL_ORE", 20, "IRON_INGOT", 8),
                "MITHRIL_PICKAXE", 1, 3600));

        r.put("titanium_drill", new ForgeRecipe(
                "titanium_drill", "Titanium Drill",
                Map.of("TITANIUM", 15, "MITHRIL_PICKAXE", 1, "COAL", 64),
                "TITANIUM_DRILL", 1, 14400));

        r.put("refined_mithril", new ForgeRecipe(
                "refined_mithril", "Refined Mithril",
                Map.of("MITHRIL_ORE", 10),
                "REFINED_MITHRIL", 1, 900));

        r.put("refined_titanium", new ForgeRecipe(
                "refined_titanium", "Refined Titanium",
                Map.of("TITANIUM", 5),
                "REFINED_TITANIUM", 1, 1800));

        r.put("fuel_tank", new ForgeRecipe(
                "fuel_tank", "Fuel Tank",
                Map.of("REFINED_MITHRIL", 5, "IRON_INGOT", 16),
                "FUEL_TANK", 1, 7200));

        r.put("mithril_plate", new ForgeRecipe(
                "mithril_plate", "Mithril Plate",
                Map.of("REFINED_MITHRIL", 8),
                "MITHRIL_PLATE", 1, 3600));

        r.put("tungsten_key", new ForgeRecipe(
                "tungsten_key", "Tungsten Key",
                Map.of("TUNGSTEN", 3, "IRON_INGOT", 4),
                "TUNGSTEN_KEY", 1, 600));

        r.put("gemstone_gauntlet", new ForgeRecipe(
                "gemstone_gauntlet", "Gemstone Gauntlet",
                Map.of("RUBY_GEMSTONE", 5, "SAPPHIRE_GEMSTONE", 5, "REFINED_MITHRIL", 4),
                "GEMSTONE_GAUNTLET", 1, 28800));

        r.put("dragon_scale", new ForgeRecipe(
                "dragon_scale", "Dragon Scale",
                Map.of("DRAGON_FRAGMENT", 12, "REFINED_TITANIUM", 3),
                "DRAGON_SCALE", 1, 86400));

        r.put("mithril_blade", new ForgeRecipe(
                "mithril_blade", "Mithril Blade",
                Map.of("REFINED_MITHRIL", 6, "IRON_INGOT", 5),
                "MITHRIL_BLADE", 1, 7200));

        RECIPES = Collections.unmodifiableMap(r);
    }

    // ---------------------------------------------------------------------------
    // Singleton + state
    // ---------------------------------------------------------------------------

    private static final ForgeManager INSTANCE = new ForgeManager();

    /** Active forge job per player. */
    private final Map<UUID, ForgeJob> activeJobs = new HashMap<>();

    private ForgeManager() {}

    /**
     * Returns the single shared {@code ForgeManager} instance.
     *
     * @return the singleton instance
     */
    public static ForgeManager getInstance() {
        return INSTANCE;
    }

    // ---------------------------------------------------------------------------
    // Recipe access
    // ---------------------------------------------------------------------------

    /**
     * Returns an unmodifiable view of all available forge recipes, keyed by ID.
     *
     * @return the recipe catalogue
     */
    public Map<String, ForgeRecipe> getRecipes() {
        return RECIPES;
    }

    /**
     * Returns the recipe with the given ID, or {@code null} if unknown.
     *
     * @param id recipe identifier (case-sensitive)
     * @return matching {@link ForgeRecipe}, or {@code null}
     */
    public ForgeRecipe getRecipe(String id) {
        return RECIPES.get(id);
    }

    // ---------------------------------------------------------------------------
    // Active jobs
    // ---------------------------------------------------------------------------

    /**
     * Starts a forge job for the player.
     *
     * @param playerId       the player starting the forge
     * @param recipeId       the recipe to forge
     * @param nowMillis      current wall-clock time in milliseconds
     * @throws IllegalArgumentException if the recipe ID is unknown
     * @throws IllegalStateException    if the player already has an active forge job
     */
    public void startForge(UUID playerId, String recipeId, long nowMillis) {
        Objects.requireNonNull(playerId, "playerId");
        ForgeRecipe recipe = RECIPES.get(recipeId);
        if (recipe == null) {
            throw new IllegalArgumentException("Unknown recipe: " + recipeId);
        }
        if (activeJobs.containsKey(playerId)) {
            throw new IllegalStateException("Player already has an active forge job");
        }
        activeJobs.put(playerId, new ForgeJob(recipe, nowMillis));
    }

    /**
     * Returns the active forge job for the player, or {@code null} if none.
     *
     * @param playerId the player to look up
     * @return the {@link ForgeJob}, or {@code null}
     */
    public ForgeJob getActiveJob(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        return activeJobs.get(playerId);
    }

    /**
     * Collects a completed forge job, removing it from the active-job map.
     *
     * @param playerId  the player collecting their item
     * @param nowMillis current wall-clock time in milliseconds
     * @return the completed {@link ForgeJob}
     * @throws IllegalStateException if there is no active job, or it is not yet complete
     */
    public ForgeJob collectForge(UUID playerId, long nowMillis) {
        Objects.requireNonNull(playerId, "playerId");
        ForgeJob job = activeJobs.get(playerId);
        if (job == null) {
            throw new IllegalStateException("No active forge job for this player");
        }
        if (!job.isComplete(nowMillis)) {
            throw new IllegalStateException("Forge job is not yet complete");
        }
        activeJobs.remove(playerId);
        return job;
    }

    /**
     * Cancels and removes the player's active forge job, if any.
     *
     * @param playerId the player whose job to cancel
     * @return {@code true} if a job was cancelled, {@code false} if there was none
     */
    public boolean cancelForge(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        return activeJobs.remove(playerId) != null;
    }
}
