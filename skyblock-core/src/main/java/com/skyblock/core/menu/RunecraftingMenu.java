package com.skyblock.core.menu;

import com.skyblock.core.manager.RunecraftingManager;
import com.skyblock.core.manager.RunecraftingManager.RuneType;
import com.skyblock.core.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

/**
 * 6-row GUI titled {@code §5Runecrafting Table}. Shows the player's skill
 * level in the header and all rune types with their current level and count.
 */
public final class RunecraftingMenu extends AbstractSkyBlockMenu {

    private static final int[] RUNE_SLOTS = {
            19, 20, 21, 22, 23, 24, 25,
            28, 29, 30, 31
    };

    public RunecraftingMenu(Player player) {
        super(player, "§5Runecrafting Table", 6);
    }

    @Override
    protected void populate() {
        ItemStack pane = new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE).displayName("§r").build();
        for (int slot = 0; slot < 9; slot++) setItem(slot, pane);

        buildSkillHeader();
        buildRuneList();
    }

    private void buildSkillHeader() {
        RunecraftingManager mgr = RunecraftingManager.getInstance();
        int level = mgr.getSkillLevel(player.getUniqueId());
        long xp = mgr.getSkillXp(player.getUniqueId());

        List<String> lore = new ArrayList<>();
        lore.add("§7Skill Level: §d" + level + " §7/ §d" + RunecraftingManager.MAX_SKILL_LEVEL);
        lore.add("§7Total XP: §e" + xp);
        if (level < RunecraftingManager.MAX_SKILL_LEVEL) {
            long next = RunecraftingManager.XP_TABLE[level];
            lore.add("§7Next Level: §e" + next + " XP");
        } else {
            lore.add("§a§lMAXED");
        }

        setItem(4, new ItemBuilder(Material.ENCHANTING_TABLE)
                .displayName("§5Runecrafting")
                .lore(lore.toArray(new String[0]))
                .build());
    }

    private void buildRuneList() {
        RunecraftingManager mgr = RunecraftingManager.getInstance();
        RuneType[] types = RuneType.values();
        for (int i = 0; i < RUNE_SLOTS.length && i < types.length; i++) {
            RuneType type = types[i];
            int level = mgr.getRuneLevel(player.getUniqueId(), type);
            int count = mgr.getRuneCount(player.getUniqueId(), type);

            List<String> lore = new ArrayList<>();
            lore.add("§7Level: §d" + level + " §7/ §d" + type.getMaxLevel());
            lore.add("§7Owned: §e" + count);

            setItem(RUNE_SLOTS[i], new ItemBuilder(Material.MAGMA_CREAM)
                    .displayName("§5" + formatName(type.name()) + " Rune")
                    .lore(lore.toArray(new String[0]))
                    .build());
        }
    }

    private static String formatName(String name) {
        return name.charAt(0) + name.substring(1).toLowerCase();
    }
}
