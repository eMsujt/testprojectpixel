package com.skyblock.core.manager;

import org.bukkit.Location;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public final class NPCManager {

    public record NPCData(String name, Location loc, String type) {
        public NPCData {
            Objects.requireNonNull(name, "name");
            Objects.requireNonNull(loc,  "loc");
            Objects.requireNonNull(type, "type");
        }
    }

    private static final NPCManager INSTANCE = new NPCManager();

    private final Map<String, NPCData> npcs = new LinkedHashMap<>();

    private NPCManager() {}

    public static NPCManager getInstance() {
        return INSTANCE;
    }

    public void register(NPCData data) {
        Objects.requireNonNull(data, "data");
        npcs.put(data.name(), data);
    }

    public NPCData get(String name) {
        Objects.requireNonNull(name, "name");
        return npcs.get(name);
    }

    public List<NPCData> getAll() {
        return Collections.unmodifiableList(new ArrayList<>(npcs.values()));
    }
}
