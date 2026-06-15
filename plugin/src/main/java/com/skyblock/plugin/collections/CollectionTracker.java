package com.skyblock.plugin.collections;

import com.skyblock.plugin.profile.ProfileManager;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDeathEvent;

import java.util.EnumMap;
import java.util.Map;

/**
 * @deprecated Use {@link com.skyblock.plugin.collection.CollectionTracker} instead.
 */
@Deprecated
public final class CollectionTracker implements Listener {

    /** Maps a broken block to the collection it contributes to. */
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

    /** Maps a slain mob to the collection it contributes to. */
    private static final Map<EntityType, String> MOB_COLLECTION = new EnumMap<>(EntityType.class);

    static {
        MOB_COLLECTION.put(EntityType.ZOMBIE,   "rotten_flesh");
        MOB_COLLECTION.put(EntityType.SKELETON, "bone");
        MOB_COLLECTION.put(EntityType.SPIDER,   "string");
        MOB_COLLECTION.put(EntityType.CREEPER,  "gunpowder");
        MOB_COLLECTION.put(EntityType.ENDERMAN, "ender_pearl");
        MOB_COLLECTION.put(EntityType.SLIME,    "slime_ball");
        MOB_COLLECTION.put(EntityType.BLAZE,    "blaze_rod");
    }

    private final ProfileManager profileManager = ProfileManager.getInstance();

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        String collection = BLOCK_COLLECTION.get(event.getBlock().getType());
        if (collection == null) {
            return;
        }
        increment(event.getPlayer(), collection);
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        Player killer = event.getEntity().getKiller();
        if (killer == null) {
            return;
        }
        String collection = MOB_COLLECTION.get(event.getEntityType());
        if (collection == null) {
            return;
        }
        increment(killer, collection);
    }

    private void increment(Player player, String collection) {
        profileManager.getOrCreate(player.getUniqueId()).addCollectionCount(collection, 1L);
    }
}
