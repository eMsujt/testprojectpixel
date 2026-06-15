package com.skyblock.core.skills;

import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/** @deprecated Use {@link SkillManager} directly. */
@Deprecated
public final class SkillsManager {

    /** Canonical skill list. */
    public static final List<String> SKILLS = List.copyOf(SkillManager.SKILL_XP_TABLE.keySet());

    /**
     * Cumulative XP needed to reach each level for each skill.
     * @deprecated Use {@link SkillManager#SKILL_XP_TABLE} and {@link SkillManager#levelForXp}.
     */
    @Deprecated
    public static final Map<String, long[]> XP_REQUIREMENTS;

    static {
        // Guard: SKILL_XP_TABLE must store per-level XP deltas, not cumulative totals.
        // The standard curve's level-2 entry is 125 XP (delta from level 1 to level 2).
        // If the table stored cumulative values it would be 175 (50 + 125), which would
        // cause the loop below to double-cumulate every entry silently.
        long[] farmingCurve = SkillManager.SKILL_XP_TABLE.get("farming");
        if (farmingCurve != null && farmingCurve.length >= 2 && farmingCurve[1] != 125L) {
            throw new IllegalStateException(
                    "XP_REQUIREMENTS: SKILL_XP_TABLE[\"farming\"][1] = " + farmingCurve[1]
                    + " but expected per-level delta 125. If SKILL_XP_TABLE now stores cumulative"
                    + " totals, remove the conversion loop in SkillsManager.XP_REQUIREMENTS.");
        }
        Map<String, long[]> m = new LinkedHashMap<>();
        for (Map.Entry<String, long[]> entry : SkillManager.SKILL_XP_TABLE.entrySet()) {
            long[] per = entry.getValue();
            long[] cum = new long[per.length];
            long running = 0;
            for (int i = 0; i < per.length; i++) {
                running += per[i];
                cum[i] = running;
            }
            m.put(entry.getKey(), cum);
        }
        XP_REQUIREMENTS = Collections.unmodifiableMap(m);
    }

    private static final SkillsManager INSTANCE = new SkillsManager();

    private final SkillManager delegate = SkillManager.getInstance();
    private final Map<UUID, List<String>> skillsHistory = new HashMap<>();

    private SkillsManager() {}

    public static SkillsManager getInstance() {
        return INSTANCE;
    }

    public void load(File dataFolder) {
        delegate.load(dataFolder);
        File file = new File(dataFolder, "skills-history.yml");
        skillsHistory.clear();
        if (file.exists()) {
            YamlConfiguration cfg = YamlConfiguration.loadConfiguration(file);
            if (cfg.isConfigurationSection("skillsHistory")) {
                for (String key : cfg.getConfigurationSection("skillsHistory").getKeys(false)) {
                    try {
                        List<String> entries = cfg.getStringList("skillsHistory." + key);
                        if (!entries.isEmpty()) {
                            skillsHistory.put(UUID.fromString(key), new ArrayList<>(entries));
                        }
                    } catch (IllegalArgumentException ignored) {}
                }
            }
        }
    }

    public void save(File dataFolder) {
        delegate.save(dataFolder);
        File file = new File(dataFolder, "skills-history.yml");
        YamlConfiguration cfg = new YamlConfiguration();
        for (Map.Entry<UUID, List<String>> entry : skillsHistory.entrySet()) {
            if (!entry.getValue().isEmpty()) {
                cfg.set("skillsHistory." + entry.getKey().toString(), entry.getValue());
            }
        }
        try {
            cfg.save(file);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save skills-history.yml", e);
        }
    }

    public void recordSkillEvent(UUID playerId, String summary) {
        skillsHistory.computeIfAbsent(playerId, k -> new ArrayList<>()).add(summary);
    }

    public List<String> getSkillsHistory(UUID playerId) {
        return Collections.unmodifiableList(skillsHistory.getOrDefault(playerId, Collections.emptyList()));
    }

    public Map<UUID, List<String>> getAllSkillsHistory() {
        return Collections.unmodifiableMap(skillsHistory);
    }

    public void addXp(UUID playerId, String skill, long amount) {
        int levelBefore = delegate.getSkillLevel(playerId, skill);
        delegate.addSkillXP(playerId, skill, amount);
        recordSkillEvent(playerId, "Gained " + amount + " XP in " + skill);
        int levelAfter = delegate.getSkillLevel(playerId, skill);
        if (levelAfter > levelBefore) {
            recordSkillEvent(playerId, "Leveled up " + skill + " to level " + levelAfter);
        }
    }

    public long getXp(UUID playerId, String skill) {
        return delegate.getSkillXP(playerId, skill);
    }

    public int getLevel(UUID playerId, String skill) {
        return delegate.getSkillLevel(playerId, skill);
    }

    /** Returns a map of skill name to level for all skills the player has XP in. */
    public Map<String, Integer> getSkillLevels(UUID playerId) {
        Map<String, Long> xps = delegate.getSkillXPs(playerId);
        Map<String, Integer> levels = new LinkedHashMap<>();
        for (Map.Entry<String, Long> e : xps.entrySet()) {
            levels.put(e.getKey(), SkillManager.levelForXp(e.getKey(), e.getValue()));
        }
        return Collections.unmodifiableMap(levels);
    }

    /** Returns all XP entries for a player as a lowercase-key map. */
    public Map<String, Long> getSkillXp(UUID playerId) {
        return delegate.getSkillXPs(playerId);
    }

    public String getSkillsStats(UUID playerId) {
        return delegate.getSkillsStats(playerId);
    }
}
