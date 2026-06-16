package com.skyblock.core.minion.gui;

import com.skyblock.core.manager.MinionManager;
import com.skyblock.core.manager.MinionManager.MinionData;
import com.skyblock.core.menu.Menu;
import com.skyblock.items.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.UUID;

/**
 * Canonical "My Minions" overview menu.
 *
 * <p>A 54-slot (6-row) chest titled {@code §aMy Minions}, framed by a gray glass
 * pane border. The 28 inner slots (rows 1–4) show each placed minion as a
 * {@link Material#DISPENSER} icon. Clicking a minion opens its {@link MinionMenu}.
 * An empty state shows a {@link Material#BARRIER} at slot 22.</p>
 *
 * <p>All other MinionsMenu/MinionGui-as-overview classes in the project are
 * deprecated stubs that delegate here.</p>
 */
public class MinionsMenu extends Menu {

    private static final int[] INNER_SLOTS = {
        10, 11, 12, 13, 14, 15, 16,
        19, 20, 21, 22, 23, 24, 25,
        28, 29, 30, 31, 32, 33, 34,
        37, 38, 39, 40, 41, 42, 43
    };

    private static final String[] ROMAN = {
        "", "I", "II", "III", "IV", "V",
        "VI", "VII", "VIII", "IX", "X", "XI"
    };

    private final Player player;

    public MinionsMenu(Player player) {
        super("§aMy Minions", 6);
        this.player = player;
    }

    @Override
    protected void build() {
        fillBorder();

        MinionManager manager = MinionManager.getInstance();
        List<UUID> minionIds = manager.getMinions(player.getUniqueId());

        if (minionIds.isEmpty()) {
            setItem(22, new ItemBuilder(Material.BARRIER)
                    .displayName("§cNo Minions Placed")
                    .lore("§7Place minions in the world", "§7to see them here.")
                    .build());
            return;
        }

        for (int i = 0; i < minionIds.size() && i < INNER_SLOTS.length; i++) {
            MinionData data = manager.getMinion(minionIds.get(i));
            if (data == null) continue;
            String tier = roman(data.getTier());
            setItem(INNER_SLOTS[i], new ItemBuilder(Material.DISPENSER)
                    .displayName("§a" + data.type.getDisplayName())
                    .lore(
                            "§7Tier: §e" + tier,
                            "",
                            "§eClick to manage!")
                    .build(),
                    event -> new MinionMenu(data).open(player));
        }
    }

    private void fillBorder() {
        ItemStack pane = new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE)
                .displayName("§r")
                .build();
        for (int slot = 0; slot < 54; slot++) {
            int col = slot % 9;
            if (slot < 9 || slot >= 45 || col == 0 || col == 8) {
                setItem(slot, pane);
            }
        }
    }

    private static String roman(MinionManager.MinionTier tier) {
        int index = tier.ordinal() + 1;
        return index < ROMAN.length ? ROMAN[index] : String.valueOf(index);
    }
}
