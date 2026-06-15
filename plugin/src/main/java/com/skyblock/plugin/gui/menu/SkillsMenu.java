package com.skyblock.plugin.gui.menu;

import com.skyblock.plugin.gui.ItemBuilder;
import com.skyblock.plugin.gui.Menu;
import com.skyblock.plugin.manager.ProfileManager;
import com.skyblock.plugin.manager.SkillManager;
import com.skyblock.plugin.profile.SkyBlockProfile;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class SkillsMenu extends Menu {

    private enum Skill {
        FARMING    ("Farming",    "farming",    Material.WHEAT),
        MINING     ("Mining",     "mining",     Material.IRON_PICKAXE),
        COMBAT     ("Combat",     "combat",     Material.IRON_SWORD),
        FORAGING   ("Foraging",   "foraging",   Material.OAK_LOG),
        FISHING    ("Fishing",    "fishing",    Material.FISHING_ROD),
        ENCHANTING ("Enchanting", "enchanting", Material.BOOKSHELF),
        ALCHEMY    ("Alchemy",    "alchemy",    Material.BREWING_STAND),
        TAMING     ("Taming",     "taming",     Material.BONE);

        private final String displayName;
        private final String key;
        private final Material icon;

        Skill(String displayName, String key, Material icon) {
            this.displayName = displayName;
            this.key = key;
            this.icon = icon;
        }
    }

    /** Two inner rows, alternating columns: 8 icons for 8 skills. */
    private static final int[] SLOTS = {10, 12, 14, 16, 19, 21, 23, 25};

    private final UUID playerId;

    public SkillsMenu(UUID playerId) {
        super("§aYour Skills", 6);
        this.playerId = playerId;
    }

    @Override
    protected void build() {
        fillBorder();

        SkyBlockProfile profile = ProfileManager.getInstance().getOrCreateProfile(playerId);
        SkillManager skillManager = SkillManager.getInstance();
        Skill[] values = Skill.values();
        for (int i = 0; i < values.length; i++) {
            Skill skill = values[i];
            long xp = profile.getSkillXp(skill.key);
            int level = skillManager.levelForXp(skill.key, xp);
            setItem(SLOTS[i], new ItemBuilder(skill.icon)
                    .displayName("§a" + skill.displayName)
                    .lore(
                            "§7Level: §e" + level,
                            "§7Total XP: §e" + xp)
                    .build(),
                    e -> e.setCancelled(true));
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
