package com.skyblock.core.listener;

import com.skyblock.core.manager.FishingManager;
import com.skyblock.core.manager.SkillManager;
import com.skyblock.core.model.Skill;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerFishEvent;

import java.util.UUID;

public final class CompactListeners implements Listener {

    private static final CompactListeners INSTANCE = new CompactListeners();

    private final SkillManager skillManager = SkillManager.getInstance();

    private CompactListeners() {}

    public static CompactListeners getInstance() {
        return INSTANCE;
    }

    @EventHandler
    public void onPlayerFish(PlayerFishEvent event) {
        if (event.getState() != PlayerFishEvent.State.CAUGHT_FISH) return;
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();

        long xp = FishingManager.XP_PER_CATCH;

        int before = skillManager.getLevel(uuid, Skill.FISHING);
        skillManager.addXP(uuid, Skill.FISHING, xp);
        int after = skillManager.getLevel(uuid, Skill.FISHING);

        if (after > before) {
            player.sendTitle("§aSkill Level Up!", "§eFishing §a→ §eLVL " + after, 10, 60, 20);
        }
    }
}
