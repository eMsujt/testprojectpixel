package com.skyblock.core.npc;

import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;

/**
 * Opens a {@link FunctionalNpc}'s feature menu when its armor stand is
 * right-clicked, and shields NPC stands from being destroyed by attacks.
 */
public final class FunctionalNpcListener implements Listener {

    @EventHandler
    public void onInteract(PlayerInteractAtEntityEvent event) {
        if (!(event.getRightClicked() instanceof ArmorStand stand)) {
            return;
        }
        FunctionalNpc npc = FunctionalNpcManager.getInstance().findByEntity(stand.getUniqueId());
        if (npc == null) {
            return;
        }
        event.setCancelled(true);
        Player player = event.getPlayer();
        npc.open(player);
    }

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof ArmorStand stand)) {
            return;
        }
        if (FunctionalNpcManager.getInstance().findByEntity(stand.getUniqueId()) != null) {
            event.setCancelled(true);
        }
    }
}
