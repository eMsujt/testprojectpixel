package com.skyblock.core.wardrobe;

import org.bukkit.inventory.ItemStack;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

/**
 * Singleton managing per-player wardrobe (named armor outfits).
 *
 * <p>Each player may save up to {@link #MAX_OUTFITS} named outfits. An outfit
 * is a snapshot of the four armor slots (helmet, chestplate, leggings, boots).
 * Not thread-safe; synchronize externally if accessed from multiple threads.</p>
 */
public final class WardrobeManager {

    public enum WardrobeSlot {
        SLOT_1(1, "Slot 1"),
        SLOT_2(2, "Slot 2"),
        SLOT_3(3, "Slot 3"),
        SLOT_4(4, "Slot 4"),
        SLOT_5(5, "Slot 5"),
        SLOT_6(6, "Slot 6"),
        SLOT_7(7, "Slot 7"),
        SLOT_8(8, "Slot 8"),
        SLOT_9(9, "Slot 9"),
        SLOT_10(10, "Slot 10"),
        SLOT_11(11, "Slot 11"),
        SLOT_12(12, "Slot 12"),
        SLOT_13(13, "Slot 13"),
        SLOT_14(14, "Slot 14"),
        SLOT_15(15, "Slot 15"),
        SLOT_16(16, "Slot 16"),
        SLOT_17(17, "Slot 17"),
        SLOT_18(18, "Slot 18");

        private final int slotNumber;
        private final String displayName;

        WardrobeSlot(int slotNumber, String displayName) {
            this.slotNumber = slotNumber;
            this.displayName = displayName;
        }

        public int getSlotNumber() {
            return slotNumber;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    /** Maximum named outfits a player may store. */
    public static final int MAX_OUTFITS = 18;

    private static final WardrobeManager INSTANCE = new WardrobeManager();

    /** playerId → (outfitName → armor[4]) */
    private final Map<UUID, Map<String, ItemStack[]>> wardrobes = new HashMap<>();

    private WardrobeManager() {}

    /**
     * Returns the single shared {@code WardrobeManager} instance.
     *
     * @return the singleton instance
     */
    public static WardrobeManager getInstance() {
        return INSTANCE;
    }

    /**
     * Saves the given armor snapshot under {@code name} for the player.
     *
     * @param playerId the player's UUID, must not be null
     * @param name     the outfit name, must not be null or blank
     * @param armor    the four armor slots to snapshot (index 0-3)
     * @return {@code true} if saved, {@code false} if the player already has
     *         {@link #MAX_OUTFITS} outfits and {@code name} is a new entry
     */
    public boolean saveOutfit(UUID playerId, String name, ItemStack[] armor) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(name, "name");
        Objects.requireNonNull(armor, "armor");
        Map<String, ItemStack[]> outfits = wardrobes.computeIfAbsent(playerId, id -> new HashMap<>());
        if (!outfits.containsKey(name) && outfits.size() >= MAX_OUTFITS) {
            return false;
        }
        ItemStack[] snapshot = new ItemStack[4];
        for (int i = 0; i < 4; i++) {
            snapshot[i] = (i < armor.length && armor[i] != null) ? armor[i].clone() : null;
        }
        outfits.put(name, snapshot);
        return true;
    }

    /**
     * Returns a copy of the named outfit, or {@code null} if it does not exist.
     *
     * @param playerId the player's UUID, must not be null
     * @param name     the outfit name, must not be null
     * @return cloned armor array, or {@code null} if not found
     */
    public ItemStack[] getOutfit(UUID playerId, String name) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(name, "name");
        Map<String, ItemStack[]> outfits = wardrobes.get(playerId);
        if (outfits == null) {
            return null;
        }
        ItemStack[] stored = outfits.get(name);
        if (stored == null) {
            return null;
        }
        ItemStack[] copy = new ItemStack[4];
        for (int i = 0; i < 4; i++) {
            copy[i] = stored[i] != null ? stored[i].clone() : null;
        }
        return copy;
    }

    /**
     * Deletes the named outfit for the player.
     *
     * @param playerId the player's UUID, must not be null
     * @param name     the outfit name, must not be null
     * @return {@code true} if the outfit existed and was removed
     */
    public boolean deleteOutfit(UUID playerId, String name) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(name, "name");
        Map<String, ItemStack[]> outfits = wardrobes.get(playerId);
        if (outfits == null) {
            return false;
        }
        return outfits.remove(name) != null;
    }

    /**
     * Returns an unmodifiable view of the outfit names saved by the player.
     *
     * @param playerId the player's UUID, must not be null
     * @return set of outfit names; empty if none saved
     */
    public Set<String> getOutfitNames(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        Map<String, ItemStack[]> outfits = wardrobes.get(playerId);
        if (outfits == null) {
            return Collections.emptySet();
        }
        return Collections.unmodifiableSet(outfits.keySet());
    }

    /**
     * Saves the given armor snapshot into the specified wardrobe slot for the player.
     *
     * @param playerId the player's UUID, must not be null
     * @param slot     the wardrobe slot, must not be null
     * @param armor    the four armor slots to snapshot (index 0-3)
     * @return {@code true} if saved; {@code false} if the player already has
     *         {@link #MAX_OUTFITS} outfits and the slot is a new entry
     */
    public boolean saveOutfit(UUID playerId, WardrobeSlot slot, ItemStack[] armor) {
        Objects.requireNonNull(slot, "slot");
        return saveOutfit(playerId, slot.name(), armor);
    }

    /**
     * Returns a copy of the outfit stored in the specified slot, or {@code null} if empty.
     *
     * @param playerId the player's UUID, must not be null
     * @param slot     the wardrobe slot, must not be null
     * @return cloned armor array, or {@code null} if the slot is empty
     */
    public ItemStack[] getOutfit(UUID playerId, WardrobeSlot slot) {
        Objects.requireNonNull(slot, "slot");
        return getOutfit(playerId, slot.name());
    }

    /**
     * Clears the outfit stored in the specified slot.
     *
     * @param playerId the player's UUID, must not be null
     * @param slot     the wardrobe slot, must not be null
     * @return {@code true} if the slot was occupied and is now cleared
     */
    public boolean deleteOutfit(UUID playerId, WardrobeSlot slot) {
        Objects.requireNonNull(slot, "slot");
        return deleteOutfit(playerId, slot.name());
    }

    /** Removes all stored wardrobe data. */
    public void clear() {
        wardrobes.clear();
    }
}
