package com.skyblock.plugin.listener.skills;

import com.skyblock.plugin.profile.ProfileManager;
import com.skyblock.plugin.profile.PlayerProfile;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;

/**
 * Awards Carpentry XP directly to the player's {@link PlayerProfile} whenever
 * a player crafts an item.
 */
public final class CarpentryXpListener implements Listener {

    private static final long CRAFT_XP = 1L;

    @EventHandler
    public void onCraftItem(CraftItemEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;
        PlayerProfile profile = ProfileManager.getInstance().getOrCreate(player.getUniqueId());
        profile.addSkillXp("carpentry", CRAFT_XP);
        XpActionBar.send(player, "carpentry", CRAFT_XP, profile.getSkillXp("carpentry"));
    }
}
