package com.skyblock.quests.registry;

import com.skyblock.quests.model.QuestObjectiveType;
import com.skyblock.quests.model.QuestType;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * A static registry of the 12 pre-defined SkyBlock quests available to
 * players. Each entry is an immutable {@link Quest} that can be looked up by
 * its unique string identifier.
 *
 * <p>This class is not instantiable; all access is via its static methods.</p>
 */
public final class QuestRegistry {

    /**
     * An immutable description of a pre-defined SkyBlock quest: its unique
     * identifier, human-readable name, {@link QuestType},
     * {@link QuestObjectiveType}, and the progress target required to complete
     * it.
     */
    public static final class Quest {

        private final String id;
        private final String displayName;
        private final QuestType type;
        private final QuestObjectiveType objectiveType;
        private final int target;

        private Quest(String id, String displayName, QuestType type,
                QuestObjectiveType objectiveType, int target) {
            this.id = id;
            this.displayName = displayName;
            this.type = type;
            this.objectiveType = objectiveType;
            this.target = target;
        }

        /**
         * Returns the unique identifier used to look up and start this quest.
         *
         * @return the quest id, never {@code null}
         */
        public String getId() {
            return id;
        }

        /**
         * Returns the human-readable name shown in quest logs and menus.
         *
         * @return the display name, never {@code null}
         */
        public String getDisplayName() {
            return displayName;
        }

        /**
         * Returns the category this quest belongs to.
         *
         * @return the quest type, never {@code null}
         */
        public QuestType getType() {
            return type;
        }

        /**
         * Returns the kind of objective the quest asks the player to complete.
         *
         * @return the objective type, never {@code null}
         */
        public QuestObjectiveType getObjectiveType() {
            return objectiveType;
        }

        /**
         * Returns the progress target required to complete the quest.
         *
         * @return the target, always at least one
         */
        public int getTarget() {
            return target;
        }
    }

    private static final Map<String, Quest> REGISTRY;

    static {
        Map<String, Quest> map = new LinkedHashMap<>();
        add(map, "kill_zombies_daily",     "Slay Zombies",          QuestType.DAILY,  QuestObjectiveType.KILL_MOB,          50);
        add(map, "kill_skeletons_daily",   "Slay Skeletons",        QuestType.DAILY,  QuestObjectiveType.KILL_MOB,          25);
        add(map, "kill_spiders_daily",     "Slay Spiders",          QuestType.DAILY,  QuestObjectiveType.KILL_MOB,          10);
        add(map, "kill_endermen_weekly",   "Hunt Endermen",         QuestType.WEEKLY, QuestObjectiveType.KILL_MOB,          30);
        add(map, "mine_cobblestone_daily", "Mine Cobblestone",      QuestType.DAILY,  QuestObjectiveType.COLLECT_ITEM,      64);
        add(map, "mine_iron_ore_daily",    "Mine Iron Ore",         QuestType.DAILY,  QuestObjectiveType.COLLECT_ITEM,      32);
        add(map, "collect_wheat_daily",    "Harvest Wheat",         QuestType.DAILY,  QuestObjectiveType.COLLECT_ITEM,      32);
        add(map, "collect_pumpkins_weekly","Harvest Pumpkins",      QuestType.WEEKLY, QuestObjectiveType.COLLECT_ITEM,      20);
        add(map, "collect_diamonds_side",  "Gather Diamonds",       QuestType.SIDE,   QuestObjectiveType.COLLECT_ITEM,       8);
        add(map, "reach_mining_5",         "Mining Apprentice",     QuestType.STORY,  QuestObjectiveType.REACH_SKILL_LEVEL,  5);
        add(map, "visit_hub",              "Welcome to the Hub",    QuestType.STORY,  QuestObjectiveType.VISIT_LOCATION,     1);
        add(map, "talk_to_blacksmith",     "Meet the Blacksmith",   QuestType.SIDE,   QuestObjectiveType.TALK_TO_NPC,        1);
        REGISTRY = Collections.unmodifiableMap(map);
    }

    private static void add(Map<String, Quest> map, String id, String displayName,
            QuestType type, QuestObjectiveType objectiveType, int target) {
        map.put(id, new Quest(id, displayName, type, objectiveType, target));
    }

    private QuestRegistry() {}

    /**
     * Returns the quest with the given identifier, or {@code null} if no such
     * quest is registered.
     *
     * @param id the quest identifier
     * @return the quest, or {@code null}
     */
    public static Quest get(String id) {
        return REGISTRY.get(id);
    }

    /**
     * Returns whether the registry contains a quest with the given identifier.
     *
     * @param id the quest identifier
     * @return {@code true} if the quest exists
     */
    public static boolean contains(String id) {
        return REGISTRY.containsKey(id);
    }

    /**
     * Returns an unmodifiable view of all 12 registered quests, keyed by
     * quest id, in registration order.
     *
     * @return an unmodifiable map from quest id to {@link Quest}
     */
    public static Map<String, Quest> getAll() {
        return REGISTRY;
    }
}
