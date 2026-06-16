package com.skyblock.plugin.listener;

import com.skyblock.core.manager.CollectionManager;
import com.skyblock.plugin.profile.ProfileManager;
import com.skyblock.plugin.profile.PlayerProfile;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import java.util.Map;

/**
 * Awards Mining Skill XP to a player's {@link PlayerProfile} when they break an ore
 * or stone block, via {@link ProfileManager}.
 */
public final class MiningXpListener implements Listener {

    private static final Map<Material, Long> MINING_XP = Map.ofEntries(
            Map.entry(Material.STONE,                     1L),
            Map.entry(Material.COBBLESTONE,               1L),
            Map.entry(Material.COAL_ORE,                  5L),
            Map.entry(Material.DEEPSLATE_COAL_ORE,        5L),
            Map.entry(Material.IRON_ORE,                 10L),
            Map.entry(Material.DEEPSLATE_IRON_ORE,       10L),
            Map.entry(Material.GOLD_ORE,                 18L),
            Map.entry(Material.DEEPSLATE_GOLD_ORE,       18L),
            Map.entry(Material.NETHER_GOLD_ORE,           4L),
            Map.entry(Material.DIAMOND_ORE,              25L),
            Map.entry(Material.DEEPSLATE_DIAMOND_ORE,    25L),
            Map.entry(Material.LAPIS_ORE,                 5L),
            Map.entry(Material.DEEPSLATE_LAPIS_ORE,       5L),
            Map.entry(Material.EMERALD_ORE,              28L),
            Map.entry(Material.DEEPSLATE_EMERALD_ORE,    28L),
            Map.entry(Material.REDSTONE_ORE,              5L),
            Map.entry(Material.DEEPSLATE_REDSTONE_ORE,   5L),
            Map.entry(Material.NETHER_QUARTZ_ORE,         4L)
    );

    private static final Map<Material, String> MINING_DROP = Map.ofEntries(
            Map.entry(Material.STONE,                     "COBBLESTONE"),
            Map.entry(Material.COBBLESTONE,               "COBBLESTONE"),
            Map.entry(Material.COAL_ORE,                  "COAL"),
            Map.entry(Material.DEEPSLATE_COAL_ORE,        "COAL"),
            Map.entry(Material.IRON_ORE,                  "RAW_IRON"),
            Map.entry(Material.DEEPSLATE_IRON_ORE,        "RAW_IRON"),
            Map.entry(Material.GOLD_ORE,                  "RAW_GOLD"),
            Map.entry(Material.DEEPSLATE_GOLD_ORE,        "RAW_GOLD"),
            Map.entry(Material.NETHER_GOLD_ORE,           "RAW_GOLD"),
            Map.entry(Material.DIAMOND_ORE,               "DIAMOND"),
            Map.entry(Material.DEEPSLATE_DIAMOND_ORE,     "DIAMOND"),
            Map.entry(Material.LAPIS_ORE,                 "LAPIS_LAZULI"),
            Map.entry(Material.DEEPSLATE_LAPIS_ORE,       "LAPIS_LAZULI"),
            Map.entry(Material.EMERALD_ORE,               "EMERALD"),
            Map.entry(Material.DEEPSLATE_EMERALD_ORE,     "EMERALD"),
            Map.entry(Material.REDSTONE_ORE,              "REDSTONE"),
            Map.entry(Material.DEEPSLATE_REDSTONE_ORE,    "REDSTONE"),
            Map.entry(Material.NETHER_QUARTZ_ORE,         "QUARTZ")
    );

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Material type = event.getBlock().getType();
        Long xp = MINING_XP.get(type);
        if (xp == null) {
            return;
        }
        Player player = event.getPlayer();
        PlayerProfile profile = ProfileManager.getInstance().getOrCreate(player.getUniqueId());
        profile.addSkillXp("mining", xp);
        XpActionBar.send(player, "mining", xp, profile.getSkillXp("mining"));
        CollectionManager.getInstance().addItems(player.getUniqueId(), MINING_DROP.get(type), 1);
    }
}
