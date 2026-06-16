package com.skyblock.core.manager;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * Singleton tracking each player's essence balances per {@link EssenceType},
 * the essence-shop perks they have purchased, and the essence-gated items
 * they have unlocked.
 *
 * <p>Essence is a currency used to upgrade SkyBlock items at the Essence Shop.
 * Not thread-safe; synchronize externally if accessed from multiple threads.</p>
 */
public final class EssenceManager {

    /** Every essence type available in SkyBlock. */
    public enum EssenceType {
        WITHER("Wither"),
        SPIDER("Spider"),
        DRAGON("Dragon"),
        GOLD("Gold"),
        DIAMOND("Diamond"),
        ICE("Ice"),
        UNDEAD("Undead"),
        CRIMSON("Crimson");

        private final String displayName;

        EssenceType(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    /**
     * An essence-shop perk purchasable with a specific {@link EssenceType}.
     *
     * <p>Each level costs {@code baseCost * (currentLevel + 1)} essence and a
     * perk cannot be upgraded beyond {@link #getMaxLevel()}.</p>
     */
    public enum EssenceShopPerk {
        HEALTH("Health Boost", EssenceType.WITHER, 100, 50),
        DEFENSE("Defense Boost", EssenceType.WITHER, 100, 50),
        SPEED("Speed Boost", EssenceType.UNDEAD, 80, 25),
        INTELLIGENCE("Intelligence Boost", EssenceType.DRAGON, 120, 25),
        CRIT_DAMAGE("Crit Damage Boost", EssenceType.SPIDER, 150, 10),
        TOUGH_SKIN("Tough Skin", EssenceType.CRIMSON, 200, 10);

        private final String displayName;
        private final EssenceType essenceType;
        private final int baseCost;
        private final int maxLevel;

        EssenceShopPerk(String displayName, EssenceType essenceType, int baseCost, int maxLevel) {
            this.displayName = displayName;
            this.essenceType = essenceType;
            this.baseCost = baseCost;
            this.maxLevel = maxLevel;
        }

        public String getDisplayName() { return displayName; }
        public EssenceType getEssenceType() { return essenceType; }
        public int getBaseCost() { return baseCost; }
        public int getMaxLevel() { return maxLevel; }

        /** Returns the essence cost to upgrade this perk from {@code currentLevel} to the next level. */
        public int getUpgradeCost(int currentLevel) {
            return baseCost * (currentLevel + 1);
        }
    }

    /** An item whose use is gated behind a minimum essence balance of a given type. */
    public enum EssenceItem {
        WITHER_CLOAK("Wither Cloak Sword", EssenceType.WITHER, 4000),
        IMPLOSION("Implosion", EssenceType.WITHER, 8000),
        WITHER_SHIELD("Wither Shield", EssenceType.WITHER, 8000),
        SHADOW_WARP("Shadow Warp", EssenceType.WITHER, 8000),
        NECRON_BLADE("Necron's Blade", EssenceType.WITHER, 5000),
        HYPERION("Hyperion", EssenceType.WITHER, 20000),
        GIANTS_SWORD("Giant's Sword", EssenceType.UNDEAD, 12000);

        private final String displayName;
        private final EssenceType essenceType;
        private final int requiredEssence;

        EssenceItem(String displayName, EssenceType essenceType, int requiredEssence) {
            this.displayName = displayName;
            this.essenceType = essenceType;
            this.requiredEssence = requiredEssence;
        }

        public String getDisplayName() { return displayName; }
        public EssenceType getEssenceType() { return essenceType; }
        public int getRequiredEssence() { return requiredEssence; }
    }

    private static final EssenceManager INSTANCE = new EssenceManager();

    /** Per-player essence balances; absent entries default to zero. */
    private final Map<UUID, Map<EssenceType, Integer>> playerEssence = new HashMap<>();

    /** Per-player purchased perk levels; absent entries default to zero. */
    private final Map<UUID, Map<EssenceShopPerk, Integer>> playerPerks = new HashMap<>();

    private EssenceManager() {
    }

    /**
     * Returns the single shared {@code EssenceManager} instance.
     *
     * @return the singleton instance
     */
    public static EssenceManager getInstance() {
        return INSTANCE;
    }

    /**
     * Returns the essence balance of the given type for the specified player.
     *
     * @param playerId    the player to look up
     * @param essenceType the type of essence to query
     * @return the current balance, {@code 0} if the player has none
     */
    public int getBalance(UUID playerId, EssenceType essenceType) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(essenceType, "essenceType");
        Map<EssenceType, Integer> balances = playerEssence.get(playerId);
        return balances == null ? 0 : balances.getOrDefault(essenceType, 0);
    }

