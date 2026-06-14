package com.skyblock.plugin.gui.menus;

import com.skyblock.plugin.gui.ItemBuilder;
import com.skyblock.plugin.gui.Menu;
import com.skyblock.plugin.managers.SkillsManager;
import org.bukkit.Material;

import java.util.Map;
import java.util.UUID;

/**
 * A 54-slot Skills menu showing one player-skull icon per skill, with the
 * player's current level and total XP in the lore.
 */
public class SkillsMenu extends Menu {

    private final UUID playerId;

    /**
     * Creates a Skills menu for the given player.
     *
     * @param playerId the player whose skills are displayed
     */
    public SkillsMenu(UUID playerId) {
        super("Your Skills", 6);
        this.playerId = playerId;
    }

    @Override
    protected void build() {
        SkillsManager skills = SkillsManager.getInstance();
        int slot = 10;
        for (String skill : SkillsManager.SKILL_XP_TABLE.keySet()) {
            long xp = skills.getSkillXP(playerId, skill);
            int level = computeLevel(skill, xp);
            String name = skill.substring(0, 1).toUpperCase() + skill.substring(1);
            setItem(slot, new ItemBuilder(Material.PLAYER_HEAD)
                    .displayName("§a" + name)
                    .lore(
                            "§7Level: §e" + level,
                            "§7Total XP: §e" + xp)
                    .build());
            slot++;
        }
    }

    /** Computes the current level for {@code skill} from its cumulative XP table. */
    private int computeLevel(String skill, long totalXP) {
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
