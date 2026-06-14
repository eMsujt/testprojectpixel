package com.skyblock.plugin.collection;

import com.skyblock.plugin.collections.CollectionManager;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import java.util.Map;

public final class CollectionListener implements Listener {

    private static final Map<Material, Material> BLOCK_TO_COLLECTION = Map.ofEntries(
            // Mining ores → their collection material
            Map.entry(Material.COAL_ORE,          Material.COAL),
            Map.entry(Material.IRON_ORE,          Material.IRON_INGOT),
            Map.entry(Material.GOLD_ORE,          Material.GOLD_INGOT),
            Map.entry(Material.DIAMOND_ORE,       Material.DIAMOND),
            // Stone/cobblestone
            Map.entry(Material.STONE,             Material.COBBLESTONE),
            Map.entry(Material.COBBLESTONE,       Material.COBBLESTONE),
            // Farming crops
            Map.entry(Material.WHEAT,             Material.WHEAT),
            Map.entry(Material.CARROTS,           Material.WHEAT),
            Map.entry(Material.POTATOES,          Material.WHEAT),
            // Foraging logs
            Map.entry(Material.OAK_LOG,           Material.OAK_LOG),
            Map.entry(Material.BIRCH_LOG,         Material.OAK_LOG),
            Map.entry(Material.SPRUCE_LOG,        Material.OAK_LOG),
            Map.entry(Material.JUNGLE_LOG,        Material.OAK_LOG),
            Map.entry(Material.ACACIA_LOG,        Material.OAK_LOG),
            Map.entry(Material.DARK_OAK_LOG,      Material.OAK_LOG)
    );

    private final CollectionManager collectionManager = CollectionManager.getInstance();

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Material blockType = event.getBlock().getType();
        Material collection = BLOCK_TO_COLLECTION.get(blockType);
        if (collection == null) {
            return;
        }
        Player player = event.getPlayer();
        int unlocked = collectionManager.addCollection(player.getUniqueId(), collection, 1L);
        if (unlocked > 0) {
            int tier = collectionManager.getTier(player.getUniqueId(), collection);
            player.sendMessage("§a§lCOLLECTION UNLOCKED §7"
                    + collection.name().toLowerCase() + " §eTier " + tier);
        }
    }
}
