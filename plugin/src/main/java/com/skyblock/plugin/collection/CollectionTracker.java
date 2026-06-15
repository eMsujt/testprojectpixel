package com.skyblock.plugin.collection;

import com.skyblock.plugin.manager.ProfileManager;
import com.skyblock.plugin.profile.SkyBlockProfile;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerFishEvent;

import java.util.Map;

public final class CollectionTracker implements Listener {

    private static final Map<Material, String> BLOCK_COLLECTION = Map.ofEntries(
            // Mining
            Map.entry(Material.COAL_ORE,            "coal"),
            Map.entry(Material.DEEPSLATE_COAL_ORE,  "coal"),
            Map.entry(Material.IRON_ORE,            "iron_ingot"),
            Map.entry(Material.DEEPSLATE_IRON_ORE,  "iron_ingot"),
            Map.entry(Material.GOLD_ORE,            "gold_ingot"),
            Map.entry(Material.DEEPSLATE_GOLD_ORE,  "gold_ingot"),
            Map.entry(Material.DIAMOND_ORE,         "diamond"),
            Map.entry(Material.DEEPSLATE_DIAMOND_ORE, "diamond"),
            Map.entry(Material.EMERALD_ORE,         "emerald"),
            Map.entry(Material.DEEPSLATE_EMERALD_ORE, "emerald"),
            Map.entry(Material.LAPIS_ORE,           "lapis_lazuli"),
            Map.entry(Material.DEEPSLATE_LAPIS_ORE, "lapis_lazuli"),
            Map.entry(Material.REDSTONE_ORE,        "redstone"),
            Map.entry(Material.DEEPSLATE_REDSTONE_ORE, "redstone"),
            Map.entry(Material.STONE,               "cobblestone"),
            Map.entry(Material.COBBLESTONE,         "cobblestone"),
            // Farming
            Map.entry(Material.WHEAT,               "wheat"),
            Map.entry(Material.POTATOES,            "potato"),
            Map.entry(Material.CARROTS,             "carrot"),
            Map.entry(Material.BEETROOTS,           "beetroot"),
            Map.entry(Material.SUGAR_CANE,          "sugar_cane"),
            Map.entry(Material.PUMPKIN,             "pumpkin"),
            Map.entry(Material.MELON,               "melon"),
            Map.entry(Material.NETHER_WART,         "nether_wart"),
            Map.entry(Material.CACTUS,              "cactus"),
            // Foraging
            Map.entry(Material.OAK_LOG,             "oak_wood"),
            Map.entry(Material.BIRCH_LOG,           "birch_wood"),
            Map.entry(Material.SPRUCE_LOG,          "spruce_wood"),
            Map.entry(Material.JUNGLE_LOG,          "jungle_wood"),
            Map.entry(Material.ACACIA_LOG,          "acacia_wood"),
            Map.entry(Material.DARK_OAK_LOG,        "dark_oak_wood")
    );

    private static final Map<Material, String> FISH_COLLECTION = Map.of(
            Material.RAW_COD,       "cod",
            Material.RAW_SALMON,    "salmon",
            Material.TROPICAL_FISH, "tropical_fish",
            Material.PUFFERFISH,    "pufferfish"
    );

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        String collection = BLOCK_COLLECTION.get(event.getBlock().getType());
        if (collection == null) {
            return;
        }
        Player player = event.getPlayer();
        SkyBlockProfile profile = ProfileManager.getInstance().getOrCreateProfile(player.getUniqueId());
        profile.addCollectionXp(collection, 1L);
    }

    @EventHandler
    public void onFish(PlayerFishEvent event) {
        if (event.getState() != PlayerFishEvent.State.CAUGHT_FISH) {
            return;
        }
        if (!(event.getCaught() instanceof Item caught)) {
            return;
        }
        String collection = FISH_COLLECTION.get(caught.getItemStack().getType());
        if (collection == null) {
            return;
        }
        Player player = event.getPlayer();
        SkyBlockProfile profile = ProfileManager.getInstance().getOrCreateProfile(player.getUniqueId());
        profile.addCollectionXp(collection, 1L);
    }
}
