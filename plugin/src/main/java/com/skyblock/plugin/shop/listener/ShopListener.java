package com.skyblock.plugin.shop.listener;

import com.skyblock.core.manager.ShopManager;
import com.skyblock.core.manager.ShopManager.Shop;
import com.skyblock.core.manager.ShopManager.ShopEntry;
import com.skyblock.core.menu.ShopMenu;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;

import java.util.ArrayList;
import java.util.List;

/**
 * Listener that opens a {@link ShopMenu} when a player right-clicks a shop
 * NPC {@link Villager}.
 *
 * <p>The villager's custom name (stripped of colour codes) is used as the shop
 * key, looked up in the canonical {@link ShopManager}. Villagers without a
 * custom name are ignored; for named shop villagers the interaction is
 * cancelled so the vanilla trade GUI does not also open.</p>
 */
public final class ShopListener implements Listener {

    public ShopListener() {}

    @EventHandler
    public void onPlayerInteract(PlayerInteractEntityEvent event) {
        Entity entity = event.getRightClicked();
        if (!(entity instanceof Villager) || entity.getCustomName() == null) {
            return;
        }

        event.setCancelled(true);
        String shopName = ChatColor.stripColor(entity.getCustomName());
        Player player = event.getPlayer();

        ShopManager.getInstance().getShop(shopName).ifPresent(shop -> {
            new ShopMenu(shop.title(), toShopItems(shop)).open(player);
        });
    }

    private static List<ShopMenu.ShopItem> toShopItems(Shop shop) {
        List<ShopMenu.ShopItem> result = new ArrayList<>();
        for (ShopEntry entry : shop.entries()) {
            Material material = Material.matchMaterial(entry.itemId());
            if (material != null) {
                result.add(new ShopMenu.ShopItem(material, (int) entry.buyPrice(), (int) entry.sellPrice()));
            }
        }
        return result;
    }
}
