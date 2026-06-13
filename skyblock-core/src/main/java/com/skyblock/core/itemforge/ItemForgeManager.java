package com.skyblock.core.itemforge;

import com.skyblock.core.item.SkyBlockItem;
import com.skyblock.core.items.CustomItemManager;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * Singleton manager for SkyBlock item forging using {@link SkyBlockItem}-typed outputs.
 *
 * <p>Maintains a static recipe catalogue and tracks each player's active forge
 * slot (one active job per player at a time).</p>
 *
 * <p>Not thread-safe; synchronize externally if needed.</p>
 */
public final class ItemForgeManager {

    /** A recipe whose output is a {@link SkyBlockItem}. */
    public static final class ItemForgeRecipe {
        private final String id;
        private final String displayName;
        /** Ingredient name → required quantity. */
        private final Map<String, Integer> ingredients;
        private final SkyBlockItem outputItem;
        private final int outputAmount;
        /** Forge duration in seconds. */
        private final int durationSeconds;

        public ItemForgeRecipe(String id, String displayName,
                               Map<String, Integer> ingredients,
                               SkyBlockItem outputItem, int outputAmount,
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
        public SkyBlockItem getOutputItem() { return outputItem; }
        public int getOutputAmount() { return outputAmount; }
        public int getDurationSeconds() { return durationSeconds; }
    }

    /** An active forge job for a player. */
    public static final class ItemForgeJob {
        private final ItemForgeRecipe recipe;
        private final long startTimeMillis;

        ItemForgeJob(ItemForgeRecipe recipe, long startTimeMillis) {
            this.recipe = Objects.requireNonNull(recipe, "recipe");
            this.startTimeMillis = startTimeMillis;
        }

        public ItemForgeRecipe getRecipe() { return recipe; }
        public long getStartTimeMillis() { return startTimeMillis; }

        /**
         * Returns whether this forge job is complete at the given wall-clock time.
         *
         * @param nowMillis current time in milliseconds
         * @return {@code true} if the required duration has elapsed
         */
        public boolean isComplete(long nowMillis) {
            return (nowMillis - startTimeMillis) >= (long) recipe.getDurationSeconds() * 1000L;
        }
    }

    // ---------------------------------------------------------------------------
    // Recipe catalogue
    // ---------------------------------------------------------------------------

    /** Canonical set of forge recipes available in SkyBlock. */
    public enum ForgeRecipe {
        MITHRIL_PICKAXE("Mithril Pickaxe", 3600, CustomItemManager.Rarity.RARE),
        TITANIUM_DRILL("Titanium Drill", 14400, CustomItemManager.Rarity.EPIC),
        REFINED_MITHRIL("Refined Mithril", 900, CustomItemManager.Rarity.UNCOMMON),
        REFINED_TITANIUM("Refined Titanium", 1800, CustomItemManager.Rarity.RARE),
        FUEL_TANK("Fuel Tank", 7200, CustomItemManager.Rarity.RARE),
        MITHRIL_PLATE("Mithril Plate", 3600, CustomItemManager.Rarity.UNCOMMON),
        TUNGSTEN_KEY("Tungsten Key", 600, CustomItemManager.Rarity.UNCOMMON),
        GEMSTONE_GAUNTLET("Gemstone Gauntlet", 28800, CustomItemManager.Rarity.LEGENDARY),
        DRAGON_SCALE("Dragon Scale", 86400, CustomItemManager.Rarity.LEGENDARY),
        MITHRIL_BLADE("Mithril Blade", 7200, CustomItemManager.Rarity.RARE),
        RUBY_GEMSTONE_RING("Ruby Gemstone Ring", 5400, CustomItemManager.Rarity.RARE),
        SAPPHIRE_GEMSTONE_RING("Sapphire Gemstone Ring", 5400, CustomItemManager.Rarity.RARE),
        JADE_GEMSTONE_RING("Jade Gemstone Ring", 5400, CustomItemManager.Rarity.RARE),
        TOPAZ_GEMSTONE_RING("Topaz Gemstone Ring", 5400, CustomItemManager.Rarity.RARE),
        JASPER_GEMSTONE_RING("Jasper Gemstone Ring", 5400, CustomItemManager.Rarity.RARE),
        OPAL_GEMSTONE_RING("Opal Gemstone Ring", 5400, CustomItemManager.Rarity.RARE),
        MITHRIL_GAUNTLET("Mithril Gauntlet", 14400, CustomItemManager.Rarity.EPIC),
        REFINED_UMBER("Refined Umber", 1800, CustomItemManager.Rarity.UNCOMMON),
        REFINED_TUNGSTEN("Refined Tungsten", 1800, CustomItemManager.Rarity.UNCOMMON),
        GOBLIN_RADAR("Goblin Radar", 10800, CustomItemManager.Rarity.EPIC),
        BEJEWELED_HANDLE("Bejeweled Handle", 21600, CustomItemManager.Rarity.LEGENDARY),
        SORROW_HELMET("Sorrow Helmet", 43200, CustomItemManager.Rarity.LEGENDARY);

