package com.skyblock.plugin.listener;

import com.skyblock.plugin.collections.CollectionsManager;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import java.util.EnumSet;
import java.util.Set;

/**
 * Records mining collection progress through {@link CollectionsManager} when a
 * player breaks an ore via {@link BlockBreakEvent}.
 */
public final class MiningListener implements Listener {

    /** Ores that contribute to a player's mining collections. */
    private static final Set<Material> ORES = EnumSet.of(
            Material.COAL_ORE,
            Material.IRON_ORE,
            Material.GOLD_ORE,
            Material.DIAMOND_ORE,
            Material.EMERALD_ORE,
            Material.LAPIS_ORE,
            Material.REDSTONE_ORE,
            Material.NETHER_QUARTZ_ORE
    );

    private final CollectionsManager collectionsManager = CollectionsManager.getInstance();

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Material type = event.getBlock().getType();
        if (!ORES.contains(type)) {
            return;
        }
        collectionsManager.trackCollection(event.getPlayer(), type, 1);
    }
}
