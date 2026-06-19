package com.skyblock.core.listener;

import com.skyblock.core.manager.CollectionManager;
import com.skyblock.core.manager.SkillManager;
import com.skyblock.core.model.Skill;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import java.util.Map;
import java.util.UUID;

public final class ExperienceListener implements Listener {

    private static final ExperienceListener INSTANCE = new ExperienceListener();

    private static final Map<Material, Long> CROP_XP = Map.of(
            Material.WHEAT,       3L,
            Material.CARROTS,     3L,
            Material.POTATOES,    3L,
            Material.BEETROOTS,   3L,
            Material.NETHER_WART, 5L,
            Material.MELON,       2L,
            Material.PUMPKIN,     5L,
            Material.COCOA,       3L,
            Material.SUGAR_CANE,  3L
    );

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

    private ExperienceListener() {}

    public static ExperienceListener getInstance() {
        return INSTANCE;
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (event.isCancelled() || event.getPlayer() == null) {
            return;
        }
        Material type = event.getBlock().getType();
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();

        Long cropXp = CROP_XP.get(type);
        if (cropXp != null) {
            int before = skillManager.getLevel(uuid, Skill.FARMING);
            skillManager.addXP(uuid, Skill.FARMING, cropXp);
            int after = skillManager.getLevel(uuid, Skill.FARMING);
            if (after > before) {
                player.sendTitle("§aSkill Level Up!", "§eFarming §a→ §eLVL " + after, 10, 60, 20);
            }
            return;
        }

        Long logXp = LOG_XP.get(type);
        if (logXp != null) {
            skillManager.addXP(uuid, Skill.FORAGING, logXp);
            collectionManager.addCollection(uuid, type, 1);
        }
    }
}
