package com.skyblock.economy.model;

/**
 * The currencies a player can earn and spend in SkyBlock.
 *
 * <p>Each currency carries its human-readable display name and whether it
 * can be transferred directly between players.</p>
 */
public enum CurrencyType {

    COINS("Coins", true),
    GEMS("Gems", false),
    BITS("Bits", false),
    MOTES("Motes", false),
    COPPER("Copper", false);

    private final String displayName;
    private final boolean tradeable;

    CurrencyType(String displayName, boolean tradeable) {
        this.displayName = displayName;
        this.tradeable = tradeable;
    }

    /**
     * Returns the human-readable name of this currency.
     *
     * @return the display name, e.g. {@code "Coins"}
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Returns whether this currency can be transferred between players.
     *
     * @return {@code true} if player-to-player trading is allowed
     */
    public boolean isTradeable() {
        return tradeable;
    }
}
