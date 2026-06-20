package com.skyblock.core.manager;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;
import java.util.UUID;

/**
 * Singleton manager for SkyBlock item forging.
 *
 * <p>Holds the static {@link ForgeRecipe} catalogue and tracks each player's
 * active forge slots. Each player owns a configurable number of forge slots
 * (see {@link #getSlotCount(UUID)}), and each slot can run an independent,
 * multi-hour forge job concurrently.</p>
 *
 * <p>Forge durations are reduced by the player's Quick Forge Heart of the
 * Mountain perk level (see {@link #quickForgeReduction(int)}).</p>
 *
 * <p>Not thread-safe; synchronize externally if needed.</p>
 */
public final class ForgeManager {

    public enum ForgeRecipe {
        REFINED_TITANIUM(    "Refined Titanium",             "REFINED_TITANIUM",             1, 1800,
                Map.of("TITANIUM", 5)),
        ENCHANTED_LAPIS_LAZULI_BLOCK("Enchanted Lapis Lazuli Block", "ENCHANTED_LAPIS_LAZULI_BLOCK", 1, 3600,
                Map.of("LAPIS_LAZULI", 160)),
        ENCHANTED_IRON_BLOCK("Enchanted Iron Block",         "ENCHANTED_IRON_BLOCK",         1, 7200,
                Map.of("IRON_INGOT", 160)),
        ENCHANTED_GOLD_BLOCK("Enchanted Gold Block",         "ENCHANTED_GOLD_BLOCK",         1, 7200,
                Map.of("GOLD_INGOT", 160)),
        ENCHANTED_DIAMOND_BLOCK("Enchanted Diamond Block",   "ENCHANTED_DIAMOND_BLOCK",      1, 14400,
                Map.of("DIAMOND", 160)),
        MITHRIL_PICKAXE(     "Mithril Pickaxe",              "MITHRIL_PICKAXE",              1, 3600,
                Map.of("MITHRIL_ORE", 20, "IRON_INGOT", 8)),
        TITANIUM_DRILL(      "Titanium Drill",               "TITANIUM_DRILL",               1, 14400,
                Map.of("TITANIUM", 15, "COAL", 64)),
        REFINED_MITHRIL(     "Refined Mithril",              "REFINED_MITHRIL",              1, 900,
                Map.of("MITHRIL_ORE", 10)),
        FUEL_TANK(           "Fuel Tank",                    "FUEL_TANK",                    1, 7200,
                Map.of("REFINED_MITHRIL", 5, "IRON_INGOT", 16)),
        MITHRIL_PLATE(       "Mithril Plate",                "MITHRIL_PLATE",                1, 3600,
                Map.of("REFINED_MITHRIL", 8)),
        TUNGSTEN_KEY(        "Tungsten Key",                 "TUNGSTEN_KEY",                 1, 600,
                Map.of("TUNGSTEN", 3, "IRON_INGOT", 4)),
        GEMSTONE_GAUNTLET(   "Gemstone Gauntlet",            "GEMSTONE_GAUNTLET",            1, 28800,
                Map.of("RUBY_GEMSTONE", 5, "SAPPHIRE_GEMSTONE", 5, "REFINED_MITHRIL", 4)),
        DRAGON_SCALE(        "Dragon Scale",                 "DRAGON_SCALE",                 1, 86400,
                Map.of("DRAGON_FRAGMENT", 12, "REFINED_TITANIUM", 3)),
        MITHRIL_BLADE(       "Mithril Blade",                "MITHRIL_BLADE",                1, 7200,
                Map.of("REFINED_MITHRIL", 6, "IRON_INGOT", 5)),
        REFINED_TUNGSTEN(    "Refined Tungsten",             "REFINED_TUNGSTEN",             1, 900,
                Map.of("TUNGSTEN", 10)),
        REFINED_UMBER(       "Refined Umber",                "REFINED_UMBER",                1, 900,
                Map.of("UMBER", 10)),
        BEJEWELED_HANDLE(    "Bejeweled Handle",             "BEJEWELED_HANDLE",             1, 14400,
                Map.of("RUBY_GEMSTONE", 3, "SAPPHIRE_GEMSTONE", 3, "REFINED_TUNGSTEN", 4)),
        SEARING_STONE(       "Searing Stone",                "SEARING_STONE",                1, 21600,
                Map.of("REFINED_UMBER", 8, "COAL", 128)),
        PETRIFIED_STARFALL(  "Petrified Starfall",           "PETRIFIED_STARFALL",           1, 43200,
                Map.of("STARFALL", 20, "REFINED_MITHRIL", 5)),
        GLACIAL_FRAGMENT(    "Glacial Fragment",             "GLACIAL_FRAGMENT",             1, 10800,
                Map.of("ICE", 512, "REFINED_MITHRIL", 3)),
        POWER_CRYSTAL(       "Power Crystal",                "POWER_CRYSTAL",                1, 86400,
                Map.of("RUBY_GEMSTONE", 8, "SAPPHIRE_GEMSTONE", 8, "JADE_GEMSTONE", 8, "REFINED_TITANIUM", 4)),
        MITHRIL_GOLEM_KIT(   "Mithril Golem Kit",           "MITHRIL_GOLEM_KIT",            1, 57600,
                Map.of("REFINED_MITHRIL", 12, "IRON_INGOT", 64, "COAL", 32)),
        DIAMOND_SWORD(       "Diamond Sword",                "DIAMOND_SWORD",                1, 1800,
                Map.of("DIAMOND", 2, "STICK", 1)),
        IRON_CHESTPLATE(     "Iron Chestplate",              "IRON_CHESTPLATE",              1, 3600,
                Map.of("IRON_INGOT", 8)),
        GOLDEN_PICKAXE(      "Golden Pickaxe",               "GOLDEN_PICKAXE",               1, 900,
                Map.of("GOLD_INGOT", 3, "STICK", 2)),
        ENCHANTED_BOW(       "Enchanted Bow",                "ENCHANTED_BOW",                1, 1800,
                Map.of("STRING", 3, "STICK", 3));

