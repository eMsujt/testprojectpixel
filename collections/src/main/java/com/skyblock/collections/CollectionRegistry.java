package com.skyblock.collections;

import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import org.bukkit.Material;

/**
 * @deprecated Use {@link com.skyblock.core.util.CollectionRegistry} with
 * {@link com.skyblock.core.model.CollectionCategory} instead.
 */
@Deprecated
public final class CollectionRegistry {

    private final Map<Material, CollectionCategory> categoryByMaterial = new HashMap<>();
    private final Map<CollectionCategory, Set<Material>> materialsByCategory =
            new EnumMap<>(CollectionCategory.class);

    /**
     * Registers a material under a collection category.
     *
     * @param material the material to register
     * @param category the category it belongs to
     * @throws IllegalStateException if the material is already registered
     */
    public synchronized void register(Material material, CollectionCategory category) {
        Objects.requireNonNull(material, "material");
        Objects.requireNonNull(category, "category");
        if (categoryByMaterial.containsKey(material)) {
            throw new IllegalStateException(material + " is already registered under "
                    + categoryByMaterial.get(material));
        }
        categoryByMaterial.put(material, category);
        materialsByCategory.computeIfAbsent(category, k -> new LinkedHashSet<>()).add(material);
    }

    /**
     * Returns the category for the given material, or {@code null} if not
     * registered.
     *
     * @param material the material to look up
     * @return the category, or {@code null}
     */
    public synchronized CollectionCategory getCategory(Material material) {
        return categoryByMaterial.get(material);
    }

    /**
     * Returns an immutable snapshot of all materials registered under a
     * category.
     *
     * @param category the category to query
     * @return the registered materials; empty if none
     */
    public synchronized Set<Material> getMaterials(CollectionCategory category) {
        Objects.requireNonNull(category, "category");
        Set<Material> set = materialsByCategory.get(category);
        return set == null ? Set.of() : Collections.unmodifiableSet(new LinkedHashSet<>(set));
    }

    /**
     * Returns whether a material is registered in any collection.
     *
     * @param material the material to test
     * @return {@code true} if the material is registered
     */
    public synchronized boolean isRegistered(Material material) {
        return categoryByMaterial.containsKey(material);
    }
}
