package com.skyblock.plugin.listener;

import com.skyblock.plugin.manager.ProfileManager;
import com.skyblock.plugin.profile.SkyBlockProfile;
import com.skyblock.plugin.profile.SkyBlockProfileRepository;
import org.bukkit.plugin.Plugin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.UUID;

/**
 * Loads a player's {@link SkyBlockProfile} into {@link ProfileManager} on join
 * and saves/removes it on quit.
 */
public final class PlayerJoinQuitListener implements Listener {

    private final Plugin plugin;

    public PlayerJoinQuitListener(Plugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        UUID uuid = event.getPlayer().getUniqueId();
        SkyBlockProfileRepository.getInstance().loadAsync(plugin, uuid)
                .thenAccept(profile -> plugin.getServer().getScheduler().runTask(plugin, () ->
                        ProfileManager.getInstance().addProfile(profile)));
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        UUID uuid = event.getPlayer().getUniqueId();
        SkyBlockProfile profile = ProfileManager.getInstance().removeProfile(uuid);
        if (profile != null) {
            SkyBlockProfileRepository.getInstance().saveAsync(plugin, profile);
        }
    }
}
