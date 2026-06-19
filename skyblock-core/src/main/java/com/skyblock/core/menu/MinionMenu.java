package com.skyblock.core.menu;

import com.skyblock.core.manager.MinionManager;
import com.skyblock.core.manager.MinionManager.MinionData;
import com.skyblock.core.util.SkyblockUtil.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.UUID;

/**
 * Canonical 54-slot Minion management menu. The top and bottom rows are
 * gray-pane borders; rows 1–2 hold the 12 minion slots (6 per row, bordered
 * left and right); row 3 is a gray-pane separator; row 4 is unused; slot 53
 * shows a slot-count summary. Occupied slots render as a colored-terracotta
 * item labeled with the minion's display name and key stats; empty unlocked
 * slots are light-gray panes; locked slots (beyond the player's current cap)
 * are red panes.
 */
public final class MinionMenu extends Menu {

    static final int[] MINION_SLOTS = {
            10, 11, 12, 13, 14, 15,
            19, 20, 21, 22, 23, 24
    };

    private static final Material[] TERRACOTTA_COLORS = {
            Material.WHITE_TERRACOTTA,
            Material.ORANGE_TERRACOTTA,
            Material.MAGENTA_TERRACOTTA,
            Material.LIGHT_BLUE_TERRACOTTA,
            Material.YELLOW_TERRACOTTA,
            Material.LIME_TERRACOTTA,
            Material.PINK_TERRACOTTA,
            Material.GRAY_TERRACOTTA,
            Material.LIGHT_GRAY_TERRACOTTA,
            Material.CYAN_TERRACOTTA,
            Material.PURPLE_TERRACOTTA,
            Material.BLUE_TERRACOTTA,
            Material.BROWN_TERRACOTTA,
            Material.GREEN_TERRACOTTA,
            Material.RED_TERRACOTTA,
            Material.BLACK_TERRACOTTA
    };

    private final UUID owner;

    public MinionMenu(UUID owner) {
        super("§6Minions", 6);
        this.owner = owner;
    }

    @Override
    protected void build() {
        ItemStack pane = new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE).displayName("§r").build();

        for (int slot = 0; slot < 9; slot++) setItem(slot, pane);
        setItem(9, pane);
        setItem(16, pane);
        setItem(17, pane);
        setItem(18, pane);
        setItem(25, pane);
        setItem(26, pane);
        for (int slot = 27; slot < 36; slot++) setItem(slot, pane);
        for (int slot = 45; slot < 53; slot++) setItem(slot, pane);

        MinionManager manager = MinionManager.getInstance();
        List<UUID> minionIds = manager.getMinions(owner);
        int maxSlots = manager.getMaxSlots(owner);

        for (int i = 0; i < MINION_SLOTS.length; i++) {
            int slot = MINION_SLOTS[i];
            if (i < minionIds.size()) {
                MinionData data = manager.getMinion(minionIds.get(i));
                if (data != null) {
                    Material mat = TERRACOTTA_COLORS[data.type.ordinal() % TERRACOTTA_COLORS.length];
                    setItem(slot, new ItemBuilder(mat)
                            .displayName("§a" + data.type.getDisplayName())
                            .lore(
                                    "§7Tier: §e" + (data.getTier().ordinal() + 1),
                                    "§7Stored: §f" + data.getStoredResources(),
                                    "§7Fuel: §f" + data.getFuel().name().replace('_', ' '))
                            .build());
                }
            } else if (i < maxSlots) {
                setItem(slot, new ItemBuilder(Material.LIGHT_GRAY_STAINED_GLASS_PANE)
                        .displayName("§7Empty Minion Slot")
                        .lore("§7Place a minion to fill this slot.")
                        .build());
            } else {
                setItem(slot, new ItemBuilder(Material.RED_STAINED_GLASS_PANE)
                        .displayName("§cLocked Slot")
                        .lore("§7Upgrade your island to unlock more slots.")
                        .build());
            }
        }

        setItem(53, new ItemBuilder(Material.PAPER)
                .displayName("§fMinion Slots")
                .lore("§7Used: §e" + minionIds.size() + " §7/ §e" + maxSlots)
                .build());
    }
}
