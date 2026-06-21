package com.skyblock.core.menu;

import com.skyblock.core.manager.MinionManager;
import com.skyblock.core.manager.MinionManager.MinionData;
import com.skyblock.core.util.ItemBuilder;
import com.skyblock.core.util.SkyblockUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

/**
 * 6-row Minions overview menu backed by {@link MinionManager}.
 *
 * <p>Gray-pane border on the top and bottom rows and the leftmost/rightmost
 * columns. Slot 4 shows a DISPENSER header item. The 28 inner slots (rows 1–4,
 * columns 1–7) list each placed minion sorted by type name. An empty state
 * renders a BARRIER at slot 22. Slot 53 shows a slot-count summary.</p>
 */
public final class MinionsMenu extends Menu {

    static final int HEADER_SLOT  = 4;
    static final int EMPTY_SLOT   = 22;
    static final int SUMMARY_SLOT = 53;

    static final int[] INNER_SLOTS = {
        10, 11, 12, 13, 14, 15, 16,
        19, 20, 21, 22, 23, 24, 25,
        28, 29, 30, 31, 32, 33, 34,
        37, 38, 39, 40, 41, 42, 43
    };

    private final UUID owner;

    public MinionsMenu(Player player) {
        super("§6Minions", 6);
        this.owner = player.getUniqueId();
    }

    @Override
    protected void build() {
        ItemStack pane = new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE).displayName("§r").build();
        for (int slot = 0; slot < 54; slot++) {
            int col = slot % 9;
            if (slot < 9 || slot >= 45 || col == 0 || col == 8) {
                setItem(slot, pane);
            }
        }

        MinionManager manager = MinionManager.getInstance();
        List<UUID> minionIds = manager.getMinions(owner);
        int maxSlots = manager.getMaxSlots(owner);

        setItem(HEADER_SLOT, new ItemBuilder(Material.DISPENSER)
                .displayName("§6Minions")
                .lore(
                        "§7Placed: §e" + minionIds.size() + " §7/ §e" + maxSlots,
                        "§7Click a minion to manage it.")
                .build());

        if (minionIds.isEmpty()) {
            setItem(EMPTY_SLOT, new ItemBuilder(Material.BARRIER)
                    .displayName("§cNo Minions Placed")
                    .lore("§7Place minions in the world", "§7to see them here.")
                    .build());
        } else {
            List<MinionData> minions = new ArrayList<>();
            for (UUID id : minionIds) {
                MinionData data = manager.getMinion(id);
                if (data != null) minions.add(data);
            }
            minions.sort(Comparator.comparing(d -> d.type.getDisplayName()));

            for (int i = 0; i < minions.size() && i < INNER_SLOTS.length; i++) {
                MinionData data = minions.get(i);
                setItem(INNER_SLOTS[i], new ItemBuilder(Material.DISPENSER)
                        .displayName("§a" + data.type.getDisplayName())
                        .lore(
                                "§7Tier: §e" + SkyblockUtils.toRoman(data.getTier().ordinal() + 1),
                                "§7Fuel: §e" + pretty(data.getFuel()),
                                "§7Upgrade 1: §e" + pretty(data.getUpgrade(0)),
                                "§7Upgrade 2: §e" + pretty(data.getUpgrade(1)),
                                "",
                                "§eClick to manage!")
                        .build());
            }
        }

        setItem(SUMMARY_SLOT, new ItemBuilder(Material.PAPER)
                .displayName("§fMinion Slots")
                .lore("§7Used: §e" + minionIds.size() + " §7/ §e" + maxSlots)
                .build());
    }

    private static String pretty(Enum<?> value) {
        String[] parts = value.name().toLowerCase().split("_");
        StringBuilder sb = new StringBuilder();
        for (String part : parts) {
            if (part.isEmpty()) continue;
            if (sb.length() > 0) sb.append(' ');
            sb.append(Character.toUpperCase(part.charAt(0))).append(part.substring(1));
        }
        return sb.toString();
    }
}
