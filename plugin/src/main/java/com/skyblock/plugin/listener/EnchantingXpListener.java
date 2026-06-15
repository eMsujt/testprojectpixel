package com.skyblock.plugin.listener;

import com.skyblock.plugin.manager.ProfileManager;
import com.skyblock.plugin.profile.SkyBlockProfile;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;

/**
 * Awards Enchanting XP directly to the player's {@link SkyBlockProfile} whenever
 * a player clicks the output slot of an enchanting table.
 */
public final class EnchantingXpListener implements Listener {

    @EventHandler
    public void onEnchant(InventoryClickEvent event) {
        if (event.getInventory().getType() != InventoryType.ENCHANTING) return;
        if (event.getRawSlot() != 0) return;
        if (!(event.getWhoClicked() instanceof Player player)) return;

        SkyBlockProfile profile = ProfileManager.getInstance().getOrCreateProfile(player.getUniqueId());
        profile.addSkillXp("enchanting", 3L);
    }
}
