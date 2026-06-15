package com.skyblock.plugin.listener;

import com.skyblock.plugin.managers.QuestManager;
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
        if (!questManager.hasActiveQuest(playerId)) {
            return;
        }
        QuestManager.Quest quest = questManager.getActiveQuest(playerId);
        if (quest == QuestManager.Quest.MINING_QUEST || quest == QuestManager.Quest.FARMING_QUEST
                || quest == QuestManager.Quest.FORAGING_QUEST) {
            player.sendMessage("[Quest] Block broken — keep going!");
        }
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        if (!(event.getEntity().getKiller() instanceof Player player)) {
            return;
        }
        UUID playerId = player.getUniqueId();
        if (!questManager.hasActiveQuest(playerId)) {
            return;
        }
        QuestManager.Quest quest = questManager.getActiveQuest(playerId);
        if (quest == QuestManager.Quest.SLAYER_QUEST || quest == QuestManager.Quest.COMBAT_QUEST
                || quest == QuestManager.Quest.DUNGEON_QUEST) {
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
        if (!questManager.hasActiveQuest(playerId)) {
            return;
        }
        if (questManager.getActiveQuest(playerId) == QuestManager.Quest.FISHING_QUEST) {
            player.sendMessage("[Quest] Fish caught — keep going!");
        }
    }
}
