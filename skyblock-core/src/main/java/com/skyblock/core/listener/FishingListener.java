package com.skyblock.core.listener;

import com.skyblock.core.manager.FishingManager;
import com.skyblock.core.manager.FishingManager.SeaCreature;
import com.skyblock.core.manager.SkillManager;
import com.skyblock.core.manager.StatManager;
import com.skyblock.core.model.Skill;
import com.skyblock.core.model.Stat;
import com.skyblock.core.util.ChatUtil;
import com.skyblock.core.manager.FishingManager.WaterType;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.FishHook;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public final class FishingListener implements Listener {

    private static final FishingListener INSTANCE = new FishingListener();

    private final FishingManager fishingManager = FishingManager.getInstance();
    private final SkillManager skillManager = SkillManager.getInstance();

    private FishingListener() {}

    public static FishingListener getInstance() {
        return INSTANCE;
    }

    @EventHandler
    public void onPlayerFish(PlayerFishEvent event) {
        if (event.getState() == PlayerFishEvent.State.FISHING) {
            applyFishingSpeed(event.getHook(), event.getPlayer());
            return;
        }
        if (event.getState() != PlayerFishEvent.State.CAUGHT_FISH) return;
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();

        int level = fishingManager.getLevel(uuid);
        ItemStack loot = fishingManager.rollLoot(level);

        boolean isTreasure = loot.getType() == Material.MAP;
        double xp = isTreasure ? FishingManager.XP_TREASURE : FishingManager.XP_PER_CATCH;
        fishingManager.addXp(uuid, xp);
        fishingManager.addFishCaught(uuid);
        int beforeLevel = skillManager.getLevel(uuid, Skill.FISHING);
        skillManager.addXP(uuid, Skill.FISHING, (long) xp);
        int afterLevel = skillManager.getLevel(uuid, Skill.FISHING);
        if (afterLevel > beforeLevel) {
            player.sendTitle("§aSkill Level Up!", "§eFishing §a→ §eLVL " + afterLevel, 10, 60, 20);
        }

        player.getWorld().dropItemNaturally(event.getHook().getLocation(), loot);
        fishingManager.recordCatchEvent(uuid, "Caught " + loot.getType().name());
        com.skyblock.core.manager.ActionBarManager.getInstance()
                .flash(player, "§9+" + (int) xp + " Fishing XP");

        WaterType waterType = detectWaterType(event.getHook());
        // Sea Creature Chance above the base 20 acts as the spawn-chance bonus.
        double scc = StatManager.getInstance().getStat(uuid, Stat.SEA_CREATURE_CHANCE);
        double luck = Math.max(0.0, (scc - 20.0) / 100.0);
        SeaCreature creature = fishingManager.rollSeaCreature(level, waterType, luck);
        if (creature != null) {
            spawnSeaCreature(creature, waterType, event.getHook().getLocation(), player);
            ChatUtil.send(player, "§3[Sea Creature] §fA §b" + creature.name().replace('_', ' ')
                    + " §fhas spawned from the " + waterType.name().toLowerCase() + "!");
            fishingManager.recordCatchEvent(uuid, "Sea creature: " + creature.name()
                    + " [" + waterType.name() + "]");
        }
    }

    /** Spawns a hostile mob for a rolled sea creature at the hook, set to attack the angler. */
    private static void spawnSeaCreature(SeaCreature creature, WaterType waterType,
                                         Location loc, Player player) {
        LivingEntity mob = (LivingEntity) player.getWorld().spawnEntity(loc, mobFor(creature, waterType));
        mob.setCustomName("§3" + creature.name().replace('_', ' '));
        mob.setCustomNameVisible(true);
        mob.setRemoveWhenFarAway(true);
        if (mob instanceof Mob hostile) {
            hostile.setTarget(player);
        }
    }

    /** Shortens the bite wait by the player's Fishing Speed (0 = no change). */
    private static void applyFishingSpeed(FishHook hook, Player player) {
        double speed = StatManager.getInstance().getStat(player.getUniqueId(), Stat.FISHING_SPEED);
        if (speed <= 0.0) {
            return;
        }
        double factor = 100.0 / (100.0 + speed);
        int min = Math.max(20, (int) (hook.getMinWaitTime() * factor));
        int max = Math.max(min + 20, (int) (hook.getMaxWaitTime() * factor));
        hook.setMinWaitTime(min);
        hook.setMaxWaitTime(max);
    }

    private static EntityType mobFor(SeaCreature creature, WaterType waterType) {
        String name = creature.name();
        if (name.contains("SQUID")) return EntityType.SQUID;
        if (name.contains("GUARDIAN") || name.contains("PROTECTOR")) return EntityType.GUARDIAN;
        if (name.contains("WITCH")) return EntityType.WITCH;
        if (name.contains("ARCHER") || name.contains("SKELETON") || name.contains("PHANTOM")) return EntityType.STRAY;
        if (name.contains("BLAZE") || name.contains("FLAMING")) return EntityType.BLAZE;
        if (name.contains("MAGMA")) return EntityType.MAGMA_CUBE;
        if (name.contains("PIGMAN") || name.contains("PIGLIN")) return EntityType.ZOMBIFIED_PIGLIN;
        if (name.contains("RABBIT")) return EntityType.RABBIT;
        if (name.contains("SHEEP")) return EntityType.SHEEP;
        if (waterType == WaterType.LAVA) return EntityType.MAGMA_CUBE;
        if (waterType == WaterType.OASIS) return EntityType.RABBIT;
        return EntityType.DROWNED;
    }

    private static WaterType detectWaterType(FishHook hook) {
        Material block = hook.getLocation().getBlock().getType();
        if (block == Material.LAVA || block == Material.LAVA_CAULDRON) {
            return WaterType.LAVA;
        }
        return WaterType.WATER;
    }
}
