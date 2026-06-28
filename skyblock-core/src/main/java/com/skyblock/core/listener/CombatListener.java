package com.skyblock.core.listener;

import com.skyblock.core.SkyBlockCore;
import com.skyblock.core.combat.calculator.CombatEngine;
import com.skyblock.core.manager.GardenManager;
import com.skyblock.core.manager.StatManager;
import com.skyblock.core.model.Stat;
import com.skyblock.core.stats.CombatStatsManager;
import com.skyblock.core.talisman.manager.TalismanManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Applies the full SkyBlock damage formula to melee combat and records combat
 * statistics.
 *
 * <p>When a player deals melee damage, the event damage is recomputed from the
 * attacker's Strength, Crit Chance, and Crit Damage stats via {@link CombatEngine}.
 * When a player takes damage, it is reduced by the SkyBlock defense formula
 * ({@code damage × 100 / (100 + defense)}).</p>
 *
 * <p>Damage dealt/taken and kills/deaths are also recorded into
 * {@link CombatStatsManager}.</p>
 *
 * <p>This consolidated listener additionally feeds harvested Garden crops into
 * the player's composter as organic matter (formerly {@code CompostListener}),
 * tracked through the canonical composter state on {@link GardenManager}. Fuel
 * and the actual compost-processing step are handled separately (via
 * {@code /compost}); this listener only accumulates organic matter, so it does
 * not duplicate the Farming-XP handling done by the consolidated skill listener.</p>
 */
public final class CombatListener implements Listener {

    private static final CombatListener INSTANCE = new CombatListener();

    /** Organic matter contributed to the composter per harvested crop block. */
    private static final Map<Material, Long> ORGANIC_MATTER = Map.of(
            Material.WHEAT,       2L,
            Material.CARROTS,     2L,
            Material.POTATOES,    2L,
            Material.BEETROOTS,   2L,
            Material.NETHER_WART, 3L,
            Material.MELON,       1L,
            Material.PUMPKIN,     3L,
            Material.COCOA,       2L,
            Material.SUGAR_CANE,  2L
    );

    private CombatListener() {}

