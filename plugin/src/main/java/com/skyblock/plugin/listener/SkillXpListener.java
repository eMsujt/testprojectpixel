package com.skyblock.plugin.listener;

import com.skyblock.core.manager.CollectionManager;
import com.skyblock.plugin.profile.PlayerProfile;
import com.skyblock.plugin.profile.ProfileManager;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.Ageable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityTameEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerFishEvent;

import java.util.Map;

/**
 * Awards skill XP on the player's {@link PlayerProfile} for the basic gathering
 * and combat actions of every skill. Consolidates the former per-skill
 * {@code *XpListener} stubs (Carpentry, Enchanting, Alchemy, Taming, Fishing,
 * Combat, Foraging, Farming and Mining) into a single grouped module.
 */
public final class SkillXpListener implements Listener {

    // --- Carpentry (crafting) ---
    private static final long CRAFT_XP = 1L;

    // --- Alchemy (brewing) ---
    private static final double POTION_XP = 22.5;

    // --- Taming (animal taming) ---
    private static final Map<EntityType, Long> TAME_XP = Map.of(
            EntityType.WOLF, 10L
    );

    // --- Fishing ---
    private static final long CAUGHT_FISH_XP = 50L;

    private static final Map<EntityType, Long> SEA_CREATURE_XP = Map.ofEntries(
            Map.entry(EntityType.SQUID,          8L),
            Map.entry(EntityType.GUARDIAN,       10L),
            Map.entry(EntityType.ELDER_GUARDIAN, 50L),
            Map.entry(EntityType.ZOMBIE,          5L),
            Map.entry(EntityType.DROWNED,         5L),
            Map.entry(EntityType.SKELETON,        8L),
            Map.entry(EntityType.WITCH,          18L),
            Map.entry(EntityType.IRON_GOLEM,    120L)
    );

    // --- Combat (mob kills) ---
    private static final Map<EntityType, String> MOB_DROP = Map.ofEntries(
            Map.entry(EntityType.ZOMBIE,          "ROTTEN_FLESH"),
            Map.entry(EntityType.SKELETON,        "BONE"),
            Map.entry(EntityType.CREEPER,         "GUNPOWDER"),
            Map.entry(EntityType.SPIDER,          "STRING"),
            Map.entry(EntityType.CAVE_SPIDER,     "STRING"),
            Map.entry(EntityType.ENDERMAN,        "ENDER_PEARL"),
            Map.entry(EntityType.BLAZE,           "BLAZE_ROD"),
            Map.entry(EntityType.GHAST,           "GHAST_TEAR"),
            Map.entry(EntityType.WITCH,           "GLASS_BOTTLE"),
            Map.entry(EntityType.SLIME,           "SLIMEBALL"),
            Map.entry(EntityType.MAGMA_CUBE,      "MAGMA_CREAM"),
            Map.entry(EntityType.WITHER_SKELETON, "WITHER_SKELETON_SKULL")
    );

    private static final Map<EntityType, Long> MOB_XP = Map.ofEntries(
            Map.entry(EntityType.ZOMBIE,          5L),
            Map.entry(EntityType.SKELETON,        5L),
            Map.entry(EntityType.CREEPER,         6L),
            Map.entry(EntityType.SPIDER,          4L),
            Map.entry(EntityType.CAVE_SPIDER,     5L),
            Map.entry(EntityType.ENDERMAN,       12L),
            Map.entry(EntityType.BLAZE,          10L),
            Map.entry(EntityType.GHAST,          15L),
            Map.entry(EntityType.WITCH,          10L),
            Map.entry(EntityType.SLIME,           3L),
            Map.entry(EntityType.MAGMA_CUBE,      5L),
            Map.entry(EntityType.WITHER_SKELETON,15L)
    );

