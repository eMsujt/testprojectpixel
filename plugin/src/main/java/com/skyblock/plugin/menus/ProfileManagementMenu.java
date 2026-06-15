package com.skyblock.plugin.menus;

import com.skyblock.core.util.ItemBuilder;
import com.skyblock.plugin.gui.Menu;
import com.skyblock.plugin.profile.PlayerProfile;
import com.skyblock.plugin.profile.ProfileManager;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * The Profile Management menu.
 *
 * <p>A 54-slot (6-row) menu titled {@code §aProfile Management} laying out the
 * eight Hypixel-style fruit profile slots across the first inner row (slots
 * 10-17), framed by a {@code GRAY_STAINED_GLASS_PANE} border. The first slot is
 * the player's active profile, shown on a {@code GRASS_BLOCK}; the remaining
 * slots are empty and offer to create a new profile. An info item and a close
 * button sit on the bottom row.</p>
 */
public class ProfileManagementMenu extends Menu {

    /** The eight profile slots displayed across the first inner row. */
    private static final int[] PROFILE_SLOTS = {10, 11, 12, 13, 14, 15, 16, 17};

    /** The eight Hypixel fruit profile names, in order. */
    private static final String[] PROFILE_NAMES = {
            "Apple", "Banana", "Blueberry", "Coconut",
            "Cucumber", "Grapes", "Kiwi", "Lemon"
    };

    /** Slot for the info item. */
    private static final int INFO_SLOT = 49;

    /** Slot for the close button. */
    private static final int CLOSE_SLOT = 53;

    private final Player player;

    public ProfileManagementMenu(Player player) {
        super("§aProfile Management", 6);
        this.player = player;
    }

    @Override
    protected void build() {
        ItemStack pane = new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE).displayName("§r").build();
        for (int slot = 0; slot < 54; slot++) {
            int column = slot % 9;
            if (slot < 9 || slot >= 45 || column == 0 || column == 8) {
                setItem(slot, pane);
            }
        }

        PlayerProfile profile = ProfileManager.getInstance().getProfile(player.getUniqueId());

        // The first slot is the player's active profile; the rest are empty.
        for (int i = 0; i < PROFILE_NAMES.length; i++) {
            boolean active = i == 0 && profile != null;
            if (active) {
                setItem(PROFILE_SLOTS[i], new ItemBuilder(Material.GRASS_BLOCK)
                        .displayName("§a" + PROFILE_NAMES[i])
                        .lore(
                                "§7Active profile",
                                "§8" + player.getName())
                        .build());
            } else {
                setItem(PROFILE_SLOTS[i], new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE)
                        .displayName("§7" + PROFILE_NAMES[i])
                        .lore("§7Empty profile slot")
                        .build());
            }
        }

        setItem(INFO_SLOT, new ItemBuilder(Material.BOOK)
                .displayName("§aProfile Management")
                .lore("§7Profiles: §f" + PROFILE_NAMES.length)
                .build());

        setItem(CLOSE_SLOT, new ItemBuilder(Material.BARRIER)
                .displayName("§cClose")
                .build(), e -> e.getWhoClicked().closeInventory());
    }
}
