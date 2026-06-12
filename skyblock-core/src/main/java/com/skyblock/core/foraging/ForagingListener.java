package com.skyblock.core.foraging;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import java.util.EnumMap;
import java.util.Map;

/**
 * Bukkit listener that intercepts {@link BlockBreakEvent} for wood logs
 * and routes chops to {@link ForagingManager}.
 */
public final class ForagingListener implements Listener {

    private static final Map<Material, ForagingManager.TreeType> MATERIAL_TO_TREE;

    static {
        Map<Material, ForagingManager.TreeType> map = new EnumMap<>(Material.class);
        for (ForagingManager.TreeType tree : ForagingManager.TreeType.values()) {
            map.put(tree.getMaterial(), tree);
        }
        MATERIAL_TO_TREE = Map.copyOf(map);
    }

    private final ForagingManager foragingManager;

    public ForagingListener(ForagingManager foragingManager) {
        this.foragingManager = foragingManager;
    }

    /**
     * Intercepts log block-break events and records a chop in
     * {@link ForagingManager}, awarding skill XP to the breaking player.
     *
     * @param event the block-break event
     */
    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        ForagingManager.TreeType tree = MATERIAL_TO_TREE.get(event.getBlock().getType());
        if (tree == null) {
            return;
        }
        Player player = event.getPlayer();
        foragingManager.recordChop(player.getUniqueId(), tree, 1);
    }
}
