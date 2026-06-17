package com.skyblock.core.manager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * Canonical singleton for SkyBlock brewing-stand potions and active potion effects.
 *
 * <p>Holds the static {@link PotionRecipe} catalogue (keyed by recipe id), each
 * recipe producing a levelled, timed {@link PotionType} effect. Recipes may be
 * brewed as normal or splash variants; splash potions trade a portion of their
 * duration for area application. Active effects are tracked per player with an
 * expiry timestamp and can be applied, queried, refreshed, and expired.</p>
 *
 * <p>Not thread-safe; synchronize externally if accessed from multiple threads.</p>
 */
public final class PotionManager {

    /** The effect a brewed potion grants when consumed. */
    public enum PotionType {
        SPEED("Speed"),
        STRENGTH("Strength"),
        CRITICAL("Critical"),
        HEALING("Healing"),
        REGENERATION("Regeneration"),
        FIRE_RESISTANCE("Fire Resistance"),
        NIGHT_VISION("Night Vision"),
        WATER_BREATHING("Water Breathing"),
        INVISIBILITY("Invisibility"),
        STUN("Stun");

        private final String displayName;

        PotionType(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    /** A brewing-stand recipe producing a levelled, timed potion effect. */
    public static final class PotionRecipe {
        private final String id;
        private final PotionType type;
        private final int level;
        /** Base effect duration in seconds for the non-splash variant. */
        private final int durationSeconds;
        /** Ingredient name → required quantity. */
        private final Map<String, Integer> ingredients;

        public PotionRecipe(String id, PotionType type, int level, int durationSeconds,
                            Map<String, Integer> ingredients) {
            this.id = Objects.requireNonNull(id, "id");
            this.type = Objects.requireNonNull(type, "type");
            if (level < 1) {
                throw new IllegalArgumentException("level must be at least 1, got " + level);
            }
            if (durationSeconds < 0) {
                throw new IllegalArgumentException("durationSeconds must not be negative, got " + durationSeconds);
            }
            this.level = level;
            this.durationSeconds = durationSeconds;
            this.ingredients = Collections.unmodifiableMap(new HashMap<>(ingredients));
        }

        public String getId() { return id; }
        public PotionType getType() { return type; }
        public int getLevel() { return level; }
        public int getDurationSeconds() { return durationSeconds; }
        public Map<String, Integer> getIngredients() { return ingredients; }
    }

    /** An active potion effect on a player, with its expiry. */
    public static final class ActiveEffect {
        private final PotionType type;
        private final int level;
        private final boolean splash;
        private final long expiresAtMillis;

        ActiveEffect(PotionType type, int level, boolean splash, long expiresAtMillis) {
            this.type = type;
            this.level = level;
            this.splash = splash;
            this.expiresAtMillis = expiresAtMillis;
        }

        public PotionType getType() { return type; }
        public int getLevel() { return level; }
        public boolean isSplash() { return splash; }
        public long getExpiresAtMillis() { return expiresAtMillis; }

        /** Returns whether this effect has expired at the given time. */
        public boolean isExpired(long nowMillis) {
            return nowMillis >= expiresAtMillis;
        }

        /** Returns the remaining time in milliseconds, or {@code 0} if expired. */
        public long getRemainingMillis(long nowMillis) {
            return Math.max(0L, expiresAtMillis - nowMillis);
        }
    }

    /**
     * Splash potions apply over an area at the cost of a fraction of their
     * duration. A splash potion lasts this proportion of the drinkable duration.
     */
    private static final double SPLASH_DURATION_FACTOR = 0.75;

    private static final PotionManager INSTANCE = new PotionManager();

    /** Static recipe catalogue keyed by recipe id. */
    private final Map<String, PotionRecipe> recipes;
    /** Per-player active effects, keyed by {@link PotionType} (one active per type). */
    private final Map<UUID, Map<PotionType, ActiveEffect>> activeEffects = new HashMap<>();

    private PotionManager() {
        Map<String, PotionRecipe> map = new HashMap<>();
        addRecipe(map, "speed_i", PotionType.SPEED, 1, 180,
                Map.of("Sugar", 1, "Nether_Wart", 1));
        addRecipe(map, "speed_ii", PotionType.SPEED, 2, 90,
                Map.of("Sugar", 2, "Glowstone_Dust", 1, "Nether_Wart", 1));
        addRecipe(map, "strength_i", PotionType.STRENGTH, 1, 180,
                Map.of("Blaze_Powder", 1, "Nether_Wart", 1));
        addRecipe(map, "strength_ii", PotionType.STRENGTH, 2, 90,
                Map.of("Blaze_Powder", 2, "Glowstone_Dust", 1, "Nether_Wart", 1));
        addRecipe(map, "critical", PotionType.CRITICAL, 1, 120,
                Map.of("Spider_Eye", 1, "Nether_Wart", 1));
        addRecipe(map, "healing_i", PotionType.HEALING, 1, 0,
                Map.of("Glistering_Melon", 1, "Nether_Wart", 1));
        addRecipe(map, "regeneration", PotionType.REGENERATION, 1, 45,
                Map.of("Ghast_Tear", 1, "Nether_Wart", 1));
        addRecipe(map, "fire_resistance", PotionType.FIRE_RESISTANCE, 1, 180,
                Map.of("Magma_Cream", 1, "Nether_Wart", 1));
        addRecipe(map, "night_vision", PotionType.NIGHT_VISION, 1, 180,
                Map.of("Golden_Carrot", 1, "Nether_Wart", 1));
        addRecipe(map, "water_breathing", PotionType.WATER_BREATHING, 1, 180,
                Map.of("Pufferfish", 1, "Nether_Wart", 1));
        addRecipe(map, "invisibility", PotionType.INVISIBILITY, 1, 180,
                Map.of("Fermented_Spider_Eye", 1, "Night_Vision_Potion", 1));
        recipes = Collections.unmodifiableMap(map);
    }

    private static void addRecipe(Map<String, PotionRecipe> map, String id, PotionType type,
                                  int level, int durationSeconds, Map<String, Integer> ingredients) {
        map.put(id, new PotionRecipe(id, type, level, durationSeconds, ingredients));
    }

    /**
     * Returns the single shared {@code PotionManager} instance.
     *
     * @return the singleton instance
     */
    public static PotionManager getInstance() {
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
     * Returns the effective duration in seconds for the recipe brewed as a
     * normal or splash variant. Splash potions last {@value #SPLASH_DURATION_FACTOR}
     * of the drinkable duration. Instant effects (zero duration) are unaffected.
     *
     * @param recipe the recipe to measure
     * @param splash whether the splash variant is brewed
     * @return the effective duration in seconds
     */
    public int getEffectiveDurationSeconds(PotionRecipe recipe, boolean splash) {
        Objects.requireNonNull(recipe, "recipe");
        if (!splash || recipe.getDurationSeconds() == 0) {
            return recipe.getDurationSeconds();
        }
        return (int) Math.round(recipe.getDurationSeconds() * SPLASH_DURATION_FACTOR);
    }

    /**
     * Applies the given recipe's effect to a player, starting now. An existing
     * active effect of the same type is replaced. Instant (zero-duration) effects
     * are not retained as active effects.
     *
     * @param playerId  the player to apply the effect to
     * @param recipeId  the recipe being consumed
     * @param splash    whether the splash variant is consumed
     * @param nowMillis current time in milliseconds
     * @return the applied {@link ActiveEffect}, or {@code null} for instant effects
     * @throws IllegalArgumentException if the recipe is unknown
     */
    public ActiveEffect applyEffect(UUID playerId, String recipeId, boolean splash, long nowMillis) {
        Objects.requireNonNull(playerId, "playerId");
        PotionRecipe recipe = recipes.get(recipeId);
        if (recipe == null) {
            throw new IllegalArgumentException("Unknown recipe: " + recipeId);
        }
        int duration = getEffectiveDurationSeconds(recipe, splash);
        if (duration == 0) {
            return null;
        }
        ActiveEffect effect = new ActiveEffect(recipe.getType(), recipe.getLevel(), splash,
                nowMillis + (long) duration * 1000L);
        activeEffects.computeIfAbsent(playerId, ignored -> new HashMap<>())
                .put(recipe.getType(), effect);
        return effect;
    }

    /**
     * Returns the player's active effect of the given type, or {@code null} if
     * none is active (or it has expired and not yet been cleared).
     *
     * @param playerId  the player to look up
     * @param type      the effect type
     * @param nowMillis current time in milliseconds
     * @return the active {@link ActiveEffect}, or {@code null}
     */
    public ActiveEffect getActiveEffect(UUID playerId, PotionType type, long nowMillis) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(type, "type");
        Map<PotionType, ActiveEffect> effects = activeEffects.get(playerId);
        if (effects == null) {
            return null;
        }
        ActiveEffect effect = effects.get(type);
        return (effect == null || effect.isExpired(nowMillis)) ? null : effect;
    }

    /**
     * Returns whether the player currently has a non-expired effect of the type.
     *
     * @param playerId  the player to look up
     * @param type      the effect type
     * @param nowMillis current time in milliseconds
     * @return {@code true} if the effect is active
     */
    public boolean hasEffect(UUID playerId, PotionType type, long nowMillis) {
        return getActiveEffect(playerId, type, nowMillis) != null;
    }

    /**
     * Returns the player's currently active (non-expired) effects.
     *
     * @param playerId  the player to look up
     * @param nowMillis current time in milliseconds
     * @return a list of active effects, empty if none
     */
    public List<ActiveEffect> getActiveEffects(UUID playerId, long nowMillis) {
        Objects.requireNonNull(playerId, "playerId");
        Map<PotionType, ActiveEffect> effects = activeEffects.get(playerId);
        if (effects == null) {
            return Collections.emptyList();
        }
        List<ActiveEffect> active = new ArrayList<>();
        for (ActiveEffect effect : effects.values()) {
            if (!effect.isExpired(nowMillis)) {
                active.add(effect);
            }
        }
        return active;
    }

    /**
     * Removes the player's active effect of the given type.
     *
     * @param playerId the player whose effect is removed
     * @param type     the effect type
     * @return {@code true} if an effect was removed
     */
    public boolean removeEffect(UUID playerId, PotionType type) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(type, "type");
        Map<PotionType, ActiveEffect> effects = activeEffects.get(playerId);
        return effects != null && effects.remove(type) != null;
    }

    /**
     * Drops all expired effects for the player, freeing their storage.
     *
     * @param playerId  the player to clean up
     * @param nowMillis current time in milliseconds
     * @return the number of expired effects removed
     */
    public int expireEffects(UUID playerId, long nowMillis) {
        Objects.requireNonNull(playerId, "playerId");
        Map<PotionType, ActiveEffect> effects = activeEffects.get(playerId);
        if (effects == null) {
            return 0;
        }
        int removed = 0;
        Iterator<ActiveEffect> it = effects.values().iterator();
        while (it.hasNext()) {
            if (it.next().isExpired(nowMillis)) {
                it.remove();
                removed++;
            }
        }
        if (effects.isEmpty()) {
            activeEffects.remove(playerId);
        }
        return removed;
    }

    /**
     * Clears all active effects for the player.
     *
     * @param playerId the player to clear
     */
    public void clearEffects(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        activeEffects.remove(playerId);
    }
}
