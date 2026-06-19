package com.skyblock.core.quest.gui;

import com.skyblock.core.manager.QuestManager;
import com.skyblock.core.manager.QuestManager.QuestStatus;
import com.skyblock.core.manager.QuestManager.QuestType;
import com.skyblock.core.util.ItemBuilder;
import com.skyblock.core.menu.Menu;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * The canonical Quests &amp; Objectives menu. A 54-slot (6-row) chest GUI titled
 * {@code §eQuests &amp; Objectives} showing all {@link QuestType} entries across
 * inner slots, framed by a {@code GRAY_STAINED_GLASS_PANE} border. Clicking a
 * NOT_STARTED quest starts it; a close button sits at slot 49.
 */
public final class QuestsMenu extends Menu {

    private static final String TITLE = "§eQuests & Objectives";

    /** Inner slots used to display quests (rows 2–3, columns 1–7). */
    private static final int[] QUEST_SLOTS = {10, 11, 12, 13, 14, 15, 16, 19, 20, 21};

    private static final int CLOSE_SLOT = 49;

    private final Player player;

    public QuestsMenu(Player player) {
        super(TITLE, 6);
        this.player = player;
    }

    @Override
    protected void build() {
        ItemStack pane = new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE).displayName("§r").build();
        for (int slot = 0; slot < 54; slot++) {
            int col = slot % 9;
            if (slot < 9 || slot >= 45 || col == 0 || col == 8) {
                setItem(slot, pane, e -> e.setCancelled(true));
            }
        }

        QuestManager manager = QuestManager.getInstance();
        QuestType[] types = QuestType.values();
        int count = Math.min(types.length, QUEST_SLOTS.length);
        for (int i = 0; i < count; i++) {
            QuestType type = types[i];
            QuestStatus status = manager.getStatus(player.getUniqueId(), type);
            Material icon = status == QuestStatus.COMPLETED ? Material.ENCHANTED_BOOK
                    : status == QuestStatus.IN_PROGRESS ? Material.WRITABLE_BOOK
                    : Material.PAPER;
            String color = status == QuestStatus.COMPLETED ? "§a"
                    : status == QuestStatus.IN_PROGRESS ? "§e"
                    : "§f";
            String statusLine = status == QuestStatus.COMPLETED ? "§aCompleted"
                    : status == QuestStatus.IN_PROGRESS
                            ? "§eIn Progress §7(" + manager.getProgress(player.getUniqueId(), type) + "§7/§f" + type.getGoal() + "§7)"
                    : "§7Not Started";
            ItemBuilder builder = new ItemBuilder(icon)
                    .displayName(color + type.getDisplayName())
                    .lore("§7Status: " + statusLine);
            if (status == QuestStatus.NOT_STARTED) {
                builder.addLore("§eClick to start!");
            }
            setItem(QUEST_SLOTS[i], builder.build(), e -> {
                e.setCancelled(true);
                if (manager.getStatus(player.getUniqueId(), type) == QuestStatus.NOT_STARTED) {
                    manager.startQuest(player.getUniqueId(), type);
                    player.sendMessage("§aStarted quest: §f" + type.getDisplayName() + "§a!");
                    player.closeInventory();
                }
            });
        }

        setItem(CLOSE_SLOT, new ItemBuilder(Material.BARRIER)
                .displayName("§cClose")
                .build(),
                e -> {
                    e.setCancelled(true);
                    e.getWhoClicked().closeInventory();
                });
    }
}
