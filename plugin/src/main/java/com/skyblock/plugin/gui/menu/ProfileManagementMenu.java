package com.skyblock.plugin.gui.menu;

import com.skyblock.plugin.gui.ItemBuilder;
import com.skyblock.plugin.gui.Menu;
import com.skyblock.plugin.profile.PlayerProfile;
import com.skyblock.plugin.profile.ProfileManager;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * The Profile Management menu.
 *
 * <p>A 54-slot (6-row) menu with a gray glass-pane border. Each profile the
 * player owns is shown as a {@code PLAYER_HEAD} across a centred row, matching
 * Hypixel's layout. Only owned profiles are rendered; the active profile is
 * highlighted by its name.</p>
 */
public class ProfileManagementMenu extends Menu {

    /** The centred content slots, one per owned profile. */
    private static final int[] SLOTS = {20, 21, 22, 23, 24};

    private final Player player;

    public ProfileManagementMenu(Player player) {
        super("§aProfile Management", 6);
        this.player = player;
    }

    @Override
    protected void build() {
        fillBorder();

        PlayerProfile profile = ProfileManager.getInstance().getProfile(player.getUniqueId());
        // Only render a head for a profile the player actually owns.
        if (profile == null) {
            return;
        }

        String activeName = profile.getActiveProfileName();
        setItem(SLOTS[0], new ItemBuilder(Material.PLAYER_HEAD)
                .displayName(activeName != null ? "§a" + activeName : "§7Profile")
                .lore(
                        "§7Active profile",
                        "§8" + player.getName())
                .build());
    }

    /** Fills the menu's outer edge with gray glass panes, matching Hypixel. */
    private void fillBorder() {
        ItemStack pane = new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE)
                .displayName("§r")
                .build();
        for (int slot = 0; slot < 54; slot++) {
            int column = slot % 9;
            if (slot < 9 || slot >= 45 || column == 0 || column == 8) {
                setItem(slot, pane);
            }
        }
    }
}
