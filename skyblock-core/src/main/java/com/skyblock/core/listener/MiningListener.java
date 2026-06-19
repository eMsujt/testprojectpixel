package com.skyblock.core.listener;

import com.skyblock.core.manager.CollectionManager;
import com.skyblock.core.manager.SkillManager;
import com.skyblock.core.model.Skill;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import java.util.Map;
import java.util.UUID;

public final class MiningListener implements Listener {

    private static final MiningListener INSTANCE = new MiningListener();

    private static final Map<Material, Long> ORE_XP = Map.ofEntries(
            Map.entry(Material.COAL_ORE,               5L),
            Map.entry(Material.DEEPSLATE_COAL_ORE,     5L),
            Map.entry(Material.IRON_ORE,              10L),
            Map.entry(Material.DEEPSLATE_IRON_ORE,    10L),
            Map.entry(Material.GOLD_ORE,              15L),
            Map.entry(Material.DEEPSLATE_GOLD_ORE,    15L),
            Map.entry(Material.REDSTONE_ORE,          20L),
            Map.entry(Material.DEEPSLATE_REDSTONE_ORE, 20L),
            Map.entry(Material.LAPIS_ORE,             25L),
            Map.entry(Material.DEEPSLATE_LAPIS_ORE,   25L),
            Map.entry(Material.EMERALD_ORE,           40L),
            Map.entry(Material.DEEPSLATE_EMERALD_ORE, 40L),
            Map.entry(Material.DIAMOND_ORE,           50L),
            Map.entry(Material.DEEPSLATE_DIAMOND_ORE, 50L),
            Map.entry(Material.NETHER_QUARTZ_ORE,     10L),
            Map.entry(Material.NETHER_GOLD_ORE,       15L)
    );

    private final SkillManager skillManager = SkillManager.getInstance();
    private final CollectionManager collectionManager = CollectionManager.getInstance();

    private MiningListener() {}

    public static MiningListener getInstance() {
        return INSTANCE;
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Long xp = ORE_XP.get(event.getBlock().getType());
        if (xp == null) {
            return;
        }
        UUID uuid = event.getPlayer().getUniqueId();
        skillManager.addXP(uuid, Skill.MINING, xp);
        collectionManager.addCollection(uuid, event.getBlock().getType(), 1);
    }
}
