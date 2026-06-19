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

public final class ForagingListener implements Listener {

    private static final ForagingListener INSTANCE = new ForagingListener();

    private static final Map<Material, Long> LOG_XP = Map.ofEntries(
            Map.entry(Material.OAK_LOG,                6L),
            Map.entry(Material.STRIPPED_OAK_LOG,       6L),
            Map.entry(Material.BIRCH_LOG,              6L),
            Map.entry(Material.STRIPPED_BIRCH_LOG,     6L),
            Map.entry(Material.SPRUCE_LOG,             6L),
            Map.entry(Material.STRIPPED_SPRUCE_LOG,    6L),
            Map.entry(Material.JUNGLE_LOG,             8L),
            Map.entry(Material.STRIPPED_JUNGLE_LOG,    8L),
            Map.entry(Material.ACACIA_LOG,             8L),
            Map.entry(Material.STRIPPED_ACACIA_LOG,    8L),
            Map.entry(Material.DARK_OAK_LOG,           8L),
            Map.entry(Material.STRIPPED_DARK_OAK_LOG,  8L),
            Map.entry(Material.MANGROVE_LOG,          10L),
            Map.entry(Material.STRIPPED_MANGROVE_LOG, 10L),
            Map.entry(Material.CHERRY_LOG,            10L),
            Map.entry(Material.STRIPPED_CHERRY_LOG,   10L)
    );

    private final SkillManager skillManager = SkillManager.getInstance();
    private final CollectionManager collectionManager = CollectionManager.getInstance();

    private ForagingListener() {}

    public static ForagingListener getInstance() {
        return INSTANCE;
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (event.isCancelled() || event.getPlayer() == null) {
            return;
        }
        Long xp = LOG_XP.get(event.getBlock().getType());
        if (xp == null) {
            return;
        }
        UUID uuid = event.getPlayer().getUniqueId();
        skillManager.addXP(uuid, Skill.FORAGING, xp);
        collectionManager.addCollection(uuid, event.getBlock().getType(), 1);
    }
}
