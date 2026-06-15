package com.skyblock.plugin.listener;

import com.skyblock.core.manager.CollectionManager;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import java.util.EnumMap;
import java.util.Map;

/**
 * Increments a player's collection counts via {@link CollectionManager} when
 * they break a mining block.
 */
public final class CollectionGatherListener implements Listener {

    private static final Map<Material, String> BLOCK_COLLECTION = new EnumMap<>(Material.class);

    static {
        // Mining
        BLOCK_COLLECTION.put(Material.COBBLESTONE,                "COBBLESTONE");
        BLOCK_COLLECTION.put(Material.STONE,                      "COBBLESTONE");
        BLOCK_COLLECTION.put(Material.COAL_ORE,                   "COAL");
        BLOCK_COLLECTION.put(Material.DEEPSLATE_COAL_ORE,         "COAL");
        BLOCK_COLLECTION.put(Material.IRON_ORE,                   "RAW_IRON");
        BLOCK_COLLECTION.put(Material.DEEPSLATE_IRON_ORE,         "RAW_IRON");
        BLOCK_COLLECTION.put(Material.GOLD_ORE,                   "RAW_GOLD");
        BLOCK_COLLECTION.put(Material.DEEPSLATE_GOLD_ORE,         "RAW_GOLD");
        BLOCK_COLLECTION.put(Material.NETHER_GOLD_ORE,            "RAW_GOLD");
        BLOCK_COLLECTION.put(Material.DIAMOND_ORE,                "DIAMOND");
        BLOCK_COLLECTION.put(Material.DEEPSLATE_DIAMOND_ORE,      "DIAMOND");
        BLOCK_COLLECTION.put(Material.LAPIS_ORE,                  "LAPIS_LAZULI");
        BLOCK_COLLECTION.put(Material.DEEPSLATE_LAPIS_ORE,        "LAPIS_LAZULI");
        BLOCK_COLLECTION.put(Material.EMERALD_ORE,                "EMERALD");
        BLOCK_COLLECTION.put(Material.DEEPSLATE_EMERALD_ORE,      "EMERALD");
        BLOCK_COLLECTION.put(Material.REDSTONE_ORE,               "REDSTONE");
        BLOCK_COLLECTION.put(Material.DEEPSLATE_REDSTONE_ORE,     "REDSTONE");
        BLOCK_COLLECTION.put(Material.NETHER_QUARTZ_ORE,          "QUARTZ");
        BLOCK_COLLECTION.put(Material.OBSIDIAN,                   "OBSIDIAN");
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        String collection = BLOCK_COLLECTION.get(event.getBlock().getType());
        if (collection == null) {
            return;
        }
        Player player = event.getPlayer();
        CollectionManager.getInstance().addItems(player.getUniqueId(), collection, 1);
    }
}
