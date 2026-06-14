package com.skyblock.plugin.gui.menu;

import com.skyblock.plugin.gui.ItemBuilder;
import com.skyblock.plugin.gui.Menu;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * The Objectives menu.
 *
 * <p>A 54-slot (6-row) menu with a gray glass-pane border. Each active
 * objective is laid out across a centred grid as a {@code BOOK} that, when
 * clicked, refreshes the menu, matching Hypixel's layout.</p>
 */
public class ObjectivesMenu extends Menu {

    /** The centred content slots, one per objective. */
    private static final int[] SLOTS = {
            20, 21, 22,
            29, 30, 31,
            38, 39, 40
    };

    public ObjectivesMenu() {
        super("§eObjectives", 6);
    }

    @Override
    protected void build() {
        fillBorder();

        for (int i = 0; i < SLOTS.length; i++) {
            int objective = i + 1;
            setItem(SLOTS[i], new ItemBuilder(Material.BOOK)
                            .displayName("§aObjective " + objective)
                            .lore(
                                    "§7Not started",
                                    "§eClick to view!")
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
