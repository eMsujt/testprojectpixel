package com.skyblock.core.listener;

import com.skyblock.core.manager.EnchantingManager;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.enchant.EnchantItemEvent;

import java.util.Map;
import java.util.UUID;

/**
 * Records enchanting-table activity: whenever a player enchants an item, each
 * applied enchant is logged against their enchanting history.
 */
public final class EnchantListener implements Listener {

    private static final EnchantListener INSTANCE = new EnchantListener();

    private final EnchantingManager enchantingManager = EnchantingManager.getInstance();

    private EnchantListener() {}

    public static EnchantListener getInstance() {
        return INSTANCE;
    }

    @EventHandler
    public void onEnchantItem(EnchantItemEvent event) {
        Player player = event.getEnchanter();
        if (player == null) return;

        UUID uuid = player.getUniqueId();
        for (Map.Entry<Enchantment, Integer> entry : event.getEnchantsToAdd().entrySet()) {
            enchantingManager.recordEnchantingEvent(uuid,
                    "Enchanted " + entry.getKey().getKey().getKey() + " level " + entry.getValue());
        }
    }
}
