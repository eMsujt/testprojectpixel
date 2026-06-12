package com.skyblock.profiles;

/**
 * The modes a SkyBlock profile can be played in.
 *
 * <p>Each mode carries its human-readable display name and whether the
 * profile may trade with other players (auctions, bazaar and direct
 * trades) or must progress entirely on its own.</p>
 */
public enum ProfileMode {

    NORMAL("Normal", true),
    IRONMAN("Ironman", false),
    STRANDED("Stranded", false),
    BINGO("Bingo", false);

    private final String displayName;
    private final boolean tradingAllowed;

    ProfileMode(String displayName, boolean tradingAllowed) {
        this.displayName = displayName;
        this.tradingAllowed = tradingAllowed;
    }

    /**
     * Returns the human-readable name shown in profile menus.
     *
     * @return the display name, e.g. {@code "Stranded"}
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Returns whether profiles of this mode may trade with other players
     * through auctions, the bazaar or direct trades.
     *
     * @return {@code true} if trading is allowed,
     *         {@code false} if the profile progresses on its own
     */
    public boolean isTradingAllowed() {
        return tradingAllowed;
    }
}
