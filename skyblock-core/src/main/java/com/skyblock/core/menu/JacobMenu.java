package com.skyblock.core.menu;

import com.skyblock.core.manager.GardenManager.ContestMedal;
import com.skyblock.core.manager.GardenManager.GardenCrop;
import com.skyblock.core.manager.JacobManager;
import com.skyblock.core.manager.JacobManager.UpcomingContest;
import com.skyblock.core.util.SkyblockUtil.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 54-slot Jacob's Farming Contest menu.
 *
 * <p>Slot 4 holds a wheat "Jacob's Farming Contest" summary showing the viewing
 * player's contest participation and medal tally (from {@link JacobManager}).
 * The middle row renders the next three upcoming contests as clock items, each
 * listing its year-day and featured crops. Top and bottom rows are gray-pane
 * borders.</p>
 */
public final class JacobMenu extends Menu {

    static final int SUMMARY_SLOT = 4;
    private static final int[] CONTEST_SLOTS = {20, 22, 24};

    private final UUID playerId;

    public JacobMenu(UUID playerId) {
        super("§6Jacob's Farming Contest", 6);
        this.playerId = playerId;
    }

    @Override
    protected void build() {
        ItemStack pane = new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE).displayName("§r").build();
        for (int slot = 0; slot < 9; slot++) setItem(slot, pane);
        for (int slot = 45; slot < 54; slot++) setItem(slot, pane);

        JacobManager manager = JacobManager.getInstance();

        List<String> summaryLore = new ArrayList<>();
        summaryLore.add("§7Contests participated: §e" + manager.getContestsParticipated(playerId));
        summaryLore.add("§7Total medals: §e" + manager.getTotalMedals(playerId));
        summaryLore.add("");
        for (ContestMedal medal : ContestMedal.values()) {
            if (medal != ContestMedal.NONE) {
                summaryLore.add("§7" + medal.getDisplayName() + ": §e" + manager.getMedalCount(playerId, medal));
            }
        }
        setItem(SUMMARY_SLOT, new ItemBuilder(Material.WHEAT)
                .displayName("§eJacob's Farming Contest")
                .lore(summaryLore)
                .build());

        List<UpcomingContest> upcoming = manager.getUpcomingContests(CONTEST_SLOTS.length);
        for (int i = 0; i < upcoming.size() && i < CONTEST_SLOTS.length; i++) {
            UpcomingContest contest = upcoming.get(i);
            String crops = contest.getCrops().stream()
                    .map(GardenCrop::getDisplayName)
                    .collect(Collectors.joining(", "));
            setItem(CONTEST_SLOTS[i], new ItemBuilder(Material.CLOCK)
                    .displayName("§aContest on Day " + contest.getDay())
                    .lore(
                            "§7Featured crops:",
                            "§e" + crops)
                    .build());
        }
    }

    @Override
    public void handleClick(InventoryClickEvent event) {
        event.setCancelled(true);
    }
}
