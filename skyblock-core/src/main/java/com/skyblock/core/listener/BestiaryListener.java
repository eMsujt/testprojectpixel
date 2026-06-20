package com.skyblock.core.listener;

import com.skyblock.core.manager.BestiaryManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

public final class BestiaryListener implements Listener {

    private static final BestiaryListener INSTANCE = new BestiaryListener();

    private BestiaryListener() {}

    public static BestiaryListener getInstance() {
        return INSTANCE;
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        Player killer = event.getEntity().getKiller();
        if (killer == null) {
            return;
        }
        String mobType = event.getEntity().getType().name().toLowerCase();
        BestiaryManager.getInstance().recordKill(killer.getUniqueId(), mobType);
    }
}
