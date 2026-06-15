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

import java.util.Arrays;
import java.util.UUID;

public class SkillsMenu extends Menu {

    private enum Skill {
        FARMING      ("Farming",      "farming",      Color.fromRGB( 85, 255,  85)),
        MINING       ("Mining",       "mining",       Color.fromRGB(170, 170, 170)),
        COMBAT       ("Combat",       "combat",       Color.fromRGB(255,  85,  85)),
        FORAGING     ("Foraging",     "foraging",     Color.fromRGB(  0, 170,   0)),
        FISHING      ("Fishing",      "fishing",      Color.fromRGB( 85,  85, 255)),
        ENCHANTING   ("Enchanting",   "enchanting",   Color.fromRGB(170,   0, 170)),
        ALCHEMY      ("Alchemy",      "alchemy",      Color.fromRGB(255, 255,  85)),
        TAMING       ("Taming",       "taming",       Color.fromRGB(255, 170,   0)),
        CARPENTRY    ("Carpentry",    "carpentry",    Color.fromRGB(150,  75,   0)),
        RUNECRAFTING ("Runecrafting", "runecrafting", Color.fromRGB(255,  85, 255)),
        SOCIAL       ("Social",       "social",       Color.fromRGB( 85, 255, 255));

        private final String displayName;
        private final String key;
        private final Color color;

        Skill(String displayName, String key, Color color) {
            this.displayName = displayName;
            this.key = key;
            this.color = color;
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
            ItemStack helmet = new ItemStack(Material.LEATHER_HELMET);
            LeatherArmorMeta meta = (LeatherArmorMeta) helmet.getItemMeta();
            if (meta != null) {
                meta.setColor(skill.color);
                meta.setDisplayName("§a" + skill.displayName);
                meta.setLore(Arrays.asList("§7Level: §e" + level, "§7Total XP: §e" + xp));
                helmet.setItemMeta(meta);
            }
            setItem(SLOTS[i], helmet, e -> e.setCancelled(true));
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
