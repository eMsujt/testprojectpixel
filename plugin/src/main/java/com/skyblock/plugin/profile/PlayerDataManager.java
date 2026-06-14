package com.skyblock.plugin.profile;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.Objects;
import java.util.UUID;

/**
 * Singleton listener that loads a player's SkyBlock data when they join.
 *
 * <p>On {@link PlayerJoinEvent} the joining player's {@link PlayerProfile} is
 * resolved from {@link ProfileManager}, creating a fresh one if none exists yet,
 * so the rest of the plugin can assume a profile is always present for an online
 * player.</p>
 *
 * <p>This type is registered as an event listener in
 * {@link com.skyblock.plugin.SkyBlockPlugin#onEnable()}.</p>
 */
public final class PlayerDataManager implements Listener {

    private static final PlayerDataManager INSTANCE = new PlayerDataManager();

    private PlayerDataManager() {}

    public static PlayerDataManager getInstance() {
        return INSTANCE;
    }

    /**
     * Returns the joining player's profile, loading or creating it as needed.
     *
     * @param uuid unique identifier of the player
     * @return the player's profile, never {@code null}
     */
    public PlayerProfile load(UUID uuid) {
        Objects.requireNonNull(uuid, "uuid");
        return ProfileManager.getInstance().getOrCreate(uuid);
    }

    /**
     * Loads the joining player's profile so it is ready for use.
     *
     * @param event the join event
     */
    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        load(player.getUniqueId());
    }
}
