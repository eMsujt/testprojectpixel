package com.skyblock.plugin.gui.menus;

import com.skyblock.plugin.gui.ItemBuilder;
import com.skyblock.plugin.gui.Menu;
import com.skyblock.plugin.managers.SkillsManager;
import org.bukkit.Material;

import java.util.UUID;

/**
 * The Skills hub menu.
 *
 * <p>A 54-slot (6-row) menu presenting one {@code PLAYER_HEAD} icon per Hypixel
 * skill, showing the viewing player's current level and total XP in the lore.</p>
 */
public class SkillsMenu extends Menu {

    /** A Hypixel skill and its internal key (matching {@link SkillsManager}). */
    private enum Skill {
        FARMING("Farming", "farming"),
        MINING("Mining", "mining"),
        COMBAT("Combat", "combat"),
        FORAGING("Foraging", "foraging"),
        FISHING("Fishing", "fishing"),
        ENCHANTING("Enchanting", "enchanting"),
        ALCHEMY("Alchemy", "alchemy"),
        TAMING("Taming", "taming"),
        CARPENTRY("Carpentry", "carpentry"),
        RUNECRAFTING("Runecrafting", "runecrafting"),
        SOCIAL("Social", "social"),
        DUNGEONEERING("Dungeoneering", "dungeoneering");

        private final String displayName;
        private final String key;

        Skill(String displayName, String key) {
            this.displayName = displayName;
            this.key = key;
        }
    }

    /** Centred slots across two rows, one per skill. */
    private static final int[] SLOTS = {19, 20, 21, 22, 23, 24, 28, 29, 30, 31, 32, 33};

    private final UUID playerId;

    public SkillsMenu(UUID playerId) {
        super("Skills", 6);
        this.playerId = playerId;
    }

    @Override
    protected void build() {
        SkillsManager skills = SkillsManager.getInstance();
        Skill[] values = Skill.values();
        for (int i = 0; i < values.length; i++) {
            Skill skill = values[i];
            long totalXP = skills.getSkillXP(playerId, skill.key);
            int level = computeLevel(skill.key, totalXP);
            setItem(SLOTS[i], new ItemBuilder(Material.PLAYER_HEAD)
                    .displayName("§a" + skill.displayName + " §7Lvl " + level)
                    .lore(
                            "§7Level: §a" + level,
                            "§7Total XP: §b" + totalXP)
                    .build());
        }
    }

    /** Derives the skill level from total XP using the manager's XP table. */
    private static int computeLevel(String skill, long totalXP) {
        long[] table = SkillsManager.SKILL_XP_TABLE.get(skill);
        if (table == null) {
            return 0;
        }
        long cumulative = 0;
        int level = 0;
        for (long threshold : table) {
            cumulative += threshold;
            if (totalXP < cumulative) {
                break;
            }
            level++;
        }
        return level;
    }
}
