package com.skyblock.core.listener;

import com.skyblock.core.manager.FishingManager;
import com.skyblock.core.manager.FishingManager.SeaCreature;
import com.skyblock.core.util.ChatUtil;
import com.skyblock.core.manager.FishingManager.WaterType;
import com.skyblock.core.manager.SkillManager;
import com.skyblock.core.model.Skill;
import org.bukkit.Material;
import org.bukkit.entity.FishHook;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public final class FishingListener implements Listener {

    private static final FishingListener INSTANCE = new FishingListener();

    private final FishingManager fishingManager = FishingManager.getInstance();
    private final SkillManager skillManager = SkillManager.getInstance();

    private FishingListener() {}

    public static FishingListener getInstance() {
        return INSTANCE;
    }

    @EventHandler
    public void onPlayerFish(PlayerFishEvent event) {
        if (event.getState() != PlayerFishEvent.State.CAUGHT_FISH) return;
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();

        int level = fishingManager.getLevel(uuid);
        ItemStack loot = fishingManager.rollLoot(level);

        boolean isTreasure = loot.getType() == Material.MAP;
        double xp = isTreasure ? FishingManager.XP_TREASURE : FishingManager.XP_PER_CATCH;
        fishingManager.addXp(uuid, xp);
        fishingManager.addFishCaught(uuid);

        int before = skillManager.getLevel(uuid, Skill.FISHING);
        skillManager.addXP(uuid, Skill.FISHING, (long) xp);
        int after = skillManager.getLevel(uuid, Skill.FISHING);

        player.getWorld().dropItemNaturally(event.getHook().getLocation(), loot);
        fishingManager.recordCatchEvent(uuid, "Caught " + loot.getType().name());
        ChatUtil.send(player, "§9[Fishing] §fYou caught §e" + loot.getType().name().replace('_', ' ')
                + "§f! §7(+" + (int) xp + " XP)");

        if (after > before) {
            player.sendTitle("§aSkill Level Up!", "§eFishing §a→ §eLVL " + after, 10, 60, 20);
        }

        WaterType waterType = detectWaterType(event.getHook());
        SeaCreature creature = fishingManager.rollSeaCreature(level, waterType, 0.0);
        if (creature != null) {
            ChatUtil.send(player, "§3[Sea Creature] §fA §b" + creature.name().replace('_', ' ')
                    + " §fhas spawned from the " + waterType.name().toLowerCase() + "!");
            fishingManager.recordCatchEvent(uuid, "Sea creature: " + creature.name()
                    + " [" + waterType.name() + "]");
        }
    }

    private static WaterType detectWaterType(FishHook hook) {
        Material block = hook.getLocation().getBlock().getType();
        if (block == Material.LAVA || block == Material.LAVA_CAULDRON) {
            return WaterType.LAVA;
        }
        return WaterType.WATER;
    }
}
