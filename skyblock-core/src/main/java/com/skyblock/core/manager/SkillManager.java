package com.skyblock.core.manager;

import com.skyblock.core.skills.SkillManager.SkillType;

import java.io.File;
import java.util.Map;
import java.util.UUID;

/**
 * Canonical singleton in {@code com.skyblock.core.manager} for skill XP and levels.
 *
 * <p>All logic lives in {@link com.skyblock.core.skills.SkillManager}; this class
 * is a thin forwarding facade so callers in the manager package have a consistent
 * access point alongside {@link AuctionHouseManager} and {@link CollectionManager}.</p>
 *
 * <p>All other SkillManager/SkillsManager copies in this repository are deprecated
 * stubs that delegate to the underlying singleton.</p>
 */
public final class SkillManager {

    /** XP required per level for every skill (lowercase key → per-level thresholds). */
    public static final Map<String, long[]> SKILL_XP_TABLE =
            com.skyblock.core.skills.SkillManager.SKILL_XP_TABLE;

    private static final SkillManager INSTANCE = new SkillManager();

    private final com.skyblock.core.skills.SkillManager delegate =
            com.skyblock.core.skills.SkillManager.getInstance();

    private SkillManager() {}

    public static SkillManager getInstance() {
        return INSTANCE;
    }

    // -------------------------------------------------------------------------
    // Typed API (SkillType enum)
    // -------------------------------------------------------------------------

    /** Adds XP (double, fractional part truncated) and returns the new total. */
    public double addXp(UUID playerId, SkillType skill, double amount) {
        return delegate.addXp(playerId, skill, amount);
    }

    /** Adds XP (long) and returns the new total. */
    public long addXP(UUID playerId, SkillType skill, long amount) {
        return delegate.addXP(playerId, skill, amount);
    }

    /** Returns total accumulated XP for the given skill (0 if none recorded). */
    public long getXp(UUID playerId, SkillType skill) {
        return delegate.getXp(playerId, skill);
    }

    /** Alias for {@link #getXp(UUID, SkillType)} with uppercase name. */
    public long getXP(UUID playerId, SkillType skill) {
        return delegate.getXP(playerId, skill);
    }

    /** Returns the player's current level for the given skill. */
    public int getLevel(UUID playerId, SkillType skill) {
        return delegate.getLevel(playerId, skill);
    }

    // -------------------------------------------------------------------------
    // String-based API (lowercase skill keys)
    // -------------------------------------------------------------------------

    /** Adds XP in the given skill (by lowercase key). Ignores unknown skills. */
    public void addSkillXP(UUID playerId, String skill, long amount) {
        delegate.addSkillXP(playerId, skill, amount);
    }

    /** Directly sets the XP value for a player in the given skill. */
    public void setSkillXP(UUID playerId, String skill, long amount) {
        delegate.setSkillXP(playerId, skill, amount);
    }

    /** Returns the total accumulated XP for a skill (by lowercase key). */
    public long getSkillXP(UUID playerId, String skill) {
        return delegate.getSkillXP(playerId, skill);
    }

    /** Returns the player's level in the given skill (by lowercase key). */
    public int getSkillLevel(UUID playerId, String skill) {
        return delegate.getSkillLevel(playerId, skill);
    }

    /** Returns all XP entries for a player as a lowercase-key map. */
    public Map<String, Long> getSkillXPs(UUID playerId) {
        return delegate.getSkillXPs(playerId);
    }

    /** Returns all players' XP for a single skill, keyed by player UUID. */
    public Map<UUID, Long> getAllSkillXP(String skill) {
        return delegate.getAllSkillXP(skill);
    }

    /** Returns a human-readable summary of a player's levels across all skills. */
    public String getSkillsStats(UUID playerId) {
        return delegate.getSkillsStats(playerId);
    }

    /** Returns the XP curve map (skill name → per-level thresholds). */
    public Map<String, long[]> getCurves() {
        return delegate.getCurves();
    }

    // -------------------------------------------------------------------------
    // Static utilities
    // -------------------------------------------------------------------------

    /** Resolves a raw XP total to a level using the given skill's XP table. */
    public static int levelForXp(String skill, long totalXP) {
        return com.skyblock.core.skills.SkillManager.levelForXp(skill, totalXP);
    }

    /** Returns the maximum level for the given skill, or 0 if unknown. */
    public static int maxLevel(String skill) {
        return com.skyblock.core.skills.SkillManager.maxLevel(skill);
    }

    // -------------------------------------------------------------------------
    // Persistence
    // -------------------------------------------------------------------------

    public void load(File dataFolder) {
        delegate.load(dataFolder);
    }

    public void save(File dataFolder) {
        delegate.save(dataFolder);
    }
}
