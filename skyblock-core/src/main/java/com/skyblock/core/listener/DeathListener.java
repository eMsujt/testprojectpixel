package com.skyblock.core.listener;

import com.skyblock.core.manager.BankManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import java.util.UUID;

public final class DeathListener implements Listener {

    private static final DeathListener INSTANCE = new DeathListener();

    private final BankManager bankManager = BankManager.getInstance();

    private DeathListener() {}

    public static DeathListener getInstance() {
        return INSTANCE;
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        UUID uuid = player.getUniqueId();
        long purse = bankManager.getPurseBalance(uuid);
        long penalty = (long) (purse * 0.05);
        if (penalty > 0) {
            bankManager.removeFromPurse(uuid, penalty);
            player.sendMessage("§cYou lost §6" + penalty + " coins §cfrom your purse on death.");
        }
    }
}
