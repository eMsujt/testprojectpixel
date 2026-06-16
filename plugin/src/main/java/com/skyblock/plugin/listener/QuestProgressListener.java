package com.skyblock.plugin.listener;

import com.skyblock.core.quest.manager.QuestManager;
import com.skyblock.core.quest.manager.QuestManager.QuestStatus;
import com.skyblock.core.quest.manager.QuestManager.QuestType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerFishEvent;

import java.util.UUID;

public final class QuestProgressListener implements Listener {

    private final QuestManager questManager = QuestManager.getInstance();

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        UUID playerId = player.getUniqueId();
        if (questManager.getStatus(playerId, QuestType.MINE_ORES) == QuestStatus.IN_PROGRESS
                || questManager.getStatus(playerId, QuestType.MINE_500_BLOCKS) == QuestStatus.IN_PROGRESS) {
            player.sendMessage("[Quest] Block broken — keep going!");
        }
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        if (!(event.getEntity().getKiller() instanceof Player player)) {
            return;
        }
        UUID playerId = player.getUniqueId();
        if (questManager.getStatus(playerId, QuestType.KILL_MOBS) == QuestStatus.IN_PROGRESS
                || questManager.getStatus(playerId, QuestType.KILL_100_MOBS) == QuestStatus.IN_PROGRESS
                || questManager.getStatus(playerId, QuestType.COMPLETE_DUNGEONS) == QuestStatus.IN_PROGRESS) {
            player.sendMessage("[Quest] Kill registered — keep going!");
        }
    }

    @EventHandler
    public void onFish(PlayerFishEvent event) {
        if (event.getState() != PlayerFishEvent.State.CAUGHT_FISH) {
            return;
        }
        Player player = event.getPlayer();
        UUID playerId = player.getUniqueId();
        if (questManager.getStatus(playerId, QuestType.CATCH_FISH) == QuestStatus.IN_PROGRESS
                || questManager.getStatus(playerId, QuestType.FISH_50_FISH) == QuestStatus.IN_PROGRESS) {
            player.sendMessage("[Quest] Fish caught — keep going!");
        }
    }
}
