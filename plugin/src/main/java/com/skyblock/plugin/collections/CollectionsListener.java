package com.skyblock.plugin.collections;

import com.skyblock.plugin.managers.CollectionsManager;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import java.util.Map;
import java.util.UUID;

/**
 * @deprecated Use {@link com.skyblock.plugin.collection.CollectionListener} instead.
 */
@Deprecated
public final class CollectionsListener implements Listener {

    private static final Map<Material, String> BLOCK_COLLECTION = Map.ofEntries(
            // Farming
            Map.entry(Material.WHEAT,              "wheat"),
            Map.entry(Material.CARROTS,            "carrot"),
            Map.entry(Material.POTATOES,           "potato"),
            Map.entry(Material.PUMPKIN,            "pumpkin"),
            Map.entry(Material.MELON,              "melon"),
            Map.entry(Material.SUGAR_CANE,         "sugar_cane"),
            Map.entry(Material.COCOA,              "cocoa_beans"),
            Map.entry(Material.CACTUS,             "cactus"),
            Map.entry(Material.BROWN_MUSHROOM,     "brown_mushroom"),
            Map.entry(Material.RED_MUSHROOM,       "red_mushroom"),
            Map.entry(Material.NETHER_WART,        "nether_wart"),
            // Mining
            Map.entry(Material.COBBLESTONE,        "cobblestone"),
            Map.entry(Material.COAL_ORE,           "coal"),
            Map.entry(Material.IRON_ORE,           "iron_ingot"),
            Map.entry(Material.GOLD_ORE,           "gold_ingot"),
            Map.entry(Material.DIAMOND_ORE,        "diamond"),
            Map.entry(Material.LAPIS_ORE,          "lapis_lazuli"),
            Map.entry(Material.EMERALD_ORE,        "emerald"),
            Map.entry(Material.REDSTONE_ORE,       "redstone"),
            Map.entry(Material.NETHER_QUARTZ_ORE,  "quartz"),
            Map.entry(Material.OBSIDIAN,           "obsidian"),
            // Foraging
            Map.entry(Material.OAK_LOG,            "oak_wood"),
            Map.entry(Material.SPRUCE_LOG,         "spruce_wood"),
            Map.entry(Material.BIRCH_LOG,          "birch_wood"),
            Map.entry(Material.JUNGLE_LOG,         "jungle_wood"),
            Map.entry(Material.ACACIA_LOG,         "acacia_wood"),
            Map.entry(Material.DARK_OAK_LOG,       "dark_oak_wood")
    );

    // Cumulative counts required to reach each collection tier (Hypixel-style curve).
    private static final long[] DEFAULT_TIERS =
            {50, 100, 250, 500, 1000, 2500, 5000, 10000, 25000, 50000, 100000};

    private static final Map<String, long[]> TIER_REQUIREMENTS;
    static {
        Map<String, long[]> tiers = new java.util.HashMap<>();
        for (String collection : BLOCK_COLLECTION.values()) {
            tiers.put(collection, DEFAULT_TIERS);
        }
        TIER_REQUIREMENTS = Map.copyOf(tiers);
    }

    private final CollectionsManager collectionsManager = CollectionsManager.getInstance();

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        String collection = BLOCK_COLLECTION.get(event.getBlock().getType());
        if (collection == null) {
            return;
        }
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();
        collectionsManager.addCollectionCount(uuid, collection, 1L);
        checkTierUnlock(player, uuid, collection);
    }

    private void checkTierUnlock(Player player, UUID uuid, String collection) {
        long[] thresholds = TIER_REQUIREMENTS.get(collection);
        if (thresholds == null) {
            return;
        }
        long total = collectionsManager.getCollectionCount(uuid, collection);
        int newTier = tierFor(total, thresholds);
        int currentTier = collectionsManager.getCollectionMilestone(uuid, collection);
        if (newTier > currentTier) {
            collectionsManager.setCollectionMilestone(uuid, collection, newTier);
            collectionsManager.recordUnlock(uuid, collection + " tier " + newTier);
            player.sendMessage("§a§lCOLLECTION UNLOCKED §7" + collection + " §eTier " + newTier);
        }
    }

    private static int tierFor(long count, long[] thresholds) {
        int tier = 0;
        for (long threshold : thresholds) {
            if (count >= threshold) {
                tier++;
            } else {
                break;
            }
        }
        return tier;
    }
}
