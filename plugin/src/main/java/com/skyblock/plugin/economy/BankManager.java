package com.skyblock.plugin.economy;

import com.skyblock.plugin.profile.PlayerProfile;
import com.skyblock.plugin.profile.ProfileManager;

import java.util.UUID;

/**
 * @deprecated Use {@link com.skyblock.core.manager.BankManager} instead.
 */
@Deprecated
public final class BankManager {

    private static final BankManager INSTANCE = new BankManager();

    private final ProfileManager profileManager;

    private BankManager() {
        this.profileManager = ProfileManager.getInstance();
    }

    public static BankManager getInstance() {
        return INSTANCE;
    }

    /**
     * Moves {@code amount} coins from the player's purse into their bank.
     *
     * @return {@code true} if the deposit succeeded; {@code false} if the player
     *         lacks sufficient purse funds or the amount is not positive
     */
    public boolean deposit(UUID uuid, long amount) {
        if (amount <= 0) return false;
        PlayerProfile profile = profileManager.getOrCreate(uuid);
        if (profile.getPurse() < amount) return false;
        profile.setPurse(profile.getPurse() - amount);
        profile.setBank(profile.getBank() + amount);
        return true;
    }

    /**
     * Moves {@code amount} coins from the player's bank back into their purse.
     *
     * @return {@code true} if the withdrawal succeeded; {@code false} if the bank
     *         lacks sufficient funds or the amount is not positive
     */
    public boolean withdraw(UUID uuid, long amount) {
        if (amount <= 0) return false;
        PlayerProfile profile = profileManager.getOrCreate(uuid);
        if (profile.getBank() < amount) return false;
        profile.setBank(profile.getBank() - amount);
        profile.setPurse(profile.getPurse() + amount);
        return true;
    }

    /** Returns the player's current purse balance. */
    public long getPurse(UUID uuid) {
        return profileManager.getOrCreate(uuid).getPurse();
    }

    /** Returns the player's current bank balance. */
    public long getBank(UUID uuid) {
        return profileManager.getOrCreate(uuid).getBank();
    }
}
