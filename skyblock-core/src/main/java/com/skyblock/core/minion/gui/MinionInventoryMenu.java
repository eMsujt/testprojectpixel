package com.skyblock.core.minion.gui;

import com.skyblock.core.manager.MinionManager;
import com.skyblock.core.util.ItemBuilder;
import com.skyblock.core.util.SkyblockUtils;
import com.skyblock.core.menu.Menu;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

/**
 * The collected-resource inventory for a single placed minion.
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

    private final MinionManager.MinionData data;

    public MinionInventoryMenu(MinionManager.MinionData data) {
        super("§a" + data.type.getDisplayName() + " " + SkyblockUtils.toRoman(data.getTier().ordinal() + 1), 3);
        this.data = data;
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
