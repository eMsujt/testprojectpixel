package com.skyblock.core.listener;

import com.skyblock.core.manager.MiningManager;
import com.skyblock.core.manager.MiningManager.OreType;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import java.util.UUID;

/**
 * Listener for mining-specific block-break events.
 *
 * <p>Detects ore and stone blocks broken by a player and forwards XP
 * to {@link MiningManager}, which tracks per-player mining XP and level
 * independently of the generic skill XP handled by {@link SkillListener}.</p>
 */
public final class MiningListener implements Listener {

    private static final MiningListener INSTANCE = new MiningListener();

    private static final long STONE_XP = 1L;

    private MiningListener() {}

    public static MiningListener getInstance() {
        return INSTANCE;
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (event.isCancelled()) return;
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();
        Material type = event.getBlock().getType();

        MiningManager manager = MiningManager.getInstance();

        OreType ore = MiningManager.MATERIAL_TO_ORE.get(type);
        if (ore != null) {
            int before = manager.getLevel(uuid);
            manager.addXp(uuid, ore.getXp());
            int after = manager.getLevel(uuid);
            if (after > before) {
                player.sendTitle("§aSkill Level Up!", "§eMining §a→ §eLVL " + after, 10, 60, 20);
            }
            return;
        }

        if (type == Material.STONE || type == Material.COBBLESTONE
                || type == Material.DEEPSLATE || type == Material.COBBLED_DEEPSLATE) {
            manager.addXp(uuid, STONE_XP);
        }
    }
}
