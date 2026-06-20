package com.skyblock.core.listener;

import com.skyblock.core.combat.calculator.DamageFormula;
import com.skyblock.core.farming.manager.FarmingManager;
import com.skyblock.core.farming.manager.FarmingManager.CropType;
import com.skyblock.core.foraging.ForagingManager;
import com.skyblock.core.foraging.ForagingManager.TreeType;
import com.skyblock.core.manager.AlchemyManager;
import com.skyblock.core.manager.CollectionManager;
import com.skyblock.core.manager.FishingManager;
import com.skyblock.core.manager.SkillManager;
import com.skyblock.core.manager.StatManager;
import com.skyblock.core.model.Skill;
import com.skyblock.core.model.Stat;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.inventory.BrewEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Collection;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Consolidated listener for all skill XP events: Mining, Farming, Foraging,
 * Fishing, Combat, and Alchemy.
 */
public final class SkillListener implements Listener {

    private static final SkillListener INSTANCE = new SkillListener();

    private static final double XP_PER_BREW = 10.0;

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

    private static final Map<EntityType, Long> COMBAT_XP;

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

        Map<EntityType, Long> m = new EnumMap<>(EntityType.class);
        m.put(EntityType.ZOMBIE,           5L);
        m.put(EntityType.SKELETON,         5L);
        m.put(EntityType.SPIDER,           5L);
        m.put(EntityType.CREEPER,          5L);
        m.put(EntityType.WITCH,           10L);
        m.put(EntityType.BLAZE,           20L);
        m.put(EntityType.ENDERMAN,        30L);
        m.put(EntityType.WITHER_SKELETON, 25L);
        m.put(EntityType.CAVE_SPIDER,      5L);
        m.put(EntityType.SILVERFISH,       2L);
        COMBAT_XP = m;
    }

    private final SkillManager skillManager       = SkillManager.getInstance();
    private final CollectionManager collectionManager = CollectionManager.getInstance();
    private final FishingManager fishingManager   = FishingManager.getInstance();
    private final StatManager statManager         = StatManager.getInstance();
    private final AlchemyManager alchemyManager   = AlchemyManager.getInstance();
    private final FarmingManager farmingManager   = FarmingManager.getInstance();
    private final ForagingManager foragingManager = ForagingManager.getInstance();

    private final Map<Location, UUID> brewerMap = new HashMap<>();

    private SkillListener() {}

    public static SkillListener getInstance() {
        return INSTANCE;
    }

    // --- Mining / Farming / Foraging ---

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
                player.sendMessage("§2[Foraging] §fYou gained §e+" + tree.getBaseXp() + " Foraging XP§f!");
            }
        }
    }

    // --- Fishing ---

    @EventHandler
    public void onPlayerFish(PlayerFishEvent event) {
        if (event.getState() != PlayerFishEvent.State.CAUGHT_FISH) return;
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();

        int level = fishingManager.getLevel(uuid);
        ItemStack loot = fishingManager.rollLoot(level);

        boolean isTreasure = loot.getType() == Material.MAP;
        double xp = isTreasure ? FishingManager.XP_TREASURE : FishingManager.XP_PER_CATCH;
        fishingManager.addXp(uuid, xp);
        fishingManager.addFishCaught(uuid);

        player.getWorld().dropItemNaturally(event.getHook().getLocation(), loot);

        FishingManager.SeaCreature creature = fishingManager.rollSeaCreature(level);
        String summary = "Caught " + loot.getType().name()
                + (creature != null ? " + sea creature: " + creature.name() : "");
        fishingManager.recordCatchEvent(uuid, summary);

        player.sendMessage("§9[Fishing] §fYou caught §e" + loot.getType().name().replace('_', ' ')
                + "§f! §7(+" + (int) xp + " XP)"
                + (creature != null ? " §c+ " + creature.name().replace('_', ' ') : ""));
    }

    // --- Combat ---

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        Entity attacker = event.getDamager();
        if (!(attacker instanceof Player)) return;
        UUID uuid = attacker.getUniqueId();
        double strength   = statManager.getStat(uuid, Stat.STRENGTH);
        double critChance = statManager.getStat(uuid, Stat.CRIT_CHANCE);
        double critDamage = statManager.getStat(uuid, Stat.CRIT_DAMAGE);
        event.setDamage(DamageFormula.calculate(event.getDamage(), strength, critChance, critDamage));
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        LivingEntity entity = event.getEntity();
        Player killer = entity.getKiller();
        if (killer == null) return;
        Long xp = COMBAT_XP.get(entity.getType());
        if (xp == null) return;
        skillManager.addXP(killer.getUniqueId(), Skill.COMBAT, xp);
    }

    // --- Alchemy ---

    @EventHandler
    public void onInventoryOpen(InventoryOpenEvent event) {
        if (event.getInventory().getType() != InventoryType.BREWING) return;
        Location loc = event.getInventory().getLocation();
        if (loc != null) brewerMap.put(loc, event.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (event.getInventory().getType() != InventoryType.BREWING) return;
        Location loc = event.getInventory().getLocation();
        if (loc != null) brewerMap.remove(loc);
    }

    @EventHandler
    public void onBrew(BrewEvent event) {
        Location loc = event.getBlock().getLocation();
        UUID uuid = brewerMap.get(loc);
        if (uuid != null) alchemyManager.addXp(uuid, XP_PER_BREW);
        alchemyManager.processCompletedJobs(System.currentTimeMillis());
    }

    // --- Enchanting ---

    @EventHandler
    public void onEnchantItem(EnchantItemEvent event) {
        Player player = event.getEnchanter();
        UUID uuid = player.getUniqueId();
        long xp = (long) event.getExpLevelCost() * 3L;
        if (xp <= 0) return;
        int before = skillManager.getLevel(uuid, Skill.ENCHANTING);
        skillManager.addXP(uuid, Skill.ENCHANTING, xp);
        int after = skillManager.getLevel(uuid, Skill.ENCHANTING);
        if (after > before) {
            player.sendTitle("§aSkill Level Up!", "§eEnchanting §a→ §eLVL " + after, 10, 60, 20);
        }
    }

    public static double calculateDamage(Player attacker, ItemStack weapon, Entity target) {
        StatManager stats = StatManager.getInstance();
        UUID attackerId = attacker.getUniqueId();
        double weaponDamage = getWeaponDamage(weapon);
        double strength   = stats.getStat(attackerId, Stat.STRENGTH);
        double critChance = stats.getStat(attackerId, Stat.CRIT_CHANCE);
        double critDamage = stats.getStat(attackerId, Stat.CRIT_DAMAGE);
        double damage = DamageFormula.calculate(weaponDamage, strength, critChance, critDamage);
        if (target instanceof Player defender) {
            UUID defenderId = defender.getUniqueId();
            double defense     = stats.getStat(defenderId, Stat.DEFENSE);
            double trueDefense = stats.getStat(defenderId, Stat.TRUE_DEFENSE);
            damage *= (1.0 - defense / (defense + 100.0));
            damage = Math.max(0.0, damage - trueDefense);
        }
        return damage;
    }

    public static double calculateDamage(double weaponDamage, double strength,
                                         double critChancePercent, double critDamagePercent) {
        return DamageFormula.calculate(weaponDamage, strength, critChancePercent, critDamagePercent);
    }

    private static double getWeaponDamage(ItemStack weapon) {
        if (weapon == null) return 0.0;
        ItemMeta meta = weapon.getItemMeta();
        if (meta == null) return 0.0;
        Collection<AttributeModifier> mods = meta.getAttributeModifiers(Attribute.ATTACK_DAMAGE);
        if (mods == null || mods.isEmpty()) return 0.0;
        double total = 0.0;
        for (AttributeModifier mod : mods) total += mod.getAmount();
        return Math.max(0.0, total);
    }
}
