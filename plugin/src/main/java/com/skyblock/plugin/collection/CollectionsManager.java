package com.skyblock.plugin.collection;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;
import java.util.UUID;

/**
 * @deprecated Use {@link com.skyblock.core.manager.CollectionManager} instead.
 */
@Deprecated
public final class CollectionsManager {

    private static final CollectionsManager INSTANCE = new CollectionsManager();
    private final com.skyblock.core.manager.CollectionManager delegate =
            com.skyblock.core.manager.CollectionManager.getInstance();

    private CollectionsManager() {}

    public static CollectionsManager getInstance() {
        return INSTANCE;
    }

    /** No-op — event-based persistence superseded by canonical load/save. */
    public void register(JavaPlugin owningPlugin) {}

    public void trackCollection(Player player, Material material, int amount) {
        if (player == null || material == null || amount <= 0) return;
        delegate.addItems(player.getUniqueId(), material.name(), amount);
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
        return Collections.unmodifiableMap(result);
    }

    public int getTier(UUID playerId, Material material) {
        com.skyblock.core.model.Collection c = com.skyblock.core.model.Collection.parse(material.name());
        return c == null ? 0 : delegate.getTier(playerId, c);
    }
}
