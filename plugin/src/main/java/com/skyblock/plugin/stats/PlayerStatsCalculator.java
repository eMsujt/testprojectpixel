package com.skyblock.plugin.stats;

import com.skyblock.plugin.item.ItemStatBlock;
import com.skyblock.plugin.item.SkyBlockItem;

import java.util.Collection;
import java.util.Objects;

/**
 * A stateless utility that aggregates the {@link ItemStatBlock} bonuses of a
 * collection of {@link SkyBlockItem}s into a single combined block.
 */
public final class PlayerStatsCalculator {

    private PlayerStatsCalculator() {
        throw new AssertionError("No PlayerStatsCalculator instances for you!");
    }

    /**
     * Sums the stat blocks of every item, accumulating each of the eight stats
     * field-by-field.
     *
     * @param items the items whose stats to combine, never null and containing
     *              no null elements
     * @return an {@link ItemStatBlock} holding the element-wise totals; an
     *         all-zero block when {@code items} is empty
     * @throws NullPointerException if {@code items} or any element is null
     */
    public static ItemStatBlock sum(Collection<SkyBlockItem> items) {
        Objects.requireNonNull(items, "items");

        double damage = 0, health = 0, defense = 0, strength = 0;
        double intelligence = 0, critChance = 0, critDamage = 0, speed = 0;

        for (SkyBlockItem item : items) {
            Objects.requireNonNull(item, "item");
            damage += item.stat("damage");
            health += item.stat("health");
            defense += item.stat("defense");
            strength += item.stat("strength");
            intelligence += item.stat("intelligence");
            critChance += item.stat("critChance");
            critDamage += item.stat("critDamage");
            speed += item.stat("speed");
        }

        return new ItemStatBlock(damage, health, defense, strength,
                intelligence, critChance, critDamage, speed);
    }
}