        private final String displayName;
        private final String outputItem;
        private final int outputAmount;
        private final int durationSeconds;
        private final Map<String, Integer> ingredients;

        ForgeRecipe(String displayName, String outputItem, int outputAmount, int durationSeconds,
                    Map<String, Integer> ingredients) {
            this.displayName = displayName;
            this.outputItem = outputItem;
            this.outputAmount = outputAmount;
            this.durationSeconds = durationSeconds;
            this.ingredients = Collections.unmodifiableMap(new HashMap<>(ingredients));
        }

        public String getDisplayName() { return displayName; }
        public String getOutputItem() { return outputItem; }
        public int getOutputAmount() { return outputAmount; }
        public int getDurationSeconds() { return durationSeconds; }
        public Map<String, Integer> getIngredients() { return ingredients; }
    }

    /** An active forge job occupying one of a player's forge slots. */
    public static final class ForgeJob {
        private final ForgeRecipe recipe;
        private final int slot;
        private final long startTimeMillis;
        private final int durationSeconds;

        ForgeJob(ForgeRecipe recipe, int slot, long startTimeMillis, int durationSeconds) {
            this.recipe = Objects.requireNonNull(recipe, "recipe");
            this.slot = slot;
            this.startTimeMillis = startTimeMillis;
            this.durationSeconds = durationSeconds;
        }

        public ForgeRecipe getRecipe() { return recipe; }
        public int getSlot() { return slot; }
        public long getStartTimeMillis() { return startTimeMillis; }

        /**
         * Returns this job's effective duration in seconds, after any Quick Forge
         * time reduction applied when the job was started.
         *
         * @return the effective duration in seconds
         */
        public int getDurationSeconds() { return durationSeconds; }

        /**
         * Returns whether the forge job is complete at the given wall-clock time.
         *
         * @param nowMillis current time in milliseconds
         * @return {@code true} if the effective duration has elapsed
         */
        public boolean isComplete(long nowMillis) {
            return (nowMillis - startTimeMillis) >= (long) durationSeconds * 1000L;
        }
    }

