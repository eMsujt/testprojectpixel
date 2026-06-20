package com.skyblock.core.listener;

import com.skyblock.core.talisman.manager.TalismanManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.Locale;
import java.util.UUID;

public final class AccessoryListener implements Listener {

    private static final AccessoryListener INSTANCE = new AccessoryListener();

    private final TalismanManager talismanManager = TalismanManager.getInstance();

    private AccessoryListener() {}

    public static AccessoryListener getInstance() {
        return INSTANCE;
    }

    @EventHandler
    public void onItemHeld(PlayerItemHeldEvent event) {
        refreshAccessories(event.getPlayer());
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (!(event.getPlayer() instanceof Player player)) {
            return;
        }
        refreshAccessories(player);
    }

    private void refreshAccessories(Player player) {
        UUID uuid = player.getUniqueId();
        talismanManager.reset(uuid);
        PlayerInventory inv = player.getInventory();
        for (int slot = 0; slot < 36; slot++) {
            ItemStack item = inv.getItem(slot);
            if (item == null) {
                continue;
            }
            TalismanManager.TalismanType type = resolveType(item);
            if (type != null) {
                talismanManager.equip(uuid, type);
            }
        }
    }

    private static TalismanManager.TalismanType resolveType(ItemStack item) {
        if (!item.hasItemMeta()) {
            return null;
        }
        String displayName = item.getItemMeta().getDisplayName();
        if (displayName == null || displayName.isEmpty()) {
            return null;
        }
        String key = displayName.replaceAll("§.", "").trim()
                .toUpperCase(Locale.ROOT).replace(' ', '_');
        try {
            return TalismanManager.TalismanType.valueOf(key);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
