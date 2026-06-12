package com.skyblock.core.alchemy;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.BrewEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Bukkit listener that awards alchemy XP when a player brews a potion.
 *
 * <p>Tracks which player last interacted with each brewing stand via
 * {@link PlayerInteractEvent}; then on {@link BrewEvent} looks up the
 * associated player and awards alchemy XP. Any brewing stand with no
 * recorded interaction is silently ignored.</p>
 */
public final class AlchemyListener implements Listener {

    /** Base XP awarded per brewing-stand completion. */
    private static final double BASE_BREW_XP = 5.0;

    private final AlchemyManager alchemyManager;
    /** Maps brewing-stand block location → player UUID of the last interactor. */
    private final Map<Location, UUID> brewingPlayers = new HashMap<>();

    /**
     * Creates a listener backed by the given {@link AlchemyManager}.
     *
     * @param alchemyManager the alchemy manager, must not be null
     * @throws IllegalArgumentException if {@code alchemyManager} is null
     */
    public AlchemyListener(AlchemyManager alchemyManager) {
        if (alchemyManager == null) {
            throw new IllegalArgumentException("alchemyManager must not be null");
        }
        this.alchemyManager = alchemyManager;
    }

    /**
     * Records the player who most recently right-clicked a brewing stand.
     *
     * @param event the player interact event
     */
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getClickedBlock() == null) {
            return;
        }
        if (event.getClickedBlock().getType() != Material.BREWING_STAND) {
            return;
        }
        brewingPlayers.put(event.getClickedBlock().getLocation(), event.getPlayer().getUniqueId());
    }

    /**
     * Awards alchemy XP to the last player who interacted with the brewing stand
     * when a brew completes.
     *
     * @param event the brew event fired by the server
     */
    @EventHandler
    public void onBrew(BrewEvent event) {
        UUID playerId = brewingPlayers.get(event.getBlock().getLocation());
        if (playerId == null) {
            return;
        }
        alchemyManager.addXp(playerId, BASE_BREW_XP);
    }
}
