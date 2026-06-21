package com.skyblock.core.ability;

import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

import java.util.Locale;

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
            case "instant transmission", "ether transmission" -> true;
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
            default -> { }
        }
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
