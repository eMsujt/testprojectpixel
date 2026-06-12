package com.skyblock.wardrobe;

import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Manages per-player wardrobes: each player has a fixed number of numbered
 * slots, each of which can hold a stored {@link ArmorSet}, and at most one
 * slot may be equipped at a time.
 *
 * <p>Wardrobes are stored in a {@link ConcurrentHashMap} keyed by player id,
 * with per-wardrobe state guarded by the wardrobe's own monitor, so all
 * operations are thread-safe.</p>
 */
public final class WardrobeManager {

    /** The default number of wardrobe slots a player starts with. */
    public static final int DEFAULT_SLOT_COUNT = 9;

    /** The maximum number of wardrobe slots a player can have. */
    public static final int MAX_SLOT_COUNT = 18;

    /** The armor pieces a wardrobe slot can hold. */
    public enum ArmorPiece {
        HELMET,
        CHESTPLATE,
        LEGGINGS,
        BOOTS
    }

    /** An immutable snapshot of the armor pieces stored in a wardrobe slot, keyed by piece. */
    public static final class ArmorSet {

        private final Map<ArmorPiece, String> pieces;

        private ArmorSet(Map<ArmorPiece, String> pieces) {
            this.pieces = Collections.unmodifiableMap(new EnumMap<>(pieces));
        }

        /** Returns an unmodifiable view of the stored pieces, keyed by piece; absent pieces are empty. */
        public Map<ArmorPiece, String> getPieces() {
            return pieces;
        }

        /** Returns the item id stored for the given piece, if any. */
        public Optional<String> getPiece(ArmorPiece piece) {
            return Optional.ofNullable(pieces.get(piece));
        }
    }

    /** A single player's wardrobe. */
    private static final class Wardrobe {

        private final Map<Integer, ArmorSet> slots = new ConcurrentHashMap<>();
        private int slotCount = DEFAULT_SLOT_COUNT;
        private int equippedSlot = -1;

        private synchronized int getSlotCount() {
            return slotCount;
        }

        private synchronized void unlockSlots(int newCount) {
            if (newCount <= slotCount) {
                throw new IllegalArgumentException(
                        "newCount must exceed the current slot count of " + slotCount);
            }
            if (newCount > MAX_SLOT_COUNT) {
                throw new IllegalArgumentException("newCount must not exceed " + MAX_SLOT_COUNT);
            }
            slotCount = newCount;
        }

        private synchronized void store(int slot, ArmorSet set) {
            requireSlot(slot);
            slots.put(slot, set);
        }

        private synchronized Optional<ArmorSet> get(int slot) {
            requireSlot(slot);
            return Optional.ofNullable(slots.get(slot));
        }

        private synchronized Optional<ArmorSet> remove(int slot) {
            requireSlot(slot);
            if (equippedSlot == slot) {
                equippedSlot = -1;
            }
            return Optional.ofNullable(slots.remove(slot));
        }

        private synchronized ArmorSet equip(int slot) {
            requireSlot(slot);
            ArmorSet set = slots.get(slot);
            if (set == null) {
                throw new IllegalStateException("slot " + slot + " is empty");
            }
            equippedSlot = slot;
            return set;
        }

        private synchronized boolean unequip() {
            if (equippedSlot < 0) {
                return false;
            }
            equippedSlot = -1;
            return true;
        }

        private synchronized Optional<Integer> getEquippedSlot() {
            return equippedSlot < 0 ? Optional.empty() : Optional.of(equippedSlot);
        }

        private void requireSlot(int slot) {
            if (slot < 0 || slot >= slotCount) {
                throw new IllegalArgumentException(
                        "slot must be between 0 and " + (slotCount - 1) + ", got " + slot);
            }
        }
    }

    private final ConcurrentHashMap<UUID, Wardrobe> wardrobes = new ConcurrentHashMap<>();

    /**
     * Returns the number of slots in the given player's wardrobe.
     *
     * @param player the player whose wardrobe to inspect
     * @return the player's slot count, {@value #DEFAULT_SLOT_COUNT} by default
     */
    public int getSlotCount(UUID player) {
        return wardrobe(player).getSlotCount();
    }

    /**
     * Increases the number of slots in the given player's wardrobe.
     *
     * @param player   the player whose wardrobe to grow
     * @param newCount the new slot count, greater than the current count and
     *                 at most {@value #MAX_SLOT_COUNT}
     */
    public void unlockSlots(UUID player, int newCount) {
        wardrobe(player).unlockSlots(newCount);
    }

    /**
     * Stores an armor set in a wardrobe slot, replacing any set already there.
     *
     * @param player the player whose wardrobe to update
     * @param slot   the slot to store into, from 0 to the player's slot count minus one
     * @param pieces the armor pieces to store, keyed by piece; must be non-empty
     */
    public void storeSet(UUID player, int slot, Map<ArmorPiece, String> pieces) {
        if (pieces == null || pieces.isEmpty()) {
            throw new IllegalArgumentException("pieces must be non-empty");
        }
        for (Map.Entry<ArmorPiece, String> entry : pieces.entrySet()) {
            if (entry.getValue() == null || entry.getValue().isBlank()) {
                throw new IllegalArgumentException("item id for " + entry.getKey() + " must be non-blank");
            }
        }
        wardrobe(player).store(slot, new ArmorSet(pieces));
    }

    /**
     * Returns the armor set stored in a wardrobe slot, if any.
     *
     * @param player the player whose wardrobe to inspect
     * @param slot   the slot to read, from 0 to the player's slot count minus one
     * @return the stored set, or empty if the slot is empty
     */
    public Optional<ArmorSet> getSet(UUID player, int slot) {
        return wardrobe(player).get(slot);
    }

    /**
     * Removes the armor set stored in a wardrobe slot, unequipping it first
     * if it was the equipped slot.
     *
     * @param player the player whose wardrobe to update
     * @param slot   the slot to clear, from 0 to the player's slot count minus one
     * @return the removed set, or empty if the slot was already empty
     */
    public Optional<ArmorSet> removeSet(UUID player, int slot) {
        return wardrobe(player).remove(slot);
    }

    /**
     * Equips the armor set stored in a wardrobe slot, replacing any
     * previously equipped slot.
     *
     * @param player the player equipping the set
     * @param slot   the slot to equip, from 0 to the player's slot count minus one
     * @return the equipped set
     * @throws IllegalStateException if the slot is empty
     */
    public ArmorSet equip(UUID player, int slot) {
        return wardrobe(player).equip(slot);
    }

    /**
     * Unequips the given player's currently equipped slot, if any.
     *
     * @param player the player to unequip
     * @return {@code true} if a slot was equipped and is now unequipped
     */
    public boolean unequip(UUID player) {
        return wardrobe(player).unequip();
    }

    /**
     * Returns the slot the given player currently has equipped, if any.
     *
     * @param player the player whose wardrobe to inspect
     * @return the equipped slot index, or empty if nothing is equipped
     */
    public Optional<Integer> getEquippedSlot(UUID player) {
        return wardrobe(player).getEquippedSlot();
    }

    private Wardrobe wardrobe(UUID player) {
        if (player == null) {
            throw new IllegalArgumentException("player must be non-null");
        }
        return wardrobes.computeIfAbsent(player, id -> new Wardrobe());
    }
}
