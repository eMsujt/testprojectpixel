package com.skyblock.core.listener;

import com.skyblock.core.manager.SkillManager;
import com.skyblock.core.model.Rarity;
import com.skyblock.core.model.Skill;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerFishEvent;

import java.util.Map;
import java.util.UUID;

/**
 * Awards Fishing XP whenever a player reels in a catch, scaled by the rarity
 * of the caught item. Vanilla fish are {@link Rarity#COMMON}; treasure catches
 * (enchanted books, bows, name tags, nautilus shells, saddles) rank higher.
 */
public final class FishingListener implements Listener {

    private static final FishingListener INSTANCE = new FishingListener();

    private static final Map<Rarity, Long> RARITY_XP = Map.of(
            Rarity.COMMON,    3L,
            Rarity.UNCOMMON,  5L,
            Rarity.RARE,      8L,
            Rarity.EPIC,      12L,
            Rarity.LEGENDARY, 20L
    );

    private static final Map<Material, Rarity> CATCH_RARITY = Map.of(
            Material.ENCHANTED_BOOK, Rarity.EPIC,
            Material.NAUTILUS_SHELL, Rarity.EPIC,
            Material.BOW,            Rarity.RARE,
            Material.NAME_TAG,       Rarity.RARE,
            Material.SADDLE,         Rarity.RARE,
            Material.PUFFERFISH,     Rarity.UNCOMMON,
            Material.TROPICAL_FISH,  Rarity.UNCOMMON
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

        Player player = event.getPlayer();
        Rarity rarity = CATCH_RARITY.getOrDefault(caught.getItemStack().getType(), Rarity.COMMON);
        long xp = RARITY_XP.getOrDefault(rarity, 3L);

        UUID id = player.getUniqueId();
        int before = skillManager.getLevel(id, Skill.FISHING);
        skillManager.addXP(id, Skill.FISHING, xp);
        int after = skillManager.getLevel(id, Skill.FISHING);
        if (after > before) {
            skillManager.grantLevelUpRewards(id, Skill.FISHING, before, after);
            player.sendTitle("§aSkill Level Up!", "§eFishing §a→ §eLVL " + after, 10, 60, 20);
        }
    }
}
