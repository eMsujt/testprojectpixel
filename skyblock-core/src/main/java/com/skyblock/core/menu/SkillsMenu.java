package com.skyblock.core.menu;

import com.skyblock.core.manager.SkillsManager;
import com.skyblock.core.model.Skill;
import com.skyblock.core.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * The "Your Skills" menu, opened from the SkyBlock Menu. Laid out 1:1 with Hypixel:
 * the Skill Average summary (Diamond Sword) at slot 4, the twelve skills across rows
 * 3–4 at their documented slots and vanilla icons, and a Go Back arrow at slot 48.
 */
public class SkillsMenu extends AbstractSkyBlockMenu {

    /** The eight skills that count toward Skill Average. */
    private static final Skill[] MAIN = {
        Skill.FARMING, Skill.MINING, Skill.COMBAT, Skill.FORAGING,
        Skill.FISHING, Skill.ENCHANTING, Skill.ALCHEMY, Skill.TAMING
    };

    /** Hypixel slot + vanilla icon for each skill in the "Your Skills" menu. */
    private static final Object[][] LAYOUT = {
        {19, Skill.COMBAT,        Material.STONE_SWORD},
        {20, Skill.FARMING,       Material.GOLDEN_HOE},
        {21, Skill.FISHING,       Material.FISHING_ROD},
        {22, Skill.MINING,        Material.STONE_PICKAXE},
        {23, Skill.FORAGING,      Material.JUNGLE_SAPLING},
        {24, Skill.ENCHANTING,    Material.ENCHANTING_TABLE},
        {25, Skill.ALCHEMY,       Material.BREWING_STAND},
        {28, Skill.CARPENTRY,     Material.CRAFTING_TABLE},
        {29, Skill.RUNECRAFTING,  Material.MAGMA_CREAM},
        {30, Skill.TAMING,        Material.WOLF_SPAWN_EGG},
        {32, Skill.SOCIAL,        Material.EMERALD},
        {34, Skill.DUNGEONEERING, Material.SKELETON_SKULL},
    };

    public SkillsMenu(JavaPlugin plugin, Player player) {
        super(player, "§aYour Skills", 6);
    }

    @Override
    protected void populate() {
        SkillsManager skills = SkillsManager.getInstance();
        UUID id = player.getUniqueId();

        double sum = 0;
        for (Skill s : MAIN) sum += SkillsManager.levelForXp(s.key(), skills.getSkillXP(id, s.key()));
        setItem(4, new ItemBuilder(Material.DIAMOND_SWORD)
                .displayName("§aYour Skills")
                .lore(
                        "§7Skill Average: §e" + String.format("%.1f", sum / MAIN.length),
                        "",
                        "§7View your Skill progression",
                        "§7and rewards.")
                .build(), e -> e.setCancelled(true));

        for (Object[] entry : LAYOUT) {
            int slot = (int) entry[0];
            Skill skill = (Skill) entry[1];
            Material icon = (Material) entry[2];
            long xp = skills.getSkillXP(id, skill.key());
            int level = SkillsManager.levelForXp(skill.key(), xp);
            setItem(slot, new ItemBuilder(icon)
                    .displayName("§a" + skill.displayName + " §7" + level)
                    .lore(skillLore(skill, xp, level))
                    .build(), e -> e.setCancelled(true));
        }

        setItem(48, new ItemBuilder(Material.ARROW)
                .displayName("§aGo Back")
                .lore("§7To SkyBlock Menu")
                .build(),
                e -> { e.setCancelled(true); new SkyBlockMenu(player).open(player); });
    }

    private static List<String> skillLore(Skill skill, long xp, int level) {
        List<String> lore = new ArrayList<>();
        lore.add("§7Total XP: §e" + String.format("%,d", xp));
        lore.add("");

        long curLevelXp = SkillsManager.xpForLevel(skill.key(), level);
        long nextLevelXp = SkillsManager.xpForLevel(skill.key(), level + 1);
        if (nextLevelXp < 0) {
            // No higher threshold: either maxed, or a skill with no XP curve (e.g. Dungeoneering).
            lore.add(level > 0 ? "§6§lMAX LEVEL!" : "§7Progress is tracked in-game.");
        } else {
            long into = xp - Math.max(0, curLevelXp);
            long need = nextLevelXp - Math.max(0, curLevelXp);
            double pct = need > 0 ? Math.min(100.0, into * 100.0 / need) : 100.0;
            // Hypixel-style dashed bar: filled portion dark-green, remainder white.
            int filled = (int) Math.round(pct / 100.0 * 20);
            lore.add("§7Progress to Level " + roman(level + 1) + ": §e" + String.format("%.1f", pct) + "%");
            lore.add("§2" + "-".repeat(filled) + "§f" + "-".repeat(20 - filled)
                    + " §e" + String.format("%,d", into) + "§6/§e" + String.format("%,d", need));
        }

        lore.add("");
        lore.add("§eClick to view!");
        return lore;
    }

    /** Renders 1..120 as a Roman numeral (Hypixel shows skill levels in Roman numerals). */
    static String roman(int n) {
        if (n <= 0) return Integer.toString(n);
        int[] values = {100, 90, 50, 40, 10, 9, 5, 4, 1};
        String[] symbols = {"C", "XC", "L", "XL", "X", "IX", "V", "IV", "I"};
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < values.length && n > 0; i++) {
            while (n >= values[i]) { sb.append(symbols[i]); n -= values[i]; }
        }
        return sb.toString();
    }
}
