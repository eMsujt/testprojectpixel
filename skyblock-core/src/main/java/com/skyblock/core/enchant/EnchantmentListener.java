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
 * Applies enchantment effects to game events, delegating calculations to
 * {@link SkyBlockEnchantManager}.
 */
public final class EnchantmentListener implements Listener {

    private final SkyBlockEnchantManager enchantManager;

    public EnchantmentListener(SkyBlockEnchantManager enchantManager) {
        if (enchantManager == null) {
            throw new IllegalArgumentException("enchantManager must not be null");
        }
        this.enchantManager = enchantManager;
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        Entity attacker = event.getDamager();
        if (!(attacker instanceof Player)) {
            return;
        }

        Entity target = event.getEntity();
        double healthFraction = 0.0;
        if (target instanceof LivingEntity living) {
            double maxHealth = living.getMaxHealth();
            healthFraction = maxHealth > 0 ? living.getHealth() / maxHealth : 0.0;
        }

        UUID attackerId = attacker.getUniqueId();
        double adjusted = enchantManager.applyCombatEnchants(attackerId, event.getDamage(), healthFraction);
        event.setDamage(adjusted);
    }

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
