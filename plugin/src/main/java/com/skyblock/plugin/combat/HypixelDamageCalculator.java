package com.skyblock.plugin.combat;

import com.skyblock.plugin.items.StatType;
import com.skyblock.plugin.profile.SkyBlockProfile;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Objects;

/**
 * Static utility that computes SkyBlock damage for a {@link SkyBlockProfile} attacker
 * hitting a {@link LivingEntity} with a given {@link ItemStack}.
 *
 * <p>Delegates to {@link DamageFormula} for the core formula, then applies the
 * Hypixel defense reduction: {@code effective = damage × (1 − defense / (defense + 100))}.</p>
 */
public final class HypixelDamageCalculator {

    private HypixelDamageCalculator() {
        // static utility class, never instantiated
    }

    /**
     * Calculates the final damage dealt by {@code attacker} to {@code defender}
     * using the given {@code weapon}.
     *
     * @param attacker the attacking player's SkyBlock profile, must not be null
     * @param weapon   the weapon ItemStack, may be null (treated as bare-hand)
     * @param defender the defending living entity, must not be null
     * @return the final damage dealt, never negative
     */
    public static double calculate(SkyBlockProfile attacker, ItemStack weapon, LivingEntity defender) {
        Objects.requireNonNull(attacker, "attacker");
        Objects.requireNonNull(defender, "defender");

        double weaponDamage = extractWeaponDamage(weapon);
        double strength   = StatType.STRENGTH.getBaseValue();
        double critChance = StatType.CRIT_CHANCE.getBaseValue();
        double critDamage = StatType.CRIT_DAMAGE.getBaseValue();

        double damage = DamageFormula.calculate(weaponDamage, strength, critChance, critDamage);

        double defense = extractDefense(defender);
        damage *= (1.0 - defense / (defense + 100.0));
        return Math.max(0.0, damage);
    }

    private static double extractWeaponDamage(ItemStack weapon) {
        if (weapon == null || !weapon.hasItemMeta()) {
            return 0.0;
        }
        ItemMeta meta = weapon.getItemMeta();
        var modifiers = meta.getAttributeModifiers(Attribute.GENERIC_ATTACK_DAMAGE);
        if (modifiers == null || modifiers.isEmpty()) {
            return 0.0;
        }
        return modifiers.values().iterator().next().getAmount();
    }

    private static double extractDefense(LivingEntity defender) {
        AttributeInstance armor = defender.getAttribute(Attribute.GENERIC_ARMOR);
        return armor != null ? armor.getValue() : 0.0;
    }
}
