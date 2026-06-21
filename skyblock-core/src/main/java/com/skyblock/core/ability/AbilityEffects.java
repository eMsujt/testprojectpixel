package com.skyblock.core.ability;

import com.skyblock.core.SkyBlockCore;
import com.skyblock.core.manager.StatManager;
import com.skyblock.core.model.Stat;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Real effects for lore-driven item abilities. Only abilities listed here actually fire; others
 * are recognised but left as no-ops until implemented, so mana is never spent on nothing.
 */
public final class AbilityEffects {

    private AbilityEffects() {}

    /** Whether {@link #run} has a real effect for this ability name. */
    public static boolean isImplemented(String abilityName) {
        if (abilityName == null) return false;
        return switch (abilityName.toLowerCase(Locale.ROOT)) {
            case "instant transmission", "ether transmission", "dragon rage", "giant's slam",
                 "weird transmission", "instant heal", "implosion", "leap", "showtime" -> true;
            default -> false;
        };
    }

    /** Executes the ability's effect on the player. No-op for unimplemented abilities. */
    public static void run(LoreAbility ability, Player player) {
        switch (ability.name.toLowerCase(Locale.ROOT)) {
            case "instant transmission" ->
                    teleport(player, clipForward(player, ability.magnitude > 0 ? ability.magnitude : 8),
                            true);
            case "ether transmission" ->
                    teleport(player, etherTarget(player, ability.magnitude > 0 ? ability.magnitude : 57),
                            false);
            case "dragon rage" -> dragonRage(player, abilityDamage(player, parseDamage(ability)));
            case "giant's slam" -> giantsSlam(player, abilityDamage(player, parseDamage(ability)));
            case "weird transmission" ->
                    teleport(player, clipForward(player, ability.magnitude > 0 ? ability.magnitude : 3),
                            false);
            case "instant heal" -> instantHeal(player, ability);
            case "implosion" -> implosion(player, abilityDamage(player, parseDamage(ability)));
            case "leap" -> leap(player, abilityDamage(player, parseDamage(ability)));
            case "showtime" -> showtime(player, abilityDamage(player, parseDamage(ability)));
            default -> { }
        }
    }

    /** Shared key tagging an ability projectile with the damage it deals on impact. */
    public static NamespacedKey projectileDamageKey() {
        return new NamespacedKey(SkyBlockCore.getInstance(), "ability_proj_dmg");
    }

