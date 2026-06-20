package com.skyblock.core.listener;

import com.skyblock.core.manager.FairySoulManager;
import com.skyblock.core.manager.FairySoulManager.FairyIsland;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Handles fairy soul collection on right-click and cleans up player data on quit.
 *
 * <p>Fairy soul blocks must carry two PDC entries set when placed:
 * {@code fairy_soul_island} (String, a {@link FairyIsland} name) and
 * {@code fairy_soul_index} (Integer, 1-based index within that island).</p>
 */
public final class FairySoulListener implements Listener {

    private final FairySoulManager fairySoulManager;
    private final NamespacedKey islandKey;
    private final NamespacedKey indexKey;

    public FairySoulListener(FairySoulManager fairySoulManager, JavaPlugin plugin) {
        if (fairySoulManager == null) throw new IllegalArgumentException("fairySoulManager must not be null");
        if (plugin == null) throw new IllegalArgumentException("plugin must not be null");
        this.fairySoulManager = fairySoulManager;
        this.islandKey = new NamespacedKey(plugin, "fairy_soul_island");
        this.indexKey = new NamespacedKey(plugin, "fairy_soul_index");
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getClickedBlock() == null) return;
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;

        Block block = event.getClickedBlock();
        PersistentDataContainer pdc = block.getChunk().getPersistentDataContainer();
        String islandName = pdc.get(islandKey, PersistentDataType.STRING);
        Integer soulIndex = pdc.get(indexKey, PersistentDataType.INTEGER);
        if (islandName == null || soulIndex == null) return;

        FairyIsland island;
        try {
            island = FairyIsland.valueOf(islandName);
        } catch (IllegalArgumentException e) {
            return;
        }

        Player player = event.getPlayer();
        if (fairySoulManager.collectSoul(player.getUniqueId(), island, soulIndex)) {
            player.sendMessage("§d§lFairy Soul §r§dcollected! §7("
                    + fairySoulManager.getFoundCount(player.getUniqueId())
                    + "/" + fairySoulManager.getTotalSouls() + ")");
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        fairySoulManager.resetPlayer(event.getPlayer().getUniqueId());
    }
}
