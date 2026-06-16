package com.skyblock.plugin.listener;

import com.skyblock.plugin.profile.PlayerProfile;
import com.skyblock.plugin.profile.ProfileManager;
import com.skyblock.core.manager.SkillManager;
import com.skyblock.core.model.Skill;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerFishEvent;

import java.util.Map;
import java.util.UUID;

public final class FishingListener implements Listener {

    private static final Map<Material, Long> FISH_XP = Map.of(
            Material.RAW_COD,        5L,
            Material.RAW_SALMON,     6L,
            Material.TROPICAL_FISH,  6L,
            Material.PUFFERFISH,     8L
    );

    private static final Map<Material, String> FISH_COLLECTION = Map.of(
            Material.RAW_COD,        "cod",
            Material.RAW_SALMON,     "salmon",
            Material.TROPICAL_FISH,  "tropical_fish",
            Material.PUFFERFISH,     "pufferfish"
    );

    private final SkillManager skillManager = SkillManager.getInstance();

    @EventHandler
    public void onFish(PlayerFishEvent event) {
        if (event.getState() != PlayerFishEvent.State.CAUGHT_FISH) return;
        if (!(event.getCaught() instanceof Item caught)) return;

        Player player = event.getPlayer();
        Material type = caught.getItemStack().getType();
        long xp = FISH_XP.getOrDefault(type, 3L);

        UUID id = player.getUniqueId();
        int before = skillManager.getLevel(id, Skill.FISHING);
        skillManager.addXP(id, Skill.FISHING, xp);
        int after = skillManager.getLevel(id, Skill.FISHING);
        if (after > before) {
            skillManager.grantLevelUpRewards(id, Skill.FISHING, before, after);
            player.sendTitle("§aSkill Level Up!", "§eFishing §a→ §eLVL " + after, 10, 60, 20);
        }

        String col = FISH_COLLECTION.get(type);
        if (col != null) {
            PlayerProfile profile = ProfileManager.getInstance().getOrCreate(id);
            profile.addCollectionCount(col, 1L);
        }
    }
}
