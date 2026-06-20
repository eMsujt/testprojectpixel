package com.skyblock.core.listener;

import com.skyblock.core.manager.FishingManager;
import com.skyblock.core.manager.FishingManager.SeaCreature;
import com.skyblock.core.manager.FishingManager.WaterType;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.FishHook;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerFishEvent;

import java.util.UUID;

/**
 * Handles sea-creature detection on every successful fish catch.
 *
 * <p>Detects the body of water the player is fishing in (water, lava, etc.)
 * from the block at the hook location, then delegates to
 * {@link FishingManager#rollSeaCreature(int, WaterType, double)} for the
 * weighted creature roll. The player is notified when a creature spawns.</p>
 */
public final class FishingListener implements Listener {

    private final FishingManager fishingManager = FishingManager.getInstance();

    @EventHandler
    public void onPlayerFish(PlayerFishEvent event) {
        if (event.getState() != PlayerFishEvent.State.CAUGHT_FISH) {
            return;
        }

        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();
        int level = fishingManager.getLevel(uuid);

        WaterType waterType = detectWaterType(event.getHook());

        SeaCreature creature = fishingManager.rollSeaCreature(level, waterType, 0.0);
        if (creature == null) {
            return;
        }

        player.sendMessage("§3[Sea Creature] §fA §b" + creature.name().replace('_', ' ')
                + " §fhas spawned from the " + waterType.name().toLowerCase() + "!");
        fishingManager.recordCatchEvent(uuid, "Sea creature: " + creature.name()
                + " [" + waterType.name() + "]");
    }

    private static WaterType detectWaterType(FishHook hook) {
        Location loc = hook.getLocation();
        Material block = loc.getBlock().getType();
        if (block == Material.LAVA || block == Material.LAVA_CAULDRON) {
            return WaterType.LAVA;
        }
        return WaterType.WATER;
    }
}
