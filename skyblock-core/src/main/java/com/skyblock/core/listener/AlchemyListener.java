package com.skyblock.core.listener;

import com.skyblock.core.manager.AlchemyManager;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.BrewEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class AlchemyListener implements Listener {

    private static final AlchemyListener INSTANCE = new AlchemyListener();

    private static final double XP_PER_BREW = 10.0;

    private final Map<Location, UUID> brewerMap = new HashMap<>();
    private final AlchemyManager alchemyManager = AlchemyManager.getInstance();

    private AlchemyListener() {}

    public static AlchemyListener getInstance() {
        return INSTANCE;
    }

    @EventHandler
    public void onInventoryOpen(InventoryOpenEvent event) {
        if (event.getInventory().getType() != InventoryType.BREWING) {
            return;
        }
        Location loc = event.getInventory().getLocation();
        if (loc == null) {
            return;
        }
        brewerMap.put(loc, event.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (event.getInventory().getType() != InventoryType.BREWING) {
            return;
        }
        Location loc = event.getInventory().getLocation();
        if (loc != null) {
            brewerMap.remove(loc);
        }
    }

    @EventHandler
    public void onBrew(BrewEvent event) {
        Location loc = event.getBlock().getLocation();
        UUID uuid = brewerMap.get(loc);
        if (uuid != null) {
            alchemyManager.addXp(uuid, XP_PER_BREW);
        }
        alchemyManager.processCompletedJobs(System.currentTimeMillis());
    }

    public Map<Location, UUID> getBrewerMap() {
        return brewerMap;
    }
}
