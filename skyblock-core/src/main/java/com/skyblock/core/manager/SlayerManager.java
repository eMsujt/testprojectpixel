package com.skyblock.core.manager;

import com.skyblock.core.config.Constants;
import org.bukkit.entity.EntityType;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.UUID;

public final class SlayerManager {

    public static final int MAX_LEVEL = 9;

    /** Boss HP per tier (T1–T5), keyed by slayer type name. */
    public static final Map<String, int[]> BOSS_HEALTH;

    static {
        Map<String, int[]> m = new LinkedHashMap<>();
        m.put("Zombie",   new int[]{500,       20_000,    400_000,   3_000_000,  10_000_000});
        m.put("Spider",   new int[]{10_000,      10_000,     80_000,   1_000_000,   5_000_000});
        m.put("Wolf",     new int[]{2_000,      40_000,    750_000,   2_000_000,   4_000_000});
        m.put("Enderman", new int[]{10_000,    100_000,  2_000_000,  10_000_000,  30_000_000});
        m.put("Blaze",    new int[]{3_000_000, 8_000_000, 20_000_000, 100_000_000, 250_000_000});
        BOSS_HEALTH = Collections.unmodifiableMap(m);
    }

    /** XP awarded per tier kill (T1, T2, T3, T4), keyed by slayer boss short name. */
    public static final Map<String, int[]> TIER_XP_THRESHOLDS;

    static {
        Map<String, int[]> m = new LinkedHashMap<>();
        m.put("Revenant",   new int[]{5,  15,  200, 1000});
        m.put("Tarantula",  new int[]{5,  25,  200, 1500});
        m.put("Sven",       new int[]{10, 30,  250, 1500});
        m.put("Voidgloom",  new int[]{10, 30,  250, 2500});
        m.put("Inferno",    new int[]{10, 30,  250, 2500});
        m.put("Riftstalker",new int[]{10, 30,  250, 2500});
        TIER_XP_THRESHOLDS = Collections.unmodifiableMap(m);
    }

    /** Summary data per slayer type: {maxLevel, coinsToActivate}. */
    public static final Map<String, int[]> SLAYER_BOSS_DATA;

    static {
        Map<String, int[]> m = new LinkedHashMap<>();
        m.put("ZOMBIE",   new int[]{9,  2_000});
        m.put("SPIDER",   new int[]{9,  2_000});
        m.put("WOLF",     new int[]{9,  2_000});
        m.put("ENDERMAN", new int[]{9,  2_000});
        m.put("BLAZE",    new int[]{9,  10_000});
        m.put("VAMPIRE",  new int[]{5,  0});
        SLAYER_BOSS_DATA = Collections.unmodifiableMap(m);
    }

    public enum SlayerType {
        ZOMBIE("Zombie",   EntityType.ZOMBIE,
                new int[]{5, 15, 200, 1_000, 5_000, 20_000, 100_000, 400_000, 1_000_000}),
        SPIDER("Spider",   EntityType.SPIDER,
                new int[]{5, 15, 200, 1_000, 5_000, 20_000, 100_000, 400_000, 1_000_000}),
        WOLF("Wolf",       EntityType.WOLF,
                new int[]{5, 15, 200, 1_000, 5_000, 20_000, 100_000, 400_000, 1_000_000}),
        ENDERMAN("Enderman", EntityType.ENDERMAN,
                new int[]{5, 15, 200, 1_000, 5_000, 20_000, 100_000, 400_000, 1_000_000}),
        BLAZE("Blaze",     EntityType.BLAZE,
                new int[]{5, 15, 200, 1_000, 5_000, 20_000, 100_000, 400_000, 1_000_000}),
        VAMPIRE("Vampire", EntityType.BAT,
                new int[]{5, 15, 200, 1_000, 5_000, 20_000, 100_000, 400_000, 1_000_000});

