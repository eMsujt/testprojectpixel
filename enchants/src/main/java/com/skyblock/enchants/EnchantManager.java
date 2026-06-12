package com.skyblock.enchants;

import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Registry of the custom enchants available in SkyBlock.
 *
 * <p>Enchants are identified by a case-insensitive id. Each enchant
 * carries a display name and a maximum level that callers can validate
 * levels against. Not thread-safe; synchronize externally if accessed
 * from multiple threads.</p>
 */
public final class EnchantManager {

    private final Map<String, CustomEnchant> enchantRegistry = new HashMap<>();

    /**
     * Registers a new custom enchant.
     *
     * @param id          the unique enchant id, must not be null or blank
     * @param displayName the name shown to players, must not be null or blank
     * @param maxLevel    the highest level the enchant can reach, must be at least 1
     * @return the registered enchant
     * @throws IllegalArgumentException if an argument is invalid or the id is already registered
     */
    public CustomEnchant registerEnchant(String id, String displayName, int maxLevel) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("id must not be null or blank");
        }
        if (displayName == null || displayName.isBlank()) {
            throw new IllegalArgumentException("displayName must not be null or blank");
        }
        if (maxLevel < 1) {
            throw new IllegalArgumentException("maxLevel must be at least 1: " + maxLevel);
        }
        String key = normalize(id);
        if (enchantRegistry.containsKey(key)) {
            throw new IllegalArgumentException("enchant already registered: " + id);
        }
        CustomEnchant enchant = new CustomEnchant(key, displayName, maxLevel);
        enchantRegistry.put(key, enchant);
        return enchant;
    }

    /**
     * Removes an enchant from the registry.
     *
     * @param id the enchant id
     * @return {@code true} if the enchant was registered and has been removed
     */
    public boolean unregisterEnchant(String id) {
        if (id == null) {
            return false;
        }
        return enchantRegistry.remove(normalize(id)) != null;
    }

    /**
     * Looks up an enchant by id.
     *
     * @param id the enchant id
     * @return the enchant, or {@code null} if not registered
     */
    public CustomEnchant getEnchant(String id) {
        if (id == null) {
            return null;
        }
        return enchantRegistry.get(normalize(id));
    }

    /**
     * Returns whether an enchant is registered.
     *
     * @param id the enchant id
     * @return {@code true} if the enchant is registered
     */
    public boolean isRegistered(String id) {
        return getEnchant(id) != null;
    }

    /**
     * Returns whether a level is valid for a registered enchant.
     *
     * @param id    the enchant id
     * @param level the level to check
     * @return {@code true} if the enchant is registered and the level is
     *         between 1 and the enchant's maximum level
     */
    public boolean isValidLevel(String id, int level) {
        CustomEnchant enchant = getEnchant(id);
        return enchant != null && level >= 1 && level <= enchant.getMaxLevel();
    }

    /**
     * Returns all registered enchants keyed by id.
     *
     * @return an unmodifiable view of the registry
     */
    public Map<String, CustomEnchant> getEnchants() {
        return Collections.unmodifiableMap(enchantRegistry);
    }

    /**
     * Removes all enchants from the registry.
     */
    public void clear() {
        enchantRegistry.clear();
    }

    private static String normalize(String id) {
        return id.toLowerCase(Locale.ROOT);
    }

    /**
     * A custom enchant definition. Instances are created through
     * {@link EnchantManager#registerEnchant(String, String, int)}.
     */
    public static final class CustomEnchant {

        private final String id;
        private final String displayName;
        private final int maxLevel;

        private CustomEnchant(String id, String displayName, int maxLevel) {
            this.id = id;
            this.displayName = displayName;
            this.maxLevel = maxLevel;
        }

        /** @return the unique enchant id, normalized to lower case */
        public String getId() {
            return id;
        }

        /** @return the name shown to players */
        public String getDisplayName() {
            return displayName;
        }

        /** @return the highest level the enchant can reach */
        public int getMaxLevel() {
            return maxLevel;
        }
    }
}
