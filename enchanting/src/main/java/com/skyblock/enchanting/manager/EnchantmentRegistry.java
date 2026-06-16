package com.skyblock.enchanting.manager;

import com.skyblock.enchanting.model.SkyBlockEnchantment;

import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Registry of {@link SkyBlockEnchantment}s keyed by their string id.
 *
 * <p>Ids are case-insensitive and stored in lower case, e.g.
 * {@code "first_strike"} for {@link SkyBlockEnchantment#FIRST_STRIKE}.
 * All built-in enchantments are registered on construction. Not
 * thread-safe; synchronize externally if accessed from multiple
 * threads.</p>
 */
public final class EnchantmentRegistry {

    private final Map<String, SkyBlockEnchantment> registry = new HashMap<>();

    /**
     * Creates a registry pre-populated with every
     * {@link SkyBlockEnchantment}, keyed by its lower-case enum name.
     */
    public EnchantmentRegistry() {
        for (SkyBlockEnchantment enchantment : SkyBlockEnchantment.values()) {
            registry.put(normalize(enchantment.name()), enchantment);
        }
    }

    /**
     * Registers an enchantment under the given id, replacing any
     * previous mapping for that id.
     *
     * @param id          the enchantment id, must not be null or blank
     * @param enchantment the enchantment, must not be null
     * @throws IllegalArgumentException if an argument is null or the id is blank
     */
    public void register(String id, SkyBlockEnchantment enchantment) {
        if (id == null || id.trim().isEmpty() || enchantment == null) {
            throw new IllegalArgumentException("id and enchantment must not be null or blank");
        }
        registry.put(normalize(id), enchantment);
    }

    /**
     * Removes the enchantment registered under the given id.
     *
     * @param id the enchantment id
     * @return {@code true} if an enchantment was registered and has been removed
     */
    public boolean unregister(String id) {
        if (id == null) {
            return false;
        }
        return registry.remove(normalize(id)) != null;
    }

    /**
     * Looks up an enchantment by id.
     *
     * @param id the enchantment id, case-insensitive
     * @return the registered enchantment, or {@code null} if none is registered
     */
    public SkyBlockEnchantment get(String id) {
        if (id == null) {
            return null;
        }
        return registry.get(normalize(id));
    }

    /**
     * Returns whether an enchantment is registered under the given id.
     *
     * @param id the enchantment id, case-insensitive
     * @return {@code true} if an enchantment is registered
     */
    public boolean isRegistered(String id) {
        return get(id) != null;
    }

    /**
     * Returns all registered enchantments keyed by id.
     *
     * @return an unmodifiable view of the registry
     */
    public Map<String, SkyBlockEnchantment> getAll() {
        return Collections.unmodifiableMap(registry);
    }

    private static String normalize(String id) {
        return id.trim().toLowerCase(Locale.ROOT);
    }
}
