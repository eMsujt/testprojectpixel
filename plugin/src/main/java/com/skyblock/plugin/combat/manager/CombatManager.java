package com.skyblock.plugin.combat.manager;

import com.skyblock.core.model.Stat;
import com.skyblock.core.stat.StatManager;
import com.skyblock.plugin.combat.calculator.DamageFormula;
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

/**
 * Singleton listener that resolves SkyBlock combat damage.
 *
 * <p>On {@link EntityDamageByEntityEvent}, when the damager is a {@link Player},
 * the vanilla damage is replaced with the value from {@link DamageFormula} using
 * the attacker's combat stats from {@link StatManager}.</p>
 *
 * <p>This type is registered as an event listener in
 * {@link com.skyblock.plugin.SkyBlockPlugin#onEnable()}.</p>
 */
public final class CombatManager implements Listener {

    private static final CombatManager INSTANCE = new CombatManager();

    private final StatManager statManager = StatManager.getInstance();

    private CombatManager() {}

    public static CombatManager getInstance() {
        return INSTANCE;
    }

    /**
     * Replaces a player's vanilla hit damage with the SkyBlock-computed value.
     *
     * @param event the damage event
     */
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

        // The vanilla damage of the swung weapon feeds the formula's weapon-damage term.
        double weaponDamage = event.getDamage();
        event.setDamage(DamageFormula.calculate(weaponDamage, strength, critChance, critDamage));
    }

    /**
     * Calculates the melee damage of a hit, resolving stats from the attacker and
     * applying defense reduction when the target is a player.
     *
     * @param attacker the attacking player
     * @param weapon   the held weapon (may be null for bare-hand hits)
     * @param target   the entity being attacked
     * @return the final damage dealt, never negative
     */
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

    /**
     * Calculates the melee damage of a hit from the given combat stats.
     *
     * @param weaponDamage      base weapon damage stat, clamped to &ge; 0
     * @param strength          attacker's strength stat, clamped to &ge; 0
     * @param critChancePercent chance to land a critical hit as a percentage, e.g. {@code 30.0} for 30 %
     * @param critDamagePercent crit damage bonus as a percentage, e.g. {@code 50.0} for +50 %
     * @return the final damage dealt, never negative
     */
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
        Collection<AttributeModifier> mods = meta.getAttributeModifiers(Attribute.GENERIC_ATTACK_DAMAGE);
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
