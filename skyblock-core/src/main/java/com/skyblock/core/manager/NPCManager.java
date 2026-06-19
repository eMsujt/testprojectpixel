package com.skyblock.core.manager;

import org.bukkit.Material;

import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * Singleton manager for SkyBlock hub NPCs.
 *
 * <p>Tracks per-player interaction counts and provides the canonical NPC registry.</p>
 */
public final class NPCManager {

    /** All interactable NPCs in the SkyBlock hub. */
    public enum NpcType {
        BANKER("Arthur",       Material.GOLD_INGOT,    "§6Banker"),
        BAZAAR_AGENT("Barry",  Material.EMERALD,       "§aBazaar Agent"),
        CARPENTER("Carpenter", Material.OAK_PLANKS,    "§7Carpenter"),
        AUCTION_MASTER("Simon",Material.PAPER,         "§eAuction Master"),
        ADVENTURER("Adventurer",Material.MAP,          "§9Adventurer"),
        BUILDER("Bob",         Material.BRICKS,        "§7Builder"),
        LIBRARIAN("Librarian", Material.BOOKSHELF,     "§dLibrarian"),
        BLACKSMITH("Blacksmith",Material.IRON_INGOT,   "§8Blacksmith"),
        FARMER("Farmer John",  Material.WHEAT,         "§aFarmer"),
        FISHING_MERCHANT("Odger",Material.COD,         "§3Fishing Merchant");

        /** Display name shown to players. */
        public final String displayName;
        /** Material used to represent the NPC's head/icon. */
        public final Material icon;
        /** Formatted chat prefix for this NPC. */
        public final String prefix;

        NpcType(String displayName, Material icon, String prefix) {
            this.displayName = displayName;
            this.icon = icon;
            this.prefix = prefix;
        }
    }

    private static final NPCManager INSTANCE = new NPCManager();

    /** Per-player interaction counts per NPC type. */
    private final Map<UUID, EnumMap<NpcType, Integer>> interactionCounts = new java.util.HashMap<>();

    private NPCManager() {
    }

    public static NPCManager getInstance() {
        return INSTANCE;
    }

    /**
     * Records that a player interacted with an NPC and returns the new interaction count.
     *
     * @param playerId the interacting player
     * @param npc      the NPC being interacted with
     * @return the updated interaction count
     */
    public int recordInteraction(UUID playerId, NpcType npc) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(npc, "npc");
        return interactionCounts
                .computeIfAbsent(playerId, k -> new EnumMap<>(NpcType.class))
                .merge(npc, 1, Integer::sum);
    }

    /**
     * Returns the number of times a player has interacted with a specific NPC.
     *
     * @param playerId the player to look up
     * @param npc      the NPC to query
     * @return interaction count, {@code 0} if none recorded
     */
    public int getInteractionCount(UUID playerId, NpcType npc) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(npc, "npc");
        EnumMap<NpcType, Integer> counts = interactionCounts.get(playerId);
        return counts == null ? 0 : counts.getOrDefault(npc, 0);
    }

    /**
     * Returns an unmodifiable view of all NPC interaction counts for a player.
     *
     * @param playerId the player to look up
     * @return unmodifiable map of NPC type to count; empty if none recorded
     */
    public Map<NpcType, Integer> getAllInteractions(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        EnumMap<NpcType, Integer> counts = interactionCounts.get(playerId);
        return counts == null ? Collections.emptyMap() : Collections.unmodifiableMap(counts);
    }

    /** Clears all interaction data for a player. */
    public void resetPlayer(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        interactionCounts.remove(playerId);
    }
}
