package com.skyblock.plugin.gui.menu;

import com.skyblock.plugin.gui.ItemBuilder;
import com.skyblock.plugin.gui.Menu;
import com.skyblock.plugin.minions.MinionManager;
import com.skyblock.plugin.minions.MinionManager.MinionData;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

/**
 * The SkyBlock My Minions menu.
 *
 * <p>A 54-slot (6-row) chest GUI with a gray glass-pane border. Slots 10–43
 * (inner grid) are populated with one DISPENSER icon per placed minion, showing
 * the minion type and tier. Empty inner slots are left blank.</p>
 */
public class MinionsMenu extends Menu {

    private final Player player;

    public MinionsMenu(Player player) {
        super("§aMy Minions", 6);
        this.player = player;
    }

    @Override
    protected void build() {
        fillBorder();

        List<MinionData> minions = MinionManager.getInstance().getMinions(player.getUniqueId());

        int[] innerSlots = buildInnerSlots();
        for (int i = 0; i < minions.size() && i < innerSlots.length; i++) {
            MinionData minion = minions.get(i);
            setItem(innerSlots[i], new ItemBuilder(Material.DISPENSER)
                    .displayName("§a" + minion.type() + " Minion")
                    .lore(
                            "§7Tier: §e" + minion.tier(),
                            "",
                            "§eClick to manage!")
                    .build());
        }
    }

    private int[] buildInnerSlots() {
        int[] slots = new int[4 * 7];
        int idx = 0;
        for (int row = 1; row <= 4; row++) {
            for (int col = 1; col <= 7; col++) {
                slots[idx++] = row * 9 + col;
            }
        }
        return slots;
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
