package com.skyblock.plugin.manager;

import com.skyblock.plugin.profile.SkyBlockProfile;

import java.util.Objects;
import java.util.UUID;

/**
 * Central access point for a player's coin balance (their purse).
 *
 * <p>Delegates to the {@link SkyBlockProfile} tracked by {@link ProfileManager},
 * exposing read, credit and debit operations against the player's purse. Not
 * thread-safe; access from the main server thread.</p>
 */
public final class EconomyManager {

    private static final EconomyManager INSTANCE = new EconomyManager();

    private EconomyManager() {
    }

    public static EconomyManager getInstance() {
        return INSTANCE;
    }

    /**
     * Returns the given player's current coin balance.
     *
     * @param uuid the player's UUID
     * @return the player's purse balance, or {@code 0} if no profile is loaded
     */
    public double getCoins(UUID uuid) {
        Objects.requireNonNull(uuid, "uuid");
        SkyBlockProfile profile = ProfileManager.getInstance().getProfile(uuid);
        return profile == null ? 0.0D : profile.getPurse();
    }

    /**
     * Credits the given amount of coins to the player's purse.
     *
     * @param uuid   the player's UUID
     * @param amount the amount to add; must not be negative
     */
    public void addCoins(UUID uuid, double amount) {
        Objects.requireNonNull(uuid, "uuid");
        if (amount < 0) throw new IllegalArgumentException("amount must not be negative");
        SkyBlockProfile profile = ProfileManager.getInstance().getOrCreateProfile(uuid);
        profile.setPurse(profile.getPurse() + (long) amount);
    }

    /**
     * Debits the given amount of coins from the player's purse, if affordable.
     *
     * @param uuid   the player's UUID
     * @param amount the amount to remove; must not be negative
     * @return {@code true} if the balance covered the amount and was debited,
     *         {@code false} otherwise
     */
    public boolean removeCoins(UUID uuid, double amount) {
        Objects.requireNonNull(uuid, "uuid");
        if (amount < 0) throw new IllegalArgumentException("amount must not be negative");
        SkyBlockProfile profile = ProfileManager.getInstance().getProfile(uuid);
        if (profile == null || profile.getPurse() < amount) {
            return false;
        }
        profile.setPurse(profile.getPurse() - (long) amount);
        return true;
    }

    /**
     * Returns whether the player's purse can cover the given amount.
     *
     * @param uuid   the player's UUID
     * @param amount the amount to test
     * @return {@code true} if the balance is at least {@code amount}
     */
    public boolean hasCoins(UUID uuid, double amount) {
        Objects.requireNonNull(uuid, "uuid");
        return getCoins(uuid) >= amount;
    }
}