    /**
     * Lightweight immutable record of a single forge job, capturing the recipe
     * together with its absolute start and finish wall-clock times (in millis).
     *
     * @param recipe     the recipe being forged
     * @param startTime  the time the job started, in milliseconds
     * @param finishTime the time the job completes, in milliseconds
     */
    public record ForgeEntry(ForgeRecipe recipe, long startTime, long finishTime) {

        public ForgeEntry {
            Objects.requireNonNull(recipe, "recipe");
        }

        /**
         * Returns whether the forge job is complete at the given wall-clock time.
         *
         * @param nowMillis current time in milliseconds
         * @return {@code true} if {@code nowMillis} has reached the finish time
         */
        public boolean isComplete(long nowMillis) {
            return nowMillis >= finishTime;
        }
    }

    // ---------------------------------------------------------------------------
    // Recipe catalogue
    // ---------------------------------------------------------------------------

    private static final Map<String, ForgeRecipe> RECIPES;

    static {
        Map<String, ForgeRecipe> r = new HashMap<>();
        for (ForgeRecipe recipe : ForgeRecipe.values()) {
            r.put(recipe.name().toLowerCase(), recipe);
        }
        RECIPES = Collections.unmodifiableMap(r);
    }

    // ---------------------------------------------------------------------------
    // Singleton + state
    // ---------------------------------------------------------------------------

    private static final ForgeManager INSTANCE = new ForgeManager();

    /** Default number of forge slots a player owns before any HOTM upgrades. */
    public static final int DEFAULT_SLOT_COUNT = 2;

    /** Maximum number of forge slots a player can unlock. */
    public static final int MAX_SLOT_COUNT = 7;

    /** Maximum Quick Forge Heart of the Mountain perk level. */
    public static final int MAX_QUICK_FORGE_LEVEL = 20;

    /** Active forge jobs per player, keyed by slot index (sorted ascending). */
    private final Map<UUID, TreeMap<Integer, ForgeJob>> activeJobs = new HashMap<>();

    /** Number of forge slots owned per player; absent means {@link #DEFAULT_SLOT_COUNT}. */
    private final Map<UUID, Integer> slotCounts = new HashMap<>();

    /** Quick Forge perk level per player; absent means {@code 0}. */
    private final Map<UUID, Integer> quickForgeLevels = new HashMap<>();

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
    // Forge slots & Quick Forge perk
    // ---------------------------------------------------------------------------

    /**
     * Returns the number of forge slots the player owns.
     *
     * @param playerId the player to look up
     * @return the slot count (at least {@link #DEFAULT_SLOT_COUNT})
     */
    public int getSlotCount(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        return slotCounts.getOrDefault(playerId, DEFAULT_SLOT_COUNT);
    }

    /**
     * Sets the number of forge slots the player owns, clamped to
     * {@code [DEFAULT_SLOT_COUNT, MAX_SLOT_COUNT]}.
     *
     * @param playerId the player to configure
     * @param slots    the desired slot count
     */
    public void setSlotCount(UUID playerId, int slots) {
        Objects.requireNonNull(playerId, "playerId");
        int clamped = Math.max(DEFAULT_SLOT_COUNT, Math.min(MAX_SLOT_COUNT, slots));
        slotCounts.put(playerId, clamped);
    }

    /**
     * Returns the player's Quick Forge Heart of the Mountain perk level.
     *
     * @param playerId the player to look up
     * @return the perk level (0 if unset)
     */
    public int getQuickForgeLevel(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        return quickForgeLevels.getOrDefault(playerId, 0);
    }

    /**
     * Sets the player's Quick Forge perk level, clamped to
     * {@code [0, MAX_QUICK_FORGE_LEVEL]}.
     *
     * @param playerId the player to configure
     * @param level    the desired perk level
     */
    public void setQuickForgeLevel(UUID playerId, int level) {
        Objects.requireNonNull(playerId, "playerId");
        int clamped = Math.max(0, Math.min(MAX_QUICK_FORGE_LEVEL, level));
        quickForgeLevels.put(playerId, clamped);
    }

