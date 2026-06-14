package com.skyblock.plugin.listeners;

import com.skyblock.plugin.managers.QuestManager;
import com.skyblock.plugin.managers.QuestManager.Quest;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerFishEvent;

import java.util.EnumSet;
import java.util.Set;
import java.util.UUID;

public final class QuestProgressListener implements Listener {

    private static final Set<Material> MINING_BLOCKS = EnumSet.of(
            Material.STONE, Material.COBBLESTONE, Material.COAL_ORE, Material.IRON_ORE,
            Material.GOLD_ORE, Material.DIAMOND_ORE, Material.EMERALD_ORE, Material.LAPIS_ORE,
            Material.REDSTONE_ORE, Material.NETHER_QUARTZ_ORE
    );

    private static final Set<Material> FORAGING_BLOCKS = EnumSet.of(
            Material.OAK_LOG, Material.SPRUCE_LOG, Material.BIRCH_LOG, Material.JUNGLE_LOG,
            Material.ACACIA_LOG, Material.DARK_OAK_LOG
    );

    private static final Set<Material> FARMING_BLOCKS = EnumSet.of(
            Material.WHEAT, Material.CARROTS, Material.POTATOES, Material.BEETROOTS,
            Material.MELON, Material.PUMPKIN, Material.SUGAR_CANE, Material.CACTUS
    );

    private static final Set<EntityType> COMBAT_MOBS = EnumSet.of(
            EntityType.ZOMBIE, EntityType.SKELETON, EntityType.CREEPER, EntityType.SPIDER,
            EntityType.ENDERMAN, EntityType.BLAZE, EntityType.WITHER_SKELETON
    );

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        UUID id = player.getUniqueId();
        QuestManager qm = QuestManager.getInstance();
        if (!qm.hasActiveQuest(id)) {
            return;
        }
        Quest quest = qm.getActiveQuest(id);
        Material type = event.getBlock().getType();
        if (quest == Quest.MINING_QUEST && MINING_BLOCKS.contains(type)) {
            qm.completeQuest(id);
            player.sendMessage("Quest complete: Mining Quest!");
        } else if (quest == Quest.FORAGING_QUEST && FORAGING_BLOCKS.contains(type)) {
            qm.completeQuest(id);
            player.sendMessage("Quest complete: Foraging Quest!");
        } else if (quest == Quest.FARMING_QUEST && FARMING_BLOCKS.contains(type)) {
            qm.completeQuest(id);
            player.sendMessage("Quest complete: Farming Quest!");
        }
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        if (!(event.getEntity().getKiller() instanceof Player player)) {
            return;
        }
        UUID id = player.getUniqueId();
        QuestManager qm = QuestManager.getInstance();
        if (!qm.hasActiveQuest(id)) {
            return;
        }
        Quest quest = qm.getActiveQuest(id);
        EntityType entityType = event.getEntityType();
        if (quest == Quest.COMBAT_QUEST && COMBAT_MOBS.contains(entityType)) {
            qm.completeQuest(id);
            player.sendMessage("Quest complete: Combat Quest!");
        } else if (quest == Quest.SLAYER_QUEST && COMBAT_MOBS.contains(entityType)) {
            qm.completeQuest(id);
            player.sendMessage("Quest complete: Slayer Quest!");
        } else if (quest == Quest.DUNGEON_QUEST && entityType == EntityType.ZOMBIE) {
            qm.completeQuest(id);
            player.sendMessage("Quest complete: Dungeon Quest!");
        }
    }

    @EventHandler
    public void onPlayerFish(PlayerFishEvent event) {
        if (event.getState() != PlayerFishEvent.State.CAUGHT_FISH) {
            return;
        }
        UUID id = event.getPlayer().getUniqueId();
        QuestManager qm = QuestManager.getInstance();
        if (qm.hasActiveQuest(id) && qm.getActiveQuest(id) == Quest.FISHING_QUEST) {
            qm.completeQuest(id);
            event.getPlayer().sendMessage("Quest complete: Fishing Quest!");
        }
    }
}
