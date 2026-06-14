package com.skyblock.plugin.listeners;

import com.skyblock.plugin.managers.SkillsManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.enchantment.EnchantItemEvent;

/**
 * Bukkit listener that intercepts {@link EnchantItemEvent} and awards
 * Enchanting skill XP equal to the sum of the applied enchantment levels.
 */
public final class EnchantingListener implements Listener {

    private final SkillsManager skillsManager = SkillsManager.getInstance();

    @EventHandler
    public void onEnchantItem(EnchantItemEvent event) {
        int xp = 0;
        for (int level : event.getEnchantsToAdd().values()) {
            xp += level;
        }
        if (xp <= 0) {
            return;
        }
        Player player = event.getEnchanter();
        skillsManager.addSkillXP(player.getUniqueId(), "enchanting", xp);
    }
}