    /** Launches a projectile that bursts for AoE damage on impact (handled by the hit listener). */
    private static void showtime(Player player, double damage) {
        Snowball ball = player.launchProjectile(Snowball.class);
        ball.setShooter(player);
        ball.getPersistentDataContainer().set(projectileDamageKey(), PersistentDataType.DOUBLE, damage);
        ball.setVelocity(player.getEyeLocation().getDirection().multiply(1.6));
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_SNOWBALL_THROW, 1f, 0.8f);
    }

    /** Launches the player up and forward, then damages nearby monsters (Leaping Sword). */
    private static void leap(Player player, double damage) {
        Vector look = player.getEyeLocation().getDirection();
        Vector velocity = new Vector(look.getX(), 0, look.getZ());
        if (velocity.lengthSquared() > 1.0e-6) {
            velocity.normalize().multiply(0.6);
        }
        velocity.setY(1.0);
        player.setVelocity(velocity);

        double radius = 5.0;
        for (Entity entity : player.getNearbyEntities(radius, radius, radius)) {
            if (entity instanceof Monster monster) {
                monster.damage(damage, player);
            }
        }
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_ENDER_DRAGON_FLAP, 1f, 1.4f);
    }

    /** AoE burst: damages every monster around the player (no knockback), explosion feedback. */
    private static void implosion(Player player, double damage) {
        Location center = player.getLocation();
        double radius = 6.0;
        for (Entity entity : player.getNearbyEntities(radius, radius, radius)) {
            if (entity instanceof Monster monster) {
                monster.damage(damage, player);
            }
        }
        player.getWorld().playSound(center, Sound.ENTITY_GENERIC_EXPLODE, 1f, 1.2f);
        player.getWorld().spawnParticle(Particle.EXPLOSION, center, 3, 1.5, 0.5, 1.5, 0.0);
    }

    private static final Pattern HEAL_FLAT = Pattern.compile("Heal for ([0-9,]+)");
    private static final Pattern PERCENT = Pattern.compile("([0-9]+)%");

    /** Heals the player by the lore-stated flat amount plus its percent of max health. */
    private static void instantHeal(Player player, LoreAbility ability) {
        double maxHealth = player.getMaxHealth();
        double heal = firstNumber(ability.lines, HEAL_FLAT, 0)
                + maxHealth * (firstNumber(ability.lines, PERCENT, 0) / 100.0);
        if (heal <= 0) {
            heal = maxHealth * 0.2;
        }
        player.setHealth(Math.min(maxHealth, player.getHealth() + heal));
        player.getWorld().spawnParticle(Particle.HEART, player.getLocation().add(0, 1, 0),
                8, 0.4, 0.4, 0.4, 0);
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 0.6f, 1.6f);
    }

    private static double firstNumber(List<String> lines, Pattern pattern, double fallback) {
        for (String line : lines) {
            Matcher m = pattern.matcher(line);
            if (m.find()) {
                try {
                    return Double.parseDouble(m.group(1).replace(",", ""));
                } catch (NumberFormatException ignored) {
                }
            }
        }
        return fallback;
    }

    // "...take 12,000 \n damage" (number and word split across lines) or "100,000 damage ...".
    private static final Pattern TAKE_DAMAGE = Pattern.compile("take\\s+([0-9,]+(?:\\.[0-9]+)?)");
    private static final Pattern N_DAMAGE = Pattern.compile("([0-9,]+(?:\\.[0-9]+)?)\\s+damage");

    /**
     * Scales an ability's base (lore) damage by the player's stats: Intelligence (the lore value is
     * the damage at 100 Intelligence) and the Ability Damage stat.
     */
    private static double abilityDamage(Player player, double baseDamage) {
        StatManager stats = StatManager.getInstance();
        double intelligence = stats.getStat(player.getUniqueId(), Stat.INTELLIGENCE);
        double abilityDamage = stats.getStat(player.getUniqueId(), Stat.ABILITY_DAMAGE);
        return baseDamage * (intelligence / 100.0) * (1.0 + abilityDamage / 100.0);
    }

    /** Pulls the ability's damage value from its description; 1000 if none is stated. */
    private static double parseDamage(LoreAbility ability) {
        for (Pattern pattern : new Pattern[] {TAKE_DAMAGE, N_DAMAGE}) {
            for (String line : ability.lines) {
                Matcher m = pattern.matcher(line);
                if (m.find()) {
                    try {
                        return Double.parseDouble(m.group(1).replace(",", ""));
                    } catch (NumberFormatException ignored) {
                    }
                }
            }
        }
        return 1000;
    }

    /** AoE: damage and knock back all monsters in a cone in front of the player. */
    private static void dragonRage(Player player, double damage) {
        Location eye = player.getEyeLocation();
        Vector look = eye.getDirection().normalize();
        double range = 5.0;
        for (Entity entity : player.getNearbyEntities(range, range, range)) {
            if (!(entity instanceof Monster monster)) {
                continue;
            }
            Vector toward = monster.getLocation().add(0, monster.getHeight() / 2, 0)
                    .toVector().subtract(eye.toVector());
            if (toward.lengthSquared() < 1.0e-6) {
                continue;
            }
            if (look.dot(toward.normalize()) < 0.4) {
                continue; // outside the forward cone
            }
            monster.damage(damage, player);
            monster.setVelocity(monster.getVelocity().add(look.clone().multiply(1.4).setY(0.35)));
        }
        player.getWorld().playSound(eye, Sound.ENTITY_ENDER_DRAGON_GROWL, 1f, 1f);
        player.getWorld().spawnParticle(Particle.FLAME,
                eye.clone().add(look.clone().multiply(2)), 30, 1, 1, 1, 0.01);
    }

    /** AoE slam: damage and launch every monster around the player. */
    private static void giantsSlam(Player player, double damage) {
        Location center = player.getLocation();
        double radius = 6.0;
        for (Entity entity : player.getNearbyEntities(radius, radius, radius)) {
            if (!(entity instanceof Monster monster)) {
                continue;
            }
            Vector away = monster.getLocation().toVector().subtract(center.toVector());
            away = away.lengthSquared() < 1.0e-6 ? new Vector(0, 1, 0) : away.normalize();
            monster.damage(damage, player);
            monster.setVelocity(monster.getVelocity().add(away.multiply(1.0).setY(0.6)));
        }
        player.getWorld().playSound(center, Sound.ENTITY_GENERIC_EXPLODE, 1f, 0.6f);
        player.getWorld().spawnParticle(Particle.EXPLOSION, center, 6, 2, 0.2, 2, 0.0);
    }

    /** Teleports the player, preserving their facing, with the ender-teleport sound. */
    private static void teleport(Player player, Location dest, boolean speedBoost) {
        Location from = player.getLocation();
        dest.setYaw(from.getYaw());
        dest.setPitch(from.getPitch());
        player.getWorld().playSound(from, Sound.ENTITY_ENDERMAN_TELEPORT, 1f, 1f);
        player.teleport(dest);
        player.getWorld().playSound(dest, Sound.ENTITY_ENDERMAN_TELEPORT, 1f, 1f);
        if (speedBoost) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 3 * 20, 1, true, false));
        }
    }

    /** Furthest clear spot up to {@code blocks} ahead along the look direction, clipped to walls. */
    private static Location clipForward(Player player, int blocks) {
        Location from = player.getLocation();
        Vector dir = player.getEyeLocation().getDirection().normalize();
        Location dest = from.clone();
        for (int i = 1; i <= blocks; i++) {
            Location step = from.clone().add(dir.clone().multiply(i));
            if (standable(step)) {
                dest = step;
            } else {
                break;
            }
        }
        return dest;
    }

    /** Targeted-block teleport: stand against the block face the player is looking at. */
    private static Location etherTarget(Player player, int max) {
        RayTraceResult ray = player.rayTraceBlocks(max);
        if (ray != null && ray.getHitBlock() != null) {
            Block hit = ray.getHitBlock();
            BlockFace face = ray.getHitBlockFace();
            Block landing = face != null ? hit.getRelative(face) : hit;
            Location dest = landing.getLocation().add(0.5, 0, 0.5);
            if (standable(dest)) {
                return dest;
            }
        }
        // Nothing valid hit — behave like a long forward blink.
        return clipForward(player, max);
    }

    /** True if a player can stand at {@code loc} (feet and head blocks are passable). */
    private static boolean standable(Location loc) {
        return loc.getBlock().isPassable() && loc.clone().add(0, 1, 0).getBlock().isPassable();
    }
}
