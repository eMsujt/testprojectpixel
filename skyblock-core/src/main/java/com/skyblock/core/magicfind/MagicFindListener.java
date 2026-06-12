package com.skyblock.core.magicfind;

import com.skyblock.core.mob.MobLootManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Bukkit listener that applies Magic Find bonuses to mob drop chances.
 *
 * <p>When a player kills a mob, any drops that were added via
 * {@link MobLootManager} are re-evaluated with the killer's Magic Find so
 * that rare items have a proportionally higher chance of appearing.</p>
 */
public final class MagicFindListener implements Listener {

    private final MagicFindManager magicFindManager;
    private final MobLootManager mobLootManager;
    private final Random random = new Random();

    /**
     * Creates a listener backed by the given managers.
     *
     * @param magicFindManager the Magic Find manager, must not be null
     * @param mobLootManager   the mob loot manager, must not be null
     */
    public MagicFindListener(MagicFindManager magicFindManager, MobLootManager mobLootManager) {
        if (magicFindManager == null) {
            throw new IllegalArgumentException("magicFindManager must not be null");
        }
        if (mobLootManager == null) {
            throw new IllegalArgumentException("mobLootManager must not be null");
        }
        this.magicFindManager = magicFindManager;
        this.mobLootManager   = mobLootManager;
    }

    /**
     * Re-rolls non-guaranteed (rare) drops from the mob loot table with the
     * player's Magic Find applied to each entry's base chance.
     *
     * <p>Guaranteed drops (base chance == 1.0) are always included unchanged.
     * Only entries with a base chance below 1.0 have their probability
     * boosted by Magic Find.</p>
     *
     * @param event the entity death event
     */
    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        Player killer = event.getEntity().getKiller();
        if (killer == null) {
            return;
        }

        List<MobLootManager.LootEntry> entries = mobLootManager.getEntries(event.getEntityType());
        if (entries == null || entries.isEmpty()) {
            return;
        }

        double magicFind = magicFindManager.getMagicFind(killer.getUniqueId());
        if (magicFind <= 0) {
            return;
        }

        // Replace the vanilla/default drops with Magic-Find-adjusted rolls.
        List<ItemStack> drops = new ArrayList<>();
        for (MobLootManager.LootEntry entry : entries) {
            double adjustedChance = magicFindManager.applyToChance(entry.getChance(), magicFind);
            if (random.nextDouble() < adjustedChance) {
                drops.add(new ItemStack(entry.getMaterial(), entry.getAmount()));
            }
        }

        if (!drops.isEmpty()) {
            event.getDrops().clear();
            event.getDrops().addAll(drops);
        }
    }

    /**
     * Cleans up Magic Find data when a player disconnects.
     *
     * @param event the player quit event
     */
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        magicFindManager.remove(event.getPlayer().getUniqueId());
    }
}
