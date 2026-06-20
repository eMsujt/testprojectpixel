package com.skyblock.core.listener;

import com.skyblock.core.manager.GardenManager;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import java.util.Map;

/**
 * Feeds harvested Garden crops into the player's composter as organic matter.
 *
 * <p>When a player breaks a farmable crop block, the crop contributes a small
 * amount of organic matter to that player's composter reserve, tracked through
 * the canonical composter state on {@link GardenManager}. Fuel and the actual
 * compost-processing step are handled separately (via {@code /compost}); this
 * listener only accumulates organic matter, so it does not duplicate the
 * Farming-XP handling done by the consolidated skill listener.</p>
 */
public final class CompostListener implements Listener {

    /** Organic matter contributed to the composter per harvested crop block. */
    private static final Map<Material, Long> ORGANIC_MATTER = Map.of(
            Material.WHEAT,       2L,
            Material.CARROTS,     2L,
            Material.POTATOES,    2L,
            Material.BEETROOTS,   2L,
            Material.NETHER_WART, 3L,
            Material.MELON,       1L,
            Material.PUMPKIN,     3L,
            Material.COCOA,       2L,
            Material.SUGAR_CANE,  2L
    );

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Long matter = ORGANIC_MATTER.get(event.getBlock().getType());
        if (matter == null) {
            return;
        }
        Player player = event.getPlayer();
        GardenManager.getInstance().addComposterOrganicMatter(player.getUniqueId(), matter);
    }
}
