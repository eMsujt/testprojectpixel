package com.skyblock.core.ability;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

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
            case "instant transmission", "ether transmission", "dragon rage", "giant's slam" -> true;
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
            case "dragon rage" -> dragonRage(player, parseDamage(ability));
            case "giant's slam" -> giantsSlam(player, parseDamage(ability));
            default -> { }
        }
    }

    // "...take 12,000 \n damage" (number and word split across lines) or "100,000 damage ...".
    private static final Pattern TAKE_DAMAGE = Pattern.compile("take\\s+([0-9,]+)");
    private static final Pattern N_DAMAGE = Pattern.compile("([0-9,]+)\\s+damage");

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
