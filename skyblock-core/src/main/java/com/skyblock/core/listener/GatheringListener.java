package com.skyblock.core.listener;

import com.skyblock.core.foraging.ForagingManager;
import com.skyblock.core.foraging.ForagingManager.TreeType;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import java.util.EnumMap;
import java.util.Map;

public final class GatheringListener implements Listener {

    private static final GatheringListener INSTANCE = new GatheringListener();

    private static final Map<Material, TreeType> TREE_MAP = new EnumMap<>(Material.class);

    static {
        for (TreeType tree : TreeType.values()) {
            TREE_MAP.put(tree.getMaterial(), tree);
        }
    }

    private final ForagingManager foragingManager = ForagingManager.getInstance();

    private GatheringListener() {}

    public static GatheringListener getInstance() {
        return INSTANCE;
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Block block = event.getBlock();
        TreeType tree = TREE_MAP.get(block.getType());
        if (tree == null) {
            return;
        }

        Player player = event.getPlayer();
        foragingManager.recordChop(player.getUniqueId(), tree, 1);
    }

    @EventHandler
    public void onForagingXp(BlockBreakEvent event) {
        Block block = event.getBlock();
        TreeType tree = TREE_MAP.get(block.getType());
        if (tree == null) {
            return;
        }

        Player player = event.getPlayer();
        player.sendMessage("§2[Foraging] §fYou gained §e+" + tree.getBaseXp() + " Foraging XP§f!");
    }
}