        private final String displayName;
        private final int durationSeconds;
        private final CustomItemManager.Rarity rarity;

        ForgeRecipe(String displayName, int durationSeconds, CustomItemManager.Rarity rarity) {
            this.displayName = displayName;
            this.durationSeconds = durationSeconds;
            this.rarity = rarity;
        }

        public String getDisplayName() { return displayName; }
        public int getDurationSeconds() { return durationSeconds; }
        public CustomItemManager.Rarity getRarity() { return rarity; }

        /** Returns the recipe ID used as the map key (lowercase enum name). */
        public String getId() { return name().toLowerCase(); }
    }

    private static final Map<String, ItemForgeRecipe> RECIPES;

    static {
        Map<String, ItemForgeRecipe> r = new HashMap<>();

        r.put("mithril_pickaxe", new ItemForgeRecipe(
                "mithril_pickaxe", "Mithril Pickaxe",
                Map.of("MITHRIL_ORE", 20, "IRON_INGOT", 8),
                new SkyBlockItem("MITHRIL_PICKAXE", CustomItemManager.Rarity.RARE), 1, 3600));

        r.put("titanium_drill", new ItemForgeRecipe(
                "titanium_drill", "Titanium Drill",
                Map.of("TITANIUM", 15, "MITHRIL_PICKAXE", 1, "COAL", 64),
                new SkyBlockItem("TITANIUM_DRILL", CustomItemManager.Rarity.EPIC), 1, 14400));

        r.put("refined_mithril", new ItemForgeRecipe(
                "refined_mithril", "Refined Mithril",
                Map.of("MITHRIL_ORE", 10),
                new SkyBlockItem("REFINED_MITHRIL", CustomItemManager.Rarity.UNCOMMON), 1, 900));

        r.put("refined_titanium", new ItemForgeRecipe(
                "refined_titanium", "Refined Titanium",
                Map.of("TITANIUM", 5),
                new SkyBlockItem("REFINED_TITANIUM", CustomItemManager.Rarity.RARE), 1, 1800));

        r.put("fuel_tank", new ItemForgeRecipe(
                "fuel_tank", "Fuel Tank",
                Map.of("REFINED_MITHRIL", 5, "IRON_INGOT", 16),
                new SkyBlockItem("FUEL_TANK", CustomItemManager.Rarity.RARE), 1, 7200));

        r.put("mithril_plate", new ItemForgeRecipe(
                "mithril_plate", "Mithril Plate",
                Map.of("REFINED_MITHRIL", 8),
                new SkyBlockItem("MITHRIL_PLATE", CustomItemManager.Rarity.UNCOMMON), 1, 3600));

        r.put("tungsten_key", new ItemForgeRecipe(
                "tungsten_key", "Tungsten Key",
                Map.of("TUNGSTEN", 3, "IRON_INGOT", 4),
                new SkyBlockItem("TUNGSTEN_KEY", CustomItemManager.Rarity.UNCOMMON), 1, 600));

        r.put("gemstone_gauntlet", new ItemForgeRecipe(
                "gemstone_gauntlet", "Gemstone Gauntlet",
                Map.of("RUBY_GEMSTONE", 5, "SAPPHIRE_GEMSTONE", 5, "REFINED_MITHRIL", 4),
                new SkyBlockItem("GEMSTONE_GAUNTLET", CustomItemManager.Rarity.LEGENDARY), 1, 28800));

        r.put("dragon_scale", new ItemForgeRecipe(
                "dragon_scale", "Dragon Scale",
                Map.of("DRAGON_FRAGMENT", 12, "REFINED_TITANIUM", 3),
                new SkyBlockItem("DRAGON_SCALE", CustomItemManager.Rarity.LEGENDARY), 1, 86400));

        r.put("mithril_blade", new ItemForgeRecipe(
                "mithril_blade", "Mithril Blade",
                Map.of("REFINED_MITHRIL", 6, "IRON_INGOT", 5),
                new SkyBlockItem("MITHRIL_BLADE", CustomItemManager.Rarity.RARE), 1, 7200));

        r.put("ruby_gemstone_ring", new ItemForgeRecipe(
                "ruby_gemstone_ring", "Ruby Gemstone Ring",
                Map.of("RUBY_GEMSTONE", 8, "GOLD_INGOT", 4),
                new SkyBlockItem("RUBY_GEMSTONE_RING", CustomItemManager.Rarity.RARE), 1, 5400));

        r.put("sapphire_gemstone_ring", new ItemForgeRecipe(
                "sapphire_gemstone_ring", "Sapphire Gemstone Ring",
                Map.of("SAPPHIRE_GEMSTONE", 8, "GOLD_INGOT", 4),
                new SkyBlockItem("SAPPHIRE_GEMSTONE_RING", CustomItemManager.Rarity.RARE), 1, 5400));

        r.put("jade_gemstone_ring", new ItemForgeRecipe(
                "jade_gemstone_ring", "Jade Gemstone Ring",
                Map.of("JADE_GEMSTONE", 8, "GOLD_INGOT", 4),
                new SkyBlockItem("JADE_GEMSTONE_RING", CustomItemManager.Rarity.RARE), 1, 5400));

        r.put("topaz_gemstone_ring", new ItemForgeRecipe(
                "topaz_gemstone_ring", "Topaz Gemstone Ring",
                Map.of("TOPAZ_GEMSTONE", 8, "GOLD_INGOT", 4),
                new SkyBlockItem("TOPAZ_GEMSTONE_RING", CustomItemManager.Rarity.RARE), 1, 5400));

        r.put("jasper_gemstone_ring", new ItemForgeRecipe(
                "jasper_gemstone_ring", "Jasper Gemstone Ring",
                Map.of("JASPER_GEMSTONE", 8, "GOLD_INGOT", 4),
                new SkyBlockItem("JASPER_GEMSTONE_RING", CustomItemManager.Rarity.RARE), 1, 5400));

        r.put("opal_gemstone_ring", new ItemForgeRecipe(
                "opal_gemstone_ring", "Opal Gemstone Ring",
                Map.of("OPAL_GEMSTONE", 8, "GOLD_INGOT", 4),
                new SkyBlockItem("OPAL_GEMSTONE_RING", CustomItemManager.Rarity.RARE), 1, 5400));

        r.put("mithril_gauntlet", new ItemForgeRecipe(
                "mithril_gauntlet", "Mithril Gauntlet",
                Map.of("REFINED_MITHRIL", 10, "MITHRIL_PLATE", 2),
                new SkyBlockItem("MITHRIL_GAUNTLET", CustomItemManager.Rarity.EPIC), 1, 14400));

        r.put("refined_umber", new ItemForgeRecipe(
                "refined_umber", "Refined Umber",
                Map.of("UMBER", 10),
                new SkyBlockItem("REFINED_UMBER", CustomItemManager.Rarity.UNCOMMON), 1, 1800));

        r.put("refined_tungsten", new ItemForgeRecipe(
                "refined_tungsten", "Refined Tungsten",
                Map.of("TUNGSTEN", 5),
                new SkyBlockItem("REFINED_TUNGSTEN", CustomItemManager.Rarity.UNCOMMON), 1, 1800));

        r.put("goblin_radar", new ItemForgeRecipe(
                "goblin_radar", "Goblin Radar",
                Map.of("REFINED_MITHRIL", 6, "REFINED_TITANIUM", 2, "GOBLIN_EGG", 1),
                new SkyBlockItem("GOBLIN_RADAR", CustomItemManager.Rarity.EPIC), 1, 10800));

        r.put("bejeweled_handle", new ItemForgeRecipe(
                "bejeweled_handle", "Bejeweled Handle",
                Map.of("RUBY_GEMSTONE", 3, "SAPPHIRE_GEMSTONE", 3, "REFINED_MITHRIL", 5),
                new SkyBlockItem("BEJEWELED_HANDLE", CustomItemManager.Rarity.LEGENDARY), 1, 21600));

        r.put("sorrow_helmet", new ItemForgeRecipe(
                "sorrow_helmet", "Sorrow Helmet",
                Map.of("DRAGON_SCALE", 8, "REFINED_TITANIUM", 5),
                new SkyBlockItem("SORROW_HELMET", CustomItemManager.Rarity.LEGENDARY), 1, 43200));

        RECIPES = Collections.unmodifiableMap(r);
    }

