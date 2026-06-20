package com.skyblock.core.config;

/**
 * Central home for tuning constants shared across the core managers.
 *
 * <p>Values that were previously hardcoded inside individual managers (bank
 * capacities, skill XP curves, slayer drop chances) live here so they can be
 * located and adjusted in one place.</p>
 */
public final class Constants {

    private Constants() {}

    // -------------------------------------------------------------------------
    // Economy
    // -------------------------------------------------------------------------

    /** Default maximum a player's bank may hold (Gold tier). */
    public static final long DEFAULT_BANK_CAPACITY = 50_000_000L;

    // -------------------------------------------------------------------------
    // Slayer drop chances (base chance at tier 1, in the range [0, 1])
    // -------------------------------------------------------------------------

    /** Base chance for a common slayer drop. */
    public static final double SLAYER_COMMON_DROP_CHANCE = 0.30;

    /** Base chance for a rare slayer drop. */
    public static final double SLAYER_RARE_DROP_CHANCE = 0.01;
}
