package com.skyblock.core.manager;

import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;

public final class HotMManager {

    public enum HotMPerk {
        MINING_SPEED_BOOST,
        FORGE_TIME,
        DAILY_POWDER,
        GOBLIN_KILLER,
        STAR_POWDER,
        SKY_MALL,
        MINING_MADNESS,
        SEASONED_MINEMAN,
        EFFICIENT_MINER,
        QUICK_FORGE,
        TITANIUM_INSANIUM,
        DAILY_GRIND,
        LONESOME_MINER,
        PROFESSIONAL,
        MOLE,
        FORTUNATE,
        GREAT_EXPLORER,
        MANIAC_MINER,
        CHANCE_UPON_A_FIND,
        MINING_EXP_BOOST,
        ORBITER,
        SPECIAL_0,
        MINING_FORTUNE_BOOST
    }

    private static final HotMManager INSTANCE = new HotMManager();

    private final Map<HotMPerk, Integer> perkLevels = new EnumMap<>(HotMPerk.class);

    private HotMManager() {
        for (HotMPerk perk : HotMPerk.values()) {
            perkLevels.put(perk, 0);
        }
    }

    public static HotMManager getInstance() {
        return INSTANCE;
    }

    public int getLevel(HotMPerk perk) {
        return perkLevels.getOrDefault(perk, 0);
    }

    public void setLevel(HotMPerk perk, int level) {
        perkLevels.put(perk, level);
    }

    public boolean isUnlocked(HotMPerk perk) {
        return perkLevels.getOrDefault(perk, 0) > 0;
    }

    public Map<HotMPerk, Integer> getPerkLevels() {
        return Collections.unmodifiableMap(perkLevels);
    }
}
