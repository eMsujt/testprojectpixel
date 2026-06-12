package com.skyblock.core.quest;

import com.skyblock.core.quests.QuestManager.QuestData;
import com.skyblock.core.quests.QuestManager.QuestStatus;
import com.skyblock.core.quests.QuestManager.QuestType;

import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * Singleton facade over {@link com.skyblock.core.quests.QuestManager} that adds a
 * static {@link QuestDefinition} catalogue (display name, description, default goal,
 * and coin reward for each {@link QuestType}).
 *
 * <p>Not thread-safe; synchronize externally if needed.</p>
 */
public final class QuestManager {

    /** Static metadata for a single quest type. */
    public static final class QuestDefinition {
        private final QuestType type;
        private final String displayName;
        private final String description;
        /** Suggested default goal players see in the UI. */
        private final long defaultGoal;
        /** Coin reward granted on completion. */
        private final long rewardCoins;

        public QuestDefinition(QuestType type, String displayName, String description,
                               long defaultGoal, long rewardCoins) {
            this.type = Objects.requireNonNull(type, "type");
            this.displayName = Objects.requireNonNull(displayName, "displayName");
            this.description = Objects.requireNonNull(description, "description");
            this.defaultGoal = defaultGoal;
            this.rewardCoins = rewardCoins;
        }

        public QuestType getType() { return type; }
        public String getDisplayName() { return displayName; }
        public String getDescription() { return description; }
        public long getDefaultGoal() { return defaultGoal; }
        public long getRewardCoins() { return rewardCoins; }
    }

    private static final QuestManager INSTANCE = new QuestManager();

    private final com.skyblock.core.quests.QuestManager delegate =
            com.skyblock.core.quests.QuestManager.getInstance();

    private final Map<QuestType, QuestDefinition> definitions;

    private QuestManager() {
        Map<QuestType, QuestDefinition> map = new EnumMap<>(QuestType.class);
        map.put(QuestType.KILL_MONSTERS,       def(QuestType.KILL_MONSTERS,       "Monster Slayer",      "Kill monsters in the world.",          50,   500));
        map.put(QuestType.COLLECT_ITEMS,       def(QuestType.COLLECT_ITEMS,       "Item Collector",      "Collect items from the environment.",  100,  800));
        map.put(QuestType.MINE_BLOCKS,         def(QuestType.MINE_BLOCKS,         "Block Miner",         "Mine blocks across the island.",       200,  600));
        map.put(QuestType.FISH_ITEMS,          def(QuestType.FISH_ITEMS,          "Fisherman",           "Catch fish from any body of water.",   30,   400));
        map.put(QuestType.CRAFT_ITEMS,         def(QuestType.CRAFT_ITEMS,         "Craftsman",           "Craft items at a crafting table.",     20,   300));
        map.put(QuestType.EXPLORE_ZONES,       def(QuestType.EXPLORE_ZONES,       "Explorer",            "Explore different zones.",             5,    750));
        map.put(QuestType.COMPLETE_DUNGEONS,   def(QuestType.COMPLETE_DUNGEONS,   "Dungeon Crawler",     "Complete dungeon runs.",               3,    2000));
        map.put(QuestType.EARN_COINS,          def(QuestType.EARN_COINS,          "Coin Earner",         "Earn coins through any activity.",     1000, 250));
        map.put(QuestType.REACH_SKILL_LEVEL,   def(QuestType.REACH_SKILL_LEVEL,   "Skill Trainer",       "Reach a target skill level.",          10,   1500));
        map.put(QuestType.TRADE_IN_BAZAAR,     def(QuestType.TRADE_IN_BAZAAR,     "Bazaar Trader",       "Complete trades in the Bazaar.",        10,   1000));
        definitions = Collections.unmodifiableMap(map);
    }

    private static QuestDefinition def(QuestType type, String name, String desc,
                                       long goal, long reward) {
        return new QuestDefinition(type, name, desc, goal, reward);
    }

    public static QuestManager getInstance() {
        return INSTANCE;
    }

    /** Returns the static definition for the given quest type. */
    public QuestDefinition getDefinition(QuestType type) {
        return definitions.get(Objects.requireNonNull(type, "type"));
    }

    /** Returns an unmodifiable view of all quest definitions. */
    public Map<QuestType, QuestDefinition> getDefinitions() {
        return definitions;
    }

    // ── Delegate runtime methods ──────────────────────────────────────────────

    public void startQuest(UUID playerId, QuestType type, long goal) {
        delegate.startQuest(playerId, type, goal);
    }

    public long addProgress(UUID playerId, QuestType type, long amount) {
        return delegate.addProgress(playerId, type, amount);
    }

    public long getProgress(UUID playerId, QuestType type) {
        return delegate.getProgress(playerId, type);
    }

    public QuestData getQuestData(UUID playerId, QuestType type) {
        return delegate.getQuestData(playerId, type);
    }

    public QuestStatus getStatus(UUID playerId, QuestType type) {
        return delegate.getStatus(playerId, type);
    }

    public boolean reset(UUID playerId) {
        return delegate.reset(playerId);
    }
}
