package com.skyblock.core.listener;

import com.skyblock.core.combat.calculator.DamageFormula;
import com.skyblock.core.manager.CollectionManager;
import com.skyblock.core.manager.FishingManager;
import com.skyblock.core.manager.SkillManager;
import com.skyblock.core.manager.StatManager;
import com.skyblock.core.model.Skill;
import com.skyblock.core.model.Stat;
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
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Collection;
import java.util.EnumMap;
import java.util.Map;
import java.util.UUID;

/**
 * Consolidated skill-XP listener handling Mining, Fishing and Combat events.
 */
public final class SkillXpListener implements Listener {

    private static final SkillXpListener INSTANCE = new SkillXpListener();

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

    private static final Map<EntityType, Long> COMBAT_XP;

    static {
        Map<EntityType, Long> m = new EnumMap<>(EntityType.class);
        m.put(EntityType.ZOMBIE,    5L);
        m.put(EntityType.SKELETON,  5L);
        m.put(EntityType.SPIDER,    5L);
        m.put(EntityType.CREEPER,   5L);
        m.put(EntityType.WITCH,    10L);
        m.put(EntityType.BLAZE,    20L);
        m.put(EntityType.ENDERMAN, 30L);
        m.put(EntityType.WITHER_SKELETON, 25L);
        m.put(EntityType.CAVE_SPIDER, 5L);
        m.put(EntityType.SILVERFISH,  2L);
        COMBAT_XP = m;
    }

    private final SkillManager skillManager = SkillManager.getInstance();
    private final CollectionManager collectionManager = CollectionManager.getInstance();
    private final FishingManager fishingManager = FishingManager.getInstance();
    private final StatManager statManager = StatManager.getInstance();

    private SkillXpListener() {}

    public static SkillXpListener getInstance() {
        return INSTANCE;
    }

    // --- Mining ---

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

    // --- Fishing ---

    @EventHandler
    public void onPlayerFish(PlayerFishEvent event) {
        if (event.getState() != PlayerFishEvent.State.CAUGHT_FISH) {
            return;
        }

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
        if (!(attacker instanceof Player)) {
            return;
        }

        UUID uuid = attacker.getUniqueId();
        double strength   = statManager.getStat(uuid, Stat.STRENGTH);
        double critChance = statManager.getStat(uuid, Stat.CRIT_CHANCE);
        double critDamage = statManager.getStat(uuid, Stat.CRIT_DAMAGE);

        double weaponDamage = event.getDamage();
        event.setDamage(DamageFormula.calculate(weaponDamage, strength, critChance, critDamage));
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        LivingEntity entity = event.getEntity();
        Player killer = entity.getKiller();
        if (killer == null) {
            return;
        }
        Long xp = COMBAT_XP.get(entity.getType());
        if (xp == null) {
            return;
        }
        skillManager.addXP(killer.getUniqueId(), Skill.COMBAT, xp);
    }

    public static double calculateDamage(Player attacker, ItemStack weapon, Entity target) {
        StatManager stats = StatManager.getInstance();
        UUID attackerId = attacker.getUniqueId();

        double weaponDamage = getWeaponDamage(weapon);
        double strength   = stats.getStat(attackerId, Stat.STRENGTH);
        double critChance = stats.getStat(attackerId, Stat.CRIT_CHANCE);
        double critDamage = stats.getStat(attackerId, Stat.CRIT_DAMAGE);

        double damage = DamageFormula.calculate(weaponDamage, strength, critChance, critDamage);

        if (target instanceof Player) {
            UUID defenderId = target.getUniqueId();
            double defense     = stats.getStat(defenderId, Stat.DEFENSE);
            double trueDefense = stats.getStat(defenderId, Stat.TRUE_DEFENSE);
            damage *= (1.0 - defense / (defense + 100.0));
            damage = Math.max(0.0, damage - trueDefense);
        }

        return damage;
    }

    public static double calculateDamage(double weaponDamage, double strength, double critChancePercent, double critDamagePercent) {
        return DamageFormula.calculate(weaponDamage, strength, critChancePercent, critDamagePercent);
    }

    private static double getWeaponDamage(ItemStack weapon) {
        if (weapon == null) {
            return 0.0;
        }
        ItemMeta meta = weapon.getItemMeta();
        if (meta == null) {
            return 0.0;
        }
        Collection<AttributeModifier> mods = meta.getAttributeModifiers(Attribute.ATTACK_DAMAGE);
        if (mods == null || mods.isEmpty()) {
            return 0.0;
        }
        double total = 0.0;
        for (AttributeModifier mod : mods) {
            total += mod.getAmount();
        }
        return Math.max(0.0, total);
    }
}
