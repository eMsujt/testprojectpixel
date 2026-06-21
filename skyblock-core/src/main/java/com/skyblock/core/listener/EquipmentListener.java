package com.skyblock.core.listener;

import com.skyblock.core.SkyBlockCore;
import com.skyblock.core.manager.ItemStatManager;
import com.skyblock.core.manager.StatManager;
import com.skyblock.core.model.Stat;
import com.destroystokyo.paper.event.player.PlayerArmorChangeEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerArmorStandManipulateEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Map;
import java.util.UUID;

public final class EquipmentListener implements Listener {

    private static final EquipmentListener INSTANCE = new EquipmentListener();

    private EquipmentListener() {}

    public static EquipmentListener getInstance() {
        return INSTANCE;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onArmorStandManipulate(PlayerArmorStandManipulateEvent event) {
        Player player = event.getPlayer();
        Bukkit.getScheduler().runTaskLater(SkyBlockCore.getInstance(), () -> rescanArmor(player), 1L);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerArmorChange(PlayerArmorChangeEvent event) {
        Player player = event.getPlayer();
        Bukkit.getScheduler().runTaskLater(SkyBlockCore.getInstance(), () -> rescanArmor(player), 1L);
    }

    @EventHandler
    public void onItemHeld(PlayerItemHeldEvent event) {
        Player player = event.getPlayer();
        // The held slot hasn't switched yet, so read the item in the slot being switched TO.
        recompute(player, player.getInventory().getItem(event.getNewSlot()));
    }

    private void rescanArmor(Player player) {
        recompute(player, player.getInventory().getItemInMainHand());
    }

    /** Recomputes a player's gear bonuses from their armor plus the given held item. */
    private void recompute(Player player, ItemStack heldItem) {
        UUID id = player.getUniqueId();
        StatManager sm = StatManager.getInstance();
        sm.clearBonuses(id);
        ItemStatManager ism = ItemStatManager.getInstance();
        for (ItemStack piece : player.getInventory().getArmorContents()) {
            applyItemStats(sm, ism, id, piece);
        }
        applyItemStats(sm, ism, id, heldItem);
    }

    private static void applyItemStats(StatManager sm, ItemStatManager ism, UUID id, ItemStack item) {
        if (item == null) {
            return;
        }
        for (Map.Entry<Stat, Integer> entry : ism.getStats(item).entrySet()) {
            sm.addBonus(id, entry.getKey(), entry.getValue());
        }
    }
}
