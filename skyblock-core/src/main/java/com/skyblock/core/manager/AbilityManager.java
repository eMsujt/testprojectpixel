package com.skyblock.core.manager;

import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

/**
 * Canonical singleton for SkyBlock item abilities.
 *
 * <p>Tracks which abilities each player has unlocked, which ability is active
 * (equipped), and the per-ability cooldown state.</p>
 *
 * <p>Not thread-safe; synchronize externally if needed.</p>
 */
public final class AbilityManager {

    /** Every ability available in SkyBlock. */
    public enum AbilityType {
        WITHER_SHIELD(10),
        SHADOW_FURY(8),
        IMPLOSION(12),
        HURRICANE_BOW(6),
        HYPE(15),
        OVERLOAD(20),
        GYROKINESIS(10),
        ADRENALINE(8),
        SWORD_SPECIALIST(5),
        MANA_STEAL(7);

        /** Cooldown in seconds. */
        public final int cooldownSeconds;

        AbilityType(int cooldownSeconds) {
            this.cooldownSeconds = cooldownSeconds;
        }
    }

    private static final AbilityManager INSTANCE = new AbilityManager();

    /** Unlocked abilities per player. */
    private final Map<UUID, Set<AbilityType>> unlockedAbilities = new HashMap<>();

    /** Active (equipped) ability per player; absent means none equipped. */
    private final Map<UUID, AbilityType> activeAbility = new HashMap<>();

    /**
     * Last activation timestamp (System.currentTimeMillis) per player per ability.
     * Used to enforce cooldowns.
     */
    private final Map<UUID, Map<AbilityType, Long>> lastUsed = new HashMap<>();

    private AbilityManager() {}

    public static AbilityManager getInstance() {
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
