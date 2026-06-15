package com.skyblock.quests;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * @deprecated Use {@link com.skyblock.core.manager.QuestManager} instead.
 */
@Deprecated
public final class QuestManager {

    /**
     * The state of a quest a player is currently working on: its
     * {@link QuestObjectiveType}, the progress target, and the progress
     * made so far.
     */
    public static final class ActiveQuest {

        private final QuestObjectiveType objectiveType;
        private final int target;
        private int progress;

        private ActiveQuest(QuestObjectiveType objectiveType, int target) {
            this.objectiveType = objectiveType;
            this.target = target;
        }

        /**
         * Returns the kind of objective this quest asks the player to complete.
         *
         * @return the objective type, never {@code null}
         */
        public QuestObjectiveType getObjectiveType() {
            return objectiveType;
        }

        /**
         * Returns the amount of progress required to complete the quest.
         *
         * @return the target, always at least one
         */
        public int getTarget() {
            return target;
        }

        /**
         * Returns the progress the player has made towards the target.
         *
         * @return the current progress, never above the target
         */
        public int getProgress() {
            return progress;
        }
    }

    private final Map<UUID, Map<String, ActiveQuest>> activeQuests = new HashMap<>();
    private final Map<UUID, Set<String>> completedQuests = new HashMap<>();

    /**
     * Starts a quest for the player, tracking progress towards the given
     * objective. Non-{@linkplain QuestObjectiveType#isIncremental incremental}
     * objectives must use a target of one.
     *
     * @param playerId      the player's UUID
     * @param questId       the quest's unique identifier
     * @param objectiveType the kind of objective the quest asks for
     * @param target        the progress required to complete the quest, at least one
     * @throws IllegalArgumentException if {@code target} is less than one, if a
     *         non-incremental objective has a target above one, or if the quest
     *         is already active or already completed for the player
     */
    public void startQuest(UUID playerId, String questId, QuestObjectiveType objectiveType, int target) {
        if (target < 1) {
            throw new IllegalArgumentException("target must be at least one: " + target);
        }
        if (!objectiveType.isIncremental() && target != 1) {
            throw new IllegalArgumentException(
                    "non-incremental objective " + objectiveType + " requires a target of one: " + target);
        }
        if (isCompleted(playerId, questId)) {
            throw new IllegalArgumentException("quest already completed: " + questId);
        }
        Map<String, ActiveQuest> quests = activeQuests.computeIfAbsent(playerId, id -> new LinkedHashMap<>());
        if (quests.containsKey(questId)) {
            throw new IllegalArgumentException("quest already active: " + questId);
        }
        quests.put(questId, new ActiveQuest(objectiveType, target));
    }

    /**
     * Records progress towards one of the player's active quests. When the
     * quest's target is reached it is removed from the active quests and
     * recorded as completed.
     *
     * @param playerId the player's UUID
     * @param questId  the quest's unique identifier
     * @param amount   the progress to add, must be positive
     * @return the quest's progress after this update, capped at the target
     * @throws IllegalArgumentException if {@code amount} is not positive or the
     *         quest is not active for the player
     */
    public int recordProgress(UUID playerId, String questId, int amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("amount must be positive: " + amount);
        }
        Map<String, ActiveQuest> quests = activeQuests.getOrDefault(playerId, Collections.emptyMap());
        ActiveQuest quest = quests.get(questId);
        if (quest == null) {
            throw new IllegalArgumentException("quest is not active: " + questId);
        }
        quest.progress = Math.min(quest.target, quest.progress + amount);
        if (quest.progress >= quest.target) {
            quests.remove(questId);
            completedQuests.computeIfAbsent(playerId, id -> new HashSet<>()).add(questId);
        }
        return quest.progress;
    }

    /**
     * Returns the state of one of the player's active quests.
     *
     * @param playerId the player's UUID
     * @param questId  the quest's unique identifier
     * @return the active quest, or {@code null} if it is not active
     */
    public ActiveQuest getActiveQuest(UUID playerId, String questId) {
        return activeQuests.getOrDefault(playerId, Collections.emptyMap()).get(questId);
    }

    /**
     * Returns the identifiers of the quests the player is currently working
     * on, in the order they were started.
     *
     * @param playerId the player's UUID
     * @return an unmodifiable view of the active quest ids, empty if none
     */
    public Set<String> getActiveQuestIds(UUID playerId) {
        return Collections.unmodifiableSet(
                activeQuests.getOrDefault(playerId, Collections.emptyMap()).keySet());
    }

    /**
     * Returns whether the player has completed the given quest.
     *
     * @param playerId the player's UUID
     * @param questId  the quest's unique identifier
     * @return {@code true} if the quest has been completed
     */
    public boolean isCompleted(UUID playerId, String questId) {
        return completedQuests.getOrDefault(playerId, Collections.emptySet()).contains(questId);
    }

    /**
     * Returns the identifiers of the quests the player has completed.
     *
     * @param playerId the player's UUID
     * @return an unmodifiable view of the completed quest ids, empty if none
     */
    public Set<String> getCompletedQuestIds(UUID playerId) {
        return Collections.unmodifiableSet(
                completedQuests.getOrDefault(playerId, Collections.emptySet()));
    }

    /**
     * Resets the player's quest progression, abandoning all active quests and
     * forgetting all completed ones.
     *
     * @param playerId the player's UUID
     */
    public void reset(UUID playerId) {
        activeQuests.remove(playerId);
        completedQuests.remove(playerId);
    }
}
