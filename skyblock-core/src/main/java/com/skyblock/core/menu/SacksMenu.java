package com.skyblock.core.menu;

import com.skyblock.core.manager.SackManager;
import com.skyblock.core.manager.SackManager.SackType;
import com.skyblock.core.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

public final class SacksMenu extends AbstractSkyBlockMenu {

    private static final int[] CONTENT_SLOTS = {
            10, 11, 12, 13, 14, 15, 16,
            19, 20, 21, 22, 23, 24, 25,
            28, 29, 30, 31, 32, 33, 34,
            37, 38, 39, 40, 41, 42, 43
    };

    public SacksMenu(Player player) {
        super(player, "§6Sacks of Holding", 6);
    }

    @Override
    protected void populate() {
        ItemStack pane = new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE).displayName("§r").build();
        for (int slot = 0; slot < 9; slot++) setItem(slot, pane);

        SackManager manager = SackManager.getInstance();
        SackType[] types = SackType.values();
        for (int i = 0; i < types.length && i < CONTENT_SLOTS.length; i++) {
            SackType type = types[i];
            Map<String, Integer> contents = manager.getSackContents(player.getUniqueId(), type);
            int total = contents.values().stream().mapToInt(Integer::intValue).sum();
            setItem(CONTENT_SLOTS[i], new ItemBuilder(Material.CHEST)
                    .displayName("§6" + type.getDisplayName())
                    .lore("§7Items stored: §e" + total,
                          "§eClick to open")
                    .build());
        }

        if (types.length == 0) {
            setItem(31, new ItemBuilder(Material.BARRIER)
                    .displayName("§cNo Sacks")
                    .lore("§7No sack types available.")
                    .build());
        }
    }
}
