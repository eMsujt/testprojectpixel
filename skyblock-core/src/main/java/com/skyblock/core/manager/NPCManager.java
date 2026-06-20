package com.skyblock.core.manager;

import com.skyblock.core.npc.NpcManager;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Singleton that tracks spawned NPCs as {@link NPCData} records (name, location, type).
 *
 * <p>Delegates shop/definition lookups to {@link NpcManager}.</p>
 */
public final class NPCManager {

    /**
     * Immutable snapshot of a spawned NPC.
     *
     * @param name display name of the NPC
     * @param loc  world location where the NPC is placed
     * @param type role type string (e.g. "MERCHANT", "BANKER")
     */
    public record NPCData(String name, Location loc, String type) {
        public NPCData {
            Objects.requireNonNull(name, "name");
            Objects.requireNonNull(loc, "loc");
            Objects.requireNonNull(type, "type");
        }
    }

    private static final NPCManager INSTANCE = new NPCManager();

    private final List<NPCData> npcs = new ArrayList<>();

    private NPCManager() {}

    public static NPCManager getInstance() { return INSTANCE; }

    /** Registers a new NPC at the given location. */
    public void addNPC(String name, Location loc, String type) {
        npcs.add(new NPCData(name, loc, type));
    }

    /** Registers a pre-built {@link NPCData}. */
    public void addNPC(NPCData data) {
        Objects.requireNonNull(data, "data");
        npcs.add(data);
    }

    /** Returns an unmodifiable view of all registered NPC data. */
    public List<NPCData> getNPCs() {
        return Collections.unmodifiableList(npcs);
    }

    /** Returns the underlying {@link NpcManager} for definition/shop lookups. */
    public NpcManager getNpcManager() {
        return NpcManager.getInstance();
    }
}