    /**
     * Returns the fractional forge-time reduction granted by the Quick Forge perk
     * at the given level. Level 1 grants 10.5%, increasing by 0.5% per level up to
     * 19.5% at level 19, with the maximum level 20 granting 30%.
     *
     * @param level the Quick Forge perk level
     * @return the reduction as a fraction in {@code [0, 0.30]}
     */
    public static double quickForgeReduction(int level) {
        if (level <= 0) {
            return 0.0;
        }
        if (level >= MAX_QUICK_FORGE_LEVEL) {
            return 0.30;
        }
        return (10.0 + 0.5 * level) / 100.0;
    }

    /**
     * Returns a recipe's effective forge duration after applying the Quick Forge
     * reduction for the given perk level.
     *
     * @param recipe          the recipe being forged
     * @param quickForgeLevel the player's Quick Forge perk level
     * @return the reduced duration in seconds
     */
    public static int effectiveDurationSeconds(ForgeRecipe recipe, int quickForgeLevel) {
        Objects.requireNonNull(recipe, "recipe");
        double reduced = recipe.getDurationSeconds() * (1.0 - quickForgeReduction(quickForgeLevel));
        return (int) Math.round(reduced);
    }

    // ---------------------------------------------------------------------------
    // Active jobs
    // ---------------------------------------------------------------------------

    /**
     * Starts a forge job in the player's lowest-numbered free slot.
     *
     * @param playerId  the player starting the forge
     * @param recipeId  the recipe to forge
     * @param nowMillis current wall-clock time in milliseconds
     * @return the started {@link ForgeJob}
     * @throws IllegalArgumentException if the recipe ID is unknown
     * @throws IllegalStateException    if all of the player's forge slots are busy
     */
    public ForgeJob startForge(UUID playerId, String recipeId, long nowMillis) {
        Objects.requireNonNull(playerId, "playerId");
        int slot = firstFreeSlot(playerId);
        if (slot < 0) {
            throw new IllegalStateException("All forge slots are busy");
        }
        return startForge(playerId, recipeId, slot, nowMillis);
    }

    /**
     * Starts a forge job in a specific slot.
     *
     * @param playerId  the player starting the forge
     * @param recipeId  the recipe to forge
     * @param slot      the slot index, in {@code [0, getSlotCount(playerId))}
     * @param nowMillis current wall-clock time in milliseconds
     * @return the started {@link ForgeJob}
     * @throws IllegalArgumentException if the recipe ID is unknown or the slot is out of range
     * @throws IllegalStateException    if the slot is already occupied
     */
    public ForgeJob startForge(UUID playerId, String recipeId, int slot, long nowMillis) {
        Objects.requireNonNull(playerId, "playerId");
        ForgeRecipe recipe = RECIPES.get(recipeId);
        if (recipe == null) {
            throw new IllegalArgumentException("Unknown recipe: " + recipeId);
        }
        if (slot < 0 || slot >= getSlotCount(playerId)) {
            throw new IllegalArgumentException("Slot out of range: " + slot);
        }
        TreeMap<Integer, ForgeJob> jobs = activeJobs.computeIfAbsent(playerId, k -> new TreeMap<>());
        if (jobs.containsKey(slot)) {
            throw new IllegalStateException("Forge slot " + slot + " is already occupied");
        }
        int duration = effectiveDurationSeconds(recipe, getQuickForgeLevel(playerId));
        ForgeJob job = new ForgeJob(recipe, slot, nowMillis, duration);
        jobs.put(slot, job);
        return job;
    }

    /**
     * Returns the player's active forge jobs keyed by slot, or an empty map if none.
     *
     * @param playerId the player to look up
     * @return an unmodifiable, slot-ordered view of the player's active jobs
     */
    public Map<Integer, ForgeJob> getActiveJobs(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        TreeMap<Integer, ForgeJob> jobs = activeJobs.get(playerId);
        return jobs == null ? Collections.emptyMap() : Collections.unmodifiableMap(jobs);
    }

