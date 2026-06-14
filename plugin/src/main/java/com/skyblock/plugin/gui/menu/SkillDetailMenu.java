package com.skyblock.plugin.gui.menu;

import com.skyblock.plugin.gui.ItemBuilder;
import com.skyblock.plugin.gui.Menu;
import com.skyblock.plugin.managers.SkillsManager;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

/**
 * The detail view for a single skill.
 *
 * <p>A 45-slot (5-row) chest titled with the skill name and a gray glass-pane
 * border. Slot 13 shows the skill's material icon with the player's current
 * level and total XP. Opened when clicking a skill icon in {@link SkillsMenu}.</p>
 */
public class SkillDetailMenu extends Menu {

    private final UUID playerId;
    private final String displayName;
    private final String key;
    private final Material icon;

    public SkillDetailMenu(UUID playerId, String displayName, String key, Material icon) {
        super("§b" + displayName, 5);
        this.playerId = playerId;
        this.displayName = displayName;
        this.key = key;
        this.icon = icon;
    }

    @Override
    protected void build() {
        fillBorder();

        SkillsManager skills = SkillsManager.getInstance();
        long totalXP = skills.getSkillXP(playerId, key);
        int level = skills.getSkillLevel(playerId, key);
        setItem(13, new ItemBuilder(icon)
                .displayName("§a" + displayName)
                .lore(
                        "§7Level: §e" + level,
                        "§7Total XP: §e" + totalXP)
                .build());

        setItem(40, new ItemBuilder(Material.ARROW)
                .displayName("§aBack")
                .lore("§7Return to your skills.")
                .build(),
                e -> {
                    e.setCancelled(true);
                    new SkillsMenu(playerId).open((Player) e.getWhoClicked());
                });
    }

    private void fillBorder() {
        ItemStack pane = new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE)
                .displayName("§r")
                .build();
        for (int slot = 0; slot < 45; slot++) {
            int column = slot % 9;
            if (slot < 9 || slot >= 36 || column == 0 || column == 8) {
                setItem(slot, pane);
            }
        }
    }
}
