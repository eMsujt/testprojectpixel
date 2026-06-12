package com.skyblock.core.enchant;

import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;
import java.util.UUID;

/**
 * Bukkit listener that applies SkyBlock enchant effects to game events.
 *
 * <p>Handles:</p>
 * <ul>
 *   <li>{@link EntityDamageByEntityEvent} — adjusts damage via
 *       {@link SkyBlockEnchantManager#applyCombatEnchants}</li>
 *   <li>{@link BlockBreakEvent} — applies FORTUNE drop multiplier and
 *       TELEKINESIS (drops sent directly to inventory)</li>
 * </ul>
 */
public final class SkyBlockEnchantListener implements Listener {

    private final SkyBlockEnchantManager enchantManager;

    /**
     * Creates the listener backed by the given manager.
     *
     * @param enchantManager the enchant manager, must not be null
     */
    public SkyBlockEnchantListener(SkyBlockEnchantManager enchantManager) {
        if (enchantManager == null) {
            throw new IllegalArgumentException("enchantManager must not be null");
        }
        this.enchantManager = enchantManager;
    }

    /**
     * Adjusts damage dealt by player attackers based on their active combat enchants.
     *
     * @param event the damage event
     */
    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        Entity attacker = event.getDamager();
        if (!(attacker instanceof Player)) {
            return;
        }

        Entity target = event.getEntity();
        double healthFraction = 0.0;
        if (target instanceof LivingEntity) {
            LivingEntity living = (LivingEntity) target;
            double maxHealth = living.getMaxHealth();
            healthFraction = maxHealth > 0 ? living.getHealth() / maxHealth : 0.0;
        }

        UUID attackerId = attacker.getUniqueId();
        double adjusted = enchantManager.applyCombatEnchants(
                attackerId, event.getDamage(), healthFraction);
        event.setDamage(adjusted);
    }

    /**
     * Applies FORTUNE multiplier to block drops and TELEKINESIS to redirect
     * drops straight into the breaker's inventory.
     *
     * @param event the block-break event
     */
    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        UUID playerId = player.getUniqueId();

        boolean telekinesis = enchantManager.hasTelekinesis(playerId);
        double fortuneMultiplier = enchantManager.getFortuneMultiplier(playerId);

        if (!telekinesis && fortuneMultiplier <= 1.0) {
            return;
        }

        if (telekinesis) {
            // Suppress natural drops; add them directly to inventory
            event.setDropItems(false);
            Collection<ItemStack> drops = event.getBlock().getDrops(
                    player.getInventory().getItemInMainHand());
            for (ItemStack drop : drops) {
                if (fortuneMultiplier > 1.0) {
                    int boosted = (int) Math.round(drop.getAmount() * fortuneMultiplier);
                    drop.setAmount(boosted);
                }
                player.getInventory().addItem(drop);
            }
        } else {
            // FORTUNE only: boost natural drops post-break via scheduled task workaround
            // is not available here without a plugin reference, so we apply to the
            // explicit drops collection (covers most cases; auto-drops stay vanilla)
            Collection<ItemStack> drops = event.getBlock().getDrops(
                    player.getInventory().getItemInMainHand());
            event.setDropItems(false);
            for (ItemStack drop : drops) {
                int boosted = (int) Math.round(drop.getAmount() * fortuneMultiplier);
                drop.setAmount(Math.max(1, boosted));
                event.getBlock().getWorld().dropItemNaturally(
                        event.getBlock().getLocation(), drop);
            }
        }
    }
}