        private final String displayName;
        public final EntityType entityType;
        /** Cumulative XP required to reach each level (index = level - 1). */
        public final int[] xpTable;

        SlayerType(String displayName, EntityType entityType, int[] xpTable) {
            this.displayName = displayName;
            this.entityType = entityType;
            this.xpTable = xpTable;
        }

        public String getDisplayName() {
            return displayName;
        }

        public EntityType getEntityType() {
            return entityType;
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
        TIER_1, TIER_2, TIER_3, TIER_4, TIER_5
    }

    /** Mob kills required before the slayer boss can be summoned, per quest tier. */
    public static final Map<QuestTier, Integer> KILLS_TO_SPAWN_BOSS;

    static {
        Map<QuestTier, Integer> m = new EnumMap<>(QuestTier.class);
        m.put(QuestTier.TIER_1, 10);
        m.put(QuestTier.TIER_2, 20);
        m.put(QuestTier.TIER_3, 30);
        m.put(QuestTier.TIER_4, 40);
        m.put(QuestTier.TIER_5, 50);
        KILLS_TO_SPAWN_BOSS = Collections.unmodifiableMap(m);
    }

    /** Coins required to summon a slayer boss, per quest tier (T1–T5), keyed by slayer type. */
    public static final Map<SlayerType, int[]> SPAWN_COST;

    static {
        // Hypixel uses a uniform tier start-cost ladder for the four standard slayers
        // (2k/7.5k/20k/50k, +100k T5 for Revenant only); Blaze has its own higher scale.
        // T5 for Spider/Wolf/Enderman/Blaze is undocumented (max tier IV) — kept as a
        // non-zero sentinel rather than invented.
        Map<SlayerType, int[]> m = new EnumMap<>(SlayerType.class);
        m.put(SlayerType.ZOMBIE,   new int[]{2_000,   7_500,    20_000,    50_000,   100_000});
        m.put(SlayerType.SPIDER,   new int[]{2_000,   7_500,    20_000,    50_000,   500_000});
        m.put(SlayerType.WOLF,     new int[]{2_000,   7_500,    20_000,    50_000, 1_000_000});
        m.put(SlayerType.ENDERMAN, new int[]{2_000,   7_500,    20_000,    50_000, 4_000_000});
        m.put(SlayerType.BLAZE,    new int[]{10_000, 25_000,    60_000,   150_000, 25_000_000});
        m.put(SlayerType.VAMPIRE,  new int[]{0,        0,          0,         0,          0});
        SPAWN_COST = Collections.unmodifiableMap(m);
    }

    /** Maps each slayer type to its key in {@link #TIER_XP_THRESHOLDS}. */
    private static final Map<SlayerType, String> XP_KEY;

    static {
        Map<SlayerType, String> m = new EnumMap<>(SlayerType.class);
        m.put(SlayerType.ZOMBIE,   "Revenant");
        m.put(SlayerType.SPIDER,   "Tarantula");
        m.put(SlayerType.WOLF,     "Sven");
        m.put(SlayerType.ENDERMAN, "Voidgloom");
        m.put(SlayerType.BLAZE,    "Inferno");
        m.put(SlayerType.VAMPIRE,  "Riftstalker");
        XP_KEY = Collections.unmodifiableMap(m);
    }

    /** A single possible reward from a slain slayer boss with its base drop chance. */
    public static final class SlayerDrop {
        public final String item;
        /** Base drop chance in the range {@code [0, 1]} at tier 1. */
        public final double chance;

        SlayerDrop(String item, double chance) {
            this.item = item;
            this.chance = chance;
        }
    }

    /** Possible drops per slayer type. Chances scale up with the boss tier. */
    public static final Map<SlayerType, List<SlayerDrop>> DROP_TABLE;