    public static CombatListener getInstance() {
        return INSTANCE;
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        StatManager stats = StatManager.getInstance();
        boolean isCrit = false;

        if (event.getDamager() instanceof Player) {
            Player attacker = (Player) event.getDamager();
            double strength = stats.getStat(attacker.getUniqueId(), Stat.STRENGTH);
            double critChance = stats.getStat(attacker.getUniqueId(), Stat.CRIT_CHANCE);
            double critDamage = stats.getStat(attacker.getUniqueId(), Stat.CRIT_DAMAGE);
            isCrit = CombatEngine.rollCrit(critChance);
            double base = weaponBaseDamage(attacker, event.getDamage());
            double damage = CombatEngine.applyStrength(base, strength);
            if (isCrit) {
                damage = CombatEngine.applyCrit(damage, critDamage);
            }
            // Enchant damage bucket: Sharpness (all mobs) + Smite/Bane of Arthropods/Ender Slayer
            // (by mob family), each +5% per level, read from the held weapon's lore (item-based,
            // values from the bundled 1:1 data). One additive bucket, multiplied once.
            double enchantPercent = enchantDamagePercent(
                    attacker.getInventory().getItemInMainHand(), event.getEntity(),
                    stats.getStat(attacker.getUniqueId(), Stat.HEALTH));
            if (enchantPercent > 0.0) {
                damage *= 1.0 + enchantPercent / 100.0;
            }
            // Ferocity: every 100 grants a guaranteed extra hit, the remainder a chance for one more.
            // Combined into one event (same total damage) to avoid re-triggering this handler.
            double ferocity = stats.getStat(attacker.getUniqueId(), Stat.FEROCITY);
            int hits = 1 + (int) (ferocity / 100.0);
            if (Math.random() * 100.0 < ferocity % 100.0) {
                hits++;
            }
            damage *= hits;
            event.setDamage(Math.max(0.0, damage));
        }

        // Custom mobs hit players for their full SkyBlock damage, not the tiny
        // vanilla base. Set it before the player's defense is applied below.
        if (!(event.getDamager() instanceof Player)
                && event.getDamager() instanceof org.bukkit.entity.LivingEntity mobAttacker) {
            com.skyblock.core.manager.MobManager.MobDefinition mobDef =
                    com.skyblock.core.mob.CustomMobManager.getInstance().getDefinition(mobAttacker.getUniqueId());
            if (mobDef != null) {
                event.setDamage(mobDef.getDamage());
            }
        }

        if (event.getEntity() instanceof Player) {
            Player defender = (Player) event.getEntity();
            double defense = stats.getStat(defender.getUniqueId(), Stat.DEFENSE);
            double reduced = event.getDamage() * 100.0 / (100.0 + Math.max(0.0, defense));
            event.setDamage(reduced);
        }

        CombatStatsManager combatStats = CombatStatsManager.getInstance();
        double finalDamage = event.getFinalDamage();
        if (event.getDamager() instanceof Player) {
            combatStats.addDamageDealt(((Player) event.getDamager()).getUniqueId(), finalDamage);
        }
        if (event.getEntity() instanceof Player) {
            combatStats.addDamageTaken(((Player) event.getEntity()).getUniqueId(), finalDamage);
        }

        spawnDamageIndicator(event.getEntity().getLocation(), finalDamage, isCrit);

        // Custom mobs carry their real (often huge) SkyBlock health in a side pool,
        // since the vanilla max-health attribute caps at ~1024. Drain that pool by
        // the dealt damage, suppress the vanilla hit, and kill on empty so the
        // death rewards/drops still fire.
        if (!(event.getEntity() instanceof Player)
                && event.getEntity() instanceof org.bukkit.entity.LivingEntity victim) {
            com.skyblock.core.mob.CustomMobManager mobs = com.skyblock.core.mob.CustomMobManager.getInstance();
            if (mobs.isCustomMob(victim.getUniqueId())) {
                boolean dead = mobs.damageSkyblock(victim, event.getDamage());
                event.setDamage(0.0);
                if (dead) {
                    victim.setHealth(0.0);
                }
            }
        }
    }

    private static final Pattern WEAPON_DAMAGE = Pattern.compile("^Damage:\\s*\\+?([0-9,]+)");

    /**
     * Base melee damage from the player's held weapon: {@code 5 + weapon Damage stat} read from the
     * item's lore (1:1 SkyBlock base), falling back to the vanilla damage for non-SkyBlock items.
     */
    private static double weaponBaseDamage(Player attacker, double vanillaDamage) {
        ItemStack weapon = attacker.getInventory().getItemInMainHand();
        ItemMeta meta = weapon.getItemMeta();
        if (meta == null || meta.getLore() == null) {
            return vanillaDamage;
        }
        for (String line : meta.getLore()) {
            Matcher m = WEAPON_DAMAGE.matcher(ChatColor.stripColor(line).trim());
            if (m.find()) {
                try {
                    return 5.0 + Double.parseDouble(m.group(1).replace(",", ""));
                } catch (NumberFormatException ignored) {
                }
            }
        }
        return vanillaDamage;
    }

    /** Mob families targeted by the type-specific damage enchants. */
    private static final Set<EntityType> UNDEAD = EnumSet.of(
            EntityType.ZOMBIE, EntityType.ZOMBIE_VILLAGER, EntityType.HUSK, EntityType.DROWNED,
            EntityType.SKELETON, EntityType.STRAY, EntityType.WITHER_SKELETON, EntityType.ZOMBIFIED_PIGLIN,
            EntityType.ZOGLIN, EntityType.PHANTOM, EntityType.WITHER, EntityType.SKELETON_HORSE,
            EntityType.ZOMBIE_HORSE);
    private static final Set<EntityType> ARTHROPODS = EnumSet.of(
            EntityType.SPIDER, EntityType.CAVE_SPIDER, EntityType.SILVERFISH, EntityType.BEE);
    private static final Set<EntityType> ENDER = EnumSet.of(
            EntityType.ENDERMAN, EntityType.ENDERMITE, EntityType.ENDER_DRAGON, EntityType.SHULKER);

