package com.skyblock.plugin.gui.menu;

import com.skyblock.plugin.gui.ItemBuilder;
import com.skyblock.plugin.gui.Menu;
import com.skyblock.plugin.managers.QuestManager;
import com.skyblock.plugin.managers.QuestManager.Quest;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class QuestsMenu extends Menu {

    private static final int[] INNER_SLOTS = {
            10, 11, 12, 13, 14, 15, 16,
            19, 20, 21, 22, 23, 24, 25,
            28, 29, 30, 31, 32, 33, 34,
            37, 38, 39, 40, 41, 42, 43
    };

    private final Player player;

    public QuestsMenu(Player player) {
        super("§dQuests & Objectives", 6);
        this.player = player;
    }

    @Override
    protected void build() {
        fillBorder();

        setItem(4, new ItemBuilder(Material.MAP)
                .displayName("§eObjectives")
                .lore("§7Track your active quests.")
                .build());

        QuestManager manager = QuestManager.getInstance();
        Quest active = manager.getActiveQuest(player.getUniqueId());
        Quest completed = manager.getLastCompletedQuest(player.getUniqueId());
        Quest[] quests = Quest.values();

        for (int i = 0; i < quests.length && i < INNER_SLOTS.length; i++) {
            Quest quest = quests[i];
            boolean isActive = quest.equals(active);
            boolean isCompleted = quest.equals(completed);

            Material icon = isCompleted ? Material.WRITTEN_BOOK
                    : isActive ? Material.BOOK
                    : Material.BOOK;

            String status = isCompleted ? "§aCompleted"
                    : isActive ? "§eIn Progress"
                    : "§7Not Started";

            setItem(INNER_SLOTS[i], new ItemBuilder(icon)
                    .displayName((isActive ? "§e" : isCompleted ? "§a" : "§f")
                            + formatName(quest.name()))
                    .lore("§7Status: " + status,
                            "",
                            "§eClick to start!")
                    .build());
        }

        if (quests.length == 0) {
            setItem(22, new ItemBuilder(Material.BARRIER)
                    .displayName("§cNo Quests Available")
                    .lore("§7There are no quests to display.")
                    .build());
        }
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

    private static String formatName(String enumName) {
        String[] words = enumName.toLowerCase().split("_");
        StringBuilder sb = new StringBuilder();
        for (String word : words) {
            if (sb.length() > 0) sb.append(' ');
            sb.append(Character.toUpperCase(word.charAt(0))).append(word.substring(1));
        }
        return sb.toString();
    }
}
