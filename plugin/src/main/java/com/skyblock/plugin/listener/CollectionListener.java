package com.skyblock.plugin.listener;

import com.skyblock.plugin.manager.ProfileManager;
import com.skyblock.plugin.profile.SkyBlockProfile;
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
 * Tracks a player's collections directly on their {@link SkyBlockProfile} in
 * response to real in-world actions. Breaking a block counts the block towards
 * its collection; killing a mob counts the mob towards its collection.
 */
public final class CollectionListener implements Listener {

    private static final Map<Material, String> BLOCK_COLLECTION = new EnumMap<>(Material.class);
    private static final Map<EntityType, String> MOB_COLLECTION = new EnumMap<>(EntityType.class);

    static {
        // Farming
        BLOCK_COLLECTION.put(Material.WHEAT,             "wheat");
        BLOCK_COLLECTION.put(Material.CARROTS,           "carrot");
        BLOCK_COLLECTION.put(Material.POTATOES,          "potato");
        BLOCK_COLLECTION.put(Material.PUMPKIN,           "pumpkin");
        BLOCK_COLLECTION.put(Material.MELON,             "melon");
        BLOCK_COLLECTION.put(Material.SUGAR_CANE,        "sugar_cane");
        BLOCK_COLLECTION.put(Material.COCOA,             "cocoa_beans");
        BLOCK_COLLECTION.put(Material.CACTUS,            "cactus");
        BLOCK_COLLECTION.put(Material.BROWN_MUSHROOM,    "brown_mushroom");
        BLOCK_COLLECTION.put(Material.RED_MUSHROOM,      "red_mushroom");
        BLOCK_COLLECTION.put(Material.NETHER_WART,       "nether_wart");
        // Mining
        BLOCK_COLLECTION.put(Material.COBBLESTONE,       "cobblestone");
        BLOCK_COLLECTION.put(Material.COAL_ORE,          "coal");
        BLOCK_COLLECTION.put(Material.DEEPSLATE_COAL_ORE, "coal");
        BLOCK_COLLECTION.put(Material.IRON_ORE,          "iron_ingot");
        BLOCK_COLLECTION.put(Material.DEEPSLATE_IRON_ORE, "iron_ingot");
        BLOCK_COLLECTION.put(Material.GOLD_ORE,          "gold_ingot");
        BLOCK_COLLECTION.put(Material.DEEPSLATE_GOLD_ORE, "gold_ingot");
        BLOCK_COLLECTION.put(Material.DIAMOND_ORE,       "diamond");
        BLOCK_COLLECTION.put(Material.DEEPSLATE_DIAMOND_ORE, "diamond");
        BLOCK_COLLECTION.put(Material.LAPIS_ORE,         "lapis_lazuli");
        BLOCK_COLLECTION.put(Material.DEEPSLATE_LAPIS_ORE, "lapis_lazuli");
        BLOCK_COLLECTION.put(Material.EMERALD_ORE,       "emerald");
        BLOCK_COLLECTION.put(Material.DEEPSLATE_EMERALD_ORE, "emerald");
        BLOCK_COLLECTION.put(Material.REDSTONE_ORE,      "redstone");
        BLOCK_COLLECTION.put(Material.DEEPSLATE_REDSTONE_ORE, "redstone");
        BLOCK_COLLECTION.put(Material.NETHER_QUARTZ_ORE, "quartz");
        BLOCK_COLLECTION.put(Material.OBSIDIAN,          "obsidian");
        // Foraging
        BLOCK_COLLECTION.put(Material.OAK_LOG,           "oak_wood");
        BLOCK_COLLECTION.put(Material.SPRUCE_LOG,        "spruce_wood");
        BLOCK_COLLECTION.put(Material.BIRCH_LOG,         "birch_wood");
        BLOCK_COLLECTION.put(Material.JUNGLE_LOG,        "jungle_wood");
        BLOCK_COLLECTION.put(Material.ACACIA_LOG,        "acacia_wood");
        BLOCK_COLLECTION.put(Material.DARK_OAK_LOG,      "dark_oak_wood");
        // Combat
        MOB_COLLECTION.put(EntityType.ZOMBIE,   "rotten_flesh");
        MOB_COLLECTION.put(EntityType.SKELETON, "bone");
        MOB_COLLECTION.put(EntityType.SPIDER,   "string");
        MOB_COLLECTION.put(EntityType.CREEPER,  "gunpowder");
        MOB_COLLECTION.put(EntityType.ENDERMAN, "ender_pearl");
        MOB_COLLECTION.put(EntityType.SLIME,    "slime_ball");
        MOB_COLLECTION.put(EntityType.BLAZE,    "blaze_rod");
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        String collection = BLOCK_COLLECTION.get(event.getBlock().getType());
        if (collection == null) {
            return;
        }
        Player player = event.getPlayer();
        SkyBlockProfile profile = ProfileManager.getInstance().getOrCreateProfile(player.getUniqueId());
        profile.incrementCollection(collection);
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
        SkyBlockProfile profile = ProfileManager.getInstance().getOrCreateProfile(killer.getUniqueId());
        profile.incrementCollection(collection);
    }
}
