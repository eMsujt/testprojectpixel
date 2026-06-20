package com.skyblock.core.manager;

import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public final class HOTMManager {

    public enum HotMNode {
        MINING_SPEED(50, "Mining Speed"),
        MINING_SPEED_BOOST(1, "Mining Speed Boost"),
        PICKOBULUS(1, "Pickobulus"),
        MINING_FORTUNE(50, "Mining Fortune"),
        DAILY_POWDER(100, "Daily Powder"),
        EFFICIENT_MINER(100, "Efficient Miner"),
        QUICK_FORGE(20, "Quick Forge"),
        TITANIUM_INSANITY(50, "Titanium Insanity"),
        LUCK_OF_THE_CAVE(45, "Luck of the Cave"),
        POWDER_BUFF(50, "Powder Buff"),
        MINING_MADNESS(1, "Mining Madness"),
        SKY_MALL(1, "Sky Mall"),
        GOBLIN_KILLER(1, "Goblin Killer"),
        STAR_POWDER(1, "Star Powder"),
        MOLE(200, "Mole"),
        PROFESSIONAL(140, "Professional"),
        LONESOME_MINER(45, "Lonesome Miner"),
        GREAT_EXPLORER(20, "Great Explorer"),
        FORTUNATE(20, "Fortunate"),
        MINING_EXPERIENCE_BOOST(100, "Mining Experience Boost"),
        SEASONED_MINEMAN(100, "Seasoned Mineman"),
        ANOMALOUS_DESIRE(20, "Anomalous Desire"),
        MANIACAL_MINER(1, "Maniacal Miner"),
        VEIN_SEEKER(1, "Vein Seeker");

        public final int maxLevel;
        private final String displayName;

        HotMNode(int maxLevel, String displayName) {
            this.maxLevel = maxLevel;
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    public static final int MAX_TIER = 7;

    private static final long[] TIER_XP_THRESHOLDS = {0L, 3000L, 9000L, 25000L, 60000L, 100000L, 150000L};

    public static final Map<String, int[]> NODE_POWDER_COSTS;

    static {
        Map<String, int[]> m = new LinkedHashMap<>();
        m.put("MINING_SPEED",              buildPowderCosts(50,  3000, 2.0));
        m.put("MINING_SPEED_BOOST",        new int[]{20000});
        m.put("PICKOBULUS",                new int[]{1200});
        m.put("MINING_FORTUNE",            buildPowderCosts(50,  3000, 2.0));
        m.put("DAILY_POWDER",              buildPowderCosts(100, 1200, 1.8));
        m.put("EFFICIENT_MINER",           buildPowderCosts(100, 1200, 1.8));
        m.put("QUICK_FORGE",               buildPowderCosts(20,  7500, 2.2));
        m.put("TITANIUM_INSANITY",         buildPowderCosts(50,  4000, 2.1));
        m.put("LUCK_OF_THE_CAVE",          buildPowderCosts(45,  2000, 2.0));
        m.put("POWDER_BUFF",               buildPowderCosts(50,  3000, 2.0));
        m.put("MINING_MADNESS",            new int[]{2000});
        m.put("SKY_MALL",                  new int[]{2000});
        m.put("GOBLIN_KILLER",             new int[]{2000});
        m.put("STAR_POWDER",               new int[]{2000});
        m.put("MOLE",                      buildPowderCosts(200, 1000, 1.4));
        m.put("PROFESSIONAL",              buildPowderCosts(140, 2000, 1.7));
        m.put("LONESOME_MINER",            buildPowderCosts(45,  3500, 2.0));
        m.put("GREAT_EXPLORER",            buildPowderCosts(20,  4000, 2.2));
        m.put("FORTUNATE",                 buildPowderCosts(20,  4000, 2.2));
        m.put("MINING_EXPERIENCE_BOOST",   buildPowderCosts(100, 2000, 1.8));
        m.put("SEASONED_MINEMAN",          buildPowderCosts(100, 2000, 1.8));
        m.put("ANOMALOUS_DESIRE",          buildPowderCosts(20,  4000, 2.2));
        m.put("MANIACAL_MINER",            new int[]{2000});
        m.put("VEIN_SEEKER",               new int[]{4000});
        NODE_POWDER_COSTS = Collections.unmodifiableMap(m);
    }

    public static final Map<String, int[]> PERK_DATA;

    static {
        Map<String, int[]> p = new LinkedHashMap<>();
        p.put("MINING_SPEED",            new int[]{50,  20});
        p.put("MINING_FORTUNE",          new int[]{50,   5});
        p.put("DAILY_POWDER",            new int[]{100,  1});
        p.put("EFFICIENT_MINER",         new int[]{100,  1});
        p.put("QUICK_FORGE",             new int[]{20,   5});
        p.put("TITANIUM_INSANITY",       new int[]{50,   2});
        p.put("LUCK_OF_THE_CAVE",        new int[]{45,   1});
        p.put("POWDER_BUFF",             new int[]{50,   1});
        p.put("MOLE",                    new int[]{200,  1});
        p.put("PROFESSIONAL",            new int[]{140,  4});
        p.put("LONESOME_MINER",          new int[]{45,   5});
        p.put("GREAT_EXPLORER",          new int[]{20,   3});
        p.put("FORTUNATE",               new int[]{20,   4});
        p.put("MINING_EXPERIENCE_BOOST", new int[]{100,  1});
        p.put("SEASONED_MINEMAN",        new int[]{100,  1});
        p.put("ANOMALOUS_DESIRE",        new int[]{20,   2});
        p.put("MINING_SPEED_BOOST",      new int[]{1,    0});
        p.put("PICKOBULUS",              new int[]{3,    0});
        p.put("MINING_MADNESS",          new int[]{1,    0});
        p.put("SKY_MALL",               new int[]{1,    0});
        p.put("GOBLIN_KILLER",           new int[]{1,    0});
        p.put("STAR_POWDER",             new int[]{1,    0});
        p.put("MANIACAL_MINER",          new int[]{1,    0});
        p.put("VEIN_SEEKER",             new int[]{1,    0});
        PERK_DATA = Collections.unmodifiableMap(p);
    }

    private static int[] buildPowderCosts(int levels, int base, double scale) {
        int[] costs = new int[levels];
        for (int i = 0; i < levels; i++) {
            costs[i] = (int) Math.round(base * Math.pow(i + 1, scale));
        }
        return costs;
    }

    private static final HOTMManager INSTANCE = new HOTMManager();

    private final Map<UUID, int[]> playerPerks = new HashMap<>();
    private final Map<UUID, Integer> hotmTier = new HashMap<>();
    private final Map<UUID, Long> miningXp = new HashMap<>();
    private final Map<UUID, Long> mithrilPowder = new HashMap<>();
    private final Map<UUID, Long> gemstonePowder = new HashMap<>();
    private final Map<UUID, List<String>> hotmHistory = new HashMap<>();

    private HOTMManager() {
    }

    public static HOTMManager getInstance() {
        return INSTANCE;
    }

    public int getLevel(UUID playerId, HotMNode node) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(node, "node");
        int[] levels = playerPerks.get(playerId);
        return levels == null ? 0 : levels[node.ordinal()];
    }

    public void setLevel(UUID playerId, HotMNode node, int level) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(node, "node");
        if (level < 0) {
            throw new IllegalArgumentException("level must not be negative");
        }
        int clamped = Math.min(level, node.maxLevel);
        int[] levels = playerPerks.computeIfAbsent(playerId, id -> new int[HotMNode.values().length]);
        levels[node.ordinal()] = clamped;
    }

    public int upgrade(UUID playerId, HotMNode node) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(node, "node");
        int current = getLevel(playerId, node);
        if (current >= node.maxLevel) {
            return -1;
        }
        int[] levels = playerPerks.computeIfAbsent(playerId, id -> new int[HotMNode.values().length]);
        levels[node.ordinal()] = current + 1;
        recordHotmEvent(playerId, "Upgraded " + node.getDisplayName() + " to level " + (current + 1));
        return current + 1;
    }

    public int getUpgradeCost(HotMNode node, int currentLevel) {
        Objects.requireNonNull(node, "node");
        int[] costs = NODE_POWDER_COSTS.get(node.name());
        if (costs == null || currentLevel < 0 || currentLevel >= costs.length) {
            return -1;
        }
        return costs[currentLevel];
    }

    public int getPerkBonus(UUID playerId, HotMNode node) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(node, "node");
        int[] data = PERK_DATA.get(node.name());
        if (data == null || data.length < 2) {
            return 0;
        }
        return data[1] * getLevel(playerId, node);
    }

    public int purchaseUpgrade(UUID playerId, HotMNode node) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(node, "node");
        int current = getLevel(playerId, node);
        if (current >= node.maxLevel) {
            return -1;
        }
        int cost = getUpgradeCost(node, current);
        if (cost > 0 && !spendMithrilPowder(playerId, cost)) {
            return -2;
        }
        return upgrade(playerId, node);
    }

    public int[] getAllLevels(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        int[] levels = playerPerks.get(playerId);
        if (levels == null) {
            return new int[HotMNode.values().length];
        }
        return Arrays.copyOf(levels, levels.length);
    }

    public void reset(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        int[] levels = playerPerks.get(playerId);
        if (levels != null) {
            Arrays.fill(levels, 0);
        }
    }

    public long getMiningXp(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        return miningXp.getOrDefault(playerId, 0L);
    }

    public int addMiningXp(UUID playerId, long amount) {
        Objects.requireNonNull(playerId, "playerId");
        if (amount < 0) throw new IllegalArgumentException("amount must not be negative");
        long total = miningXp.merge(playerId, amount, Long::sum);
        int oldTier = getHotmTier(playerId);
        int newTier = computeTier(total);
        if (newTier > oldTier) {
            hotmTier.put(playerId, newTier);
            recordHotmEvent(playerId, "Reached HOTM Tier " + newTier);
        }
        return getHotmTier(playerId);
    }

    public long getMithrilPowder(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        return mithrilPowder.getOrDefault(playerId, 0L);
    }

    public void addMithrilPowder(UUID playerId, long amount) {
        Objects.requireNonNull(playerId, "playerId");
        if (amount < 0) throw new IllegalArgumentException("amount must not be negative");
        mithrilPowder.merge(playerId, amount, Long::sum);
    }

    public boolean spendMithrilPowder(UUID playerId, long amount) {
        Objects.requireNonNull(playerId, "playerId");
        if (amount < 0) throw new IllegalArgumentException("amount must not be negative");
        long current = getMithrilPowder(playerId);
        if (current < amount) return false;
        mithrilPowder.put(playerId, current - amount);
        recordHotmEvent(playerId, "Spent " + amount + " Mithril Powder");
        return true;
    }

    public long getGemstonePowder(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        return gemstonePowder.getOrDefault(playerId, 0L);
    }

    public void addGemstonePowder(UUID playerId, long amount) {
        Objects.requireNonNull(playerId, "playerId");
        if (amount < 0) throw new IllegalArgumentException("amount must not be negative");
        gemstonePowder.merge(playerId, amount, Long::sum);
    }

    public boolean spendGemstonePowder(UUID playerId, long amount) {
        Objects.requireNonNull(playerId, "playerId");
        if (amount < 0) throw new IllegalArgumentException("amount must not be negative");
        long current = getGemstonePowder(playerId);
        if (current < amount) return false;
        gemstonePowder.put(playerId, current - amount);
        recordHotmEvent(playerId, "Spent " + amount + " Gemstone Powder");
        return true;
    }

    public int getHotmTier(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        return hotmTier.getOrDefault(playerId, 1);
    }

    public void setHotmTier(UUID playerId, int tier) {
        Objects.requireNonNull(playerId, "playerId");
        hotmTier.put(playerId, Math.max(1, Math.min(MAX_TIER, tier)));
    }

    private static int computeTier(long totalXp) {
        int tier = 1;
        for (int i = 1; i < TIER_XP_THRESHOLDS.length; i++) {
            if (totalXp >= TIER_XP_THRESHOLDS[i]) {
                tier = i + 1;
            } else {
                break;
            }
        }
        return tier;
    }

    public void recordHotmEvent(UUID playerId, String summary) {
        Objects.requireNonNull(playerId, "playerId");
        hotmHistory.computeIfAbsent(playerId, id -> new ArrayList<>()).add(summary);
    }

    public List<String> getHotmHistory(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        return hotmHistory.getOrDefault(playerId, Collections.emptyList());
    }

    public Map<UUID, List<String>> getAllHotmHistory() {
        return Collections.unmodifiableMap(hotmHistory);
    }

    public String getMiningStats(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        return "HOTM Tier: " + getHotmTier(playerId)
                + " | Mithril Powder: " + getMithrilPowder(playerId)
                + " | Gemstone Powder: " + getGemstonePowder(playerId);
    }

    public boolean remove(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        hotmTier.remove(playerId);
        miningXp.remove(playerId);
        mithrilPowder.remove(playerId);
        gemstonePowder.remove(playerId);
        hotmHistory.remove(playerId);
        return playerPerks.remove(playerId) != null;
    }

    public void load(File dataFolder) {
        File file = new File(dataFolder, "hotm.yml");
        if (!file.exists()) {
            return;
        }
        YamlConfiguration cfg = YamlConfiguration.loadConfiguration(file);
        playerPerks.clear();
        hotmTier.clear();
        miningXp.clear();
        mithrilPowder.clear();
        gemstonePowder.clear();
        hotmHistory.clear();
        HotMNode[] nodes = HotMNode.values();
        for (String key : cfg.getKeys(false)) {
            try {
                UUID uuid = UUID.fromString(key);
                int[] levels = new int[nodes.length];
                boolean hasData = false;
                for (HotMNode node : nodes) {
                    String path = key + "." + node.name();
                    if (cfg.contains(path)) {
                        levels[node.ordinal()] = cfg.getInt(path, 0);
                        hasData = true;
                    }
                }
                if (hasData) {
                    playerPerks.put(uuid, levels);
                }
                String tierPath = key + ".hotm_tier";
                if (cfg.contains(tierPath)) {
                    hotmTier.put(uuid, Math.max(1, Math.min(MAX_TIER, cfg.getInt(tierPath, 1))));
                }
                String xpPath = key + ".mining_xp";
                if (cfg.contains(xpPath)) {
                    miningXp.put(uuid, cfg.getLong(xpPath, 0L));
                }
                String powderPath = key + ".mithril_powder";
                if (cfg.contains(powderPath)) {
                    mithrilPowder.put(uuid, cfg.getLong(powderPath, 0L));
                }
                String gemstonePath = key + ".gemstone_powder";
                if (cfg.contains(gemstonePath)) {
                    gemstonePowder.put(uuid, cfg.getLong(gemstonePath, 0L));
                }
                String historyPath = key + ".hotm_history";
                if (cfg.contains(historyPath)) {
                    List<String> entries = cfg.getStringList(historyPath);
                    if (!entries.isEmpty()) {
                        hotmHistory.put(uuid, new ArrayList<>(entries));
                    }
                }
            } catch (IllegalArgumentException ignored) {
                // skip malformed entries
            }
        }
    }

    public void save(File dataFolder) {
        File file = new File(dataFolder, "hotm.yml");
        YamlConfiguration cfg = new YamlConfiguration();
        for (Map.Entry<UUID, int[]> entry : playerPerks.entrySet()) {
            String key = entry.getKey().toString();
            int[] levels = entry.getValue();
            for (HotMNode node : HotMNode.values()) {
                int level = levels[node.ordinal()];
                if (level != 0) {
                    cfg.set(key + "." + node.name(), level);
                }
            }
        }
        for (Map.Entry<UUID, Integer> entry : hotmTier.entrySet()) {
            cfg.set(entry.getKey().toString() + ".hotm_tier", entry.getValue());
        }
        for (Map.Entry<UUID, Long> entry : miningXp.entrySet()) {
            if (entry.getValue() != 0) {
                cfg.set(entry.getKey().toString() + ".mining_xp", entry.getValue());
            }
        }
        for (Map.Entry<UUID, Long> entry : mithrilPowder.entrySet()) {
            if (entry.getValue() != 0) {
                cfg.set(entry.getKey().toString() + ".mithril_powder", entry.getValue());
            }
        }
        for (Map.Entry<UUID, Long> entry : gemstonePowder.entrySet()) {
            if (entry.getValue() != 0) {
                cfg.set(entry.getKey().toString() + ".gemstone_powder", entry.getValue());
            }
        }
        for (Map.Entry<UUID, List<String>> entry : hotmHistory.entrySet()) {
            if (!entry.getValue().isEmpty()) {
                cfg.set(entry.getKey().toString() + ".hotm_history", entry.getValue());
            }
        }
        try {
            cfg.save(file);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save hotm.yml", e);
        }
    }
}
