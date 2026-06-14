package com.skyblock.plugin.gui.menu;

import com.skyblock.plugin.gui.ItemBuilder;
import com.skyblock.plugin.gui.Menu;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * The SkyBlock Settings menu.
 *
 * <p>A 54-slot (6-row) chest GUI with a gray glass-pane border. Each setting is
 * shown as a lever across a centred row; clicking a lever toggles that setting
 * on or off and refreshes the menu, matching Hypixel's layout.</p>
 */
public class SettingsMenu extends Menu {

    /** The toggleable settings, in display order. */
    private static final String[] SETTINGS = {
            "Player Visibility",
            "Trades With Strangers",
            "Combo Display",
            "Skill XP Display",
            "Action Bar Health",
            "Calendar Notifications",
            "Mob Glow"
    };

    /** The seven centred content slots, one per setting. */
    private static final int[] SLOTS = {19, 20, 21, 22, 23, 24, 25};

    /** Current on/off state for each setting, indexed alongside {@link #SETTINGS}. */
    private final boolean[] enabled = new boolean[SETTINGS.length];

    public SettingsMenu() {
        super("§8SkyBlock Settings", 6);
    }

    @Override
    protected void build() {
        fillBorder();

        for (int i = 0; i < SETTINGS.length; i++) {
            final int index = i;
            setItem(SLOTS[i], new ItemBuilder(Material.LEVER)
                            .displayName("§a" + SETTINGS[i])
                            .lore(
                                    enabled[i] ? "§7Currently: §aEnabled" : "§7Currently: §cDisabled",
                                    "§eClick to toggle!")
                            .build(),
                    event -> {
                        enabled[index] = !enabled[index];
                        open((Player) event.getWhoClicked());
                    });
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