    static {
        Map<SlayerType, List<SlayerDrop>> m = new EnumMap<>(SlayerType.class);
        m.put(SlayerType.ZOMBIE, Arrays.asList(
                new SlayerDrop("Revenant Flesh", Constants.SLAYER_COMMON_DROP_CHANCE),
                new SlayerDrop("Foul Flesh", 0.10),
                new SlayerDrop("Beheaded Horror", Constants.SLAYER_RARE_DROP_CHANCE)));
        m.put(SlayerType.SPIDER, Arrays.asList(
                new SlayerDrop("Tarantula Web", Constants.SLAYER_COMMON_DROP_CHANCE),
                new SlayerDrop("Toxic Arrow Poison", 0.08),
                new SlayerDrop("Digested Mosquito", Constants.SLAYER_RARE_DROP_CHANCE)));
        m.put(SlayerType.WOLF, Arrays.asList(
                new SlayerDrop("Wolf Tooth", Constants.SLAYER_COMMON_DROP_CHANCE),
                new SlayerDrop("Spirit Rune", 0.05),
                new SlayerDrop("Red Claw Egg", Constants.SLAYER_RARE_DROP_CHANCE)));
        m.put(SlayerType.ENDERMAN, Arrays.asList(
                new SlayerDrop("Null Sphere", Constants.SLAYER_COMMON_DROP_CHANCE),
                new SlayerDrop("Mana Steal", 0.05),
                new SlayerDrop("Judgement Core", Constants.SLAYER_RARE_DROP_CHANCE)));
        m.put(SlayerType.BLAZE, Arrays.asList(
                new SlayerDrop("Derelict Ashe", Constants.SLAYER_COMMON_DROP_CHANCE),
                new SlayerDrop("Bundle of Magma", 0.05),
                new SlayerDrop("Hollow Wand", Constants.SLAYER_RARE_DROP_CHANCE)));
        m.put(SlayerType.VAMPIRE, Arrays.asList(
                new SlayerDrop("Blood Ichor", 0.20),
                new SlayerDrop("Chalice", 0.05),
                new SlayerDrop("Twilight Arrow Poison", Constants.SLAYER_RARE_DROP_CHANCE)));
        DROP_TABLE = Collections.unmodifiableMap(m);
    }

    /** Live combat state for a summoned slayer boss, including health and phase tracking. */
    public static final class BossFight {
        public final SlayerType type;
        public final QuestTier tier;
        private final int maxHealth;
        private final int totalPhases;
        private int health;
        private int phase;

        BossFight(SlayerType type, QuestTier tier, int maxHealth, int totalPhases) {
            this.type = type;
            this.tier = tier;
            this.maxHealth = maxHealth;
            this.totalPhases = totalPhases;
            this.health = maxHealth;
            this.phase = 1;
        }

        public int getMaxHealth() {
            return maxHealth;
        }

        public int getHealth() {
            return health;
        }

        public int getPhase() {
            return phase;
        }

        public int getTotalPhases() {
            return totalPhases;
        }

        public boolean isDead() {
            return health <= 0;
        }

        /** Applies damage, escalates the combat phase as health falls, and returns remaining health. */
        public int damage(int amount) {
            if (amount < 0) {
                throw new IllegalArgumentException("amount must not be negative, got " + amount);
            }
            health = Math.max(0, health - amount);
            int completed = (int) ((long) (maxHealth - health) * totalPhases / maxHealth);
            phase = Math.min(totalPhases, completed + 1);
            return health;
        }
    }

    /** The outcome of slaying a slayer boss: experience gained and the items dropped. */
    public static final class SlayerReward {
        public final long xp;
        public final List<String> drops;

        SlayerReward(long xp, List<String> drops) {
            this.xp = xp;
            this.drops = Collections.unmodifiableList(drops);
        }

        public long getXp() {
            return xp;
        }

