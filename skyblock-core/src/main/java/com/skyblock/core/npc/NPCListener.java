package com.skyblock.core.npc;

import com.skyblock.core.npc.NpcManager.NpcDefinition;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Bukkit listener that intercepts right-click interactions with NPC
 * {@link ArmorStand} entities spawned by {@link NpcManager} and opens
 * {@link NpcShopMenu} for the interacting player.
 */
public final class NPCListener implements Listener {

    private final NpcManager npcManager;
    private final JavaPlugin plugin;

    public NPCListener(JavaPlugin plugin, NpcManager npcManager) {
        if (plugin == null) throw new IllegalArgumentException("plugin must not be null");
        if (npcManager == null) throw new IllegalArgumentException("npcManager must not be null");
        this.plugin = plugin;
        this.npcManager = npcManager;
    }

    @EventHandler
    public void onPlayerInteractAtEntity(PlayerInteractAtEntityEvent event) {
        if (!(event.getRightClicked() instanceof ArmorStand)) {
            return;
        }

        NpcDefinition npc = npcManager.findByEntity(event.getRightClicked().getUniqueId());
        if (npc == null) {
            return;
        }

        event.setCancelled(true);
        Player player = event.getPlayer();
        new NpcShopMenu(plugin, player, npc).open(player);
    }
}
