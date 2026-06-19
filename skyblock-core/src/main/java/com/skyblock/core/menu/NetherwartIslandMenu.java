package com.skyblock.core.menu;

import com.skyblock.core.manager.NetherwartIslandManager;
import com.skyblock.core.manager.NetherwartIslandManager.Faction;
import com.skyblock.core.manager.NetherwartIslandManager.KuudraTier;
import com.skyblock.core.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

/**
 * 54-slot Crimson Isle (Nether wart Island) overview menu. Slot 20 shows the
 * player's pledged faction and slot 24 their reputation. Row 4 (slots 29–33)
 * shows the player's completion count for each of the five Kuudra tiers. Slot
 * 49 summarises area-exploration progress.
 */
public final class NetherwartIslandMenu extends Menu {

    private static final int FACTION_SLOT = 20;
    private static final int REPUTATION_SLOT = 24;

    private static final int[] KUUDRA_SLOTS = {29, 30, 31, 32, 33};

    private static final int SUMMARY_SLOT = 49;

    private static final KuudraTier[] KUUDRA_ORDER = {
            KuudraTier.BASIC,
            KuudraTier.HOT,
            KuudraTier.BURNING,
            KuudraTier.FIERY,
            KuudraTier.INFERNAL
    };

    private final UUID playerId;

    public NetherwartIslandMenu(UUID playerId) {
        super("§4Crimson Isle", 6);
        this.playerId = playerId;
    }

    @Override
    protected void build() {
        ItemStack pane = new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE).displayName("§r").build();

        for (int slot = 0; slot < 9; slot++) setItem(slot, pane);
        for (int slot = 45; slot < 54; slot++) setItem(slot, pane);
        setItem(18, pane); setItem(19, pane);
        setItem(25, pane); setItem(26, pane);

        NetherwartIslandManager manager = NetherwartIslandManager.getInstance();

        Faction faction = manager.getFaction(playerId);
        setItem(FACTION_SLOT, new ItemBuilder(Material.NETHERITE_SWORD)
                .displayName("§cFaction")
                .lore(faction == null
                        ? "§7Pledged to: §cNone"
                        : "§7Pledged to: §e" + faction.getDisplayName())
                .build());

        setItem(REPUTATION_SLOT, new ItemBuilder(Material.PAPER)
                .displayName("§cReputation")
                .lore("§7Reputation: §e" + manager.getReputation(playerId))
                .build());

        for (int i = 0; i < KUUDRA_ORDER.length; i++) {
            KuudraTier tier = KUUDRA_ORDER[i];
            int completions = manager.getKuudraCompletions(playerId, tier);
            Material icon = completions > 0
                    ? Material.LIME_STAINED_GLASS_PANE
                    : Material.RED_STAINED_GLASS_PANE;

            setItem(KUUDRA_SLOTS[i], new ItemBuilder(icon)
                    .displayName("§4Kuudra §c" + tier.getDisplayName())
                    .lore("§7Completions: §e" + completions)
                    .build());
        }

        int discovered = manager.getDiscoveredAreas(playerId).size();
        int total = NetherwartIslandManager.CrimsonArea.values().length;
        int percent = (int) Math.round(manager.getAreaProgress(playerId) * 100);
        setItem(SUMMARY_SLOT, new ItemBuilder(Material.NETHER_WART)
                .displayName("§4Crimson Isle")
                .lore(
                        "§7Areas discovered: §e" + discovered + "§7/§e" + total,
                        "§7Exploration: §e" + percent + "%")
                .build());
    }
}
