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
 * "Profile Management", opened from the SkyBlock Menu. Laid out 1:1 with Hypixel's
 * Profile Management GUI (wiki Profile/UI): the profile tiles run along row 2
 * (slots 11-15) — owned profiles first (the active one as a Block of Emerald),
 * then a "create solo profile" Wooden Button, then the rank-gated locked slots
 * ([VIP] 13, [MVP+] 14, gem slot #5 15). Go Back sits at slot 30.
 */
public final class ProfileMenu extends AbstractSkyBlockMenu {

    static final int[] PROFILE_SLOTS = {11, 12, 13, 14, 15};

    public ProfileMenu(Player player) {
        super(player, "Profile Management", 4);
    }

    @Override
    protected void populate() {
        UUID playerId = player.getUniqueId();
        ProfileManager manager = ProfileManager.getInstance();

        ItemStack pane = new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE).displayName("§r").build();
        for (int slot = 0; slot < 36; slot++) setItem(slot, pane);

        ProfileManager.SkyBlockProfile active = manager.getActiveProfile(playerId);
        List<ProfileManager.SkyBlockProfile> profiles = manager.getProfilesForOwner(playerId);
        int souls = manager.getFairySouls(playerId);
        long sbXp = manager.getSkyBlockXp(playerId);
        ProfileManager.ProfileData data = manager.getPlayerData(playerId);
        String created = data != null
                ? new SimpleDateFormat("yyyy-MM-dd").format(new Date(data.createdAt()))
                : "N/A";

        int owned = profiles.size();
        for (int i = 0; i < PROFILE_SLOTS.length; i++) {
            int slot = PROFILE_SLOTS[i];
            if (i < owned) {
                ProfileManager.SkyBlockProfile profile = profiles.get(i);
                boolean isActive = active != null && active.profileId().equals(profile.profileId());
                int index = i + 1;
                if (isActive) {
                    setItem(slot, new ItemBuilder(Material.EMERALD_BLOCK)
                            .displayName("§eProfile: §a" + profile.name())
                            .lore("§8Selected Slot",
                                    "",
                                    "§7Mode: §f" + profile.gameMode().getDisplayName(),
                                    "§7Fairy Souls: §e" + souls,
                                    "§7SkyBlock XP: §e" + sbXp,
                                    "§7Age: §f" + created,
                                    "",
                                    "§aYou are playing on this profile!")
                            .build(), e -> e.setCancelled(true));
                } else {
                    setItem(slot, new ItemBuilder(Material.PAPER)
                            .displayName("§eProfile: §e" + profile.name())
                            .lore("§8Available Slot",
                                    "",
                                    "§7Mode: §f" + profile.gameMode().getDisplayName(),
                                    "",
                                    "§eClick to switch to this profile!")
                            .build(),
                            e -> {
                                e.setCancelled(true);
                                manager.switchProfile(playerId, index);
                                player.closeInventory();
                                player.sendMessage("§aSwitched to profile \"" + profile.name() + "\".");
                            });
                }
            } else if (i == owned && (slot == 11 || slot == 12 || slot == 13)) {
                // First empty free slot: create a new solo profile.
                setItem(slot, new ItemBuilder(Material.OAK_BUTTON)
                        .displayName("§eEmpty profile slot")
                        .lore("§8Available",
                                "",
                                "§7Use this slot if you want to",
                                "§7start a new SkyBlock adventure.",
                                "",
                                "§7Each profile has its own:",
                                "§8• §7Personal island",
                                "§8• §7Inventory",
                                "§8• §7Ender Chest",
                                "§8• §7Bank & Purse",
                                "§8• §7Quests",
                                "§8• §7Collections",
                                "",
                                "§4§lWARNING:§r§c Creation of profiles",
                                "§cwhich boost other profiles will",
                                "§cbe considered abusive and",
                                "§cpunished.",
                                "",
                                "§bUse /coopadd <name> to invite your friends!",
                                "§eClick to create solo profile!")
                        .build(),
                        e -> {
                            e.setCancelled(true);
                            player.sendMessage("§eUse §6/profile create <name> §eto start a new profile.");
                        });
            } else {
                setItem(slot, lockedSlot(slot));
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

    /** The rank/gem-gated locked profile slots, matching Hypixel's positions (13 VIP, 14 MVP+, 15 gems). */
    private static ItemStack lockedSlot(int slot) {
        if (slot == 15) {
            return new ItemBuilder(Material.BEDROCK)
                    .displayName("§6Profile Slot #5")
                    .lore("§8Unavailable",
                            "",
                            "§7Cost",
                            "§a2,750 SkyBlock Gems",
                            "",
                            "§7You have: §a0 Gems",
                            "",
                            "§cCannot afford this!",
                            "§eClick here to get gems!")
                    .build();
        }
        String rank = slot == 13 ? "§a[VIP]" : "§b[MVP§c+§b]";
        return new ItemBuilder(Material.BEDROCK)
                .displayName("§cLocked profile slot")
                .lore("§8Unavailable",
                        "",
                        "§7Requires " + rank)
                .build();
    }
}
