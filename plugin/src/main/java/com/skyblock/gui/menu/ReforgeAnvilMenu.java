package com.skyblock.gui.menu;

import com.skyblock.core.util.ItemBuilder;
import com.skyblock.core.menu.Menu;
import com.skyblock.core.manager.ReforgeManager;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

/**
 * The Reforge Anvil menu.
 *
 * <p>A 54-slot (6-row) menu listing every reforge registered with
 * {@link ReforgeManager}. Each reforge renders as an {@link Material#ANVIL}
 * icon in the inner slots, its lore summarising the stat bonuses it grants.</p>
 */
public class ReforgeAnvilMenu extends Menu {

    /** Creates a reforge anvil menu backed by the shared registry. */
    public ReforgeAnvilMenu() {
        super("§5Reforge Anvil", 6);
    }

    @Override
    protected void build() {
        fillBorder();

        int slot = 10;
        for (ReforgeManager.ReforgeType reforge : ReforgeManager.ReforgeType.values()) {
            if (reforge == ReforgeManager.ReforgeType.NONE) {
                continue;
            }
            // Skip the border columns, wrapping to the next row's inner slots.
            while (slot % 9 == 0 || slot % 9 == 8) {
                slot++;
            }
            if (slot >= 45) {
                break;
            }
            setItem(slot, new ItemBuilder(Material.ANVIL)
                    .displayName("§d" + reforge.getDisplayName())
                    .lore(statLore(reforge))
                    .build());
            slot++;
        }
    }

    /** Builds the lore lines describing a reforge's non-zero stat bonuses. */
    private List<String> statLore(ReforgeManager.ReforgeType reforge) {
        List<String> lore = new ArrayList<>();
        addStat(lore, "§cStrength", reforge.getStrengthBonus());
        addStat(lore, "§aDefense", reforge.getDefenseBonus());
        addStat(lore, "§fSpeed", reforge.getSpeedBonus());
        if (lore.isEmpty()) {
            lore.add("§7No stat bonuses.");
        }
        return lore;
    }

    /** Appends a stat line if {@code value} is non-zero. */
    private void addStat(List<String> lore, String label, int value) {
        if (value != 0) {
            lore.add(label + ": §a" + (value > 0 ? "+" : "") + value);
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
