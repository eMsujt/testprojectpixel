package com.skyblock.plugin.economy;

import com.skyblock.plugin.profile.PlayerProfile;
import com.skyblock.plugin.profile.ProfileManager;

import java.util.Objects;
import java.util.UUID;

/**
 * Tracks each player's spendable coin balances: their {@code purse} (coins on
 * hand) and their {@code bank} (stored coins).
 *
 * <p>Rather than keeping its own in-memory {@code UUID -> coins} map, every
 * balance is read from and written to the player's {@link PlayerProfile} via
 * {@link ProfileManager}, so coins share the single profile source of truth and
 * are persisted alongside the rest of the profile.</p>
 *
 * <p>A shared instance is available via {@link #getInstance()} for callers that
 * want a single source of truth, while a public constructor is retained for
 * components that own their own instance.</p>
 */
public final class PlayerEconomy {

    private static final PlayerEconomy INSTANCE = new PlayerEconomy();

    public PlayerEconomy() {}

    public static PlayerEconomy getInstance() {
        return INSTANCE;
    }

    private PlayerProfile profile(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        return ProfileManager.getInstance().getOrCreate(playerId);
    }

    public long getPurse(UUID playerId) {
        return profile(playerId).getPurse();
    }

    public void setPurse(UUID playerId, long amount) {
        profile(playerId).setPurse(Math.max(0L, amount));
    }

    public void addPurse(UUID playerId, long amount) {
        setPurse(playerId, getPurse(playerId) + amount);
    }

    public long getBank(UUID playerId) {
        return profile(playerId).getBank();
    }

    public void setBank(UUID playerId, long amount) {
        profile(playerId).setBank(Math.max(0L, amount));
    }

    public void addBank(UUID playerId, long amount) {
        setBank(playerId, getBank(playerId) + amount);
    }

    /** Purse balance, exposed under the name menus/shops expect. */
    public long getBalance(UUID playerId) {
        return getPurse(playerId);
    }

    /**
     * Removes {@code amount} coins from the player's purse if they can afford it.
     *
     * @return {@code true} if the coins were withdrawn, {@code false} otherwise
     */
    public boolean withdraw(UUID playerId, long amount) {
        if (amount <= 0 || getPurse(playerId) < amount) {
            return false;
        }
        setPurse(playerId, getPurse(playerId) - amount);
        return true;
    }
}
