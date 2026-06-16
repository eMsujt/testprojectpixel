package com.skyblock.gui.menu;

import com.skyblock.core.util.ItemBuilder;
import com.skyblock.core.menu.Menu;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class ObjectivesMenu extends Menu {

    private static final int[] SLOTS = {
            20, 21, 22,
            29, 30, 31,
            38, 39, 40
    };

    private final Player player;
    private final List<String> completedObjectives;

    public ObjectivesMenu(Player player, List<String> completedObjectives) {
        super("§eObjectives", 6);
        this.player = player;
        this.completedObjectives = completedObjectives;
    }

    @Override
    protected void build() {
        fillBorder();

        for (int i = 0; i < SLOTS.length; i++) {
            String name = "Objective " + (i + 1);
            boolean done = completedObjectives.contains(name);

            setItem(SLOTS[i], new ItemBuilder(done ? Material.WRITTEN_BOOK : Material.BOOK)
                    .displayName((done ? "§a" : "§e") + name)
                    .lore(done ? "§aCompleted" : "§7Not started",
                            "",
                            "§eClick to view!")
                    .build(),
                    event -> open((Player) event.getWhoClicked()));
        }
    }

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
