package com.skyblock.collections;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import org.bukkit.Material;

/**
 * Tracks each player's collection progress per {@link Material}.
 *
 * <p>Only materials registered in the backing {@link CollectionRegistry}
 * accrue progress; unregistered materials are rejected. Not thread-safe;
 * synchronize externally if accessed from multiple threads.</p>
 */
public final class CollectionManager {

    private final CollectionRegistry registry;
    private final Map<UUID, Map<Material, Long>> playerProgress = new HashMap<>();

    /**
     * Creates a manager backed by the given registry.
     *
     * @param registry the registry defining which materials are collectable
     */
    public CollectionManager(CollectionRegistry registry) {
        this.registry = Objects.requireNonNull(registry, "registry");
    }

    /**
     * Adds collected items to the player's progress for a material.
     *
     * @param playerId the player's UUID
     * @param material the material collected
     * @param amount   the number of items collected, must be positive
     * @return the player's total for the material after the change
     * @throws IllegalArgumentException if the amount is not positive or the
     *         material is not registered in any collection
     */
    public long addProgress(UUID playerId, Material material, long amount) {
        Objects.requireNonNull(material, "material");
        if (amount <= 0) {
            throw new IllegalArgumentException("amount must be positive: " + amount);
        }
        if (!registry.isRegistered(material)) {
            throw new IllegalArgumentException(material + " is not a registered collection");
        }
        return playerProgress.computeIfAbsent(playerId, id -> new HashMap<>())
                .merge(material, amount, Long::sum);
    }

    /**
     * Returns the player's collected total for a material.
     *
     * @param playerId the player's UUID
     * @param material the material to look up
     * @return the collected total, or {@code 0} if never collected
     */
    public long getProgress(UUID playerId, Material material) {
        Objects.requireNonNull(material, "material");
        Map<Material, Long> progress = playerProgress.get(playerId);
        if (progress == null) {
            return 0L;
        }
        return progress.getOrDefault(material, 0L);
    }

    /**
     * Returns an immutable snapshot of all of the player's collection totals.
     *
     * @param playerId the player's UUID
     * @return the player's totals by material; empty if none
     */
    public Map<Material, Long> getProgress(UUID playerId) {
        Map<Material, Long> progress = playerProgress.get(playerId);
        return progress == null
                ? Map.of()
                : Collections.unmodifiableMap(new HashMap<>(progress));
    }

    /**
     * Removes all of the player's collection progress.
     *
     * @param playerId the player's UUID
     */
    public void resetProgress(UUID playerId) {
        playerProgress.remove(playerId);
    }
}