    // --- Foraging (logs / leaves) ---
    private static final Map<Material, Long> LOG_XP = Map.ofEntries(
            Map.entry(Material.OAK_LOG,             6L),
            Map.entry(Material.BIRCH_LOG,           6L),
            Map.entry(Material.SPRUCE_LOG,          6L),
            Map.entry(Material.JUNGLE_LOG,          8L),
            Map.entry(Material.ACACIA_LOG,          8L),
            Map.entry(Material.DARK_OAK_LOG,        8L),
            Map.entry(Material.MANGROVE_LOG,       10L),
            Map.entry(Material.CHERRY_LOG,         10L),
            Map.entry(Material.OAK_LEAVES,          1L),
            Map.entry(Material.BIRCH_LEAVES,        1L),
            Map.entry(Material.SPRUCE_LEAVES,       1L),
            Map.entry(Material.JUNGLE_LEAVES,       1L),
            Map.entry(Material.ACACIA_LEAVES,       1L),
            Map.entry(Material.DARK_OAK_LEAVES,     1L),
            Map.entry(Material.MANGROVE_LEAVES,     1L),
            Map.entry(Material.CHERRY_LEAVES,       1L)
    );

    private static final Map<Material, String> LOG_DROP = Map.ofEntries(
            Map.entry(Material.OAK_LOG,          "OAK_LOG"),
            Map.entry(Material.SPRUCE_LOG,       "SPRUCE_LOG"),
            Map.entry(Material.BIRCH_LOG,        "BIRCH_LOG"),
            Map.entry(Material.JUNGLE_LOG,       "JUNGLE_LOG"),
            Map.entry(Material.ACACIA_LOG,       "ACACIA_LOG"),
            Map.entry(Material.DARK_OAK_LOG,     "DARK_OAK_LOG"),
            Map.entry(Material.MANGROVE_LOG,     "MANGROVE_LOG"),
            Map.entry(Material.CHERRY_LOG,       "CHERRY_LOG"),
            Map.entry(Material.OAK_LEAVES,       "OAK_LEAVES"),
            Map.entry(Material.BIRCH_LEAVES,     "BIRCH_LEAVES"),
            Map.entry(Material.SPRUCE_LEAVES,    "SPRUCE_LEAVES"),
            Map.entry(Material.JUNGLE_LEAVES,    "JUNGLE_LEAVES"),
            Map.entry(Material.ACACIA_LEAVES,    "ACACIA_LEAVES"),
            Map.entry(Material.DARK_OAK_LEAVES,  "DARK_OAK_LEAVES"),
            Map.entry(Material.MANGROVE_LEAVES,  "MANGROVE_LEAVES"),
            Map.entry(Material.CHERRY_LEAVES,    "CHERRY_LEAVES")
    );

    // --- Farming (crops) ---
    private static final Map<Material, Long> CROP_XP = Map.ofEntries(
            Map.entry(Material.WHEAT,                3L),
            Map.entry(Material.POTATOES,             3L),
            Map.entry(Material.CARROTS,              3L),
            Map.entry(Material.BEETROOTS,            3L),
            Map.entry(Material.NETHER_WART,          5L),
            Map.entry(Material.PUMPKIN,              8L),
            Map.entry(Material.MELON,                2L),
            Map.entry(Material.SUGAR_CANE,           2L),
            Map.entry(Material.CACTUS,               2L),
            Map.entry(Material.COCOA,                3L),
            Map.entry(Material.MUSHROOM_STEM,        3L),
            Map.entry(Material.RED_MUSHROOM_BLOCK,   3L),
            Map.entry(Material.BROWN_MUSHROOM_BLOCK, 3L)
    );

    private static final Map<Material, String> CROP_DROP = Map.ofEntries(
            Map.entry(Material.WHEAT,                "wheat"),
            Map.entry(Material.POTATOES,             "potato"),
            Map.entry(Material.CARROTS,              "carrot"),
            Map.entry(Material.BEETROOTS,            "beetroot"),
            Map.entry(Material.NETHER_WART,          "nether_wart"),
            Map.entry(Material.PUMPKIN,              "pumpkin"),
            Map.entry(Material.MELON,                "melon_slice"),
            Map.entry(Material.SUGAR_CANE,           "sugar_cane"),
            Map.entry(Material.CACTUS,               "cactus"),
            Map.entry(Material.COCOA,                "cocoa_beans"),
            Map.entry(Material.MUSHROOM_STEM,        "mushroom_stem"),
            Map.entry(Material.RED_MUSHROOM_BLOCK,   "red_mushroom"),
            Map.entry(Material.BROWN_MUSHROOM_BLOCK, "brown_mushroom")
    );

