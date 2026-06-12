package com.skyblock.npc;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Manages the NPCs available on SkyBlock.
 *
 * <p>NPCs are identified by a unique string id and have a {@link NpcType}
 * and a display name. NPCs must be registered before they can be looked up.
 * Not thread-safe; synchronize externally if accessed from multiple
 * threads.</p>
 */
public final class NpcManager {

    /**
     * A registered NPC.
     */
    public static final class Npc {

        private final String id;
        private final NpcType type;
        private final String displayName;

        Npc(String id, NpcType type, String displayName) {
            this.id = id;
            this.type = type;
            this.displayName = displayName;
        }

        public String getId() {
            return id;
        }

        public NpcType getType() {
            return type;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    private final Map<String, Npc> npcsById = new LinkedHashMap<>();

    /**
     * Registers an NPC so it can be looked up by id or type.
     *
     * @param id          the unique NPC id, must not be null or blank
     * @param type        the NPC type, must not be null
     * @param displayName the display name, must not be null or blank
     * @return the registered NPC
     * @throws IllegalArgumentException if an argument is null or blank, or an
     *                                  NPC with that id is already registered
     */
    public Npc registerNpc(String id, NpcType type, String displayName) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("id must not be null or blank");
        }
        if (type == null) {
            throw new IllegalArgumentException("type must not be null");
        }
        if (displayName == null || displayName.isBlank()) {
            throw new IllegalArgumentException("displayName must not be null or blank");
        }
        if (npcsById.containsKey(id)) {
            throw new IllegalArgumentException("npc already registered: " + id);
        }
        Npc npc = new Npc(id, type, displayName);
        npcsById.put(id, npc);
        return npc;
    }

    /**
     * Unregisters an NPC.
     *
     * @param id the NPC id
     * @return {@code true} if the NPC was registered and has been removed
     */
    public boolean unregisterNpc(String id) {
        return npcsById.remove(id) != null;
    }

    /**
     * Returns whether an NPC with the given id is registered.
     *
     * @param id the NPC id
     * @return {@code true} if the NPC is registered
     */
    public boolean isRegistered(String id) {
        return npcsById.containsKey(id);
    }

    /**
     * Looks up an NPC by id.
     *
     * @param id the NPC id
     * @return the NPC, or empty if no NPC with that id is registered
     */
    public Optional<Npc> getNpc(String id) {
        return Optional.ofNullable(npcsById.get(id));
    }

    /**
     * Returns the registered NPCs of a given type, in registration order.
     *
     * @param type the NPC type
     * @return an unmodifiable list of the matching NPCs, empty if none
     */
    public List<Npc> getNpcsByType(NpcType type) {
        return npcsById.values().stream()
                .filter(npc -> npc.getType() == type)
                .collect(Collectors.collectingAndThen(Collectors.toList(), Collections::unmodifiableList));
    }

    /**
     * Returns all registered NPCs in registration order.
     *
     * @return an unmodifiable view of the registered NPCs
     */
    public List<Npc> getNpcs() {
        return List.copyOf(npcsById.values());
    }

    /**
     * Returns the number of registered NPCs.
     *
     * @return the registered NPC count
     */
    public int getNpcCount() {
        return npcsById.size();
    }
}
