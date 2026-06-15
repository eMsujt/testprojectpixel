package com.skyblock.plugin.listener;

import com.skyblock.plugin.manager.ProfileManager;
import com.skyblock.plugin.profile.SkyBlockProfile;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;

/**
 * Awards Enchanting XP on the player's {@link SkyBlockProfile} whenever they
 * click the output slot of an enchanting-table inventory, indicating they have
 * collected an enchanted item.
 */
public final class EnchantingXpListener implements Listener {

    private static final int OUTPUT_SLOT = 0;
    private static final long ENCHANT_XP  = 3L;

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getInventory().getType() != InventoryType.ENCHANTING) {
            return;
        }
        if (!(event.getWhoClicked() instanceof Player player)) {
            return;
        }
        if (event.getRawSlot() != OUTPUT_SLOT) {
            return;
        }
        if (event.getCurrentItem() == null || event.getCurrentItem().getType().isAir()) {
            return;
        }
        SkyBlockProfile profile = ProfileManager.getInstance().getOrCreateProfile(player.getUniqueId());
        profile.addSkillXp("enchanting", ENCHANT_XP);
    }
}
