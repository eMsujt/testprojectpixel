package com.skyblock.core.manager;

import com.skyblock.core.manager.EssenceManager.EssenceShopPerk;

import java.util.Objects;
import java.util.UUID;

/**
 * Facade over {@link EssenceManager} for essence-shop purchase operations.
 *
 * <p>Centralises shop availability checks and purchase delegation so that
 * {@link com.skyblock.core.menu.EssenceShopMenu} has a single point of entry
 * rather than calling {@link EssenceManager} directly.</p>
 */
public final class EssenceShopManager {

    private static final EssenceShopManager INSTANCE = new EssenceShopManager();

    private final EssenceManager essenceManager = EssenceManager.getInstance();

    private EssenceShopManager() {
    }

    public static EssenceShopManager getInstance() {
        return INSTANCE;
    }

    /** Returns all perks sold by the essence shop. */
    public EssenceShopPerk[] getAvailablePerks() {
        return EssenceShopPerk.values();
    }

    /** Returns the purchased level of {@code perk} for the given player. */
    public int getPerkLevel(UUID playerId, EssenceShopPerk perk) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(perk, "perk");
        return essenceManager.getPerkLevel(playerId, perk);
    }

    /**
     * Returns {@code true} when the player has enough essence to buy the next
     * level of {@code perk} and the perk is not yet maxed.
     */
    public boolean canAfford(UUID playerId, EssenceShopPerk perk) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(perk, "perk");
        int level = essenceManager.getPerkLevel(playerId, perk);
        if (level >= perk.getMaxLevel()) return false;
        int cost = perk.getUpgradeCost(level);
        return essenceManager.getBalance(playerId, perk.getEssenceType()) >= cost;
    }

    /**
     * Attempts to purchase the next level of {@code perk} for the player,
     * deducting its upgrade cost from the matching essence balance.
     *
     * @return {@code true} if the upgrade succeeded; {@code false} if the perk
     *         is already maxed or the player has insufficient essence
     */
    public boolean purchasePerk(UUID playerId, EssenceShopPerk perk) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(perk, "perk");
        return essenceManager.purchasePerk(playerId, perk);
    }
}
