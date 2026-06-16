package com.skyblock.plugin.listener;

import com.skyblock.core.menu.manager.SkyBlockMenuManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/**
 * Opens the SkyBlock main menu when a player right-clicks while holding the
 * SkyBlock Menu item (a NETHER_STAR named {@code §aSkyBlock Menu}).
 */
public final class SkyBlockMenuItemListener implements Listener {

    private static final String ITEM_NAME = "§aSkyBlock Menu";

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Action action = event.getAction();
        if (action != Action.RIGHT_CLICK_AIR && action != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        ItemStack item = event.getItem();
        if (item == null) {
            return;
        }
        ItemMeta meta = item.getItemMeta();
        if (meta == null || !ITEM_NAME.equals(meta.getDisplayName())) {
            return;
        }

        event.setCancelled(true);
        Player player = event.getPlayer();
        SkyBlockMenuManager.getInstance().openMainMenu(player);
    }
}
