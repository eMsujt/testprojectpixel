package com.skyblock.core.menu;

import com.skyblock.core.manager.SkillManager;
import com.skyblock.core.model.Skill;
import com.skyblock.core.util.SkyblockUtils.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Canonical Skills hub menu. Displays one {@link Material#PLAYER_HEAD} per
 * skill with the player's current level and total XP in the lore.
 */
public class SkillsMenu extends Menu {

    /** Skills that have a head skin — shown in the menu. */
    private static final List<Skill> MENU_SKILLS = Arrays.stream(Skill.values())
            .filter(s -> s.texture != null)
            .collect(Collectors.toList());

    private static final int[] SLOTS = {10, 12, 14, 16, 19, 21, 23, 25, 28, 30, 32};

    private final UUID playerId;

    public SkillsMenu(UUID playerId) {
        super("§aSkills", 6);
        this.playerId = playerId;
    }

    @Override
    protected void build() {
        fillBorder();
        SkillManager skills = SkillManager.getInstance();
        for (int i = 0; i < MENU_SKILLS.size(); i++) {
            Skill skill = MENU_SKILLS.get(i);
            long xp = skills.getSkillXP(playerId, skill.key());
            int level = SkillManager.levelForXp(skill.key(), xp);
            setItem(SLOTS[i], new ItemBuilder(Material.PLAYER_HEAD)
                    .skullTexture(skill.texture)
                    .displayName("§a" + skill.displayName)
                    .lore("§7Level: §e" + level, "§7Total XP: §e" + xp)
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
