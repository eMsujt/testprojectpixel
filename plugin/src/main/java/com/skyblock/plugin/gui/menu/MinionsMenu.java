package com.skyblock.plugin.gui.menu;

import com.skyblock.plugin.gui.ItemBuilder;
import com.skyblock.plugin.gui.Menu;
import com.skyblock.plugin.minion.Minion;
import com.skyblock.plugin.minion.MinionManager;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

/**
 * The My Minions menu.
 *
 * <p>A 54-slot (6-row) menu titled {@code §aMy Minions}, framed by a gray glass
 * border, that lists every minion the opening player has placed. Each minion
 * occupies one of the 28 inner slots (slots 10–16, 19–25, 28–34, 37–43) and is
 * shown as a {@link Material#DISPENSER} icon displaying the minion's type,
 * tier, and storage count, matching Hypixel's layout.</p>
 */
public class MinionsMenu extends Menu {

    /** Inner slots across the four centre rows, left-to-right, top-to-bottom. */
    private static final int[] INNER_SLOTS = {
            10, 11, 12, 13, 14, 15, 16,
            19, 20, 21, 22, 23, 24, 25,
            28, 29, 30, 31, 32, 33, 34,
            37, 38, 39, 40, 41, 42, 43
    };

    private final Player player;

    public MinionsMenu(Player player) {
        super("§aMy Minions", 6);
        this.player = player;
    }

    @Override
    protected void build() {
        fillBorder();

        List<Minion> placedMinions = MinionManager.getInstance().getMinions(player.getUniqueId());

        for (int i = 0; i < placedMinions.size() && i < INNER_SLOTS.length; i++) {
            Minion minion = placedMinions.get(i);
            int tierNumber = minion.getTier().ordinal() + 1;
            setItem(INNER_SLOTS[i], new ItemBuilder(Material.DISPENSER)
                    .displayName("§a" + minion.type.getDisplayName())
                    .lore(
                            "§7Tier: §e" + tierNumber,
                            "§7Storage: §e" + minion.getStorage().size() + " items",
                            "",
                            "§eClick to manage!")
                    .build(),
                    event -> new MinionsMenu(player).open(player));
        }

        if (placedMinions.isEmpty()) {
            setItem(22, new ItemBuilder(Material.BARRIER)
                    .displayName("§cNo Minions Placed")
                    .lore("§7Place minions in the world", "§7to see them here.")
                    .build());
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
