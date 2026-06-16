package com.skyblock.itemforge;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Manages timed item forge recipes and per-player forge jobs: a recipe turns
 * a set of input items into an output item after a fixed duration, and each
 * player has a fixed number of forge slots in which jobs run concurrently.
 *
 * <p>Recipes and per-player forges are stored in {@link ConcurrentHashMap}s,
 * with per-forge state guarded by the forge's own monitor, so all operations
 * are thread-safe. Time is passed in explicitly so callers control the clock.</p>
 */
public final class ItemForgeManager {

    /** The number of forge slots each player has. */
    public static final int SLOT_COUNT = 5;

    /** An immutable timed forge recipe: input item counts, an output item, and a duration. */
    public static final class ForgeRecipe {

        private final String id;
        private final Map<String, Integer> inputs;
        private final String outputItemId;
        private final long durationMillis;

        private ForgeRecipe(String id, Map<String, Integer> inputs, String outputItemId, long durationMillis) {
            this.id = id;
            this.inputs = Collections.unmodifiableMap(new LinkedHashMap<>(inputs));
            this.outputItemId = outputItemId;
            this.durationMillis = durationMillis;
        }

        /** Returns the unique recipe id. */
        public String getId() {
            return id;
        }

        /** Returns an unmodifiable view of the required input item counts, keyed by item id. */
        public Map<String, Integer> getInputs() {
            return inputs;
        }

        /** Returns the item id this recipe produces. */
        public String getOutputItemId() {
            return outputItemId;
        }

        /** Returns how long a job for this recipe takes, in milliseconds. */
        public long getDurationMillis() {
            return durationMillis;
        }
    }

    /** An immutable snapshot of a forge job running in a slot. */
    public static final class ForgeJob {

        private final ForgeRecipe recipe;
        private final long startedAtMillis;

        private ForgeJob(ForgeRecipe recipe, long startedAtMillis) {
            this.recipe = recipe;
            this.startedAtMillis = startedAtMillis;
        }

        /** Returns the recipe this job is forging. */
        public ForgeRecipe getRecipe() {
            return recipe;
        }

        /** Returns the timestamp the job started at, in milliseconds. */
        public long getStartedAtMillis() {
            return startedAtMillis;
        }

        /** Returns the timestamp the job completes at, in milliseconds. */
        public long getCompletesAtMillis() {
            return startedAtMillis + recipe.getDurationMillis();
        }

        /** Returns whether the job is complete at the given time. */
        public boolean isComplete(long nowMillis) {
            return nowMillis >= getCompletesAtMillis();
        }
    }

    /** A single player's forge: its slots and the jobs running in them. */
    private static final class Forge {

        private final Map<Integer, ForgeJob> jobs = new ConcurrentHashMap<>();

        private synchronized ForgeJob start(int slot, ForgeRecipe recipe, long nowMillis) {
            requireSlot(slot);
            if (jobs.containsKey(slot)) {
                throw new IllegalStateException("slot " + slot + " already has a job running");
            }
            ForgeJob job = new ForgeJob(recipe, nowMillis);
            jobs.put(slot, job);
            return job;
        }

        private synchronized Optional<ForgeJob> get(int slot) {
            requireSlot(slot);
            return Optional.ofNullable(jobs.get(slot));
        }

        private synchronized String collect(int slot, long nowMillis) {
            requireSlot(slot);
            ForgeJob job = jobs.get(slot);
            if (job == null) {
                throw new IllegalStateException("slot " + slot + " has no job running");
            }
            if (!job.isComplete(nowMillis)) {
                throw new IllegalStateException("slot " + slot + " is not complete yet");
            }
            jobs.remove(slot);
            return job.getRecipe().getOutputItemId();
        }

        private synchronized Optional<ForgeJob> cancel(int slot) {
            requireSlot(slot);
            return Optional.ofNullable(jobs.remove(slot));
        }

        private static void requireSlot(int slot) {
            if (slot < 0 || slot >= SLOT_COUNT) {
                throw new IllegalArgumentException(
                        "slot must be between 0 and " + (SLOT_COUNT - 1) + ", got " + slot);
            }
        }
    }

    private final ConcurrentHashMap<String, ForgeRecipe> recipes = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<UUID, Forge> forges = new ConcurrentHashMap<>();

