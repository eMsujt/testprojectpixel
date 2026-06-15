package com.skyblock.plugin.gui.menu;

import com.skyblock.plugin.gui.ItemBuilder;
import com.skyblock.plugin.gui.Menu;
import com.skyblock.plugin.manager.ProfileManager;
import com.skyblock.plugin.manager.SkillManager;
import com.skyblock.plugin.profile.SkyBlockProfile;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;

import java.util.UUID;

public class SkillsMenu extends Menu {

    private enum Skill {
        FARMING    ("Farming",    "farming",    Color.fromRGB(255, 215,   0)),
        MINING     ("Mining",     "mining",     Color.fromRGB(169, 169, 169)),
        COMBAT     ("Combat",     "combat",     Color.fromRGB(255,   0,   0)),
        FORAGING   ("Foraging",   "foraging",   Color.fromRGB(  0, 128,   0)),
        FISHING    ("Fishing",    "fishing",    Color.fromRGB(  0, 191, 255)),
        ENCHANTING ("Enchanting", "enchanting", Color.fromRGB(138,  43, 226)),
        ALCHEMY    ("Alchemy",    "alchemy",    Color.fromRGB( 50, 205,  50)),
        TAMING     ("Taming",     "taming",     Color.fromRGB(255, 165,   0));

        private final String displayName;
        private final String key;
        private final Color color;

        Skill(String displayName, String key, Color color) {
            this.displayName = displayName;
            this.key = key;
            this.color = color;
        }
    }

    /** Two inner rows, alternating columns: 8 icons for 8 skills. */
    private static final int[] SLOTS = {10, 12, 14, 16, 19, 21, 23, 25};

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
            long xp = profile.getSkillXp(skill.key);
            int level = skillManager.levelForXp(skill.key, xp);
            setItem(SLOTS[i], dyedHelmet(skill.color, "§a" + skill.displayName,
                            "§7Level: §e" + level,
                            "§7Total XP: §e" + xp),
                    e -> e.setCancelled(true));
        }
    }

    private ItemStack dyedHelmet(Color color, String name, String... lore) {
        ItemStack helmet = new ItemStack(Material.LEATHER_HELMET, 1);
        LeatherArmorMeta meta = (LeatherArmorMeta) helmet.getItemMeta();
        if (meta != null) {
            meta.setColor(color);
            meta.setDisplayName(name);
            meta.setLore(java.util.Arrays.asList(lore));
            helmet.setItemMeta(meta);
        }
        return helmet;
    }

    private void fillBorder() {
        ItemStack pane = new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE)
                .displayName("§r")
                .build();
        for (int slot = 0; slot < 9; slot++)  setItem(slot, pane);
        for (int slot = 45; slot < 54; slot++) setItem(slot, pane);
    }
}
