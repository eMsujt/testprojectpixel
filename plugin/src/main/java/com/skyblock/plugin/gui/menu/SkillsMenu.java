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
        FARMING      ("Farming",      "farming",      Material.WHEAT),
        MINING       ("Mining",       "mining",       Material.IRON_PICKAXE),
        COMBAT       ("Combat",       "combat",       Material.IRON_SWORD),
        FORAGING     ("Foraging",     "foraging",     Material.OAK_LOG),
        FISHING      ("Fishing",      "fishing",      Material.FISHING_ROD),
        ENCHANTING   ("Enchanting",   "enchanting",   Material.BOOKSHELF),
        ALCHEMY      ("Alchemy",      "alchemy",      Material.GLASS_BOTTLE),
        TAMING       ("Taming",       "taming",       Material.BONE),
        CARPENTRY    ("Carpentry",    "carpentry",    Material.CRAFTING_TABLE),
        RUNECRAFTING ("Runecrafting", "runecrafting", Material.MAGMA_CREAM),
        SOCIAL       ("Social",       "social",       Material.FEATHER);

        private final String displayName;
        private final String key;
        private final Material icon;

        Skill(String displayName, String key, Material icon) {
            this.displayName = displayName;
            this.key = key;
            this.icon = icon;
        }
    }

    private static final int[] SLOTS = {10, 11, 12, 13, 14, 19, 20, 21, 22, 23, 24};

    private final UUID playerId;

    public SkillsMenu(UUID playerId) {
        super("§aSkills", 6);
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
            double xp = profile.getSkillXp(skill.key);
            int level = skillManager.levelForXp(skill.key, (long) xp);
            setItem(SLOTS[i], new ItemBuilder(skill.icon)
                            .displayName("§a" + skill.displayName)
                            .lore("§7Level: §e" + level,
                                  "§7Total XP: §e" + xp)
                            .build(),
                    e -> e.setCancelled(true));
        }
    }

    private void fillBorder() {
        ItemStack pane = new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE)
                .displayName("§r")
                .build();
        for (int slot = 0; slot < 9; slot++)  setItem(slot, pane);
        for (int slot = 45; slot < 54; slot++) setItem(slot, pane);
    }
}
