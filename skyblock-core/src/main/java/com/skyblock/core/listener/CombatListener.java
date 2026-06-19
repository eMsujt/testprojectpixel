package com.skyblock.core.listener;

import com.skyblock.core.combat.calculator.DamageFormula;
import com.skyblock.core.model.Stat;
import com.skyblock.core.manager.StatManager;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Collection;
import java.util.UUID;

public final class CombatListener implements Listener {

    private static final CombatListener INSTANCE = new CombatListener();

    private final StatManager statManager = StatManager.getInstance();

    private CombatListener() {}

    public static CombatListener getInstance() {
        return INSTANCE;
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        Entity attacker = event.getDamager();
        if (!(attacker instanceof Player)) {
            return;
        }

        UUID uuid = attacker.getUniqueId();
        double strength   = statManager.getStat(uuid, Stat.STRENGTH);
        double critChance = statManager.getStat(uuid, Stat.CRIT_CHANCE);
        double critDamage = statManager.getStat(uuid, Stat.CRIT_DAMAGE);

        double weaponDamage = event.getDamage();
        event.setDamage(DamageFormula.calculate(weaponDamage, strength, critChance, critDamage));
    }

    public static double calculateDamage(Player attacker, ItemStack weapon, Entity target) {
        StatManager stats = StatManager.getInstance();
        UUID attackerId = attacker.getUniqueId();

        double weaponDamage = getWeaponDamage(weapon);
        double strength   = stats.getStat(attackerId, Stat.STRENGTH);
        double critChance = stats.getStat(attackerId, Stat.CRIT_CHANCE);
        double critDamage = stats.getStat(attackerId, Stat.CRIT_DAMAGE);

        double damage = DamageFormula.calculate(weaponDamage, strength, critChance, critDamage);

        if (target instanceof Player) {
            UUID defenderId = target.getUniqueId();
            double defense     = stats.getStat(defenderId, Stat.DEFENSE);
            double trueDefense = stats.getStat(defenderId, Stat.TRUE_DEFENSE);
            damage *= (1.0 - defense / (defense + 100.0));
            damage = Math.max(0.0, damage - trueDefense);
        }

        return damage;
    }

    public static double calculateDamage(double weaponDamage, double strength, double critChancePercent, double critDamagePercent) {
        return DamageFormula.calculate(weaponDamage, strength, critChancePercent, critDamagePercent);
    }

    private static double getWeaponDamage(ItemStack weapon) {
        if (weapon == null) {
            return 0.0;
        }
        ItemMeta meta = weapon.getItemMeta();
        if (meta == null) {
            return 0.0;
        }
        Collection<AttributeModifier> mods = meta.getAttributeModifiers(Attribute.ATTACK_DAMAGE);
        if (mods == null || mods.isEmpty()) {
            return 0.0;
        }
        double total = 0.0;
        for (AttributeModifier mod : mods) {
            total += mod.getAmount();
        }
        return Math.max(0.0, total);
    }
}
