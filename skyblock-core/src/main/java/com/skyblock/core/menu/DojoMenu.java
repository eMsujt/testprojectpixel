package com.skyblock.core.menu;

import com.skyblock.core.manager.DojoManager;
import com.skyblock.core.manager.DojoManager.DojoChallenge;
import com.skyblock.core.util.SkyblockUtils.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * 54-slot Dojo menu.
 *
 * <p>Slot 4 shows a summary item with the player's total score and grade.
 * Slots 19–24 render each {@link DojoChallenge} with its score and grade.
 * Top and bottom rows are gray-pane borders.</p>
 */
public final class DojoMenu extends Menu {

    static final int SUMMARY_SLOT = 4;
    private static final int[] CHALLENGE_SLOTS = {19, 20, 21, 22, 23, 24};

    private final UUID playerId;

    public DojoMenu(UUID playerId) {
        super("§6Dojo", 6);
        this.playerId = playerId;
    }

    @Override
    protected void build() {
        ItemStack pane = new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE).displayName("§r").build();
        for (int slot = 0; slot < 9; slot++) setItem(slot, pane);
        for (int slot = 45; slot < 54; slot++) setItem(slot, pane);

        DojoManager manager = DojoManager.getInstance();
        int total = manager.getTotalScore(playerId);
        int maxTotal = DojoManager.getMaxTotalScore();
        String overallGrade = DojoManager.getGrade(total, maxTotal);

        List<String> summaryLore = new ArrayList<>();
        summaryLore.add("§7Total Score: §e" + total + " §7/ §e" + maxTotal);
        summaryLore.add("§7Overall Grade: §e" + overallGrade);
        setItem(SUMMARY_SLOT, new ItemBuilder(Material.GOLDEN_SWORD)
                .displayName("§6Dojo")
                .lore(summaryLore)
                .build());

        DojoChallenge[] challenges = DojoChallenge.values();
        for (int i = 0; i < CHALLENGE_SLOTS.length && i < challenges.length; i++) {
            DojoChallenge challenge = challenges[i];
            int score = manager.getScore(playerId, challenge);
            String grade = DojoManager.getGrade(score, challenge.maxScore());
            List<String> lore = new ArrayList<>();
            lore.add("§7Score: §e" + score + " §7/ §e" + challenge.maxScore());
            lore.add("§7Grade: §e" + grade);
            setItem(CHALLENGE_SLOTS[i], new ItemBuilder(Material.IRON_SWORD)
                    .displayName("§a" + challenge.getDisplayName())
                    .lore(lore)
                    .build());
        }
    }

    @Override
    public void handleClick(InventoryClickEvent event) {
        event.setCancelled(true);
    }
}
