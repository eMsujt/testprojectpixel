package com.skyblock.core.listener;

import com.skyblock.core.SkyBlockCore;
import com.skyblock.core.manager.ItemStatManager;
import com.skyblock.core.manager.StatManager;
import com.skyblock.core.model.Stat;
import com.skyblock.core.talisman.manager.TalismanManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.Locale;
import java.util.Map;
import java.util.UUID;

/**
 * Keeps a player's derived stats in sync with the gear they are wearing or
 * carrying.
 *
 * <p>This consolidated listener owns two related concerns that were previously
 * split across {@code AccessoryListener} and {@code ItemStatsManager}:</p>
 * <ul>
 *   <li><b>Accessory-bag refresh</b> — re-equips every talisman found in the
 *       player's inventory whenever they switch held items or close an
 *       inventory, via {@link TalismanManager}.</li>
 *   <li><b>Armor-stat rescan</b> — recomputes the stat bonuses granted by the
 *       player's worn armor on join and after any inventory click, via
 *       {@link StatManager} and {@link ItemStatManager}.</li>
 * </ul>
 */
public final class EquipmentListener implements Listener {

    private static final EquipmentListener INSTANCE = new EquipmentListener();

    private final TalismanManager talismanManager = TalismanManager.getInstance();

    private EquipmentListener() {}

    public static EquipmentListener getInstance() {
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

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        rescanArmor(event.getPlayer());
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) {
            return;
        }
        // schedule 1 tick so the inventory state reflects the completed click
        player.getServer().getScheduler().runTaskLater(
                SkyBlockCore.getInstance(),
                () -> rescanArmor(player),
                1L);
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

    private void rescanArmor(Player player) {
        UUID id = player.getUniqueId();
        StatManager sm = StatManager.getInstance();
        sm.clearBonuses(id);
        ItemStatManager ism = ItemStatManager.getInstance();
        for (ItemStack piece : player.getInventory().getArmorContents()) {
            if (piece == null) {
                continue;
            }
            Map<Stat, Integer> stats = ism.getStats(piece);
            for (Map.Entry<Stat, Integer> entry : stats.entrySet()) {
                sm.addBonus(id, entry.getKey(), entry.getValue());
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
