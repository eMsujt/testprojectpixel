package com.skyblock.plugin.minion;

import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;

import java.util.List;
import java.util.UUID;

/**
 * Listener that opens a {@link MinionMenu} when a player right-clicks the
 * {@link ArmorStand} representing one of their placed minions.
 *
 * <p>Minions are rendered as armour stands; interacting with one opens the
 * management menu for the player's minion. The interaction is cancelled so the
 * armour stand's pose is not edited.</p>
 */
public final class MinionListener implements Listener {

    @EventHandler
    public void onPlayerInteract(PlayerInteractAtEntityEvent event) {
        Entity entity = event.getRightClicked();
        if (!(entity instanceof ArmorStand)) {
            return;
        }

        Player player = event.getPlayer();
        UUID owner = player.getUniqueId();
        List<Minion> minions = MinionManager.getInstance().getMinions(owner);
        if (minions.isEmpty()) {
            return;
        }

        event.setCancelled(true);
        new MinionMenu(minions.get(0)).open(player);
    }
}
