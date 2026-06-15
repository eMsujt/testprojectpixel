package com.skyblock.plugin.listener;

import com.skyblock.plugin.manager.ProfileManager;
import com.skyblock.plugin.profile.SkyBlockProfile;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;

import java.util.Map;

/**
 * Awards Carpentry XP directly to the player's {@link SkyBlockProfile} whenever
 * a player crafts an item mapped in {@code RECIPE_XP}. XP is scaled 1–10 by recipe.
 */
public final class CarpentryXpListener implements Listener {

    private static final Map<Material, Long> RECIPE_XP = Map.ofEntries(
            Map.entry(Material.CRAFTING_TABLE,     1L),
            Map.entry(Material.CHEST,              2L),
            Map.entry(Material.TRAPPED_CHEST,      2L),
            Map.entry(Material.BARREL,             3L),
            Map.entry(Material.BOOKSHELF,          3L),
            Map.entry(Material.LECTERN,            4L),
            Map.entry(Material.COMPOSTER,          4L),
            Map.entry(Material.LOOM,               4L),
            Map.entry(Material.CARTOGRAPHY_TABLE,  5L),
            Map.entry(Material.FLETCHING_TABLE,    5L),
            Map.entry(Material.SMITHING_TABLE,     5L),
            Map.entry(Material.WOODEN_SWORD,       2L),
            Map.entry(Material.WOODEN_PICKAXE,     2L),
            Map.entry(Material.WOODEN_AXE,         2L),
            Map.entry(Material.WOODEN_SHOVEL,      2L),
            Map.entry(Material.WOODEN_HOE,         2L),
            Map.entry(Material.OAK_DOOR,           3L),
            Map.entry(Material.SPRUCE_DOOR,        3L),
            Map.entry(Material.BIRCH_DOOR,         3L),
            Map.entry(Material.JUNGLE_DOOR,        3L),
            Map.entry(Material.ACACIA_DOOR,        3L),
            Map.entry(Material.DARK_OAK_DOOR,      3L),
            Map.entry(Material.OAK_FENCE,          2L),
            Map.entry(Material.OAK_FENCE_GATE,     3L),
            Map.entry(Material.OAK_STAIRS,         2L),
            Map.entry(Material.OAK_SLAB,           1L),
            Map.entry(Material.OAK_TRAPDOOR,       3L),
            Map.entry(Material.NOTE_BLOCK,         5L),
            Map.entry(Material.JUKEBOX,            8L),
            Map.entry(Material.DAYLIGHT_DETECTOR,  7L),
            Map.entry(Material.BEEHIVE,            6L),
            Map.entry(Material.FLOWER_POT,         2L),
            Map.entry(Material.ITEM_FRAME,         3L),
            Map.entry(Material.PAINTING,           3L),
            Map.entry(Material.BED,                4L),
            Map.entry(Material.BOWL,               1L),
            Map.entry(Material.ARMOR_STAND,        5L),
            Map.entry(Material.SHULKER_BOX,       10L),
            Map.entry(Material.ENDER_CHEST,       10L)
    );

    @EventHandler
    public void onCraftItem(CraftItemEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;
        Long xp = RECIPE_XP.get(event.getRecipe().getResult().getType());
        if (xp == null) return;
        SkyBlockProfile profile = ProfileManager.getInstance().getOrCreateProfile(player.getUniqueId());
        profile.addSkillXp("carpentry", xp);
        XpActionBar.send(player, "carpentry", xp, profile.getSkillXp("carpentry"));
    }
}
