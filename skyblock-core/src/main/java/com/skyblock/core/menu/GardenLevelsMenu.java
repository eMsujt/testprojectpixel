package com.skyblock.core.menu;

import com.skyblock.core.manager.GardenManager;
import com.skyblock.core.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

/**
 * The "Garden Levels" menu, opened from the Garden hub. Lays out the 15 Garden
 * level reward tiles 1:1 with Hypixel (wiki The Garden/Skill UI) on their
 * documented winding-path slots, each showing its verbatim rewards and coloured
 * by unlocked / in-progress / locked relative to the player's Garden level.
 */
public final class GardenLevelsMenu extends AbstractSkyBlockMenu {

    private static final String INDENT = "　"; // full-width space, Hypixel's reward indent
    private static final String[] ROMAN = {
            "I", "II", "III", "IV", "V", "VI", "VII", "VIII", "IX", "X", "XI", "XII", "XIII", "XIV", "XV"
    };

    /** Documented slot for each Garden level tile (level 1 = index 0). */
    private static final int[] LEVEL_SLOTS = {
            9, 18, 27, 28, 29, 20, 11, 2, 3, 4, 13, 22, 31, 32, 33
    };

    /** Verbatim reward lines per level (the level header/status is added dynamically). */
    private static final String[][] REWARDS = {
            {INDENT + "§8+§b47 §eVisitors", INDENT + "§aWheat §7Crop"},
            {INDENT + "§8+§b9 §eVisitors", INDENT + "§aCarrot §7Crop", INDENT + "§aTier I §7Crop Upgrades",
             INDENT + "§8+§b10 SkyBlock XP", INDENT + "§8+§210 Crop Growth"},
            {INDENT + "§8+§b13 §eVisitors", INDENT + "§aPotato §7Crop", INDENT + "§aMedieval Barn Skin",
             INDENT + "§8+§b10 SkyBlock XP", INDENT + "§8+§210 Crop Growth"},
            {INDENT + "§8+§b5 §eVisitors", INDENT + "§aPumpkin §7Crop", INDENT + "§aTier II §7Crop Upgrades",
             INDENT + "§8+§7Gearing Up Quest", INDENT + "§8+§b10 SkyBlock XP", INDENT + "§8+§210 Crop Growth"},
            {INDENT + "§8+§b5 §eVisitors", INDENT + "§aSugar Cane §7Crop", INDENT + "§6Fly Pest",
             INDENT + "§6Lunar Moth Pest", INDENT + "§6Cricket Pest", INDENT + "§6Locust Pest",
             INDENT + "§6Rat Pest", INDENT + "§6Field Mouse Pest", INDENT + "§6Mosquito Pest",
             INDENT + "§8+§b10 SkyBlock XP", INDENT + "§8+§210 Crop Growth"},
            {INDENT + "§8+§b6 §eVisitors", INDENT + "§aMelon Slice §7Crop", INDENT + "§6Earthworm Pest",
             INDENT + "§aTier III §7Crop Upgrades", INDENT + "§aSunny Barn Skin",
             INDENT + "§8+§b10 SkyBlock XP", INDENT + "§8+§210 Crop Growth"},
            {INDENT + "§8+§b10 §eVisitors", INDENT + "§aCactus §7Crop", INDENT + "§6Mite Pest",
             INDENT + "§8+§b10 SkyBlock XP", INDENT + "§8+§210 Crop Growth"},
            {INDENT + "§8+§b9 §eVisitors", INDENT + "§aCocoa Beans §7Crop", INDENT + "§6Moth Pest",
             INDENT + "§aTier IV §7Crop Upgrades", INDENT + "§8+§b10 SkyBlock XP", INDENT + "§8+§210 Crop Growth"},
            {INDENT + "§8+§b8 §eVisitors", INDENT + "§aMushroom §7Crop", INDENT + "§6Slug Pest",
             INDENT + "§aRed Barn Skin", INDENT + "§8+§b10 SkyBlock XP", INDENT + "§8+§210 Crop Growth"},
            {INDENT + "§8+§b9 §eVisitors", INDENT + "§aNether Wart §7Crop", INDENT + "§6Beetle Pest",
             INDENT + "§aTier V §7Crop Upgrades", INDENT + "§8+§b10 SkyBlock XP", INDENT + "§8+§210 Crop Growth"},
            {INDENT + "§8+§b4 §eVisitors", INDENT + "§aSunflower §7Crop", INDENT + "§aMoonflower §7Crop",
             INDENT + "§6Firefly Pest", INDENT + "§6Dragonfly Pest", INDENT + "§9Cabin Barn Skin",
             INDENT + "§8+§b10 SkyBlock XP", INDENT + "§8+§210 Crop Growth"},
            {INDENT + "§8+§b9 §eVisitors", INDENT + "§aWild Rose §7Crop", INDENT + "§6Praying Mantis Pest",
             INDENT + "§aTier VI §7Crop Upgrades", INDENT + "§8+§b10 SkyBlock XP", INDENT + "§8+§210 Crop Growth"},
            {INDENT + "§8+§b2 §eVisitors", INDENT + "§aTier VII §7Crop Upgrades",
             INDENT + "§8+§b10 SkyBlock XP", INDENT + "§8+§210 Crop Growth"},
            {INDENT + "§8+§b2 §eVisitors", INDENT + "§aTier VIII §7Crop Upgrades",
             INDENT + "§8+§b10 SkyBlock XP", INDENT + "§8+§210 Crop Growth"},
            {INDENT + "§8+§b2 §eVisitor", INDENT + "§aTier IX §7Crop Upgrades",
             INDENT + "§5Mansion Heights Barn Skin", INDENT + "§8+§b10 SkyBlock XP", INDENT + "§8+§210 Crop Growth"}
    };

