package com.skyblock.core.listener;

import com.skyblock.core.SkyBlockCore;
import com.skyblock.core.manager.ItemStatManager;
import com.skyblock.core.manager.StatManager;
import com.skyblock.core.model.Stat;
import com.destroystokyo.paper.event.player.PlayerArmorChangeEvent;
import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerArmorStandManipulateEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;

import java.util.EnumMap;
import java.util.Map;

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

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        rescanArmor(event.getPlayer());
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getWhoClicked() instanceof Player player) {
            // Run 1 tick later so the inventory reflects the completed click.
            Bukkit.getScheduler().runTaskLater(SkyBlockCore.getInstance(), () -> rescanArmor(player), 1L);
        }
    }

    private void rescanArmor(Player player) {
        recompute(player, player.getInventory().getItemInMainHand());
    }

    /**
     * Recomputes a player's gear bonuses from their armor plus the given held item, replacing only
     * the equipment bonuses so skill/pet/potion bonuses are preserved.
     */
    private void recompute(Player player, ItemStack heldItem) {
        ItemStatManager ism = ItemStatManager.getInstance();
        Map<Stat, Double> totals = new EnumMap<>(Stat.class);
        for (ItemStack piece : player.getInventory().getArmorContents()) {
            accumulate(totals, ism, piece);
        }
        accumulate(totals, ism, heldItem);
        StatManager sm = StatManager.getInstance();
        sm.setEquipmentBonuses(player.getUniqueId(), totals);
        applyMaxHealth(player, sm);
        applyWalkSpeed(player, sm);
        applyAttackSpeed(player, sm);
    }

    /**
     * SkyBlock uses 1.8-style combat (no attack cooldown). Sets a high attack-speed base so swings
     * recharge near-instantly, scaled further by the Bonus Attack Speed stat.
     */
    private void applyAttackSpeed(Player player, StatManager sm) {
        AttributeInstance attr = player.getAttribute(Attribute.ATTACK_SPEED);
        if (attr == null) {
            return;
        }
        double bonus = sm.getStat(player.getUniqueId(), Stat.ATTACK_SPEED);
        attr.setBaseValue(16.0 * (1.0 + bonus / 100.0));
    }

    /** Sets the player's walk speed from their Speed stat (100 Speed = vanilla 0.2). */
    private void applyWalkSpeed(Player player, StatManager sm) {
        double speed = sm.getStat(player.getUniqueId(), Stat.SPEED);
        float walk = (float) Math.max(0.05, Math.min(1.0, 0.2 * (speed / 100.0)));
        player.setWalkSpeed(walk);
    }

    private static void accumulate(Map<Stat, Double> totals, ItemStatManager ism, ItemStack item) {
        if (item == null) {
            return;
        }
        for (Map.Entry<Stat, Integer> entry : ism.getStats(item).entrySet()) {
            totals.merge(entry.getKey(), (double) entry.getValue(), Double::sum);
        }
    }

    /**
     * Updates the player's max health to their current SkyBlock Health stat, so equipping or
     * removing health gear takes effect immediately (not only on respawn).
     */
    private void applyMaxHealth(Player player, StatManager sm) {
        AttributeInstance attr = player.getAttribute(Attribute.MAX_HEALTH);
        if (attr == null) {
            return;
        }
        double maxHealth = Math.max(1.0, sm.getStat(player.getUniqueId(), Stat.HEALTH));
        attr.setBaseValue(maxHealth);
        if (player.getHealth() > maxHealth) {
            player.setHealth(maxHealth);
        }
    }
}
