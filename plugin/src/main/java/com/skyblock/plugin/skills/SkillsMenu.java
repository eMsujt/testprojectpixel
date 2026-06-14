package com.skyblock.plugin.skills;

import com.skyblock.plugin.gui.ItemBuilder;
import com.skyblock.plugin.gui.Menu;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class SkillsMenu extends Menu {

    private enum Skill {
        FARMING("Farming", "farming", Material.GOLDEN_HOE),
        MINING("Mining", "mining", Material.STONE_PICKAXE),
        COMBAT("Combat", "combat", Material.STONE_SWORD),
        FORAGING("Foraging", "foraging", Material.OAK_LOG),
        FISHING("Fishing", "fishing", Material.FISHING_ROD),
        ENCHANTING("Enchanting", "enchanting", Material.ENCHANTING_TABLE),
        ALCHEMY("Alchemy", "alchemy", Material.BREWING_STAND),
        TAMING("Taming", "taming", Material.BONE),
        CARPENTRY("Carpentry", "carpentry", Material.CRAFTING_TABLE),
        RUNECRAFTING("Runecrafting", "runecrafting", Material.MAGMA_CREAM);

        private final String displayName;
        private final String key;
        private final Material icon;

        Skill(String displayName, String key, Material icon) {
            this.displayName = displayName;
            this.key = key;
            this.icon = icon;
        }
    }

    private static final int[] SLOTS = {19, 20, 21, 22, 23, 24, 25, 28, 29, 30};

    private final UUID playerId;

    public SkillsMenu(UUID playerId) {
        super("§aSkills", 6);
        this.playerId = playerId;
    }

    @Override
    protected void build() {
        fillBorder();

        SkillsManager skills = SkillsManager.getInstance();
        Skill[] values = Skill.values();
        for (int i = 0; i < values.length; i++) {
            Skill skill = values[i];
            long totalXP = skills.getSkillXP(playerId, skill.key);
            int level = skills.getSkillLevel(playerId, skill.key);
            setItem(SLOTS[i], new ItemBuilder(skill.icon)
                    .displayName("§a" + skill.displayName)
                    .lore(
                            "§7Level: §e" + level,
                            "§7Total XP: §e" + totalXP)
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
}
