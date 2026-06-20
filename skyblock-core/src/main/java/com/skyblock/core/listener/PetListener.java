package com.skyblock.core.listener;

import com.skyblock.core.manager.PetManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

import java.util.UUID;

/**
 * Awards XP to a player's currently active pet whenever they kill a mob.
 */
public final class PetListener implements Listener {

    private static final PetListener INSTANCE = new PetListener();

    /** Pet XP granted per mob kill. */
    private static final long XP_PER_KILL = 5L;

    private final PetManager petManager = PetManager.getInstance();

    private PetListener() {}

    public static PetListener getInstance() {
        return INSTANCE;
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        Player killer = event.getEntity().getKiller();
        if (killer == null) return;

        UUID uuid = killer.getUniqueId();
        if (petManager.getActivePet(uuid) == null) return;

        petManager.addPetXp(uuid, XP_PER_KILL);
    }
}
