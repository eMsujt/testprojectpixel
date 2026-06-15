package com.skyblock.plugin.collection;

import org.bukkit.Material;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.EnumMap;
import java.util.Map;
import java.util.UUID;

/**
 * @deprecated Use {@link com.skyblock.core.manager.CollectionManager} instead.
 */
@Deprecated
public final class CollectionManager {

    private static final CollectionManager INSTANCE = new CollectionManager();
    private final com.skyblock.core.manager.CollectionManager delegate =
            com.skyblock.core.manager.CollectionManager.getInstance();

    private CollectionManager() {}

    public static CollectionManager getInstance() {
        return INSTANCE;
    }

    /** No-op — event-based persistence superseded by canonical load/save. */
    public void register(JavaPlugin owningPlugin) {}

    public int addCollection(UUID playerId, Material material, long amount) {
        int tierBefore = getTier(playerId, material);
        delegate.addItems(playerId, material.name(), amount);
        return getTier(playerId, material) - tierBefore;
    }

    public int addCollection(UUID playerId, String itemId, long amount) {
        Material material = Material.matchMaterial(itemId);
        if (material == null) return 0;
        return addCollection(playerId, material, amount);
    }

    public int increment(UUID playerId, Material material, long amount) {
        return addCollection(playerId, material, amount);
    }

    public int getTier(UUID playerId, Material material) {
        com.skyblock.core.model.Collection c = com.skyblock.core.model.Collection.parse(material.name());
        return c == null ? 0 : delegate.getTier(playerId, c);
    }

    public long getCollection(UUID playerId, Material material) {
        com.skyblock.core.model.Collection c = com.skyblock.core.model.Collection.parse(material.name());
        return c == null ? 0L : delegate.getItems(playerId, c);
    }

    public Map<Material, Long> getCollections(UUID playerId) {
        Map<Material, Long> result = new EnumMap<>(Material.class);
        for (Map.Entry<com.skyblock.core.model.Collection, Long> e : delegate.getAll(playerId).entrySet()) {
            Material mat = Material.matchMaterial(e.getKey().itemKey);
            if (mat != null) result.put(mat, e.getValue());
        }
        return result;
    }

    public int[] getThresholds(Material material) {
        com.skyblock.core.model.Collection c = com.skyblock.core.model.Collection.parse(material.name());
        if (c == null) return new int[]{50, 100, 250, 500, 1000, 2500, 5000, 10000, 25000, 50000};
        int[] thresholds = com.skyblock.core.manager.CollectionManager.TIER_DATA.get(c);
        return thresholds == null ? new int[]{50, 100, 250, 500, 1000, 2500, 5000, 10000, 25000, 50000}
                : thresholds.clone();
    }
}
