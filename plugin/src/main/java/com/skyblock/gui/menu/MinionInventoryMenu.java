package com.skyblock.gui.menu;

import com.skyblock.items.util.ItemBuilder;
import com.skyblock.core.menu.Menu;
import com.skyblock.plugin.minion.model.Minion;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

/**
 * The collected-resource inventory for a single placed {@link Minion}.
 *
 * <p>A 27-slot (3-row) menu titled with the minion's name and tier, e.g.
 * {@code §aCobblestone Minion XI}, opened when a player right-clicks their
 * minion's storage. The interior holds the resources the minion has gathered;
 * the layout is framed by a {@code GRAY_STAINED_GLASS_PANE} border with a close
 * button on the bottom row.</p>
 */
public class MinionInventoryMenu extends Menu {

    /** Slot for the close button. */
    private static final int CLOSE_SLOT = 22;

    /** Roman numerals indexed by tier number (index 0 unused). */
    private static final String[] ROMAN = {
            "", "I", "II", "III", "IV", "V",
            "VI", "VII", "VIII", "IX", "X", "XI"
    };

    private final Minion minion;

    public MinionInventoryMenu(Minion minion) {
        super("§a" + minion.type.getDisplayName() + " " + ROMAN[Math.min(minion.getTier().ordinal() + 1, ROMAN.length - 1)], 3);
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

        setItem(CLOSE_SLOT, new ItemBuilder(Material.BARRIER)
                .displayName("§cClose")
                .build(), e -> e.getWhoClicked().closeInventory());
    }
}
