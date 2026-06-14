package com.skyblock.plugin.minion;

import com.skyblock.plugin.gui.ItemBuilder;
import com.skyblock.plugin.gui.Menu;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

/**
 * The management menu for a single placed {@link Minion}.
 *
 * <p>A 27-slot (3-row) menu titled with the minion's name, e.g.
 * {@code §6Cobblestone Minion}, framed by a {@code GRAY_STAINED_GLASS_PANE}
 * border. Slot 13 shows the current minion and its tier; a close button sits on
 * the bottom row.</p>
 */
public class MinionMenu extends Menu {

    /** Slot showing the current minion. */
    private static final int MINION_SLOT = 13;

    /** Slot for the close button. */
    private static final int CLOSE_SLOT = 22;

    /** Roman numerals indexed by tier number (index 0 unused). */
    private static final String[] ROMAN = {
            "", "I", "II", "III", "IV", "V",
            "VI", "VII", "VIII", "IX", "X", "XI"
    };

    private final Minion minion;

    public MinionMenu(Minion minion) {
        super("§6" + minion.type.getDisplayName(), 3);
        this.minion = minion;
    }

    @Override
    protected void build() {
        ItemStack pane = new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE).displayName("§r").build();
        for (int slot = 0; slot < 27; slot++) {
            int column = slot % 9;
            if (slot < 9 || slot >= 18 || column == 0 || column == 8) {
                setItem(slot, pane);
            }
        }

        String tier = ROMAN[minion.getTier().ordinal() + 1];
        setItem(MINION_SLOT, new ItemBuilder(Material.PLAYER_HEAD)
                .displayName("§a" + minion.type.getDisplayName() + " " + tier)
                .lore("§7Tier: §e" + tier)
                .build());

        setItem(CLOSE_SLOT, new ItemBuilder(Material.BARRIER)
                .displayName("§cClose")
                .build(), e -> e.getWhoClicked().closeInventory());
    }
}
