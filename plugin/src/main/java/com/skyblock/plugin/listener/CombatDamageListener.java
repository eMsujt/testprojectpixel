package com.skyblock.plugin.listener;

import com.skyblock.core.model.Stat;
import com.skyblock.core.manager.StatManager;
import com.skyblock.core.combat.calculator.DamageFormula;
import com.skyblock.core.item.ItemRegistry;
import com.skyblock.core.item.ItemRegistry.ItemDefinition;
import org.bukkit.ChatColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.UUID;

/**
 * Bukkit listener that intercepts {@link EntityDamageByEntityEvent} and, when the
 * attacker is a {@link Player}, reads the held item's damage stat from
 * {@link ItemRegistry} and replaces vanilla damage with the SkyBlock value from
 * {@link DamageFormula}, then applies the defender's defense and true-defense
 * reductions when the victim is also a player.
 *
 * <p>Hypixel defense formula: {@code effective = damage × (1 - defense / (defense + 100))},
 * followed by a flat reduction of {@code trueDefense}.</p>
 */
public final class CombatDamageListener implements Listener {

    private final StatManager statManager = StatManager.getInstance();

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        Entity damager = event.getDamager();
        if (!(damager instanceof Player)) {
            return;
        }

        Player attacker = (Player) damager;
        UUID attackerId = attacker.getUniqueId();

        double weaponDamage = heldItemDamage(attacker, event.getDamage());
        double strength   = statManager.getStat(attackerId, Stat.STRENGTH);
        double critChance = statManager.getStat(attackerId, Stat.CRIT_CHANCE);
        double critDamage = statManager.getStat(attackerId, Stat.CRIT_DAMAGE);

        double damage = DamageFormula.calculate(weaponDamage, strength, critChance, critDamage);

        Entity victim = event.getEntity();
        if (victim instanceof Player) {
            UUID defenderId = victim.getUniqueId();
            double defense     = statManager.getStat(defenderId, Stat.DEFENSE);
            double trueDefense = statManager.getStat(defenderId, Stat.TRUE_DEFENSE);
            // Hypixel defense formula: damage × (1 - defense / (defense + 100)), then flat true-defense reduction
            damage *= (1.0 - defense / (defense + 100.0));
            damage = Math.max(0.0, damage - trueDefense);
        }

        event.setDamage(damage);
    }

    /**
     * Returns the {@code damage} stat of the held item from {@link ItemRegistry} when
     * the item's display name matches a registered definition; falls back to
     * {@code fallback} (the vanilla event damage) when no match is found.
     */
    private double heldItemDamage(Player player, double fallback) {
        ItemStack held = player.getInventory().getItemInMainHand();
        ItemMeta meta = held.getItemMeta();
        if (meta == null || !meta.hasDisplayName()) {
            return fallback;
        }
        String displayName = ChatColor.stripColor(meta.getDisplayName());
        for (ItemDefinition def : ItemRegistry.getInstance().getItems().values()) {
            if (def.displayName().equalsIgnoreCase(displayName) && def.damage() > 0) {
                return def.damage();
            }
        }
        return fallback;
    }
}
