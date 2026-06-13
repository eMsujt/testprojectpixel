package com.skyblock.core.slayer;

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
        BLAZE("Blaze");

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
        INFERNO_DEMONLORD(SlayerType.BLAZE,       "Inferno Demonlord",     600_000);

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

    public boolean reset(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        boolean hadData = slayerExperience.remove(playerId) != null;
        hadData |= activeQuests.remove(playerId) != null;
        return hadData;
    }
}
