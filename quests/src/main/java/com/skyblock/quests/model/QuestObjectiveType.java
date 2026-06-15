package com.skyblock.quests.model;

/**
 * The kinds of objectives a SkyBlock quest can ask a player to complete.
 *
 * <p>Each type carries its human-readable display name and whether progress
 * towards it accumulates over multiple events (e.g. killing several mobs)
 * or completes in a single moment (e.g. talking to an NPC).</p>
 */
public enum QuestObjectiveType {

    KILL_MOB("Kill Mobs", true),
    COLLECT_ITEM("Collect Items", true),
    REACH_SKILL_LEVEL("Reach Skill Level", true),
    VISIT_LOCATION("Visit Location", false),
    TALK_TO_NPC("Talk to NPC", false);

    private final String displayName;
    private final boolean incremental;

    QuestObjectiveType(String displayName, boolean incremental) {
        this.displayName = displayName;
        this.incremental = incremental;
    }

    /**
     * Returns the human-readable name shown in quest logs and menus.
     *
     * @return the display name, e.g. {@code "Kill Mobs"}
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Returns whether progress towards this objective accumulates over
     * multiple events rather than completing in a single moment.
     *
     * @return {@code true} if the objective tracks a running count,
     *         {@code false} if it completes instantly
     */
    public boolean isIncremental() {
        return incremental;
    }
}
