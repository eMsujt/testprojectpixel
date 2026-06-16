package com.skyblock.fishing;

/**
 * @deprecated Use {@link com.skyblock.core.fishing.manager.FishingManager} loot handling instead.
 *
 * A single entry in a fishing zone's loot table, pairing an item id with the
 * probability that the item drops on a given catch.
 */
@Deprecated
public final class FishingDrop {

    private final String itemId;
    private final double dropChance;

    /**
     * Creates a drop entry.
     *
     * @param itemId     the identifier of the item to drop, must not be null
     * @param dropChance the probability of dropping, as a fraction in [0, 1]
     * @throws IllegalArgumentException if {@code itemId} is null or
     *                                  {@code dropChance} is outside [0, 1]
     */
    public FishingDrop(String itemId, double dropChance) {
        if (itemId == null) {
            throw new IllegalArgumentException("itemId must not be null");
        }
        if (dropChance < 0 || dropChance > 1) {
            throw new IllegalArgumentException("dropChance must be in [0, 1]: " + dropChance);
        }
        this.itemId = itemId;
        this.dropChance = dropChance;
    }

    /**
     * Returns the identifier of the item this entry drops.
     *
     * @return the item id
     */
    public String getItemId() {
        return itemId;
    }

    /**
     * Returns the probability that this item drops on a single catch.
     *
     * @return the drop chance as a fraction in [0, 1]
     */
    public double getDropChance() {
        return dropChance;
    }
}
