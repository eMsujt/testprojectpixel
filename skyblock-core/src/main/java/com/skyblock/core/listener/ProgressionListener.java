package com.skyblock.core.listener;

import com.skyblock.core.manager.FairySoulManager;
import com.skyblock.core.manager.FairySoulManager.FairyIsland;
import com.skyblock.core.manager.SkillManager;
import com.skyblock.core.model.Skill;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Consolidated progression listener covering skill XP on block-break and
 * fairy soul collection on right-click.
 *
 * <p>Fairy soul blocks must carry two PDC entries set when placed:
 * {@code fairy_soul_island} (String, a {@link FairyIsland} name) and
 * {@code fairy_soul_index} (Integer, 1-based index within that island).</p>
 */
public final class ProgressionListener implements Listener {

    private final SkillManager skillManager;
    private final FairySoulManager fairySoulManager;
    private final NamespacedKey islandKey;
    private final NamespacedKey indexKey;

    public ProgressionListener(SkillManager skillManager, FairySoulManager fairySoulManager, JavaPlugin plugin) {
        if (skillManager == null) throw new IllegalArgumentException("skillManager must not be null");
        if (fairySoulManager == null) throw new IllegalArgumentException("fairySoulManager must not be null");
        if (plugin == null) throw new IllegalArgumentException("plugin must not be null");
        this.skillManager = skillManager;
        this.fairySoulManager = fairySoulManager;
        this.islandKey = new NamespacedKey(plugin, "fairy_soul_island");
        this.indexKey = new NamespacedKey(plugin, "fairy_soul_index");
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Skill skill = skillFor(event.getBlock().getType());
        skillManager.addXP(player.getUniqueId(), skill, 1L);
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getClickedBlock() == null) return;
        org.bukkit.event.block.Action action = event.getAction();
        if (action != org.bukkit.event.block.Action.RIGHT_CLICK_BLOCK) return;

        Block block = event.getClickedBlock();
        PersistentDataContainer pdc = block.getChunk().getPersistentDataContainer();
        String islandName = pdc.get(islandKey, PersistentDataType.STRING);
        Integer soulIndex = pdc.get(indexKey, PersistentDataType.INTEGER);
        if (islandName == null || soulIndex == null) return;

        FairyIsland island;
        try {
            island = FairyIsland.valueOf(islandName);
        } catch (IllegalArgumentException e) {
            return;
        }

        Player player = event.getPlayer();
        if (fairySoulManager.collectSoul(player.getUniqueId(), island, soulIndex)) {
            player.sendMessage("§d§lFairy Soul §r§dcollected! §7("
                    + fairySoulManager.getFoundCount(player.getUniqueId())
                    + "/" + fairySoulManager.getTotalSouls() + ")");
        }
    }

    private static Skill skillFor(Material material) {
        String name = material.name();
        if (name.endsWith("_LOG") || name.endsWith("_WOOD")) {
            return Skill.FORAGING;
        }
        if (name.endsWith("_CROP") || name.equals("WHEAT") || name.equals("CARROTS")
                || name.equals("POTATOES") || name.equals("BEETROOTS")
                || name.equals("NETHER_WART") || name.equals("PUMPKIN")
                || name.equals("MELON") || name.equals("COCOA")
                || name.equals("SUGAR_CANE")) {
            return Skill.FARMING;
        }
        return Skill.MINING;
    }
}
