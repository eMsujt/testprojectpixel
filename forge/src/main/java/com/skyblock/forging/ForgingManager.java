package com.skyblock.forging;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * Tracks per-player forge slots for timed item forging.
 *
 * <p>Not thread-safe; synchronize externally if accessed from multiple threads.</p>
 */
public final class ForgingManager {

    private final Map<UUID, Map<Integer, ForgeSlot>> forgeSlots = new HashMap<>();

    /**
     * Starts forging an item in the given slot for the player.
     *
     * @param playerId       the player starting the forge
     * @param slotId         the index of the forge slot, must not be negative
     * @param itemId         the identifier of the item to forge
     * @param startTime      the time the forging process starts, in epoch milliseconds
     * @param durationMillis how long the forging process takes, must not be negative
     * @return the created forge slot entry
     * @throws IllegalStateException if the slot is already occupied
     * @throws IllegalArgumentException if {@code slotId} or {@code durationMillis} is negative
     * @throws NullPointerException if {@code playerId} or {@code itemId} is {@code null}
     */
    public ForgeSlot startForging(UUID playerId, int slotId, String itemId, long startTime, long durationMillis) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(itemId, "itemId");
        if (durationMillis < 0) {
            throw new IllegalArgumentException("durationMillis must not be negative, got " + durationMillis);
        }
        Map<Integer, ForgeSlot> slots = forgeSlots.computeIfAbsent(playerId, ignored -> new HashMap<>());
        if (slots.containsKey(slotId)) {
            throw new IllegalStateException("Forge slot " + slotId + " is already occupied");
        }
        ForgeSlot slot = new ForgeSlot(slotId, itemId, startTime, startTime + durationMillis);
        slots.put(slotId, slot);
        return slot;
    }

    /**
     * Returns the forge slot entry for the player, or {@code null} if the slot is empty.
     *
     * @param playerId the player to look up
     * @param slotId   the index of the forge slot
     * @return the forge slot entry, or {@code null} if nothing is being forged in it
     */
    public ForgeSlot getSlot(UUID playerId, int slotId) {
        Objects.requireNonNull(playerId, "playerId");
        Map<Integer, ForgeSlot> slots = forgeSlots.get(playerId);
        return slots == null ? null : slots.get(slotId);
    }

    /**
     * Collects the finished item from the given slot, freeing it for reuse.
     *
     * @param playerId the player collecting the item
     * @param slotId   the index of the forge slot
     * @param now      the current time in epoch milliseconds
     * @return the identifier of the forged item
     * @throws IllegalStateException if the slot is empty or the process has not completed
     */
    public String collectItem(UUID playerId, int slotId, long now) {
        ForgeSlot slot = getSlot(playerId, slotId);
        if (slot == null) {
            throw new IllegalStateException("Forge slot " + slotId + " is empty");
        }
        if (!slot.isComplete(now)) {
            throw new IllegalStateException(
                    "Forge slot " + slotId + " has " + slot.getRemainingTime(now) + "ms remaining");
        }
        forgeSlots.get(playerId).remove(slotId);
        return slot.getItemId();
    }

    /**
     * Cancels the forging process in the given slot, discarding the item.
     *
     * @param playerId the player cancelling the process
     * @param slotId   the index of the forge slot
     * @return {@code true} if a process was cancelled, {@code false} if the slot was empty
     */
    public boolean cancelForging(UUID playerId, int slotId) {
        Objects.requireNonNull(playerId, "playerId");
        Map<Integer, ForgeSlot> slots = forgeSlots.get(playerId);
        return slots != null && slots.remove(slotId) != null;
    }

    /**
     * Returns an unmodifiable view of the player's occupied forge slots, keyed by slot index.
     *
     * @param playerId the player to look up
     * @return the player's forge slots, empty if none are occupied
     */
    public Map<Integer, ForgeSlot> getSlots(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        Map<Integer, ForgeSlot> slots = forgeSlots.get(playerId);
        return slots == null ? Map.of() : Collections.unmodifiableMap(slots);
    }
}
