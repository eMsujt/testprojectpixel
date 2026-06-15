package com.skyblock.plugin.listener;

import com.skyblock.plugin.manager.CollectionManager;
import com.skyblock.plugin.manager.ProfileManager;
import com.skyblock.plugin.profile.SkyBlockProfile;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerFishEvent;

import java.util.Map;

/**
 * Awards Fishing XP directly to the player's {@link SkyBlockProfile} whenever
 * a fish item is caught ({@link PlayerFishEvent.State#CAUGHT_FISH}).
 */
public final class FishingXpListener implements Listener {

    private static final Map<Material, Long> FISH_XP = Map.of(
            Material.RAW_COD,       50L,
            Material.RAW_SALMON,    80L,
            Material.TROPICAL_FISH, 10L,
            Material.PUFFERFISH,    10L
    );

    private static final Map<Material, String> FISH_DROP = Map.of(
            Material.RAW_COD,       "RAW_COD",
            Material.RAW_SALMON,    "RAW_SALMON",
            Material.TROPICAL_FISH, "TROPICAL_FISH",
            Material.PUFFERFISH,    "PUFFERFISH"
    );

    @EventHandler
    public void onFish(PlayerFishEvent event) {
        if (event.getState() != PlayerFishEvent.State.CAUGHT_FISH) return;
        if (!(event.getCaught() instanceof Item caught)) return;

        Material type = caught.getItemStack().getType();
        long xp = FISH_XP.getOrDefault(type, 3L);

        Player player = event.getPlayer();
        SkyBlockProfile profile = ProfileManager.getInstance().getOrCreateProfile(player.getUniqueId());
        profile.addSkillXp("fishing", xp);
        XpActionBar.send(player, "fishing", xp, profile.getSkillXp("fishing"));
        String drop = FISH_DROP.get(type);
        if (drop != null) {
            CollectionManager.getInstance().addCount(player.getUniqueId(), drop, 1);
        }
    }
}
