package com.skyblock.core.npc;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * Singleton managing registered NPCs and player interactions with them.
 *
 * <p>NPCs are keyed by a {@link UUID} assigned at registration time.
 * Not thread-safe; synchronize externally if accessed from multiple threads.</p>
 */
public final class NPCManager {

    /** All NPC types available in SkyBlock. */
    public enum NPCType {
        BANKER, MERCHANT, QUEST_GIVER, SKILL_MASTER,
        AUCTION_MASTER, BAZAAR_TRADER, DUNGEON_GUIDE,
        WEAPONSMITH, BLACKSMITH, ENCHANTER, PET_KEEPER,
        FARMER, FISHERMAN, MINER
    }

    /** Immutable description of a registered NPC. */
    public static final class NPCData {
        public final UUID id;
        public final NPCType type;
        public final String name;
        /** World name where the NPC is located. */
        public final String world;
        public final double x;
        public final double y;
        public final double z;
        /** Default dialogue line shown to players on first interaction. */
        public final String dialogue;

        public NPCData(UUID id, NPCType type, String name,
                       String world, double x, double y, double z,
                       String dialogue) {
            this.id      = Objects.requireNonNull(id,      "id");
            this.type    = Objects.requireNonNull(type,    "type");
            this.name    = Objects.requireNonNull(name,    "name");
            this.world   = Objects.requireNonNull(world,   "world");
            this.x       = x;
            this.y       = y;
            this.z       = z;
            this.dialogue = Objects.requireNonNull(dialogue, "dialogue");
        }
    }

    private static final NPCManager INSTANCE = new NPCManager();

    /** All registered NPCs keyed by their ID. */
    private final Map<UUID, NPCData> npcs = new HashMap<>();

    /** Tracks the NPC a player is currently interacting with, if any. */
    private final Map<UUID, UUID> activeInteractions = new HashMap<>();

    private NPCManager() {
    }

    /**
     * Returns the single shared {@code NPCManager} instance.
     *
     * @return the singleton instance
     */
    public static NPCManager getInstance() {
        return INSTANCE;
    }

    /**
     * Registers a new NPC and returns its generated ID.
     *
     * @param type     the NPC type
     * @param name     display name shown above the NPC
     * @param world    the world the NPC is placed in
     * @param x        X coordinate
     * @param y        Y coordinate
     * @param z        Z coordinate
     * @param dialogue default dialogue line for player interactions
     * @return the UUID assigned to this NPC
     */
    public UUID register(NPCType type, String name,
                         String world, double x, double y, double z,
                         String dialogue) {
        Objects.requireNonNull(type,     "type");
        Objects.requireNonNull(name,     "name");
        Objects.requireNonNull(world,    "world");
        Objects.requireNonNull(dialogue, "dialogue");
        UUID id = UUID.randomUUID();
        npcs.put(id, new NPCData(id, type, name, world, x, y, z, dialogue));
        return id;
    }

    /**
     * Returns the {@link NPCData} for the given NPC ID, or {@code null} if not found.
     *
     * @param npcId the NPC identifier
     * @return the NPC data, or {@code null}
     */
    public NPCData getNPC(UUID npcId) {
        Objects.requireNonNull(npcId, "npcId");
        return npcs.get(npcId);
    }

    /**
     * Returns an unmodifiable view of all registered NPCs.
     *
     * @return all registered {@link NPCData} instances
     */
    public Collection<NPCData> getAllNPCs() {
        return Collections.unmodifiableCollection(npcs.values());
    }

    /**
     * Records that a player has started interacting with an NPC.
     *
     * @param playerId the interacting player
     * @param npcId    the NPC being interacted with
     * @throws IllegalArgumentException if the NPC is not registered
     */
    public void startInteraction(UUID playerId, UUID npcId) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(npcId,    "npcId");
        if (!npcs.containsKey(npcId)) {
            throw new IllegalArgumentException("No NPC registered with id " + npcId);
        }
        activeInteractions.put(playerId, npcId);
    }

    /**
     * Ends an active player–NPC interaction.
     *
     * @param playerId the player ending the interaction
     * @return {@code true} if the player had an active interaction that was removed
     */
    public boolean endInteraction(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        return activeInteractions.remove(playerId) != null;
    }

    /**
     * Returns the NPC the given player is currently interacting with, or {@code null}.
     *
     * @param playerId the player to query
     * @return the {@link NPCData} for the active interaction, or {@code null}
     */
    public NPCData getActiveInteraction(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        UUID npcId = activeInteractions.get(playerId);
        return npcId == null ? null : npcs.get(npcId);
    }

    /**
     * Removes a registered NPC and ends any player interaction with it.
     *
     * @param npcId the NPC to remove
     * @return {@code true} if an NPC with that ID was registered
     */
    public boolean unregister(UUID npcId) {
        Objects.requireNonNull(npcId, "npcId");
        boolean removed = npcs.remove(npcId) != null;
        activeInteractions.values().removeIf(npcId::equals);
        return removed;
    }
}