    /** Lower-case enchant display name → enum key, for the damage enchants we apply. */
    private static final Map<String, String> DAMAGE_ENCHANTS = Map.of(
            "sharpness", "SHARPNESS",
            "smite", "SMITE",
            "bane of arthropods", "BANE_OF_ARTHROPODS",
            "ender slayer", "ENDER_SLAYER",
            "execute", "EXECUTE",
            "prosecute", "PROSECUTE",
            "giant killer", "GIANT_KILLER");

    // Per-level rates from the bundled 1:1 data (item_tooltips.properties), index = level-1.
    /** Execute: +rate% damage per 1% of the target's MISSING health. */
    private static final double[] EXECUTE_RATE   = {0.2, 0.4, 0.6, 0.8, 1.0, 1.25};
    /** Prosecute: +rate% damage per 1% of the target's CURRENT health. */
    private static final double[] PROSECUTE_RATE = {0.1, 0.2, 0.3, 0.4, 0.7, 1.0};
    /** Giant Killer: +rate% per 1% the target's max health exceeds yours, capped. */
    private static final double[] GIANT_KILLER_RATE = {0.1, 0.2, 0.3, 0.4, 0.6, 0.9, 1.2};
    private static final double[] GIANT_KILLER_CAP  = {5,   10,  15,  20,  30,  45,  65};

    /**
     * Total damage-% from the held weapon's enchants against the given target. Sharpness applies to
     * every mob; Smite/Bane of Arthropods/Ender Slayer apply only to their mob family (each +5%/level).
     * Execute/Prosecute/Giant Killer scale with the target's health relative to the attacker. All
     * values come from the bundled 1:1 item data; summed into one additive bucket.
     */
    private static double enchantDamagePercent(ItemStack weapon, Entity target, double attackerMaxHp) {
        Map<String, Integer> ench = parseWeaponEnchants(weapon);
        if (ench.isEmpty()) {
            return 0.0;
        }
        EntityType type = target.getType();
        double pct = 5.0 * ench.getOrDefault("SHARPNESS", 0);
        if (UNDEAD.contains(type))     pct += 5.0 * ench.getOrDefault("SMITE", 0);
        if (ARTHROPODS.contains(type)) pct += 5.0 * ench.getOrDefault("BANE_OF_ARTHROPODS", 0);
        if (ENDER.contains(type))      pct += 5.0 * ench.getOrDefault("ENDER_SLAYER", 0);

        if (target instanceof LivingEntity living) {
            double maxHp = living.getMaxHealth();
            double hpFraction = maxHp > 0 ? living.getHealth() / maxHp : 0.0;

            int execute = ench.getOrDefault("EXECUTE", 0);
            if (execute > 0) {
                pct += EXECUTE_RATE[Math.min(execute, EXECUTE_RATE.length) - 1] * (1.0 - hpFraction) * 100.0;
            }
            int prosecute = ench.getOrDefault("PROSECUTE", 0);
            if (prosecute > 0) {
                pct += PROSECUTE_RATE[Math.min(prosecute, PROSECUTE_RATE.length) - 1] * hpFraction * 100.0;
            }
            int gk = ench.getOrDefault("GIANT_KILLER", 0);
            if (gk > 0 && attackerMaxHp > 0) {
                double extraPct = Math.max(0.0, (maxHp - attackerMaxHp) / attackerMaxHp * 100.0);
                int i = Math.min(gk, GIANT_KILLER_RATE.length) - 1;
                pct += Math.min(GIANT_KILLER_CAP[i], GIANT_KILLER_RATE[i] * extraPct);
            }
        }
        return pct;
    }

