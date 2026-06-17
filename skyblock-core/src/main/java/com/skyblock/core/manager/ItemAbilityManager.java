package com.skyblock.core.manager;

import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

/**
 * Canonical singleton for SkyBlock item and full-set abilities.
 *
 * <p>Tracks which abilities each player has unlocked, which ability is active
 * (equipped), and the per-ability cooldown state. Each ability carries a mana
 * cost and a cooldown; {@link #activate} gates use on the player being able to
 * pay both.</p>
 *
 * <p>Abilities come in two flavours (see {@link AbilityCategory}): single-item
 * weapon/tool abilities, and armor/weapon full-set abilities that are only
 * available while the complete set is worn.</p>
 *
 * <p>Not thread-safe; synchronize externally if needed.</p>
 */
public final class ItemAbilityManager {

    /** Whether an ability comes from a single item or a worn full set. */
    public enum AbilityCategory {
        ITEM,
        FULL_SET
    }

    /** Outcome of an {@link #activate} attempt. */
    public enum ActivationResult {
        SUCCESS,
        NOT_UNLOCKED,
        ON_COOLDOWN,
        NOT_ENOUGH_MANA
    }

    /** Every ability available in SkyBlock. */
    public enum AbilityType {
        // Single-item weapon/tool abilities.
        WITHER_SHIELD(AbilityCategory.ITEM, 10, 150),
        SHADOW_FURY(AbilityCategory.ITEM, 8, 300),
        IMPLOSION(AbilityCategory.ITEM, 12, 300),
        HURRICANE_BOW(AbilityCategory.ITEM, 6, 0),
        HYPE(AbilityCategory.ITEM, 15, 250),
        OVERLOAD(AbilityCategory.ITEM, 20, 200),
        GYROKINESIS(AbilityCategory.ITEM, 10, 150),
        ADRENALINE(AbilityCategory.ITEM, 8, 100),
        SWORD_SPECIALIST(AbilityCategory.ITEM, 5, 50),
        MANA_STEAL(AbilityCategory.ITEM, 7, 0),
        // Armor/weapon full-set abilities (require the complete set worn).
        RADIANT_FULL_SET(AbilityCategory.FULL_SET, 30, 200),
        HOLY_FULL_SET(AbilityCategory.FULL_SET, 25, 100),
        SUPERIOR_FULL_SET(AbilityCategory.FULL_SET, 20, 0);

        /** Whether this ability comes from a single item or a worn full set. */
        public final AbilityCategory category;

        /** Cooldown in seconds. */
        public final int cooldownSeconds;

        /** Mana cost to activate. */
        public final int manaCost;

        AbilityType(AbilityCategory category, int cooldownSeconds, int manaCost) {
            this.category = category;
            this.cooldownSeconds = cooldownSeconds;
            this.manaCost = manaCost;
        }
    }

    private static final ItemAbilityManager INSTANCE = new ItemAbilityManager();

    /** Unlocked abilities per player. */
    private final Map<UUID, Set<AbilityType>> unlockedAbilities = new HashMap<>();

    /** Active (equipped) ability per player; absent means none equipped. */
    private final Map<UUID, AbilityType> activeAbility = new HashMap<>();

    /**
     * Last activation timestamp (System.currentTimeMillis) per player per ability.
     * Used to enforce cooldowns.
     */
    private final Map<UUID, Map<AbilityType, Long>> lastUsed = new HashMap<>();

    private ItemAbilityManager() {}

    public static ItemAbilityManager getInstance() {
        return INSTANCE;
    }

    public void unlock(UUID playerId, AbilityType abilityType) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(abilityType, "abilityType");
        unlockedAbilities.computeIfAbsent(playerId, id -> EnumSet.noneOf(AbilityType.class))
                .add(abilityType);
    }

    public boolean isUnlocked(UUID playerId, AbilityType abilityType) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(abilityType, "abilityType");
        Set<AbilityType> set = unlockedAbilities.get(playerId);
        return set != null && set.contains(abilityType);
    }

    public Set<AbilityType> getUnlocked(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        Set<AbilityType> set = unlockedAbilities.get(playerId);
        return set == null ? Collections.emptySet() : Collections.unmodifiableSet(set);
    }

    /**
     * Sets the player's active (equipped) ability.
     *
     * @throws IllegalArgumentException if the ability is not unlocked
     */
    public void setActive(UUID playerId, AbilityType abilityType) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(abilityType, "abilityType");
        if (!isUnlocked(playerId, abilityType)) {
            throw new IllegalArgumentException("Ability not unlocked: " + abilityType);
        }
        activeAbility.put(playerId, abilityType);
    }

    public AbilityType getActive(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        return activeAbility.get(playerId);
    }

    public void clearActive(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        activeAbility.remove(playerId);
    }

    /** Returns the remaining cooldown in seconds; {@code 0} if off cooldown. */
    public long getRemainingCooldown(UUID playerId, AbilityType abilityType) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(abilityType, "abilityType");
        Map<AbilityType, Long> playerMap = lastUsed.get(playerId);
        if (playerMap == null) return 0;
        Long last = playerMap.get(abilityType);
        if (last == null) return 0;
        long elapsedSeconds = (System.currentTimeMillis() - last) / 1000L;
        long remaining = abilityType.cooldownSeconds - elapsedSeconds;
        return Math.max(0, remaining);
    }

    /**
     * Attempts to activate an ability, gating on unlock state, cooldown, and the
     * player's available mana. On {@link ActivationResult#SUCCESS} the ability's
     * cooldown is started; the caller is responsible for deducting
     * {@link AbilityType#manaCost} from the player's mana pool.
     *
     * @param availableMana the player's current mana
     */
    public ActivationResult activate(UUID playerId, AbilityType abilityType, int availableMana) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(abilityType, "abilityType");
        if (!isUnlocked(playerId, abilityType)) {
            return ActivationResult.NOT_UNLOCKED;
        }
        if (getRemainingCooldown(playerId, abilityType) > 0) {
            return ActivationResult.ON_COOLDOWN;
        }
        if (availableMana < abilityType.manaCost) {
            return ActivationResult.NOT_ENOUGH_MANA;
        }
        recordUse(playerId, abilityType);
        return ActivationResult.SUCCESS;
    }

    /** Records ability use for the given player, resetting its cooldown. */
    public void recordUse(UUID playerId, AbilityType abilityType) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(abilityType, "abilityType");
        lastUsed.computeIfAbsent(playerId, id -> new HashMap<>())
                .put(abilityType, System.currentTimeMillis());
    }

    /** Removes all ability data for the given player. Call on player quit. */
    public void remove(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        unlockedAbilities.remove(playerId);
        activeAbility.remove(playerId);
        lastUsed.remove(playerId);
    }
}
