package com.skyblock.profiles;

/**
 * The game modes a SkyBlock profile can be created with.
 *
 * <p>Each mode carries its human-readable display name and whether the
 * profile may use the shared economy (auctions, bazaar and trading) or
 * must progress in isolation.</p>
 */
public enum GameMode {

    CLASSIC("Classic", true),
    IRONMAN("Ironman", false),
    BINGO("Bingo", false),
    STRANDED("Stranded", false);

    private final String displayName;
    private final boolean sharedEconomy;

    GameMode(String displayName, boolean sharedEconomy) {
        this.displayName = displayName;
        this.sharedEconomy = sharedEconomy;
    }

    /**
     * Returns the human-readable name shown in profile menus.
     *
     * @return the display name, e.g. {@code "Ironman"}
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Returns whether profiles of this mode may use the shared economy
     * (auctions, bazaar and trading with other players).
     *
     * @return {@code true} if the shared economy is available,
     *         {@code false} if the profile progresses in isolation
     */
    public boolean hasSharedEconomy() {
        return sharedEconomy;
    }
}
