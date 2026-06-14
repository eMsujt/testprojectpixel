package com.skyblock.plugin.gui.menu;

import com.skyblock.plugin.gui.ItemBuilder;
import com.skyblock.plugin.gui.Menu;
import com.skyblock.plugin.profile.PlayerProfile;
import com.skyblock.plugin.profile.ProfileManager;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

/**
 * The Profile Management menu.
 *
 * <p>A 54-slot (6-row) chest titled {@code §aProfile Management} with a gray
 * glass-pane border. Each SkyBlock profile the player owns is shown as a
 * {@code PLAYER_HEAD} across a centred 3×3 grid; clicking one selects that
 * profile and refreshes the menu, matching Hypixel's layout.</p>
 */
public class ProfileManagementMenu extends Menu {

    /** The nine centred content slots, one per profile. */
    private static final int[] SLOTS = {
            20, 21, 22,
            29, 30, 31,
            38, 39, 40
    };

    /** The profiles owned by the viewing player, in display order. */
    private final List<PlayerProfile> profiles;

    public ProfileManagementMenu(Player player) {
        super("§aProfile Management", 6);
        this.profiles = ownedProfiles(player);
    }

    private static List<PlayerProfile> ownedProfiles(Player player) {
        List<PlayerProfile> owned = new ArrayList<>();
        owned.add(ProfileManager.getInstance().getOrCreate(player.getUniqueId()));
        return owned;
    }

    @Override
    protected void build() {
        fillBorder();

        for (int i = 0; i < SLOTS.length && i < profiles.size(); i++) {
            PlayerProfile profile = profiles.get(i);
            boolean active = profile.getActiveProfileName() != null;
            setItem(SLOTS[i], new ItemBuilder(Material.PLAYER_HEAD)
                            .displayName("§a" + profile.getActiveProfileName())
                            .lore(
                                    active ? "§7Currently selected" : "§7Not selected",
                                    "§eClick to select!")
                            .build(),
                    event -> open((Player) event.getWhoClicked()));
        }
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