    public GardenLevelsMenu(Player player) {
        super(player, "Garden Levels", 6);
    }

    @Override
    protected void populate() {
        int level = GardenManager.getInstance().getGardenLevel(player.getUniqueId());
        int maxLevel = GardenManager.getInstance().getMaxGardenLevel();

        setItem(0, new ItemBuilder(Material.SUNFLOWER)
                .displayName("§aGarden Levels")
                .lore("§7Earn Garden experience by",
                      "§7accepting visitors' offers and",
                      "§7unlocking new milestones!",
                      "",
                      "§7Garden Level: §e" + level + "§7/§e" + maxLevel,
                      "",
                      "§8Increase your Garden level to",
                      "§8unlock new visitors, crops and",
                      "§8more!")
                .build(), e -> e.setCancelled(true));

        for (int i = 0; i < LEVEL_SLOTS.length; i++) {
            int tierLevel = i + 1;
            boolean unlocked = tierLevel <= level;
            boolean current = tierLevel == level + 1;

            Material pane = unlocked ? Material.LIME_STAINED_GLASS_PANE
                    : current ? Material.YELLOW_STAINED_GLASS_PANE
                    : Material.RED_STAINED_GLASS_PANE;
            String nameColor = unlocked ? "§a" : current ? "§e" : "§c";

            List<String> lore = new ArrayList<>();
            lore.add("§7Rewards:");
            for (String line : REWARDS[i]) lore.add(line);
            lore.add("");
            lore.add(unlocked ? "§aUNLOCKED" : current ? "§eIn progress" : "§cLocked");

            setItem(LEVEL_SLOTS[i], new ItemBuilder(pane)
                    .displayName(nameColor + "Garden Level " + ROMAN[i])
                    .lore(lore)
                    .build(), e -> e.setCancelled(true));
        }

        setItem(49, new ItemBuilder(Material.ARROW)
                .displayName("§aGo Back")
                .lore("§7To the Garden")
                .build(),
                e -> { e.setCancelled(true); new GardenMenu(player).open(player); });
    }
}
