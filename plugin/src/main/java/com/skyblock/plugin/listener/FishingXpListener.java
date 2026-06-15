package com.skyblock.plugin.listener;

import com.skyblock.plugin.manager.ProfileManager;
import com.skyblock.plugin.profile.SkyBlockProfile;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerFishEvent;

/**
 * Awards Fishing XP directly to the player's {@link SkyBlockProfile} whenever
 * a fish item is caught ({@link PlayerFishEvent.State#CAUGHT_FISH}).
 */
public final class FishingXpListener implements Listener {

    private static final long FISHING_XP = 6L;

    @EventHandler
    public void onFish(PlayerFishEvent event) {
        if (event.getState() != PlayerFishEvent.State.CAUGHT_FISH) return;

        Player player = event.getPlayer();
        SkyBlockProfile profile = ProfileManager.getInstance().getOrCreateProfile(player.getUniqueId());
        profile.addSkillXp("fishing", FISHING_XP);
        XpActionBar.send(player, "fishing", FISHING_XP, profile.getSkillXp("fishing"));
    }
}