    /**
     * Adds essence of the given type to the player's balance.
     *
     * @param playerId    the player to update
     * @param essenceType the type of essence to add
     * @param amount      the amount to add (must be positive)
     * @return the new balance after the addition
     * @throws IllegalArgumentException if {@code amount} is not positive
     */
    public int addEssence(UUID playerId, EssenceType essenceType, int amount) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(essenceType, "essenceType");
        if (amount <= 0) {
            throw new IllegalArgumentException("amount must be positive");
        }
        Map<EssenceType, Integer> balances = playerEssence.computeIfAbsent(
                playerId, id -> new EnumMap<>(EssenceType.class));
        int newBalance = balances.getOrDefault(essenceType, 0) + amount;
        balances.put(essenceType, newBalance);
        return newBalance;
    }

    /**
     * Removes essence of the given type from the player's balance.
     *
     * @param playerId    the player to update
     * @param essenceType the type of essence to remove
     * @param amount      the amount to remove (must be positive)
     * @return {@code true} if the player had sufficient balance and the removal succeeded,
     *         {@code false} if the player had insufficient essence
     * @throws IllegalArgumentException if {@code amount} is not positive
     */
    public boolean removeEssence(UUID playerId, EssenceType essenceType, int amount) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(essenceType, "essenceType");
        if (amount <= 0) {
            throw new IllegalArgumentException("amount must be positive");
        }
        int current = getBalance(playerId, essenceType);
        if (current < amount) {
            return false;
        }
        Map<EssenceType, Integer> balances = playerEssence.computeIfAbsent(
                playerId, id -> new EnumMap<>(EssenceType.class));
        balances.put(essenceType, current - amount);
        return true;
    }

    /**
     * Returns the purchased level of the given essence-shop perk for the player.
     *
     * @param playerId the player to look up
     * @param perk     the perk to query
     * @return the current perk level, {@code 0} if not purchased
     */
    public int getPerkLevel(UUID playerId, EssenceShopPerk perk) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(perk, "perk");
        Map<EssenceShopPerk, Integer> perks = playerPerks.get(playerId);
        return perks == null ? 0 : perks.getOrDefault(perk, 0);
    }

    /**
     * Attempts to purchase the next level of an essence-shop perk, deducting its
     * upgrade cost from the player's matching essence balance.
     *
     * @param playerId the player to update
     * @param perk     the perk to upgrade
     * @return {@code true} if the upgrade succeeded, {@code false} if the perk is
     *         already maxed or the player has insufficient essence
     */
    public boolean purchasePerk(UUID playerId, EssenceShopPerk perk) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(perk, "perk");
        int level = getPerkLevel(playerId, perk);
        if (level >= perk.getMaxLevel()) {
            return false;
        }
        int cost = perk.getUpgradeCost(level);
        if (!removeEssence(playerId, perk.getEssenceType(), cost)) {
            return false;
        }
        playerPerks.computeIfAbsent(playerId, id -> new EnumMap<>(EssenceShopPerk.class))
                .put(perk, level + 1);
        return true;
    }

    /**
     * Returns whether the player has spent enough essence to use an essence-gated item.
     *
     * <p>Mirrors the SkyBlock requirement that ability items remain locked until the
     * owner has invested the listed essence into them.</p>
     *
     * @param playerId the player to check
     * @param item     the essence-gated item
     * @return {@code true} if the player's matching essence balance meets the item's requirement
     */
    public boolean canUnlock(UUID playerId, EssenceItem item) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(item, "item");
        return getBalance(playerId, item.getEssenceType()) >= item.getRequiredEssence();
    }

    /**
     * Removes all essence data for the given player (e.g. on quit).
     *
     * @param playerId the player to remove
     * @return {@code true} if the player had data, {@code false} otherwise
     */
    public boolean remove(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        boolean hadEssence = playerEssence.remove(playerId) != null;
        boolean hadPerks = playerPerks.remove(playerId) != null;
        return hadEssence || hadPerks;
    }
}