    /**
     * Returns the job in a specific slot, or {@code null} if the slot is empty.
     *
     * @param playerId the player to look up
     * @param slot     the slot index
     * @return the {@link ForgeJob}, or {@code null}
     */
    public ForgeJob getJob(UUID playerId, int slot) {
        Objects.requireNonNull(playerId, "playerId");
        TreeMap<Integer, ForgeJob> jobs = activeJobs.get(playerId);
        return jobs == null ? null : jobs.get(slot);
    }

    /**
     * Returns the player's lowest-slot active forge job, or {@code null} if none.
     *
     * @param playerId the player to look up
     * @return the {@link ForgeJob}, or {@code null}
     */
    public ForgeJob getActiveJob(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        TreeMap<Integer, ForgeJob> jobs = activeJobs.get(playerId);
        return (jobs == null || jobs.isEmpty()) ? null : jobs.firstEntry().getValue();
    }

    /**
     * Collects the completed job in a specific slot, freeing the slot.
     *
     * @param playerId  the player collecting their item
     * @param slot      the slot index
     * @param nowMillis current wall-clock time in milliseconds
     * @return the completed {@link ForgeJob}
     * @throws IllegalStateException if the slot is empty or its job is not yet complete
     */
    public ForgeJob collectForge(UUID playerId, int slot, long nowMillis) {
        Objects.requireNonNull(playerId, "playerId");
        TreeMap<Integer, ForgeJob> jobs = activeJobs.get(playerId);
        ForgeJob job = jobs == null ? null : jobs.get(slot);
        if (job == null) {
            throw new IllegalStateException("No active forge job in slot " + slot);
        }
        if (!job.isComplete(nowMillis)) {
            throw new IllegalStateException("Forge job is not yet complete");
        }
        jobs.remove(slot);
        if (jobs.isEmpty()) {
            activeJobs.remove(playerId);
        }
        return job;
    }

    /**
     * Collects the player's lowest-slot completed forge job.
     *
     * @param playerId  the player collecting their item
     * @param nowMillis current wall-clock time in milliseconds
     * @return the completed {@link ForgeJob}
     * @throws IllegalStateException if there is no active job, or none is yet complete
     */
    public ForgeJob collectForge(UUID playerId, long nowMillis) {
        Objects.requireNonNull(playerId, "playerId");
        TreeMap<Integer, ForgeJob> jobs = activeJobs.get(playerId);
        if (jobs == null || jobs.isEmpty()) {
            throw new IllegalStateException("No active forge job for this player");
        }
        for (Map.Entry<Integer, ForgeJob> entry : jobs.entrySet()) {
            if (entry.getValue().isComplete(nowMillis)) {
                return collectForge(playerId, entry.getKey(), nowMillis);
            }
        }
        throw new IllegalStateException("Forge job is not yet complete");
    }

    /**
     * Cancels the job in a specific slot, if any.
     *
     * @param playerId the player whose job to cancel
     * @param slot     the slot index
     * @return {@code true} if a job was cancelled, {@code false} if the slot was empty
     */
    public boolean cancelForge(UUID playerId, int slot) {
        Objects.requireNonNull(playerId, "playerId");
        TreeMap<Integer, ForgeJob> jobs = activeJobs.get(playerId);
        if (jobs == null || jobs.remove(slot) == null) {
            return false;
        }
        if (jobs.isEmpty()) {
            activeJobs.remove(playerId);
        }
        return true;
    }

    /**
     * Cancels the player's lowest-slot active forge job, if any.
     *
     * @param playerId the player whose job to cancel
     * @return {@code true} if a job was cancelled, {@code false} if there was none
     */
    public boolean cancelForge(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        TreeMap<Integer, ForgeJob> jobs = activeJobs.get(playerId);
        if (jobs == null || jobs.isEmpty()) {
            return false;
        }
        return cancelForge(playerId, jobs.firstKey());
    }

    /** Returns the lowest free slot index for the player, or {@code -1} if all are busy. */
    private int firstFreeSlot(UUID playerId) {
        TreeMap<Integer, ForgeJob> jobs = activeJobs.get(playerId);
        int slotCount = getSlotCount(playerId);
        for (int i = 0; i < slotCount; i++) {
            if (jobs == null || !jobs.containsKey(i)) {
                return i;
            }
        }
        return -1;
    }
}
