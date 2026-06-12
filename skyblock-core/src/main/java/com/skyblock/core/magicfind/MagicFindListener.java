package com.skyblock.core.magicfind;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Bukkit listener that applies a player's Magic Find bonus to mob loot.
 *
 * <p>When a player kills a {@link LivingEntity}, each item in the default drop
 * list is rolled an additional time with probability equal to
 * {@code (magicFind * {@value MagicFindManager#BONUS_PER_POINT})%} — up to a
 * maximum of one extra copy of each drop per kill.</p>
 */
public final class MagicFindListener implements Listener {

    private static final Random RANDOM = new Random();

    private final MagicFindManager magicFindManager;

    public MagicFindListener(MagicFindManager magicFindManager) {
        this.magicFindManager = magicFindManager;
    }

    /**
     * Applies the killer's Magic Find bonus to the entity's drop list.
     *
     * @param event the entity-death event
     */
    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        Player killer = event.getEntity().getKiller();
        if (killer == null) {
            return;
        }

        double multiplier = magicFindManager.getDropMultiplier(killer.getUniqueId());
        if (multiplier <= 1.0) {
            return;
        }

        // bonus chance = fractional part of the multiplier (e.g. 1.35 → 35%)
        double bonusChance = multiplier - 1.0;

        List<ItemStack> baseDrops = new ArrayList<>(event.getDrops());
        if (baseDrops.isEmpty()) {
            return;
        }

        for (ItemStack drop : baseDrops) {
            if (RANDOM.nextDouble() < bonusChance) {
                event.getDrops().add(drop.clone());
            }
        }
    }
}
