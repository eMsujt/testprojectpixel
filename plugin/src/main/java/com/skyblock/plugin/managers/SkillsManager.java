package com.skyblock.plugin.managers;

import com.skyblock.plugin.skill.SkillLevelUpHandler;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * @deprecated Use {@link com.skyblock.core.skills.SkillManager} instead.
 *
 * <p>XP storage delegates to the canonical singleton. History tracking and
 * level-up callbacks are kept here for backward compatibility.</p>
 */
@Deprecated
public final class SkillsManager {

    /** @deprecated Use {@link com.skyblock.core.skills.SkillManager#SKILL_XP_TABLE}. */
    @Deprecated
    public static final Map<String, long[]> SKILL_XP_TABLE =
            com.skyblock.core.skills.SkillManager.SKILL_XP_TABLE;

    private static final SkillsManager INSTANCE = new SkillsManager();
    private final com.skyblock.core.skills.SkillManager delegate =
            com.skyblock.core.skills.SkillManager.getInstance();

    private final Map<UUID, Map<String, Integer>> xpHistory = new HashMap<>();
    private final Map<UUID, List<String>> skillHistory = new HashMap<>();

    private SkillsManager() {}

    public static SkillsManager getInstance() { return INSTANCE; }

    public int getSkillMaxLevel(String skill) {
        int max = com.skyblock.core.skills.SkillManager.maxLevel(skill);
        return max > 0 ? max : 60;
    }

    public void setSkillMaxLevel(String skill, int maxLevel) {
        // max level is determined by SKILL_XP_TABLE in the canonical; no-op
    }

    public long getSkillXP(UUID playerId, String skill) {
        return delegate.getSkillXP(playerId, skill);
    }

    public void addSkillXP(UUID playerId, String skill, long amount) {
        int levelBefore = delegate.getSkillLevel(playerId, skill);
        delegate.addSkillXP(playerId, skill, amount);
        recordSkillEvent(playerId, "Gained " + amount + " XP in " + skill);
        int levelAfter = delegate.getSkillLevel(playerId, skill);
        if (levelAfter > levelBefore) {
            recordSkillEvent(playerId, "Leveled up " + skill + " to level " + levelAfter);
            Player player = Bukkit.getPlayer(playerId);
            if (player != null) {
                SkillLevelUpHandler.handle(player, skill, levelAfter);
            }
        }
    }

    public void setSkillXP(UUID playerId, String skill, long amount) {
        delegate.setSkillXP(playerId, skill, amount);
    }

    public Map<String, Long> getSkillXPs(UUID playerId) {
        return delegate.getSkillXPs(playerId);
    }

    public void recordXpGain(UUID playerId, String skill, int amount) {
        xpHistory.computeIfAbsent(playerId, k -> new HashMap<>()).merge(skill, amount, Integer::sum);
    }

    public Map<String, Integer> getXpHistory(UUID playerId) {
        return Collections.unmodifiableMap(xpHistory.getOrDefault(playerId, new HashMap<>()));
    }

    public Map<UUID, Map<String, Integer>> getAllXpHistory() {
        return Collections.unmodifiableMap(xpHistory);
    }

    public void recordSkillGain(UUID playerId, String summary) {
        skillHistory.computeIfAbsent(playerId, k -> new ArrayList<>()).add(summary);
    }

    public List<String> getSkillHistory(UUID playerId) {
        return Collections.unmodifiableList(skillHistory.getOrDefault(playerId, Collections.emptyList()));
    }

    public Map<UUID, List<String>> getAllSkillHistory() {
        return Collections.unmodifiableMap(skillHistory);
    }

    public void recordSkillEvent(UUID playerId, String summary) {
        skillHistory.computeIfAbsent(playerId, k -> new ArrayList<>()).add(summary);
    }

    public List<String> getSkillsHistory(UUID playerId) {
        return getSkillHistory(playerId);
    }

    public Map<UUID, List<String>> getAllSkillsHistory() {
        return getAllSkillHistory();
    }

    public Map<UUID, Long> getAllSkillXP(String skill) {
        return delegate.getAllSkillXP(skill);
    }

    public int getSkillLevel(UUID playerId, String skill) {
        return delegate.getSkillLevel(playerId, skill);
    }

    public String getSkillsStats(UUID playerId) {
        return delegate.getSkillsStats(playerId);
    }

    public void load(File dataFolder) {
        delegate.load(dataFolder);
    }

    public void save(File dataFolder) {
        delegate.save(dataFolder);
    }
}