    /** Parses {@code §9<Enchant> <Roman>} lore lines (incl. comma-joined) into enchant-key → level. */
    private static Map<String, Integer> parseWeaponEnchants(ItemStack weapon) {
        Map<String, Integer> result = new HashMap<>();
        if (weapon == null) {
            return result;
        }
        ItemMeta meta = weapon.getItemMeta();
        if (meta == null || meta.getLore() == null) {
            return result;
        }
        for (String raw : meta.getLore()) {
            String line = ChatColor.stripColor(raw).trim();
            for (String part : line.split(",")) {
                part = part.trim();
                int sp = part.lastIndexOf(' ');
                if (sp <= 0) {
                    continue;
                }
                int level = romanToInt(part.substring(sp + 1).trim());
                if (level <= 0) {
                    continue;
                }
                String key = DAMAGE_ENCHANTS.get(part.substring(0, sp).trim().toLowerCase(Locale.ROOT));
                if (key != null) {
                    result.merge(key, level, Math::max);
                }
            }
        }
        return result;
    }

    /** Parses a small Roman numeral (I–X range); returns 0 if not a clean numeral. */
    private static int romanToInt(String s) {
        if (s.isEmpty()) {
            return 0;
        }
        int total = 0;
        int prev = 0;
        for (int i = s.length() - 1; i >= 0; i--) {
            int v;
            switch (Character.toUpperCase(s.charAt(i))) {
                case 'I': v = 1; break;
                case 'V': v = 5; break;
                case 'X': v = 10; break;
                default: return 0;
            }
            if (v < prev) total -= v; else { total += v; prev = v; }
        }
        return total;
    }

    private void spawnDamageIndicator(Location location, double damage, boolean isCrit) {
        Location display = location.clone().add(0, 1.5, 0);
        ArmorStand stand = (ArmorStand) location.getWorld().spawnEntity(display, EntityType.ARMOR_STAND);
        String label = isCrit
                ? "§c§l✧ " + (long) damage
                : "§f" + (long) damage;
        stand.setCustomName(label);
        stand.setCustomNameVisible(true);
        stand.setGravity(false);
        stand.setVisible(false);
        stand.setInvulnerable(true);
        Bukkit.getScheduler().runTaskLater(SkyBlockCore.getInstance(), stand::remove, 30L);
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        CombatStatsManager combatStats = CombatStatsManager.getInstance();
        Player victim = event.getEntity();
        combatStats.recordDeath(victim.getUniqueId());

        Player killer = victim.getKiller();
        if (killer != null) {
            combatStats.recordKill(killer.getUniqueId());
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Long matter = ORGANIC_MATTER.get(event.getBlock().getType());
        if (matter == null) {
            return;
        }
        Player player = event.getPlayer();
        GardenManager.getInstance().addComposterOrganicMatter(player.getUniqueId(), matter);
    }

    // --- Accessory / talisman tracking (formerly AccessoryListener) ---

    @EventHandler
    public void onItemHeld(PlayerItemHeldEvent event) {
        refreshAccessories(event.getPlayer());
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (!(event.getPlayer() instanceof Player player)) {
            return;
        }
        refreshAccessories(player);
    }

    private void refreshAccessories(Player player) {
        UUID uuid = player.getUniqueId();
        TalismanManager talismanManager = TalismanManager.getInstance();
        talismanManager.reset(uuid);
        PlayerInventory inv = player.getInventory();
        for (int slot = 0; slot < 36; slot++) {
            ItemStack item = inv.getItem(slot);
            if (item == null) {
                continue;
            }
            TalismanManager.TalismanType type = resolveTalisman(item);
            if (type != null) {
                talismanManager.equip(uuid, type);
            }
        }
    }

    private static TalismanManager.TalismanType resolveTalisman(ItemStack item) {
        if (!item.hasItemMeta()) {
            return null;
        }
        String displayName = item.getItemMeta().getDisplayName();
        if (displayName == null || displayName.isEmpty()) {
            return null;
        }
        String key = displayName.replaceAll("§.", "").trim()
                .toUpperCase(Locale.ROOT).replace(' ', '_');
        try {
            return TalismanManager.TalismanType.valueOf(key);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    // Armor/held stat scanning is owned by EquipmentListener (single source of truth).
}
