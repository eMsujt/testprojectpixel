package com.skyblock.plugin.gui.menu;

import com.skyblock.plugin.gui.ItemBuilder;
import com.skyblock.plugin.gui.Menu;
import com.skyblock.plugin.managers.QuestManager;
import com.skyblock.plugin.managers.QuestManager.Quest;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class QuestsMenu extends Menu {

    private static final int STARTER_QUEST_COUNT = 8;

    private static final int[] QUEST_SLOTS = {
            10, 11, 12, 13, 14, 15, 16, 19
    };

    private final Player player;

    public QuestsMenu(Player player) {
        super("§eQuests & Objectives", 6);
        this.player = player;
    }

    @Override
    protected void build() {
        fillBorder();

        QuestManager manager = QuestManager.getInstance();
        Quest active = manager.getActiveQuest(player.getUniqueId());
        Quest completed = manager.getLastCompletedQuest(player.getUniqueId());
        Quest[] quests = Quest.values();

        int count = Math.min(STARTER_QUEST_COUNT, quests.length);
        for (int i = 0; i < count; i++) {
            Quest quest = quests[i];
            boolean isActive = quest.equals(active);
            boolean isCompleted = quest.equals(completed);

            String status = isCompleted ? "§aCompleted"
                    : isActive ? "§eIn Progress"
                    : "§7Not Started";

            String nameColor = isCompleted ? "§a" : isActive ? "§e" : "§f";

            setItem(QUEST_SLOTS[i], new ItemBuilder(Material.PAPER)
                    .displayName(nameColor + formatName(quest.name()))
                    .lore("§7Status: " + status,
                            "",
                            "§eClick to start!")
                    .build());
        }
    }

    private void fillBorder() {
        ItemStack pane = new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE)
                .displayName("§r")
                .build();
        for (int slot = 0; slot < 54; slot++) {
            if (slot < 9 || slot >= 45) {
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
