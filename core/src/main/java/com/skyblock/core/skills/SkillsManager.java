package com.skyblock.core.skills;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public final class SkillsManager {

    /** Cumulative XP required to reach each level (index 0 = level 1) for each skill. */
    public static final Map<String, long[]> XP_REQUIREMENTS;

    static {
        long[] cumulative = {
                50L, 175L, 375L, 675L, 1_175L, 1_925L, 2_925L, 4_425L, 6_425L, 9_925L,
                14_925L, 22_425L, 32_425L, 47_425L, 67_425L, 97_425L, 147_425L, 222_425L, 322_425L, 472_425L,
                672_425L, 972_425L, 1_372_425L, 1_872_425L, 2_472_425L, 3_172_425L, 3_972_425L, 4_872_425L, 5_872_425L, 6_972_425L,
                8_172_425L, 9_472_425L, 10_872_425L, 12_372_425L, 13_972_425L, 15_672_425L, 17_472_425L, 19_372_425L, 21_372_425L, 23_472_425L,
                25_672_425L, 27_972_425L, 30_372_425L, 32_872_425L, 35_472_425L, 38_222_425L, 41_122_425L, 44_222_425L, 47_622_425L, 51_322_425L,
                55_522_425L, 60_222_425L, 65_422_425L, 71_122_425L, 77_322_425L, 84_022_425L, 91_222_425L, 98_922_425L, 107_122_425L, 115_822_425L
        };
        Map<String, long[]> m = new LinkedHashMap<>();
        m.put("farming",    cumulative.clone());
        m.put("mining",     cumulative.clone());
        m.put("combat",     cumulative.clone());
        m.put("foraging",   cumulative.clone());
        m.put("fishing",    cumulative.clone());
        m.put("enchanting", cumulative.clone());
        m.put("alchemy",    cumulative.clone());
        m.put("carpentry",  cumulative.clone());
        m.put("runecrafting", cumulative.clone());
        m.put("taming",     cumulative.clone());
        XP_REQUIREMENTS = Collections.unmodifiableMap(m);
    }

    private final Map<UUID, Map<String, Integer>> skillLevels = new HashMap<>();
    private final Map<UUID, Map<String, Long>> skillXp = new HashMap<>();
    private final Map<UUID, List<String>> skillsHistory = new HashMap<>();

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

    public void recordSkillEvent(UUID playerUuid, String summary) {
        skillsHistory.computeIfAbsent(playerUuid, k -> new ArrayList<>()).add(summary);
    }

    public List<String> getSkillsHistory(UUID playerUuid) {
        return Collections.unmodifiableList(skillsHistory.getOrDefault(playerUuid, Collections.emptyList()));
    }

    public Map<UUID, List<String>> getAllSkillsHistory() {
        return Collections.unmodifiableMap(skillsHistory);
    }
}
