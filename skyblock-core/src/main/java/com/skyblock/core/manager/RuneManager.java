package com.skyblock.core.manager;

import com.skyblock.core.util.SkyblockUtils;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Canonical singleton for SkyBlock runes applied to items.
 *
 * <p>Holds the static {@link RuneType} registry, each rune offering a number of
 * levels (I, II, III, …) and a cosmetic {@code visual} describing the effect it
 * paints on the item it is applied to. An item — identified by an opaque item
 * key — may carry at most one rune at a time; applying a new rune replaces any
 * existing one. Applied runes can be queried, replaced, and removed, and a
 * human-readable visual line can be rendered for display.</p>
 *
 * <p>Not thread-safe; synchronize externally if accessed from multiple threads.</p>
 */
public final class RuneManager {

    /** A cosmetic rune that can be applied to an item, with a fixed level cap. */
    public enum RuneType {
        ENCHANT("Enchant", 3, "swirling enchantment glyphs"),
        MUSIC("Music", 3, "floating musical notes"),
        GRAND_SEARING("Grand Searing", 3, "blazing embers"),
        ICE_SKATES("Ice Skates", 1, "a frosted trail"),
        BARK("Bark", 3, "drifting bark flakes"),
        SMOKEY("Smokey", 3, "rising smoke"),
        GOLDEN("Golden", 3, "a gilded shimmer"),
        TIDAL("Tidal", 3, "cresting waves"),
        ZOMBIE_SLAYER("Zombie Slayer", 3, "rotting motes"),
        PRIMAL_FEAR("Primal Fear", 3, "pulsing shadow");

        private final String displayName;
        private final int maxLevel;
        private final String visual;

        RuneType(String displayName, int maxLevel, String visual) {
            this.displayName = displayName;
            this.maxLevel = maxLevel;
            this.visual = visual;
        }

        public String getDisplayName() { return displayName; }
        public int getMaxLevel() { return maxLevel; }
        public String getVisual() { return visual; }
    }

    /** A rune applied to an item at a particular level. */
    public static final class AppliedRune {
        private final RuneType type;
        private final int level;

        AppliedRune(RuneType type, int level) {
            this.type = type;
            this.level = level;
        }

        public RuneType getType() { return type; }
        public int getLevel() { return level; }
    }

    private static final RuneManager INSTANCE = new RuneManager();

    /** Static rune registry keyed by rune id (lower-cased enum name). */
    private final Map<String, RuneType> registry;
    /** Per-item applied rune, keyed by the item's opaque key (one rune per item). */
    private final Map<String, AppliedRune> appliedRunes = new HashMap<>();

    private RuneManager() {
        Map<String, RuneType> map = new LinkedHashMap<>();
        for (RuneType type : RuneType.values()) {
            map.put(type.name().toLowerCase(), type);
        }
        registry = Collections.unmodifiableMap(map);
    }

    /**
     * Returns the single shared {@code RuneManager} instance.
     *
     * @return the singleton instance
     */
    public static RuneManager getInstance() {
        return INSTANCE;
    }

    /**
     * Returns an unmodifiable view of the rune registry.
     *
     * @return rune id → {@link RuneType}
     */
    public Map<String, RuneType> getRegistry() {
        return registry;
    }

    /**
     * Returns the rune for the given id, or {@code null} if not found.
     *
     * @param runeId rune identifier (case-insensitive)
     * @return the rune, or {@code null}
     */
    public RuneType getRune(String runeId) {
        return runeId == null ? null : registry.get(runeId.toLowerCase());
    }

    /**
     * Applies a rune at the given level to an item, replacing any rune the item
     * already carries.
     *
     * @param itemKey the opaque key identifying the item
     * @param type    the rune to apply
     * @param level   the rune level, between 1 and the rune's max level
     * @return the resulting {@link AppliedRune}
     * @throws IllegalArgumentException if the level is outside the rune's range
     */
    public AppliedRune applyRune(String itemKey, RuneType type, int level) {
        Objects.requireNonNull(itemKey, "itemKey");
        Objects.requireNonNull(type, "type");
        if (level < 1 || level > type.getMaxLevel()) {
            throw new IllegalArgumentException("level must be between 1 and " + type.getMaxLevel()
                    + " for rune " + type.getDisplayName() + ", got " + level);
        }
        AppliedRune rune = new AppliedRune(type, level);
        appliedRunes.put(itemKey, rune);
        return rune;
    }

    /**
     * Returns the rune currently applied to the item, or {@code null} if none.
     *
     * @param itemKey the opaque key identifying the item
     * @return the applied rune, or {@code null}
     */
    public AppliedRune getAppliedRune(String itemKey) {
        Objects.requireNonNull(itemKey, "itemKey");
        return appliedRunes.get(itemKey);
    }

    /**
     * Returns whether the item currently carries a rune.
     *
     * @param itemKey the opaque key identifying the item
     * @return {@code true} if a rune is applied
     */
    public boolean hasRune(String itemKey) {
        Objects.requireNonNull(itemKey, "itemKey");
        return appliedRunes.containsKey(itemKey);
    }

    /**
     * Removes the rune applied to the item.
     *
     * @param itemKey the opaque key identifying the item
     * @return the removed {@link AppliedRune}, or {@code null} if none was applied
     */
    public AppliedRune removeRune(String itemKey) {
        Objects.requireNonNull(itemKey, "itemKey");
        return appliedRunes.remove(itemKey);
    }

    /**
     * Renders the cosmetic visual line for the item's applied rune.
     *
     * @param itemKey the opaque key identifying the item
     * @return the visual description, or {@code null} if no rune is applied
     */
    public String getRuneVisual(String itemKey) {
        AppliedRune rune = getAppliedRune(itemKey);
        return rune == null ? null : describeVisual(rune.getType(), rune.getLevel());
    }

    /**
     * Renders the cosmetic visual description for a rune at a given level, e.g.
     * {@code "Enchant III: swirling enchantment glyphs"}.
     *
     * @param type  the rune
     * @param level the rune level
     * @return the visual description
     */
    public String describeVisual(RuneType type, int level) {
        Objects.requireNonNull(type, "type");
        return type.getDisplayName() + " " + SkyblockUtils.toRoman(level) + ": " + type.getVisual();
    }
}
