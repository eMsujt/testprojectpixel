package com.skyblock.events;

/**
 * The recurring calendar events that take place across SkyBlock.
 */
public enum SkyBlockEvent {

    SPOOKY_FESTIVAL("Spooky Festival", 3, true),
    TRAVELING_ZOO("Traveling Zoo", 3, false),
    NEW_YEAR_CELEBRATION("New Year Celebration", 3, false),
    SEASON_OF_JERRY("Season of Jerry", 24, false),
    JERRYS_WORKSHOP("Jerry's Workshop", 31, false),
    DARK_AUCTION("Dark Auction", 1, false),
    JACOBS_FARMING_CONTEST("Jacob's Farming Contest", 1, true),
    MINING_FIESTA("Mining Fiesta", 5, true),
    FISHING_FESTIVAL("Fishing Festival", 1, true),
    MYTHOLOGICAL_RITUAL("Mythological Ritual", 31, true),
    CULT_OF_THE_FALLEN_STAR("Cult of the Fallen Star", 1, false),
    BANK_INTEREST("Bank Interest", 1, false),
    ELECTION_BOOTH_OPENS("Election Booth Opens", 1, false),
    ELECTION_OVER("Election Over", 1, false),
    FEAR_MONGERER("Fear Mongerer", 8, false),
    HOPPITYS_HUNT("Hoppity's Hunt", 31, true);

    private final String displayName;
    private final int durationDays;
    private final boolean grantsBonusRewards;

    SkyBlockEvent(String displayName, int durationDays, boolean grantsBonusRewards) {
        this.displayName = displayName;
        this.durationDays = durationDays;
        this.grantsBonusRewards = grantsBonusRewards;
    }

    public String getDisplayName() {
        return displayName;
    }

    public int getDurationDays() {
        return durationDays;
    }

    public boolean grantsBonusRewards() {
        return grantsBonusRewards;
    }
}
