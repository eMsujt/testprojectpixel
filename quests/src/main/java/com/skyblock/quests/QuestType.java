package com.skyblock.quests;

/**
 * The categories a SkyBlock quest can belong to.
 *
 * <p>Each type carries its human-readable display name and whether quests of
 * that type reset and can be completed again (e.g. daily and weekly quests)
 * or are one-time only (e.g. story quests).</p>
 */
public enum QuestType {

    DAILY("Daily", true),
    WEEKLY("Weekly", true),
    STORY("Story", false),
    SIDE("Side", false),
    EVENT("Event", true);

    private final String displayName;
    private final boolean repeatable;

    QuestType(String displayName, boolean repeatable) {
        this.displayName = displayName;
        this.repeatable = repeatable;
    }

    /**
     * Returns the human-readable name shown in quest logs and menus.
     *
     * @return the display name, e.g. {@code "Daily"}
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Returns whether quests of this type reset and can be completed again.
     *
     * @return {@code true} if the quest type is repeatable,
     *         {@code false} if it can only ever be completed once
     */
    public boolean isRepeatable() {
        return repeatable;
    }
}