    /**
     * Registers a forge recipe.
     *
     * @param id             the unique recipe id; must be non-blank and unused
     * @param inputs         the required input item counts, keyed by item id; must be
     *                       non-empty with positive counts
     * @param outputItemId   the item id the recipe produces; must be non-blank
     * @param durationMillis how long a job takes, in milliseconds; must be positive
     * @return the registered recipe
     * @throws IllegalArgumentException if the id is already registered
     */
    public ForgeRecipe registerRecipe(String id, Map<String, Integer> inputs,
                                      String outputItemId, long durationMillis) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("id must be non-blank");
        }
        if (inputs == null || inputs.isEmpty()) {
            throw new IllegalArgumentException("inputs must be non-empty");
        }
        for (Map.Entry<String, Integer> entry : inputs.entrySet()) {
            if (entry.getKey() == null || entry.getKey().isBlank()) {
                throw new IllegalArgumentException("input item ids must be non-blank");
            }
            if (entry.getValue() == null || entry.getValue() <= 0) {
                throw new IllegalArgumentException("count for " + entry.getKey() + " must be positive");
            }
        }
        if (outputItemId == null || outputItemId.isBlank()) {
            throw new IllegalArgumentException("outputItemId must be non-blank");
        }
        if (durationMillis <= 0) {
            throw new IllegalArgumentException("durationMillis must be positive");
        }
        ForgeRecipe recipe = new ForgeRecipe(id, inputs, outputItemId, durationMillis);
        if (recipes.putIfAbsent(id, recipe) != null) {
            throw new IllegalArgumentException("recipe " + id + " is already registered");
        }
        return recipe;
    }

    /**
     * Returns the recipe registered under the given id, if any.
     *
     * @param id the recipe id to look up
     * @return the recipe, or empty if none is registered under the id
     */
    public Optional<ForgeRecipe> getRecipe(String id) {
        return Optional.ofNullable(id == null ? null : recipes.get(id));
    }

    /**
     * Starts a forge job in one of the given player's slots.
     *
     * @param player    the player starting the job
     * @param slot      the slot to use, from 0 to {@value #SLOT_COUNT} minus one
     * @param recipeId  the id of a registered recipe
     * @param nowMillis the current time, in milliseconds
     * @return the started job
     * @throws IllegalArgumentException if no recipe is registered under the id
     * @throws IllegalStateException    if the slot already has a job running
     */
    public ForgeJob startJob(UUID player, int slot, String recipeId, long nowMillis) {
        ForgeRecipe recipe = getRecipe(recipeId)
                .orElseThrow(() -> new IllegalArgumentException("unknown recipe: " + recipeId));
        return forge(player).start(slot, recipe, nowMillis);
    }

    /**
     * Returns the job running in one of the given player's slots, if any.
     *
     * @param player the player whose forge to inspect
     * @param slot   the slot to read, from 0 to {@value #SLOT_COUNT} minus one
     * @return the running job, or empty if the slot is idle
     */
    public Optional<ForgeJob> getJob(UUID player, int slot) {
        return forge(player).get(slot);
    }

    /**
     * Collects the output of a completed forge job, freeing its slot.
     *
     * @param player    the player collecting the output
     * @param slot      the slot to collect from, from 0 to {@value #SLOT_COUNT} minus one
     * @param nowMillis the current time, in milliseconds
     * @return the item id produced by the job's recipe
     * @throws IllegalStateException if the slot is idle or the job is not complete yet
     */
    public String collectJob(UUID player, int slot, long nowMillis) {
        return forge(player).collect(slot, nowMillis);
    }

    /**
     * Cancels the job running in one of the given player's slots, if any,
     * freeing the slot without producing output.
     *
     * @param player the player whose forge to update
     * @param slot   the slot to clear, from 0 to {@value #SLOT_COUNT} minus one
     * @return the cancelled job, or empty if the slot was already idle
     */
    public Optional<ForgeJob> cancelJob(UUID player, int slot) {
        return forge(player).cancel(slot);
    }

    private Forge forge(UUID player) {
        if (player == null) {
            throw new IllegalArgumentException("player must be non-null");
        }
        return forges.computeIfAbsent(player, id -> new Forge());
    }
}
