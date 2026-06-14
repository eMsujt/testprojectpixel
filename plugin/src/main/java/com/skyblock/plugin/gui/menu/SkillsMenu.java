package com.skyblock.plugin.gui.menu;

import com.skyblock.plugin.gui.ItemBuilder;
import com.skyblock.plugin.gui.Menu;
import com.skyblock.plugin.managers.SkillsManager;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

/**
 * The Skills hub menu.
 *
 * <p>A 54-slot (6-row) chest titled {@code §bYour Skills} with a gray glass-pane
 * border. Ten skill icons are placed in alternating inner slots across rows 1–3,
 * each showing the player's current level and total XP.</p>
 */
public class SkillsMenu extends Menu {

    private enum Skill {
        FARMING   ("Farming",     "farming",     Material.WHEAT),
        MINING    ("Mining",      "mining",      Material.IRON_PICKAXE),
        COMBAT    ("Combat",      "combat",      Material.IRON_SWORD),
        FORAGING  ("Foraging",    "foraging",    Material.OAK_LOG),
        FISHING   ("Fishing",     "fishing",     Material.FISHING_ROD),
        ENCHANTING("Enchanting",  "enchanting",  Material.BOOKSHELF),
        ALCHEMY   ("Alchemy",     "alchemy",     Material.BREWING_STAND),
        TAMING    ("Taming",      "taming",      Material.BONE),
        CARPENTRY ("Carpentry",   "carpentry",   Material.CRAFTING_TABLE),
        RUNECRAFTING("Runecrafting","runecrafting",Material.NETHER_STAR);

        private final String displayName;
        private final String key;
        private final Material icon;

        Skill(String displayName, String key, Material icon) {
            this.displayName = displayName;
            this.key = key;
            this.icon = icon;
        }
    }

    /** Alternating inner slots across rows 1–3 (cols 1,3,5,7 then 1,3,5,7 then 1,3). */
    private static final int[] SLOTS = {10, 12, 14, 16, 19, 21, 23, 25, 28, 30};

    private final UUID playerId;

    public SkillsMenu(UUID playerId) {
        super("§bYour Skills", 6);
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
                    .build(),
                    e -> {
                        e.setCancelled(true);
                        new SkillDetailMenu(playerId, skill.displayName, skill.key, skill.icon)
                                .open((org.bukkit.entity.Player) e.getWhoClicked());
                    });
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
