package com.skyblock.plugin.listener;

import com.skyblock.plugin.manager.ProfileManager;
import com.skyblock.plugin.profile.SkyBlockProfile;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.inventory.ItemStack;

/**
 * Awards Carpentry XP directly to the player's {@link SkyBlockProfile} whenever
 * a player crafts an item. XP equals the number of non-empty slots in the
 * crafting matrix (1–9).
 */
public final class CarpentryXpListener implements Listener {

    @EventHandler
    public void onCraftItem(CraftItemEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;
        int filledSlots = 0;
        for (ItemStack slot : event.getInventory().getMatrix()) {
            if (slot != null && slot.getType() != Material.AIR) {
                filledSlots++;
            }
        }
        if (filledSlots == 0) return;
        long xp = filledSlots;
        SkyBlockProfile profile = ProfileManager.getInstance().getOrCreateProfile(player.getUniqueId());
        profile.addSkillXp("carpentry", xp);
        XpActionBar.send(player, "carpentry", xp, profile.getSkillXp("carpentry"));
    }
}
