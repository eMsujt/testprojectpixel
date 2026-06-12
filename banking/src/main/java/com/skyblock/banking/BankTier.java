package com.skyblock.banking;

/**
 * The upgrade tiers of a player's SkyBlock bank account.
 *
 * <p>Each tier carries its human-readable display name and the maximum
 * number of coins an account of that tier can hold. Players unlock higher
 * tiers to raise the cap enforced by {@link BankManager} deposits.</p>
 */
public enum BankTier {

    STARTER("Starter Account", 50_000_000L),
    GOLD("Gold Account", 100_000_000L),
    DELUXE("Deluxe Account", 250_000_000L),
    SUPER_DELUXE("Super Deluxe Account", 450_000_000L),
    PREMIER("Premier Account", 1_000_000_000L);

    private final String displayName;
    private final long coinCap;

    BankTier(String displayName, long coinCap) {
        this.displayName = displayName;
        this.coinCap = coinCap;
    }

    /**
     * Returns the human-readable name shown in bank menus.
     *
     * @return the display name, e.g. {@code "Starter Account"}
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Returns the maximum number of coins an account of this tier can hold.
     *
     * @return the coin capacity of this tier
     */
    public long getCoinCap() {
        return coinCap;
    }
}
