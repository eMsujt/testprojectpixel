package com.skyblock.forging;

import java.util.Objects;

/**
 * Data holder for a single forge slot occupied by an item being forged.
 *
 * <p>Tracks which slot the process runs in, the item being forged and the
 * start and end timestamps of the process. Instances are immutable and
 * therefore safe to share between threads.</p>
 */
public final class ForgeSlot {

    private final int slotId;
    private final String itemId;
    private final long startTime;
    private final long endTime;

    /**
     * Creates a new forge slot entry.
     *
     * @param slotId    the index of the forge slot, must not be negative
     * @param itemId    the identifier of the item being forged
     * @param startTime the time the forging process started, in epoch milliseconds
     * @param endTime   the time the forging process completes, in epoch milliseconds
     * @throws IllegalArgumentException if {@code slotId} is negative or
     *         {@code endTime} is before {@code startTime}
     * @throws NullPointerException if {@code itemId} is {@code null}
     */
    public ForgeSlot(int slotId, String itemId, long startTime, long endTime) {
        if (slotId < 0) {
            throw new IllegalArgumentException("slotId must not be negative, got " + slotId);
        }
        if (endTime < startTime) {
            throw new IllegalArgumentException(
                    "endTime must not be before startTime, got start=" + startTime + ", end=" + endTime);
        }
        this.slotId = slotId;
        this.itemId = Objects.requireNonNull(itemId, "itemId");
        this.startTime = startTime;
        this.endTime = endTime;
    }

    /**
     * Returns the index of the forge slot.
     *
     * @return the slot index, never negative
     */
    public int getSlotId() {
        return slotId;
    }

    /**
     * Returns the identifier of the item being forged.
     *
     * @return the item identifier
     */
    public String getItemId() {
        return itemId;
    }

    /**
     * Returns the time the forging process started.
     *
     * @return the start time in epoch milliseconds
     */
    public long getStartTime() {
        return startTime;
    }

    /**
     * Returns the time the forging process completes.
     *
     * @return the end time in epoch milliseconds, never before the start time
     */
    public long getEndTime() {
        return endTime;
    }

    /**
     * Returns whether the forging process has completed at the given time.
     *
     * @param now the current time in epoch milliseconds
     * @return {@code true} if {@code now} is at or past the end time
     */
    public boolean isComplete(long now) {
        return now >= endTime;
    }

    /**
     * Returns how long the forging process has left at the given time.
     *
     * @param now the current time in epoch milliseconds
     * @return the remaining duration in milliseconds, or {@code 0} if complete
     */
    public long getRemainingTime(long now) {
        return Math.max(0L, endTime - now);
    }

    @Override
    public String toString() {
        return "ForgeSlot{slotId=" + slotId + ", itemId=" + itemId
                + ", startTime=" + startTime + ", endTime=" + endTime + '}';
    }
}