    // --- Mining (ores / stone) ---
    private static final Map<Material, Long> MINING_XP = Map.ofEntries(
            Map.entry(Material.STONE,                     1L),
            Map.entry(Material.COBBLESTONE,               1L),
            Map.entry(Material.COAL_ORE,                  5L),
            Map.entry(Material.DEEPSLATE_COAL_ORE,        5L),
            Map.entry(Material.IRON_ORE,                 10L),
            Map.entry(Material.DEEPSLATE_IRON_ORE,       10L),
            Map.entry(Material.GOLD_ORE,                 18L),
            Map.entry(Material.DEEPSLATE_GOLD_ORE,       18L),
            Map.entry(Material.NETHER_GOLD_ORE,           4L),
            Map.entry(Material.DIAMOND_ORE,              25L),
            Map.entry(Material.DEEPSLATE_DIAMOND_ORE,    25L),
            Map.entry(Material.LAPIS_ORE,                 5L),
            Map.entry(Material.DEEPSLATE_LAPIS_ORE,       5L),
            Map.entry(Material.EMERALD_ORE,              28L),
            Map.entry(Material.DEEPSLATE_EMERALD_ORE,    28L),
            Map.entry(Material.REDSTONE_ORE,              5L),
            Map.entry(Material.DEEPSLATE_REDSTONE_ORE,   5L),
            Map.entry(Material.NETHER_QUARTZ_ORE,         4L)
    );

    private static final Map<Material, String> MINING_DROP = Map.ofEntries(
            Map.entry(Material.STONE,                     "COBBLESTONE"),
            Map.entry(Material.COBBLESTONE,               "COBBLESTONE"),
            Map.entry(Material.COAL_ORE,                  "COAL"),
            Map.entry(Material.DEEPSLATE_COAL_ORE,        "COAL"),
            Map.entry(Material.IRON_ORE,                  "RAW_IRON"),
            Map.entry(Material.DEEPSLATE_IRON_ORE,        "RAW_IRON"),
            Map.entry(Material.GOLD_ORE,                  "RAW_GOLD"),
            Map.entry(Material.DEEPSLATE_GOLD_ORE,        "RAW_GOLD"),
            Map.entry(Material.NETHER_GOLD_ORE,           "RAW_GOLD"),
            Map.entry(Material.DIAMOND_ORE,               "DIAMOND"),
            Map.entry(Material.DEEPSLATE_DIAMOND_ORE,     "DIAMOND"),
            Map.entry(Material.LAPIS_ORE,                 "LAPIS_LAZULI"),
            Map.entry(Material.DEEPSLATE_LAPIS_ORE,       "LAPIS_LAZULI"),
            Map.entry(Material.EMERALD_ORE,               "EMERALD"),
            Map.entry(Material.DEEPSLATE_EMERALD_ORE,     "EMERALD"),
            Map.entry(Material.REDSTONE_ORE,              "REDSTONE"),
            Map.entry(Material.DEEPSLATE_REDSTONE_ORE,    "REDSTONE"),
            Map.entry(Material.NETHER_QUARTZ_ORE,         "QUARTZ")
    );

    @EventHandler
    public void onCraftItem(CraftItemEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;
        PlayerProfile profile = ProfileManager.getInstance().getOrCreate(player.getUniqueId());
        profile.addSkillXp("carpentry", CRAFT_XP);
        XpActionBar.send(player, "carpentry", CRAFT_XP, profile.getSkillXp("carpentry"));
    }

    @EventHandler
    public void onEnchant(EnchantItemEvent event) {
        if (!(event.getEnchanter() instanceof Player player)) return;

        double xp = event.getExpLevelCost();
        PlayerProfile profile = ProfileManager.getInstance().getOrCreate(player.getUniqueId());
        profile.addSkillXp("enchanting", xp);
        XpActionBar.send(player, "enchanting", xp, profile.getSkillXp("enchanting"));
    }

    @EventHandler
    public void onAlchemy(InventoryClickEvent event) {
        if (event.getInventory().getType() != InventoryType.BREWING) return;
        // Slots 0-2 are the three output potion slots of a brewing stand
        if (event.getRawSlot() < 0 || event.getRawSlot() > 2) return;
        if (event.getCurrentItem() == null || event.getCurrentItem().getType().isAir()) return;
        if (!(event.getWhoClicked() instanceof Player player)) return;

        PlayerProfile profile = ProfileManager.getInstance().getOrCreate(player.getUniqueId());
        profile.addSkillXp("alchemy", POTION_XP);
        XpActionBar.send(player, "alchemy", POTION_XP, profile.getSkillXp("alchemy"));
    }

