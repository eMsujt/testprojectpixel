package com.skyblock.core.listener;

import com.skyblock.core.farming.manager.FarmingManager;
import com.skyblock.core.farming.manager.FarmingManager.CropType;
import com.skyblock.core.foraging.ForagingManager;
import com.skyblock.core.foraging.ForagingManager.TreeType;
import com.skyblock.core.manager.CollectionManager;
import com.skyblock.core.manager.SkillManager;
import com.skyblock.core.model.Skill;
import com.skyblock.core.util.ChatUtil;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import java.util.EnumMap;
import java.util.Map;
import java.util.UUID;

public final class GatheringListener implements Listener {

    private static final GatheringListener INSTANCE = new GatheringListener();

    private static final Map<Material, Long> ORE_XP = Map.ofEntries(
            Map.entry(Material.STONE,                    1L),
            Map.entry(Material.COBBLESTONE,              1L),
            Map.entry(Material.OBSIDIAN,                 5L),
            Map.entry(Material.COAL_ORE,                 5L),
            Map.entry(Material.DEEPSLATE_COAL_ORE,       5L),
            Map.entry(Material.IRON_ORE,                10L),
            Map.entry(Material.DEEPSLATE_IRON_ORE,      10L),
            Map.entry(Material.GOLD_ORE,                15L),
            Map.entry(Material.DEEPSLATE_GOLD_ORE,      15L),
            Map.entry(Material.REDSTONE_ORE,            20L),
            Map.entry(Material.DEEPSLATE_REDSTONE_ORE,  20L),
            Map.entry(Material.LAPIS_ORE,               25L),
            Map.entry(Material.DEEPSLATE_LAPIS_ORE,     25L),
            Map.entry(Material.EMERALD_ORE,             40L),
            Map.entry(Material.DEEPSLATE_EMERALD_ORE,   40L),
            Map.entry(Material.DIAMOND_ORE,             50L),
            Map.entry(Material.DEEPSLATE_DIAMOND_ORE,   50L),
            Map.entry(Material.NETHER_QUARTZ_ORE,       10L),
            Map.entry(Material.NETHER_GOLD_ORE,         15L)
    );

    private static final Map<Material, Long> CROP_XP = Map.ofEntries(
            Map.entry(Material.WHEAT,          4L),
            Map.entry(Material.CARROTS,        3L),
            Map.entry(Material.POTATOES,       3L),
            Map.entry(Material.BEETROOTS,      3L),
            Map.entry(Material.NETHER_WART,    5L),
            Map.entry(Material.MELON,          4L),
            Map.entry(Material.PUMPKIN,        5L),
            Map.entry(Material.COCOA,          3L),
            Map.entry(Material.SUGAR_CANE,     3L),
            Map.entry(Material.CACTUS,         2L)
    );

    private static final Map<Material, CropType> CROP_MAP = new EnumMap<>(Material.class);

    private static final Map<Material, Long> LOG_XP = Map.ofEntries(
            Map.entry(Material.OAK_LOG,                 6L),
            Map.entry(Material.STRIPPED_OAK_LOG,        6L),
            Map.entry(Material.BIRCH_LOG,               6L),
            Map.entry(Material.STRIPPED_BIRCH_LOG,      6L),
            Map.entry(Material.SPRUCE_LOG,              6L),
            Map.entry(Material.STRIPPED_SPRUCE_LOG,     6L),
            Map.entry(Material.JUNGLE_LOG,              8L),
            Map.entry(Material.STRIPPED_JUNGLE_LOG,     8L),
            Map.entry(Material.ACACIA_LOG,              8L),
            Map.entry(Material.STRIPPED_ACACIA_LOG,     8L),
            Map.entry(Material.DARK_OAK_LOG,            8L),
            Map.entry(Material.STRIPPED_DARK_OAK_LOG,   8L),
            Map.entry(Material.MANGROVE_LOG,           10L),
            Map.entry(Material.STRIPPED_MANGROVE_LOG,  10L),
            Map.entry(Material.CHERRY_LOG,             10L),
            Map.entry(Material.STRIPPED_CHERRY_LOG,    10L)
    );

    private static final Map<Material, TreeType> TREE_MAP = new EnumMap<>(Material.class);

    static {
        CROP_MAP.put(Material.WHEAT,          CropType.WHEAT);
        CROP_MAP.put(Material.CARROTS,        CropType.CARROT);
        CROP_MAP.put(Material.POTATOES,       CropType.POTATO);
        CROP_MAP.put(Material.PUMPKIN,        CropType.PUMPKIN);
        CROP_MAP.put(Material.MELON,          CropType.MELON);
        CROP_MAP.put(Material.SUGAR_CANE,     CropType.SUGAR_CANE);
        CROP_MAP.put(Material.COCOA,          CropType.COCOA_BEANS);
        CROP_MAP.put(Material.CACTUS,         CropType.CACTUS);
        CROP_MAP.put(Material.RED_MUSHROOM,   CropType.MUSHROOM);
        CROP_MAP.put(Material.BROWN_MUSHROOM, CropType.MUSHROOM);
        CROP_MAP.put(Material.NETHER_WART,    CropType.NETHER_WART);

        for (TreeType tree : TreeType.values()) {
            TREE_MAP.put(tree.getMaterial(), tree);
        }
    }

    private final SkillManager skillManager         = SkillManager.getInstance();
    private final CollectionManager collectionManager = CollectionManager.getInstance();
    private final FarmingManager farmingManager     = FarmingManager.getInstance();
    private final ForagingManager foragingManager   = ForagingManager.getInstance();

    private GatheringListener() {}

    public static GatheringListener getInstance() {
        return INSTANCE;
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (event.isCancelled()) return;
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();
        Material type = event.getBlock().getType();

        Long oreXp = ORE_XP.get(type);
        if (oreXp != null) {
            int before = skillManager.getLevel(uuid, Skill.MINING);
            skillManager.addXP(uuid, Skill.MINING, oreXp);
            int after = skillManager.getLevel(uuid, Skill.MINING);
            if (after > before) {
                player.sendTitle("§aSkill Level Up!", "§eMining §a→ §eLVL " + after, 10, 60, 20);
            }
            collectionManager.addCollection(uuid, type, 1);
            return;
        }

        Long cropXp = CROP_XP.get(type);
        CropType crop = CROP_MAP.get(type);
        if (cropXp != null || crop != null) {
            if (cropXp != null) {
                int before = skillManager.getLevel(uuid, Skill.FARMING);
                skillManager.addXP(uuid, Skill.FARMING, cropXp);
                int after = skillManager.getLevel(uuid, Skill.FARMING);
                if (after > before) {
                    player.sendTitle("§aSkill Level Up!", "§eFarming §a→ §eLVL " + after, 10, 60, 20);
                }
            }
            if (crop != null) {
                farmingManager.recordHarvest(uuid, crop, 1);
            }
            return;
        }

        Long logXp = LOG_XP.get(type);
        TreeType tree = TREE_MAP.get(type);
        if (logXp != null || tree != null) {
            if (logXp != null) {
                skillManager.addXP(uuid, Skill.FORAGING, logXp);
                collectionManager.addCollection(uuid, type, 1);
            }
            if (tree != null) {
                foragingManager.recordChop(uuid, tree, 1);
                ChatUtil.send(player, "§2[Foraging] §fYou gained §e+" + tree.getBaseXp() + " Foraging XP§f!");
            }
        }
    }
}
