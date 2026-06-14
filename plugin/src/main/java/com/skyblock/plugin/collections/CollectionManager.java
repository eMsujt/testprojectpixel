package com.skyblock.plugin.collections;

import org.bukkit.Material;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class CollectionManager {

    private static final CollectionManager INSTANCE = new CollectionManager();

    private final Map<UUID, Map<Material, Long>> collections = new HashMap<>();

    private CollectionManager() {}

    public static CollectionManager getInstance() {
        return INSTANCE;
    }

    public void addCollection(UUID playerId, Material material, long amount) {
        collections
                .computeIfAbsent(playerId, k -> new EnumMap<>(Material.class))
                .merge(material, amount, Long::sum);
    }

    public long getCollection(UUID playerId, Material material) {
        Map<Material, Long> counts = collections.get(playerId);
        if (counts == null) return 0L;
        return counts.getOrDefault(material, 0L);
    }

    public Map<Material, Long> getCollections(UUID playerId) {
        return collections.getOrDefault(playerId, new EnumMap<>(Material.class));
    }
}