    @EventHandler
    public void onEntityTame(EntityTameEvent event) {
        Long xp = TAME_XP.get(event.getEntityType());
        if (xp == null) return;
        if (!(event.getOwner() instanceof Player player)) return;
        PlayerProfile profile = ProfileManager.getInstance().getOrCreate(player.getUniqueId());
        profile.addSkillXp("taming", xp);
        XpActionBar.send(player, "taming", xp, profile.getSkillXp("taming"));
    }

    @EventHandler
    public void onFish(PlayerFishEvent event) {
        Player player = event.getPlayer();
        Entity caught = event.getCaught();

        if (event.getState() == PlayerFishEvent.State.CAUGHT_FISH) {
            if (!(caught instanceof Item item)) return;
            Material type = item.getItemStack().getType();
            PlayerProfile profile = ProfileManager.getInstance().getOrCreate(player.getUniqueId());
            profile.addSkillXp("fishing", CAUGHT_FISH_XP);
            XpActionBar.send(player, "fishing", CAUGHT_FISH_XP, profile.getSkillXp("fishing"));
            CollectionManager.getInstance().addItems(player.getUniqueId(), type.name(), 1);

        } else if (event.getState() == PlayerFishEvent.State.CAUGHT_ENTITY && caught != null) {
            Long xp = SEA_CREATURE_XP.get(caught.getType());
            if (xp == null) return;
            PlayerProfile profile = ProfileManager.getInstance().getOrCreate(player.getUniqueId());
            profile.addSkillXp("fishing", xp);
            XpActionBar.send(player, "fishing", xp, profile.getSkillXp("fishing"));
        }
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        Player killer = event.getEntity().getKiller();
        if (killer == null) {
            return;
        }
        Long xp = MOB_XP.get(event.getEntityType());
        if (xp == null) {
            return;
        }
        PlayerProfile profile = ProfileManager.getInstance()
                .getOrCreate(killer.getUniqueId());
        profile.addSkillXp("combat", xp);
        XpActionBar.send(killer, "combat", xp, profile.getSkillXp("combat"));
        String drop = MOB_DROP.get(event.getEntityType());
        if (drop == null) return;
        profile.addCollectionCount(drop, 1L);
    }

    @EventHandler
    public void onForagingBlockBreak(BlockBreakEvent event) {
        Material type = event.getBlock().getType();
        Long xp = LOG_XP.get(type);
        if (xp == null) {
            return;
        }
        Player player = event.getPlayer();
        PlayerProfile profile = ProfileManager.getInstance().getOrCreate(player.getUniqueId());
        profile.addSkillXp("foraging", xp);
        XpActionBar.send(player, "foraging", xp, profile.getSkillXp("foraging"));
        CollectionManager.getInstance().addItems(player.getUniqueId(), LOG_DROP.get(type), 1);
    }

    @EventHandler
    public void onFarmingBlockBreak(BlockBreakEvent event) {
        Block block = event.getBlock();
        Long xp = CROP_XP.get(block.getType());
        if (xp == null || !isMature(block)) {
            return;
        }
        Player player = event.getPlayer();
        PlayerProfile profile = ProfileManager.getInstance().getOrCreate(player.getUniqueId());
        profile.addSkillXp("farming", xp);
        XpActionBar.send(player, "farming", xp, profile.getSkillXp("farming"));
        CollectionManager.getInstance().addItems(player.getUniqueId(), CROP_DROP.get(block.getType()), 1);
    }

    @EventHandler
    public void onMiningBlockBreak(BlockBreakEvent event) {
        Material type = event.getBlock().getType();
        Long xp = MINING_XP.get(type);
        if (xp == null) {
            return;
        }
        Player player = event.getPlayer();
        PlayerProfile profile = ProfileManager.getInstance().getOrCreate(player.getUniqueId());
        profile.addSkillXp("mining", xp);
        XpActionBar.send(player, "mining", xp, profile.getSkillXp("mining"));
        CollectionManager.getInstance().addItems(player.getUniqueId(), MINING_DROP.get(type), 1);
    }

    private static boolean isMature(Block block) {
        if (block.getBlockData() instanceof Ageable ageable) {
            return ageable.getAge() >= ageable.getMaximumAge();
        }
        return true;
    }
}
