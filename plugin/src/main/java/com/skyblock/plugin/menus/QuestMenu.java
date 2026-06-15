package com.skyblock.plugin.menus;

import com.skyblock.core.util.ItemBuilder;
import com.skyblock.plugin.gui.Menu;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

/**
 * The Quests &amp; Objectives menu.
 *
 * <p>A 54-slot (6-row) menu titled {@code §aQuests &amp; Objectives} that lists the
 * player's active objectives across the inner slots, framed by a
 * {@code GRAY_STAINED_GLASS_PANE} border. Each active objective is rendered as a
 * {@code PAPER} icon; a close button sits on the bottom row.</p>
 */
public class QuestMenu extends Menu {

    /** Inner slots used to display active objectives (rows 2–5, columns 1–7). */
    private static final int[] OBJECTIVE_SLOTS = {
            10, 11, 12, 13, 14, 15, 16,
            19, 20, 21, 22, 23, 24, 25,
            28, 29, 30, 31, 32, 33, 34,
            37, 38, 39, 40, 41, 42, 43
    };

    /** Slot for the close button. */
    private static final int CLOSE_SLOT = 53;

    private final List<String> active;

    public QuestMenu(List<String> active) {
        super("§aQuests & Objectives", 6);
        this.active = active != null ? new ArrayList<>(active) : new ArrayList<>();
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

        int count = Math.min(active.size(), OBJECTIVE_SLOTS.length);
        for (int i = 0; i < count; i++) {
            setItem(OBJECTIVE_SLOTS[i], new ItemBuilder(Material.PAPER)
                    .displayName("§a" + active.get(i))
                    .lore("§7Active objective")
                    .build());
        }

        setItem(CLOSE_SLOT, new ItemBuilder(Material.BARRIER)
                .displayName("§cClose")
                .build(), e -> e.getWhoClicked().closeInventory());
    }
}
