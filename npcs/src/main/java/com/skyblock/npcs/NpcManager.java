package com.skyblock.npcs;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Registry of the NPCs available in SkyBlock.
 *
 * <p>NPCs are identified by a case-insensitive id. Each NPC carries a
 * display name and a role describing what it does (for example
 * {@code "merchant"} or {@code "banker"}). Not thread-safe; synchronize
 * externally if accessed from multiple threads.</p>
 */
public final class NpcManager {

    private final Map<String, Npc> npcRegistry = new HashMap<>();

    /**
     * Registers a new NPC.
     *
     * @param id          the unique NPC id, must not be null or blank
     * @param displayName the name shown to players, must not be null or blank
     * @param role        the NPC's role, must not be null or blank
     * @return the registered NPC
     * @throws IllegalArgumentException if an argument is invalid or the id is already registered
     */
    public Npc registerNpc(String id, String displayName, String role) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("id must not be null or blank");
        }
        if (displayName == null || displayName.isBlank()) {
            throw new IllegalArgumentException("displayName must not be null or blank");
        }
        if (role == null || role.isBlank()) {
            throw new IllegalArgumentException("role must not be null or blank");
        }
        String key = normalize(id);
        if (npcRegistry.containsKey(key)) {
            throw new IllegalArgumentException("npc already registered: " + id);
        }
        Npc npc = new Npc(key, displayName, normalize(role));
        npcRegistry.put(key, npc);
        return npc;
    }

    /**
     * Removes an NPC from the registry.
     *
     * @param id the NPC id
     * @return {@code true} if the NPC was registered and has been removed
     */
    public boolean unregisterNpc(String id) {
        if (id == null) {
            return false;
        }
        return npcRegistry.remove(normalize(id)) != null;
    }

    /**
     * Looks up an NPC by id.
     *
     * @param id the NPC id
     * @return the NPC, or {@code null} if not registered
     */
    public Npc getNpc(String id) {
        if (id == null) {
            return null;
        }
        return npcRegistry.get(normalize(id));
    }

    /**
     * Returns whether an NPC is registered.
     *
     * @param id the NPC id
     * @return {@code true} if the NPC is registered
     */
    public boolean isRegistered(String id) {
        return getNpc(id) != null;
    }

    /**
     * Returns the registered NPCs with a given role.
     *
     * @param role the role to match, case-insensitive
     * @return an unmodifiable list of the matching NPCs, empty if none
     */
    public List<Npc> getNpcsByRole(String role) {
        if (role == null) {
            return List.of();
        }
        String key = normalize(role);
        return npcRegistry.values().stream()
                .filter(npc -> npc.getRole().equals(key))
                .collect(Collectors.collectingAndThen(Collectors.toList(), Collections::unmodifiableList));
    }

    /**
     * Returns all registered NPCs keyed by id.
     *
     * @return an unmodifiable view of the registry
     */
    public Map<String, Npc> getNpcs() {
        return Collections.unmodifiableMap(npcRegistry);
    }

    /**
     * Returns the number of registered NPCs.
     *
     * @return the registered NPC count
     */
    public int getNpcCount() {
        return npcRegistry.size();
    }

    /**
     * Removes all NPCs from the registry.
     */
    public void clear() {
        npcRegistry.clear();
    }

    private static String normalize(String value) {
        return value.toLowerCase(Locale.ROOT);
    }

    /**
     * A registered NPC. Instances are created through
     * {@link NpcManager#registerNpc(String, String, String)}.
     */
    public static final class Npc {

        private final String id;
        private final String displayName;
        private final String role;

        private Npc(String id, String displayName, String role) {
            this.id = id;
            this.displayName = displayName;
            this.role = role;
        }

        /** @return the unique NPC id, normalized to lower case */
        public String getId() {
            return id;
        }

        /** @return the name shown to players */
        public String getDisplayName() {
            return displayName;
        }

        /** @return the NPC's role, normalized to lower case */
        public String getRole() {
            return role;
        }
    }
}
