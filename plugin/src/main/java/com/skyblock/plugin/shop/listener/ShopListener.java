package com.skyblock.plugin.shop.listener;

import com.skyblock.plugin.shop.NpcShopMenu;
import org.bukkit.ChatColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

/**
 * Listener that opens an {@link NpcShopMenu} when a player right-clicks a shop
 * NPC {@link Villager}.
 *
 * <p>The villager's custom name (stripped of colour codes) is used as the shop
 * name, so it maps to {@code shops/<name>.yml} in the plugin data folder.
 * Villagers without a custom name are ignored, leaving vanilla interaction
 * untouched; for named shop villagers the interaction is cancelled so the
 * vanilla trade GUI does not also open.</p>
 */
public final class ShopListener implements Listener {

    private final JavaPlugin plugin;

    /**
     * @param plugin the owning plugin, used for resource extraction and logging
     */
    public ShopListener(JavaPlugin plugin) {
        this.plugin = Objects.requireNonNull(plugin, "plugin");
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEntityEvent event) {
        Entity entity = event.getRightClicked();
        if (!(entity instanceof Villager) || entity.getCustomName() == null) {
            return;
        }

        event.setCancelled(true);
        String shopName = ChatColor.stripColor(entity.getCustomName());
        Player player = event.getPlayer();
        new NpcShopMenu(plugin, shopName).open(player);
    }
}