        public List<String> getDrops() {
            return drops;
        }
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
    private final Map<UUID, BossFight> activeBosses = new HashMap<>();
    private final Random random = new Random();

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
        activeBosses.remove(playerId);
        bossActive.remove(playerId);
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
        int[] table = type.xpTable;
        while (level < MAX_LEVEL && xp >= table[level]) {
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

    /** Records a mob kill toward the active quest's boss-spawn requirement and returns the new total. */
    public int addQuestKill(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        SlayerQuest quest = activeQuests.get(playerId);
        if (quest == null) {
            throw new IllegalStateException("Player has no active slayer quest");
        }
        return quest.incrementKills();
    }

    /** Returns the coin cost to summon the slayer boss for the given type and tier. */
    public int getSpawnCost(SlayerType type, QuestTier tier) {
        Objects.requireNonNull(type, "type");
        Objects.requireNonNull(tier, "tier");
        int[] costs = SPAWN_COST.get(type);
        int ordinal = tier.ordinal();
        return costs != null && ordinal < costs.length ? costs[ordinal] : 0;
    }

    /** Returns {@code true} when the player has killed enough mobs to summon the boss for their quest. */
    public boolean canSpawnBoss(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        SlayerQuest quest = activeQuests.get(playerId);
        if (quest == null || quest.isBossSpawned()) {
            return false;
        }
        return quest.getKills() >= KILLS_TO_SPAWN_BOSS.getOrDefault(quest.tier, Integer.MAX_VALUE);
    }

    /**
     * Summons the slayer boss for the player's active quest, returning its fresh combat state.
     * The boss health scales with the quest tier and the number of combat phases grows with it.
     */
    public BossFight spawnBoss(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        SlayerQuest quest = activeQuests.get(playerId);
        if (quest == null) {
            throw new IllegalStateException("Player has no active slayer quest");
        }
        if (quest.isBossSpawned()) {
            throw new IllegalStateException("Boss already spawned for this quest");
        }
        if (!canSpawnBoss(playerId)) {
            throw new IllegalStateException("Not enough kills to spawn the boss");
        }
        int tierIndex = quest.tier.ordinal();
        int[] healthByTier = BOSS_HEALTH.get(quest.type.getDisplayName());
        SlayerBoss boss = SlayerBoss.forType(quest.type);
        int maxHealth = healthByTier != null
                ? healthByTier[tierIndex]
                : (boss != null ? boss.maxHealth : 1);
        int totalPhases = tierIndex + 1;
        BossFight fight = new BossFight(quest.type, quest.tier, maxHealth, totalPhases);
        activeBosses.put(playerId, fight);
        quest.setBossSpawned(true);
        setBossActive(playerId, true);
        return fight;
    }

    public BossFight getBossFight(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        return activeBosses.get(playerId);
    }

    /** Applies damage to the player's active boss and returns its remaining health. */
    public int damageBoss(UUID playerId, int amount) {
        Objects.requireNonNull(playerId, "playerId");
        BossFight fight = activeBosses.get(playerId);
        if (fight == null) {
            throw new IllegalStateException("Player has no active boss");
        }
        return fight.damage(amount);
    }

    /**
     * Finalises a defeated boss: awards tier-scaled experience, rolls the drop table, completes the
     * quest, and clears the combat state. The boss must have been reduced to zero health first.
     */
    public SlayerReward killBoss(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        BossFight fight = activeBosses.get(playerId);
        if (fight == null) {
            throw new IllegalStateException("Player has no active boss");
        }
        if (!fight.isDead()) {
            throw new IllegalStateException("Boss is not dead yet");
        }
        int tierIndex = fight.tier.ordinal();
        int[] xpByTier = TIER_XP_THRESHOLDS.get(XP_KEY.get(fight.type));
        long xpReward = xpByTier != null ? xpByTier[tierIndex] : 0L;

        List<String> drops = new ArrayList<>();
        List<SlayerDrop> table = DROP_TABLE.get(fight.type);
        if (table != null) {
            for (SlayerDrop drop : table) {
                double chance = Math.min(1.0, drop.chance * (tierIndex + 1));
                if (random.nextDouble() < chance) {
                    drops.add(drop.item);
                }
            }
        }

        addKill(playerId, fight.type);
        activeBosses.remove(playerId);
        setBossActive(playerId, false);
        long total = completeQuest(playerId, xpReward);
        return new SlayerReward(total >= 0 ? xpReward : 0L, drops);
    }

    /**
     * Escalates the player's active quest to the next tier, replacing it with a fresh quest.
     * Only allowed before a boss has spawned and never beyond {@link QuestTier#TIER_4}.
     */
    public SlayerQuest escalateTier(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        SlayerQuest quest = activeQuests.get(playerId);
        if (quest == null) {
            throw new IllegalStateException("Player has no active slayer quest");
        }
        if (quest.isBossSpawned()) {
            throw new IllegalStateException("Cannot escalate tier after the boss has spawned");
        }
        int nextIndex = quest.tier.ordinal() + 1;
        QuestTier[] tiers = QuestTier.values();
        if (nextIndex >= tiers.length) {
            throw new IllegalStateException("Quest is already at the maximum tier");
        }
        SlayerQuest escalated = new SlayerQuest(quest.type, tiers[nextIndex]);
        activeQuests.put(playerId, escalated);
        return escalated;
    }

    public boolean reset(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        boolean hadData = slayerExperience.remove(playerId) != null;
        hadData |= activeQuests.remove(playerId) != null;
        hadData |= killCounts.remove(playerId) != null;
        hadData |= activeBosses.remove(playerId) != null;
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
        activeBosses.clear();
        activeQuests.clear();
        for (String key : cfg.getKeys(false)) {
            try {
                UUID uuid = UUID.fromString(key);
                if (cfg.contains(key + ".quest.type")) {
                    try {
                        SlayerType type = SlayerType.valueOf(cfg.getString(key + ".quest.type"));
                        QuestTier tier = QuestTier.valueOf(cfg.getString(key + ".quest.tier", "TIER_1"));
                        SlayerQuest quest = new SlayerQuest(type, tier);
                        quest.setBossSpawned(cfg.getBoolean(key + ".quest.bossSpawned", false));
                        quest.setComplete(cfg.getBoolean(key + ".quest.complete", false));
                        int questKills = cfg.getInt(key + ".quest.kills", 0);
                        for (int i = 0; i < questKills; i++) {
                            quest.incrementKills();
                        }
                        activeQuests.put(uuid, quest);
                        if (cfg.contains(key + ".boss.health")) {
                            int maxHealth = cfg.getInt(key + ".boss.maxHealth", 1);
                            int totalPhases = cfg.getInt(key + ".boss.totalPhases", 1);
                            int health = cfg.getInt(key + ".boss.health", maxHealth);
                            BossFight fight = new BossFight(type, tier, maxHealth, totalPhases);
                            fight.damage(Math.max(0, maxHealth - health));
                            activeBosses.put(uuid, fight);
                        }
                    } catch (IllegalArgumentException ignored) {
                        // skip malformed quest entries
                    }
                }
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
        for (Map.Entry<UUID, SlayerQuest> entry : activeQuests.entrySet()) {
            String key = entry.getKey().toString();
            SlayerQuest quest = entry.getValue();
            cfg.set(key + ".quest.type", quest.type.name());
            cfg.set(key + ".quest.tier", quest.tier.name());
            cfg.set(key + ".quest.kills", quest.getKills());
            cfg.set(key + ".quest.bossSpawned", quest.isBossSpawned());
            cfg.set(key + ".quest.complete", quest.isComplete());
            BossFight fight = activeBosses.get(entry.getKey());
            if (fight != null) {
                cfg.set(key + ".boss.maxHealth", fight.getMaxHealth());
                cfg.set(key + ".boss.totalPhases", fight.getTotalPhases());
                cfg.set(key + ".boss.health", fight.getHealth());
            }
        }
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
