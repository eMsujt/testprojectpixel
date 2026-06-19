package com.skyblock.core.menu;

import com.skyblock.core.manager.FairySoulManager;
import com.skyblock.core.manager.FairySoulManager.FairyIsland;
import com.skyblock.core.model.Stat;
import com.skyblock.core.util.SkyblockUtil.*;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Map;
import java.util.UUID;

/**
 * 54-slot Fairy Soul overview menu. The top row (slots 0–7) shows one
 * colored-wool item per island: GREEN = all souls found, YELLOW = partially
 * found, RED = none found. Slot 49 summarises total progress and earned
 * stat bonuses.
 */
public final class FairySoulMenu extends Menu {

    static final int[] ISLAND_SLOTS = {0, 1, 2, 3, 4, 5, 6, 7};

    private static final int SUMMARY_SLOT = 49;

    private final UUID playerId;

    public FairySoulMenu(UUID playerId) {
        super("§dFairy Souls", 6);
        this.playerId = playerId;
    }

    @Override
    protected void build() {
        ItemStack pane = new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE).displayName("§r").build();

        for (int slot = 8; slot < 54; slot++) setItem(slot, pane);

        FairySoulManager manager = FairySoulManager.getInstance();
        FairyIsland[] islands = FairyIsland.values();

        for (int i = 0; i < islands.length; i++) {
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
                        "§7Total found: §e" + totalFound + "§7/§e" + manager.getTotalSouls(),
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
