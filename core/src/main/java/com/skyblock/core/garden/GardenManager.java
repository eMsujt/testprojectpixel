package com.skyblock.core.garden;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public final class GardenManager {

    private final Map<UUID, Integer> unlockedPlots = new HashMap<>();
    private final Map<UUID, Map<String, Integer>> cropLevels = new HashMap<>();

    /** Static metadata for each SkyBlock garden crop: {baseYield, maxUpgradeLevel, milestoneCount}. */
    public static final Map<String, int[]> CROP_DATA;
    static {
        Map<String, int[]> m = new HashMap<>();
        m.put("wheat",      new int[]{1, 12, 25});
        m.put("carrot",     new int[]{3, 12, 25});
        m.put("potato",     new int[]{3, 12, 25});
        m.put("melon",      new int[]{4, 12, 25});
        m.put("pumpkin",    new int[]{1, 12, 25});
        m.put("sugarcane",  new int[]{2, 12, 25});
        m.put("cocoa",      new int[]{3, 12, 25});
        m.put("cactus",     new int[]{2, 12, 25});
        m.put("mushroom",   new int[]{1, 12, 25});
        CROP_DATA = Collections.unmodifiableMap(m);
    }

    private static final List<String> DEFAULT_CROPS = List.of(
            "wheat", "carrot", "potato", "melon", "pumpkin",
            "cactus", "sugarcane", "mushroom", "cocoa", "nether_wart"
    );

    public int getPlots(UUID uuid) {
        return unlockedPlots.getOrDefault(uuid, 1);
    }

    public void setPlots(UUID uuid, int plots) {
        unlockedPlots.put(uuid, Math.max(1, plots));
    }

    public int getCropLevel(UUID uuid, String crop) {
        return cropLevels.computeIfAbsent(uuid, k -> new HashMap<>()).getOrDefault(crop.toLowerCase(), 0);
    }

    public void setCropLevel(UUID uuid, String crop, int level) {
        cropLevels.computeIfAbsent(uuid, k -> new HashMap<>()).put(crop.toLowerCase(), Math.max(0, level));
    }

    public void incrementCropLevel(UUID uuid, String crop) {
        int current = getCropLevel(uuid, crop);
        setCropLevel(uuid, crop, current + 1);
    }

    public Map<String, Integer> getCropLevels(UUID uuid) {
        return Collections.unmodifiableMap(cropLevels.computeIfAbsent(uuid, k -> new HashMap<>()));
    }

    public static List<String> getDefaultCrops() {
        return DEFAULT_CROPS;
    }
}
