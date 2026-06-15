package com.skyblock.plugin.listener;

import com.skyblock.plugin.manager.ProfileManager;
import com.skyblock.plugin.profile.SkyBlockProfile;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;

/**
 * Awards Carpentry XP directly to the player's {@link SkyBlockProfile} whenever
 * a player crafts an item.
 */
public final class CarpentryXpListener implements Listener {

    private static final long CRAFT_XP = 1L;

    @EventHandler
    public void onCraftItem(CraftItemEvent.Post event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;
        SkyBlockProfile profile = ProfileManager.getInstance().getOrCreateProfile(player.getUniqueId());
        profile.addSkillXp("carpentry", CRAFT_XP);
        XpActionBar.send(player, "carpentry", CRAFT_XP, profile.getSkillXp("carpentry"));
    }
}
