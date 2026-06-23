package com.skyblock.core.listener;

import com.skyblock.core.SkyBlockCore;
import com.skyblock.core.menu.SkyBlockMenu;
import com.skyblock.core.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

/**
 * Gives every player the Hypixel-style "SkyBlock Menu" item in their hotbar (slot 8) on join and
 * opens the SkyBlock menu when it's right-clicked.
 */
public final class MenuItemListener implements Listener {

    private static final MenuItemListener INSTANCE = new MenuItemListener();
    private static final int MENU_SLOT = 8;

    private MenuItemListener() {}

    public static MenuItemListener getInstance() {
        return INSTANCE;
    }

    private static NamespacedKey key() {
        return new NamespacedKey(SkyBlockCore.getInstance(), "sb_menu_item");
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (hasMenuItem(player)) {
            return;
        }
        ItemStack atSlot = player.getInventory().getItem(MENU_SLOT);
        if (atSlot == null || atSlot.getType() == Material.AIR) {
            player.getInventory().setItem(MENU_SLOT, menuItem());
        } else {
            player.getInventory().addItem(menuItem());
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (event.getHand() != EquipmentSlot.HAND) {
            return;
        }
        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }
        if (!isMenuItem(event.getItem())) {
            return;
        }
        event.setCancelled(true);
        new SkyBlockMenu(event.getPlayer()).open(event.getPlayer());
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (isMenuItem(event.getCurrentItem()) || isMenuItem(event.getCursor())) {
            event.setCancelled(true);
            return;
        }
        if (event.getClick() == ClickType.NUMBER_KEY
                && isMenuItem(event.getView().getBottomInventory().getItem(event.getHotbarButton()))) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        if (isMenuItem(event.getOldCursor())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onDropItem(PlayerDropItemEvent event) {
        if (isMenuItem(event.getItemDrop().getItemStack())) {
            event.setCancelled(true);
        }
    }

    private static ItemStack menuItem() {
        ItemStack item = new ItemBuilder(Material.NETHER_STAR)
                .displayName("§aSkyBlock Menu §7(Click)")
                .lore("§7View your profile, skills,", "§7collections, and more.", "", "§eClick to open!")
                .build();
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.getPersistentDataContainer().set(key(), PersistentDataType.BYTE, (byte) 1);
            item.setItemMeta(meta);
        }
        return item;
    }

    private static boolean isMenuItem(ItemStack item) {
        if (item == null) {
            return false;
        }
        ItemMeta meta = item.getItemMeta();
        return meta != null && meta.getPersistentDataContainer().has(key(), PersistentDataType.BYTE);
    }

    private static boolean hasMenuItem(Player player) {
        for (ItemStack item : player.getInventory().getContents()) {
            if (isMenuItem(item)) {
                return true;
            }
        }
        return false;
    }
}
