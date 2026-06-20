package com.skyblock.core.menu;

import com.skyblock.core.profile.manager.ProfileManager;
import com.skyblock.core.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public final class ProfileMenu extends AbstractMenu {

    static final int[] PROFILE_SLOTS = {10, 11, 12, 13, 14, 15, 16};
    private static final int NEW_SLOT = 22;

    public ProfileMenu(JavaPlugin plugin, Player player) {
        super(plugin, player, "§bSkyBlock Profiles", 36);
    }

    @Override
    protected void populate() {
        ItemStack pane = new ItemBuilder(Material.BLUE_STAINED_GLASS_PANE).name("§r").build();
        for (int slot = 0; slot < 36; slot++) {
            int col = slot % 9;
            if (slot < 9 || slot >= 27 || col == 0 || col == 8) {
                setItem(slot, pane);
            }
        }

        ProfileManager pm = ProfileManager.getInstance();
        List<ProfileManager.SkyBlockProfile> profiles = pm.getProfilesForOwner(player.getUniqueId());
        ProfileManager.SkyBlockProfile active = pm.getActiveProfile(player.getUniqueId());

        for (int i = 0; i < profiles.size() && i < PROFILE_SLOTS.length; i++) {
            ProfileManager.SkyBlockProfile profile = profiles.get(i);
            boolean isActive = active != null && profile.profileId().equals(active.profileId());
            int slot = PROFILE_SLOTS[i];
            int index = i + 1;
            setItem(slot, new ItemBuilder(Material.PAPER)
                    .name((isActive ? "§a§l" : "§e") + profile.name())
                    .lore("§7Mode: §f" + profile.gameMode().getDisplayName(),
                            isActive ? "§a(Currently Active)" : "§7Click to switch")
                    .build(), e -> {
                e.setCancelled(true);
                pm.switchProfile(player.getUniqueId(), index);
                player.closeInventory();
                player.sendMessage("§aSwitched to profile \"" + profile.name() + "\".");
            });
        }

        if (profiles.size() < ProfileManager.MAX_PROFILES) {
            setItem(NEW_SLOT, new ItemBuilder(Material.LIME_DYE)
                    .name("§aCreate New Profile")
                    .lore("§7Use /profile create <name>",
                            "§7Slots: §e" + profiles.size() + "§7/§e" + ProfileManager.MAX_PROFILES)
                    .build(), e -> e.setCancelled(true));
        }
    }
}
