package com.skyblock.core.accessory.manager;

import com.skyblock.core.model.AccessoryRarity;
import com.skyblock.core.talisman.manager.TalismanManager;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * Singleton manager tracking the rarity of each accessory a player owns.
 *
 * <p>Rarity determines the stat multiplier applied to an accessory's bonuses
 * when it is active in the accessory bag.</p>
 *
 * <p>Not thread-safe; synchronize externally if needed.</p>
 */
public final class AccessoryManager {

    private static final AccessoryManager INSTANCE = new AccessoryManager();

    /** Per-player map of accessory type to its assigned rarity. */
    private final Map<UUID, Map<TalismanManager.TalismanType, AccessoryRarity>> playerAccessories = new HashMap<>();

    private AccessoryManager() {}

    /**
     * Returns the single shared {@code AccessoryManager} instance.
     *
     * @return the singleton instance
     */
    public static AccessoryManager getInstance() {
        return INSTANCE;
    }

    /**
     * Assigns a rarity to an accessory for the given player.
     *
     * @param playerId the player's UUID, must not be null
     * @param type     the accessory type, must not be null
     * @param rarity   the rarity to assign, must not be null
     */
    public void setRarity(UUID playerId, TalismanManager.TalismanType type, AccessoryRarity rarity) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(type, "type");
        Objects.requireNonNull(rarity, "rarity");
        playerAccessories.computeIfAbsent(playerId, id -> new HashMap<>()).put(type, rarity);
    }

    /**
     * Returns the rarity of an accessory for the given player, or {@link AccessoryRarity#COMMON}
     * if not explicitly set.
     *
     * @param playerId the player's UUID, must not be null
     * @param type     the accessory type, must not be null
     * @return the assigned rarity
     */
    public AccessoryRarity getRarity(UUID playerId, TalismanManager.TalismanType type) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(type, "type");
        Map<TalismanManager.TalismanType, AccessoryRarity> accessories = playerAccessories.get(playerId);
        if (accessories == null) {
            return AccessoryRarity.COMMON;
        }
        return accessories.getOrDefault(type, AccessoryRarity.COMMON);
    }

    /**
     * Removes the rarity assignment for an accessory from the given player.
     *
     * @param playerId the player's UUID, must not be null
     * @param type     the accessory type, must not be null
     * @return {@code true} if a rarity was removed, {@code false} if none was set
     */
    public boolean removeAccessory(UUID playerId, TalismanManager.TalismanType type) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(type, "type");
        Map<TalismanManager.TalismanType, AccessoryRarity> accessories = playerAccessories.get(playerId);
        return accessories != null && accessories.remove(type) != null;
    }

    /**
     * Returns an unmodifiable view of all accessory rarities assigned to the given player.
     *
     * @param playerId the player's UUID, must not be null
     * @return map of accessory type to rarity; empty if none assigned
     */
    public Map<TalismanManager.TalismanType, AccessoryRarity> getAccessories(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        Map<TalismanManager.TalismanType, AccessoryRarity> accessories = playerAccessories.get(playerId);
        return accessories == null ? Collections.emptyMap() : Collections.unmodifiableMap(accessories);
    }

    /**
     * Clears all accessory rarity assignments for the given player.
     *
     * @param playerId the player's UUID, must not be null
     */
    public void clearAccessories(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        playerAccessories.remove(playerId);
    }
}
