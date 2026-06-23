package com.skyblock.core.menu;

import com.skyblock.core.profile.manager.ProfileManager;
import com.skyblock.core.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * "Profile Management", opened from the SkyBlock Menu. Laid out 1:1 with Hypixel:
 * a 4-row chest with the profile tiles along row 2 (slots 11–15), a player-head
 * summary at slot 4, and Go Back (30) / Close (31) on the bottom row.
 */
public final class ProfileMenu extends AbstractSkyBlockMenu {

    static final int PLAYER_SLOT = 4;
    static final int[] PROFILE_SLOTS = {11, 12, 13, 14, 15};

    public ProfileMenu(Player player) {
        super(player, "§bProfile Management", 4);
    }

    @Override
    protected void populate() {
        UUID playerId = player.getUniqueId();
        ProfileManager manager = ProfileManager.getInstance();

        ItemStack greenPane = new ItemBuilder(Material.GREEN_STAINED_GLASS_PANE).displayName("§r").build();
        for (int slot = 0; slot < 9; slot++) setItem(slot, greenPane);
        for (int slot = 27; slot < 36; slot++) setItem(slot, greenPane);

        ProfileManager.SkyBlockProfile active = manager.getActiveProfile(playerId);
        String activeName = active != null ? active.name() : "None";
        String activeMode = active != null ? active.gameMode().getDisplayName() : "N/A";
        int souls = manager.getFairySouls(playerId);
        long sbXp = manager.getSkyBlockXp(playerId);
        List<ProfileManager.SkyBlockProfile> profiles = manager.getProfilesForOwner(playerId);
        ProfileManager.ProfileData data = manager.getPlayerData(playerId);
        String created = data != null
                ? new SimpleDateFormat("yyyy-MM-dd").format(new Date(data.createdAt()))
                : "N/A";

        setItem(PLAYER_SLOT, new ItemBuilder(Material.PLAYER_HEAD).skullOwner(player)
                .displayName("§a" + player.getName())
                .lore("§7Active Profile: §e" + activeName,
                        "§7Mode: §e" + activeMode,
                        "",
                        "§7Profiles: §e" + profiles.size() + "§7/§e" + ProfileManager.MAX_PROFILES,
                        "§7Member since: §e" + created,
                        "§7Fairy Souls: §e" + souls,
                        "§7SkyBlock XP: §e" + sbXp)
                .build());

        for (int i = 0; i < PROFILE_SLOTS.length; i++) {
            if (i < profiles.size()) {
                ProfileManager.SkyBlockProfile profile = profiles.get(i);
                boolean isActive = active != null && active.profileId().equals(profile.profileId());
                int index = i + 1;
                setItem(PROFILE_SLOTS[i], new ItemBuilder(isActive ? Material.EMERALD_BLOCK : Material.PAPER)
                        .displayName((isActive ? "§a§l" : "§e") + profile.name())
                        .lore("§7Mode: §f" + profile.gameMode().getDisplayName(),
                                "",
                                isActive ? "§a(Currently Active)" : "§eClick to switch")
                        .build(),
                        e -> {
                            e.setCancelled(true);
                            manager.switchProfile(playerId, index);
                            player.closeInventory();
                            player.sendMessage("§aSwitched to profile \"" + profile.name() + "\".");
                        });
            } else {
                setItem(PROFILE_SLOTS[i], new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE)
                        .displayName("§7Empty Slot")
                        .lore("§7No profile in this slot.")
                        .build());
            }
        }

        setItem(30, new ItemBuilder(Material.ARROW)
                .displayName("§aGo Back")
                .lore("§7To SkyBlock Menu")
                .build(),
                e -> { e.setCancelled(true); new SkyBlockMenu(player).open(player); });

        setItem(31, new ItemBuilder(Material.BARRIER)
                .displayName("§cClose")
                .build(),
                e -> { e.setCancelled(true); player.closeInventory(); });
    }
}
