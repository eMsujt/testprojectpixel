package com.skyblock.core.listener;

import com.skyblock.core.manager.SlayerManager;
import com.skyblock.core.manager.SlayerManager.SlayerQuest;
import com.skyblock.core.manager.SlayerManager.SlayerReward;
import com.skyblock.core.manager.SlayerManager.SlayerType;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

import java.util.EnumMap;
import java.util.Map;
import java.util.UUID;

public final class SlayerListener implements Listener {

    private static final SlayerListener INSTANCE = new SlayerListener();

    private static final Map<EntityType, SlayerType> MOB_TO_SLAYER = new EnumMap<>(EntityType.class);

    static {
        MOB_TO_SLAYER.put(EntityType.ZOMBIE,   SlayerType.ZOMBIE);
        MOB_TO_SLAYER.put(EntityType.ZOMBIE_VILLAGER, SlayerType.ZOMBIE);
        MOB_TO_SLAYER.put(EntityType.SPIDER,   SlayerType.SPIDER);
        MOB_TO_SLAYER.put(EntityType.CAVE_SPIDER, SlayerType.SPIDER);
        MOB_TO_SLAYER.put(EntityType.WOLF,     SlayerType.WOLF);
        MOB_TO_SLAYER.put(EntityType.ENDERMAN, SlayerType.ENDERMAN);
        MOB_TO_SLAYER.put(EntityType.BLAZE,    SlayerType.BLAZE);
    }

    private final SlayerManager slayerManager = SlayerManager.getInstance();

    private SlayerListener() {}

    public static SlayerListener getInstance() {
        return INSTANCE;
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        Player killer = event.getEntity().getKiller();
        if (killer == null) return;

        UUID uuid = killer.getUniqueId();
        SlayerQuest quest = slayerManager.getActiveQuest(uuid);
        if (quest == null || quest.isComplete()) return;

        SlayerType questType = quest.type;

        // Boss kill: entity named after this player's slayer boss
        if (slayerManager.isBossActive(uuid)) {
            SlayerManager.BossFight fight = slayerManager.getBossFight(uuid);
            if (fight != null && fight.isDead()) {
                SlayerReward reward = slayerManager.killBoss(uuid);
                killer.sendMessage("§c§lSlayer Boss slain! §r§7(+" + reward.getXp() + " XP)");
                if (!reward.getDrops().isEmpty()) {
                    killer.sendMessage("§6Drops: §e" + String.join("§7, §e", reward.getDrops()));
                }
            }
            return;
        }

        // Regular quest mob kill: only count mobs matching the quest type
        SlayerType mobType = MOB_TO_SLAYER.get(event.getEntity().getType());
        if (mobType != questType) return;

        int kills = slayerManager.addQuestKill(uuid);
        int required = SlayerManager.KILLS_TO_SPAWN_BOSS.getOrDefault(quest.tier, Integer.MAX_VALUE);
        killer.sendMessage("§7[Slayer] §fKills: §e" + kills + "§7/§e" + required);

        if (slayerManager.canSpawnBoss(uuid)) {
            killer.sendMessage("§c§lYou can now summon the Slayer Boss! §r§7Use /slayer to open the menu.");
        }
    }
}
