package com.skyblock.plugin.listener;

import com.skyblock.plugin.profile.PlayerProfile;
import com.skyblock.plugin.profile.ProfileManager;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerFishEvent;

import java.util.Map;
import java.util.Set;

public final class FishingListener implements Listener {

    private static final Set<Material> COMMON_FISH = Set.of(
            Material.COD,
            Material.SALMON,
            Material.TROPICAL_FISH,
            Material.PUFFERFISH
    );

    private static final Map<Material, String> FISH_COLLECTION = Map.of(
            Material.COD,            "cod",
            Material.SALMON,         "salmon",
            Material.TROPICAL_FISH,  "tropical_fish",
            Material.PUFFERFISH,     "pufferfish"
    );

    @EventHandler
    public void onFish(PlayerFishEvent event) {
        if (event.getState() != PlayerFishEvent.State.CAUGHT_FISH) return;
        if (!(event.getCaught() instanceof Item caught)) return;

        Player player = event.getPlayer();
        PlayerProfile profile = ProfileManager.getInstance().getOrCreate(player.getUniqueId());
        Material type = caught.getItemStack().getType();
        long xp = COMMON_FISH.contains(type) ? 5L : 20L;
        profile.addSkillXp("fishing", xp);
        String col = FISH_COLLECTION.get(type);
        if (col != null) profile.addCollectionCount(col, 1L);
    }
}
