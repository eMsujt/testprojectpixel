package com.skyblock.plugin.menus;

import com.skyblock.core.util.ItemBuilder;
import com.skyblock.plugin.gui.Menu;
import com.skyblock.plugin.managers.QuestManager;
import com.skyblock.plugin.managers.QuestManager.Quest;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * The Quests &amp; Objectives menu.
 *
 * <p>A 54-slot (6-row) menu titled {@code §eQuests &amp; Objectives} showing seven
 * starter quests across the first inner row (slots 10-16), framed by a
 * {@code GRAY_STAINED_GLASS_PANE} border. Clicking a quest starts it (unless one
 * is already active or it has been completed); an info item and a close button
 * sit on the bottom row.</p>
 */
public class QuestsMenu extends Menu {

    /** The seven starter quest slots displayed across the first inner row. */
    private static final int[] QUEST_SLOTS = {10, 11, 12, 13, 14, 15, 16};

    /** The seven starter quests, one per displayed slot. */
    private static final Quest[] QUESTS = {
            Quest.SLAYER_QUEST,
            Quest.FISHING_QUEST,
            Quest.MINING_QUEST,
            Quest.FARMING_QUEST,
            Quest.COMBAT_QUEST,
            Quest.DUNGEON_QUEST,
            Quest.FORAGING_QUEST
    };

    /** Slot for the info item. */
    private static final int INFO_SLOT = 49;

    /** Slot for the close button. */
    private static final int CLOSE_SLOT = 53;

    private final Player player;

    public QuestsMenu(Player player) {
        super("§eQuests & Objectives", 6);
        this.player = player;
    }

    @Override
    protected void build() {
        ItemStack pane = new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE).displayName("§r").build();
        for (int slot = 0; slot < 54; slot++) {
            int column = slot % 9;
            if (slot < 9 || slot >= 45 || column == 0 || column == 8) {
                setItem(slot, pane);
            }
        }

        QuestManager manager = QuestManager.getInstance();
        for (int i = 0; i < QUESTS.length; i++) {
            Quest quest = QUESTS[i];
            boolean active = quest.equals(manager.getActiveQuest(player.getUniqueId()));
            boolean completed = quest.equals(manager.getLastCompletedQuest(player.getUniqueId()));
            String status = completed ? "§aCompleted" : active ? "§eIn progress" : "§7Click to start";
            setItem(QUEST_SLOTS[i],
                    new ItemBuilder(active ? Material.WRITABLE_BOOK : completed ? Material.ENCHANTED_BOOK : Material.PAPER)
                            .displayName("§e" + name(quest))
                            .lore(status)
                            .build(),
                    (active || completed) ? null : event -> {
                        manager.startQuest(player.getUniqueId(), quest);
                        player.sendMessage("§aStarted " + name(quest) + ".");
                        player.closeInventory();
                    });
        }

        setItem(INFO_SLOT, new ItemBuilder(Material.BOOK)
                .displayName("§aQuests & Objectives")
                .lore("§7Starter quests: §f" + QUESTS.length)
                .build());

        setItem(CLOSE_SLOT, new ItemBuilder(Material.BARRIER)
                .displayName("§cClose")
                .build(), e -> e.getWhoClicked().closeInventory());
    }

    /** Converts a quest enum constant to a readable title (e.g. {@code SLAYER_QUEST} → {@code Slayer Quest}). */
    private static String name(Quest quest) {
        String[] words = quest.name().toLowerCase().split("_");
        StringBuilder sb = new StringBuilder();
        for (String word : words) {
            if (sb.length() > 0) {
                sb.append(' ');
            }
            sb.append(Character.toUpperCase(word.charAt(0))).append(word.substring(1));
        }
        return sb.toString();
    }
}
