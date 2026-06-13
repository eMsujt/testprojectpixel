package com.skyblock.core.slayer;

import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public final class SlayerManager {

    public static final int MAX_LEVEL = 9;

    private static final long[] XP_PER_LEVEL = {
            5, 15, 200, 1000, 5000, 20000, 100000, 400000, 1000000
    };

    public enum SlayerType {
        ZOMBIE("Zombie"),
        SPIDER("Spider"),
        WOLF("Wolf"),
        ENDERMAN("Enderman"),
        BLAZE("Blaze"),
        VAMPIRE("Vampire");

        private final String displayName;

        SlayerType(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    /** Named slayer bosses, each linked to the {@link SlayerType} that spawns them. */
    public enum SlayerBoss {
        REVENANT_HORROR(SlayerType.ZOMBIE,     "Revenant Horror",       200_000),
        TARANTULA_BROODFATHER(SlayerType.SPIDER,     "Tarantula Broodfather", 300_000),
        SVEN_PACKMASTER(SlayerType.WOLF,         "Sven Packmaster",       400_000),
        VOIDGLOOM_SERAPH(SlayerType.ENDERMAN, "Voidgloom Seraph",      500_000),
        INFERNO_DEMONLORD(SlayerType.BLAZE,       "Inferno Demonlord",     600_000),
        RIFTSTALKER_BLOODFIEND(SlayerType.VAMPIRE, "Riftstalker Bloodfiend", 700_000);

        public final SlayerType type;
        public final String displayName;
        /** Maximum health points of this boss at its highest tier. */
        public final int maxHealth;

        SlayerBoss(SlayerType type, String displayName, int maxHealth) {
            this.type = type;
            this.displayName = displayName;
            this.maxHealth = maxHealth;
        }

        public String getDisplayName() {
            return displayName;
        }

        /** Returns the {@link SlayerBoss} for the given {@link SlayerType}, or {@code null} if none. */
        public static SlayerBoss forType(SlayerType type) {
            for (SlayerBoss boss : values()) {
                if (boss.type == type) {
                    return boss;
                }
            }
            return null;
        }
    }

    public enum QuestTier {
        TIER_1, TIER_2, TIER_3, TIER_4
    }

    public static final class SlayerQuest {
        public final SlayerType type;
        public final QuestTier tier;
        private int kills;
        private boolean bossSpawned;
        private boolean complete;

        public SlayerQuest(SlayerType type, QuestTier tier) {
            this.type = Objects.requireNonNull(type, "type");
            this.tier = Objects.requireNonNull(tier, "tier");
        }

        public int getKills() {
            return kills;
        }

        public boolean isBossSpawned() {
            return bossSpawned;
        }

        public boolean isComplete() {
            return complete;
        }

        public int incrementKills() {
            return ++kills;
        }

        public void setBossSpawned(boolean bossSpawned) {
            this.bossSpawned = bossSpawned;
        }

        public void setComplete(boolean complete) {
            this.complete = complete;
        }
    }

    private static final SlayerManager INSTANCE = new SlayerManager();

    private final Map<UUID, Map<SlayerType, Long>> slayerExperience = new HashMap<>();
    private final Map<UUID, SlayerQuest> activeQuests = new HashMap<>();
    private final Map<UUID, Map<SlayerType, Integer>> killCounts = new HashMap<>();
    private final Map<UUID, Boolean> bossActive = new HashMap<>();

    private SlayerManager() {}

    public static SlayerManager getInstance() {
        return INSTANCE;
    }

    public SlayerQuest startQuest(UUID playerId, SlayerType type, QuestTier tier) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(type, "type");
        Objects.requireNonNull(tier, "tier");
        if (activeQuests.containsKey(playerId)) {
            throw new IllegalStateException("Player already has an active slayer quest");
        }
        SlayerQuest quest = new SlayerQuest(type, tier);
        activeQuests.put(playerId, quest);
        return quest;
    }

    public SlayerQuest getActiveQuest(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        return activeQuests.get(playerId);
    }

    public long completeQuest(UUID playerId, long xpReward) {
        Objects.requireNonNull(playerId, "playerId");
        SlayerQuest quest = activeQuests.remove(playerId);
        if (quest == null) {
            return -1L;
        }
        quest.setComplete(true);
        return addExperience(playerId, quest.type, xpReward);
    }

    public boolean cancelQuest(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        return activeQuests.remove(playerId) != null;
    }

    public long addExperience(UUID playerId, SlayerType type, long amount) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(type, "type");
        if (amount < 0) {
            throw new IllegalArgumentException("amount must not be negative, got " + amount);
        }
        Map<SlayerType, Long> xpMap = slayerExperience.computeIfAbsent(
                playerId, id -> new EnumMap<>(SlayerType.class));
        long total = xpMap.getOrDefault(type, 0L) + amount;
        xpMap.put(type, total);
        return total;
    }

    public long getExperience(UUID playerId, SlayerType type) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(type, "type");
        Map<SlayerType, Long> xpMap = slayerExperience.get(playerId);
        return xpMap == null ? 0L : xpMap.getOrDefault(type, 0L);
    }

    public int getLevel(UUID playerId, SlayerType type) {
        long xp = getExperience(playerId, type);
        int level = 0;
        while (level < MAX_LEVEL && xp >= XP_PER_LEVEL[level]) {
            level++;
        }
        return level;
    }

    public int addKill(UUID playerId, SlayerType type) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(type, "type");
        Map<SlayerType, Integer> counts = killCounts.computeIfAbsent(
                playerId, id -> new EnumMap<>(SlayerType.class));
        int total = counts.getOrDefault(type, 0) + 1;
        counts.put(type, total);
        return total;
    }

    public int getKillCount(UUID playerId, SlayerType type) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(type, "type");
        Map<SlayerType, Integer> counts = killCounts.get(playerId);
        return counts == null ? 0 : counts.getOrDefault(type, 0);
    }

    public void setBossActive(UUID playerId, boolean active) {
        Objects.requireNonNull(playerId, "playerId");
        bossActive.put(playerId, active);
    }

    public boolean isBossActive(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        return Boolean.TRUE.equals(bossActive.get(playerId));
    }

    public boolean reset(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        boolean hadData = slayerExperience.remove(playerId) != null;
        hadData |= activeQuests.remove(playerId) != null;
        hadData |= killCounts.remove(playerId) != null;
        bossActive.remove(playerId);
        return hadData;
    }

    public void load(File dataFolder) {
        File file = new File(dataFolder, "slayer.yml");
        if (!file.exists()) {
            return;
        }
        YamlConfiguration cfg = YamlConfiguration.loadConfiguration(file);
        slayerExperience.clear();
        killCounts.clear();
        bossActive.clear();
        for (String key : cfg.getKeys(false)) {
            try {
                UUID uuid = UUID.fromString(key);
                if (cfg.isConfigurationSection(key + ".xp")) {
                    Map<SlayerType, Long> xpMap = new EnumMap<>(SlayerType.class);
                    for (String typeName : cfg.getConfigurationSection(key + ".xp").getKeys(false)) {
                        try {
                            SlayerType type = SlayerType.valueOf(typeName);
                            xpMap.put(type, cfg.getLong(key + ".xp." + typeName, 0L));
                        } catch (IllegalArgumentException ignored) {
                            // skip unknown slayer types
                        }
                    }
                    if (!xpMap.isEmpty()) {
                        slayerExperience.put(uuid, xpMap);
                    }
                }
                if (cfg.isConfigurationSection(key + ".kills")) {
                    Map<SlayerType, Integer> counts = new EnumMap<>(SlayerType.class);
                    for (String typeName : cfg.getConfigurationSection(key + ".kills").getKeys(false)) {
                        try {
                            SlayerType type = SlayerType.valueOf(typeName);
                            counts.put(type, cfg.getInt(key + ".kills." + typeName, 0));
                        } catch (IllegalArgumentException ignored) {
                            // skip unknown slayer types
                        }
                    }
                    if (!counts.isEmpty()) {
                        killCounts.put(uuid, counts);
                    }
                }
                if (cfg.contains(key + ".bossActive")) {
                    bossActive.put(uuid, cfg.getBoolean(key + ".bossActive", false));
                }
            } catch (IllegalArgumentException ignored) {
                // skip malformed entries
            }
        }
    }

    public void save(File dataFolder) {
        File file = new File(dataFolder, "slayer.yml");
        YamlConfiguration cfg = new YamlConfiguration();
        for (Map.Entry<UUID, Map<SlayerType, Long>> entry : slayerExperience.entrySet()) {
            String key = entry.getKey().toString();
            for (Map.Entry<SlayerType, Long> xp : entry.getValue().entrySet()) {
                cfg.set(key + ".xp." + xp.getKey().name(), xp.getValue());
            }
        }
        for (Map.Entry<UUID, Map<SlayerType, Integer>> entry : killCounts.entrySet()) {
            String key = entry.getKey().toString();
            for (Map.Entry<SlayerType, Integer> kc : entry.getValue().entrySet()) {
                cfg.set(key + ".kills." + kc.getKey().name(), kc.getValue());
            }
        }
        for (Map.Entry<UUID, Boolean> entry : bossActive.entrySet()) {
            cfg.set(entry.getKey().toString() + ".bossActive", entry.getValue());
        }
        try {
            cfg.save(file);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save slayer.yml", e);
        }
    }
}
