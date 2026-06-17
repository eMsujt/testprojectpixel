package com.skyblock.core.mob;

import com.skyblock.core.manager.MobManager;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.LivingEntity;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * Singleton manager for spawned custom-mob instances.
 *
 * <p>Tracks living entities that were spawned via {@link #spawnMob} by mapping
 * their entity {@link UUID} to the {@link MobManager.MobDefinition} that
 * describes them. Call {@link #remove(UUID)} when an entity dies or leaves
 * the world so the map stays clean.</p>
 *
 * <p>Not thread-safe; synchronize externally if needed.</p>
 */
public final class CustomMobManager {

    private static final CustomMobManager INSTANCE = new CustomMobManager();

    /** Tracks living custom-mob instances: entity UUID → definition. */
    private final Map<UUID, MobManager.MobDefinition> activeEntities = new HashMap<>();

    private CustomMobManager() {
    }

    /**
     * Returns the single shared {@code CustomMobManager} instance.
     *
     * @return the singleton instance
     */
    public static CustomMobManager getInstance() {
        return INSTANCE;
    }

    /**
     * Spawns a custom mob at the given location, applies its health and display
     * name, and begins tracking it.
     *
     * @param definition the mob definition from {@link MobManager}; must not be null
     * @param location   the location at which to spawn; must not be null
     * @return the spawned {@link LivingEntity}
     * @throws IllegalArgumentException if the world is null or the entity type
     *                                  does not produce a {@link LivingEntity}
     */
    public LivingEntity spawnMob(MobManager.MobDefinition definition, Location location) {
        Objects.requireNonNull(definition, "definition");
        Objects.requireNonNull(location, "location");
        if (location.getWorld() == null) {
            throw new IllegalArgumentException("location has no world");
        }

        org.bukkit.entity.Entity raw =
                location.getWorld().spawnEntity(location, definition.getEntityType());
        if (!(raw instanceof LivingEntity)) {
            raw.remove();
            throw new IllegalArgumentException(
                    "entityType " + definition.getEntityType() + " is not a LivingEntity");
        }

        LivingEntity entity = (LivingEntity) raw;
        entity.setCustomName(definition.getDisplayName());
        entity.setCustomNameVisible(true);

        AttributeInstance maxHealth = entity.getAttribute(Attribute.MAX_HEALTH);
        if (maxHealth != null) {
            maxHealth.setBaseValue(definition.getHealth());
            entity.setHealth(definition.getHealth());
        }

        activeEntities.put(entity.getUniqueId(), definition);
        return entity;
    }

    /**
     * Returns {@code true} if the entity with the given UUID was spawned by
     * this manager and has not yet been removed.
     *
     * @param entityId the entity's UUID
     * @return whether it is a tracked custom mob
     */
    public boolean isCustomMob(UUID entityId) {
        return activeEntities.containsKey(entityId);
    }

    /**
     * Returns the {@link MobManager.MobDefinition} for the given entity UUID,
     * or {@code null} if it is not a tracked custom mob.
     *
     * @param entityId the entity's UUID
     * @return the definition, or {@code null}
     */
    public MobManager.MobDefinition getDefinition(UUID entityId) {
        return activeEntities.get(entityId);
    }

    /**
     * Stops tracking the entity with the given UUID. Call this on entity death
     * or world removal to prevent stale entries accumulating.
     *
     * @param entityId the entity's UUID
     */
    public void remove(UUID entityId) {
        activeEntities.remove(entityId);
    }

    /**
     * Returns an unmodifiable view of all tracked entities keyed by entity UUID.
     *
     * @return immutable map of entity UUID → definition
     */
    public Map<UUID, MobManager.MobDefinition> getActiveEntities() {
        return Collections.unmodifiableMap(activeEntities);
    }
}
