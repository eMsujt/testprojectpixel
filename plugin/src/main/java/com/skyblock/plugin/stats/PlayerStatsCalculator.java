package com.skyblock.plugin.stats;

import com.skyblock.plugin.item.ItemStatBlock;
import com.skyblock.plugin.item.SkyBlockItem;

import java.util.Collection;
import java.util.Objects;

/**
 * Stateless utility that aggregates {@link ItemStatBlock} bonuses across a
 * collection of equipped {@link SkyBlockItem}s.
 */
public final class PlayerStatsCalculator {

    private PlayerStatsCalculator() {}

    /**
     * Returns an {@link ItemStatBlock} whose fields are the element-wise sum of
     * every item's stat block in {@code equipped}.
     *
     * @param equipped the player's equipped items, never null; null elements are
     *                 silently skipped
     * @return the combined stat bonuses
     */
    public static ItemStatBlock sum(Collection<SkyBlockItem> equipped) {
        Objects.requireNonNull(equipped, "equipped");
        double damage = 0, health = 0, defense = 0, strength = 0;
        double intelligence = 0, critChance = 0, critDamage = 0, speed = 0;
        for (SkyBlockItem item : equipped) {
            if (item == null) {
                continue;
            }
            ItemStatBlock s = item.statBlock();
            damage += s.damage();
            health += s.health();
            defense += s.defense();
            strength += s.strength();
            intelligence += s.intelligence();
            critChance += s.critChance();
            critDamage += s.critDamage();
            speed += s.speed();
        }
        return new ItemStatBlock(damage, health, defense, strength, intelligence,
                critChance, critDamage, speed);
    }
}
