package com.skyblock.core.menu;

import com.skyblock.core.manager.FairySoulManager;
import com.skyblock.core.manager.FairySoulManager.FairyIsland;
import com.skyblock.core.model.Stat;
import com.skyblock.core.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Map;
import java.util.UUID;

public final class FairySoulMenu extends AbstractSkyBlockMenu {

    static final int[] ISLAND_SLOTS = {19, 20, 21, 22, 23, 24, 25, 28, 29, 30, 31, 32, 33};

    private static final int SUMMARY_SLOT = 40;

    public FairySoulMenu(Player player) {
        super(player, buildTitle(player), 5);
    }

    private static String buildTitle(Player player) {
        int found = FairySoulManager.getInstance().getFoundCount(player.getUniqueId());
        return "§dFairy Souls §7(" + found + "/227)";
    }

    @Override
    protected void populate() {
        UUID playerId = player.getUniqueId();
        FairySoulManager manager = FairySoulManager.getInstance();

        ItemStack purplePane = new ItemBuilder(Material.PURPLE_STAINED_GLASS_PANE).displayName("§r").build();
        for (int slot = 9; slot <= 17; slot++) {
            setItem(slot, purplePane);
        }

        FairyIsland[] islands = FairyIsland.values();
        for (int i = 0; i < islands.length && i < ISLAND_SLOTS.length; i++) {
            FairyIsland island = islands[i];
            int found = manager.getFoundCount(playerId, island);
            int total = island.getSoulCount();

            Material wool;
            if (found == total) {
                wool = Material.GREEN_WOOL;
            } else if (found > 0) {
                wool = Material.YELLOW_WOOL;
            } else {
                wool = Material.RED_WOOL;
            }

            setItem(ISLAND_SLOTS[i], new ItemBuilder(wool)
                    .displayName("§d" + island.getDisplayName())
                    .lore(
                            "§7Fairy Souls found:",
                            "§e" + found + "§7/§e" + total)
                    .build());
        }

        int totalFound = manager.getFoundCount(playerId);
        ItemBuilder summary = new ItemBuilder(Material.NETHER_STAR)
                .displayName("§dFairy Soul Overview")
                .lore(
                        "§7Total found: §e" + totalFound + "§7/§e" + FairySoulManager.MAX_SOULS,
                        "§7Every §e" + FairySoulManager.SOULS_PER_REWARD + " §7souls grants",
                        "§7a permanent stat bonus.",
                        "§r");

        Map<Stat, Double> bonuses = manager.getStatBonuses(playerId);
        if (bonuses.isEmpty()) {
            summary.addLore("§7No bonuses earned yet.");
        } else {
            summary.addLore("§7Bonuses earned:");
            bonuses.forEach((stat, amount) ->
                    summary.addLore("§7" + stat.getSymbol() + " " + stat.getDisplayName() + ": §a+" + amount));
        }

        setItem(SUMMARY_SLOT, summary.build());
    }
}
