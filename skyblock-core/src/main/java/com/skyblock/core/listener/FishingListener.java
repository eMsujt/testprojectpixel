package com.skyblock.core.listener;

import com.skyblock.core.manager.SkillManager;
import com.skyblock.core.model.Skill;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerFishEvent;

import java.util.Map;

public final class FishingListener implements Listener {

    private static final FishingListener INSTANCE = new FishingListener();

    private static final Map<Material, Long> FISH_XP = Map.of(
            Material.RAW_COD,       5L,
            Material.RAW_SALMON,    6L,
            Material.TROPICAL_FISH, 6L,
            Material.PUFFERFISH,    8L
    );

    private final SkillManager skillManager = SkillManager.getInstance();

    private FishingListener() {}

    public static FishingListener getInstance() {
        return INSTANCE;
    }

    @EventHandler
    public void onFish(PlayerFishEvent event) {
        if (event.getState() != PlayerFishEvent.State.CAUGHT_FISH) return;
        if (!(event.getCaught() instanceof Item caught)) return;
        Material type = caught.getItemStack().getType();
        long xp = FISH_XP.getOrDefault(type, 3L);
        skillManager.addXP(event.getPlayer().getUniqueId(), Skill.FISHING, xp);
    }
}
