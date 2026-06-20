package com.skyblock.plugin.listener;

import com.skyblock.core.menu.manager.SkyBlockMenuManager;
import com.skyblock.plugin.managers.TimeManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public final class PlayerEventListener implements Listener {

    private static final String SKYBLOCK_MENU_ITEM = "§aSkyBlock Menu";

    private final TimeManager timeManager = TimeManager.getInstance();

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        TimeManager.SkyblockTime current = timeManager.getCurrentTime();
        event.getPlayer().sendMessage("§aCurrent Skyblock time: §e" + current.name());
    }

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
        if (meta == null || !SKYBLOCK_MENU_ITEM.equals(meta.getDisplayName())) {
            return;
        }
        event.setCancelled(true);
        SkyBlockMenuManager.getInstance().openMainMenu(event.getPlayer());
    }
}
