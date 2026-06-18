package com.skyblock.core.menu;

import com.skyblock.core.manager.FairySoulManager;
import com.skyblock.core.manager.FairySoulManager.FairyIsland;
import com.skyblock.core.model.Stat;
import com.skyblock.core.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.EnumMap;
import java.util.Map;
import java.util.UUID;

/**
 * 54-slot Fairy Soul overview menu. Shows each SkyBlock island that contains
 * fairy souls (Hub, Farming Islands, Spider's Den, The End, Crimson Isle,
 * Deep Caverns, The Park, Dungeon Hub) as a thematic block item with the
 * player's found / total count for that island, plus an overall summary of
 * the permanent stat bonuses earned.
 */
public final class FairySoulMenu extends Menu {

    static final int[] ISLAND_SLOTS = {20, 21, 22, 23, 24, 30, 31, 32};

    private static final int SUMMARY_SLOT = 49;

    private static final Map<FairyIsland, Material> ISLAND_ICONS = new EnumMap<>(FairyIsland.class);

    static {
        ISLAND_ICONS.put(FairyIsland.HUB,             Material.GRASS_BLOCK);
        ISLAND_ICONS.put(FairyIsland.FARMING_ISLANDS, Material.WHEAT);
        ISLAND_ICONS.put(FairyIsland.SPIDERS_DEN,     Material.COBWEB);
        ISLAND_ICONS.put(FairyIsland.THE_END,         Material.END_STONE);
        ISLAND_ICONS.put(FairyIsland.CRIMSON_ISLE,    Material.NETHERRACK);
        ISLAND_ICONS.put(FairyIsland.DEEP_CAVERNS,    Material.STONE);
        ISLAND_ICONS.put(FairyIsland.THE_PARK,        Material.OAK_SAPLING);
        ISLAND_ICONS.put(FairyIsland.DUNGEON_HUB,     Material.MOSSY_COBBLESTONE);
    }

    private final UUID playerId;

    public FairySoulMenu(UUID playerId) {
        super("§dFairy Souls", 6);
        this.playerId = playerId;
    }

    @Override
    protected void build() {
        ItemStack pane = new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE).displayName("§r").build();

        for (int slot = 0; slot < 9; slot++) setItem(slot, pane);
        for (int slot = 45; slot < 54; slot++) setItem(slot, pane);

        FairySoulManager manager = FairySoulManager.getInstance();
        FairyIsland[] islands = FairyIsland.values();

        for (int i = 0; i < islands.length; i++) {
            FairyIsland island = islands[i];
            int found = manager.getFoundCount(playerId, island);

            setItem(ISLAND_SLOTS[i], new ItemBuilder(ISLAND_ICONS.get(island))
                    .displayName("§d" + island.getDisplayName())
                    .lore(
                            "§7Fairy Souls found:",
                            "§e" + found + "§7/§e" + island.getSoulCount())
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
