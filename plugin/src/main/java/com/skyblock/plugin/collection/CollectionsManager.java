package com.skyblock.plugin.collection;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Singleton tracking how much of each collection material a player has gathered.
 *
 * <p>Registered in {@link com.skyblock.plugin.SkyBlockPlugin#onEnable()} and
 * exposes a {@link Player}-oriented {@link #trackCollection(Player, Material, int)}
 * entry point for gameplay listeners to record collection progress.</p>
 */
public final class CollectionsManager {

    private static final CollectionsManager INSTANCE = new CollectionsManager();

    private final Map<UUID, Map<Material, Long>> collections = new HashMap<>();

    private JavaPlugin plugin;

    private CollectionsManager() {}

    public static CollectionsManager getInstance() {
        return INSTANCE;
    }

    /** Wires the manager to its owning plugin. */
    public void register(JavaPlugin owningPlugin) {
        this.plugin = owningPlugin;
    }

    /**
     * Records that the player gathered {@code amount} of {@code material},
     * adding it to their running collection total. Non-positive amounts are
     * ignored.
     */
    public void trackCollection(Player player, Material material, int amount) {
        if (player == null || material == null || amount <= 0) return;
        Map<Material, Long> counts = collections.computeIfAbsent(
                player.getUniqueId(), k -> new EnumMap<>(Material.class));
        counts.merge(material, (long) amount, Long::sum);
    }

    /** Total amount the player has collected of the given material. */
    public long getCollection(UUID playerId, Material material) {
        Map<Material, Long> counts = collections.get(playerId);
        if (counts == null) return 0L;
        return counts.getOrDefault(material, 0L);
    }

    /** Read-only snapshot of every collection total for the player. */
    public Map<Material, Long> getCollections(UUID playerId) {
        return Collections.unmodifiableMap(
                collections.getOrDefault(playerId, new EnumMap<>(Material.class)));
    }
}
