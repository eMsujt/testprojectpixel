package com.skyblock.core.menu;

import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import com.skyblock.core.manager.SkillManager;
import com.skyblock.core.model.Skill;
import com.skyblock.core.util.ItemBuilder;

public class SkillsMenu extends AbstractMenu {

    private static final Skill[] SKILLS = {
        Skill.FARMING, Skill.MINING, Skill.COMBAT, Skill.FORAGING,
        Skill.FISHING, Skill.ENCHANTING, Skill.ALCHEMY, Skill.TAMING
    };

    private static final Color[] COLORS = {
        Color.fromRGB(0,   200, 0),    // Farming  — green
        Color.fromRGB(128, 128, 128),  // Mining   — gray
        Color.fromRGB(200, 0,   0),    // Combat   — red
        Color.fromRGB(100, 60,  0),    // Foraging — brown
        Color.fromRGB(0,   150, 200),  // Fishing  — aqua
        Color.fromRGB(150, 0,   200),  // Enchanting — purple
        Color.fromRGB(255, 200, 0),    // Alchemy  — yellow
        Color.fromRGB(200, 100, 0),    // Taming   — orange
    };

    // Two centred rows (1 and 2), four icons each
    private static final int[] SLOTS = {10, 12, 14, 16, 19, 21, 23, 25};

    public SkillsMenu(JavaPlugin plugin, Player player) {
        super(plugin, player, "§aSkills", 54);
    }

    @Override
    protected void populate() {
        fillBorder();
        SkillManager skills = SkillManager.getInstance();
        for (int i = 0; i < SKILLS.length; i++) {
            Skill skill = SKILLS[i];
            long xp = skills.getSkillXP(player.getUniqueId(), skill.key());
            int level = SkillManager.levelForXp(skill.key(), xp);
            setItem(SLOTS[i], new ItemBuilder(Material.LEATHER_CHESTPLATE)
                    .leatherColor(COLORS[i])
                    .displayName("§a" + skill.displayName)
                    .lore("§7Level: §e" + level, "§7Total XP: §e" + xp)
                    .flags(org.bukkit.inventory.ItemFlag.HIDE_ATTRIBUTES)
                    .build(),
                    e -> e.setCancelled(true));
        }
    }

    private void fillBorder() {
        ItemStack pane = new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE)
                .displayName("§r")
                .build();
        for (int slot = 0; slot < 9; slot++) setItem(slot, pane);
        for (int slot = 45; slot < 54; slot++) setItem(slot, pane);
    }
}
