package com.skyblock.core.manager;

import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * Singleton manager for SkyBlock alchemy skill progression and potion brewing.
 *
 * <p>Holds the static {@link PotionRecipe} catalogue, tracks each player's
 * active brew slot (one active brew per player at a time), and accumulates
 * per-player alchemy XP and level (1–{@value #MAX_LEVEL}).</p>
 *
 * <p>Not thread-safe; synchronize externally if needed.</p>
 */
public final class AlchemyManager {

    /** Potion types representing the output potions produced by brewing. */
    public enum PotionType {
        SPEED("Speed"),
        STRENGTH("Strength"),
        CRIT_CHANCE("Crit Chance"),
        CRIT_DAMAGE("Crit Damage"),
        HASTE("Haste"),
        RESISTANCE("Resistance"),
        REGENERATION("Regeneration"),
        HEALING("Healing"),
        FIRE_RESISTANCE("Fire Resistance"),
        WATER_BREATHING("Water Breathing"),
        NIGHT_VISION("Night Vision"),
        INVISIBILITY("Invisibility"),
        POISON("Poison");

        /** Human-readable display name shown to players. */
        public final String displayName;

        PotionType(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    /** A potion recipe that can be brewed at the Brewing Stand. */
    public static final class PotionRecipe {
        private final String id;
        private final String displayName;
        /** Ingredient name → required quantity. */
        private final Map<String, Integer> ingredients;
        private final String outputPotion;
        private final int outputAmount;
        /** Brew duration in seconds. */
        private final int durationSeconds;
        /** XP awarded when the brew completes. */
        private final double xpReward;

        public PotionRecipe(String id, String displayName,
                            Map<String, Integer> ingredients,
                            String outputPotion, int outputAmount,
                            int durationSeconds, double xpReward) {
            this.id = Objects.requireNonNull(id, "id");
            this.displayName = Objects.requireNonNull(displayName, "displayName");
            this.ingredients = Collections.unmodifiableMap(new HashMap<>(ingredients));
            this.outputPotion = Objects.requireNonNull(outputPotion, "outputPotion");
            this.outputAmount = outputAmount;
            this.durationSeconds = durationSeconds;
            this.xpReward = xpReward;
        }

        public String getId() { return id; }
        public String getDisplayName() { return displayName; }
        public Map<String, Integer> getIngredients() { return ingredients; }
        public String getOutputPotion() { return outputPotion; }
        public int getOutputAmount() { return outputAmount; }
        public int getDurationSeconds() { return durationSeconds; }
        public double getXpReward() { return xpReward; }
    }

    /** An active brew job for a player. */
    public static final class BrewJob {
        private final PotionRecipe recipe;
        private final long startTimeMillis;

        BrewJob(PotionRecipe recipe, long startTimeMillis) {
            this.recipe = Objects.requireNonNull(recipe, "recipe");
            this.startTimeMillis = startTimeMillis;
        }

        public PotionRecipe getRecipe() { return recipe; }
        public long getStartTimeMillis() { return startTimeMillis; }

        public boolean isComplete(long nowMillis) {
            return (nowMillis - startTimeMillis) >= (long) recipe.getDurationSeconds() * 1000L;
        }
    }

    private static final int MAX_LEVEL = 50;

    private static final AlchemyManager INSTANCE = new AlchemyManager();

    /** Static recipe catalogue keyed by recipe id. */
    private final Map<String, PotionRecipe> recipes;
    /** Per-player active brew job. */
    private final Map<UUID, BrewJob> activeJobs = new HashMap<>();
    /** Per-player accumulated alchemy XP. */
    private final Map<UUID, Double> alchemyXp = new HashMap<>();
    /** Per-player alchemy level cache. */
    private final Map<UUID, Integer> alchemyLevel = new HashMap<>();

    private AlchemyManager() {
        Map<String, PotionRecipe> map = new HashMap<>();
        addRecipe(map, "speed_i",      "Swiftness Potion I",
                Map.of("Sugar", 1, "Nether_Wart", 1),              "SPEED",         1, 20,  10.0);
        addRecipe(map, "speed_ii",     "Swiftness Potion II",
                Map.of("Sugar", 2, "Glowstone_Dust", 1, "Nether_Wart", 1), "SPEED_II", 1, 40, 25.0);
        addRecipe(map, "strength_i",   "Strength Potion I",
                Map.of("Blaze_Powder", 1, "Nether_Wart", 1),       "STRENGTH",      1, 30,  20.0);
        addRecipe(map, "strength_ii",  "Strength Potion II",
                Map.of("Blaze_Powder", 2, "Glowstone_Dust", 1, "Nether_Wart", 1), "STRENGTH_II", 1, 60, 45.0);
        addRecipe(map, "crit_chance",  "Crit Chance Potion",
                Map.of("Spider_Eye", 1, "Nether_Wart", 1),         "CRIT_CHANCE",   1, 25,  18.0);
        addRecipe(map, "crit_damage",  "Crit Damage Potion",
                Map.of("Spider_Eye", 2, "Glowstone_Dust", 1, "Nether_Wart", 1), "CRIT_DAMAGE", 1, 50, 35.0);
        addRecipe(map, "healing_i",    "Healing Potion I",
                Map.of("Glistering_Melon", 1, "Nether_Wart", 1),   "INSTANT_HEAL",  1, 20,  15.0);
        addRecipe(map, "healing_ii",   "Healing Potion II",
                Map.of("Glistering_Melon", 2, "Glowstone_Dust", 1, "Nether_Wart", 1), "INSTANT_HEAL_II", 1, 45, 35.0);
        addRecipe(map, "fire_resist",  "Fire Resistance Potion",
                Map.of("Magma_Cream", 1, "Nether_Wart", 1),        "FIRE_RESISTANCE", 1, 35, 20.0);
        addRecipe(map, "night_vision", "Night Vision Potion",
                Map.of("Golden_Carrot", 1, "Nether_Wart", 1),      "NIGHT_VISION",  1, 25,  18.0);
        addRecipe(map, "invisibility", "Invisibility Potion",
                Map.of("Fermented_Spider_Eye", 1, "Night_Vision_Potion", 1), "INVISIBILITY", 1, 50, 40.0);
        addRecipe(map, "splash_poison","Splash Poison Potion",
                Map.of("Spider_Eye", 1, "Nether_Wart", 1, "Gunpowder", 1), "POISON",  1, 40, 30.0);
        recipes = Collections.unmodifiableMap(map);
    }

    private static void addRecipe(Map<String, PotionRecipe> map, String id, String name,
                                  Map<String, Integer> ingredients, String output,
                                  int outputAmount, int duration, double xp) {
        map.put(id, new PotionRecipe(id, name, ingredients, output, outputAmount, duration, xp));
    }

    /**
     * Returns the single shared {@code AlchemyManager} instance.
     *
     * @return the singleton instance
     */
    public static AlchemyManager getInstance() {
        return INSTANCE;
    }

    /**
     * Returns an unmodifiable view of the recipe catalogue.
     *
     * @return recipe id → {@link PotionRecipe}
     */
    public Map<String, PotionRecipe> getRecipes() {
        return recipes;
    }

    /**
     * Returns the recipe for the given id, or {@code null} if not found.
     *
     * @param id recipe identifier
     * @return the recipe, or {@code null}
     */
    public PotionRecipe getRecipe(String id) {
        return recipes.get(id);
    }

    /**
     * Starts a brew job for the player.
     *
     * @param playerId       the player starting the brew
     * @param recipeId       the recipe to brew
     * @param nowMillis      current time in milliseconds
     * @throws IllegalArgumentException if the recipe is unknown
     * @throws IllegalStateException    if the player already has an active brew
     */
    public void startBrew(UUID playerId, String recipeId, long nowMillis) {
        Objects.requireNonNull(playerId, "playerId");
        PotionRecipe recipe = recipes.get(recipeId);
        if (recipe == null) {
            throw new IllegalArgumentException("Unknown recipe: " + recipeId);
        }
        if (activeJobs.containsKey(playerId)) {
            throw new IllegalStateException("Player already has an active brew job");
        }
        activeJobs.put(playerId, new BrewJob(recipe, nowMillis));
    }

    /**
     * Returns the player's active brew job, or {@code null} if none.
     *
     * @param playerId the player to look up
     * @return the active {@link BrewJob}, or {@code null}
     */
    public BrewJob getActiveJob(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        return activeJobs.get(playerId);
    }

    /**
     * Collects a completed brew job, awards XP, and removes the active job.
     *
     * @param playerId  the player collecting the job
     * @param nowMillis current time in milliseconds
     * @return the completed {@link BrewJob}
     * @throws IllegalStateException if there is no active job or it is not yet complete
     */
    public BrewJob collectBrew(UUID playerId, long nowMillis) {
        Objects.requireNonNull(playerId, "playerId");
        BrewJob job = activeJobs.get(playerId);
        if (job == null) {
            throw new IllegalStateException("No active brew job");
        }
        if (!job.isComplete(nowMillis)) {
            throw new IllegalStateException("Brew job is not yet complete");
        }
        activeJobs.remove(playerId);
        addXp(playerId, job.getRecipe().getXpReward());
        return job;
    }

    /**
     * Cancels the player's active brew job without awarding XP.
     *
     * @param playerId the player whose job is cancelled
     * @return {@code true} if a job was cancelled, {@code false} if there was none
     */
    public boolean cancelBrew(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        return activeJobs.remove(playerId) != null;
    }

    /**
     * Adds alchemy XP to the player and updates their level if thresholds are crossed.
     *
     * @param playerId the player receiving XP
     * @param amount   XP to add, must not be negative
     * @return the player's new total XP
     */
    public double addXp(UUID playerId, double amount) {
        Objects.requireNonNull(playerId, "playerId");
        if (amount < 0) {
            throw new IllegalArgumentException("amount must not be negative, got " + amount);
        }
        double total = alchemyXp.merge(playerId, amount, Double::sum);
        alchemyLevel.put(playerId, computeLevel(total));
        return total;
    }

    /**
     * Returns the player's current alchemy XP.
     *
     * @param playerId the player to look up
     * @return total XP, {@code 0} if none recorded
     */
    public double getXp(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        return alchemyXp.getOrDefault(playerId, 0.0);
    }

    /**
     * Returns the player's current alchemy level (1–{@value #MAX_LEVEL}).
     *
     * @param playerId the player to look up
     * @return alchemy level
     */
    public int getLevel(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        return alchemyLevel.getOrDefault(playerId, 1);
    }

    public void load(File dataFolder) {
        File file = new File(dataFolder, "alchemy.yml");
        if (!file.exists()) {
            return;
        }
        YamlConfiguration cfg = YamlConfiguration.loadConfiguration(file);
        alchemyXp.clear();
        alchemyLevel.clear();
        activeJobs.clear();
        for (String key : cfg.getKeys(false)) {
            try {
                UUID uuid = UUID.fromString(key);
                double xp = cfg.getDouble(key + ".xp", 0.0);
                if (xp > 0) {
                    alchemyXp.put(uuid, xp);
                    alchemyLevel.put(uuid, computeLevel(xp));
                }
                String recipeId = cfg.getString(key + ".activeBrew.recipeId");
                if (recipeId != null) {
                    PotionRecipe recipe = recipes.get(recipeId);
                    if (recipe != null) {
                        long startTime = cfg.getLong(key + ".activeBrew.startTime", 0L);
                        activeJobs.put(uuid, new BrewJob(recipe, startTime));
                    }
                }
            } catch (IllegalArgumentException ignored) {
                // skip malformed entries
            }
        }
    }

    public void save(File dataFolder) {
        File file = new File(dataFolder, "alchemy.yml");
        YamlConfiguration cfg = new YamlConfiguration();
        for (Map.Entry<UUID, Double> entry : alchemyXp.entrySet()) {
            cfg.set(entry.getKey().toString() + ".xp", entry.getValue());
        }
        for (Map.Entry<UUID, BrewJob> entry : activeJobs.entrySet()) {
            String base = entry.getKey().toString() + ".activeBrew";
            cfg.set(base + ".recipeId", entry.getValue().getRecipe().getId());
            cfg.set(base + ".startTime", entry.getValue().getStartTimeMillis());
        }
        try {
            cfg.save(file);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save alchemy.yml", e);
        }
    }

    /**
     * Computes the alchemy level for the given total XP.
     * Formula: level {@code n} requires {@code 50 * n^2} cumulative XP.
     */
    private static int computeLevel(double totalXp) {
        int level = 1;
        while (level < MAX_LEVEL) {
            double threshold = 50.0 * (level + 1) * (level + 1);
            if (totalXp < threshold) {
                break;
            }
            level++;
        }
        return level;
    }
}
