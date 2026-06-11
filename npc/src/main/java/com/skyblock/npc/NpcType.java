package com.skyblock.npc;

/**
 * The kinds of NPCs players can interact with on SkyBlock.
 */
public enum NpcType {

    BANKER("Banker"),
    MERCHANT("Merchant"),
    AUCTIONEER("Auctioneer"),
    QUEST_GIVER("Quest Giver"),
    BLACKSMITH("Blacksmith"),
    ALCHEMIST("Alchemist");

    private final String displayName;

    NpcType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
