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

    /** SkyBlock max-health values far exceed the vanilla 1024 attribute cap, so we
     *  keep the vanilla bar low and track the real SkyBlock health ourselves. */
    private static final double VANILLA_HEALTH_CAP = 1000.0;

    /** Tracks living custom-mob instances: entity UUID → definition. */
    private final Map<UUID, MobManager.MobDefinition> activeEntities = new HashMap<>();

    /** Real (SkyBlock) current health per custom mob; can be far above 1024. */
    private final Map<UUID, Double> skyblockHealth = new HashMap<>();

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
        applyTo(entity, definition);
        return entity;
    }

    /**
     * Turns an already-spawned vanilla entity into a custom mob: caps its vanilla
     * health bar, tracks the real SkyBlock health, sets the Hypixel-style name, and
     * gives it the mob's equipment. Used both by {@link #spawnMob} and by natural
     * Hub spawn-conversion.
     */
    public void applyTo(LivingEntity entity, MobManager.MobDefinition definition) {
        Objects.requireNonNull(entity, "entity");
        Objects.requireNonNull(definition, "definition");

        AttributeInstance maxHealth = entity.getAttribute(Attribute.MAX_HEALTH);
        if (maxHealth != null) {
            double vanillaHp = Math.min(definition.getHealth(), VANILLA_HEALTH_CAP);
            maxHealth.setBaseValue(vanillaHp);
            entity.setHealth(vanillaHp);
        }
        entity.setRemoveWhenFarAway(false);
        if (entity instanceof org.bukkit.entity.Ageable ageable) {
            ageable.setAdult();
        }
        equip(entity, definition);

        activeEntities.put(entity.getUniqueId(), definition);
        skyblockHealth.put(entity.getUniqueId(), definition.getHealth());
        updateName(entity, definition);
    }

    /** Gives the mob its themed gear (Crypt Ghoul / Golden Ghoul), never droppable. */
    private void equip(LivingEntity entity, MobManager.MobDefinition def) {
        org.bukkit.inventory.EntityEquipment eq = entity.getEquipment();
        if (eq == null) {
            return;
        }
        switch (def.getId()) {
            case "crypt_ghoul" -> {
                eq.setItemInMainHand(new org.bukkit.inventory.ItemStack(org.bukkit.Material.IRON_SWORD));
                eq.setChestplate(new org.bukkit.inventory.ItemStack(org.bukkit.Material.CHAINMAIL_CHESTPLATE));
            }
            case "golden_ghoul" -> {
                eq.setItemInMainHand(new org.bukkit.inventory.ItemStack(org.bukkit.Material.GOLDEN_SWORD));
                eq.setHelmet(new org.bukkit.inventory.ItemStack(org.bukkit.Material.GOLDEN_HELMET));
                eq.setChestplate(new org.bukkit.inventory.ItemStack(org.bukkit.Material.GOLDEN_CHESTPLATE));
                eq.setLeggings(new org.bukkit.inventory.ItemStack(org.bukkit.Material.GOLDEN_LEGGINGS));
                eq.setBoots(new org.bukkit.inventory.ItemStack(org.bukkit.Material.GOLDEN_BOOTS));
            }
            default -> { }
        }
        eq.setItemInMainHandDropChance(0f);
        eq.setHelmetDropChance(0f);
        eq.setChestplateDropChance(0f);
        eq.setLeggingsDropChance(0f);
        eq.setBootsDropChance(0f);
    }

    /**
     * Applies {@code amount} of SkyBlock damage to a custom mob and refreshes its
     * health-bar name. Returns {@code true} if this reduced it to 0 (the caller
     * should then kill the entity to trigger the death rewards/drops).
     */
    public boolean damageSkyblock(LivingEntity entity, double amount) {
        UUID id = entity.getUniqueId();
        Double cur = skyblockHealth.get(id);
        MobManager.MobDefinition def = activeEntities.get(id);
        if (cur == null || def == null) {
            return false;
        }
        double next = cur - amount;
        if (next <= 0.0) {
            skyblockHealth.put(id, 0.0);
            return true;
        }
        skyblockHealth.put(id, next);
        updateName(entity, def);
        return false;
    }

    /** Returns the custom mob's current SkyBlock health, or 0 if not tracked. */
    public double getSkyblockHealth(UUID entityId) {
        return skyblockHealth.getOrDefault(entityId, 0.0);
    }

    /** Sets the Hypixel-style name "[LvN] Name cur/max❤" on the entity. */
    private void updateName(LivingEntity entity, MobManager.MobDefinition def) {
        double cur = skyblockHealth.getOrDefault(entity.getUniqueId(), def.getHealth());
        entity.setCustomName("§8[§7Lv" + def.getBaseLevel() + "§8] §c" + def.getDisplayName()
                + " §a" + formatHp(cur) + "§f/§a" + formatHp(def.getHealth()) + "§c❤");
        entity.setCustomNameVisible(true);
    }

    private static String formatHp(double hp) {
        return String.format("%,d", Math.round(hp));
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
        skyblockHealth.remove(entityId);
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
