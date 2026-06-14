package com.skyblock.plugin.gui.menu;

import com.skyblock.plugin.gui.ItemBuilder;
import com.skyblock.plugin.gui.Menu;
import com.skyblock.plugin.manager.ProfileManager;
import com.skyblock.plugin.profile.SkyBlockProfile;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * The SkyBlock Settings menu.
 *
 * <p>A 54-slot (6-row) chest GUI with a gray glass-pane border. Each toggleable
 * setting is shown as a lever across a centred row; clicking a lever flips that
 * setting on the player's {@link SkyBlockProfile} and refreshes the menu,
 * matching Hypixel's layout.</p>
 */
public class SettingsMenu extends Menu {

    private final Player player;

    public SettingsMenu(Player player) {
        super("§7SkyBlock Settings", 5);
        this.player = player;
    }

    @Override
    protected void build() {
        fillBorder();

        SkyBlockProfile profile = ProfileManager.getInstance().getOrCreateProfile(player.getUniqueId());

        setItem(20, lever("§aSkill Notifications", profile.isShowSkillNotifications()),
                event -> {
                    profile.setShowSkillNotifications(!profile.isShowSkillNotifications());
                    open((Player) event.getWhoClicked());
                });

        setItem(24, lever("§aCollection Notifications", profile.isShowCollectionNotifications()),
                event -> {
                    profile.setShowCollectionNotifications(!profile.isShowCollectionNotifications());
                    open((Player) event.getWhoClicked());
                });
    }

    /** Builds a lever icon showing the setting's name and current on/off state. */
    private ItemStack lever(String name, boolean enabled) {
        return new ItemBuilder(Material.LEVER)
                .displayName(name)
                .lore(
                        enabled ? "§7Currently: §aEnabled" : "§7Currently: §cDisabled",
                        "§eClick to toggle!")
                .build();
    }

    /** Fills the menu's outer edge with gray glass panes, matching Hypixel. */
    private void fillBorder() {
        ItemStack pane = new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE)
                .displayName("§r")
                .build();
        for (int slot = 0; slot < 45; slot++) {
            int column = slot % 9;
            if (slot < 9 || slot >= 36 || column == 0 || column == 8) {
                setItem(slot, pane);
            }
        }
    }
}
