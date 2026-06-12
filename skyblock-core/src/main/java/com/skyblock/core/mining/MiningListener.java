package com.skyblock.core.mining;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

/**
 * Bukkit listener that awards mining XP when a player breaks an ore block.
 *
 * <p>Ore blocks are identified via {@link MiningManager#MATERIAL_TO_ORE}; any
 * other broken block is silently ignored.</p>
 */
public final class MiningListener implements Listener {

    private final MiningManager miningManager;

    /**
     * Creates a listener backed by the given {@link MiningManager}.
     *
     * @param miningManager the mining manager, must not be null
     * @throws IllegalArgumentException if {@code miningManager} is null
     */
    public MiningListener(MiningManager miningManager) {
        if (miningManager == null) {
            throw new IllegalArgumentException("miningManager must not be null");
        }
        this.miningManager = miningManager;
    }

    /**
     * Awards mining XP to the player when they break an ore block.
     *
     * @param event the block-break event fired by the server
     */
    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Material material = event.getBlock().getType();
        MiningManager.OreType oreType = MiningManager.MATERIAL_TO_ORE.get(material);
        if (oreType == null) {
            return;
        }
        Player player = event.getPlayer();
        miningManager.addXp(player.getUniqueId(), oreType.getXp());
    }
}