    // ---------------------------------------------------------------------------
    // Singleton + state
    // ---------------------------------------------------------------------------

    private static final ItemForgeManager INSTANCE = new ItemForgeManager();

    /** Active forge job per player. */
    private final Map<UUID, ItemForgeJob> activeJobs = new HashMap<>();

    private ItemForgeManager() {}

    /**
     * Returns the single shared {@code ItemForgeManager} instance.
     *
     * @return the singleton instance
     */
    public static ItemForgeManager getInstance() {
        return INSTANCE;
    }

    // ---------------------------------------------------------------------------
    // Recipe access
    // ---------------------------------------------------------------------------

    /**
     * Returns an unmodifiable view of all available recipes, keyed by ID.
     *
     * @return the recipe catalogue
     */
    public Map<String, ItemForgeRecipe> getRecipes() {
        return RECIPES;
    }

    /**
     * Returns the recipe with the given ID, or {@code null} if unknown.
     *
     * @param id recipe identifier (case-sensitive)
     * @return matching {@link ItemForgeRecipe}, or {@code null}
     */
    public ItemForgeRecipe getRecipe(String id) {
        return RECIPES.get(id);
    }

    // ---------------------------------------------------------------------------
    // Active jobs
    // ---------------------------------------------------------------------------

    /**
     * Starts a forge job for the player.
     *
     * @param playerId  the player starting the forge
     * @param recipeId  the recipe to forge
     * @param nowMillis current wall-clock time in milliseconds
     * @throws IllegalArgumentException if the recipe ID is unknown
     * @throws IllegalStateException    if the player already has an active forge job
     */
    public void startForge(UUID playerId, String recipeId, long nowMillis) {
        Objects.requireNonNull(playerId, "playerId");
        ItemForgeRecipe recipe = RECIPES.get(recipeId);
        if (recipe == null) {
            throw new IllegalArgumentException("Unknown recipe: " + recipeId);
        }
        if (activeJobs.containsKey(playerId)) {
            throw new IllegalStateException("Player already has an active forge job");
        }
        activeJobs.put(playerId, new ItemForgeJob(recipe, nowMillis));
    }

    /**
     * Returns the active forge job for the player, or {@code null} if none.
     *
     * @param playerId the player to look up
     * @return the {@link ItemForgeJob}, or {@code null}
     */
    public ItemForgeJob getActiveJob(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        return activeJobs.get(playerId);
    }

    /**
     * Collects a completed forge job, removing it from the active-job map.
     *
     * @param playerId  the player collecting their item
     * @param nowMillis current wall-clock time in milliseconds
     * @return the completed {@link ItemForgeJob}
     * @throws IllegalStateException if there is no active job, or it is not yet complete
     */
    public ItemForgeJob collectForge(UUID playerId, long nowMillis) {
        Objects.requireNonNull(playerId, "playerId");
        ItemForgeJob job = activeJobs.get(playerId);
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
