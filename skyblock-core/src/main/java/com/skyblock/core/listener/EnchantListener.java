package com.skyblock.core.listener;

import com.skyblock.core.manager.EnchantingManager;
import com.skyblock.core.manager.EnchantingManager.SkyBlockEnchantment;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.enchantment.EnchantItemEvent;

import java.util.Map;
import java.util.UUID;

public final class EnchantListener implements Listener {

    private static final EnchantListener INSTANCE = new EnchantListener();

    private final EnchantingManager enchantingManager = EnchantingManager.getInstance();

    private EnchantListener() {}

    public static EnchantListener getInstance() {
        return INSTANCE;
    }

    @EventHandler
    public void onEnchantItem(EnchantItemEvent event) {
        UUID playerId = event.getEnchanter().getUniqueId();
        for (Map.Entry<Enchantment, Integer> entry : event.getEnchantsToAdd().entrySet()) {
            String key = entry.getKey().getKey().getKey().toUpperCase();
            int level = entry.getValue();
            try {
                SkyBlockEnchantment type = SkyBlockEnchantment.valueOf(key);
                int clamped = Math.min(level, type.getMaxLevel());
                try {
                    enchantingManager.setEnchantment(playerId, type, clamped);
                } catch (IllegalArgumentException ignored) {
                    enchantingManager.recordEnchantingEvent(playerId,
                            "Enchanted " + type.name() + " level " + clamped + " (conflict skipped)");
                }
            } catch (IllegalArgumentException ignored) {
                enchantingManager.recordEnchantingEvent(playerId,
                        "Enchanted " + key + " level " + level);
            }
        }
    }
}
