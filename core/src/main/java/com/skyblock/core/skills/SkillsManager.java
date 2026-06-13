package com.skyblock.core.skills;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public final class SkillsManager {

    private final Map<UUID, Map<String, Integer>> skillLevels = new HashMap<>();
    private final Map<UUID, Map<String, Long>> skillXp = new HashMap<>();

    public static final List<String> SKILLS = List.of(
            "farming", "mining", "combat", "foraging", "fishing",
            "enchanting", "alchemy", "carpentry", "runecrafting", "taming"
    );

    public int getLevel(UUID uuid, String skill) {
        return skillLevels.computeIfAbsent(uuid, k -> new HashMap<>()).getOrDefault(skill.toLowerCase(), 0);
    }

    public void setLevel(UUID uuid, String skill, int level) {
        skillLevels.computeIfAbsent(uuid, k -> new HashMap<>()).put(skill.toLowerCase(), Math.max(0, level));
    }

    public long getXp(UUID uuid, String skill) {
        return skillXp.computeIfAbsent(uuid, k -> new HashMap<>()).getOrDefault(skill.toLowerCase(), 0L);
    }

    public void addXp(UUID uuid, String skill, long amount) {
        Map<String, Long> xpMap = skillXp.computeIfAbsent(uuid, k -> new HashMap<>());
        xpMap.put(skill.toLowerCase(), xpMap.getOrDefault(skill.toLowerCase(), 0L) + Math.max(0, amount));
    }

    public Map<String, Integer> getSkillLevels(UUID uuid) {
        return Collections.unmodifiableMap(skillLevels.computeIfAbsent(uuid, k -> new HashMap<>()));
    }

    public Map<String, Long> getSkillXp(UUID uuid) {
        return Collections.unmodifiableMap(skillXp.computeIfAbsent(uuid, k -> new HashMap<>()));
    }
}
