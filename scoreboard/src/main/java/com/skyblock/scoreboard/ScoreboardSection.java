package com.skyblock.scoreboard;

/**
 * The sections rendered on the SkyBlock sidebar scoreboard, in display order.
 */
public enum ScoreboardSection {

    HEADER("Header", 0),
    DATE_TIME("Date & Time", 1),
    LOCATION("Location", 2),
    PURSE("Purse", 3),
    BITS("Bits", 4),
    OBJECTIVE("Objective", 5),
    EVENT("Event", 6),
    SLAYER_QUEST("Slayer Quest", 7),
    FOOTER("Footer", 8);

    private final String displayName;
    private final int displayOrder;

    ScoreboardSection(String displayName, int displayOrder) {
        this.displayName = displayName;
        this.displayOrder = displayOrder;
    }

    public String getDisplayName() {
        return displayName;
    }

    public int getDisplayOrder() {
        return displayOrder;
    }
}
