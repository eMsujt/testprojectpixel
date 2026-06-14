package com.skyblock.plugin.accessories;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Tracks the accessories each player has equipped in their accessory bag.
 *
 * <p>A singleton holding, per player, an ordered {@link List} of equipped
 * accessory item ids. Each accessory id may only be equipped once.</p>
 */
public final class AccessoryBagManager {

    private static final AccessoryBagManager INSTANCE = new AccessoryBagManager();

    private final Map<UUID, List<String>> equipped = new HashMap<>();

    private AccessoryBagManager() {}

    public static AccessoryBagManager getInstance() {
        return INSTANCE;
    }

    /**
     * Equips the given accessory for the player.
     *
     * @return {@code true} if it was added, {@code false} if already equipped.
     */
    public boolean equip(UUID playerId, String accessoryId) {
        List<String> accessories = equipped.computeIfAbsent(playerId, k -> new ArrayList<>());
        if (accessories.contains(accessoryId)) {
            return false;
        }
        accessories.add(accessoryId);
        return true;
    }

    /**
     * Unequips the given accessory for the player.
     *
     * @return {@code true} if it was removed, {@code false} if it was not equipped.
     */
    public boolean unequip(UUID playerId, String accessoryId) {
        List<String> accessories = equipped.get(playerId);
        return accessories != null && accessories.remove(accessoryId);
    }

    /** Returns whether the player has the given accessory equipped. */
    public boolean isEquipped(UUID playerId, String accessoryId) {
        List<String> accessories = equipped.get(playerId);
        return accessories != null && accessories.contains(accessoryId);
    }

    /** Returns an unmodifiable view of the player's equipped accessories. */
    public List<String> getEquipped(UUID playerId) {
        List<String> accessories = equipped.get(playerId);
        if (accessories == null) {
            return Collections.emptyList();
        }
        return Collections.unmodifiableList(accessories);
    }
}
